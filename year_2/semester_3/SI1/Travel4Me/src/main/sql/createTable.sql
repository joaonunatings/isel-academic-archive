/**
 * Sistemas de Informação I
 * Segunda fase do trabalho prático
 * Semestre de Verão 2020-2021
 *
 * Ficheiro createTable.sql
 * Cria as tabelas para a DB com as possíveis restrições de integridades (realizáveis em T-SQL).
 *
 * Autores: João Nunes (47220) e Alexandre Silva (47192)
 */

begin try
    begin transaction

        create table PASSAGEIRO (
            nid char(10),
            nome nvarchar(150) not null,
            dtnascimento date not null,
            email nvarchar(60) not null,
            genero char(2),
            constraint PK_PASSAGEIRO primary key (nid),
            constraint PASSAGEIRO_EMAIL check (email like ('%@%')),
            constraint PASSAGEIRO_GENERO check (genero in ('F', 'M', 'NR'))
        )

        create table LOCALIDADE (
            codpostal int,
            nome nvarchar(150) not null,
            constraint PK_LOCALIDADE primary key (codpostal),
            constraint LOCALIDADE_CODPOSTAL check (codpostal like '_______')
        )

        create table ESTACAO (
            nome varchar(40),
            tipo char(10) not null,
            nplataforma tinyint not null,
            localidade int not null,
            constraint PK_ESTACAO primary key (nome),
            constraint ESTACAO_TIPO check (tipo in ('terminal', 'paragem'))
        )
        alter table ESTACAO add constraint
            FK_ESTACAO_LOCALIDADE foreign key (localidade) references LOCALIDADE (codpostal) on delete cascade
        -- alter table ESTACAO drop constraint FK_ESTACAO_LOCALIDADE

        create table VIAGEM (
            ident       int identity(1,1),
            dataviag    date        not null,
            horapartida time        not null,
            horachegada time        not null,
            distancia   int         not null,
            estpartida  varchar(40) not null,
            estchegada  varchar(40) not null,
            constraint PK_VIAGEM primary key (ident),
            constraint VIAGEM_DATAVIAG check (dataviag like ('[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]')),
            constraint VIAGEM_HORACHEGADA check (horachegada > horapartida),
            constraint VIAGEM_ESTCHEGADA check (estpartida != estchegada)
        )
        alter table VIAGEM add constraint FK_VIAGEM_ESTPARTIDA foreign key (estpartida) references ESTACAO (nome)
        -- alter table VIAGEM drop constraint FK_VIAGEM_ESTPARTIDA
        alter table VIAGEM add constraint FK_VIAGEM_ESTCHEGADA foreign key (estchegada) references ESTACAO (nome)
        -- alter table VIAGEM drop constraint FK_VIAGEM_ESTCHEGADA

        create table TRANSPORTE (
            ident tinyint identity(1,1),
            viagem int not null,
            velmaxima int not null,
            dataserv date not null,
            atrdiscriminante char(1) not null,
            constraint PK_TRANSPORTE primary key (ident),
            constraint TRANSPORTE_DATASERV check (dataserv <= cast(getdate() as date)),
            constraint TRANSPORTE_ATRDISCRIMNANTE check (atrdiscriminante in ('A', 'C'))
        )
        alter table TRANSPORTE add constraint FK_TRANSPORTE_VIAGEM foreign key (viagem) references VIAGEM (ident)
        -- alter table TRANSPORTE drop constraint FK_TRANSPORTE_VIAGEM

        create table RESERVA (
            ident int identity(1,1),
            datares smalldatetime not null,
            modopagamento char(10) not null,
            viagem int not null,
            constraint PK_RESERVA primary key (ident),
            constraint RESERVA_MODOPAGAMENTO check (modopagamento in ('MB', 'MBWAY', 'PayPal', 'CC'))
        )
        alter table RESERVA add constraint FK_RESERVA_VIAGEM foreign key (viagem) references VIAGEM (ident) on delete cascade
        -- alter table RESERVA drop constraint FK_RESERVA_VIAGEM

        create table PAGMBWAY (
            reserva int,
            telefone varchar(25),
            constraint PK_PAGMBWAY primary key (reserva, telefone),
            constraint PAGMBWAY_TELEFONE check (telefone like ('+3519[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'))
        )
        alter table PAGMBWAY add constraint FK_PAGMBWAY_RESERVA foreign key (reserva) references RESERVA (ident) on delete cascade
        -- alter table PAGMBWAY drop constraint FK_PAGMBWAY_RESERVA

        create table AUTOCARROTIPO (
            marca nchar(10),
            modelo nchar(6),
            nlugares tinyint not null,
            constraint PK_AUTOCARROTIPO primary key (marca, modelo)
        )

        create table AUTOCARRO (
            matricula nchar(8),
            transporte tinyint,
            datarevisao date not null,
            marca nchar(10) not null,
            modelo nchar(6) not null,
            constraint PK_AUTOCARRO primary key (matricula, transporte),
            constraint AUTOCARRO_MATRICULA check (matricula like ('[A-Z][A-Z]-[0-9][0-9]-[A-Z][A-Z]')),
            constraint AUTOCARRO_DATAREVISAO check (datarevisao > cast(getdate() as date))
        )
        alter table AUTOCARRO add constraint FK_AUTOCARRO_TRANSPORTE foreign key (transporte) references TRANSPORTE (ident)
        -- alter table AUTOCARRO drop constraint FK_AUTOCARRO_TRANSPORTE
        alter table AUTOCARRO add constraint FK_AUTOCARRO_MARCA_MODELO foreign key (marca, modelo) references AUTOCARROTIPO (marca, modelo) on delete cascade
        -- alter table AUTOCARRO drop constraint FK_AUTOCARRO_MARCA_MODELO

        create table COMBOIOTIPO (
            id char(2),
            nome char(25) not null,
            nlugclasse1 tinyint not null,
            nlugclasse2 tinyint not null,
            constraint PK_COMBOIOTIPO primary key (id),
            constraint COMBOIOTIPO_ID check (id in ('AP', 'IC', 'IR', 'R')),
            constraint COMBOIOTIPO_NOME check (nome in ('alfa-pendular', 'inter-cidades', 'inter-regional', 'regional')),
        )

        create table COMBOIO (
            transporte tinyint,
            tipo char(2) not null,
            ncarruagens tinyint not null,
            constraint PK_COMBOIO primary key (transporte),
            constraint COMBOIO_NRCARRUAGENS check ((ncarruagens > 0) and ((ncarruagens <= 8 and tipo != 'AP') OR (ncarruagens <= 6 and tipo = 'AP')))
        )
        alter table COMBOIO add constraint FK_COMBOIO_TRANSPORTE foreign key (transporte) references TRANSPORTE (ident)
        -- alter table COMBOIO drop constraint FK_COMBOIO_TRANSPORTE
        alter table COMBOIO add constraint FK_COMBOIO_TIPO foreign key (tipo) references COMBOIOTIPO (id) on delete cascade
        -- alter table COMBOIO drop constraint FK_COMBOIO_TIPO

        create table LOCOMOTIVA (
            nserie int,
            comboio tinyint,
            marca varchar(15) not null,
            constraint PK_LOCOMOTIVA primary key (nserie, comboio),
        )
        alter table LOCOMOTIVA add constraint FK_LOCOMOTIVA_COMBOIO foreign key (comboio) references COMBOIO (transporte)
        -- alter table LOCOMOTIVA drop constraint FK_LOCOMOTIVA_COMBOIO

        create table ALFAPENDULAR (
            nserie int,
            comboio tinyint,
            numero int not null,
            constraint PK_ALFAPENDULAR primary key (nserie, comboio)
        )
        alter table ALFAPENDULAR add constraint FK_ALFAPENDULAR_LOCOMOTIVA foreign key (nserie, comboio) references LOCOMOTIVA (nserie, comboio) on delete cascade
        -- alter table ALFAPENDULAR drop constraint FK_ALFAPENDULAR_LOCOMOTIVA

        create table LUGARTIPO (
            numero int,
            nome nvarchar(10) not null,
            classe tinyint,
            preco decimal(5,2) not null,
            constraint PK_LUGARTIPO primary key (numero),
            constraint LUGARTIPO_NOME check (nome in ('adulto', 'jovem', N'sénior', N'criança', 'militar')),
            constraint LUGARTIPO_CLASSE check (classe in (1, 2)),
        )

        create table LUGAR (
            numero int,
            transporte tinyint,
            tipo int,
            constraint PK_LUGAR primary key (numero, transporte, tipo)
        )
        alter table LUGAR add constraint FK_LUGAR_TRANSPORTE foreign key (transporte) references TRANSPORTE (ident) on delete cascade
        -- alter table LUGAR drop constraint FK_LUGAR_TRANSPORTE
        alter table LUGAR add constraint FK_LUGAR_TIPO foreign key (tipo) references LUGARTIPO (numero)
        -- alter table LUGAR drop constraint FK_LUGAR_TIPO

        create table BILHETE (
            passageiro char(10) foreign key references PASSAGEIRO (nid) on delete cascade,
            nlugar int not null,
            tipolugar int not null,
            transporte tinyint not null,
            reserva int foreign key references RESERVA (ident),
            constraint PK_BILHETE primary key (passageiro, nlugar, tipolugar, transporte, reserva)
        )
        alter table BILHETE add constraint FK_BILHETE_LUGAR foreign key (nlugar, transporte, tipolugar) references LUGAR (numero, transporte, tipo) on delete cascade
        -- alter table BILHETE drop constraint FK_BILHETE_LUGAR

    commit
end try
begin catch
    rollback transaction
    select error_message() as ErrorMessage,
           error_severity() as ErrorSeverity,
           error_line() as ErrorLine
end catch
