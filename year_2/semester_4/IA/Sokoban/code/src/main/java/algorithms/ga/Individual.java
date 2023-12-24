package algorithms.ga;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import sokoban.model.Board;
import sokoban.model.entities.Box;
import util.Direction;
import util.Utils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Individual implements Cloneable {
    private Board board;
    private List<Direction> moves;
    private int currentMoveIdx = 0;
    private int fitness;

    public Individual(Board board) {
        this.board = board;
        this.moves = Utils.listOfRandomDirections(200, 0.5f);
        fitness = calculateFitness();
    }

    /**
     * Move individual one step based on their genetic code and current move index.
     */
    public void move() {
        int localIdx = currentMoveIdx % moves.size();
        Direction direction = moves.get(localIdx);
        Box box = Utils.getTouchingBox(board);
        if (moves.get(localIdx) == null) { // Try to pull box
            if (box != null && box.getHistory().size() > 0) {
                Direction boxDirection = box.getHistory().get(box.getHistory().size() - 1).inverse();
                board.tryPull(boxDirection);
            }
        } else { // Try to move player
            board.tryMove(direction);
        }
        currentMoveIdx++;
        if (currentMoveIdx == moves.size() - 1) currentMoveIdx = 0;
    }

    /**
     * Crossover two individuals.
     * Crosspoint is randomly calculated between 0 and the size of the genetic code.
     * Right part of the genetic code is split between crosspoint and end of genetic code (in both individuals).
     * Right part of the genetic code from first individual is copied to right part of genetic code of the second individual.
     * The vice-versa is done for the second individual.
     * @param other individual
     */
    public void crossover(Individual other) {
        int crossPoint = (int) (Math.random() * (moves.size() - 1));
        // Right part of first individual
        List<Direction> newMoves = new ArrayList<>(moves.subList(crossPoint, moves.size()));

        // Right part of second individual
        List<Direction> otherMoves = new ArrayList<>(other.getMoves().subList(crossPoint, other.getMoves().size()));

        for (int i = crossPoint; i < moves.size(); i++) {
            // Set right part of first individual equal to right part of second individual
            moves.set(i, otherMoves.get(i - crossPoint));
            other.getMoves().set(i, newMoves.get(i - crossPoint));
        }
    }

    /**
     * Mutate individual based on a given probability.
     * Mutation point is calculated based on random number from 0 to last genetic code index.
     * Direction can be random or null (pull box).
     */
    public void mutate() {
        int mutationPoint = (int) (Math.random() * (moves.size() - 1));
        Direction direction = Math.random() < 0.5 ? null : Direction.random();
        moves.set(mutationPoint, direction);
    }

    public void mutate(int times) {
        for (int i = 0; i < times; i++) {
            mutate();
        }
    }

    private int calculateFitness() {
        return board.getBoxes().size() - board.getBoxesOnGoals();
    }

    public boolean isWin() {
        return board.isWin();
    }

    @Override
    @SneakyThrows
    public Individual clone() {
        Individual clone = (Individual) super.clone();
        clone.setBoard(board.clone());
        clone.setMoves(new ArrayList<>(moves));
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Individual)) return false;
        Individual other = (Individual) o;
        return board.equals(other.board);
    }
}