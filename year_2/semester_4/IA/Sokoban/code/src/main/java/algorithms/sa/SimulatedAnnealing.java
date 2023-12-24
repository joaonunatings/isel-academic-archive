package algorithms.sa;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sokoban.model.Board;

@Builder(builderMethodName = "simulatedAnnealingBuilder")
@Getter
@Setter
public class SimulatedAnnealing {
    @Builder.Default private double maxTemperature = 1;
    @Builder.Default private double minTemperature = 0.0000001;
    @Builder.Default private double temperature = 1;
    @Builder.Default private double coolingRate = 0.001;
    @Builder.Default private long maxIterations = 5;

    private final Board initialBoard;
    private final State initialState;

    /**
     * Run the simulated annealing algorithm.
     * Stop running if any state is a win state (board is solved) or if the temperature drops below the minTemperature.
     * @return The best state found.
     */
    public State execute() {
        State currentState = initialState.clone();
        State lastState = initialState.clone();
        State bestState = initialState.clone();


        int rateIncrement = 0;
        while (!currentState.isWinState()) {
            long currentIteration = 0;
            while (currentIteration < maxIterations && !currentState.isWinState()) {
                if (lastState.getBoard().getPlayerMoves() != currentState.getBoard().getPlayerMoves()) {
                    lastState = currentState.clone();
                }
                currentState.next();
                if (currentState.getScore() < bestState.getScore()) {
                    bestState = currentState.clone();
                } else {
                    final long loss = Math.abs(bestState.getScore() - currentState.getScore());
                    final double probability = Math.exp((-loss / temperature));
                    if (Math.random() <= probability) bestState = currentState.clone();
                    else currentState = lastState;
                }
                currentIteration++;
            }
            if (!currentState.isWinState()) {
                rateIncrement++;
                temperature = maxTemperature * Math.exp(-coolingRate * rateIncrement);
                if (temperature < minTemperature) {
                    break;
                }
            }
        }

        return bestState;
    }

    public static SimulatedAnnealingBuilder builder(Board initialBoard) {
        return simulatedAnnealingBuilder().initialBoard(initialBoard);
    }

    public static class SimulatedAnnealingBuilder {
        public SimulatedAnnealingBuilder initialBoard(Board initialBoard) {
            this.initialBoard = initialBoard;
            this.initialState = new State(initialBoard);
            return this;
        }
    }
}
