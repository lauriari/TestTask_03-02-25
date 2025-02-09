package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

class OurWriter {
    private File file = null;

    public OurWriter(String filename) {
        this.file = new File(filename);
    }

    public void reWrite() throws IOException {
        try (FileReader reader = new FileReader("temp.txt");
             PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            int character;
            while ((character = reader.read()) != -1) {
                writer.print((char) character);
            }
                writer.print('\n');
        }
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
                    if (!exp2) {
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
            } else {                                     //1000000.123400000000
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

public class FileContentFilter {

    private OurWriter writerInt = null;
    private OurWriter writerDouble = null;
    private OurWriter writerString = null;

    public FileContentFilter() {
        writerInt = new OurWriter("integers.txt");
        writerDouble = new OurWriter("double.txt");
        writerString = new OurWriter("string.txt");
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
                switch (checker.getType()) {
                    case 1:
                        writerInt.reWrite();
                        break;
                    case 2:
                        writerDouble.reWrite();
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
