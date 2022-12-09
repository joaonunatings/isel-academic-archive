package pt.isel.poo.design;
import android.graphics.Point;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;

public class Figures {
    private Point pointA, pointB;
    private int radius;
    private char figure;

    public void setFigure(char f) {
        figure = f;
    }

    public  char getFigure() {
        return figure;
    }

    public  int getRadius() {
        return radius;
    }

    public void setPointA(Point pA) {
        pointA = pA;
    }

    public void setPointB(Point pB) {
        pointB = pB;
    }



    public  Point getPointA() {
        return pointA;
    }

    public  Point getPointB() { return pointB; }


    public void setRadius (Point pointA,Point pointB){
        double HorizontalDistance = (pointB.x - pointA.x);
        double VerticalDistance = pointB.y - pointA.y;
        radius = (int)(Math.sqrt(Math.pow(VerticalDistance,2) + Math.pow(HorizontalDistance,2)));
    }

    public void setIntRadius (int valueRadius){
       radius = valueRadius;
    }

    public static Figures

    createFrom(Scanner input) {

        Figures newFigure = new Figures();
        String line = input.nextLine();
        char figureType = line.charAt(0);
        newFigure.setFigure(figureType);
        line = line.replaceAll("[^0-9]+", " ");
        Arrays.asList(line.trim().split(" ")).get(0);
        String [] Array = Arrays.asList(line.trim().split(" ")).toArray(new String[0]);

        switch (figureType){
            case 'R':
            case 'L':
                newFigure.setPointA(new Point(Integer.parseInt(Array[0]),Integer.parseInt(Array[1])));
                newFigure.setPointB(new Point(Integer.parseInt(Array[2]),Integer.parseInt(Array[3])));

                return newFigure;

            case 'C':
                newFigure.setPointA(new Point(Integer.parseInt(Array[0]),Integer.parseInt(Array[1])));
                newFigure.setIntRadius(Integer.parseInt(Array[2]));
                return newFigure;

            case 'P':
                newFigure.setPointB(new Point(Integer.parseInt(Array[0]),Integer.parseInt(Array[1])));
                return newFigure;
        }
        return null;
    }

    private final ArrayList<Figures> list = new ArrayList<>();

}




