Usando o algoritmo de partial order planning, foram criadas duas operações, uma para movimentar o player, e outra para o player empurrar caixas.

```prolog
can(go(P1,A,B), [at(P1,A), clear(B)]) :- 
    sokoban(P1), adjacent(A,B).

effects(go(P1,A,B), [at(P1,B), clear(A), ~at(P1,A), ~clear(B)]).
```

```
can(push(P1,A,Box,B,C), [at(P1,A),at(Box,B),clear(C)]) :-
    sokoban(P1), box(Box), adjacent(A,B), adjacent(B,C), inline(A,C).
    
effects(push(P1,A,Box,B,C), [at(P1,B), at(Box,C), clear(A), ~at(P1,A), ~at(Box,B), ~clear(C)]).
```

- Adjacent() retorna true se duas posições sao adjacentes

- Inline() retorna true se duas posições estiverem na mesma direção

  É usado o state1() para devolver um estado, e goal1() para devolver as goals para esse estado

  Paredes são representadas por #, o player por @, e as caixas por $1, $2, ...

  A forma do goal para as caixas nao foi em si modelado, neste caso é representado pelas goals para cada caixa, dadas pelo predicado goal1() 

Desta forma o programa é corrido com a seguinte chamada: 

````
state1(S), goal1(G), plan(S,G,P), show_pop(P).
````

