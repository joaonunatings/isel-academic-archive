
depth_first_iterative_deepening( Node, Solution) :-
	path( Node, GoalNode, Solution),
	goal( GoalNode).

%       B is sucessor of A
sucessor(A,B) :-
    direction(DX,DY), 
    can_move(A, DX, DY), 
    play_direction(DX, DY, A, B).

%   goal(Grid)    True if * is within the grid (* means box on top of goal square)
goal([X,Y|Grid]) :-           
    member(L,Grid),
    member(*,L),
    not(aux(Grid)).            % TODO: make this \+ member(.,L) when multiple boxes are present

aux(Grid) :-
    member(L, Grid),
    member(.,L).

path(Node, Node, [Node]). % Single node path

path(FirstNode, LastNode, [LastNode | Path]) :-
	path(FirstNode, OneButLast, Path), % Path up to one-but-last node
	sucessor(OneButLast, LastNode),           % Last step      LastNode is successor of OneButLast
	\+ member(LastNode, Path).         % No cycle


% ---------- EXTRAS -----------

%       play_direction(X,Y,Grid,AfterGrid) - Assuming the player can move!
play_direction(DX,DY,[PX,PY|Grid],[MoveX,MoveY|AfterGrid]) :-
    MoveX is PX + DX,
    MoveY is PY + DY,
    get_element_at(MoveX, MoveY, Grid, Elem),
    (Elem = '$' ->
        move_player_to(PX, PY, MoveX, MoveY, Grid, NGrid),
        NextMoveX is MoveX + DX,
        NextMoveY is MoveY + DY,
        get_element_at(NextMoveX, NextMoveY, NGrid, Elem2),
        (Elem2 = '.' ->
            replace_element_at(NextMoveX, NextMoveY, NGrid, *, AfterGrid)
            ;
            replace_element_at(NextMoveX, NextMoveY, NGrid, $, AfterGrid)
        )
        ;
        move_player_to(PX, PY, MoveX, MoveY, Grid, AfterGrid)
    ).

%       Moves the player to the given coordinates and empties previous position
move_player_to(PX,PY,X,Y,Grid,AfterGrid) :-
    replace_element_at(PX, PY, Grid, -, NGrid1),
    replace_element_at(X, Y, NGrid1, @, AfterGrid).

%       Gets the element at x,y coordinates of the grid
get_element_at(X,Y,Grid,Elem) :-
    nth0(X, Grid, Line),
    nth0(Y, Line, Elem).

%       Replaces the element at x,y coordinates of the grid
replace_element_at(X,Y,Grid,With,AfterGrid) :-
    nth0(X,Grid,Line),
    replace(Line,Y,With,AfterLine),
    replace(Grid,X,AfterLine, AfterGrid).

%       direction(X,Y) [X][Y]
direction(0,+1). % Right
direction(0,-1). % Left
direction(-1,0). % Up
direction(+1,0). % Down

%       can_move(Grid,X,Y)
can_move([PX,PY|Grid],DX,DY) :-        
    MoveX is PX + DX,
    MoveY is PY + DY,
    in_grid(MoveX, MoveY, 4, 2),                % May be useless
    get_element_at(MoveX, MoveY, Grid, Elem),
    (Elem = '#' ->                          % Can move if theres not a wall in front of player
        false
        ;
        (Elem = '$' ->
            NextMoveX is MoveX + DX,
            NextMoveY is MoveY + DY,
            get_element_at(NextMoveX, NextMoveY, Grid, Elem2),
            (Elem2 = '#' ->                 % Can move if theres not a wall in front of player and box
                false
                ;
                true
            )
            ;
            true
        )
    ).


replace([_|T], 0, X, [X|T]).
replace([H|T], I, X, [H|R]) :- I > -1, NI is I-1, replace(T, NI, X, R), !.
replace(L, _, _, L).

% May be removed later
in_grid(X,Y,LimitX,LimitY) :-
    (X > LimitX ; X < 0 ->
        false
        ;
        (Y > LimitY ; Y < 0 ->
            false
            ;
            true
        )
    ).