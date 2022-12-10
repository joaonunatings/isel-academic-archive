package pt.isel.poo.covid.model;

/**
 *Represents a position in the level.
 */
public class Location {


    public  int x,y;

    /**
     * Initiates the instance with the given coordinates
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     */
    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Location add(Direction direction) {
        return new Location(x + direction.dx, y + direction.dy);
    }

}
