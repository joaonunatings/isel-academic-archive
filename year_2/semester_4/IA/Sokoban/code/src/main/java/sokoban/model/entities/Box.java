package sokoban.model.entities;

import lombok.Getter;
import lombok.Setter;
import util.Direction;
import util.Pos;

import java.util.LinkedList;

/**
 * A box on the board.
 * Box can be moved by player.
 * A box cannot be pushed if it is against a wall or another box.
 */
@Getter
@Setter
public class Box extends Piece {
    public static final char BOX_CHAR = '$', BOX_ON_GOAL_CHAR = '*';

    // History of moves from box
    private LinkedList<Direction> history = new LinkedList<>();

    public Box(Pos pos, boolean isGoal) {
        super(pos, false, true, isGoal);
    }

    public Box(Pos pos) {
        this(pos, false);
    }

    @Override
    public Box clone() {
        Box box = (Box)super.clone();
        box.setPos(getPos().clone());
        box.setHistory(new LinkedList<>(getHistory()));
        return box;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        Box box = (Box)obj;
        return getHistory().equals(box.getHistory());
    }

    @Override
    public String toString() {
        return String.valueOf(isGoal() ? BOX_ON_GOAL_CHAR : BOX_CHAR);
    }
}