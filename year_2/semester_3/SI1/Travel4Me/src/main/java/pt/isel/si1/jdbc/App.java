package pt.isel.si1.jdbc;

import pt.isel.si1.util.DBTablePrinter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

interface Worker {
	void doWork() throws Exception;
}

public class App
{
	public enum Option {
		Unknown("Desconhecido"),
		Exit("Sair"),
		AddReservation("Adicionar reserva"),
		UpdateTrip("Atualizar viagem"),
		OutofServiceBus("Atualizar um autocarro como fora de serviço"),
		GetBusMileage("Obter quilometragem de um autocarro"),
		EmptySeatsBusFrom("Lugares vazios de autocarro"),
		PriceSumByCategory("Preço total por categoria"),
		AgeAverageByReservation("Média de idades por reserva"),
		RunningTrips("Viagens a decorrer num determinado intervalo de tempo"),
		AgeAverageByPaymentType("Mostrar média de idades por tipo de pagamento");

		private final String name;

		public String getName() {
			return name;
		}

		Option (String name) {
			this.name = name;
		}
	}

	private static App __instance = null;
	private String __connectionString;
	private static final String __appName = "Aplicação JDBC - Reservas Online (SI1 20/21 - L3NG14)\n";
	private final LinkedHashMap<Option, Worker> __dbMethods;
	private static Connection con;
	private static final Scanner in = new Scanner(System.in);

	private App() {
		__dbMethods = new LinkedHashMap<>();
		__dbMethods.put(Option.Exit, App.this::Exit);
		__dbMethods.put(Option.AddReservation, JDBC::AddReservation);
		__dbMethods.put(Option.UpdateTrip, JDBC::UpdateTrip);
		__dbMethods.put(Option.OutofServiceBus, JDBC::OutofServiceBus);
		__dbMethods.put(Option.GetBusMileage, JDBC::GetBusMileage);
		__dbMethods.put(Option.EmptySeatsBusFrom, JDBC::EmptySeatsBusFrom);
		__dbMethods.put(Option.PriceSumByCategory, JDBC::PriceSumByCategory);
		__dbMethods.put(Option.AgeAverageByReservation, JDBC::AgeAverageByReservation);
		__dbMethods.put(Option.RunningTrips, JDBC::RunningTrips);
		__dbMethods.put(Option.AgeAverageByPaymentType, JDBC::AgeAverageByPaymentType);
	}

	public static App getInstance() {
		return (__instance == null) ? __instance = new App() : __instance;
	}

	private void DisplayMenu() {
		System.out.println(__appName);
		int index = 1;
		for (Map.Entry<Option, Worker> entry : __dbMethods.entrySet()) {
			System.out.println(index + ". " + entry.getKey().name);
			index++;
		}
		System.out.print(">");
	}

	private static void clearConsole() {
		for (int y = 0; y < 25; y++)
			System.out.println("\n");
	}

	public void Login() {
		System.out.println("Connecting to database...");
		try {
			con = DriverManager.getConnection(App.getInstance().getConnectionString());
			con.setAutoCommit(true);
			System.out.println("Connected!");
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			System.err.println("Connection failed.");
		}
	}

	private void Exit() throws IOException {
		try {
			if (con != null)
				con.close();
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		System.out.println("Desconectado.");
		System.in.read();
		in.close();
		System.exit(0);
	}

	public void Run() throws Exception {
		Login();
		Option userInput;
		do {
			clearConsole();
			DisplayMenu();
			userInput = null;
			try {
				int res = in.nextInt();
				userInput = Option.values()[res];
				clearConsole();
				System.out.println(userInput.getName() + "\n");
				__dbMethods.get(userInput).doWork();
				System.in.read();
			} catch (Exception e) {
				System.err.println("Escolha inválida.");
				in.nextLine(); in.nextLine();
			}
		} while(userInput!= Option.Exit);
		Exit();
	}

	public String getConnectionString() {
		return __connectionString;
	}

	public void setConnectionString(String s) {
		__connectionString = s;
	}

	public static Connection getConnection() {
		return con;
	}

	public static void printResults(ResultSet rs) {
		//Downloaded from: https://github.com/htorun/dbtableprinter
		DBTablePrinter.printResultSet(rs);
	}
}