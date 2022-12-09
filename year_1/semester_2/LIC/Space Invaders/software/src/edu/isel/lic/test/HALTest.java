package edu.isel.lic.test;

import edu.isel.lic.link.HAL;

public class HALTest extends Test {
	private static final Test isBit_test = new Test ("isBit()",         true);
	private static final Test readBits_test = new Test ("readBits()",   true);
	private static final Test writeBits_test = new Test ("writeBits()", true);
	private static final Test setBits_test = new Test ("setBits()",     true);
	private static final Test clrBits_test = new Test ("clrBits()",     true);

	public static void main (String[] args) {
		init();

		if (isBit_test.run)
			isBit_test();
		if (readBits_test.run)
			readBits_test();
		if (writeBits_test.run)
			writeBits_test();
		if (setBits_test.run)
			setBits_test();
		if (clrBits_test.run)
			clrBits_test();
	}

	private static void isBit_test () {
		isBit_test.printBegin();

		print("1: Mudar qualquer bit de entrada.");
		waitInput();

		print("2: Ler bit de peso: ");
		int bit = readInput();

		boolean value = HAL.isBit(bit);
		print("3: Valor de bit de peso " + bit + ": " + value + ".");
		waitInput();

		printEnd();
	}

	private static void readBits_test () {
		readBits_test.printBegin();

		print("1: Mudar quaisquer bits de entrada.");
		waitInput();

		print("2: Ler bits a partir da máscara: ");
		int mask = readInput();

		int value = HAL.readBits(mask);
		print("3: Valor dos bits dentro da máscara (" + mask + "): " + value + ".");
		waitInput();

		printEnd();
	}

	private static void writeBits_test () {
		writeBits_test.printBegin();

		print("1: Mudar quaisquer bits de entrada.");
		waitInput();

		print("2: Ler esses bits e enviá-los para o output a partir da máscara: ");
		int mask = readInput();
		int input_value = HAL.readBits(0xFF), output_value = mask & input_value;

		print("3: Escrever no output: " + output_value + ".");
		HAL.writeBits(mask, output_value);
		waitInput();

		printEnd();
	}

	private static void setBits_test () {
		setBits_test.printBegin();

		print("1: Inserir máscara dos bits a definir com valor lógico '1': ");
		int mask = readInput();

		print("2: Mudar os bits mascarados (" + mask + ") para valor lógico '1'.");
		HAL.setBits(mask);
		waitInput();

		printEnd();
	}

	private static void clrBits_test () {
		clrBits_test.printBegin();

		print("1: Inserir máscara dos bits a definir com valor lógico '1': ");
		int mask = readInput();

		print("2: Mudar os bits mascarados (" + mask + ") para valor lógico '1'.");
		HAL.setBits(mask);
		waitInput();

		print("3: Inserir máscara dos bits a definir com o valor lógico '0': ");
		mask = readInput();

		print("4: Mudar os bits mascarados (" + mask + ") para valor lógico '0'.");
		HAL.clrBits(mask);
		waitInput();

		printEnd();
	}

	private static void init () { HAL.init(); }
}
