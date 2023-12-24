package sokoban.model.entities;

import util.Pos;

/**
 * A player on the board.
 * There can be only one player on the board.
 * Player can push boxes.
 * Player can not move if it is against a wall.
 */
public class Player extends Piece {
    public static final char PLAYER_CHAR = '@', PLAYER_ON_GOAL_CHAR = '+';

    public Player(Pos pos, boolean isGoal) {
        super(pos, false, true, isGoal);
    }

    public Player(Pos pos) {
        this(pos, false);
    }

    @Override
    public String toString() {
        return String.valueOf(isGoal() ? PLAYER_ON_GOAL_CHAR : PLAYER_CHAR);
    }
}