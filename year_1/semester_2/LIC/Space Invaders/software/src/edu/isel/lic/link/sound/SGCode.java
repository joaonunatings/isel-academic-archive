package edu.isel.lic.link.sound;

public class SGCode
{
	private static int value = 0;

	private static final int
		STOP = 0x0,
		PLAY = 0x1,
		SET_SOUND = 0x2,
		SET_VOLUME = 0x3;

	public enum Sound {
		GAME_OVER(0x1),
		SOUND1(0x0),
		SOUND2(0x2),
		SOUND3(0x3);

		private final int sound;

		Sound(int sound) { this.sound = sound; }

		private int getValue() { return sound; }
	}

	public enum Volume {
		MUTE(0x0),
		LOW(0x1),
		MED(0X2),
		HIGH(0x3);

		private final int volume;

		Volume(int volume) { this.volume = volume; }

		private int getValue() { return volume; }
	}

	public static int stop() {
		return STOP;
	}

	public static int play() {
		return PLAY;
	}

	public static int set_sound (Sound sound) {
		return value =
				SET_SOUND +
				(sound.getValue() << 2);
	}

	public static int set_volume (Volume volume) {

		return value =
				SET_VOLUME +
				(volume.getValue() << 2);
	}

}
