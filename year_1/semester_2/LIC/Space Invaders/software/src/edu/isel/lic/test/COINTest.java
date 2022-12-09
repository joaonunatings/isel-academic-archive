package edu.isel.lic.test;

import edu.isel.lic.link.COIN;
import edu.isel.lic.link.HAL;

public class COINTest extends Test {
	private static final Test buttonPress_test = new Test ("buttonPress()", true);
	private static final Test coinCounter_test = new Test ("coinCounter",   true);

	public static void main (String[] args) {
		init();

		if (buttonPress_test.run)
			buttonPress_test();
		if (coinCounter_test.run)
			coinCounter_test();
	}

	private static void buttonPress_test () {
		buttonPress_test.printBegin();

		print("1: O próximo loop espera pelo botão de inserir moeda.");
		waitInput();

		boolean coinInserted = false;
		do {
			if (COIN.buttonPress())
				coinInserted = true;
		} while (!coinInserted);
		print("2: Foi inserida uma moeda.");
		waitInput();

		printEnd();
	}

	private static void coinCounter_test() {
		coinCounter_test.printBegin();

		print("1: O próximo loop vai contar quantas moedas foram inseridas e vai parar quando chegarem a 10 moedas.");
		waitInput();

		int coins = 0;
		do {
			if (COIN.buttonPress()) {
				coins++;
				System.out.println("Moedas: " + coins);
			}
		} while (coins <= 10);

		printEnd();
	}

	private static void init() {
		HAL.init();
		COIN.init();
	}
}
