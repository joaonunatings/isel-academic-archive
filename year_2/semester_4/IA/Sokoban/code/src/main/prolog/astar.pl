:- consult('depthFirst-iterativeDeepening.pl').

%
% An implementation of best-first (A*) search.
%

% bestfirst(Start, Solution): Solution is a path from Start to a goal
bestfirst(Start, Solution) :-
	expand([], l(Start, 0/0), 9999, _, yes, Solution). % Assume 9999 is > any f-value



% s(Node, SuccessorNode, Cost)
s(Node, SuccessorNode, 1) :-     % All arc costs are 1
	sucessor(Node, SuccessorNode).        



% Heuristic estimate h is the distance from the boxes to the goal
% h(Node, H) :- 		. (1,2)  $ (2,1)
/*
h1([X,Y|Grid], H) :-
	get_element_at(EX,EY,Grid,'$'),
	get_element_at(GX,GY,Grid,'.'),
	Math is abs(EX-GX) + abs(EY-GY),
	H is Math.
*/

% check if there is a goal
% if yes; then remove it and check if there is any other (1)
	% if yes; then call h again, add -50 to score (1)
	% if not; check if there is box, add -50 to score (1)
		% if yes; then remove the box and check if there is any other
			% if yes; call h again, add path length to score
			% if not; add path length to score
		% if not; add -50 to score
% if not; check if there is box
	% if yes; remove it and check if theres any other (2)
		% if yes; call h again, add path length to score (2)
		% if not; add path length to score (2)

% *  $  $ scr
h2([X,Y|Grid], H) :-
	% If there is a goal
	(not((get_element_at(Goal1X,Goal1Y,Grid,'*'),!)) -> 
		% no
		% if theres box
		(not((get_element_at(B1X,B1Y,Grid,'$'),!)) ->
			% no
			H is 0
			;
			% yes, calculate
			get_element_at(B1X,B1Y,Grid,'$'), !,
			get_element_at(G1X,G1Y,Grid,'.'), !,
			Math1 is (abs(B1X-G1X) + abs(B1Y-G1Y)),
			% remove and if has another box
			replace_element_at(B1X,B1Y,Grid,'-',AfterGrid2), !,
			(not((get_element_at(B2X,B2Y,AfterGrid2,'$'),!)) -> 
				% no
				H is + Math1
				;
				% yes
				h2([X,Y|AfterGrid2], H1), !,
				H is Math1 + H1
			)
		)
		;
		% yes
		get_element_at(Goal1X,Goal1Y,Grid,'*'), !,
		replace_element_at(Goal1X,Goal1Y,Grid,'-',AfterGrid3), !,
		% remove and if there is other
		(not((get_element_at(Goal2X,Goal2Y,AfterGrid3,'*'),!)) ->
			% no
			% if theres box
			(not((get_element_at(B3X,B3Y,AfterGrid3,'$'),!)) ->
				% no
				H is -50
				;
				% yes, calculate
				get_element_at(B3X,B3Y,AfterGrid3,'$'), !,
				get_element_at(G2X,G2Y,AfterGrid3,'.'), !,
				Math2 is abs(B3X-G2X) + abs(B3Y-G2Y),
				% remove and if has another box
				replace_element_at(B3X,B3Y,AfterGrid3,'-',AfterGrid4), !,
				(not((get_element_at(B4X,B4Y,AfterGrid4,'$'),!)) -> 
					% no
					H is -50 + Math2
					;
					% yes
					h2([X,Y|AfterGrid4], H2), !,
					H is -50 + Math2 + H2
				)
			)	
			;
			% yes
			h2([X,Y|AfterGrid3], H3), !,
			H is -50 + H3
		)
	).
/*
h1([X,Y|Grid], H) :-
	(not((get_element_at(Goal1X,Goal1Y,Grid,'*'),!)) ->
		get_element_at(EX,EY,Grid,'$'), !,
		get_element_at(GX,GY,Grid,'.'), !,
		Math is abs(EX-GX) + abs(EY-GY),
		replace_element_at(EX,EY,Grid,'-',AfterGrid),
		(not((get_element_at(X2,Y2,AfterGrid,'$'),!)) ->
			H is Math
			;
			h1([X,Y|AfterGrid], H2),
			H is Math + H2
		)
		;
		replace_element_at(Goal1X,Goal1Y,Grid,'-',AfterGrid),
		(not((get_element_at(Goal2X,Goal2Y,AfterGrid,'*'),!)) ->
			H is 50
			;
			h1([X,Y|AfterGrid], H2),
			H is 50 + H2
		)
	).

*/
/*
	goal([Empty1 | GoalSquares]),     % Get Goal squares' positions
	totdist(Tiles, GoalSquares, D),   % Total distance from home squares
	seq(Tiles, S),                    % Sequence score
	%% Experiment With/Without seq in H function
	H is D + 3*S.
	%H is D.

totdist([], [], 0).

totdist([Tile | Tiles], [Square | Squares], D) :-
	mandist(Tile, Square, D1),
	totdist(Tiles, Squares, D2),
	D is D1 + D2.

% seq(TilePositions, Score): sequence score of Tiles (except 'Empty')
%
seq([First | OtherTiles], S) :-
	seq([First | OtherTiles], First, S).

seq([Tile1, Tile2 | Tiles], First, S) :-
	score(Tile1, Tile2, S1),
	seq([Tile2 | Tiles], First, S2),
	S is S1 + S2.

seq([Last], First, S) :-
	score(Last, First, S).

score(2/2, _, 1) :- !.       % Tile in centre scores 1

score(1/3, 2/3, 0) :- !.     % Proper successor scores 0
score(2/3, 3/3, 0) :- !.
score(3/3, 3/2, 0) :- !.
score(3/2, 3/1, 0) :- !.
score(3/1, 2/1, 0) :- !.
score(2/1, 1/1, 0) :- !.
score(1/1, 1/2, 0) :- !.
score(1/2, 1/3, 0) :- !.

find_element(Elem, Node, X, Y) :-
*/



% expand( Path, Tree, Bound, Treel, Solved, Solution):
% Path is path between start node of search and subtree Tree,
% Tree1 is Tree expanded within Bound,
% if goal found then Solution is solution path and Solved = yes

% Case 1: goal leaf-node, construct a solution path
%
expand(P, l( N, _), _, _, yes, [N | P]) :-
	goal(N).

% Case 2: leaf-node, f-value less than Bound
% Generate successors and expand them within Bound
%
expand(P, l(N, F/G), Bound, Tree1, Solved, Sol) :-
	F =< Bound,
	( bagof( M/C, ( s(N, M, C), \+ member(M, P)), Succ),
	!,                      % Node N has successors
	succlist( G, Succ, Ts), % Make subtrees Ts
	bestf( Ts, F1),         % f-value of best successor
	expand( P, t(N, F1/G, Ts), Bound, Tree1, Solved, Sol)
	;
	Solved = never           % N has no successors - dead end
  ).

% Case 3: non-leaf, f-value less than Bound
% Expand the most promising subtree; depending on
% results, procedure continue will decide how to proceed
%
expand( P, t(N, F/G, [T | Ts]), Bound, Tree1, Solved, Sol) :-
	F =< Bound,
	bestf(Ts, BF), Bound1 = min( Bound, BF), % min( Bound, BF, Bound1) does not work in SWI-Prolog
	expand( [N | P], T, Bound1, T1, Solved1, Sol),
	continue( P, t(N, F/G, [T1 | Ts]), Bound, Tree1, Solved1, Solved, Sol).


% Case 4: non-leaf with empty subtrees
% This is a dead end which will never be solved
%
expand( _, t(_, _, []), _, _, never, _) :- !.

% Case 5: value greater than Bound
% Tree may not grow
%
expand( _, Tree, Bound, Tree, no, _) :-
	f( Tree, F), F > Bound.


% continue( Path, Tree, Bound, NewTree, SubtreeSolved, TreeSolved, Solution)
%
continue( _,_,_,_, yes, yes, Sol).

continue( P, t(N, F/G, [T1 | Ts]), Bound, Tree1, no, Solved, Sol) :-
	insert( T1, Ts, NTs),
	bestf( NTs, F1),
	expand( P, t(N, F1/G, NTs), Bound, Tree1, Solved, Sol).

continue( P, t(N, F/G, [_ | Ts]), Bound, Tree1, never, Solved, Sol) :-
	bestf( Ts, F1),
	expand( P, t(N, F1/G, Ts), Bound, Tree1, Solved, Sol).


% succlist( GO, [Node1/Cost1, ...], [l(BestNode, BestF/G), ...]):
%   make list of search leaves ordered by their f-values
%
succlist( _, [], []).

succlist( GO, [N/C | NCs], Ts) :-
	G is GO + C,
	h2( N, H),     % Heuristic term h(N)
	F is G + H,
	succlist( GO, NCs, Ts1),
	insert( l(N, F/G), Ts1, Ts).


% Insert T into list of trees Ts preserving order with respect to f-values
%
insert( T, Ts, [T | Ts]) :-
	f( T, F), bestf( Ts, F1),
	F =< F1, !.

insert( T, [T1 | Ts], [T1 | Ts1]) :-
	insert( T, Ts, Ts1).

% Extract f-value
%
f( l(_, F/_), F).        % f-value of a leaf

f( t(_, F/_, _), F).     % f-value of a tree

bestf( [T | _], F) :-    % Best f-value of a list of trees
	f( T, F).

bestf( [], 9999).         % No trees: bad f-value