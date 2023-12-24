# ia-g06-project2

**Grupo 06: Alexandre Luís (47222) e João Nunes (47220)**

Please locate _Java_ code in _src/main/java_.

Please locate _Prolog_ code in _src/main/prolog_

Please locate _tests_ folder code in _src/test_

## Iterative-deepening
- Consult file _depthFirst-iterativeDeepening.pl_
- Call `depth_first_iterative_deepening(Node, Solution)`.

_Node - [X,Y|[Board]] - X,Y - Player Coords (0 indexed)_

_board - [#,#,#,#,#],[#,@,$,.,#],[#,#,#,#,#]_

**Example:** `depth_first_iterative_deepening( [1,1|[#,#,#,#,#],[#,@,$,.,#],[#,#,#,#,#]], Solution)`

## A*
- Call `bestfirst(Start, Solution)`

_Start -> [X,Y|[Board]]_

**Example:** `bestfirst( [1,1|[#,#,#,#,#],[#,@,$,.,#],[#,#,#,#,#]], Solution)`

## Simulated annealing
To run this algorithm go into `SimulatedAnnealingTest` and see test examples.
Set different pararameters with `SimulatedAnnealingTest.builder`.

## Genetic algorithm
To run this algorithm go into `GeneticAlgorithmTest` and see test examples.
Set different pararameters with `GeneticAlgorithm.builder`.
