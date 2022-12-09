package pt.isel.poo.design;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Scanner;

public class Design {


    public static LinkedList<Figures> list = new LinkedList<>();
    public static LinkedList<Figures> finalList = new LinkedList<>();


    public void reset() {
        finalList.clear();
    }


    public void save(PrintStream output) {

        output.println(finalList.size());
        for (int i = 0 ; i <finalList.size();){

            output.print(finalList.get(i).getFigure());


            switch (finalList.get(i).getFigure()){
                case 'R':
                case 'L':
                    output.printf(" (%d,%d) (%d,%d) %n",finalList.get(i).getPointA().x,finalList.get(i).getPointA().y,finalList.get(i).getPointB().x,finalList.get(i).getPointB().y);


                    break;

                case 'P':
                    output.printf(" (%d,%d) %n", finalList.get(i).getPointB().x,finalList.get(i).getPointB().y);

                    break;
                case 'C':
                    output.printf(" (%d,%d)  |%d| %n",finalList.get(i).getPointA().x,finalList.get(i).getPointA().y,finalList.get(i).getRadius());
                    break;
            }

        i++;
        }

    }

    public static void  createFrom(Scanner input) {

       int numberOfFigures = input.nextInt();
        input.nextLine();

        while((numberOfFigures> 0 )) {

            finalList.add(Figures.createFrom(input));
            numberOfFigures--;


        }
    }

}



