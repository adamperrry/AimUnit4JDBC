package com.aim.movie.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.apache.commons.validator.GenericValidator;

// based on the Singleton pattern
public class Input {
    // Only one instance of an Input object will ever exist, and the instance is
    // tracked by this reference variable.
    private static Input referenceToSingleInputObject = null;

    // Instance variable, but since only one Input can ever be created, only one
    // Scanner object will ever be created.
    private Scanner scannerKeyboard;

    // Private constructor guarantees that no Input object can be
    // created from outside the Input class
    private Input() {
        scannerKeyboard = new Scanner(System.in);
    }

    // If no <i>Input</i> object exists one will be created; if one already
    // exists, it will be re-used. Static class method
    public static Input getInstance() {
        if (referenceToSingleInputObject == null)
            referenceToSingleInputObject = new Input();
        return referenceToSingleInputObject;
    }

    // Presents a prompt to the user and retrieves an int value.
    public int getInt(String sPrompt) {
        String sInput;
        int nInput;
        System.out.print(sPrompt);
        sInput = scannerKeyboard.nextLine();
        while (!GenericValidator.isInt(sInput)) {
            System.out.println("Number is required input.");
            System.out.print(sPrompt);
            sInput = scannerKeyboard.nextLine(); // clear bad input data from the keyboard
        }
        nInput = Integer.parseInt(sInput);
        return nInput;
    }

    public int getIntOrDefault(String sPrompt, int defaultInt) {
        String sInput;
        int nInput;
        System.out.print(sPrompt);
        sInput = scannerKeyboard.nextLine();
        while (!GenericValidator.isInt(sInput) && !GenericValidator.isBlankOrNull(sInput)) {
            System.out.println("Number or blank is required input.");
            System.out.print(sPrompt);
            sInput = scannerKeyboard.nextLine(); 
        }
        if (GenericValidator.isBlankOrNull(sInput)) {
            return defaultInt;
        }
        nInput = Integer.parseInt(sInput);
        return nInput;
    }

    // Presents a prompt to the user and retrieves an int value which is within
    // the range of nLow< to nHigh(inclusive).
    public int getInt(String sPrompt, int nLow, int nHigh) {
        int nInput;
        do {
            System.out.printf("%s (%d-%d): ", sPrompt, nLow, nHigh);
            while (!scannerKeyboard.hasNextInt()) { // peek into keyboard buffer to see if next token is a legitimate
                // int
                System.out.println("Number is required input.");
                System.out.print(sPrompt);
                scannerKeyboard.nextLine(); // retrieves input to the next \r\n (line separator) and we choose to ignore
                // the String that is created and returned
            }
            nInput = scannerKeyboard.nextInt();
            if (nInput >= nLow && nInput <= nHigh) // int value is within range, thus it is valid . . . time to break
                // out of loop
                break;
            System.out.println("Value out of range. Try again.");
        } while (true);
        scannerKeyboard.nextLine();
        return nInput;
    } // end int getInt(String sPrompt, int nLow, int nHigh)

    // Presents a prompt to the user and retrieves a reference-to-String.
    public String getString(String sPrompt) {
        System.out.print(sPrompt);
        String sInput = scannerKeyboard.nextLine();
        return sInput;
    }

    public String getString(String sPrompt, Boolean strict) {
        System.out.print(sPrompt);
        String sInput = scannerKeyboard.nextLine();
        while(sInput.isEmpty() && strict) {
            System.out.println("Title must not be blank.");
            System.out.print(sPrompt);
            sInput = scannerKeyboard.nextLine();
        }
        return sInput;
    }

    public String getStringOrDefault(String sPrompt, String defaulString) {
        System.out.print(sPrompt);
        String sInput = scannerKeyboard.nextLine();
        if (GenericValidator.isBlankOrNull(sInput)) {
            return defaulString;
        }
        return sInput;
    }

    // Presents a prompt to the user and retrieves a reference-to-Char
    public char getChar(String Prompt) {
        System.out.print(Prompt);
        char cInput = scannerKeyboard.next().charAt(0);
        scannerKeyboard.nextLine();
        return cInput;
    }

    public Date getDate(String sPrompt, String dateFormat) {
        Date dInput = null;
        System.out.print(sPrompt);
        String dateString = scannerKeyboard.nextLine();
        while (!GenericValidator.isDate(dateString, dateFormat, false)) {
            System.out.println("Valid date is required input.");
            System.out.print(sPrompt);
            dateString = scannerKeyboard.nextLine();
        }
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            dInput = formatter.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dInput;
    }

    public Date getDateOrDefault(String sPrompt, String dateFormat, Date defaultDate) {
        Date dInput = null;
        System.out.print(sPrompt);
        String dateString = scannerKeyboard.nextLine();
        while (!GenericValidator.isDate(dateString, dateFormat, false) && !GenericValidator.isBlankOrNull(dateString)) {
            System.out.println("Valid date is required input.");
            System.out.print(sPrompt);
            dateString = scannerKeyboard.nextLine();
        }

        if (GenericValidator.isBlankOrNull(dateString)) {
            return defaultDate;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            dInput = formatter.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dInput;
    }
}
