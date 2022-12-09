package edu.isel.lic.link.sound;

import edu.isel.lic.link.SerialEmitter;

public class SoundGenerator { // Controla o Sound Generator.

	public static final int SERIAL_DATA_SIZE = 4;

	// Envia comando para reproduzir um som, com a identificação deste
	public static void play(SGCode.Sound sound) {
		SerialEmitter.send(SerialEmitter.Destination.SSC, SERIAL_DATA_SIZE, SGCode.set_sound(sound));
		SerialEmitter.send(SerialEmitter.Destination.SSC, SERIAL_DATA_SIZE, SGCode.play());
	}

	// Envia comando para parar o som
	public static void stop() {
		SerialEmitter.send(SerialEmitter.Destination.SSC, SERIAL_DATA_SIZE, SGCode.stop());
	}

	// Envia comando para definir o volume do som
	public static void setVolume(SGCode.Volume volume) {
		SerialEmitter.send(SerialEmitter.Destination.SSC, SERIAL_DATA_SIZE, SGCode.set_volume(volume));
	}

	// Inicia a classe, estabelecendo os valores iniciais.
	public static void init() {
		stop();
	}
}
