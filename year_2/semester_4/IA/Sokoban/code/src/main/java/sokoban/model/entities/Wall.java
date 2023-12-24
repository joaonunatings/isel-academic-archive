package sokoban.model.entities;

import util.Pos;

/**
 * A wall on the board.
 * A wall cannot be moved by player.
 * A wall cannot have a goal.
 */
public class Wall extends Piece {
    public static final char WALL_CHAR = '#';

    public Wall(Pos pos) {
        super(pos, false, false, false);
    }

    @Override
    public String toString() {
        return String.valueOf(WALL_CHAR);
    }
}