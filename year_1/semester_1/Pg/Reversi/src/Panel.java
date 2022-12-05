import isel.leic.pg.Console;
import static isel.leic.pg.Console.*;

public class Panel {
    // COLORS
    public static final int
        PIECE_A_COLOR = RED, PIECE_B_COLOR = GREEN,
        NO_PIECE_COLOR = DARK_GRAY,
        GRID_COLOR = LIGHT_GRAY;

    // DIMENSIONS
    private static final int
        BOARD_DIM = Reversi.BOARD_DIM,
        GRID_SIZE = 3,
        BOARD_SIZE = (GRID_SIZE+1) * BOARD_DIM +1,
        STATUS_COLS = 8,
        LINES = BOARD_SIZE + 1,
        COLS = BOARD_SIZE + STATUS_COLS,
        FONT_SIZE = 16;

    // Color of each grid position (NO_PIECE_COLR ,PIECE_A_COLOR or PIECE_B_COLOR)
    private static int[][] color = new int[BOARD_DIM][BOARD_DIM];  // ATENTION: Only used in Panel class.

    // Current position of cursor. ATENTION: Only used in Panel class.
    private static int lineCursor=0;  // (1..8)
    private static char colCursor=0;  // ('A'..'H')

    /**
     * Open console window and print initial panel
     */
    public static void init() {
        Console.fontSize(FONT_SIZE);
        Console.open("PG Reversi", LINES, COLS);
        Console.enableMouseEvents(false);
        printGrid();
    }

    /**
     * Close console window
     */
    public static void end() {
        Console.close();
    }

    /**
     * Converts the grid line number (1..8) to the line in console coordinates.
     * @param line of grid (1..8)
     * @return Line of the top left corner of the grid cell
     */
    private static int lineOf(int line) { return 1+(line-1)*(GRID_SIZE+1); }

    /**
     * Converts the grid column number (1..8) to the column in console coordinates.
     * @param col of grid (1..8)
     * @return Column of the top left corner of the grid cell
     */
    private static int colOf(char col) { return 1+(col-'A')*(GRID_SIZE+1); }

    // Utility methods to print a char
    private static void print(int line, int col, char c) { Console.cursor(line,col); Console.print(c); }
    private static void print(int fColor, int bColor, int line, int col, char c) { Console.color(fColor,bColor); print(line,col,c); }
    // Utility methods to print a string
    private static void print(int line, int col, String s) { Console.cursor(line,col); Console.print(s); }
    private static void print(int fColor, int bColor, int line, int col, String s) { Console.color(fColor,bColor); print(line,col,s); }

    /**
     * Print initial panel
     */
    public static void printGrid() {
        for (int l = 0; l < BOARD_DIM; l++)
            for (int c = 0; c < BOARD_DIM; c++)
                color[l][c] = NO_PIECE_COLOR;
        clearRect(0,0,BOARD_SIZE,BOARD_SIZE,GRID_COLOR);
        for (int l = 1; l < BOARD_DIM+1 ; l++) {
            print(BLACK, GRID_COLOR, lineOf(l)+(GRID_SIZE/2), 0, (char)(l+'0'));
            for (char c = 'A'; c < BOARD_DIM + 'A'; ++c) {
                if (l==1) print(BLACK, GRID_COLOR, 0, colOf(c)+(GRID_SIZE/2), c);
                clearRect(lineOf(l), colOf(c), GRID_SIZE, GRID_SIZE, NO_PIECE_COLOR);
            }
        }
        clearMessage();
        printLabel("Cursor",1);
        printValue("??",2);
        printLabel("A",4);
        printTotal(true,2);
        printLabel("B",7);
        printTotal(false,2);
    }

    /**
     * Place a piece in a grid position
     * @param line Line of grid to put piece
     * @param col Column of grid to put piece
     * @param player (true -> player A ; false -> player B)
     */
    public static void putPiece(int line, char col, boolean player) {
        final int pieceColor = player ? PIECE_A_COLOR : PIECE_B_COLOR;
        clearRect(lineOf(line), colOf(col), GRID_SIZE, GRID_SIZE, pieceColor);
        color[line-1][col-'A'] = pieceColor;
        if (line==lineCursor && col==colCursor) showCursor(player);
    }

    /**
     * Turn the piece placed in a grid position.
     * Make an animation showing one part at a time and waiting a while.
     * @param line Line of grid to turn piece
     * @param col Column of grid to turn piece
     */
    public static void flipPiece(int line, char col) {
        final int pieceColor = color[line-1][col-'A'] == PIECE_A_COLOR ? PIECE_B_COLOR : PIECE_A_COLOR;
        final int l = lineOf(line), c = colOf(col);
        clearRect(l,c,GRID_SIZE,1, NO_PIECE_COLOR);
        clearRect(l,c+2,GRID_SIZE,1, NO_PIECE_COLOR);
        sleep(100);
        clearRect(l,c,GRID_SIZE,GRID_SIZE,NO_PIECE_COLOR);
        sleep(25);
        clearRect(l,c+1,GRID_SIZE,1, pieceColor);
        sleep(100);
        clearRect(l,c,GRID_SIZE,GRID_SIZE, pieceColor);
        color[line-1][col-'A'] = pieceColor;
    }

    /**
     * Move cursor from current position to new position
     * @param line Line of new position
     * @param col Column of new position
     * @param player Current player to determine color
     */
    public static void moveCursor(int line, char col, boolean player) {
        hideCursor();           // Hide from current position
        lineCursor = line;      // Change current line
        colCursor = col;        // Change current column
        showCursor(player);     // Show in new current position
    }

    // Hide cursor from current position
    private static void hideCursor() {
        if (lineCursor!=0) {
            final int bkColor = color[lineCursor-1][colCursor-'A'];
            final int l = lineOf(lineCursor), c = colOf(colCursor);
            setBackground(bkColor);
            print(l+1, c, ' ');
            print(l+1, c+2, ' ');
            print(l, c+1, ' ');
            print(l+2, c+1, ' ');
        }
    }

    // Show cursor at current position
    private static void showCursor(boolean player) {
        final int bkColor = color[lineCursor-1][colCursor-'A'];
        final int l = lineOf(lineCursor), c = colOf(colCursor);
        final int plColor = player?PIECE_A_COLOR:PIECE_B_COLOR;
        color(plColor==bkColor? YELLOW : plColor ,bkColor);
        print(l+1, c, '#');
        print(l+1, c+2, '#');
        print(l, c+1, '#');
        print(l+2, c+1, '#');
        printValue(""+lineCursor+colCursor,2);
    }

    /**
     * Shows an asterisk in a position where it is possible to play a piece.
     * @param line Line of position
     * @param col Column of position
     * @param player Current player to determine color of asterisk
     */
    public static void putMark(int line, char col, boolean player) {
        final int bkColor = color[line-1][col-'A'];
        print(player?PIECE_A_COLOR:PIECE_B_COLOR, bkColor, lineOf(line)+1, colOf(col)+1, '*');
    }

    /**
     * Removes the asterisk that marked a position.
     * @param line Line of position
     * @param col Column of position
     */
    public static void clearMark(int line, char col) {
        final int bkColor = color[line-1][col-'A'];
        print(WHITE, bkColor, lineOf(line)+1, colOf(col)+1, ' ');
    }

    /**
     * Shows the number of pieces assigned to a player.
     * @param player The player (true -> A ; false -> B)
     * @param number The number of pieces.
     */
    public static void printTotal(boolean player, int number) {
        print(BLACK, player?PIECE_A_COLOR:PIECE_B_COLOR, 3+(player?2:5), BOARD_SIZE+1, center(""+number,STATUS_COLS-2));
    }

    /**
     * Shows the level of the algorithm used in computer automatic plays.
     * @param level The level (0 -> Manual plays ; 1..N -> Automatic plays)
     */
    public static void printAuto(int level) {
        print(WHITE, BLACK, 9, BOARD_SIZE, center(level>0 ? "AUTO "+level : "",STATUS_COLS));
    }

    // Show the label of one information
    private static void printLabel(String txt, int line) {
        print(GRAY, BLACK, line, BOARD_SIZE, center(txt,STATUS_COLS));
    }
    // Show the value of one information
    private static void printValue(String value, int line) {
        print(WHITE, BLACK, line, BOARD_SIZE, center(value,STATUS_COLS));
    }
    // Deletes a rectangular area of the console, given its position, size and background color.
    private static void clearRect(int lin, int col, int height, int width, int color) {
        setBackground(color);
        for(int i=0 ; i<height ; ++i) {
            cursor(lin + i, col);
            printRepeat(' ',width);
        }
    }
    // Prints a symbol repeatedly
    private static void printRepeat(char c, int times) {
        for (; times>0 ; times--) Console.print(c);
    }

    /**
     * Show the message in bottom line.
     * Message is cropped if larger than window width
     * @param msg Text to print.
     * @return Then real length of the written message.
     */
    public static int printMessage(String msg) {
        int key;
        while ((key= getKeyPressed())!=NO_KEY) { // Wait to release all keys
            if (key >= 0) waitKeyReleased(key);
            else getMouseEvent();
        }
        clearMessage();
        setForeground(YELLOW);
        int len = msg.length();
        if (len>COLS) {
            msg = msg.substring(0, COLS / 2 - 2) + ".." + msg.substring(len - COLS / 2 - 1, len);
            len = msg.length();
        }
        print(BOARD_SIZE, 0, center(msg,COLS));
        return len;
    }

    /**
     * Erase the message in bottom line.
     */
    public static void clearMessage() {
        clearRect(BOARD_SIZE,0,1,COLS,DARK_GRAY);
    }

    /**
     * Write the message and return when a key is pressed or after four seconds.
     * @param msg Text to print.
     */
    public static void printMessageAndWait(String msg) {
        printMessage(msg);
        int key;
        do {
            key = waitKeyPressed(4000);     // Wait a time or a key
            if (key >= 0) waitKeyReleased(key);
            getMouseEvent();
        } while(key == MOUSE_EVENT );
        clearMessage();
    }

    /**
     * Write a question followed by "(Y/N)?" and waits for a key that is either S or Y or N.
     * Returns true if the key was Y or Y. Returns false if the key was N.
     * @param question Text to print before "(Y/N)?"
     * @return true if the key was S or Y. false if the key was N.
     */
    public static boolean confirm(String question) {
        question += " (Y/N)?";
        int len = printMessage(question);
        cursor(BOARD_SIZE,COLS/2+len/2);
        cursor(true);
        boolean result = false;
        int key;
        do {
            key = waitKeyPressed(0);
            if (key >= 0) waitKeyReleased(key);
            if (key == 'S' || key == 'Y') result = true;
        } while (key!='N' && !result);
        cursor(false);
        clearMessage();
        return result;
    }

    /**
     * Write a question followed by "?" and read a string with the maximum length.
     * @param question Text to print before "?"
     * @param maxLength Maximum length of string to read
     * @return The string readed
     */
    public static String readString(String question, int maxLength) {
        question += "?";
        if (question.length()+1+maxLength>COLS && maxLength > COLS/2-2)
            maxLength = COLS/2-2;
        int len = printMessage(question+spaces(maxLength));
        String text;
        do {
            cursor(BOARD_SIZE, COLS/2 + len/2 - maxLength +1);
            color(BLACK, WHITE);
            clearAllChars();
            text = nextLine(maxLength);
        } while (text==null || text.length()==0);
        clearMessage();
        clearAllChars();
        return text;
    }

    // Center string text to a certain width.
    private static String center(String txt, int width) {
        final int spaces = width-txt.length();
        if (spaces<=0) return txt;
        char[] res = new char[width];
        int left = spaces - spaces/2, i;
        for (i = 0; i < left; i++) res[i]=' ';
        for (int j=0; j< txt.length() ; ++j) res[i++]=txt.charAt(j);
        while( i<width ) res[i++]=' ';
        return new String(res);
    }

    // Make a string with times spaces.
    private static String spaces(int times) {
        char[] res = new char[times];
        while(--times>=0) res[times] = ' ';
        return new String(res);
    }
}
