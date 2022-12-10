package pt.isel.poo.covid.model;

import android.content.Context;

import pt.isel.poo.covid.tile.Tile;

public abstract class LevelElement{


    protected Location position;
    protected char character;

    protected LevelElement(Location position){
        this.position = position;
    }

    public Location getPosition(){
        return position;
    }

    public Tile tileType(Context context){
        return null ;

    }

    public char getChar(){
        return character;
    }

    public boolean killsElement(){
        return false;
    }

}
