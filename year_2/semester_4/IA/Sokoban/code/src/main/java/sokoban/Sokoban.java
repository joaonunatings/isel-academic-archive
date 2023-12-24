package sokoban;

import sokoban.model.Board;
import sokoban.model.Game;
import util.Direction;
import util.Utils;

import java.util.Scanner;

public class Sokoban {

    private static final Scanner in = new Scanner(System.in);
    private static final String BASE_LEVELS_PATH = "src/main/resources/levels/user/";

    public static void main(String[] args) {
        System.out.println("Welcome to sokoban.Sokoban!");

        System.out.println("Select level: ");
        String level = in.nextLine();
        Board board = LevelLoader.load(BASE_LEVELS_PATH + level);
        Game game = new Game(board);

        while(true) {
            Utils.clearScreen();
            game.printBoard();
            System.out.println("0. Up");
            System.out.println("1. Down");
            System.out.println("2. Left");
            System.out.println("3. Right");
            System.out.println("4. Exit");
            int option = in.nextInt();
            try {
                switch(option) {
                    case 0:
                        game.tryMove(Direction.UP);
                        break;
                    case 1:
                        game.tryMove(Direction.DOWN);
                        break;
                    case 2:
                        game.tryMove(Direction.LEFT);
                        break;
                    case 3:
                        game.tryMove(Direction.RIGHT);
                        break;
                    case 4:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (Game.WinState winState) {
                System.out.println(winState.getMessage());
                System.exit(0);
            }
        }
    }
}
