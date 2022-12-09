package edu.isel.lic.peripherals.lcd;

import java.util.ArrayList;

public class CustomCharacter {

	ArrayList<Integer> char_pattern = new ArrayList<Integer>();

	private final int ddram_addr, fontHeight;
	private static int numOfChars = 0;
	private static final int MAX_5x8_numOfChars = 8, MAX_5x10_numOfChars = 4;

	public CustomCharacter (int[] pattern_array) {
		for (int index : pattern_array)
				this.char_pattern.add(index);

		ddram_addr = numOfChars; // Font=5x8 -> cgram_addr={0,1,2,3,4,5,6,7} | Font=5x10 -> cgram_addr={0,1,2,3}
		fontHeight = pattern_array.length; // Font=5x8 -> fontHeight=8 | Font=5x10 -> fontHeight=10
		numOfChars++;

		// Algoritmo para dar ciclo aos endereços na CGRAM caso encher até ao fim o espaço disponível
		if (fontHeight == 8)
			numOfChars = (numOfChars == MAX_5x8_numOfChars) ? 0 : numOfChars++;
		else
			numOfChars = (numOfChars == MAX_5x10_numOfChars) ? 0 : numOfChars++;
		}

	public static void add (CustomCharacter char_object) {

		for (int i = 0; i < char_object.fontHeight; ++i) {
			LCD.writeCMD(LCDCode.set_cgram_address(char_object.ddram_addr * char_object.fontHeight + i));
			LCD.writeDATA(char_object.char_pattern.get(i));
		}
		LCD.writeCMD(LCDCode.set_ddram_address(0));
	}

	public int getAddr () {
		return ddram_addr;
	}
}
