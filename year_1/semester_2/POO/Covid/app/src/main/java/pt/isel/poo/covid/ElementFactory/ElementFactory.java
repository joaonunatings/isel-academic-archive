package pt.isel.poo.covid.ElementFactory;

import pt.isel.poo.covid.model.Hero;
import pt.isel.poo.covid.model.Level;
import pt.isel.poo.covid.model.LevelElement;
import pt.isel.poo.covid.model.Location;
import pt.isel.poo.covid.model.TrashCan;
import pt.isel.poo.covid.model.Virus;
import pt.isel.poo.covid.model.Wall;

public class ElementFactory {

    public LevelElement createElement(int c, int l, char type, Level level){
        switch (type) {

            case '@':
                return new Hero(new Location(c,l),level);

            case 'X':
                return new Wall(new Location(c,l));

            case '*':
                return new Virus(new Location(c,l),level);

            case 'V':
                return new TrashCan(new Location(c,l));

            default:
                return null;

        }
    }
}
