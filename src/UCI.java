import java.util.Scanner;

public class UCI {
    Scanner sc;
    Board board;

    public UCI() {
        sc = new Scanner(System.in);
    }

    void loop() {
        String lineIn = sc.nextLine();
        while (!lineIn.equals("quit")) {
            switch (lineIn.split(" ")[0]) {
                case "isready":
                    System.out.println("readyok");
                    break;
                case "ucinewgame":
                    newGame();
                    break;
                case "position":
                    position(lineIn);
                    break;
                case "go":
                    go(lineIn);
                    break;
                case "stop":
                    stop();
                    break;
            }

            lineIn = sc.nextLine();
        }
    }

    void newGame() {
        Repetition.clearTables();
        TTable.clearTables();
        board = new Board(PosConstants.startPos);
        Repetition.addToHistory(board.zobristKey, Repetition.historyFlag);
    }

    void position(String command) {
        String pos = command.split(" ")[1];
        if (pos.equals("startpos")) {
            board = new Board(PosConstants.startPos);
        } else {
            board = new Board(command.substring(9));
        }

        Repetition.addToHistory(board.zobristKey, Repetition.historyFlag);

        if (command.contains("moves")) {
            String[] moves = command.substring(command.indexOf("moves")).split(" ");
            Board nextBoard = board;

            Repetition.clearTables();
            Repetition.addToHistory(board.zobristKey, Repetition.historyFlag);

            for (int i = 1; i < moves.length; i++) {
                MoveList moveList = MoveGeneration.getMoves(nextBoard);
                nextBoard = new Board(nextBoard);
                nextBoard.makeMove(moveList.getMoveFromString(moves[i]));
                Repetition.addToHistory(nextBoard.zobristKey, Repetition.historyFlag);
            }

            board = nextBoard;
        }
//        System.out.println(board);
    }

    void go(String command) {
        String[] commands = command.split(" ");
        int movetime = 1000;
        boolean setTime = false;
        String side;
        if (board.player) {
            side = "wtime";
        } else {
            side = "btime";
        }

        for (int i = 0; i < commands.length; i++) {
            if (commands[i].equals("movetime")) {
                movetime = Integer.parseInt(commands[i + 1]);
                setTime = true;
            }
            if (!setTime && commands[i].equals(side)) {
                movetime = Math.max(Integer.parseInt(commands[i + 1]) / 50, 250);
            }
        }

        Board temp = Engine.engineMove(board, movetime);
        System.out.println(getBestMove(temp));
    }

    String getBestMove(Board board) {
        return ("bestmove " + MoveList.toStringMove(board.pastMove));
    }

    void stop() {
        Engine.kill = true;
    }

    String uciCommand(String command) {
        switch (command.split(" ")[0]) {
            case "isready":
                return "readyok";
            case "ucinewgame":
                newGame();
                break;
            case "position":
                position(command);
                break;
            case "go":
                return goClient(command);
            case "stop":
                stop();
                break;
        }
        return null;
    }

    String goClient(String command) {
        String[] commands = command.split(" ");
        int movetime = 1000;
        boolean setTime = false;
        String side;
        if (board.player) {
            side = "wtime";
        } else {
            side = "btime";
        }

        for (int i = 0; i < commands.length; i++) {
            if (commands[i].equals("movetime")) {
                movetime = Integer.parseInt(commands[i + 1]);
                setTime = true;
            }
            if (!setTime && commands[i].equals(side)) {
                movetime = Math.max(Integer.parseInt(commands[i + 1]) / 50, 250);
            }
        }

        Board temp = Engine.engineMove(board, movetime);
        return getBestMove(temp);
    }
}