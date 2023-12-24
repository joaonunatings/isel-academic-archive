package utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Edge {

        private final Node leftNode;
        private final Node rightNode;
        private double weight;
}
