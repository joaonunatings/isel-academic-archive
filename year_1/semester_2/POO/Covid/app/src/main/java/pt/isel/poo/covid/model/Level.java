package pt.isel.poo.covid.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import pt.isel.poo.covid.ElementFactory.ElementFactory;

public  class Level {


    public final int arenaWidth, arenaHeight;
    public int arenaLevel;
    private final LevelElement[][]elements;     //Array that contains the elements.
    ArrayList<Virus> virusList = new ArrayList<Virus>(); //Array that contains the virus present in the current level


    /**
     *  @param levelNumber the number of the level
     * @param height the height of the level
     * @param width the width of the level
     */
    public Level(int levelNumber, int height, int width) {

        arenaLevel = levelNumber;
        arenaWidth = width;
        arenaHeight = height;
        elements = new LevelElement[width][height];
        listeners = new ArrayList<>();

    }



    ArrayList<Location> changedLocations = new ArrayList<Location>();

    private final ArrayList<ChangeListener> listeners;

    public interface ChangeListener {
        void onChanged(List<Location> changedLocations);

    }

    private void fireChangeEvent(List<Location> changedList) {
        for (ChangeListener listener : listeners) {
            listener.onChanged(changedList);
        }

    }

    /**
     * Returns the current level
     * @return the current arena level
     */
    public int getNumber() {
        return arenaLevel;
    }

    /**
     * Resets the bi-dimensional array.
     */

    public void reset() {

        for (int row = 0; row < arenaHeight; row++) {
            for (int col = 0; col < arenaWidth; col++) {
                elements[row][col] = null;
            }
        }
    }

    ElementFactory elementFactory = new ElementFactory();
    /**
     * Places the elements on the bi-dimensional array
     *
     * @param l    the "row" where the element will be placed.
     * @param c    the "column" where the element will be placed.
     * @param type the char representation of the type of board element that will be placed.
     */
    public void put(int l, int c, char type) {
        LevelElement levelElement =  elementFactory.createElement(c,l,type,this);
        elements[c][l] = levelElement;
        if(type == '*')virusList.add((Virus)levelElement);
        if(type == '@')setHero(levelElement);

    }

    private Hero hero;

    public void setHero(LevelElement lE){
        hero = (Hero)lE;
    }
    public Hero getHero(){
        return hero;
    }

    public ArrayList getVirusList (){
        return virusList;
    }
    public int getVirusCount(){
        return virusList.size();
    }

    public LevelElement getElementAt(int c, int l) {
        return elements[c][l];
    }

    public void swap (Location oldLocation,Location newLocation){

        elements[newLocation.x][newLocation.y] = elements[oldLocation.x][oldLocation.y] ;
        elements[oldLocation.x][oldLocation.y] = null;
        changedLocations.add(oldLocation);
        changedLocations.add(newLocation);
        fireChangeEvent(changedLocations);

    }

    public void deleteElement(Location oldLocation){
        elements[oldLocation.x][oldLocation.y] = null;
        changedLocations.add(oldLocation);
        fireChangeEvent(changedLocations);
    }

    public void updateElement(Location location){
        changedLocations.add(location);
        fireChangeEvent(changedLocations);
    }
    public void save(PrintStream output,int savedLevel){

        output.printf("#%d %d x %d %n" ,savedLevel,arenaHeight,arenaWidth );
        for (int l = 0; l < arenaHeight; l++) {

            for (int c = 0 ; c < arenaWidth; c++) {
                if(getElementAt(c,l)!=null) output.print(getElementAt(c,l).getChar());
                else output.print('.');

            }
            output.println();
        }
    }

    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }
}
