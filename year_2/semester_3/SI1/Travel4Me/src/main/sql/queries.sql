/**
 * Sistemas de Informação I
 * Segunda fase do trabalho prático
 * Semestre de Verão 2020-2021
 *
 * Ficheiro queries.sql
 * Contêm a resolução dos exercícios 3 e 4.
 *
 * Autores: João Nunes (47220) e Alexandre Silva (47192)
 */

/**
 * 3.(a) - 2.(a)
 */
select distinct ident, horapartida, horachegada from VIAGEM V
join ESTACAO E on E.nome = V.estchegada or V.estpartida = E.nome
where estpartida = any(
    select ESTACAO.nome from ESTACAO where ESTACAO.localidade in (
        select codpostal from LOCALIDADE L where L.nome = 'Lisboa'))
    and estchegada = any(
        select ESTACAO.nome from ESTACAO where ESTACAO.localidade in (
            select codpostal from LOCALIDADE L where L.nome = 'Porto'));

/**
 * 3.(a) - 2.(b)
 *
 * Uso da função DATEDIFF para calcular a idade do passageiro
 */
select nome, (select DATEDIFF(year, dtnascimento, GETDATE())) as idade, nlugar from PASSAGEIRO
join BILHETE B on PASSAGEIRO.nid = B.passageiro;

/**
 * 3.(a) - 2.(c)
 */
(select distinct A.marca, A.modelo, nlugares from AUTOCARROTIPO
    right join AUTOCARRO A on AUTOCARROTIPO.marca = A.marca and AUTOCARROTIPO.modelo = A.modelo)
union
(select marca, tipo as modelo, sum(nlugclasse1 + nlugclasse2) as nlugares from COMBOIOTIPO
join COMBOIO C on COMBOIOTIPO.id = C.tipo
join LOCOMOTIVA L on C.transporte = L.comboio
group by marca, tipo);

/**
 * 3.(a) - 2.(d)
 *
 * Começar por obter as estações de partida cuja localidade é Lisboa.
 * De seguida, calcular o número de lugares vazios. Este cálculo é feito a partir dos bilhetes que existem para a
 * viagem menos os lugares disponíveis para cada transporte
 */
select VIAGEM.ident, VIAGEM.dataviag, (AUTOCARROTIPO.nlugares - count(*)) as nrLugaresVazios from BILHETE, AUTOCARROTIPO, VIAGEM, RESERVA
where BILHETE.reserva = RESERVA.ident and RESERVA.viagem = VIAGEM.ident and viagem in (select ident from VIAGEM
    where estpartida in (select nome from ESTACAO
        where ESTACAO.localidade in (select LOCALIDADE.codpostal from LOCALIDADE
            where LOCALIDADE.nome = 'Lisboa')))
group by VIAGEM.ident, VIAGEM.dataviag, AUTOCARROTIPO.nlugares

/**
 * 3.(a) - 2.(e)
 */
-- versão 1
select count(*) as contagem, genero from PASSAGEIRO
where genero = 'NR'
group by genero
union
select count(*), genero from PASSAGEIRO
where genero = 'F'
group by genero
union
select count(*), genero from PASSAGEIRO
where genero = 'M'
group by genero
order by contagem desc;

-- versão 2
select count(*) as contagem, genero from PASSAGEIRO
group by genero
order by contagem desc;

/**
 * 3.(a) - 2.(f)
 *
 * Soma dos preços dos bilhetes através de SUM(preços dos bilhetes da categoria de adultos)
 */
-- versão 1
select sum(preco) as precoTotalAdulto from BILHETE B, LUGARTIPO L
where B.tipolugar = L.numero and L.nome = 'adulto'
-- versão 2
select sum(preco) as precoTotalAdulto from BILHETE B,
    (select * from LUGARTIPO except select * from LUGARTIPO where LUGARTIPO.nome != 'adulto') L
where B.tipolugar = L.numero

/**
 * 3.(a) - 2.(g)
 *
 * Uso da função AVG(idade) para calcular da média dos passageiros por reserva
 */
select avg(datediff(year, dtnascimento, getdate())) as mediaIdade, reserva, datares from BILHETE
join PASSAGEIRO P on P.nid = BILHETE.passageiro
join RESERVA R2 on R2.ident = BILHETE.reserva
group by reserva, datares

/**
 * 3.(b)
 *
 * É necessário organizar por ordem alfabética e depois por idades (crescente). Usa-se o operador order by (asc é a
 * ordem padrão).
 */
select P.nome, datediff(year, P.dtnascimento, getdate()) as idade
from ((RESERVA R join BILHETE B on R.ident = B.reserva) join PASSAGEIRO P on P.nid = B.passageiro)
where exists (
    select *
    from VIAGEM V, ESTACAO E, LOCALIDADE L
    where R.viagem = V.ident and V.estchegada = E.nome and E.localidade = L.codpostal and L.nome = 'Lisboa')
order by P.nome, idade

/**
 * 3.(c)
 *
 * Interseção entre viagens de partida de Lisboa com o período da manhã com viagens de chegada ao Porto com o período
 * de manhã.
 */
select *
from VIAGEM V
where estpartida in (
    select E.nome
    from ESTACAO E join LOCALIDADE L on E.localidade = L.codpostal
    where L.nome = 'Lisboa' and (V.horapartida between '06:00' and '11:59'))
intersect
select *
from VIAGEM V
where estchegada in (
    select E.nome
    from ESTACAO E join LOCALIDADE L on E.localidade = L.codpostal
    where L.nome = 'Porto' and (V.horachegada between '6:01' and '12:00'))

/**
 * 3.(d)
 *
 * Mostra o tipo de pagamento pela média de idades dos passageiros que efectuaram a reserva.
 */
-- versão 1
select R.modopagamento, avg(datediff(year, P.dtnascimento, getdate())) as idade
from RESERVA R, BILHETE B, PASSAGEIRO P
where R.ident = B.reserva and B.passageiro = P.nid
group by R.modopagamento

-- versão 2
select R2.modopagamento, avg(datediff(year, P.dtnascimento, getdate())) as idade
from PASSAGEIRO P
LEFT JOIN (BILHETE B JOIN RESERVA R2 on B.reserva = R2.ident) on P.nid = B.passageiro
group by R2.modopagamento
having modopagamento IS NOT NULL

/**
 * 3.(e)
 *
 * É necessário usar um operador de agregação, que agrupa o número de reservas por passageiro.
 */
select P.*, count(*) as n_reservas
from RESERVA R, BILHETE B, PASSAGEIRO P
where R.ident = B.reserva and B.passageiro = P.nid
group by P.nid, P.dtnascimento, P.email, P.genero, P.nome
order by n_reservas desc

/**
 * 3.(f)
 *
 * GETDATE() retorna o valor em datetime. dateres é do tipo date. Para se fazer a diferença é necessário fazer um cast
 * de datetime para date.
 */
create view RESERVAS_ULTIMO_ANO
as select *
from RESERVA R
where datediff(year, cast(R.datares as date), getdate()) = 1
-- drop view RESERVAS_ULTIMO_ANO

/**
 * 3.(g)
 *
 *  Mais uma vez, é necessário fazer cast para time do getdate() para se poder comparar as horas.
 */
create view VIAGENS_A_DECORRER
as select V.estpartida, V.estchegada, V.horapartida, datediff(minute, V.horapartida, V.horachegada) as duracao_minutos
from VIAGEM V left join ESTACAO E on V.estchegada = E.nome
where (cast(getdate() as time) between V.horapartida and V.horachegada)
-- drop view VIAGENS_A_DECORRER

/**
 * 4.
 *
 * Atualiza-se primeiro a viagem com a nova informação fornecida.
 * De seguida, atualiza-se o tipo de transporte usado para essa viagem.
 * NOTA: A alteração para todos os dias da semana é algo que só se concretiza com uma função. É alterado só um dia.
 */
begin try
    update VIAGEM
    set horapartida = '12:00:00'
    where estpartida = N'Parque das Nações' and estchegada = 'Beja'

    update COMBOIO
    set tipo = 'AP'
    where transporte in (
        select ident
        from TRANSPORTE T
        where viagem in (
            select ident
            from VIAGEM
            where estpartida = N'Parque das Nações' and estchegada = 'Beja' and horapartida = '12:00:00'
            ))
end try
begin catch
    rollback transaction
    select error_message() as ErrorMessage,
           error_severity() as ErrorSeverity,
           error_line() as ErrorLine
end catch
