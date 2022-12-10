package pt.isel.poo.covid.model;


import android.content.Context;

import pt.isel.poo.covid.tile.Tile;
import pt.isel.poo.covid.view.VirusTile;

public class Virus extends LevelElement {

    private int arenaWidth,arenaHeight;
    private Direction currentDirection;
    private Level level;
    private boolean isDead;


    public Virus(Location location,Level level) {
        super(location);
        this.currentDirection = Direction.NONE;
        this.arenaWidth = level.arenaWidth;
        this.arenaHeight = level.arenaHeight;
        this.level = level;
        this.character = '*';
    }

    public boolean canMove(Direction direction){
        final Location newLocation = position.add(direction);
        return newLocation.x >= 0 && newLocation.x < arenaWidth &&
                newLocation.y >= 0 && newLocation.y < arenaHeight &&
                (level.getElementAt(newLocation.x,newLocation.y) == null);

    }

    /**
     * Moves the virus in the selected direction
     */
    public void move() {


        if(isDead)level.deleteElement(position);
        if (canMove(currentDirection)) {
            Location oldPosition = position;
            position = position.add(currentDirection);
            level.swap(oldPosition,position);

        }
        else{
            setDead(currentDirection);
        }
    }

    public void setDead(Direction direction){
        final Location newLocation = position.add(direction);

        if(newLocation.x >= 0 && newLocation.x < arenaWidth &&
                newLocation.y >= 0 && newLocation.y < arenaHeight)
            isDead = (level.getElementAt(newLocation.x,newLocation.y).killsElement());

    }

    public boolean isDead(){
        return isDead;
    }

    public void changeDirection(Direction newDirection) {
        currentDirection = newDirection;
    }

    public Tile tileType(Context context){
        Tile tile = new VirusTile(context);
        return tile;
    }
}

