package consola;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputHandler {
    // Los archivos de datos separan los campos con estos caracteres
    public static final String SEPARADORES_PROHIBIDOS = ";|";
    public static final String SEPARADORES_PROHIBIDOS_EN_NOMBRES = ";|,";

    private static Scanner scanner;

    public InputHandler(Scanner scanner) {
        InputHandler.scanner = scanner;
    }

    // Nombres, IDs y demás datos que se guardan como campo de un archivo
    public static String readString(String prompt) {
        return leerSinSeparadores(prompt, SEPARADORES_PROHIBIDOS_EN_NOMBRES);
    }

    // Descripciones y comentarios: admiten comas
    public static String readTextoLibre(String prompt) {
        return leerSinSeparadores(prompt, SEPARADORES_PROHIBIDOS);
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(leerLinea(prompt));
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

    public static int readIntEnRango(String prompt, int minimo, int maximo) {
        while (true) {
            int valor = readInt(prompt);

            if (valor >= minimo && valor <= maximo) {
                return valor;
            }

            System.out.println("Escriba un número entre " + minimo + " y " + maximo + ".");
        }
    }

    // Acepta 0.5 o 0,5
    public static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(leerLinea(prompt).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Debe escribir un número válido.");
            }
        }
    }

    public static boolean readBoolean(String prompt) {
        while (true) {
            String input = leerLinea(prompt).toLowerCase();

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
                return LocalDate.parse(leerLinea(prompt));
            } catch (DateTimeParseException e) {
                System.out.println(
                        "Debe escribir la fecha en formato AAAA-MM-DD.");
            }
        }
    }

    public static LocalTime readTime(String prompt) {
        while (true) {
            try {
                return LocalTime.parse(leerLinea(prompt));
            } catch (DateTimeParseException e) {
                System.out.println(
                        "Debe escribir la hora en formato HH:MM.");
            }
        }
    }

    // Periodo de una mensualidad, por ejemplo 2026-11
    public static String readPeriodo(String prompt) {
        while (true) {
            try {
                return YearMonth.parse(leerLinea(prompt)).toString();
            } catch (DateTimeParseException e) {
                System.out.println(
                        "Debe escribir el periodo en formato AAAA-MM.");
            }
        }
    }

    // Muestra las opciones numeradas; se puede responder con el número o con el nombre
    public static <E extends Enum<E>> E readEnum(String titulo, E[] valores) {
        mostrarOpciones(titulo, valores);

        while (true) {
            E valor = buscarOpcion(leerLinea("Opción: "), valores);

            if (valor != null) {
                return valor;
            }

            System.out.println("Opción inválida. Escriba el número o el nombre de la opción.");
        }
    }

    // Igual que readEnum, pero 0 significa "todos" y devuelve null (útil para filtros)
    public static <E extends Enum<E>> E readEnumOpcional(String titulo, E[] valores) {
        System.out.println(titulo);
        System.out.println("0. TODOS");

        for (int i = 0; i < valores.length; i++) {
            System.out.println((i + 1) + ". " + valores[i]);
        }

        while (true) {
            String entrada = leerLinea("Opción: ");

            if (entrada.equals("0") || entrada.equalsIgnoreCase("TODOS")) {
                return null;
            }

            E valor = buscarOpcion(entrada, valores);

            if (valor != null) {
                return valor;
            }

            System.out.println("Opción inválida. Escriba el número o el nombre de la opción.");
        }
    }

    private static <E extends Enum<E>> void mostrarOpciones(String titulo, E[] valores) {
        System.out.println(titulo);

        for (int i = 0; i < valores.length; i++) {
            System.out.println((i + 1) + ". " + valores[i]);
        }
    }

    private static <E extends Enum<E>> E buscarOpcion(String entrada, E[] valores) {
        for (int i = 0; i < valores.length; i++) {
            if (entrada.equals(String.valueOf(i + 1))
                    || entrada.equalsIgnoreCase(valores[i].name())) {
                return valores[i];
            }
        }

        return null;
    }

    private static String leerSinSeparadores(String prompt, String prohibidos) {
        while (true) {
            String input = leerLinea(prompt);
            boolean valido = true;

            for (char caracter : prohibidos.toCharArray()) {
                if (input.indexOf(caracter) >= 0) {
                    valido = false;
                }
            }

            if (valido) {
                return input;
            }

            System.out.println("El texto no puede contener los caracteres "
                    + String.join(" ", prohibidos.split("")) + ".");
        }
    }

    private static String leerLinea(String prompt) {
        while (true) {
            System.out.print(prompt);

            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("No hay más datos de entrada");
            }

            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("El valor no puede estar vacío.");
        }
    }
}
