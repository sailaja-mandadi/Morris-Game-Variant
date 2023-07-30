import java.io.*;
import java.util.*;


public class HelperClass {
    public static void readInputBoard(String ipPath, char[] inputBoard) {
        try {
            Scanner in = new Scanner(new File(ipPath));
            String line = in.nextLine();
            in.close();
            for (int i = 0; i < 22; i++) {
                inputBoard[i] = line.charAt(i);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeOutputBoard(String opPath, String output) throws IOException {
        FileWriter op = null;
        try {
            op = new FileWriter(opPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        op.write(output);
        op.close();
    }

    public static List<char[]> generateMovesOpening(char[] inputBoard) {
        return generateAdd(inputBoard);
    }

    public static List<char[]> generateMovesMidgameEndgame(char[] inputBoard) {
        int noOfWhites = 0;
        for (char c : inputBoard) {
            if (c == 'W') noOfWhites++;
        }
        if (noOfWhites == 3) return generateHopping(inputBoard);
        else return generateMove(inputBoard);
    }

    public static List<char[]> generateAdd(char[] inputBoard) {
        List<char[]> op = new LinkedList<>();
        for (int loc = 0; loc < 22; loc++) {
            if (inputBoard[loc] == 'x') {
                char[] b = Arrays.copyOf(inputBoard, inputBoard.length);
                b[loc] = 'W';
                if (closeMill(loc, b)) generateRemove(b, op);
                else op.add(b);
            }
        }
        return op;
    }

    public static List<char[]> generateHopping(char[] inputBoard) {
        List<char[]> op = new LinkedList<>();
        for (int loc1 = 0; loc1 < 22; loc1++) {
            if (inputBoard[loc1] == 'W') {
                for (int loc2 = 0; loc2 < 22; loc2++) {
                    if (inputBoard[loc2] == 'x') {
                        char[] b = inputBoard;
                        b[loc1] = 'x';
                        b[loc2] = 'W';
                        if (closeMill(loc2, b)) generateRemove(b, op);
                        else op.add(b);
                    }
                }
            }
        }
        return op;
    }

    public static List<char[]> generateMove(char[] inputBoard) {
        List<char[]> op = new LinkedList<>();
        for (int loc = 0; loc < 22; loc++) {
            if (inputBoard[loc] == 'W') {
                List<Integer> n = neighbours(loc);
                for (int j : n) {
                    if (inputBoard[j] == 'x') {
                        char[] b = Arrays.copyOf(inputBoard, inputBoard.length);
                        b[loc] = 'x';
                        b[j] = 'W';
                        if (closeMill(j, b)) generateRemove(b, op);
                        else op.add(b);
                    }
                }
            }
        }
        return op;
    }

    public static void generateRemove(char[] inputBoard, List<char[]> op) {
        boolean noPositionsAdded = true;
        for (int loc = 0; loc < 22; loc++) {
            if (inputBoard[loc] == 'B') {
                if (!closeMill(loc, inputBoard)) {
                    char[] b = Arrays.copyOf(inputBoard, inputBoard.length);
                    b[loc] = 'x';
                    op.add(b);
                    noPositionsAdded = false;
                }
            }
        }
        if (noPositionsAdded) op.add(inputBoard);
    }

    public static char[] swapColors(char[] inputBoard) {
        char[] opboard = Arrays.copyOf(inputBoard, inputBoard.length);
        for (int loc = 0; loc < 22; loc++) {
            if (opboard[loc] == 'B') opboard[loc] = 'W';
            else if (opboard[loc] == 'W') opboard[loc] = 'B';
        }
        return opboard;
    }

    public static int staticEstimateOpening(char[] board) {
        int noOfWhites = 0;
        int noOfBlacks = 0;
        for (char c : board) {
            if (c == 'W') noOfWhites++;
            if (c == 'B') noOfBlacks++;
        }
        return noOfWhites - noOfBlacks;
    }

    public static int staticEstimateMidgameEndgame(char[] board) {
        int noOfWhites = 0;
        int noOfBlacks = 0;
        int noOfBlackMoves = 0;
        for (char c : board) {
            if (c == 'W') noOfWhites++;
            if (c == 'B') noOfBlacks++;
        }
        char[] blackboard = swapColors(board);
        noOfBlackMoves = (generateMovesMidgameEndgame(blackboard)).size();

        if (noOfBlacks <= 2) return 10000;
        else if (noOfWhites <= 2) return -10000;
        else if (noOfBlackMoves == 0) return 10000;
        else return ((1000 * (noOfWhites - noOfBlacks)) - noOfBlackMoves);
    }

    public static int staticEstimateImproved(char[] board) {
        int noOfWhites = 0;
        int noOfBlacks = 0;
        int noOfBlackMoves = 0;
        int noOfWhiteMoves = 0;
        int noOfWhtMillPcs = 0;
        int noOfBlcMillPcs = 0;
        int numWhitePiecesAtRisk = 0;
        int numBlackPiecesAtRisk = 0;
        for (int i = 0; i < 22; i++) {
            if (board[i] == 'W') noOfWhites++;
            if (board[i] == 'B') noOfBlacks++;
            if (board[i] == 'W' && closeMill(i, board)) noOfWhtMillPcs++;
            if (board[i] == 'B' && closeMill(i, board)) noOfBlcMillPcs++;
        }
        numWhitePiecesAtRisk = noOfWhites - noOfWhtMillPcs;
        numBlackPiecesAtRisk = noOfBlacks - noOfBlcMillPcs;
        noOfWhiteMoves = (generateMovesMidgameEndgame(board)).size();
        char[] blackboard = swapColors(board);
        noOfBlackMoves = (generateMovesMidgameEndgame(blackboard)).size();
        int score = 100 * (noOfWhites - noOfBlacks) + 100 * (noOfWhiteMoves - noOfBlackMoves) + 50 * (noOfWhtMillPcs - noOfBlcMillPcs) - 10 * (numWhitePiecesAtRisk - numBlackPiecesAtRisk);
        if (noOfBlacks <= 2) return 10000;
        else if (noOfWhites <= 2) return -10000;
        else if (noOfBlackMoves == 0) return 10000;
        else return score;
    }


    public static List<Integer> neighbours(int location) {
        switch (location) {
            case 0:
                return new LinkedList<Integer>(List.of(1, 3, 19));
            case 1:
                return new LinkedList<Integer>(List.of(0, 2, 4));
            case 2:
                return new LinkedList<Integer>(List.of(1, 5, 12));
            case 3:
                return new LinkedList<Integer>(List.of(0, 4, 6, 8));
            case 4:
                return new LinkedList<Integer>(List.of(1, 3, 5));
            case 5:
                return new LinkedList<Integer>(List.of(2, 4, 7, 11));
            case 6:
                return new LinkedList<Integer>(List.of(3, 7, 9));
            case 7:
                return new LinkedList<Integer>(List.of(5, 6, 10));
            case 8:
                return new LinkedList<Integer>(List.of(3, 9, 16));
            case 9:
                return new LinkedList<Integer>(List.of(6, 8, 13));
            case 10:
                return new LinkedList<Integer>(List.of(7, 11, 15));
            case 11:
                return new LinkedList<Integer>(List.of(5, 10, 12, 18));
            case 12:
                return new LinkedList<Integer>(List.of(2, 11, 21));
            case 13:
                return new LinkedList<Integer>(List.of(9, 14, 16));
            case 14:
                return new LinkedList<Integer>(List.of(13, 15, 17));
            case 15:
                return new LinkedList<Integer>(List.of(10, 14, 18));
            case 16:
                return new LinkedList<Integer>(List.of(8, 13, 17, 19));
            case 17:
                return new LinkedList<Integer>(List.of(14, 16, 18, 20));
            case 18:
                return new LinkedList<Integer>(List.of(11, 15, 17, 21));
            case 19:
                return new LinkedList<Integer>(List.of(0, 16, 20));
            case 20:
                return new LinkedList<Integer>(List.of(17, 19, 21));
            case 21:
                return new LinkedList<Integer>(List.of(12, 18, 20));
        }
        return null;
    }

    public static boolean closeMill(int location, char[] inputBoard) {
        char c = inputBoard[location];
        if (c == 'x') return false;
        switch (location) {
            case 0:
                if ((inputBoard[1] == c && inputBoard[2] == c) || (inputBoard[3] == c && inputBoard[6] == c))
                    return true;
                else return false;
            case 1:
                if (inputBoard[0] == c && inputBoard[2] == c) return true;
                else return false;
            case 2:
                if ((inputBoard[0] == c && inputBoard[1] == c) || (inputBoard[12] == c && inputBoard[21] == c) || (inputBoard[5] == c && inputBoard[7] == c))
                    return true;
                else return false;
            case 3:
                if ((inputBoard[4] == c && inputBoard[5] == c) || (inputBoard[0] == c && inputBoard[6] == c) || (inputBoard[8] == c && inputBoard[16] == c))
                    return true;
                else return false;
            case 4:
                if (inputBoard[3] == c && inputBoard[5] == c) return true;
                else return false;
            case 5:
                if ((inputBoard[3] == c && inputBoard[4] == c) || (inputBoard[2] == c && inputBoard[7] == c) || (inputBoard[11] == c && inputBoard[18] == c))
                    return true;
                else return false;
            case 6:
                if ((inputBoard[0] == c && inputBoard[3] == c) || (inputBoard[9] == c && inputBoard[13] == c))
                    return true;
                else return false;
            case 7:
                if ((inputBoard[2] == c && inputBoard[5] == c) || (inputBoard[10] == c && inputBoard[15] == c))
                    return true;
                else return false;
            case 8:
                if (inputBoard[3] == c && inputBoard[16] == c) return true;
                else return false;
            case 9:
                if (inputBoard[6] == c && inputBoard[13] == c) return true;
                else return false;
            case 10:
                if ((inputBoard[7] == c && inputBoard[15] == c) || (inputBoard[11] == c && inputBoard[12] == c))
                    return true;
                else return false;
            case 11:
                if ((inputBoard[10] == c && inputBoard[12] == c) || (inputBoard[5] == c && inputBoard[18] == c))
                    return true;
                else return false;
            case 12:
                if ((inputBoard[10] == c && inputBoard[11] == c) || (inputBoard[2] == c && inputBoard[21] == c))
                    return true;
                else return false;
            case 13:
                if ((inputBoard[6] == c && inputBoard[9] == c) || (inputBoard[14] == c && inputBoard[15] == c) || (inputBoard[16] == c && inputBoard[19] == c))
                    return true;
                else return false;
            case 14:
                if ((inputBoard[13] == c && inputBoard[15] == c) || (inputBoard[17] == c && inputBoard[20] == c))
                    return true;
                else return false;
            case 15:
                if ((inputBoard[13] == c && inputBoard[14] == c) || (inputBoard[18] == c && inputBoard[21] == c) || (inputBoard[7] == c && inputBoard[10] == c))
                    return true;
                else return false;
            case 16:
                if ((inputBoard[17] == c && inputBoard[18] == c) || (inputBoard[13] == c && inputBoard[19] == c) || (inputBoard[3] == c && inputBoard[8] == c))
                    return true;
                else return false;
            case 17:
                if ((inputBoard[16] == c && inputBoard[18] == c) || (inputBoard[14] == c && inputBoard[20] == c))
                    return true;
                else return false;
            case 18:
                if ((inputBoard[16] == c && inputBoard[17] == c) || (inputBoard[5] == c && inputBoard[11] == c) || (inputBoard[15] == c && inputBoard[21] == c))
                    return true;
                else return false;
            case 19:
                if ((inputBoard[13] == c && inputBoard[16] == c) || (inputBoard[20] == c && inputBoard[21] == c))
                    return true;
                else return false;
            case 20:
                if ((inputBoard[14] == c && inputBoard[17] == c) || (inputBoard[19] == c && inputBoard[21] == c))
                    return true;
                else return false;
            case 21:
                if ((inputBoard[19] == c && inputBoard[20] == c) || (inputBoard[2] == c && inputBoard[12] == c) || (inputBoard[15] == c && inputBoard[18] == c))
                    return true;
                else return false;
        }
        return false;
    }


    public static String stringBoard(char[] inputBoard) {
        String op = new String();
        for (char c : inputBoard)
            op = op + c;
        return op;
    }

    public static void printFunction(String inputBoard, int depth, String outputBoard, int noOfStaticEstimates, int minMaxEstimate) {
        System.out.println("Input Board Position : " + inputBoard + " Depth : "+depth);
        System.out.println("Output Board Position : " + outputBoard);
        System.out.println("Positions evaluated by static estimation : " + noOfStaticEstimates);
        System.out.println("MINMAX Estimate : " + minMaxEstimate);
    }
}
