package edu.isel.lic.test;

import edu.isel.lic.link.HAL;
import edu.isel.lic.link.SerialEmitter;
import edu.isel.lic.link.sound.SGCode;
import edu.isel.lic.link.sound.SoundGenerator;

public class SoundGeneratorTest extends Test {
	private static final Test setVolume_test = new Test ("setVolume()",     true);
	private static final Test play_test = new Test ("play()",               true);

	public static void main (String[] args) {
		init();

		if (setVolume_test.run)
			setVolume_test();
		if (play_test.run)
			play_test();
		}

	private static void setVolume_test() {
		setVolume_test.printBegin();

		print("1: Insira um valor de 0-3 para definir o volume: ");
		int volume = readInput();

		print("2: Definir o volume a " + volume + ".");
		switch (volume) {
			case 0:
				SoundGenerator.setVolume(SGCode.Volume.MUTE);
				break;
			case 1:
				SoundGenerator.setVolume(SGCode.Volume.LOW);
				break;
			case 2:
				SoundGenerator.setVolume(SGCode.Volume.MED);
				break;
			case 3:
				SoundGenerator.setVolume(SGCode.Volume.HIGH);
				break;
		}
		waitInput();

		printEnd();
	}

	private static void play_test () {
		play_test.printBegin();

		print("1: Tocar o primeiro som.");
		waitInput();
		SoundGenerator.play(SGCode.Sound.GAME_OVER);

		print("Parar o som.");
		waitInput();
		SoundGenerator.stop();

		print("2: Tocar o segundo som.");
		waitInput();
		SoundGenerator.play(SGCode.Sound.SOUND1);

		print("Parar o som.");
		waitInput();
		SoundGenerator.stop();

		print("3: Tocar o terceiro som.");
		waitInput();
		SoundGenerator.play(SGCode.Sound.SOUND2);

		print("Parar o som.");
		waitInput();
		SoundGenerator.stop();

		print("3: Tocar o quarto som.");
		waitInput();
		SoundGenerator.play(SGCode.Sound.SOUND3);

		print("Parar o som.");
		waitInput();
		SoundGenerator.stop();

		printEnd();
	}

	private static void init () {
		HAL.init();
		SerialEmitter.init();
		SoundGenerator.init();
	}
}
