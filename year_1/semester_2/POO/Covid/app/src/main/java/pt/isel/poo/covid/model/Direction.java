package pt.isel.poo.covid.model;

public enum Direction {

    NONE(0,0),
    NORTH(0,-1),
    SOUTH(0,1),
    EAST(1,0),
    WEST(-1,0);

    final int dx,dy;


    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}
