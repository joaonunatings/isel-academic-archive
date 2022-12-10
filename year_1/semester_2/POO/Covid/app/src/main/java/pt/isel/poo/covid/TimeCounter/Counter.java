package pt.isel.poo.covid.TimeCounter;

public class Counter {
    private int value;
    private boolean countEnable;

    public Counter(){
        value = 0;
        countEnable = true;
    }

    public void increment(){

        if(countEnable)value++;
    }

    public void enableCount(){
        countEnable = true;
    }
    public void disableCount(){
        countEnable = false;
    }
    public void reset (){
        value = 0 ;
        enableCount();
    }
    public String secToMin() {
        String sMin = "";
        String sSec = "";

        int minutes = value / 60;
        int seconds = value % 60;

        if(minutes<10) sMin = "0" + minutes;
        else sMin = "" + minutes;
        if(seconds<10) sSec = "0" + seconds;
        else sSec = ""+seconds;
        return sMin + ":" + sSec;
    }
}
