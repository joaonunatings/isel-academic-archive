package util;

import sokoban.model.Board;
import sokoban.model.entities.Box;
import sokoban.model.entities.Piece;
import sokoban.model.entities.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for common operations.
 */
public class Utils {

    /**
     * Clears screen using ANSI escape codes.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Returns a list of random integers.
     * Used to generate a list simulating a random sequence of indexes.
     * @param min minimum value
     * @param max maximum value
     * @return list of random integers
     */
    public static List<Integer> listOfRandoms(int min, int max) {
        List<Integer> list = new ArrayList<>(max - min + 1);
        for (int i = min; i <= max; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        return list;
    }

    /**
     * Returns a list of random directions.
     * Can also contain null values. These null values appear with given percentage.
     * @param size size of the list
     * @param nullPercentage percentage of null values
     * @return list of random directions
     */
    public static List<Direction> listOfRandomDirections(int size, float nullPercentage) {
        List<Direction> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            if (Math.random() < nullPercentage) {
                list.add(null);
            } else {
                list.add(Direction.random());
            }
        }
        return list;
    }

    /**
     * Returns the first box that is touching the player or null.
     * @param board board to search in
     * @return Box or null
     */
    public static Box getTouchingBox(Board board) {
        Player player = board.getPlayer();
        for (Direction direction : Direction.values()) {
            Piece piece = board.getPiece(direction.getPos(player.getPos()));
            if (piece instanceof Box) {
                return (Box) piece;
            }
        }
        return null;
    }
}
