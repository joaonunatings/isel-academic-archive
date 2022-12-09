package edu.isel.lic.test;

import java.util.Scanner;

public class Test {

	private final String nameOfTest;
	public final boolean run;

	public static final Scanner in = new Scanner(System.in);

	public Test (String nameOfTest, boolean run) {
		this.nameOfTest = nameOfTest;
		this.run = run;
	}

	public Test() {
		nameOfTest = "No such test";
		run = false;
	}

	public void printBegin () {
		System.out.println("TESTE AO MÉTODO " + nameOfTest + ":");
		in.nextLine();
	}

	public static void printEnd () {
		System.out.println("FIM DE TESTE.");
		in.nextLine();
	}

	public static void print (String string) {
		System.out.println(string);
	}

	public static void waitInput() {
		in.nextLine();
	}

	public static int readInput() {
		return in.nextInt();
	}
}
