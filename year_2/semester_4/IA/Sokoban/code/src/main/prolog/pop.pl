% Partial Order Planner, using CLP(FD) and iterative deepening search
%
% Partially ordered plan = pop( Actions, OpenConditions, TrueConditions, FinishingTime)
%
% Actions = [ Action1:Time1, Action2:Time2, ...] Actions and their execution times
% OpenConditions = [ Cond1:Time1, Cond2:Time2, ...]
% TrueConds = [ Cond1:Time11/Time12, Cond2:Time21/Time22, ... ]
% Note: Ordering constraints are implemented as CLP(FD) constraints

:- use_module(library(clpfd)). % Load library for CLP(FD)

:- op(100, fx, ~). % Notation for negative effects of an action

% plan(StartState, Goals, Plan, Finish):
% Plan is partially ordered plan that achieves Goals from StartState at time Finish
%
plan(StartState, Goals, Plan) :-
	add_intervals(0, StartState, TrueConds, Finish),    % StartState true at time 0
	add_times(Finish, Goals, OpenConds),                % Goals should be true at time Finish
	EmptyPlan = pop([], OpenConds, TrueConds, Finish),  % No actions in initial plan
	MaxActions in 0..100,                               % Maximally 100 actions in plan
	indomain(MaxActions),                               % Enforce iterative deepening search
	Finish in 1..MaxActions,                            % Domain for finishing time of Plan
	depth_first(EmptyPlan, SolutionPath, MaxActions),   % Search in space of POP's
	once(indomain(Finish)),                             % Minimize finishing time
	append(_, [Plan], SolutionPath).                    % Working plan is last element of solution path


% s(POP, NewPOP): successor relation between partially ordered plans
%     NewPOP is POP with the first open condition in POP achieved
%
s( pop(Acts, [Cond:Time | OpenPs], TrueConds, Fin),
  pop(Acts, OpenPs, TrueConds, Fin) ) :-
	member(Cond:Time1/Time2, TrueConds), % Cond already true between Time1 and Time2
	Time1 #< Time, Time #=< Time2.       % Constrain Time to interval Time1/Time2
		
s( pop(Acts, [Cond:Time | OpenPsO], TrueCondsO, Fin),
	 pop([Action1:Time1 | Acts], OpenPs, TrueConds, Fin) ) :-
	effects(Action1, Effects),              % Look for action that may achieve Cond
	del(Cond, Effects, RestEffects),        % Cond in Effects, that is Action1 achieves Cond
	can(Action1, PreConds1),                % Preconditions for Action1
	0 #< Time1, Time1 #< Time,              % Action1 must occur after 0 and before Time
	add_times(Time1, PreConds1, NewOpenPs),                   % Add Time1 to all preconditions
	add_intervals(Time1, RestEffects, RestEffectsTimes, Fin), % Add time intervals to all effects
	Time #=< Time2,                         % Achieved condition must be true until Time
	add_conds([Cond:Time1/Time2 | RestEffectsTimes], TrueCondsO, TrueConds), % Add effects to TrueCondsO
	append(NewOpenPs, OpenPsO, OpenPs).       % Add preconditions of Action to goals


% add_conds(Conds, TrueConds, NewTrueConds):
%    Add conditions Conds to list TrueConds, and set corresponding precedence constraints
%
add_conds([], TrueConds, TrueConds).

add_conds([CondTime | Conds], TrueCondsO, TrueConds) :-
	no_conflict(CondTime, TrueCondsO), % No conflict between CondTime and TrueCondsO
	add_conds(Conds, [CondTime | TrueCondsO], TrueConds).


% no_conflict(CondTime, TrueCondsO):
%   Set constraints to ensure no conflict between CondTime and TrueCondsO
%
no_conflict(_, []).

no_conflict(CondTime, [Cond1Time1 | TrueConds]) :-
	no_conflict1(CondTime, Cond1Time1),
	no_conflict(CondTime, TrueConds).

no_conflict1(CondA:Ta1/Ta2, CondB:Tb1/Tb2) :-
	inconsistent(CondA, CondB), !, % CondA inconsistent with CondB
	(Ta2 #=< Tb1; Tb2 #=< Ta1)     % Ensure no time overlap between CondA and CondB
	;
	true. % Case when CondA consistent with CondB - no constraint needed

% add_times(Time, Conds, TimedConds)
%
add_times(_, [], []).

add_times(Time, [Cond | Conds], [Cond:Time | TimedConds]) :-
	add_times(Time, Conds, TimedConds).

% add_intervals(Time, Conds, TimedConds, Finish):
%   every condition in Conds true from Time till some later time
%
add_intervals(_, [], [], _).

add_intervals(Time, [Cond | Conds], [Cond:Time/Time2 | TimedConds], Finish) :-
	Time #< Time2, Time2 #=< Finish, % Cond true from Time until Time2 =< Finish
	add_intervals(Time, Conds, TimedConds, Finish).


% depth_first(POP, SolutionPath, MaxActionsInPOP):
%   Depth-first search, with respect to number of actions, among partially ordered plans
%
depth_first(POP, [POP], _) :-
	POP = pop(_, [], _, _). % No open preconditions - this is a working plan

depth_first(First, [First | Rest], MaxActions) :-
	First = pop(Acts, _, _, _),
	length(Acts, NActs),
	(NActs < MaxActions, !         % # actions in plan is below MaxActions
	 ;
	 Second = pop(Acts, _, _, _)), % # actions in plan at maximum, no action may be added
	s(First, Second),
	depth_first(Second, Rest, MaxActions).


% Display all possible execution schedules of a partial order plan
%
show_pop(pop(Actions, _, _, _)) :-
	instantiate_times(Actions),               % Instantiate times of actions for readability
	setof(T:A, member(A:T, Actions), Sorted), % Sort actions according to times
	nl, write('Actions = '), write(Sorted),   % Write schedule
	fail                                      % Backtrack to produce other schedules
	;
	nl, nl.                                   % All schedues produced


% instantiate_times( Actions): instantiate times of actions respecting ordering constraints
%
instantiate_times([]).

instantiate_times([_:T | Acts]) :-
	indomain(T),                   % A value in domain of T
	instantiate_times(Acts).


del( X, [X | Tail], Tail).

del( X, [Y | Tail], [Y | Tail1]) :-
	del( X, Tail, Tail1).


can(go(P1,A,B), [at(P1,A), clear(B)]) :- 
    sokoban(P1), adjacent(A,B).

effects(go(P1,A,B), [at(P1,B), clear(A), ~at(P1,A), ~clear(B)]).

can(push(P1,A,Box,B,C), [at(P1,A),at(Box,B),clear(C)]) :-
    sokoban(P1), box(Box), adjacent(A,B), adjacent(B,C), inline(A,C).
    
effects(push(P1,A,Box,B,C), [at(P1,B), at(Box,C), clear(A), ~at(P1,A), ~at(Box,B), ~clear(C)]).

adjacent(A,B) :- 
    n(A,B) ; n(B,A).
%adjacent(X1/Y1,X2/Y2) :- 
%   ((abs(X1+1)=:=X2 ; abs(X1-1)=:=X2 ; X1=:=X2), Y1=:=Y2) ; 
%   ((abs(Y1+1)=:=Y2 ; abs(Y1-1)=:=Y2 ; Y1=:=Y2), X1=:=X2), !.



inline(X1/Y1,X2/Y2) :- 
    X1=:=X2 ; Y1 =:= Y2, !.

% inconsistent(G1, G2): goals G1 and G2 inconsistent
%
inconsistent(G, ~G).  % Negated goals always inconsistent

inconsistent(~G, G).

inconsistent(at(P1, C1), at(P1, C2)) :-
	C1 \== C2.

inconsistent(at(_, C), clear(C)).

inconsistent(clear(C), at(_, C)).


sokoban('@').

wall('#').

box('$1').
box('$2').

box_goal('.').

box_in_goal('*').

n(0/0,0/1). n(0/0,1/0). n(1/0,2/0). n(1/0,1/1). n(2/0,2/1). n(2/0,3/0). n(3/0,3/1). n(3/0,4/0). n(4/0,4/1). n(4/0,5/0).
n(0/1,0/2). n(0/1,1/1). n(1/1,1/2). n(1/1,2/1). n(2/1,2/2). n(2/1,3/1). n(3/1,3/2). n(3/1,4/1). n(4/1,4/2). n(4/1,5/1).
n(0/2,0/3). n(0/2,1/2). n(1/2,1/3). n(1/2,2/2). n(2/2,2/3). n(2/2,3/2). n(3/2,3/3). n(3/2,4/2). n(4/2,4/3). n(4/2,5/2).
n(0/3,0/4). n(0/3,1/3). n(1/3,1/4). n(1/3,2/3). n(2/3,2/4). n(2/3,3/3). n(3/3,3/4). n(3/3,4/3). n(4/3,4/4). n(4/3,5/3).
n(0/4,1/4). n(1/4,2/4). n(2/4,3/4). n(3/4,4/4). n(4/4,5/4). n(5/0,5/1). n(5/1,5/2). n(5/2,5/3). n(5/3,5/4).

state1([at('#',0/0),at('#',1/0),at('#',2/0),at('#',3/0),at('#',4/0),at('#',5/0),
        at('#',0/1),at('@',1/1),clear(2/1),at('$2',3/1),clear(4/1),at('#',5/1),
        at('#',0/2),clear(1/2),at('$1',2/2),clear(3/2),clear(4/2),at('#',5/2),
        at('#',0/3),at('#',1/3),clear(2/3),clear(3/3),clear(4/3),at('#',5/3),
        at('#',0/4),at('#',1/4),at('#',2/4),at('#',3/4),at('#',4/4),at('#',5/4)]).

% # # # # # #  
% # @ - $ . #  
% # - $ - - #
% # # . - - #
% # # # # # #
%

goal1([at('$1',2/3),at('$2',4/1)]).
