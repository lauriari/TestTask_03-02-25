package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;

public class Main {
    public static void main(String[] args) {

        FileContentFilter filter = new FileContentFilter();
        for (int i = 0; i < args.length; i++) {
           // File file = new File(args[i]);
            System.out.printf("args-%d, i-%d, s-%s\n", args.length, i, args[i]);
            try {
                filter.work(args[i]);
            }
            catch (FileNotFoundException e){
                System.err.println("Файл не найден: " + e.getMessage());
            }
            catch (IOException e){
                System.err.println("Ошибка ввода-вывода: " + e.getMessage());
            }
            catch (Exception e) {
                System.err.println("Неизвестная ошибка: " + e.getMessage());
            }
        }

       System.out.printf("Finish");
    }
}