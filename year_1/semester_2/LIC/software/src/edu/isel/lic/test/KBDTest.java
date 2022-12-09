package edu.isel.lic.test;

import edu.isel.lic.link.HAL;
import edu.isel.lic.peripherals.KBD;
import isel.leic.utils.Time;

public class KBDTest extends Test {
	private static final Test getKey_test = new Test ("getKey()",   true);
	private static final Test waitKey_test = new Test ("waitKey()", true);
	private static final Test hardware_test = new Test ("hardware", false);

	public static void main (String[] args) {
		if (getKey_test.run)
			getKey_test();
		if (waitKey_test.run)
			waitKey_test();
		if (hardware_test.run)
			hardware_test();
	}

	private static void getKey_test() {
		getKey_test.printBegin();

		print("1: No próximo loop vai mostrar as teclas premidas, pressione '*' para sair.");
		waitInput();

		char key;
		do {
			key = KBD.getKey();
			if (key != KBD.NONE)
				print("" + key);
			Time.sleep(200);
		} while (key != '*');
		print("2: Saiu do loop.");
		waitInput();

		printEnd();
	}

	private static void waitKey_test() {
		waitKey_test.printBegin();

		print("1: No próximo loop vai mostrar as teclas premidas ou NONE caso não premir a tecla dentro do tempo dado.");
		waitInput();

		char key;
		int timeout = 10000;
		do {
			key = KBD.waitKey(timeout);
			print("" + key);
		} while (key != '*');
		print("2: Saiu do loop.");
		waitInput();

		printEnd();
	}

	private static void hardware_test() {
		hardware_test.printBegin();

		char key = KBD.getKey();
		print("1: Após pressionar duas teclas com o software desligado e voltar a ligar, a tecla presa no Key Buffer é: " + key);
		waitInput();

		key = KBD.getKey();
		print("2: A tecla presa no módulo Key Decode é: " + key);
		waitInput();

		printEnd();
	}

	private static void init() {
		HAL.init();
		KBD.init();
	}
}
