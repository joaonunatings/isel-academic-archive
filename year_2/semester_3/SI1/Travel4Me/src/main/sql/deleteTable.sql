/**
 * Sistemas de Informação I
 * Segunda fase do trabalho prático
 * Semestre de Verão 2020-2021
 *
 * Ficheiro deleteTable.sql
 * Elimina data de todas as tabelas por ordem, respeitando cada integridade referencial.
 * A linha 'where 1=1' serve para supressar qualquer aviso do programa.
 *
 * Autores: João Nunes (47220) e Alexandre Silva (47192)
 */

begin try
    begin transaction

        delete from BILHETE where 1=1
        delete from LUGAR where 1=1
        delete from LUGARTIPO where 1=1
        delete from ALFAPENDULAR where 1=1
        delete from LOCOMOTIVA where 1=1
        delete from COMBOIO where 1=1
        delete from COMBOIOTIPO where 1=1
        delete from AUTOCARRO where 1=1
        delete from AUTOCARROTIPO where 1=1
        delete from PAGMBWAY where 1=1
        delete from RESERVA where 1=1
        delete from TRANSPORTE where 1=1
        delete from VIAGEM where 1=1
        delete from ESTACAO where 1=1
        delete from LOCALIDADE where 1=1
        delete from PASSAGEIRO where 1=1

    commit
end try
begin catch
    rollback transaction
    select error_message() as ErrorMessage,
           error_severity() as ErrorSeverity,
           error_line() as ErrorLine
end catch

