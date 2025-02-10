package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.math.RoundingMode;


interface IObserver {
    void update();
}

interface IObservable {
    void addObserver(IObserver o);

    void updateNotify();

}

class OurWriter implements IObservable {
    private File file = null;
    private boolean optionA = false;
    private List<IObserver> observers;

    public OurWriter(String filename, boolean a) {
        this.file = new File(filename);
        this.optionA = a;
        observers = new ArrayList<>();
    }

    @Override
    public void addObserver(IObserver o) {
        observers.add(o);
    }

    @Override
    public void updateNotify() {
        for (Object o : observers.toArray()) {
            ((IObserver) o).update();
        }
    }

    public void reWrite() throws IOException {
        updateNotify();
        try (FileReader reader = new FileReader("temp.txt");
             PrintWriter writer = new PrintWriter(new FileWriter(file, optionA))) {
            optionA = true;
            int character;
            while ((character = reader.read()) != -1) {
                writer.print((char) character);
            }
            writer.print('\n');
        }
    }
}

class OurCounter implements IObserver {
    private long count;
    private String name;

    public OurCounter(String s) {
        count = 0;
        name = s;
    }

    @Override
    public void update() {
        count++;
    }

    @Override
    public String toString() {
        return name + " - " + count + "\n";
    }

}

class SaveTemp {
    private File filename = null;
    private PrintWriter writer = null;

    public SaveTemp(String f) {
        this.filename = new File(f);
    }

    public void record(char ch) {
        try {
            if (writer == null) {
                writer = new PrintWriter(new FileWriter(filename));
            }
            writer.print(ch);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (writer != null) {
            writer.close();
        }
    }

    public void del() {
        if (writer != null) {
            filename.delete();
        }
    }
}


class CheckType {
    private byte type;
    private boolean start;
    private boolean exp;
    private boolean exp2;
    private boolean sign;

    private boolean dot;

    private long count;

    private long countBeforeDot;
    private long lastDigit;

    private long lastDigitBeforeDot;

    private long expp;

    public CheckType() {
        type = 1;
        start = false;
        exp = false;
        exp2 = false;
        sign = true;
        dot = false;
        countBeforeDot = 0;
        count = 0;
        lastDigitBeforeDot = 0;
        lastDigit = 0;
        expp = 0;
    }

    private byte typeCh(int ch) {
        if (ch == '+' || ch == '-') {
            return 1;
        }
        if (Character.isDigit(ch)) {
            return 2;
        }
        if (ch == 'E' || ch == 'e') {
            return 3;
        }
        if (ch == ',' || ch == '.') {
            return 4;
        }
        return 5;
    }

    public void take(int ch) {
        if (type != 3) {
            switch (typeCh(ch)) {
                case 1:
                    if (start) {
                        if (exp) {
                            exp = false;
                            if (ch == '-') {
                                sign = false;
                            }
                        } else {
                            type = 3;
                        }
                    }
                    break;
                case 2:
                    if (!dot && !exp2) {
                        countBeforeDot++;
                        if (ch != '0') {
                            lastDigitBeforeDot = countBeforeDot;
                        }
                    }
                    if (dot && !exp2) {
                        count++;
                        if (ch != '0') {
                            lastDigit = count;
                        }
                    }
                    if (exp2) {
                        expp = expp * 10 + (ch - '0');
                    }
                    break;
                case 3:
                    if (!exp2 && dot) {
                        exp = true;
                        exp2 = true;
                    } else {
                        type = 3;
                    }
                    break;
                case 4:
                    if (!dot) {
                        dot = true;
                    } else {
                        type = 3;
                    }
                    break;
                default:
                    type = 3;
            }
            start = true;
        }
    }

    public byte getType() {
        if (type == 1 && dot) {
            if (!exp2) {
                if (lastDigit > 0) {
                    return 2;
                }
            } else {
                if (sign && lastDigit > expp) {
                    return 2;
                }
                if (!sign) {
                    if (lastDigit > 0) {
                        return 2;
                    } else {
                        if (countBeforeDot - lastDigitBeforeDot < expp) {
                            return 2;
                        }
                    }
                }
            }
        }

        return type;
    }

}

class FullStatsNumber implements IObserver {
    private BigDecimal sum;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal current;
    private String name;
    private long count;

    private boolean start;

    public FullStatsNumber(String s) {
        sum = new BigDecimal("0");
        min = new BigDecimal("0");
        max = new BigDecimal("0");
        start = false;
        count = 0;
        name = s;
    }

    @Override
    public void update() {
        count++;
        try (BufferedReader reader = new BufferedReader(new FileReader("temp.txt"))) {
            String str = reader.readLine();
            current = new BigDecimal(str);
            if (!start) {
                start = true;
                min = current;
                max = current;
            } else {
                min = min.min(current);
                max = max.max(current);
            }

            sum = sum.add(current);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Full stats for " + name + "\n" + count + " elements\nmin=" + min + "\nmax=" + max + "\nsum=" + sum + "\naverage=" +
                sum.divide(BigDecimal.valueOf(count), 3, RoundingMode.HALF_UP) + "\n";
    }

}

class FullStatsString implements IObserver {

    private long min;
    private long max;
    private long count;
    private long current;
    private String name;
    private boolean start;

    public FullStatsString(String s) {
        min = 0;
        max = 0;
        count = 0;
        current = 0;
        name = s;
        start = false;
    }

    @Override
    public void update() {
        count++;
        try (FileReader reader = new FileReader("temp.txt")) {
            while ((reader.read()) != -1) {
                current++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!start) {
            start = true;
            min = current;
            max = current;
            current = 0;
        } else {
            if (current < min) {
                min = current;
            }
            if (current > max) {
                max = current;
            }
            current = 0;
        }
    }

    @Override
    public String toString() {
        return "Full stats for " + name + "\n" + count + "elements\nlenght(minStr)=" + min + "\nlenght(maxStr)=" + max + "\n";
    }
}

public class FileContentFilter {

    private OurWriter writerInt = null;
    private OurWriter writerFloat = null;
    private OurWriter writerString = null;

    private OurCounter counterInt = null;
    private OurCounter counterFloat = null;
    private OurCounter counterString = null;

    private FullStatsNumber fullInt = null;
    private FullStatsNumber fullFloat = null;

    private FullStatsString fullString = null;

    private Path way = null;


    public FileContentFilter(String pref, String sWay, boolean optionA, boolean optionS, boolean optionF) {
        String str = "";
        if (pref != null) str = pref + str;
        if (sWay != null) {
            str = sWay + "/" + str;
            this.way = Paths.get(sWay);
        }
        writerInt = new OurWriter(str + "integers.txt", optionA);
        writerFloat = new OurWriter(str + "floats.txt", optionA);
        writerString = new OurWriter(str + "strings.txt", optionA);
        if (optionS) {
            counterInt = new OurCounter("Integers");
            writerInt.addObserver(counterInt);
            counterFloat = new OurCounter("Floats");
            writerFloat.addObserver(counterFloat);
            counterString = new OurCounter("Strings");
            writerString.addObserver(counterString);
        }
        if (optionF) {
            fullInt = new FullStatsNumber("Integers");
            writerInt.addObserver(fullInt);
            fullFloat = new FullStatsNumber("Floats");
            writerFloat.addObserver(fullFloat);
            fullString = new FullStatsString("Strings");
            writerString.addObserver(fullString);
        }


    }

    public void printSmallStats() {
        System.out.print(counterInt);
        System.out.print(counterFloat);
        System.out.print(counterString);
    }

    public void printFullStats() {
        try {
            System.out.print(fullInt);
        }
        catch (ArithmeticException e){
            System.out.println("0 Integers elements");
        }
        try {
            System.out.print(fullFloat);
        }
        catch (ArithmeticException e){
            System.out.println("0 Float elements");
        }
        System.out.print(fullString);
    }

    public void work(String f) throws FileNotFoundException, IOException {

        try (FileReader reader = new FileReader(f)) {
            int character;
            while ((character = reader.read()) != -1) {
                SaveTemp tempFile = new SaveTemp("temp.txt");
                CheckType checker = new CheckType();
                while (character != -1 && character != '\n') {
                    tempFile.record((char) character);
                    checker.take(character);
                    character = reader.read();
                }
                tempFile.close();
                if (way != null && !Files.exists(way)) {
                    Files.createDirectories(way);
                }
                switch (checker.getType()) {
                    case 1:
                        writerInt.reWrite();
                        break;
                    case 2:
                        writerFloat.reWrite();
                        break;
                    case 3:
                        writerString.reWrite();
                        break;
                }

                tempFile.del();
            }
        }
    }
}
