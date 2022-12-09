package edu.isel.lic.game;

public class Scores {
    public static final int PLACEMENTS = 10 ;
    private static String[] playerName = new String[PLACEMENTS];
    private static int[] playerScore = new int[PLACEMENTS];

    public static int getPlayerScore(int pos){
        return playerScore[pos - 1];
    }

    public static String getPlayerName (int pos){
        if (playerName[pos-1]==null){
            return "";
        }else {
            return playerName[pos - 1];
        }
    }

    public static void loadScores (){

        String[] scoresArray = FileAccess.readFile(PLACEMENTS,"ScoreBoard");
        for( int i = 0 ; i<10 ; ++i ){
            String score = "";
            String name = "" ;
            int t = 0 ;

            if(scoresArray[i].length()==0){ return; }

            while(scoresArray[i].charAt(t) != ';'){
                score += scoresArray[i].charAt(t++);
            }

            while(++t < scoresArray[i].length()){
                name += scoresArray[i].charAt(t) ;
            }

            playerScore[i] =Integer.parseInt(score) ;
            playerName[i] = name;

        }
    }

    public static void archiveScores(){
        String[] scores = new String[PLACEMENTS];
        for (int i = 0 ; i < PLACEMENTS ; ++i){
            if(playerName[i]==null){
                scores[i]="";
            }else {
                scores[i] = "" + playerScore[i] + ";" + playerName[i];
            }
        }
        FileAccess.writeFileText(scores,"ScoreBoard");
    }
    public static void saveScore(int score , String name ){
        int position = findNewPlayerPosition(score);
        if(position<PLACEMENTS) {
            for (int i = PLACEMENTS - 1; i > position; --i) {
                playerScore[i] = playerScore[i - 1];
                playerName[i] = playerName[i - 1];
            }
            playerScore[position]=score;
            playerName[position]=name;
        }
    }

    private static int findNewPlayerPosition(int score){
        int position=PLACEMENTS;
        for(int i = PLACEMENTS-1 ; score>playerScore[i] ; --i){
            --position ;
            if(i==0) break; // idkw but i cant make it work without this TODO: repair this.
        }
        return position;

    }

    public static int getSize ()
    {
        int size = 0;

        for (int value : playerScore)
        {
            if (value != 0)
                size++;
        }

        return size;
    }
}
