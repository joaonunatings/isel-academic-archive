package edu.isel.lic.game;

import edu.isel.lic.link.COIN;
import edu.isel.lic.link.HAL;
import edu.isel.lic.link.M;
import edu.isel.lic.link.SerialEmitter;
import edu.isel.lic.link.sound.SGCode;
import edu.isel.lic.link.sound.SoundGenerator;
import edu.isel.lic.peripherals.KBD;
import edu.isel.lic.peripherals.lcd.LCD;
import edu.isel.lic.peripherals.lcd.LCDCode;
import isel.leic.utils.Time;

public class APP
{
    public static final String INVADERS = "0123456789";                             //Invasores a utilizar.
    public static int current_coins, total_coins;                                   //current_coins: moedas que o utilizador introduziu; total_coins: moedas que a máquina tem no total
    public static int current_score = 0, total_games;                               //current_score: pontuação do jogador; gameCounter = jogos realizados na máquina no total
    public static boolean running = true, saveStats = true;       //jogo a correr

    /**
     * Main do edu.isel.lic.game.APP
     * Inicializa a classe, entra num ciclo controlado pelo booleano running
     * Tem 3 fases: menu, game e score;
     * No fim de cada jogo incrementa ao número de jogos realizados: gameCounter
     * @param args - Sem args iniciais
     */
    public static void main (String[] args) {
        init();
        while (running) {
            menu();
            game();
            if (saveStats)
                score();
        }
    }

    /**
     * Inicializa esta classe
     */
    private static void init()
    {
        HAL.init();
        SerialEmitter.init();
        KBD.init();
        LCD.init();
        SoundGenerator.init();
        COIN.init();
        TUI.init();
        Scores.loadScores();
        Statistics.loadStats();
        total_coins = Statistics.totalCoins();
        total_games = Statistics.totalGames();
    }

    private static void menu() {
        char key;
        boolean hasCoin = (current_coins > 0), newGame = false, showNewScore = true, showMenu = true;
        long view_score_timeout = 3000, starting_time = Time.getTimeInMillis();
        int score_tracker = 1;

        do {
            key = KBD.getKey();

            if (showMenu) {
                TUI.clearAll();
                TUI.create_formatText(0, TUI.Format.ALIGN_CENTER, "Space Invaders");
                showMenu = false;
            }

            // Mostrar pontuações
            long current_time = Time.getTimeInMillis();
            if (showNewScore || key != KBD.NONE) {

                TUI.clearLine(1);
                if (Scores.getSize() > 0 && !Scores.getPlayerName(score_tracker).equals("")) {
                    TUI.create_formatText(1, TUI.Format.ALIGN_LEFT, "" + score_tracker + ":");
                    TUI.create_formatText(1, TUI.Format.ALIGN_CENTER, "" + Scores.getPlayerName(score_tracker) + "-" + Scores.getPlayerScore(score_tracker));
                } else {
                    TUI.create_formatText(1, TUI.Format.ALIGN_CENTER, "No games played.");
                }

                score_tracker++;
                if (score_tracker > Scores.getSize())
                    score_tracker = 1;

                starting_time = Time.getTimeInMillis();
            }
            showNewScore = (current_time - starting_time >= view_score_timeout);

            // Contabilizar moedas
            if (COIN.buttonPress()) {
                ++current_coins;
                TUI.clearLine(1);
                TUI.print(1, 0, " Game ");
                TUI.create_formatText(1, TUI.Format.ALIGN_RIGHT, "$" + current_coins);
                TUI.print(1, 6, LCD.spaceship);
                TUI.print(1, 9, LCD.invader);
                TUI.print(1, 11, LCD.invader);
                hasCoin = true;
            }

            // Verificar condição de início de jogo
            if (hasCoin && key == '*') {
                newGame = true;
                current_coins--;
                total_coins++;
            }

            // Menu de manutenção
            boolean showMaintenanceMenu = true;
            while (M.checkButton() && showNewScore) {
                key = KBD.getKey();

                if (showMaintenanceMenu) {
                    TUI.menu(TUI.Format.ALIGN_CENTER, "On Maintenance", "*-Count", "#-shutD");
                    showMaintenanceMenu = false;
                }

                if (key == '*') {
                    TUI.clearAll();
                    TUI.print(0, 0, "Games:" + Statistics.totalGames());
                    TUI.print(1, 0, "Coins:" + Statistics.totalCoins());
                    key = KBD.waitKey(5000);
                    if (key == '#') {
                        TUI.menu(TUI.Format.ALIGN_CENTER, "Clear counters", "5-Yes", "other-No");
                        key = KBD.waitKey(5000);
                        if (key == '5') {
                            total_coins = 0;
                            total_games = 0;
                            Statistics.saveStats (total_games, total_coins);
                        }
                        showMaintenanceMenu = true;
                    }
                }
                else if (key == '#') {
                    TUI.menu(TUI.Format.ALIGN_CENTER, "Shutdown?", "5-Yes", "other-No");
                    key = KBD.waitKey(5000);
                    if (key == '5') {
                        LCD.writeCMD(LCDCode.display_control(false,true,true));
                        Scores.archiveScores();
                        Statistics.saveStats (total_games, total_coins);
                        System.exit(0);
                    }
                    showMaintenanceMenu = true;
                }
                else if (key != KBD.NONE) {
                    newGame = true;
                    saveStats = false;
                }
                showMenu = true;
            }
        } while (!hasCoin || !newGame);
    }

    public static void game ()
    {
        char key, number_key = KBD.NONE;
        int invader_tracker = 0, max_invaders = 14;
        long spawnInvader_timeout = 1500, starting_time = Time.getTimeInMillis();
        boolean spawnInvader = true, invader_killed, gameOver = false;
        double time_multiplier = 0.005;

        current_score = 0;
        TUI.clearAll();
        TUI.print (0, 0, "]");
        TUI.print(0, 1, LCD.spaceship);
        TUI.print (1, 0, "Score:" + current_score);

        do {
            key = KBD.getKey();

            // Faz nascer um invader se passou o tempo da condição
            long current_time = Time.getTimeInMillis();
            if (spawnInvader) {
                spawnInvader();
                invader_tracker++;
                starting_time = Time.getTimeInMillis();
            }
            spawnInvader = (current_time - starting_time >= spawnInvader_timeout);

            // Atualiza o número alvo à esquerda da nave
            if (key != KBD.NONE && key != '*' && key != '#') {
                number_key = key;
                TUI.print(0, 0, "" + number_key);
            }

            // Verifica se invader foi morto ou não e atualiza o score
            if (key == '*' && number_key != KBD.NONE && invader_tracker > 0) {
                invader_killed = killInvader(number_key);
                number_key = KBD.NONE;
                if (invader_killed) {
                    invader_tracker--;
                    updateScore();
                    if (spawnInvader_timeout > 200)
                        spawnInvader_timeout -= spawnInvader_timeout * time_multiplier;
                }
            }

            // Condição de fim de jogo
            if (invader_tracker > max_invaders)
                gameOver = true;

        } while (!gameOver);

        TUI.clearLine (0);
        TUI.create_formatText (0, TUI.Format.ALIGN_CENTER, "Game Over");
        SoundGenerator.setVolume(SGCode.Volume.MED);
        SoundGenerator.play(SGCode.Sound.GAME_OVER);
        KBD.waitKey(3000);
        SoundGenerator.stop();
    }

    /**
     * Método para fazer nascer um invasor
     * new_invader - caráter do novo invasor (é gerado um número de 0 a 9 para selecionar na string INVADERS a sua posição correspondente)
     */
    private static void spawnInvader () {
        TUI.formatText (0, 2, TUI.COLS - 1, TUI.Format.SHIFT_LEFT);
        TUI.print(0, 1, LCD.spaceship);
        char new_invader = INVADERS.charAt((int)(Math.random()*10));
        TUI.create_formatText(0, TUI.Format.ALIGN_RIGHT, "" + new_invader);
    }

    /**
     * Método para verificar se é ou não morto o invasor
     * invader_killed - true: invasor foi morto; false: invasor não foi morto
     * invaders - é lido os invaders que estão no ecrã, elimina-se os espaços (edu.isel.lic.peripherals.KBD.NONE) e guarda-se o caráter da posição 0
     * lcd_col - coluna em que o invasor mais perto da nave se encontra
     * invader - carater que representa o invasor mais perto da nave
     * @param key - number_key enviado do método game(), isto é, a tecla com um número
     * @return - Retorna true se o invasor foi morto e false se não foi morto
     */
    private static boolean killInvader (char key)
    {
        boolean invader_killed = false;

        String invaders = TUI.readArray (0,  2, TUI.COLS - 1);
        invaders = invaders.replaceAll("\\s", "");
        int target_invader_col = TUI.COLS - invaders.length();
        char invader = invaders.charAt(0);
        if (key == invader) {
            TUI.clear (0, target_invader_col);
            invader_killed = true;
        }

        return invader_killed;
    }

    /**
     * Método que atualiza a pontuação nas variáveis globais e no lcd
     */
    private static void updateScore() {
        current_score++;
        TUI.clear (1, 6, TUI.COLS - 1);
        TUI.print (1, 6, "" + current_score);
    }

    /**
     * Inicializa a função de guardar a pontuação do jogador com o seu nome correspondente
     * key - tecla pressionada
     * current_letter - tecla onde o cursor se encontra
     * current_col - (nova) coluna onde o cursor se encontra
     * Este método conta com uma adição: o ciclo das letras dá volta, isto é, quando se encontra em A posso ir para Z e vice-versa
     */
    private static void score ()
    {
        char key = KBD.NONE;
        char current_letter = 'A';
        int current_col = 5;

        TUI.clearLine (0);
        TUI.print (0, 0, "Name:" + current_letter);
        TUI.cursorControl(true,true);
        TUI.moveCursor(0, 5);
        while (key != '5')
        {
            key = KBD.waitKey(100);

            switch (key)
            {
                case '4':
                    if (current_col > 5)
                        current_col--;
                    else
                        current_col = 5;

                    TUI.moveCursor(0, current_col);
                    break;
                case '6':
                    if (current_col < TUI.COLS - 1)
                        current_col++;
                    else
                        current_col = TUI.COLS - 1;
                    current_letter = TUI.readArray (0, current_col);
                    if (current_letter < 'A' || current_letter > 'Z')
                        TUI.print(0, current_col, "" + 'A');
                    TUI.moveCursor(0, current_col);
                    break;
                case '2':
                    current_letter = TUI.readArray (0, current_col);
                    if (current_letter == 'Z')
                        current_letter = 'A';
                    else
                        current_letter++;
                    TUI.print (0, current_col, "" + current_letter);
                    TUI.moveCursor(0 , current_col);
                    break;
                case '8':
                    current_letter = TUI.readArray (0, current_col);
                    if (current_letter == 'A')
                        current_letter = 'Z';
                    else
                        current_letter--;
                    TUI.print (0, current_col, "" + current_letter);
                    TUI.moveCursor(0, current_col);
                    break;
                case '*':
                    if (current_col > 5)
                    {
                        TUI.clear (0, current_col);
                        current_col--;
                    }
                    else
                        current_col = 5;

                    TUI.moveCursor(0, current_col);
                    break;
            }
        }
        total_games++;
        String player_name = TUI.readArray (0, 5, TUI.COLS - 1);
        player_name = player_name.replaceAll("\\s", "");
        Scores.saveScore (current_score, player_name);
        Statistics.saveStats(total_games, total_coins);
        TUI.cursorControl(false,false);
    }
}
