package pt.isel.poo.covid.model;

import android.content.Context;

import pt.isel.poo.covid.tile.Tile;
import pt.isel.poo.covid.view.WallTile;

public class Wall extends LevelElement {

    /**
     * Initiates a wall placing it at the given location.
     * @param location  the location where the wall is placed.
     */
    public Wall(Location location) {
        super(location);
        this.character = 'X';

    }

    public Tile tileType(Context context){
        Tile tile = new WallTile(context);
        return tile;
    }
}

