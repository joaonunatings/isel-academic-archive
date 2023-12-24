name('Connect-X').
title('Connect is a two-player connection board game').
version('1.1.0').
author('Alexandre Luís, João Nunes and Raul Santos').

print_welcome :-
	name(Str1), write(Str1), write(" - "), title(Str2), writeln(Str2), nl,
	writeln('Information:'),
	writeln('Game developed for Artificial Intelligence subject of Computer Science course at ISEL'),
	writeln('AI algorithm uses Minimax with Alphabeta pruning optimization'), nl,
	write('Developed by: '), author(Author), writeln(Author),
	write('Version: '), version(Version), writeln(Version),
	writeln('Press enter to continue...'),
	get_char(_).

print_game_mode :-
	writeln('Available modes: '),
	writeln('0. - Player vs player'),
	writeln('1. - Player vs computer'),
	writeln('Select mode (0. or 1.): ').

print_ai_algorithm :-
	writeln('Available algorith\'s: '),
	writeln('0. - Minimax'),
	writeln('1. - Alphabeta (minimax optimization)'),
	writeln('Select algorithm (0. or 1.): ').

print_starting_player :-
	writeln('Select starting player: '),
	writeln('1. - Player 1'),
	writeln('2. - Player 2').

print_starting_player_machine :-
	writeln('Select starting player: '),
	writeln('1. - Machine'),
	writeln('2. - Player').

print_player_play(Player) :-
	write("Player "), write(Player), writeln(" play").

print_end :-
	writeln('Game ended').

/*
	Displaye predicates needs cut at the end ( ! )
*/
display([A,B]) :- 
	writeln('------- BOARD ------'),
	write('	 '), writeln([A]),
	write('	 '), writeln([B]).

display([A,B,C]) :- 
	writeln('------- BOARD ------'),
	write('      '), writeln([A]),
	write('      '), writeln([B]),
	write('      '), writeln([C]).

display([A,B,C,D]) :- 
	writeln('------- BOARD ------'),
	write('     '), writeln([A]),
	write('     '), writeln([B]),
	write('     '), writeln([C]),
	write('     '), writeln([D]).

display([A,B,C,D,E,F]) :- 
	writeln('------- BOARD ------'),
	write('  '), writeln([A]),
	write('  '), writeln([C]),
	write('  '), writeln([B]),
	write('  '), writeln([D]),
	write('  '), writeln([E]),
	write('  '), writeln([F]).

exception(Message):
	writeln("Can't recognize that choice"), writeln(Message), nl, fail.

print_debug(Message) :- 
	(debug(1) -> nl, writeln(Message); true).

print_debug(Message, Arg) :-
	(debug(1) -> nl, write(Message), writeln(Arg); true).