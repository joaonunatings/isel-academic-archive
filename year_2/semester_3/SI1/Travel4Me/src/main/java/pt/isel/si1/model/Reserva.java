package pt.isel.si1.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Reserva {

    private String datares;
    private String modopagamento;
    private int viagem;
    private final String INSERT_CMD = "insert into RESERVA\n" +
            "(DATARES, MODOPAGAMENTO, VIAGEM)\n" +
            "values\n" +
            "(?,?,?)";

    public static String user_values =
            "datares,modopagamento,viagem";

    public Reserva() { }

    public Reserva(String[] attr) {
        setDatares(attr[0]);
        setModopagamento(attr[1]);
        setViagem(attr[2]);
    }

    public void setAllStatements(PreparedStatement pstmt) throws SQLException{
        pstmt.setString(1, datares);
        pstmt.setString(2, modopagamento);
        pstmt.setInt(3, viagem);
    }

    public String getDatares() {
        return datares;
    }

    public void setDatares(String datares) {
        this.datares = datares;
    }

    public String getModopagamento() {
        return modopagamento;
    }

    public void setModopagamento(String modopagamento) {
        this.modopagamento = modopagamento;
    }

    public int getViagem() {
        return viagem;
    }

    public void setViagem(String viagem) {
        this.viagem = Integer.parseInt(viagem);
    }

    public static String getUser_values() {
        return user_values;
    }

    public String getINSERT_CMD() { return INSERT_CMD; }
}