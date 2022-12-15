/**
 * Sistemas de Informação I
 * Segunda fase do trabalho prático
 * Semestre de Verão 2020-2021
 *
 * Ficheiro removeTable.sql
 * Remove todas as tabelas (se existirem), respeitando cada integridade referencial.
 *
 * Autores: João Nunes (47220) e Alexandre Silva (47192)
 */

begin try
    begin transaction

        drop table if exists BILHETE
        drop table if exists LUGAR
        drop table if exists LUGARTIPO
        drop table if exists ALFAPENDULAR
        drop table if exists LOCOMOTIVA
        drop table if exists COMBOIO
        drop table if exists COMBOIOTIPO
        drop table if exists AUTOCARRO
        drop table if exists AUTOCARROTIPO
        drop table if exists PAGMBWAY
        drop table if exists RESERVA
        drop table if exists TRANSPORTE
        drop table if exists VIAGEM
        drop table if exists ESTACAO
        drop table if exists LOCALIDADE
        drop table if exists PASSAGEIRO

    commit
end try
begin catch
    rollback transaction
    select error_message() as ErrorMessage,
           error_severity() as ErrorSeverity,
           error_line() as ErrorLine
end catch


