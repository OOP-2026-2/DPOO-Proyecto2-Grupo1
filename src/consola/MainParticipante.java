package consola;

import java.util.Scanner;
import clubdeportivo.torneos.ParticipanteTorneo;
import usuarios.Usuario;
import usuarios.tipos.Empleado;

public class MainParticipante extends MainBase {

    @Override
    protected boolean prepararAcceso() {
        Usuario usuario = iniciarSesion("ACCESO DEL PARTICIPANTE");

        // Socios, estudiantes y participantes externos; los empleados usan su propia consola
        if (!(usuario instanceof ParticipanteTorneo) || usuario instanceof Empleado) {
            System.out.println("Las credenciales no corresponden a un participante autorizado.");
            return false;
        }

        new ConsolaParticipante(club, (ParticipanteTorneo) usuario).ejecutar();
        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        new InputHandler(scanner);

        try {
            new MainParticipante().ejecutar();
        } finally {
            scanner.close();
        }
    }
}
