package edu.isel.lic.test;

import edu.isel.lic.link.HAL;
import edu.isel.lic.link.SerialEmitter;
import edu.isel.lic.peripherals.lcd.LCD;
import edu.isel.lic.peripherals.lcd.LCDCode;
import isel.leic.utils.Time;

public class LCDTest extends Test {
	private static final Test moveCursor_test = new Test ("moveCursor", true);
	private static final Test write_test = new Test ("write()",         true);

	public static void main (String[] args) {
		init();

		if (moveCursor_test.run)
			moveCursor_test();
		if (write_test.run)
			write_test();
	}

	private static void moveCursor_test() {
		moveCursor_test.printBegin();
		LCD.clear();

		int cycle_time = 200;
		print("1: Mover o cursor usando o método cursor(): ");
		waitInput();
		for (int lin = 0; lin < LCD.LINES; ++lin)
			for (int col = 0; col < LCD.COLS; ++col) {
				LCD.cursor(lin, col);
				Time.sleep(cycle_time);
			}

		print("2: Mover o cursor usando o comando do LCD - cursor shift: ");
		waitInput();
		LCD.cursor(0,0);
		for (int lin = 0; lin < LCD.LINES; ++lin)
			for (int col = 0; col < LCD.COLS; ++col) {
				LCD.writeCMD(LCDCode.cursor_or_display_shift(false,true));
				Time.sleep(cycle_time);
				if (col == 15 && lin == 0)
					LCD.cursor(1,0);
			}
		waitInput();

		printEnd();
	}

	private static void write_test() {
		write_test.printBegin();
		LCD.clear();

		print("1: Escrever um texto para mostrar no LCD: ");
		String str = in.nextLine();

		print("2: Mostrar " + str + " no LCD.");
		LCD.write(str);
		waitInput();

		LCD.clear();
		print("3: Escrever um caráter para mostrar no LCD: ");
		String c_str = in.nextLine();
		char c = c_str.charAt(0);
		waitInput();

		print("4: Mostrar " + c + " no LCD.");
		LCD.write(c);
		waitInput();

		LCD.clear();
		print("5: Mostrar caráter customizado (nave) no LCD.");
		LCD.write(LCD.spaceship);
		waitInput();

		printEnd();
	}

	private static void init() {
		HAL.init();
		SerialEmitter.init();
		LCD.init();
		LCD.writeCMD(LCDCode.display_control(true,true,false));
	}
}
