package consola;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputHandler {
    private static Scanner scanner;

    public InputHandler(Scanner scanner) {
        InputHandler.scanner = scanner;
    }

    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("El valor no puede estar vacío.");
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readString(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Debe escribir un número entero válido.");
            }
        }
    }

    public static int readNonNegativeInt(String prompt) {
        while (true) {
            int cantidad = readInt(prompt);

            if (cantidad >= 0) {
                return cantidad;
            }

            System.out.println("La cantidad no puede ser negativa.");
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readString(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Debe escribir un número válido.");
            }
        }
    }

    public static boolean readBoolean(String prompt) {
        while (true) {
            String input = readString(prompt).toLowerCase();

            if (input.equals("s")
                    || input.equals("si")
                    || input.equals("sí")) {

                return true;
            }

            if (input.equals("n")
                    || input.equals("no")) {

                return false;
            }

            System.out.println("Responda con s o n.");
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            try {
                return LocalDate.parse(readString(prompt));
            } catch (DateTimeParseException e) {
                System.out.println(
                        "Debe escribir la fecha en formato AAAA-MM-DD.");
            }
        }
    }

    public static LocalTime readTime(String prompt) {
        while (true) {
            try {
                return LocalTime.parse(readString(prompt));
            } catch (DateTimeParseException e) {
                System.out.println(
                        "Debe escribir la hora en formato HH:MM.");
            }
        }
    }
}