package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;

public class Main {

    static byte typeArgs(String args) {

        if (args.equals("-p")) return 1;
        if (args.equals("-o")) return 2;
        if (args.equals("-a")) return 3;
        if (args.equals("-s")) return 4;
        if (args.equals("-f")) return 5;
        return 6;

    }

    public static void main(String[] args) {

        FileContentFilter filter = null;
        String way = null;
        String pref = null;
        boolean optionA = false;
        boolean optionS = false;

        for (int i = 0; i < args.length; i++) {

            switch (typeArgs(args[i])) {
                case 1:
                    i++;
                    pref = args[i];
                    break;
                case 2:
                    i++;
                    way = args[i];
                    break;
                case 3:
                    optionA = true;
                    break;
                case 4:
                    optionS = true;
                    break;

                default:
                    if (filter == null) {
                        filter = new FileContentFilter(pref, way, optionA, optionS);
                    }
                    try {
                        filter.work(args[i]);
                    } catch (FileNotFoundException e) {
                        System.err.println("Файл не найден: " + e.getMessage());
                    } catch (IOException e) {
                        System.err.println("Ошибка ввода-вывода: " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("Неизвестная ошибка: " + e.getMessage());
                    }
            }
        }
        if (optionS) {
            filter.printSmallStats();
        }
        System.out.printf("Finish");
    }
}