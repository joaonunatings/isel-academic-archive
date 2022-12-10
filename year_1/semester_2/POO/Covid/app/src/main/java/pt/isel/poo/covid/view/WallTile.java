package pt.isel.poo.covid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import pt.isel.poo.covid.R;
import pt.isel.poo.covid.tile.Img;
import pt.isel.poo.covid.tile.Tile;

public class WallTile implements Tile {

    private final Paint brush;
    private Img img;


    public WallTile(Context context){
        brush = new Paint();
        img = new Img(context, R.drawable.wall);


    }

    @Override
    public void draw(Canvas canvas, int side) {

        img.draw(canvas,side,side,brush);
    }

    @Override
    public boolean setSelect(boolean selected) {
        return false;
    }
}
