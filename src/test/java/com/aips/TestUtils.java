package com.aips;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Random;

import static com.aips.BatchProcessor.DATA_ENTRY_PATTERN;

public abstract class TestUtils {
    public static boolean compare (String file1, String file2) throws IOException {
        try (BufferedReader in1 = new BufferedReader(new FileReader(file1));
             BufferedReader in2 = new BufferedReader(new FileReader(file2))) {
            String str1, str2;
            do {
                str1 = in1.readLine();
                str2 = in2.readLine();

                if (str1 == null) return str2 == null;
                if (!str1.equals(str2)) return false;
            } while(str1 != null);

            return true;
        }
    }

    public static String format(LocalDateTime ldt) {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ldt);
    }

    public static void generateData(String filename, final int noDays) throws Exception {
        LocalDateTime ldf = LocalDateTime.parse("2022-06-01T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Random ran = new Random();
        final int bound = 100;

        try (BufferedWriter out = new BufferedWriter(new FileWriter(filename + ".txt"))) {
            for (int i = 0, n = 48 * noDays; i < n; i++) {
                out.write(String.format(DATA_ENTRY_PATTERN, format(ldf), ran.nextInt(bound)));
                ldf = ldf.plusMinutes(30);
            }
        }
    }
}
