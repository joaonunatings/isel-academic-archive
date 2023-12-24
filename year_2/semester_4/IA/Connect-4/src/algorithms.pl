/* --------------------------------- MIN MAX ------------------------------------- */

% Pos - position
% BestSucc - best move from Pos
% Val - minimax value
minimax(Pos, BestSucc, Val) :-
   	moves(Pos, PosList), !,      % Legal moves in Pos produce PosList
	print_debug('POSSIBLE PLAYS: ', PosList),
	minimax_best(PosList, BestSucc, Val)
   	; % Or
   	staticval(Pos, Val),      % Pos has no successors: evaluate statically
	print_debug('IN-POS: ', Pos),
	print_debug('STATIC VAL: ', Val).

minimax_best([Pos], Pos, Val) :-
   	minimax(Pos, _, Val), !.

minimax_best([Pos1 | PosList], BestPos, BestVal) :-
   	minimax(Pos1, _, Val1),
   	minimax_best(PosList, Pos2, Val2),
   	minimax_betterof(Pos1, Val1, Pos2, Val2, BestPos, BestVal).

minimax_betterof(Pos0, Val0, Pos1, Val1, Pos0, Val0) :- % Pos0 better than Pos1
	min_to_move(Pos0),     % MIN to move in Pos0
	Val0 > Val1, !          % MAX prefers the greater value
	; % Or 
	max_to_move(Pos0),     % MAX to move in Pos0
	Val0 < Val1, !.         % MIN prefers the lesser value

minimax_betterof(Pos0, Val0, Pos1, Val1, Pos1, Val1). % Otherwise Pos1 better than Pos0

/* ------------------------------------- ALPHA-BETA ------------------------------- */

alphabeta(Pos, Alpha, Beta, GoodPos, Val) :-
	moves(Pos, PosList), !,
	alphabeta_boundedbest(PosList, Alpha, Beta, GoodPos, Val)
	; % Or
	staticval(Pos, Val).       % Static value of Pos

alphabeta_boundedbest([Pos | PosList], Alpha, Beta, GoodPos, GoodVal) :-
	alphabeta(Pos, Alpha, Beta, _, Val),
	alphabeta_goodenough(PosList, Alpha, Beta, Pos, Val, GoodPos, GoodVal).

alphabeta_goodenough([], _, _, Pos, Val, Pos, Val) :- !.   % No other candidate

alphabeta_goodenough(_, Alpha, Beta, Pos, Val, Pos, Val) :-
	min_to_move(Pos), Val > Beta, !   % Maximizer attained upper bound
	; % Or
	max_to_move(Pos), Val < Alpha, !. % Minimizer attained lower bound

alphabeta_goodenough(PosList, Alpha, Beta, Pos, Val, GoodPos, GoodVal) :-
	alphabeta_newbounds(Alpha, Beta, Pos, Val, NewAlpha, NewBeta),     % Refine bounds
	alphabeta_boundedbest(PosList, NewAlpha, NewBeta, Pos1, Val1),
	alphabeta_betterof(Pos, Val, Pos1, Val1, GoodPos, GoodVal).

alphabeta_newbounds(Alpha, Beta, Pos, Val, Val, Beta) :-
	min_to_move(Pos), Val > Alpha, !.         % Maximizer increased lower bound

alphabeta_newbounds(Alpha, Beta, Pos, Val, Alpha, Val) :-
	max_to_move(Pos), Val < Beta, !.          % Minimizer decreased upper bound
	
alphabeta_newbounds(Alpha, Beta, _, _, Alpha, Beta). % Otherwise bounds unchanged

alphabeta_betterof(Pos, Val, Pos1, Val1, Pos, Val) :-   % Pos better than Pos1
	min_to_move(Pos), Val > Val1, !
	; % Or
	max_to_move(Pos), Val < Val1, !.

alphabeta_betterof(_, _, Pos1, Val1, Pos1, Val1). % Otherwise Pos1 better
