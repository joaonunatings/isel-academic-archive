package pt.isel.si1.jdbc;

import pt.isel.si1.model.Reserva;
import pt.isel.si1.model.Viagem;
import pt.isel.si1.util.UserInput;

import java.sql.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class JDBC {
    private static final String DEFAULT_IP = "localhost";
    private static final int DEFAULT_PORT = 1433;
    private static final String DEFAULT_USER = "sa";
    private static final String DEFAULT_PASSWORD = "";

    private static Connection con;

    public static void main(String[] args) throws Exception {
        String ip = (System.getenv("T4M_IP") != null) ? System.getenv("T4M_IP") : DEFAULT_IP;
        int port = DEFAULT_PORT;
        if (System.getenv("T4M_PORT") != null) {
            try {
                port = Integer.parseInt(System.getenv("T4M_PORT"));
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port: " + DEFAULT_PORT);
            }
        }
        String user = DEFAULT_USER;
        if (System.getenv("T4M_USER") != null) {
            user = System.getenv("T4M_USER");
        }
        String password = DEFAULT_PASSWORD;
        if (System.getenv("T4M_PASSWORD") != null) {
            password = System.getenv("T4M_PASSWORD");
        }
        String database = "";
        if (System.getenv("T4M_DATABASE") == null) {
            System.err.println("No database specified. Please provide it in the DATABASE environment variable. Exiting...");
            System.exit(1);
        } else {
            database = System.getenv("T4M_DATABASE");
        }
        String url = "jdbc:sqlserver://" + ip + ':' + port + ";user=" + user + ";password=" + password + ";database=" + database + ";TrustServerCertificate=true;";
        App.getInstance().setConnectionString(url);
        App.getInstance().Login();
        con = App.getConnection();
        CheckIntegrityRestrictions();
        App.getInstance().Run();
    }

    protected static void CheckIntegrityRestrictions() throws SQLException {
        UpdateBusOnDeleteCascade();
        SetArrivalTimes();
        CreateMBWayPaymentIfNeeded();
    }

    protected static void UpdateBusOnDeleteCascade() throws SQLException {
        String remove_constraint = "alter table AUTOCARRO drop constraint FK_AUTOCARRO_TRANSPORTE";
        String add_constraint = "alter table AUTOCARRO add constraint FK_AUTOCARRO_TRANSPORTE foreign key (transporte)" +
                " references TRANSPORTE (ident) on delete cascade";

        con.setAutoCommit(false);
        Statement stmt = con.createStatement();
        try {
            stmt.execute(remove_constraint);
            stmt.execute(add_constraint);
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
        }

        stmt.close();
        con.setAutoCommit(true);
    }

    /**
     * Check if Table exists from String.
     */
    protected static boolean CheckIfTableExists(String tableName) throws SQLException {
        DatabaseMetaData dbm = con.getMetaData();
        ResultSet rs = dbm.getTables(null, null, tableName, null);
        return rs.next();
    }

    /**
     * Handles first restriction
     * Não são vendidos mais bilhetes que os permitidos no meio de transporte escolhido.
     */
    public static boolean AreTicketsAvailableFor(int transporte) throws Exception {
        String query = "select B.transporte, count(B.transporte) nbilhetes, nlugares\n" +
                "from BILHETE B, AUTOCARRO A, AUTOCARROTIPO AT\n" +
                "where B.transporte = A.transporte and A.marca = AT.marca and A.modelo = AT.modelo and B.transporte = ?\n" +
                "group by B.transporte, nlugares\n" +
                "union\n" +
                "select B.transporte, count(B.transporte) nbilhetes, (nlugclasse1 + nlugclasse2) nlugares\n" +
                "from BILHETE B, COMBOIO CB, COMBOIOTIPO CT\n" +
                "where B.transporte = CB.transporte and CB.tipo = CT.id and B.transporte = ?\n" +
                "group by B.transporte, (nlugclasse1 + nlugclasse2)";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setInt(1, transporte);
        pstmt.setInt(2, transporte);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            if (rs.getInt(2) >= rs.getInt(3)) {
                System.err.println("Transporte cheio.");
                return false;
            }
        }
        
        pstmt.close();
        rs.close();
        return true;
    }

    /**
     * Handles second restriction
     * O tempo de chegada é igual ao estipulado (definido no campo "horachegada")
     * a partir da distância e da velocidade máxima do meio de transporte.
     */
    protected static void SetArrivalTimes() throws SQLException {
        String query = "select VIAGEM.ident, horapartida, distancia, velmaxima from VIAGEM\n" +
                "join TRANSPORTE T on VIAGEM.ident = T.viagem";
        PreparedStatement pstmt = con.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            String toUpdate = CalculateArrivalTime(rs.getTime(2), rs.getInt(3), rs.getInt(4));
            UpdateArrivalTime(rs.getInt(1), toUpdate);
        }

        pstmt.close();
        rs.close();
    }

    /**
     * Atualiza o tempo de uma única viagem
     */
    protected static void SetArrivalTime(int id) throws SQLException {
        String query = "select VIAGEM.ident, horapartida, distancia, velmaxima from VIAGEM\n" +
                "join TRANSPORTE T on VIAGEM.ident = T.viagem\n" +
                "where VIAGEM.ident = ?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setInt(1, id);

        ResultSet rs = pstmt.executeQuery();


        while (rs.next()) {
            String toUpdate = CalculateArrivalTime(rs.getTime(2), rs.getInt(3), rs.getInt(4));
            UpdateArrivalTime(rs.getInt(1), toUpdate);
        }

        pstmt.close();
        rs.close();
    }

    /**
     * Calculates arrival time from departure time, distance and speed it travels
     */
    protected static String CalculateArrivalTime(Time time, double distance, double speed) {

        double avgTime = distance / speed;

        double seconds = avgTime * Duration.ofHours(1).toMinutes() * 60;
        Duration dur = Duration.ofSeconds(Math.round(seconds));

        LocalTime timeToAdd = LocalTime.parse(time.toString());

        timeToAdd = timeToAdd.plusHours(dur.toHours());
        timeToAdd = timeToAdd.plusMinutes(dur.toMinutes());
        timeToAdd = timeToAdd.plusSeconds(dur.toMinutes() * 60);

        return timeToAdd.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    /**
     * Updates arrival time in the database
     */
    protected static void UpdateArrivalTime(int ident, String time) {
        String query = "update VIAGEM\n" +
                "set horachegada = ?\n" +
                "where ident = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, time);
            pstmt.setInt(2, ident);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ignored) {}
    }

    /**
     * Handles third restriction
     * Todas as reservas realizadas e cujo modo de pagamento foi por "MBWay" estão
     * registadas na tabela PAGMBWAY.
     */
    protected static void CreateMBWayPaymentIfNeeded() throws SQLException {
        String query = "select reserva, ident from RESERVA\n" +
                "left join PAGMBWAY P on RESERVA.ident = P.reserva\n" +
                "where modopagamento = 'MBWAY'";
        PreparedStatement pstmt = con.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            rs.getInt(1);
            if (rs.wasNull())
                RegisterPhoneNumber(rs.getInt(2));
        }

        pstmt.close();
        rs.close();
    }

    /**
     * Registers a phone number to a Booking which is not associated to a PAGMBWAY's entry.
     */
    protected static void RegisterPhoneNumber(int ident) throws SQLException {
        String query = "insert into PAGMBWAY\n" +
                "(reserva, telefone)\n" +
                "values\n" +
                "(?, ?)";
        PreparedStatement pstmt = con.prepareStatement(query);
        System.out.format("Insira o número de telefone(formato: +351999999999) para a reserva %d:\n", ident);
        UserInput input = new UserInput(1);
        pstmt.setInt(1, ident);
        pstmt.setString(2, input.value(0));
        pstmt.execute();
        System.out.println("Telefone adicionado.\n");

        pstmt.close();
    }

    /**
     * Adds a new Booking's entry.
     */
    protected static void AddReservation() throws SQLException {
        //Input de utilizador
        System.out.println("Inserir dados da reserva separados por vírgula\nFormato: " + Reserva.user_values);
        UserInput input = new UserInput();
        Reserva reserva = new Reserva(input.getAttr());

        //Verificar se existe viagem para a reserva
        String query = "select ident from Viagem where ident = ?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setInt(1, Integer.parseInt(input.value(2)));
        ResultSet rs = pstmt.executeQuery();

        if (!rs.next()) {
            System.err.println("Não existe viagem para a reserva selecionada");
            return;
        }

        //Adicionar reserva.
        con.setAutoCommit(false);
        try {
            pstmt = con.prepareStatement(reserva.getINSERT_CMD());
            reserva.setAllStatements(pstmt);
            pstmt.execute();
            con.commit();
            System.out.println("Reserva adicionada com sucesso.");

            //Adicionar nr de telefone caso o modo de pagamento seja MBWAY.
            if (reserva.getModopagamento().equals("MBWAY"))
                RegisterPhoneNumber(GetReservationIdent(reserva.getDatares()));
        } catch (SQLException ex) {
            con.rollback();
            System.err.println("Reserva não adicionada. Erro na base de dados.");
            ex.printStackTrace();
        }

        rs.close();
        pstmt.close();
        con.setAutoCommit(true);
    }

    /**
     * Obtains ident from Booking through the booking date.
     */
    protected static int GetReservationIdent(String datares) throws SQLException {
        int toReturn = 0;

        String Query = "select ident from Reserva\n" +
                "where datares = ?";
        PreparedStatement pstmt = con.prepareStatement(Query);
        pstmt.setString(1, datares);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next())
            toReturn = rs.getInt(1);

        rs.close();
        pstmt.close();
        return toReturn;
    }

    /**
     * Updates Trip and auto sets arrival time.
     */
    protected static void UpdateTrip() throws SQLException {
        System.out.println("Inserir ID da viagem que pretende atualizar: ");
        UserInput input = new UserInput();

        String query = "select * from Viagem where ident = ?";

        PreparedStatement pstmt = con.prepareStatement(query);
        int id = Integer.parseInt(input.value(0));
        pstmt.setInt(1, id);

        ResultSet rs = pstmt.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("\nID inválido ou inexistente.");
            return;
        }

        //Apresenta os resultados da viagem
        System.out.println("Dados atuais: ");
        App.printResults(rs);

        System.out.println("Inserir dados da viagem separados por vírgula.\nFormato: " + Viagem.user_values);
        input = new UserInput();
        Viagem viagem = new Viagem(input.getAttr());

        //Atualiza os valores da viagem.
        con.setAutoCommit(false);
        try {
            query = "UPDATE VIAGEM\n" +
                    "SET dataviag = ?, horapartida = ?, distancia = ?, estpartida = ?, estchegada = ?\n" +
                    "where ident = ?";
            pstmt = con.prepareStatement(query);
            viagem.setAllStatements(pstmt, id);
            pstmt.executeUpdate();
            con.commit();

            //Atualiza o tempo de chegada.
            SetArrivalTime(id);
            System.out.println("\nViagem atualizada com sucesso.");
        } catch (SQLException ex) {
            System.err.println("\nErro na base de dados.");
            ex.printStackTrace();
            con.rollback();
        }

        con.setAutoCommit(true);
        pstmt.close();
        rs.close();
    }

    /**
     * Removes Bus from all the bookings and creates a table for it if it doesn't exist already.
     */
    protected static void OutofServiceBus() throws SQLException {

        System.out.println("Indique a matrícula do autocarro para inutilizar \nFormato: AA-00-AA:");
        UserInput input = new UserInput();

        String query = "select matricula, datarevisao, marca, modelo, transporte from Autocarro where matricula = ?";

        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, input.value(0));

        ResultSet rs = pstmt.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("\nMatrícula inválida ou inexistente.");
            return;
        }

        //Elimina todas as relações que o autocarro possa ter, desde Bilhete a Viagens.
        query = "delete from TRANSPORTE where ident = ?";
        pstmt = con.prepareStatement(query);
        rs.next();
        pstmt.setInt(1, rs.getInt(5));
        pstmt.executeUpdate();
        System.out.println("Viagens do autocarro eliminado.");

        //Cria a tabela AutocarroParado caso não exista.
        if (!CheckIfTableExists("AUTOCARROPARADO")) {
            query = "create table AUTOCARROPARADO (\n" +
                    "matricula nchar(8),\n" +
                    "datarevisao date not null,\n" +
                    "marca nchar(10) not null,\n" +
                    "modelo nchar(6) not null,\n" +
                    "constraint PK_AUTOCARROPARADO primary key (matricula),\n" +
                    "constraint AUTOCARROPARADO_MATRICULA check (matricula like ('[A-Z][A-Z]-[0-9][0-9]-[A-Z][A-Z]')),\n" +
                    "constraint AUTOCARROPARADO_DATAREVISAO check (datarevisao > cast(getdate() as date)),\n" +
                    "constraint FK_AUTOCARROPARADO_MARCA_MODELO foreign key (marca, modelo) references AUTOCARROTIPO (marca, modelo) on delete cascade)";
            pstmt = con.prepareStatement(query);
            pstmt.executeUpdate();
        }

        InsertStoppedBus(rs);
        System.out.println("Autocarro inserido na tabela AUTOCARROPARADO.");

        pstmt.close();
        rs.close();
    }

    /**
     * Inserts new stopped bus into AUTOCARROPARADO table.
     */
    protected static void InsertStoppedBus(ResultSet rs) throws SQLException {
        String query = "Insert into AUTOCARROPARADO\n" +
                "(matricula, datarevisao, marca, modelo)\n" +
                "values\n" +
                "(?,?,?,?)";
        PreparedStatement pstmt = con.prepareStatement(query);
        for (int i = 1; i <= 4; i++) {
            if (i == 2)
                pstmt.setDate(i, rs.getDate(i));
            else
                pstmt.setString(i, rs.getString(i));
        }
        pstmt.executeUpdate();

        pstmt.close();
    }

    /**
     * Check for total mileage in given bus
     */
    protected static void GetBusMileage() throws SQLException {
        System.out.println("Indique a matrícula do autocarro que pretende consultar \nFormato: AA-00-AA:");
        UserInput input = new UserInput();
        int mileage = 0;
        String query = "select R.viagem\n" +
                "from RESERVA R join TRANSPORTE T on R.viagem = T.viagem, AUTOCARRO A\n" +
                "where R.datares < getdate() and A.transporte = T.ident and A.matricula = ?\n" +
                "order by R.viagem";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, input.value(0));
        ResultSet rs = pstmt.executeQuery();
        int viagem, temp_viagem, distance = 0;
        temp_viagem = -1;

        // Verificar se existe autocarro
        if (!rs.isBeforeFirst()) {
            System.err.println("Não encontrado.");
            return;
        }

        // Iterar sobre cada viagem e obter a sua distância e somar à quilometragem total
        while (rs.next()) {
            viagem = rs.getInt(1);
            if (viagem != temp_viagem) {
                ResultSet distance_rs;

                String distance_query = " select distancia from VIAGEM where ident = ?";
                pstmt = con.prepareStatement(distance_query);
                pstmt.setInt(1, viagem);
                distance_rs = pstmt.executeQuery();

                if (distance_rs.next()) distance = distance_rs.getInt(1);
                distance_rs.close();
                temp_viagem = viagem;
            }
            mileage += distance;
        }

        rs.close();
        System.out.println("O autocarro com matrícula " + input.value(0) + " já percorreu " + mileage + "km.");
        pstmt.close();
    }

    /**
     * Shows user how many empty seats are in given bus
     */
    protected static void EmptySeatsBusFrom() throws SQLException {
        System.out.println("Insira localidade de partida de autocarro");
        UserInput input = new UserInput(1);

        String query = "select A.matricula, T.ident transporte, T.viagem, (AT.nlugares - count(*)) nlugaresvazios\n" +
                "from BILHETE B, TRANSPORTE T, (AUTOCARRO A join AUTOCARROTIPO AT on A.marca = AT.marca and A.modelo = AT.modelo)\n" +
                "where T.ident = A.transporte and B.transporte = T.ident\n" +
                "group by A.matricula, T.ident, T.viagem, AT.nlugares\n" +
                "having T.viagem in (select V.ident\n" +
                "from (VIAGEM V join ESTACAO E on V.estpartida = E.nome) join LOCALIDADE L on L.codpostal = E.localidade\n" +
                "where L.nome = ?)";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, input.value(0));
        ResultSet rs = pstmt.executeQuery();

        if (!rs.isBeforeFirst()) {
            System.err.println("Não encontrado.");
            return;
        }
        while (rs.next()) {
            System.out.println("Autocarro " + rs.getInt(2) + " com matrícula " + rs.getString(1) + " com " +
                    rs.getInt(4) + " lugares vazios.");
        }

        rs.close();
        pstmt.close();
    }

    /**
     * Shows user sum of price within given category
     */
    protected static void PriceSumByCategory() throws SQLException {
        System.out.println("Insira a categoria");
        UserInput input = new UserInput(1);

        String query = "select sum(preco) as precoTotalAdulto from BILHETE B, LUGARTIPO L\n" +
                "where B.tipolugar = L.numero and L.nome = ?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, input.value(0));
        ResultSet rs = pstmt.executeQuery();

        int totalPrice;
        while (rs.next()) {
            totalPrice = rs.getInt(1);
            if (rs.wasNull()) {
                System.err.println("Não encontrado.");
                return;
            }
            System.out.println("Preço total para a categoria " + input.value(0) + ": " + totalPrice +
                    "€");
        }

        rs.close();
        pstmt.close();
    }

    /**
     * Shows age average in given reservatiom
     */
    protected static void AgeAverageByReservation() throws SQLException {
        System.out.println("Insira o identificador da reserva");
        UserInput input = new UserInput(1);

        String query = "select reserva, datares, avg(datediff(year, dtnascimento, getdate())) as mediaIdade\n" +
                "from BILHETE\n" +
                "join PASSAGEIRO P on P.nid = BILHETE.passageiro\n" +
                "join RESERVA R2 on R2.ident = BILHETE.reserva\n" +
                "where R2.ident = ?\n" +
                "group by reserva, datares";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setInt(1, Integer.parseInt(input.value(0)));
        ResultSet rs = pstmt.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.err.println("Não encontrado.");
            return;
        }
        App.printResults(rs);

        rs.close();
        pstmt.close();
    }

    /**
     * Displays running trips at given time interval, departure and arrival place
     */
    protected static void RunningTrips() throws SQLException {
        System.out.println("Inserir localidade de partida e de chegada\nFormato: localidade partida;localidade chegada");
        UserInput localidades = new UserInput(2);
        System.out.println("Inserir período de tempo\nFormato: 06:00;12:00 (início;fim)");
        UserInput time_interval = new UserInput(2);

        String query = "select *\n" +
                "from VIAGEM V\n" +
                "where estpartida in (\n" +
                "select E.nome\n" +
                "from ESTACAO E join LOCALIDADE L on E.localidade = L.codpostal\n" +
                "where L.nome = ? and (V.horapartida between ? and ?))\n" +
                "intersect\n" +
                "select *\n" +
                "from VIAGEM V\n" +
                "where estchegada in (\n" +
                "select E.nome\n" +
                "from ESTACAO E join LOCALIDADE L on E.localidade = L.codpostal\n" +
                "where L.nome = ? and (V.horachegada between ? and ?))";

        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, localidades.value(0));
        pstmt.setString(2, time_interval.value(0));
        pstmt.setString(3, time_interval.value(1));
        pstmt.setString(4, localidades.value(1));
        pstmt.setString(5, time_interval.value(0));
        pstmt.setString(6, time_interval.value(1));
        ResultSet rs = pstmt.executeQuery();

        if (!rs.isBeforeFirst()) {
            System.err.println("Não encontrado.");
            return;
        }
        App.printResults(rs);

        rs.close();
        pstmt.close();
    }

    /**
     * Displays age average by given payment
     */
    protected static void AgeAverageByPaymentType() throws SQLException {
        System.out.println("Insira tipo de pagamento");
        UserInput input = new UserInput(1);

        String query = "select R.modopagamento, avg(datediff(year, P.dtnascimento, getdate())) as idade\n" +
                "from RESERVA R, BILHETE B, PASSAGEIRO P\n" +
                "where R.ident = B.reserva and B.passageiro = P.nid and R.modopagamento = ?\n" +
                "group by R.modopagamento";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, input.value(0));
        ResultSet rs = pstmt.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.err.println("Não encontrado.");
            return;
        }
        while (rs.next()) {
            System.out.println("Média de idades para pagamento com " + input.value(0) + ": " + rs.getInt(2));
        }

        rs.close();
        pstmt.close();
    }
}