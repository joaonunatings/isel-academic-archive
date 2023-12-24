package util;

import lombok.Getter;

/**
 * Represents a direction vector in 2D space.
 */
@Getter
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private final int x, y;
    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Direction random() {
        return values()[(int) (Math.random() * values().length)];
    }

    /**
     * Return destination position based on current position
     * @param pos - current position
     * @return - destination position
     */
    public Pos getPos(Pos pos) {
        return new Pos(pos.getX() + x, pos.getY() + y);
    }

    /**
     * @return - Inverse direction (i.e. UP -> DOWN, LEFT -> RIGHT, ...)
     */
    public Direction inverse() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            default:
                return LEFT;
        }
    }
}