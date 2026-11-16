package consola;

import java.util.Scanner;

// Punto de entrada: cada rol entra a su consola y al salir se guardan los cambios
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        new InputHandler(scanner);
        boolean ejecutando = true;

        try {
            while (ejecutando) {
                mostrarMenu();
                int opcion = InputHandler.readInt(
                        "Seleccione una opción: ");

                switch (opcion) {
                    case 1:
                        new MainAdministrador().ejecutar();
                        break;
                    case 2:
                        new MainEmpleado().ejecutar();
                        break;
                    case 3:
                        new MainParticipante().ejecutar();
                        break;
                    case 0:
                        ejecutando = false;
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            }
        } finally {
            scanner.close();
        }

        System.out.println("Programa finalizado.");
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("================================");
        System.out.println("   CLUB DEPORTIVO SÉNECA");
        System.out.println("================================");
        System.out.println("1. Ingresar como administrador");
        System.out.println("2. Ingresar como empleado");
        System.out.println("3. Ingresar como participante");
        System.out.println("0. Salir");
    }
}