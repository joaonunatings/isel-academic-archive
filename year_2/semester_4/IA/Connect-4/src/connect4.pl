% Connect-4 game

:- initialization main.

imports :-
	[prints],
	[utils],
	[algorithms].

debug(1).

mode(0).
mode(1).

algorithm(0).
algorithm(1).

main :- 
	imports,
	print_debug("Debug mode ON"),
	print_welcome,
	repeat,
	game_play,
	print_end.

game_play :-
	set_game_mode(Mode),
	(Mode = 1 -> set_ai_algorithm(Algorithm); true),
	set_board_size(Board),
	set_starting_player(Mode, StartingPlayer),
	(Mode = 0 -> player_move(Board, StartingPlayer);
	 	(StartingPlayer = 1 -> machine_move(Board, Algorithm); 
		 	nextPlayerMove(Board, Algorithm))).

player_move(Board, Player) :-
	display(Board),
	print_player_play(Player),
	read(Move), nl,
	break(Move),
	player(Player, Piece),
	playPiece(Move, Board, Piece, NewBoard, Ctr, Line, 'PvP'),
	hasWinCondition(Move, Line, NewBoard, Player),
	(Player = 1 -> NewPlayer = 2; NewPlayer = 1),
	player_move(NewBoard, NewPlayer).

machine_move(Board, Algorithm) :-
	nl, display(Board),
	player(1, Piece), 
	writeln("AI playing"),
	(Algorithm = 0 -> 
		minimax([2,Board],[_|[NBoard|[]]],_); 
		alphabeta([2, Board], -1.0Inf, +1.0Inf, [_|[NBoard|[]]],_)
	),
	(hasTerminalPlay(NBoard, Piece) ->
		nl, writeln('Machine won, too bad :P'),
		nl, display(NBoard),
		nl, writeln('Press Any key to exit.'),
		read(Key),
		halt
		;
		nextPlayerMove(NBoard, Algorithm)
	;
	nextPlayerMove(Board, Algorithm)).

nextPlayerMove(Board, Algorithm) :-
	nl, display(Board),
	player(2, Piece),
	print_player_play(2),
	read(Move), nl,
	break(Move),
	playPiece(Move, Board, Piece, NewBoard, Ctr, Line, 'PvP'),
	(hasTerminalPlay(NewBoard, Piece), ! ->
		nl, writeln('Player won, Congrats!'),
		nl, display(NewBoard),
		nl, writeln('Press Any key to exit.'),
		read(Key),
		halt
		;
		machine_move(NewBoard, Algorithm)
	).

set_ai_algorithm(Algorithm) :-
	print_ai_algorithm,
	read(Algorithm),
	algorithm(Algorithm).

set_game_mode(Mode) :-
	print_game_mode,
	read(Mode),
	mode(Mode).

set_board_size(Board) :-
	writeln('Insert height of board: '),
	read(Height),
	Height > 0,
	writeln('Insert width of board: '),
	read(Width),
	Width > 0,
	empty_board(Height, Width, Board).

set_starting_player(Mode, StartingPlayer) :-
	(Mode = 0 -> print_starting_player;
	print_starting_player_machine),
	read(StartingPlayer),
	player(StartingPlayer, _).

/*
	Put a piece in the board
*/
playPiece(Col, [H|[]], Piece, RetBoard, Ctr, Line, Mode) :-
	(nth0(Col, H, -) -> 
		replace(H, Col, Piece, NewH),
		replace([H], 0, NewH, NewBoard),
		append(RecBoard, NewBoard, RetBoard),
		Line is 0,
		Ctr = 0
		;
		append(RecBoard, [H], RetBoard), !,
		Line is -1,
		Ctr = 0
	).

playPiece(Col, [H|T], Piece, RetBoard, Ctr, Line, Mode) :-
	playPiece(Col, T, Piece, RBoard, Ct, L, Mode),
	(L = -1 ->
		(nth0(Col, H, -) -> 
			replace(H, Col, Piece, NewH),
			replace([H|T], 0, NewH, RetBoard),
			Line is Ct + 1,
			Ctr is Ct + 1
			;
			append(RBoard, [H], RetBoard), !,
			Line is -1,
			Ctr is Ct + 1,
			retryPlay([H|T],Piece,Ctr,Mode)
		)
		;
		append([H], RBoard, RetBoard), !,
		Line = L,
		Ctr is Ct + 1
	).

% Ask the same player to play again or outputs false depending on the game mode
retryPlay([H|T], Piece, Check, Mode) :-
	length(H, L),
	L2 is L - 1, 
	(Check = L2 ->
		(Mode = 'PvP' ->
			writeln('Column full, Choose another one!'),
			(Piece = x ->
				player_move(Board, 1)
				;
				player_move(Board, 2)
			)
			;
			false
		)
		;
		true
	).

hasWinCondition(Col, Line, Board, Player) :-
	player(Player, Piece), 		 						% Get player Piece
	fill_line(3, Piece, CheckList),						% Fill CheckList with player Piece
	(debug(1) -> write('CheckList: '), writeln(CheckList) ; true),
	toList(Col, Board, List),							% transform the played column into a list
	(debug(1) -> write('Column List: '), writeln(List) ; true),
	(hasNInLine(CheckList, List) ->						% Check if column list as a winning play
		displayPlayerWon(Player, Board)
		;												% else
		length(Board, Len),								
		L is Len - 1 - Line,							% reverse Line idx
		nth0(L, Board, HorizontalList),     			% Get the played line list
		(debug(1) -> write('Line list: '), writeln(HorizontalList) ; true),
		(hasNInLine(CheckList, HorizontalList) ->		% Check if line list as a winning play
			displayPlayerWon(Player, Board)
			;											% (Im dumb, idk how to do this crap)
			diagToList(Board, DiagList), !,				% Get all board possible diagonals lists into a list 
			(debug(1) -> write('Diagonals List: '), writeln(DiagList) ; true),
			(listOfListsHasTerminalList(DiagList, CheckList) ->  % check if any of those lists as a winning play
				displayPlayerWon(Player, Board)
				;
				true
			)
		)
	).

displayPlayerWon(Player, Board) :-
	write('PLAYER '), write(Player), writeln(' HAS WON!!'),
	display(Board),
	nl, writeln('Press Any key to exit.'),
	read(Key),
	halt.

/*
	See if there are any 4 sequencial and equal elements in a list
	CHANGE LENGTH FOR DIFFERENT BOARDS
*/
% Bether name: isSubListOfList(SubList, List)
hasNInLine(CheckList, [H|T]) :-
	length(CheckList, Len),
	length([H|T], L),
	(L = Len ->
		append([],CheckList,[H|T])
		;
		(append(CheckList,_,[H|T]) -> true ; hasNInLine(CheckList,T))
	).

% ----------------------------------- Utils for MinMax algorithm ---------------------------------

/*
	Check if Pos is a terminal play
*/
hasTerminalPlay(Board, Piece) :-
	(Piece = x ->
		CheckList = [x,x,x]
		;
		CheckList = [o,o,o]
	),
	% check if there are any horizontal wins
	(listOfListsHasTerminalList(Board, CheckList) -> 
		true 
		;
		% check if there are any vertical wins
			%Rotate Board
		rotateBoard(Board, RotatedBoard),
			% Horizontal Check
		(listOfListsHasTerminalList(RotatedBoard, CheckList) ->
			true
			;
			% Check if there are any diagonal wins
				% Get a board that consists of diagonals Lists
			diagToList(Board, DiagList), !,
				% Horizontal check
			(listOfListsHasTerminalList(DiagList, CheckList) ->
				true
				;
				false
			)
		)
	).

/*
	listOfListsHasTerminalList(Board, CheckList)
	NEEDS CUT ( ! )
*/
listOfListsHasTerminalList([H|[]], CheckList) :-
	hasNInLine(CheckList, H).

listOfListsHasTerminalList([H|T], CheckList) :-
	(hasNInLine(CheckList, H) ->
		true
		;
		listOfListsHasTerminalList(T, CheckList)
	).

/*
	Rotate Board 90º
*/
rotateBoard(Board,RotatedBoard) :-
	length(Board, L),
	rotateBoard(Board, Aux, RBoard, L), !,
	reverse(RBoard, RotatedBoard).

rotateBoard(Board, Aux, RotatedBoard, Size) :-
	(Size = 1 -> 
		toList(0, Board, List),
		append(Aux, [List], RotatedBoard)
		;
		S is Size - 1,
		Count is Size - S,
		toList(Count, Board, List),
		append(Aux, [List], Aux2),
		rotateBoard(Board, Aux2, RotatedBoard, S)
	).

/*
	Get possible winner diagonals in a List 
*/
diagToList([[A,B],[C,D]], [[A,D],[B,C]]).
diagToList([[A,B,C],[D,E,F],[G,H,I]], [[A,E,I],[C,E,G]]).
diagToList([[A,B,C,D],[E,F,G,H],[I,J,K,L],[M,N,O,P]], [[A,F,K,P],[E,J,O],[B,G,L],[D,G,J,M],[C,F,I],[H,K,N]]).

/* -----------------------TODO: REMOVE OR MAKE IT WORK------------------------ */
createBoard([]).
createBoard([H|T]) :- printBoard(H),nl,nl,createBoard(T).
printBoard([]).
printBoard([0|T]) :- ansi_format([bold,fg(blue)], 'O~w', [' ']), printBoard(T).
printBoardRows(Board) :- ansi_format([bold,fg(blue)], '1 2 3 4 5 6 7 ~w', [' ']),nl,nl, createBoard(Board).

printEmptyBoard :- emptyBoard(Board), printBoardRows(Board).

/*---------------------------------------------------------------------------------*/
col(0).
col(1).
col(2).

player(1, x).
player(2, o).

piece(x).
piece(o).
% findall(+Template, +Goal, -List)

moves([Player,Board], PosList) :-
	(Player = 1 -> P is 2; P is 1),
	findall([P,RetBoard], (col(Col), player(P,Piece), playPiece(Col,Board,Piece,RetBoard,Ctr,Line,'PvM'), \+ hasTerminalPlay(Board,Piece), Board \= RetBoard), PosList),
	(PosList = [] -> false ; true).

min_to_move([2,_]) :- true.
min_to_move([1,_]) :- false.

max_to_move([1,_]) :- true.
max_to_move([2,_]) :- false.


staticval([Player,Board],Val) :-
	(hasTerminalPlay(Board, x) ->
		Val is 1
		;
		(hasTerminalPlay(Board, o) -> 
			Val is -1
			; 
			Val is 0
		)
	).

empty_board(0, _, []) :- !.
empty_board(Height, Width, [Slot|Slots]) :-
    Height1 is Height - 1,
    fill_line(Width, -, Slot),
    empty_board(Height1, Width, Slots).

fill_line(0, Symbol, [Symbol]) :- !.
fill_line(Width, Symbol, [Symbol|Slots]) :-
    Width1 is Width - 1,
    fill_line(Width1, Symbol, Slots).

