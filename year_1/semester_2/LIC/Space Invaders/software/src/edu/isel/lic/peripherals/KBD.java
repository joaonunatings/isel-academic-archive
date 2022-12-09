package edu.isel.lic.peripherals;

import edu.isel.lic.link.HAL;
import isel.leic.utils.Time;

//TODO: interlock
public class KBD // Ler teclas. Métodos retornam ‘0’..’9’,’A’..’F’ ou NONE.
{
	public static final char NONE = 0x20; // Para podermos usar as posições iniciais da CGRAM
	public static final String kbd="147*2580369#";
	public static final int Dval_MASK = 0x10, DATA_MASK = 0x0F, ACK_MASK = 0x20;

	// Inicia a classe
	public static void init() {
		HAL.clrBits(ACK_MASK);
	}

	// Retorna de imediato a tecla premida ou NONE se não há tecla premida.
	public static char getKey() {
		char key = NONE;

		if (HAL.isBit(Dval_MASK)) {
			key = kbd.charAt(HAL.readBits(DATA_MASK));
			HAL.setBits(ACK_MASK);
			//Time.sleep(1);
			HAL.clrBits(ACK_MASK);
		}

		return key;
	}

	// Retorna quando a tecla for premida ou NONE após decorrido ‘timeout’ milisegundos.
	public static char waitKey(int timeout) {
		char key;
		long starting_time = Time.getTimeInMillis(), current_time;
		boolean keyPress = false;

		do {
			current_time = Time.getTimeInMillis();
			key = getKey();
			if (key != NONE)
				keyPress = true;
		} while((current_time - starting_time < timeout) && !keyPress);

		return key;
	}
}