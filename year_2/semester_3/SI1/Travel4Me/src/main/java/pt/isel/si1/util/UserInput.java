package pt.isel.si1.util;

import java.util.Arrays;
import java.util.Scanner;

public class UserInput {
	private final String values;
	private final String[] attr;
	private final int numAttr;

	private static final Scanner in = new Scanner (System.in);

	public UserInput() {
		values = in.nextLine();
		attr = values.split("[,;.]+");
		numAttr = attr.length;
	}

	public UserInput(int numAttr) {
		values = in.nextLine();
		String[] temp = values.split("[,;.]+");
		attr = Arrays.copyOfRange(temp, 0, numAttr);
		this.numAttr = numAttr;
	}

	public String value(int index) {
		return attr[index];
	}

	public String getValues() {
		return values;
	}

	public int numAttr() {
		return attr.length;
	}

	public String[] getAttr() {
		return attr;
	}

	public int getNumAttr() {
		return numAttr;
	}
}
