package sokoban.model.entities;

import util.Pos;

/**
 * A floor on the board.
 * A floor can be a goal or not.
 * The player can walk on the floor.
 */
public class Floor extends Piece {
    public static final char FLOOR_CHAR_1 = '-', FLOOR_CHAR_2 = ' ', FLOOR_GOAL_CHAR = '.';

    public Floor(Pos pos, boolean isGoal) {
        super(pos, true, false, isGoal);
    }

    public Floor(Pos pos) {
        this(pos, false);
    }

    @Override
    public String toString() {
        return String.valueOf(isGoal() ? FLOOR_GOAL_CHAR : FLOOR_CHAR_2);
    }
}