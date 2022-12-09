package edu.isel.lic.game;

import java.io.*;
import java.util.Scanner;

public class FileAccess
{
    public static void writeFileText(String[] text ,String file_name)
    {
        PrintWriter fileToWrite = writeTextVar(file_name);
        for (String s : text) {
            fileToWrite.println(s);
        }
        fileToWrite.flush();
        fileToWrite.close();
    }

    public static String[] readFile(int lines, String file_name)
    {
        String[] array = new String[lines];
        Scanner fileToRead = readTextVar(file_name);

        for (int i = 0; i < lines ; ++i) {
            if(fileToRead.hasNextLine()) {
                array[i] = fileToRead.nextLine();
            }else{
                array[i] = "" ;
            }
        }
        return array;
    }


    // Abrir o FileReader e Scanner para um ficheiro inputFile ou outputFile.
    public static Scanner readTextVar(String file_name){

        file_name = fileName(file_name);

        Scanner readText = null;
        try {
            readText = new Scanner(new FileReader(file_name));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File missing: " + file_name);
            System.exit(-1);
            //createFile(file_name);


        }
        return readText;
    }

    // Abrir o FileWriter e PrintWriter para um ficheiro inputFile ou outputFile.
    public static PrintWriter writeTextVar(String file_name){

        file_name = fileName(file_name);

        PrintWriter writeText = null;
        try {
            writeText = new PrintWriter(new FileWriter(file_name));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to save: "+ file_name);
            System.exit(-2);
        }

        return writeText;
    }

    // Determina a partir de uma String em qual ficheiro se vai ler/escrever.
    private static String fileName (String file_name){
        file_name = file_name+".txt";
        return file_name ;
    }
}

