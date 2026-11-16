package consola;

import java.util.Scanner;
import usuarios.Usuario;
import usuarios.tipos.Empleado;

public class MainEmpleado extends MainBase {

    @Override
    protected boolean prepararAcceso() {
        Usuario usuario = iniciarSesion("ACCESO DEL EMPLEADO");

        // El administrador no es empleado (no hereda de Empleado), entra por su propio programa
        if (!(usuario instanceof Empleado)) {
            System.out.println("Las credenciales no corresponden a un empleado autorizado.");
            return false;
        }

        new ConsolaEmpleado(club, (Empleado) usuario).ejecutar();
        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        new InputHandler(scanner);

        try {
            new MainEmpleado().ejecutar();
        } finally {
            scanner.close();
        }
    }
}
