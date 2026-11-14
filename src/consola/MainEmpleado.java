package consola;

import static consola.InputHandler.readString;

import java.io.IOException;
import java.util.Scanner;
import clubdeportivo.ClubDeportivo;
import usuarios.Usuario;
import usuarios.tipos.Empleado;
import usuarios.tipos.empleados.Administrador;
import utilidades.persistencia.PersistenciaClub;

public class MainEmpleado {
    private final ClubDeportivo club;
    private final PersistenciaClub persistencia;

    public MainEmpleado() {
        club = new ClubDeportivo();
        persistencia = new PersistenciaClub();
    }

    public void ejecutar() {
        cargarDatos();

        System.out.println("================================");
        System.out.println("      ACCESO DEL EMPLEADO");
        System.out.println("================================");

        String login = readString("Login: ");
        String contrasena = readString("Contraseña: ");
        Usuario usuario = club.autenticar(login, contrasena);

        if (!(usuario instanceof Empleado)
                || usuario instanceof Administrador) {

            System.out.println(
                    "Las credenciales no corresponden a un empleado autorizado.");
            return;
        }

        Empleado empleado = (Empleado) usuario;
        ConsolaEmpleado consola = new ConsolaEmpleado(
                club, empleado);

        consola.ejecutar();
        guardarDatos();
    }

    private void cargarDatos() {
        try {
            persistencia.cargar(club);
            System.out.println("Datos cargados correctamente.");
        } catch (IOException | RuntimeException e) {
            System.out.println("No fue posible cargar los datos: "
                    + e.getMessage());
        }
    }

    private void guardarDatos() {
        try {
            persistencia.guardar(club);
            System.out.println("Datos guardados correctamente.");
        } catch (IOException e) {
            System.out.println("No fue posible guardar los datos: "
                    + e.getMessage());
        }
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