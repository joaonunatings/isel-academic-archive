/**
 * Sistemas de Informação I
 * Segunda fase do trabalho prático
 * Semestre de Verão 2020-2021
 *
 * Ficheiro insertTable.sql
 * Insere data na DB respeitando todas as restrições impostas.
 *
 * Autores: João Nunes (47220) e Alexandre Silva (47192)
 */

begin try
    begin transaction

        insert into PASSAGEIRO
            (nid, nome, dtnascimento, email, genero)
        values
            ('302449302', 'Carla Regodeiro Faia', '1982-02-25', 'carlafaia01@gmail.com', 'F'),
            ('I19F026830', 'Filipa Gomes Santos', '2000-12-02', 'filipita@hotmail.com', 'F'),
            ('547632522', N'João Jonas Marques', '1990-04-28', 'jjm90@gmail.com', 'M'),
            ('548912316','Isabel dos Santos Soares','1950-06-25','issapessoal@gmail.com','F'),
            ('J29SSS2999', N'Gonçalo Castro Azevedo Araújo','1987-11-01','cors122@hotmail.com','NR'),
            ('554813200','Maria Teresa Fernandina','2001-09-11','mariateresa@gmail.com','F'),
            ('876566769', N'Júlia Rafael Gomes Marques','2002-12-17','bijuquerida@hotmail.com','F'),
            ('KS992KS29L', N'Leonor Pimentel Castro Lourenço Azevedo','1970-05-29','leonorpmcla@live.com.pt','NR'),
            ('7823JDJJ20','Rodrigo Marques Silva','1997-07-30','rodrii10@gmail.com','M'),
            ('937138238','Rodrigo Marques Gomes','1998-01-30','rodrigues@gmail.com','M'),
            ('765642744', N'João Martim Tomás', '1992-04-20', 'jmt92@gmail.com', 'M'),
            ('017060752','Ana Joana Mendes','2007-12-01','anocasmen@hotmail.com','F'),
            ('557987522','Lucas Gomes Tavares','1990-03-08','lucstavares@sapo.pt','M'),
            ('897674234','Leonor Alexandra da Silva','1998-04-05','leonorsilva@hotmail.com','F'),
            ('KSF8812386', N'Inês Mouro Borges', '1977-06-27', 'inesmborges@gmail.com', 'F'),
            ('PT1288JDSF', N'Luís Miguel Ferreira', '1970-04-04', 'lferr@live.com.pt', 'M'),
            ('684961200','Franscio Reis da Silva','1981-01-05','kikosilva@hotmail.com','M'),
            ('237459762','Rita Tavares Santos','2001-10-15','ritinha123@gmail.com','F'),
            ('231864231','Nelson Alberto Frade da Silva','1974-08-22','nelsonsilva@hotmail.com','M'),
            ('168743212', N'Laura Matos Assunção', '1954-09-02', 'nsilva@gmail.com', 'M'),
            ('4794521DI3','Luana Arroios Gomes','1976-03-18','lulugomes@live.com.pt','NR'),
            ('ODG94821KF','Miguel Pedro Perreira da Serra','1989-11-11','miguelppserra@gmail.com','M'),
            ('974010713', N'João Santos do Peixe', '1984-04-06', 'jonasfish@hotmail.com', 'M'),
            ('129874516','Tiago Martins Antunes','1999-07-23','tigas0404@gmail.com','M'),
            ('132465322','Maria Filomena Sequeira de Jesus','1970-12-13','filo1jesus@hotmail.com','F'),
            ('291097654', N'Érica Cabral Ribeiro', '2001-10-02', 'ericaribeiro@hotmail.com', 'F'),
            ('L8KF54521F','Manuel Atriz da Costa','1954-08-31','manuel54@live.com.pt','M'),
            ('234GFDHD75', N'André Rodrigues António Banha', '1967-07-22', 'rodribanha_00@gmail.com', 'M'),
            ('36874DS32F','Mariana Coelho Conduto','1949-07-09','marianacond@hotmail.com','F'),
            ('46WD45SDS3','Margarida Pequeno Mendes','2000-03-16','maguiemendes@gmail.com','F'),
            ('168412312','Hugo Reis Pedroso','1997-11-21','hreis_21@hotmail.com','M'),
            ('273427417','Filipe Ribeiro Fialho Marques','2000-09-27','flpmarques@hotmail.com','M'),
            ('897969874','Tiago Miguel Antonio das Neves ','1966-05-19','tiagomiguel@live.com.pt','M'),
            ('318974532', N'Maria Conceição Pardal da Sousa', '1956-11-03', 'mariaconceicao1956@gmail.com', 'F'),
            ('1F5S4S23DD','Sandra Alexandra Filipa Barreto','1999-12-08','sandrabarret0@gmail.com','F'),
            ('1FD6S58F4S','Martim Parreira Martins','1988-01-15','m&ms88@hotmail.com','M'),
            ('D5FS8DS7F9','Ana Raquel Tavares Martins','1997-06-20','raqmartins@hotmail.com','F'),
            ('321842231','Vasco Rodrigo Rodrigues','1981-01-07','vasquinho_0107@gmail.com','M'),
            ('114894231','Artur Minimeu da Ponta','1999-09-23','rodriponta@hotmail.com','M'),
            ('198231123','Artur Gomes da Ponta','1991-12-03','artuuuur@hotmail.com','M'),
            ('018942338','Artur Santos Sousa','1999-05-05','ass99@hotmail.com','M'),
            ('716872123', N'Verónica Beatriz de Ornelas', '1982-12-12', 'veronica_ornelas@live.com.pt', 'NR')

        insert into LOCALIDADE
            (codpostal, nome)
        values
            (3246542, N'Évora'),
            (7582132, N'Covilhã'),
            (6238511, N'Fátima'),
            (2123353,'Lisboa'),
            (4826882,'Beja'),
            (9529202,'Porto')

        insert into ESTACAO
            (nome, tipo, nplataforma, localidade)
        values
            (N'Fátima','paragem', 6, 6238511),
            ('Lisboa Sete Rios','paragem', 7, 2123353),
            ('Lisboa Oriente','paragem', 12, 2123353),
            (N'Covilhã','terminal', 7, 7582132),
            ('Mora','paragem', 6, 3246542),
            ('Castelo Branco','terminal', 3, 7582132),
            (N'Cais do Sodré','paragem', 2, 2123353),
            ('Porto','paragem', 2, 9529202),
            (N'Parque das Nações', 'terminal', 1, 2123353),
            ('Beja', 'terminal', 5, 4826882)

        insert into VIAGEM
            (dataviag, horapartida, horachegada, distancia, estpartida, estchegada)
        values
            ('2021-12-24', '15:20', '18:20', 323, N'Cais do Sodré', 'Porto'),
            ('2021-02-04', '15:00', '16:45', 124, N'Fátima', 'Porto'),
            ('2020-05-12', '15:30', '17:45', 102, N'Fátima', N'Cais do Sodré'),
            ('2021-07-16', '01:30', '04:30', 179, 'Lisboa Sete Rios', 'Mora'),
            ('2020-01-22', '14:10', '16:00', 299, 'Lisboa Oriente', N'Covilhã'),
            ('2022-12-14', '19:30', '22:30', 252, N'Cais do Sodré', 'Castelo Branco'),
            ('2020-10-09', '15:20', '16:50', 206, 'Lisboa Oriente', N'Fátima'),
            ('2021-08-26', '05:45', '08:15', 341, 'Porto', 'Lisboa Sete Rios'),
            ('2021-02-28', '12:40', '15:00', 183, N'Covilhã', 'Lisboa Sete Rios'),
            ('2022-12-30', '19:45', '22:45', 56, 'Mora', 'Porto'),
            ('2020-05-12', '12:30', '17:30', 179, 'Lisboa Sete Rios', N'Covilhã'),
            ('2021-01-11', '07:20', '10:45', 150, N'Fátima', 'Castelo Branco'),
            ('2020-10-25', '16:30', '19:30', 137, 'Lisboa Oriente', N'Fátima'),
            ('2021-12-20', '07:00', '11:00', 323, 'Lisboa Sete Rios', 'Porto'),
            ('2021-05-26', '10:30', '11:30', 200, N'Parque das Nações', 'Beja')

        insert into TRANSPORTE
            (viagem, velmaxima, dataserv, atrdiscriminante)
        values
            (1, 120, '2014-02-23', 'A'),
            (3, 110, '2008-05-30', 'A'),
            (5, 200, '2001-09-13', 'C'),
            (6, 140, '2019-01-31', 'C'),
            (7, 100, '2020-08-01', 'A'),
            (8, 120, '2015-11-08', 'A'),
            (9, 100, '2014-12-05', 'A'),
            (10, 140, '2013-01-08', 'C'),
            (11, 120, '2012-02-07', 'A'),
            (12, 140, '2011-08-23', 'C'),
            (13, 110, '2004-04-28', 'A'),
            (15, 120, '2004-05-10', 'A')


        insert into RESERVA
            (DATARES, MODOPAGAMENTO, VIAGEM)
        values
            ('2021-12-22 12:35:00', 'MBWAY', 1),
            ('2021-02-04 13:35:24', 'MB', 3),
            ('2020-05-06 21:31:00', 'MB', 5),
            ('2021-07-02 01:35:09', 'CC', 6),
            ('2020-01-20 13:13:06', 'CC', 7),
            ('2022-12-13 19:37:00', 'CC', 8),
            ('2020-10-02 15:21:10', 'MB', 9),
            ('2021-08-21 05:45:00', 'PayPal', 10),
            ('2021-02-27 12:40:20', 'MB', 11),
            ('2022-12-30 10:40:08', 'CC', 12),
            ('2020-05-10 11:39:34', 'MBWAY', 13),
            ('2021-02-27 12:40:32', 'MBWAY', 11),
            ('2020-01-11 12:10:53', 'MB', 7),
            ('2021-12-22 11:27:23', 'MB', 1),
            ('2020-05-10 12:35:01', 'MB', 13),
            ('2021-02-03 15:00:00', 'CC', 3),
            ('2022-12-11 20:38:59', 'CC', 8),
            ('2020-10-05 15:20:23', 'PayPal', 9),
            ('2021-02-01 15:04:01', 'CC', 3),
            ('2021-08-11 05:45:03', 'MB', 10),
            ('2021-08-10 05:46:26', 'PayPal', 10),
            ('2020-01-20 14:19:46', 'CC', 7),
            ('2022-12-08 11:31:41', 'MB', 8),
            ('2020-10-06 15:24:49', 'PayPal', 9),
            ('2021-12-23 19:29:32', 'MBWAY', 1),
            ('2020-10-23 18:30:50', 'MBWAY', 5),
            ('2022-12-24 19:45:52', 'MB', 12),
            ('2020-05-09 12:33:00', 'MB', 13),
            ('2021-08-22 04:49:59', 'CC', 10),
            ('2021-08-25 05:45:55', 'MB', 10),
            ('2021-12-23 15:23:45', 'MB', 1),
            ('2020-10-04 02:20:41', 'MBWAY', 9),
            ('2021-01-07 07:27:19', 'PayPal', 12),
            ('2020-10-22 16:34:14', 'CC', 11),
            ('2021-02-01 08:20:19', 'PayPal', 2)

        insert into PAGMBWAY
            (reserva, telefone)
        values
            (1, '+351932055833'),
            (11, '+351991248123'),
            (12, '+351919813217'),
            (25, '+351998765121'),
            (26, '+351998528598'),
            (32, '+351997865233')

        insert into AUTOCARROTIPO
            (marca, modelo, nlugares)
        values
            ('Volvo', '1', 30),
            ('Volvo', '2', 34),
            ('MAN', '1', 35),
            ('Mercedes', '1', 35)

        insert into AUTOCARRO
            (matricula, transporte, datarevisao, marca, modelo)
        values
            ('AA-00-AA', 1, '2030-04-20', 'Volvo', '1'),
            ('AA-01-AA', 2, '2031-07-23','Volvo', '2'),
            ('AA-02-AA', 5, '2030-01-02', 'MAN', '1'),
            ('AA-03-AA', 6, '2034-12-30','Volvo', '1'),
            ('AA-04-AA', 7, '2030-02-05', 'Mercedes', '1'),
            ('AA-05-AA', 9, '2031-01-02', 'Mercedes', '1'),
            ('AA-06-AA', 11, '2032-09-14', 'MAN', '1')

        insert into COMBOIOTIPO
            (id, nome, nlugclasse1, nlugclasse2)
        values
            ('AP', 'alfa-pendular', 25, 50),
            ('IC', 'inter-cidades', 30, 60),
            ('IR', 'inter-regional', 10, 30),
            ('R', 'regional', 10, 70)

        insert into COMBOIO
            (transporte, tipo, ncarruagens)
        values
            (3, 'AP', 5),
            (4, 'IR', 8),
            (8, 'R', 2),
            (10, 'IC', 6),
            (12, 'IR', 7)

        insert into LOCOMOTIVA
            (nserie, comboio, marca)
        values
            (12542483, 3, 'TGV POS'),
            (78465163, 4, 'Shanghai Maglev'),
            (21879124, 8, 'Beyer Peacock'),
            (01657427, 10, 'ALC')

        insert into ALFAPENDULAR
            (nserie, comboio, numero)
        values
            (12542483, 3, 1)

        insert into LUGARTIPO
            (numero, nome, classe, preco)
        values
            (1, 'adulto', 1, 20.90),
            (2, 'jovem', 1 , 15.90),
            (3, N'sénior', 1 , 17.90),
            (4, N'criança', 1 , 10.90),
            (5, 'militar', 1 , 13.90),
            (6, 'adulto', 2, 15.99),
            (7, 'jovem', 2 , 10.99),
            (8, N'sénior', 2 , 12.99),
            (9, N'criança', 2 , 5.99),
            (10, 'militar', 2 , 8.99)

        insert into LUGAR
            (numero, transporte, tipo)
        values
            (1, 1, 1),
            (2, 1, 2),
            (3, 1, 7),
            (1, 2, 4),
            (2, 2, 8),
            (3, 2, 10),
            (1, 3, 5),
            (2, 3, 6),
            (3, 3, 1),
            (1, 4, 2),
            (2, 4, 9),
            (3, 4, 1),
            (1, 5, 4),
            (2, 5, 8),
            (3, 5, 2),
            (1, 6, 6),
            (2, 6, 10),
            (3, 6, 3),
            (1, 7, 8),
            (2, 7, 2),
            (3, 7, 5),
            (1, 8, 1),
            (2, 8, 5),
            (3, 8, 9),
            (1, 9, 4),
            (2, 9, 2),
            (3, 9, 3),
            (1, 10, 1),
            (2, 10, 10),
            (3, 10, 8)

        insert into BILHETE
            (passageiro, nlugar, transporte, tipolugar, reserva)
        values
            ('302449302', 1, 1, 1, 5),
            ('I19F026830', 2, 1, 2, 3),
            ('547632522', 3, 1, 7, 1),
            ('548912316', 1, 2, 4, 20),
            ('J29SSS2999', 2, 2, 8, 22),
            ('554813200', 3, 2, 10, 21),
            ('876566769', 1, 3, 5, 25),
            ('KS992KS29L', 2, 3, 6, 7),
            ('7823JDJJ20', 3, 3, 1, 3),
            ('937138238', 1, 4, 2, 9),
            ('765642744', 2, 4, 9, 2),
            ('017060752', 3, 4, 1, 3),
            ('557987522', 1, 5, 4, 13),
            ('897674234', 2, 5, 8, 15),
            ('KSF8812386', 3, 5, 2, 18),
            ('PT1288JDSF', 1, 6, 6, 23),
            ('684961200', 2, 6, 10, 19),
            ('237459762', 3, 6, 3, 18),
            ('231864231', 1, 7, 8, 16),
            ('168743212', 2, 7, 2, 11),
            ('4794521DI3', 3, 7, 5, 10),
            ('ODG94821KF', 1, 8, 1, 20),
            ('974010713', 2, 8, 5, 1),
            ('129874516', 3, 8, 9, 5),
            ('132465322', 1, 9, 4, 3),
            ('291097654', 2, 9, 2, 9),
            ('L8KF54521F', 3, 9, 3, 8),
            ('234GFDHD75', 1, 10, 1, 14),
            ('36874DS32F', 2, 10, 10, 6),
            ('46WD45SDS3', 3, 10, 8, 5),
            ('46WD45SDS3', 3, 10, 8, 6),
            ('132465322', 1, 1, 1, 3)
    commit
end try
begin catch
    rollback transaction
    select error_message() as ErrorMessage,
           error_severity() as ErrorSeverity,
           error_line() as ErrorLine
end catch