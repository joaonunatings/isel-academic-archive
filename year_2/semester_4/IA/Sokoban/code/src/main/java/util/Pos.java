package util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * Represents position in 2D space.
 */
@Getter
@Setter
@AllArgsConstructor
public class Pos implements Cloneable {
    private int x, y;

    /**
     * Returns new position in given direction.
     * @param direction direction to move
     * @return new position
     */
    public Pos add(Direction direction) {
        return new Pos(x + direction.getX(), y + direction.getY());
    }

    @Override
    @SneakyThrows
    public Pos clone() {
        return (Pos)super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Pos)) return false;

        Pos pos = (Pos) obj;
        return pos.x == x && pos.y == y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
