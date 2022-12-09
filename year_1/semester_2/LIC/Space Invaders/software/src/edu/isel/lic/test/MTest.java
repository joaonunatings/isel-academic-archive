package edu.isel.lic.test;

import edu.isel.lic.link.HAL;
import edu.isel.lic.link.M;

public class MTest extends Test {
	private static final Test M_test = new Test ("M_test", true);

	public static void main (String[] args) {
		init();

		if (M_test.run)
			M_test();
	}

	public static void M_test() {
		M_test.printBegin();

		print("1: O próximo loop diz se entrou no modo de manutenção.");
		waitInput();

		do {
			M.checkButton();
		} while (M.checkButton());
		print("2: Entrou no modo de manutenção.");
		waitInput();

		print("3: O próximo loop diz se saiu do modo de manutenção.");
		waitInput();

		do {
			M.checkButton();
		} while(M.checkButton());
		print("4: Saiu do modo de manutenção.");
		waitInput();

		printEnd();
	}

	private static void init() {
		HAL.init();
	}
}
