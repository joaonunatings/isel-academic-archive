package edu.isel.lic.peripherals.lcd;

import edu.isel.lic.link.HAL;

public class LCDCode // Baseado no pdf QuickReference do
{
	private static int value = 0; // valor hexadecimal do código correspondente
	private static final int
		CLEAR_DISPLAY = 0x01,
		RETURN_HOME = 0x02,
		ENTRY_MODE_SET = 0x04,
		DISPLAY_CONTROL = 0x08,
		CURSOR_DISPLAY_SHIFT = 0x10,
		FUNCTION_SET = 0x20,
		SET_CGRAM_ADDR = 0x40,
		SET_DDRAM_ADDR = 0x80;

	// Para efeitos de teste - needs work
	public static void main (String[] args) {
		HAL.init();
		LCD.init();
	}

	// Envia comando para limpar o ecrã e posicionar o cursor em (0,0)
	public static int clear_display() { return CLEAR_DISPLAY; }

	// Retorna o ecrã e o cursor para a posição original (endereço 0)
	public static int return_home() { return RETURN_HOME; }

	/**
	 * Define a direção do movimento do cursor e especifica a deslocação do ecrã
	 * @param cursor_move_to_right - true: cursor move-se para a direita; false - cursor move-se para a esquerda
	 * @param cursor_follows_display_shift - true: ecrã desloca-se com o cursor; false: ecrã não se desloca com o cursor
	 * @return - valor hexadecimal do código correspondente
	 */
	public static int entry_mode_set(boolean cursor_move_to_right, boolean cursor_follows_display_shift) {
		return value =
				ENTRY_MODE_SET +
				((cursor_move_to_right) ? 0x02 : 0) +
				((cursor_follows_display_shift) ? 0x01 : 0);
	}

	/**
	 * Liga/desliga o ecrã, liga/desliga cursor e liga/desliga cursor a piscar
	 * @param display_on - true: liga o ecrã; false - desliga o ecrã
	 * @param cursor_on - true: liga o cursor; false - desliga o cursor
	 * @param cursor_blink - true: liga o modo cursor a piscar; false - desliga o modo cursor a piscar
	 * @return - valor hexadecimal do código correspondente
	 */
	public static int display_control (boolean display_on, boolean cursor_on, boolean cursor_blink) {
		return value =
				DISPLAY_CONTROL +
				((display_on) ? 0x04 : 0) +
				((cursor_on) ? 0x02 : 0) +
				((cursor_blink) ? 0x01 : 0);
	}

	/**
	 * Define o modo de deslocação do cursor e do ecrã
	 * @param display_shift - true: o ecrã desloca-se; false: o cursor desloca-se
	 * @param shift_to_right - true: deslocamento para a direita; false - deslocamento para a esquerda
	 * @return - valor hexadecimal do código correspondente
	 */
	public static int cursor_or_display_shift (boolean display_shift, boolean shift_to_right) {
		return value =
				CURSOR_DISPLAY_SHIFT +
				((display_shift) ? 0x08 : 0) +
				((shift_to_right) ? 0x04 : 0);
	}

	/**
	 * Define a dimensão da interface, número de linhas a mostrar e a fonte das letras
	 * @param interface_data_length_8bits - true: interface a 8 bits; false: interface a 4 bits
	 * @param number_of_display_lines_2 - true: 2 linhas no ecrã; false: 1 linha no ecrã
	 * @param character_font_5x10 - true: fonte do tipo 5x10; false: fonte do tipo 5x8
	 * @return - valor hexadecimal do código correspondente
	 */
	public static int function_set (boolean interface_data_length_8bits, boolean number_of_display_lines_2, boolean character_font_5x10) {
		return value =
				FUNCTION_SET +
				((interface_data_length_8bits) ? 0x10 : 0) +
				((number_of_display_lines_2) ? 0x08 : 0) +
				((character_font_5x10) ? 0x04 : 0);
	}

	/**
	 * Defina endereço para o módulo CGRAM
	 * @param addr - endereço (6 bits)
	 * @return - valor hexadecimal do código correspondente
	 */
	public static int set_cgram_address (int addr) {
		return value =
				SET_CGRAM_ADDR + addr;
	}

	/**
	 * Define endereço para o módulo DDRAM
	 * @param addr - endereço (7 bits)
	 * @return - valor hexadecimal do código correspondente
	 */
	public static int set_ddram_address (int addr) {
		return value =
				SET_DDRAM_ADDR + addr;
	}
}
