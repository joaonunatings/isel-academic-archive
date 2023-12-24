package sokoban;

import sokoban.model.Board;
import sokoban.model.entities.*;
import util.Pos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;

/**
 * Class that loads a level from a file.
 * @see <a href="http://www.sokobano.de/wiki/index.php?title=Level_format">Level format rules</a>
 */
public class LevelLoader {

    /**
     * Reads height and width of the level file.
     * @param filename - Filepath to the level file.
     * @return - Empty Board with height and width set.
     */
    public static Board getEmptyBoard(String filename) {
        int height = 0;
        int width = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                height++;
                width = Math.max(width, line.length());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new Board(height, width);
    }

    /**
     * Loads a level from a file.
     * @see <a href="http://www.sokobano.de/wiki/index.php?title=Level_format">Level format rules</a>
     * @param filename - Filepath to the level file.
     * @return - The loaded level in a Board object.
     */
    public static Board load(String filename) {
        Board board = getEmptyBoard(filename);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            for (int y = 0; y < board.getHeight(); y++) {
                if ((line = reader.readLine()) == null) break;
                for (int x = 0; x < line.length(); x++) {
                    char c = line.charAt(x);
                    Pos pos = new Pos(x, y);
                    switch (c) {
                        case Wall.WALL_CHAR:
                            board.setPiece(new Wall(pos));
                            break;
                        case Player.PLAYER_CHAR:
                            if (board.getPlayer() != null)
                                throw new IllegalStateException("Player already placed. Cannot place another player.");
                            Player player = new Player(pos);
                            board.setPiece(player);
                            board.setPlayer(player);
                            break;
                        case Player.PLAYER_ON_GOAL_CHAR:
                            if (board.getPlayer() != null)
                                throw new IllegalStateException("Player already placed. Cannot place another player.");
                            Player playerOnGoal = new Player(pos, true);
                            board.setPiece(playerOnGoal);
                            board.setPlayer(playerOnGoal);
                            break;
                        case Box.BOX_CHAR:
                            Box box = new Box(pos);
                            board.setPiece(box);
                            break;
                        case Box.BOX_ON_GOAL_CHAR:
                            Box boxOnGoal = new Box(pos, true);
                            board.setPiece(boxOnGoal);
                            break;
                        case Floor.FLOOR_GOAL_CHAR:
                            Floor floorGoal = new Floor(pos, true);
                            board.setPiece(floorGoal);
                            break;
                        default:
                            board.setPiece(new Floor(pos));
                            break;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        getSetup(board);
        return board;
    }

    /**
     * Gets the setup of the level (i.e.: references to goals, boxes, number of boxes on goals, etc).
     * @param board - The board to get the setup from.
     */
    private static void getSetup(Board board) {
        HashSet<Box> boxes = new HashSet<>();
        HashSet<Piece> goals = new HashSet<>();
        int boxesOnGoals = 0;
        Pos pos = new Pos(0, 0);
        for (int y = 0; y < board.getHeight(); y++) {
            pos.setY(y);
            for (int x = 0; x < board.getWidth(); x++) {
                pos.setX(x);
                Piece piece = board.getPiece(pos);
                if (piece == null) board.setPiece(new Floor(pos));
                else {
                    if (piece.isGoal()) {
                        goals.add(piece);
                    }
                    if (piece instanceof Box) {
                        boxes.add((Box) piece);
                        if (piece.isGoal()) {
                            boxesOnGoals++;
                        }
                    }
                }
            }
        }
        board.setNumberOfGoals(goals.size());
        board.setGoals(goals);
        board.setBoxes(boxes);
        board.setBoxesOnGoals(boxesOnGoals);
    }
}
