package sokoban.model.entities;

import lombok.*;
import util.Pos;

/**
 * A piece on the board.
 * There can only be one piece on a tile in the board.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Piece implements Cloneable {

    // Current position of piece
    @NonNull private Pos pos;

    // Piece can be walked on or moved something on it (i.e.: floor)
    private boolean isWalkable;

    // Piece can move (i.e.: boxes and player)
    private boolean isMovable;

    // Piece which is goal (i.e.: floor with goal, box on goal, player on goal)
    private boolean isGoal;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Piece)) return false;

        Piece piece = (Piece) obj;
        if (piece.isGoal != isGoal || piece.isMovable != isMovable || piece.isWalkable != isWalkable) return false;
        return pos.equals(piece.pos);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    @SneakyThrows
    public Piece clone() {
        Piece piece = (Piece)super.clone();
        piece.setPos(pos.clone());
        return piece;
    }
}