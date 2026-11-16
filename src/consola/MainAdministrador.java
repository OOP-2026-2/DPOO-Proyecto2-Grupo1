package consola;

import static consola.InputHandler.readDate;
import static consola.InputHandler.readString;

import java.time.LocalDate;
import java.util.Scanner;
import usuarios.Usuario;
import usuarios.tipos.empleados.Administrador;

public class MainAdministrador extends MainBase {

    @Override
    protected boolean prepararAcceso() {
        if (!existeAdministrador()) {
            registrarAdministradorInicial();
            guardarDatos();
        }

        Usuario usuario = iniciarSesion("ACCESO DEL ADMINISTRADOR");

        if (!(usuario instanceof Administrador)) {
            System.out.println("Las credenciales no corresponden a un administrador.");
            return false;
        }

        new ConsolaAdministrador(club, (Administrador) usuario).ejecutar();
        return true;
    }

    private boolean existeAdministrador() {
        for (Usuario usuario : club.getUsuarios()) {
            if (usuario instanceof Administrador) {
                return true;
            }
        }

        return false;
    }

    private void registrarAdministradorInicial() {
        System.out.println();
        System.out.println("No existe un administrador registrado.");
        System.out.println("--- REGISTRAR ADMINISTRADOR INICIAL ---");

        String id = readString("ID: ");
        String nombre = readString("Nombre: ");
        LocalDate fechaNacimiento = readDate("Fecha de nacimiento (AAAA-MM-DD): ");
        String login = readString("Login: ");
        String contrasena = readString("Contraseña: ");

        club.registrarUsuario(new Administrador(id, nombre, fechaNacimiento, login, contrasena));
        System.out.println("Administrador inicial registrado correctamente.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        new InputHandler(scanner);

        try {
            new MainAdministrador().ejecutar();
        } finally {
            scanner.close();
        }
    }
}
