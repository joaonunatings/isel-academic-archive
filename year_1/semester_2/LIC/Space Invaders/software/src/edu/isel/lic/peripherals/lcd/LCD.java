package edu.isel.lic.peripherals.lcd;

import edu.isel.lic.link.SerialEmitter;
import isel.leic.utils.*;
import edu.isel.lic.link.HAL;

public class LCD // Escreve no LCD usando a interface a 4 bits.
{
	public static final int LINES = 2, COLS = 16;           // Dimensão do display.
	public static final int SERIAL_DATA_SIZE = 5;           // Dimensão da data a enviar para SDX
	public static final int RS_MASK = 0x10;                 // Máscara para selecionar entre Data/Command
	public static final int PARALLEL_ENABLE_MASK = 0x20;    // Mascara do bit de enable em modo paralelo
	private static final int DDRAM_LINE = 0x40;

	private static final int[]
			spaceship_pattern = {
			0B11110,
			0B11000,
			0B11100,
			0B11111,
			0B11100,
			0B11000,
			0B11110,
			0B00000},

			invader_pattern = {
			0B11111,
			0B11111,
			0B10101,
			0B11111,
			0B11111,
			0B10001,
			0B10001,
			0B00000};

	public static final CustomCharacter spaceship = new CustomCharacter(spaceship_pattern);
	public static final CustomCharacter invader = new CustomCharacter(invader_pattern);

	// Define se a interface com o LCD é série ou paralela
	private static final boolean SERIAL_INTERFACE = true;

	// Escreve um nibble de comando/dados no LCD em paralelo
	private static void writeNibbleParallel (boolean rs, int data) {
		HAL.setBits(rs ? RS_MASK : 0);                              //rs on/off
		HAL.setBits(data);                                          //data on/off
		HAL.setBits(PARALLEL_ENABLE_MASK);                          //enable on
		HAL.clrBits(PARALLEL_ENABLE_MASK);                     //enable off
		HAL.clrBits(HAL.MAX_BITS);                             //clear bits
	}

	// Escreve um nibble de comando/dados no LCD em série
	private static void writeNibbleSerial (boolean rs, int data) {
		data = (rs) ? (data<<1)+0x01 : data<<1;
		SerialEmitter.send(SerialEmitter.Destination.SLCD, SERIAL_DATA_SIZE, data);
	}

	// Escreve um nibble de comando/dados no LCD
	private static void writeNibble (boolean rs, int data) {
		if(SERIAL_INTERFACE)
			writeNibbleSerial(rs, data);
		else
			writeNibbleParallel(rs, data);
	}

	// Escreve um byte de comando/dados no LCD
	private static void writeByte (boolean rs, int data) {
		int lowerBits = data & 0x0F, higherBits = data>>>4;
		writeNibble(rs, higherBits); writeNibble(rs, lowerBits);
	}

	// Escreve um comando no LCD
	public static void writeCMD (int data) { writeByte (false, data); }

	// Escreve um dado no LCD
	public static void writeDATA (int data) { writeByte (true, data); }

	// Escreve um caráter na posição corrente.
	public static void write (char c) { writeDATA (c); }

	// Escreve uma string na posição corrente.
	public static void write (String txt) {
		for (int i = 0; i < txt.length(); ++i)
			write(txt.charAt(i));
	}

	// Escreve um caráter customizado na posição corrente.
	public static void write (CustomCharacter custom_char) {
		writeDATA((char)(custom_char.getAddr()));
	}

	// Envia comando para posicionar cursor (‘lin’:0..LINES-1 , ‘col’:0..COLS-1)
	public static void cursor (int lin, int col) {
		writeCMD(LCDCode.set_ddram_address(((lin * DDRAM_LINE)) | col));
	}

	// Envia comando para limpar o ecrã e posicionar o cursor em (0,0)
	public static void clear () { writeCMD (LCDCode.clear_display()); }

	// Envia a sequência de iniciação para comunicação a 4 bits.
	public static void init () {
		//init1 -> 0011 -> 0110 -> 110
		Time.sleep(20);
		writeNibble(false, 0x03);

		//init2 -> 0011 -> 0110 -> 110
		Time.sleep(5);
		writeNibble(false, 0x03);

		//init3 -> 0011-> 0110 -> 110
		Time.sleep(1);
		writeNibble(false, 0x03);

		//set 4bit mode -> 0010 -> 0100 -> 100
		writeNibble(false, 0x02);

		//number of display lines and character font -> 0010 -> 0100 -> 100 & 1000 -> 10000
		writeCMD(LCDCode.function_set(false, true, false));

		//display off -> 0000 -> 0 & 1000 -> 10000
		writeCMD(LCDCode.display_control(false, false, false));

		//display clear -> 0000 -> 0 & 0001 -> 0010 -> 10
		writeCMD(LCDCode.clear_display());

		//cursor direction and display shift mode -> 0000 -> 0 & 0110 -> 1100
		writeCMD(LCDCode.entry_mode_set(true, false));

		//display on (entire display, cursor on, cursor blinking on) -> 0000 -> 0 & 1111 -> 11110
		writeCMD(LCDCode.display_control(true, false, false));

		//Custom Characters
		CustomCharacter.add(spaceship);
		CustomCharacter.add(invader);

		cursor(0,0); //sets ddram = 0 clear();
	}
}
