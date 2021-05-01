package board;

public class Values {
    public static int WIN = 10000;
    public static int WIN_OPPONNENT = 9000;

    public static int WIN_DONT_PLAY = -WIN_OPPONNENT;
    public static int WIN_OPPONENT_DONT_PLAY = -WIN;

    public static int TRAP = 1000;

    public static int INTERSECTION_ONE_ONE_ADJACENT = 5;
    public static int INTERSECTION_ONE_ONE_PIVOT = 10;

    public static int INTERSECTION_ONE_TWO_ADJACENT = 15;
    public static int INTERSECTION_ONE_TWO_PIVOT = 20;

    public static int INTERSECTION_TWO_TWO = 500;


    public static int INTERSECTION_EMPTY_TWO = 5;


    public static int EMPTY_LINE = 10;
    public static int LINE_ONE = 0;
    public static int LINE_TWO = 0;

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_WHITE = "\u001B[37m";

}
