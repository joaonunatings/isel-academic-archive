# Connect 4 - A two-person game

## Rules
This is a two-person game where players take turns inserting colored discs (each player chooses one color) into a suspended rack. 
Once dropped into the top of each column, the pieces slot into the lowest spaces in each column. 
By inserting discs, players aim to form a row of 4 colored discs all in a row. For instance, if a player is playing with red discs and manages to get 4 red-colored discs in a horizontal, vertical, or diagonal row, the game is won.
The rules of the game can be consulted [here](https://www.gamesver.com/the-rules-of-connect-4-according-to-m-bradley-hasbro/).

## Usage

### Compile
To compile and run this program open _SWI-Prolog_ and run: `[connect4].`

### Play
- For player _versus_ player use: `playerplayer.`
- For player _versus_ computer use: `playercomputer.`

## Insight

### Modes:
There are two modes available to play:
- Player _versus_ player
- Player _versus_ computer

To select between modes see [usage](#usage).

### AI Algorithm
Our AI uses the [Alpha-beta pruning algorithm](https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning) (implementation of the [minimax principle](https://pt.wikipedia.org/wiki/Minimax)) in order to make the best possible play given that the player plays it's best possible play.
_explain how this algorithm is used in this program_ 

### Interface
Our interface shows stuff like: points and other info...

- Reads user input from standard input.
- Prints out info for the use via standard output.

### Constraint Logic Programming

In Prolog, a variable can be either bound (have a value, possibly another variable) or free (have no value). Constraint logic programming (CLP) extends the notion of a logical variable by allowing variables to have a domain rather than a specific value. Variables can also be constrained, which means that their value must abide by certain rules specified by the programmer. Constraint logic programming makes it possible to solve complex combinatorial problems with a minimum amount of code.

## Program logic

### Phase 1

Initially, we started by developing a menu interface that would allow us to choose two modes: player vs player or player vs machine.

Next, we created a list composed of a list of "-" that would represent our empty board.

When we finished creating our table we would have to find a way to insert pieces into the board. The way we found to do it was by having the help of the rule `nth0` wich you can see [here](https://www.swi-prolog.org/pldoc/man?predicate=nth0/3), `append` can see [here](https://www.swi-prolog.org/pldoc/man?predicate=append/3). We also use a `replace` predicate so if the head of the list is the same as the element to replace, replace it, otherwise, keep it as it is. In both cases, process recursively the rest of the list. Additionally we add a configuration wich the user could choose height and width he wanted. With that the user could play Player vs Player in any size, and Player vs Machine in 2x2, 3x3, 4x4. To change between connect2, connect3 and connect4 modes for Player vs Player it is necessary to change within the hasWinCondition predicate, in the fill_line() predicate call the first parameter is the parameter that says how many pieces in a row to check.

### Phase 2

For this phase our focus was for the player vs player mode, it would be easier because we could see what was happening.

Then we start thinking into the basic rules of our game.
* Add next move player
* What would happen if a column is full and that column is played by a player?
* See if there are any 4 sequencial and equal elements in a list
* Add game possible winner diagonals in a List

### Phase 3

In phase 3 we start developing our Player vs Machine with the Minimax algorithm.
* Mini-max algorithm is a recursive or backtracking algorithm which is used in decision-making and game theory. It provides an optimal move for the player assuming that opponent is also playing optimally.
* In this algorithm two players play the game, one is called MAX and other is called MIN.
* Both the players fight it as the opponent player gets the minimum benefit while they get the maximum benefit.
* Both Players of the game are opponent of each other, where MAX will select the maximized value and MIN will select the minimized value.
* The minimax algorithm performs a depth-first search algorithm for the exploration of the complete game tree.
* The minimax algorithm proceeds all the way down to the terminal node of the tree, then backtrack the tree as the recursion.

In terms of code what was used to make the methods that the minimax and alphabeta algorithm use is `moves`, `min_to_move`, `max_to_move`, and `staticval`

## Conclusion

For this project we needed to make sure we could understand and master the syntax of Prolog programs, basic programming constructs such as atoms, numbers, compound terms, lists, operators and arithmetic in Prolog. Controlling backtracking procedures, was a key factor with that we could search the truth value of different predicates by checking whether they are correct or not. With this Two-person game we could understand the rules of what's behind a game such as connect4. When implementing the Player vs Machine the minimax and the alphabet algorithm are excellent decision rule for artificial intelligence programs.