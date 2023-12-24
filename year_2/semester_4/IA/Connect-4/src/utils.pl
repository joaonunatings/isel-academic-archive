my_length([], 0).
my_length([_|T], N1) :- my_length(T, N), N1 is N + 1.

my_nth(N, [_|T], Result) :-
    N > 0,
    N1 is N - 1,
    my_nth(N1, T, Result).
my_nth(0, [H|_], H).

set_color(Color).
	
/*
	Predicate that transforms column to a list
	get element at Col idx from board list
	add it to the outgoing list
	repeat
*/
toList(Col, [H|[]], List) :-
	nth0(Col, H, Elem),
	append(RList, [Elem], List).

toList(Col, [H|T], List) :-
	toList(Col, T, RList),
	nth0(Col, H, Elem),
	append(RList, [Elem], List).


replace([_|T], 0, X, [X|T]).
replace([H|T], I, X, [H|R]) :- I > -1, NI is I-1, replace(T, NI, X, R), !.
replace(L, _, _, L).

reverse([],Z,Z).
reverse([H|T],Z,Acc) :- reverse(T,Z,[H|Acc]).

empty_board(0, _, []) :- !.
empty_board(Height, Width, [Slot|Slots]) :-
    Height1 is Height - 1,
    fill_line(Width, -, Slot),
    empty_board(Height1, Width, Slots).

fill_line(0, _, []) :- !.
fill_line(Width, Symbol, [Symbol|Slots]) :-
    Width1 is Width - 1,
    fill_line(Width1, Symbol, Slots).

break(Input) :- (Input = 'E' -> write('exiting'), nl, halt ; writeln(" ")).