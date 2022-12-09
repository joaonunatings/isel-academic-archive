package edu.isel.lic.game;

public class Statistics {
    private static int total_games = 0, total_coins = 0;
    private static final int total_games_line = 0, total_coins_line = 1;
    private static final int numOfStats = 2;

    public static void loadStats(){
        String[] stats = FileAccess.readFile(numOfStats,"statistics");

        if (stats[total_games_line].length() > 0)
            total_games = Integer.parseInt(stats[total_games_line]);

        if (stats[total_coins_line].length() > 0)
            total_coins = Integer.parseInt(stats[total_coins_line]);
    }

    public static void saveStats(int games, int coins){
        total_games=games;
        total_coins=coins;
        String[] stats = new String[numOfStats];
        stats[0]=""+total_games;
        stats[1]=""+total_coins;
        FileAccess.writeFileText(stats , "statistics");

    }

    public static int totalGames(){
        return total_games;
    }

    public static int totalCoins(){
        return total_coins;
    }
}
