package pt.isel.si1.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Viagem {

    private String dataviag;
    private String horapartida ;
    private String horachegada;
    private int distancia;
    private String estpartida;
    private String estchegada;

    private final String INSERT_CMD = "insert into VIAGEM\n" +
            "(DATAVIAG, HORAPARTIDA, DISTANCIA, ESTPARTIDA, ESTCHEGADA)\n" +
            "values\n" +
            "(?,?,?,?,?,?)";

    public static String user_values =
            "dataviag,horapartida,distancia,estpartida,estchegada";

    public Viagem() { }

    public Viagem(String[] attr) {
        setDataviag(attr[0]);
        setHorapartida(attr[1]);
        //setHorachegada(attr[2]);
        setDistancia(attr[2]);
        setEstpartida(attr[3]);
        setEstchegada(attr[4]);
    }

    public void setAllStatements(PreparedStatement pstmt, int id) throws SQLException{
        pstmt.setString(1, dataviag);
        pstmt.setString(2, horapartida);
        pstmt.setInt(3, distancia);
        pstmt.setString(4, estpartida);
        pstmt.setString(5, estchegada);
        pstmt.setInt(6, id);
    }

    public String getDataviag() {
        return dataviag;
    }

    public void setDataviag(String dataviag) {
        this.dataviag = dataviag;
    }

    public String getHorapartida() {
        return horapartida;
    }

    public void setHorapartida(String horapartida) {
        this.horapartida = horapartida;
    }

    public String getHorachegada() {
        return horachegada;
    }

    public void setHorachegada(String horachegada) {
        this.horachegada = horachegada;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) { this.distancia = Integer.parseInt(distancia); }

    public String getEstpartida() {
        return estpartida;
    }

    public void setEstpartida(String estpartida) { this.estpartida = estpartida; }

    public String getEstchegada() {
        return estchegada;
    }

    public void setEstchegada(String estchegada) {
        this.estchegada = estchegada;
    }

    public static String getUser_values() {
        return user_values;
    }

    public String getINSERT_CMD() { return INSERT_CMD; }
}