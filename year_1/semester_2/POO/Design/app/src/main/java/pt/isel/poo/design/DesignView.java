package pt.isel.poo.design;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import static pt.isel.poo.design.Design.finalList;
import static pt.isel.poo.design.Design.list;


public class DesignView extends View {


    private final Paint pointer;


    private Design design;
    private Canvas canvas;


    public DesignView(Context context, AttributeSet attrs) {


        super(context, attrs);
        pointer = new Paint();
        pointer.setColor(Color.BLACK);
        pointer.setStrokeWidth(5);
        pointer.setStyle(Paint.Style.STROKE);
        design = null;
    }

    public void setModel(Design model) {
         this.design = model;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int i = 0;
        int a = 0;
        if(design == null )return;

        //temp
        if (list.size() > 0  )
            drawFigures(canvas,list.get(a++));

        for ( ;i < finalList.size();) {

            drawFigures(canvas,finalList.get(i));
            i++;
        }

    }

        private void drawFigures(Canvas canvas, Figures list)   {

            switch(list.getFigure()){
                case 'L' :
                    canvas.drawLine(list.getPointA().x,list.getPointA().y,list.getPointB().x,list.getPointB().y,pointer);
                    break;
                case 'C':

                    canvas.drawCircle(list.getPointA().x,list.getPointA().y,list.getRadius(),pointer);
                    break;
                case 'P':
                    canvas.drawLine(list.getPointB().x,list.getPointB().y,list.getPointB().x+1,list.getPointB().y+1,pointer);
                    canvas.drawLine(list.getPointB().x,list.getPointB().y,list.getPointB().x+1,list.getPointB().y-1,pointer);
                    canvas.drawLine(list.getPointB().x,list.getPointB().y,list.getPointB().x-1,list.getPointB().y+1,pointer);
                    canvas.drawLine(list.getPointB().x,list.getPointB().y,list.getPointB().x-1,list.getPointB().y-1,pointer);

                    break;
                case 'R':
                    canvas.drawLine(list.getPointA().x,list.getPointA().y,list.getPointB().x,list.getPointA().y,pointer);
                    canvas.drawLine(list.getPointA().x,list.getPointA().y,list.getPointA().x,list.getPointB().y,pointer);
                    canvas.drawLine(list.getPointB().x,list.getPointB().y,list.getPointA().x,list.getPointB().y,pointer);
                    canvas.drawLine(list.getPointB().x,list.getPointB().y,list.getPointB().x,list.getPointA().y,pointer);

        }
    }
 }







