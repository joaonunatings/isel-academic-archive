package edu.isel.lic.game;

import edu.isel.lic.link.HAL;
import edu.isel.lic.link.SerialEmitter;
import edu.isel.lic.peripherals.KBD;
import edu.isel.lic.peripherals.lcd.CustomCharacter;
import edu.isel.lic.peripherals.lcd.LCD;
import edu.isel.lic.peripherals.lcd.LCDCode;
import isel.leic.utils.Time;
import java.util.Scanner;
import java.lang.Math;

public class TUI
{
    public static final int LINES = LCD.LINES, COLS = LCD.COLS;
    //! lcdstate
    private static final char[][] lcdState = new char[LINES][COLS];
    public static final char NONE = ' ', CUSTOM_CHAR = '^';
    public enum Format { ALIGN_CENTER, ALIGN_RIGHT, ALIGN_LEFT, SHIFT_RIGHT, SHIFT_LEFT }
    public enum Direction { RIGHT, LEFT }
    public enum Control { DISPLAY, CURSOR }

    // Usado para ativar testes específicos
    private static final boolean
            print_test = false,
            clear_test = false,
            read_test = false,
            move_test = false,
            format_test = false,
            cursor_test = false,
            keyboard_test = true;

    // Para efeitos de teste
    public static void main (String[] args) {
        HAL.init();
        KBD.init();
        SerialEmitter.init();
        LCD.init();
        init();

        Scanner in = new Scanner(System.in);

        String str1 = "String1", str2 = "String2";

        cursorControl(false, false);

        if (print_test) {
            clearAll();
            System.out.println("TESTE DO MÉTODO Print():");
            in.nextLine();

            System.out.println("1: Escreve em lcdState a str1 na posição [0][0].");
            writeArray(0, 0, str1);
            in.nextLine();

            System.out.println("2: Imprime str1 na posição [0][0].");
            print (0, 0, str1.length()-1);
            in.nextLine();

            System.out.println("3: Imprime str2 na posição [1][0].");
            print (1, 0, str2);
            in.nextLine();

            System.out.println("FIM DE TESTE.");
            clearAll();
        }

        if (clear_test) {
            clearAll();
            System.out.println("TESTE DO MÉTODO Clear():");
            in.nextLine();

            System.out.println("1: Imprime str1 na posição [0][0].");
            print(0, 0, str1);
            in.nextLine();

            System.out.println("2: Apaga str1.");
            clear(0, 0, str1.length() - 1);
            in.nextLine();

            System.out.println("3: Imprime str2 na posição [1][0].");
            print(1, 0, str2);
            in.nextLine();

            System.out.println("4: Imprime str1 na posição [1][7].");
            print(1, str2.length(), str1);
            in.nextLine();

            System.out.println("5: Apaga a 2ª linha.");
            clearLine(1);
            in.nextLine();

            System.out.println("FIM DE TESTE.");
            clearAll();
        }

        if (read_test) {
            clearAll();
            System.out.println("TESTE AO MÉTODO ReadArray():");
            in.nextLine();

            System.out.println("1: Imprime str1 na posição [0][0].");
            print(0, 0, str1);
            in.nextLine();

            System.out.println("2: Lê essa string: " + readArray(0, 0, str1.length()-1));
            in.nextLine();

            System.out.println("3: Imprime '#' na posição [1][0].");
            print(1,0, '#');
            in.nextLine();

            System.out.println("4: Lê esse char: " + readArray(1,0));
            in.nextLine();

            System.out.println("FIM DE TESTE.");
            clearAll();
        }

        if (move_test) {
            clearAll();
            System.out.println("TESTE AO MÉTODO Move():");
            in.nextLine();

            System.out.println("1: Imprime str1 na posição [0][0].");
            print(0, 0, str1);
            in.nextLine();

            System.out.println("2: Imprime str2 na posição [1][0].");
            print(1,0,str2);
            in.nextLine();

            System.out.println("3: Move a posição de str1 de [0][0] para [1][0].");
            moveText(0, 1, 0, str1.length()-1, 0);
            in.nextLine();

            System.out.println("FIM DE TESTE.");
            clearAll();
        }

        if (format_test) {
            clearAll();
            System.out.println("TESTE AO MÉTODO Format():");
            in.nextLine();

            System.out.println("1: Imprime str1 na posição [0][0].");
            print(0, 0, str1);
            in.nextLine();

            System.out.println("2: Formata str1 para alinhar ao centro da 1ª linha.");
            formatText(0, 0, str1.length()-1, Format.ALIGN_CENTER);
            in.nextLine();

            System.out.println("3: Formata str1 para alinhar à direita da 1ª linha.");
            clearLine(0);
            create_formatText(0, Format.ALIGN_RIGHT, str1);
            in.nextLine();

            System.out.println("4: Formata str1 para deslocar uma posição para a esquerda.");
            formatText(0, COLS - str1.length() - 1, COLS - 1, Format.SHIFT_LEFT);
            in.nextLine();

            System.out.println("FIM DE TESTE.");
            clearAll();
        }

        if (cursor_test) {
            clearAll();
            System.out.println("TESTE AO MÉTODO moveCursor() e shiftControl():");
            in.nextLine();

            System.out.println("1: Imprime str1 no meio da 1ª linha.");
            create_formatText(0, Format.ALIGN_CENTER, str1);
            in.nextLine();

            System.out.println("2: Move o cursor para a posição [0][5].");
            moveCursor(0, 5);
            in.nextLine();

            System.out.println("3: Desloca o cursor uma posição para a esquerda.");
            shiftControl(Control.CURSOR, Direction.LEFT);
            in.nextLine();

            System.out.println("4: Desloca o cursor uma posição para a direita");
            shiftControl(Control.CURSOR, Direction.RIGHT);
            in.nextLine();

            System.out.println("5: Desloca o ecrã uma posição para a esquerda.");
            shiftControl(Control.DISPLAY, Direction.LEFT);
            in.nextLine();

            System.out.println("6: Desloca o ecrã uma posição para a direita.");
            shiftControl(Control.DISPLAY, Direction.RIGHT);
            in.nextLine();

            System.out.println("FIM DE TESTE.");
            clearAll();
        }

        if (keyboard_test) {
            clearAll();
            System.out.println("TESTE AO TECLADO:");
            in.nextLine();

            int points = 0, number;
            long time_difficulty = 2000, difficulty = 20;
            char key = KBD.NONE;
            boolean newNumber, increaseScore = false;

            print(0, 0, "Pontos: " + points);

            do {
                long starting_time = Time.getTimeInMillis();

                newNumber = false;
                number = (int)(Math.random()*10);
                create_formatText(1, Format.ALIGN_CENTER, "" + number);

                while (!newNumber) {
                    key = KBD.getKey();
                    newNumber = Time.getTimeInMillis() - starting_time == time_difficulty;
                    if (key == (char)(number + '0')) {
                        points++;
                        print(0,0,"Pontos: " + points);
                        newNumber = true;
                    }
                }
                if (time_difficulty > 200)
                    time_difficulty -= difficulty;
            } while (key != '*');

            System.out.println("FIM DE TESTE.");
        }
    }

    // Imprime no LCD o que está em lcdState
    public static void print (int lin, int startCol, int endCol) {
        String str = readArray (lin, startCol, endCol);
        LCD.cursor(lin, startCol);
        LCD.write(str);
    }

    // Imprime no LCD a String recebida e atualiza lcdState
    public static void print (int lin, int col, String str) {
        LCD.cursor(lin, col);
        LCD.write(str);
        writeArray(lin, col, str);
    }

    // Imprime no LCD o char recebida e atualiza lcdState
    public static void print (int lin, int col, char c) {
        LCD.cursor(lin, col);
        LCD.write(c);
        writeArray(lin, col, c);
    }

    // Imprime no LCD o char customizado recebido e atualiza lcdState
    public static void print (int lin, int col, CustomCharacter custom_char) {
        LCD.cursor(lin, col);
        LCD.write(custom_char);
        writeArray(lin, col, CUSTOM_CHAR);
    }

    // Apaga uma String no LCD
    public static void clear (int lin, int startCol, int endCol) {
        for (int col = startCol; col <= endCol; ++col)
            lcdState[lin][col] = NONE;
        print (lin, startCol, endCol);
        LCD.cursor(lin, startCol);
    }

    // Apaga um char no LCD
    public static void clear (int lin, int col) {
        lcdState[lin][col] = NONE;
        print (lin, col, col);
        LCD.cursor(lin, col);
    }

    // Apaga uma linha completa no LCD
    public static void clearLine (int lin) { clear (lin, 0, LCD.COLS - 1); }

    // Apaga tudo no LCD
    public static void clearAll() {
        clear (0, 0, LCD.COLS - 1);
        clear (1, 0, LCD.COLS - 1);
        LCD.clear();
    }

    // Escreve uma String para o lcdState
    private static void writeArray (int lin, int startCol, String str) {
        for (int col = startCol, strIndex = 0; col < LCD.COLS && strIndex < str.length(); ++col, ++strIndex)
            writeArray(lin, col, str.charAt(strIndex));
    }

    // Escreve um char para o lcdState
    private static void writeArray (int lin, int col, char c) { lcdState[lin][col] = c; }

    // Lê uma String do lcdState
    public static String readArray (int lin, int startCol, int endCol) {
        int length = endCol - startCol;
        StringBuilder str = new StringBuilder(length);

        for (int col = startCol; col <= endCol; ++col)
            str.append(lcdState[lin][col]);

        return str.toString();
    }

    // Lê um char do lcdState
    public static char readArray (int lin, int col) { return lcdState[lin][col]; }

    // Move posição de conteúdo no LCD
    public static void moveText (int linInitial, int linFinal, int startColInitial, int endColInitial, int startColFinal) {
        String str = readArray (linInitial, startColInitial, endColInitial);
        clear (linInitial, startColInitial, endColInitial);

        int endColFinal = startColFinal + str.length() - 1;
        writeArray (linFinal, startColFinal, str);

        if (invalidIndex (0, endColFinal))
            endColFinal = LCD.COLS - 1;

        if (invalidIndex (startColFinal, LCD.COLS - 1))
            startColFinal = 0;

        print(linFinal, startColFinal, endColFinal);
    }

    // Formata o texto presente no lcdState
    public static void formatText (int lin, int startColInitial, int endColInitial, Format format) {
        int startColFinal = selectText ( startColInitial, endColInitial, format);
        moveText(lin, lin, startColInitial, endColInitial, startColFinal);
    }

    // Cria e formata esse texto no LCD
    public static void create_formatText (int lin, Format format, String str) {
        int startColInitial = 0;
        int endColInitial = startColInitial + str.length();
        int startColFinal = selectText( startColInitial, endColInitial, format);

        print (lin, startColFinal, str);
    }

    // Seleciona texto e retorna as coordenadas finais dadas pelo formato escolhido
    public static int selectText (int startColInitial, int endColInitial, Format format) {
        int length = endColInitial - startColInitial + 1, startColFinal = startColInitial, increment = 0;

        switch (format) {
            case ALIGN_CENTER:
                startColFinal = LCD.COLS/2 - length/2;
                break;
            case ALIGN_RIGHT:
                startColFinal = LCD.COLS - length + 1;
                break;
            case ALIGN_LEFT:
                startColFinal = 0;
                break;
            case SHIFT_RIGHT:
                increment = 1;
                break;
            case SHIFT_LEFT:
                increment = -1;
                break;
            default:
                System.err.println("No such direction.");
                startColFinal = -1;
                break;
        }

        if(format == Format.SHIFT_RIGHT || format == Format.SHIFT_LEFT)
            startColFinal += increment;

        return startColFinal;
    }

    // Cria um meno com título e duas escolhas
    public static void menu (Format format, String title, String choiceA_text, String choiceB_text) {
        clearAll();
        create_formatText(0, format, title);
        create_formatText(1, Format.ALIGN_LEFT, choiceA_text);
        create_formatText(1, Format.ALIGN_RIGHT, choiceB_text);
    }

    // Verifica se é um índice válido de lcdState
    private static boolean invalidIndex(int startCol, int endCol) {
        boolean invalidIndex = true;
        if (startCol >= 0 && endCol < LCD.COLS)
            invalidIndex = false;
        return invalidIndex;
    }

    // Ativa/desativa o cursor
    public static void cursorControl (boolean cursorOn, boolean cursorBlink) {
        LCD.writeCMD(LCDCode.display_control(true, cursorOn, cursorBlink));
    }

    // Move o cursor para a posição indicada pelos parâmetros recebidos
    public static void moveCursor (int lin, int col) {
        LCD.cursor(lin, col);
    }

    // Desloca o cursor/ecrã uma vez para a direção indicada pelos parâmetros recebidos
    public static void shiftControl (Control ctrl, Direction dir) {
        boolean shift_to_right = dir == Direction.RIGHT;
        boolean display_shift = ctrl == Control.DISPLAY;

        LCD.writeCMD(LCDCode.cursor_or_display_shift(display_shift,shift_to_right));
    }

    // Inicia esta classe
    public static void init () {
        clearAll();
    }
}
