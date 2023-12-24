package algorithms.sa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import sokoban.model.Board;
import sokoban.model.entities.Box;
import sokoban.model.entities.Player;
import util.Direction;
import util.Utils;

/**
 * A State is a representation of a board and the player's position with a given score.
 */
@Getter
@Setter
@NoArgsConstructor
public class State implements Cloneable {
    private Board board;
    private long score;

    public State(Board initialBoard) {
        board = initialBoard;
    }

    /**
     * Generate a new state by moving the player (or pulling a box if possible)
     */
    public void next() {
        Player player = board.getPlayer();

        // Try to pull box (if possible)
        Box box = Utils.getTouchingBox(board);
        if (box != null && (Math.random() <= 0.5) && box.getHistory().size() > 0) {
            Direction direction = box.getHistory().get(box.getHistory().size() - 1).inverse();
            board.tryPull(direction);
        } else {
            Board newBoard = board.clone();
            if (newBoard.tryMove(Direction.random())) {
                board = newBoard;
            }
        }
        score = calculateScore();
    }

    private long calculateScore() {
        return board.getBoxes().size() - board.getBoxesOnGoals();
    }

    public boolean isWinState() {
        return board.isWin();
    }

    @Override
    @SneakyThrows
    public State clone() {
        State clone = (State) super.clone();
        clone.setBoard(board.clone());
        return clone;
    }

    @Override
    public String toString() {
        return "Score = " + getScore() + "\n" + board.toString();
    }
}
