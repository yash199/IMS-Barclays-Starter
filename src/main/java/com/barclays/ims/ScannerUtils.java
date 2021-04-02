package com.barclays.ims;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Singleton class, encaspulating a Scanner for user input.
 * You won't need to add to this class.
 */
public class ScannerUtils {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final ScannerUtils INSTANCE = new ScannerUtils();

    protected Scanner scanner;

    public ScannerUtils() {
        scanner = new Scanner(System.in);
    }

    public String getString() {
        scanner.useDelimiter("\r\n");
        return scanner.nextLine();
    }

    public Double getDouble() {
        String input = null;
        Double doubleInput = null;
        do {
            try {
                input = getString();
                doubleInput = Double.parseDouble(input);
            } catch (NumberFormatException nfe) {
                LOGGER.info("Error - Please enter a number");
            }
        } while (doubleInput == null);
        return doubleInput;
    }

    public Long getLong() {
        String input = null;
        Long longInput = null;
        do {
            try {
                input = getString();
                longInput = Long.parseLong(input);
            } catch (NumberFormatException nfe) {
                LOGGER.info("Error - Please enter a whole number");
            }
        } while (longInput == null);
        return longInput;
    }

}
