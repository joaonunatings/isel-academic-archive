package sokoban.model;

import sokoban.LevelLoader;
import util.Direction;

public class Game {

    private Board board;

    public Game(Board board) {
        this.board = board;
    }

    public void loadLevel(String levelPath) {
        board = LevelLoader.load(levelPath);
    }
    @SuppressWarnings("UnusedReturnValue")
    public boolean tryMove(Direction direction) throws WinState {
        boolean playerMoved = board.tryMove(direction);
        if (playerMoved && isWin()) throw new WinState("You win!");
        return playerMoved;
    }

    public boolean isWin() {
        return board.getBoxesOnGoals() == board.getBoxes().size();
    }

    public void printBoard() {
        System.out.println(board);
    }

    public Board getBoard() {
        return board;
    }
    public static class WinState extends Exception {
        public WinState(String message) {
            super(message);
        }
    }
}
