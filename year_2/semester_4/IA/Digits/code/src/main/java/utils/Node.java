package utils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Node {

    private double value = 0;
    private double activationValue = 0;
    private double error;
    private double delta;

    public Node(double value) {
        this.value = value;
    }
}
