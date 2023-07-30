import java.io.IOException;

public class MiniMaxGame {
    static int noOfStaticEstimates = 0;
    static char[] opBoard = new char[22];
    static int startingDepth = 0;

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Incorrect arguments!");
            System.err.println("Required Arguments: <Input File Path> <Output File Path> <Depth>");
        }
        String ipPath = args[0];
        String opPath = args[1];
        int depth = Integer.parseInt(args[2]);
        char[] inputBoard = new char[22];

        startingDepth = depth;
        HelperClass.readInputBoard(ipPath, inputBoard);
        int minMaxEstimate = maxMinGame(inputBoard, depth);
        String outputBoard = HelperClass.stringBoard(opBoard);
        String ipBoard = HelperClass.stringBoard(inputBoard);
        HelperClass.printFunction(ipBoard, depth ,outputBoard, noOfStaticEstimates, minMaxEstimate);
        HelperClass.writeOutputBoard(opPath, outputBoard);

    }

    private static int maxMinGame(char[] inputBoard, int depth) {
        if (depth == 0) {
            noOfStaticEstimates++;
            return HelperClass.staticEstimateMidgameEndgame(inputBoard);
        } else {
            int v = Integer.MIN_VALUE;
            for (char[] child : HelperClass.generateMovesMidgameEndgame(inputBoard)) {
                int temp = minMaxGame(child, depth - 1);
                if (temp > v) {
                    v = temp;
                    if (startingDepth == depth)
                        opBoard = child;
                }
            }
            return v;
        }
    }

    private static int minMaxGame(char[] inputBoard, int depth) {
        if (depth == 0) {
            noOfStaticEstimates++;
            return HelperClass.staticEstimateMidgameEndgame(inputBoard);
        } else {
            int v = Integer.MAX_VALUE;
            char[] blackBoard = HelperClass.swapColors(inputBoard);
            for (char[] child : HelperClass.generateMovesMidgameEndgame(blackBoard)) {
                child = HelperClass.swapColors(child);
                int temp = maxMinGame(child, depth - 1);
                v = v < temp ? v : temp;
            }
            return v;
        }
    }


}
