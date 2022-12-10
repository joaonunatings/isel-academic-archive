package pt.isel.poo.covid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import pt.isel.poo.covid.R;
import pt.isel.poo.covid.model.Hero;
import pt.isel.poo.covid.tile.Img;
import pt.isel.poo.covid.tile.Tile;

public class HeroTile implements Tile {

    private final Paint brush;
    private Img img;
    private Img img2;
    private final Hero hero;


    public HeroTile(Context context,Hero hero){
        brush = new Paint();
        img = new Img(context, R.drawable.nurse);
        img2 = new Img(context,R.drawable.dead);
        this.hero = hero;

    }

    @Override
    public void draw(Canvas canvas, int side) {
        img.draw(canvas,side,side,brush);
        if(hero.isDead())img2.draw(canvas,side,side,brush);
    }

    @Override
    public boolean setSelect(boolean selected) {
        return false;
    }
}
