package edu.isel.lic.link;

import isel.leic.*;
import isel.leic.utils.Time;

import java.util.Scanner;

public class HAL // Virtualiza o acesso ao sistema UsbPort
{
	public static int input, output;
	public static final int MAX_BITS = 0xFF;
	private static final boolean ULICX = false; // mudar para true caso estiver a ser usado uLICx

	// Inicia a classe
	public static void init() {
		clrBits(MAX_BITS);
	}

	// Retorna true se o bit tiver o valor lógico ‘1’
	public static boolean isBit(int mask) {
		return (mask == readBits(mask));
	}

	// Retorna os valores dos bits representados por mask presentes no UsbPort
	public static int readBits(int mask) {
		getInput();
		return mask & input;
	}

	// Escreve nos bits representados por mask o valor de value
	public static void writeBits(int mask, int value) {
		output = mask & value | ~mask & output;
		updateOutput();
	}

	// Coloca os bits representados por mask no valor lógico ‘1’
	public static void setBits(int mask) {
		output = output | mask;
		updateOutput();
	}

	// Coloca os bits representados por mask no valor lógico ‘0’
	public static void clrBits(int mask) {
		output = output & ~mask;
		updateOutput();
	}

	// Atualiza a saída no UsbPort com o valor da variável output
	private static void updateOutput() {
		UsbPort.out(ULICX ? output : ~output);
	}

	// Atualiza a variável input com a entrada do UsbPort
	private static void getInput() {
		input = (ULICX) ? UsbPort.in() : ~UsbPort.in();
	}
}
