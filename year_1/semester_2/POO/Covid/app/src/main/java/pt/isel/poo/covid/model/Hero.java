package pt.isel.poo.covid.model;

import android.content.Context;


import pt.isel.poo.covid.tile.Tile;
import pt.isel.poo.covid.view.HeroTile;

/**
 * Represents the Hero in the game
 */
public class Hero extends LevelElement {


    private Direction currentDirection;
    private boolean isDead;
    private Level level;
    private int arenaWidth,arenaHeight;


    /**
     * Initiates a Hero placing it at the given location.
     * @param location  the location where the hero is placed.
     */
    public Hero(Location location,Level level) {
        super(location);
        this.currentDirection = Direction.NONE;
        this.character = '@';
        this.arenaWidth = level.arenaWidth;
        this.arenaHeight = level.arenaHeight;
        this.level = level;
    }

    public boolean canMove(Direction direction){
        final Location newLocation = position.add(direction);

        return newLocation.x >= 0 && newLocation.x < arenaWidth &&
                newLocation.y >= 0 && newLocation.y < arenaHeight &&
                (level.getElementAt(newLocation.x,newLocation.y) == null);
    }

    /**
     * Moves the hero in the selected direction
     */
    public void move() {
        if (isDead)
            throw new IllegalStateException();
        if (canMove(currentDirection)) {

            Location oldPosition = position;
            position = position.add(currentDirection);
            level.swap(oldPosition,position);

        }
        else setDead(currentDirection);
    }

    public void setDead(Direction direction){
        final Location newLocation = position.add(direction);
        if(newLocation.x >= 0 && newLocation.x < arenaWidth &&
                newLocation.y >= 0 && newLocation.y < arenaHeight)
        isDead = (level.getElementAt(newLocation.x,newLocation.y).killsElement());
        if(isDead) level.updateElement(position);

    }

    public boolean isDead(){
        return isDead;
    }

    public Direction getHeroDirection (){
        return currentDirection;

    }
    public void changeDirection(Direction newDirection) {
        currentDirection = newDirection;
    }


    public Tile tileType(Context context){
        Tile tile = new HeroTile(context,this);
        return tile;
    }
}

