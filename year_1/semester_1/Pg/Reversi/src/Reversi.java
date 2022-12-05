import isel.leic.pg.Console;
import static java.awt.event.KeyEvent.*;

public class Reversi
{
    public static final int BOARD_DIM = 8, BOARD_TOTAL = BOARD_DIM * BOARD_DIM;                         // [4..9] Dimension (lines and columns) of board
    private static boolean terminate = false, newgame = true;                                           // Flag to finish the game

    private static int cursorLine, indexLine, indexCol;                                                 // Current cursor line position
    private static char cursorCol;                                                                      // Current cursor column position

    private static boolean player = true;                                                               // Current player (true->A ; false->B)
    private static byte empty = 0, playerA = 1, playerB = 2, possibleplayerA = 3, possibleplayerB = 4;
    private static int lineLimit, colLimit;
    private static int totalA = 2, totalB = 2;                                                          // Pieces of each player

    private static byte[][] boardState = new byte[BOARD_DIM][BOARD_DIM];

    public static void main(String[] args)
    {
        Panel.init();
        Panel.printMessage("Welcome");
        while (newgame)
        {
            playGame();
            newGame();
        }
        Panel.printMessageAndWait("BYE");
        Panel.end();
    }

    private static void playGame()
    {
        int key;
        int middle = BOARD_DIM / 2;
        Panel.putPiece(middle, (char) ('A' + middle), true);
        updateBoard(indexLine(middle), indexCol((char) ('A' + middle)), playerA);
        Panel.putPiece(middle, (char) ('A' + middle - 1), false);
        updateBoard(indexLine(middle), indexCol((char) ('A' + middle - 1)), playerB);
        Panel.putPiece(middle + 1, (char) ('A' + middle), false);
        updateBoard(indexLine(middle + 1), indexCol((char) ('A' + middle)), playerB);
        Panel.putPiece(middle + 1, (char) ('A' + middle - 1), true);
        updateBoard(indexLine(middle + 1), indexCol((char) ('A' + middle - 1)), playerA);
        Panel.putMark(middle - 1, (char) ('A' + middle - 1), player);
        Panel.putMark(middle, (char)('A' + middle - 2), player);
        Panel.putMark(middle + 2, (char)('A' + middle), player);
        Panel.putMark(middle + 1, (char)('A' + middle + 1), player);
        updateCursor(1, 'A');

        do
        {
            key = Console.waitKeyPressed(3000);

            if (key > 0)
            {
                processKey(key);
                Console.waitKeyReleased(key);
            } else Panel.clearMessage();  // Clear last message after 3 seconds.
        } while (!terminate);
    }

    private static void newGame()
    {
        newgame = Panel.confirm("Play Again");
        terminate = !newgame;
        if(newgame)
            resetBoard();
    }

    private static void play()
    {
        possiblePlayMarks(false);
        indexLine = indexLine(cursorLine);
        indexCol = indexCol(cursorCol);

        if (validatePlay())
        {
            scoreCount();
            Panel.printTotal(true, totalA);
            Panel.printTotal(false, totalB);
            possiblePlayMarks(false);
            player = !player;
            gameOverCheck();
            boardStateConsole();
            possiblePlayMarks(true);
        }
    }

    private static boolean validatePlay()
    {
        boolean validPlay = false, posEmpty = true;

        if (boardState[indexLine][indexCol] != empty)
            posEmpty = false;
        else
        {
            for (int option = 0; option < 8; option++)
            {
                if (validSearch(lineDir(option), colDir(option)))
                {
                    validPlay = true;
                    flipPieces (lineDir(option), colDir(option));
                }
            }
        }
        if (!posEmpty || !validPlay)
        {
            Panel.printMessage("Invalid play");
            possiblePlayMarks(true);
        }

        return validPlay;
    }

    private static boolean validSearch (int lineDir, int colDir)
    {
        boolean validSearch = false;

        if (proximitySearch (lineDir, colDir))
            if (searchArray (lineDir, colDir))
                validSearch = true;
        return validSearch;
    }

    private static boolean proximitySearch (int lineDir, int colDir)
    {
        boolean validSearch = false;

        if (borderCheck (lineDir, colDir))
            if (searchPos(lineDir, colDir) == enemyPlayer())
                validSearch = true;

        return validSearch;
    }

    private static boolean borderCheck(int lineDir, int colDir) { return (indexLine + lineDir != -1 && indexLine + lineDir != BOARD_DIM && indexCol + colDir != -1 && indexCol + colDir != BOARD_DIM); }

    private static byte searchPos (int lineDir, int colDir) { return boardState[indexLine+lineDir][indexCol+colDir]; }

    private static boolean searchArray (int lineDir, int colDir)
    {
        int pieceCounter = 0;
        boolean validPlay = false;

        for (int lineToSearch = indexLine+lineDir, colToSearch = indexCol+colDir;
             (lineToSearch != -1 && lineToSearch != BOARD_DIM) && (colToSearch != -1 && colToSearch != BOARD_DIM);
             lineToSearch += lineDir, colToSearch += colDir)
        {
            if (boardState[lineToSearch][colToSearch] == enemyPlayer())
                pieceCounter++;
            else if (boardState[lineToSearch][colToSearch] == currentPlayer() && pieceCounter > 0)
            {
                lineLimit = lineToSearch;
                colLimit = colToSearch;
                validPlay = true;
                break;
            }
            else
            {
                validPlay = false;
                break;
            }
        }

        return validPlay;
    }

    private static void flipPieces (int lineDir, int colDir)
    {
        Panel.putPiece(cursorLine, cursorCol, player);
        updateBoard(indexLine, indexCol, currentPlayer());

        for (int lineToFlip = lineLimit + invertDir(lineDir), colToFlip = colLimit + invertDir(colDir);
             (lineToFlip != indexLine) || (colToFlip != indexCol);
             lineToFlip += invertDir(lineDir), colToFlip += invertDir(colDir))
        {
            boardState[lineToFlip][colToFlip] = currentPlayer();
            Panel.flipPiece (boardLine(lineToFlip), boardCol(colToFlip));
        }
    }

    private static boolean possiblePlays()
    {
        boolean playerCanPlay = false;

        for (int line = 0; line < BOARD_DIM; line++)
        {
            for (int col = 0; col < BOARD_DIM; col++)
            {
                if (boardState[line][col] == empty)
                {
                    indexLine = line;
                    indexCol = col;
                    for (int option = 0; option < 8; option++)
                    {
                        if (validSearch(lineDir(option), colDir(option)))
                        {
                            boardState[line][col] = (player) ? possibleplayerA : possibleplayerB;
                            playerCanPlay = true;
                        }
                    }
                }
            }
        }

        return playerCanPlay;
    }

    private static void resetPossiblePlays()
    {
        for (int line = 0; line < BOARD_DIM; line++)
        {
            for (int col = 0; col < BOARD_DIM; col++)
            {
                if (boardState[line][col] == possibleplayerA || boardState[line][col] == possibleplayerB)
                    boardState[line][col] = empty;
            }
        }
    }

    private static void possiblePlayMarks(boolean PutMark) //Put = true, Clear = false;
    {
        possiblePlays();
        for (int line = 0; line < BOARD_DIM; line++)
        {
            for (int col = 0; col < BOARD_DIM; col++)
            {
                if (boardState[line][col] == (player ? possibleplayerA : possibleplayerB))
                {
                    if (PutMark)
                        Panel.putMark (boardLine(line), boardCol(col), player);
                    else
                        Panel.clearMark (boardLine(line), boardCol(col));
                }
            }
        }
        resetPossiblePlays();
    }

    private static void gameOverCheck()
    {
        boolean firstCheck = false, secondCheck = false;

        if (!possiblePlays())
        {
            player = !player;
            if (!possiblePlays())
                firstCheck = true;
        }

        resetPossiblePlays();

        int withPieceCounter = 0;
        for (int line = 0; line < BOARD_DIM; line++)
        {
            for (int col = 0; col < BOARD_DIM; col++)
            {
                if (boardState[line][col] != empty)
                    withPieceCounter++;
            }
        }
        if (withPieceCounter == BOARD_TOTAL)
            secondCheck = true;

        if (firstCheck || secondCheck)
		{
            Panel.printMessageAndWait("Game over");
            terminate = Panel.confirm("Terminate game");
		}
	}

    private static void scoreCount ()
    {
        totalA = 0;
        totalB = 0;

        for (int line = 0; line < BOARD_DIM; line++)
        {
            for (int col = 0; col < BOARD_DIM; col++)
            {
                if (boardState[line][col] == playerA)
                    totalA++;
                else if (boardState[line][col] == playerB)
                    totalB++;
            }
        }
    }



    private static void processKey(int key)
    {
        switch (key) {
            case VK_ESCAPE: terminate = Panel.confirm("Terminate game"); break;
            case VK_UP:     updateCursor(cursorLine-1,cursorCol); break;
            case VK_DOWN:   updateCursor(cursorLine+1,cursorCol); break;
            case VK_LEFT:   updateCursor(cursorLine,cursorCol-1); break;
            case VK_RIGHT:  updateCursor(cursorLine,cursorCol+1); break;
            case VK_SPACE:
            case VK_ENTER:  play(); break;
            case VK_N:      newGame(); terminate = newgame; break;
            default:
                if (key>='A' && key<'A'+BOARD_DIM) updateCursor(cursorLine,key);
                else if (key>='1' && key<'1'+BOARD_DIM) updateCursor(key-'0',cursorCol);
        }
    }

    private static void updateCursor(int line, int col)
    {
        if (line<1 || line>BOARD_DIM || col<'A' || col>='A'+BOARD_DIM) return;
        cursorLine = line;
        cursorCol = (char) col;
        Panel.moveCursor(cursorLine,cursorCol,player);
    }

    private static void resetBoard()
    {
        player = true;
        for (int line = 0; line < BOARD_DIM; line++)
        {
            for (int col = 0; col < BOARD_DIM; col++)
                boardState[line][col] = empty;
        }
        possiblePlayMarks(false);
        Panel.printGrid();
    }

    private static void updateBoard(int line, int col, byte player) { boardState[line][col] = player; }



    private static int boardLine (int line) { return line+1; }

    private static char boardCol (int col) { return (char)(col+'A'); }

    private static int indexLine (int line) { return line-1; }

    private static int indexCol (char col) { return col-'A'; }

    private static byte currentPlayer() { return (player) ? playerA : playerB; }

    private static byte enemyPlayer() { return (player) ? playerB : playerA; }

    private static int invertDir (int countDir)
    {
        switch(countDir)
        {
            case -1:
                return 1;
            case 1:
                return -1;
            default:
                return 0;
        }
    }

    private static int lineDir (int option)             //lineDir by order = [-1,-1,-1,0,0,1,1,1]
    {
        int lineDir;

        switch (option)
        {
            case 0:
            case 1:
            case 2:
                lineDir = -1;
                return lineDir;
            case 3:
            case 4:
                lineDir = 0;
                return lineDir;
            case 5:
            case 6:
            case 7:
                lineDir = 1;
                return lineDir;
            default:
                return 0;

        }
    }

    private static int colDir (int option)             //colDir by order = [-1,0,1,-1,1,-1,0,1]
    {
        int colDir;

        switch (option)
        {
            case 0:
            case 3:
            case 5:
                colDir = -1;
                return colDir;
            case 1:
            case 6:
                colDir = 0;
                return colDir;
            case 2:
            case 4:
            case 7:
                colDir = 1;
                return colDir;
            default:
                return 0;
        }
    }

    private static void boardStateConsole()
    {
        for (int line = 0; line < BOARD_DIM; line++)
        {
            for (int col = 0; col < BOARD_DIM; col++)
                System.out.print("[" + boardState[line][col] + "] ");
            System.out.println();
        }
        System.out.println("-------------------------------");
    }
}