package consola;

import static consola.InputHandler.readString;

import java.io.IOException;
import clubdeportivo.ClubDeportivo;
import usuarios.Usuario;
import utilidades.persistencia.PersistenciaClub;

// Carga, autenticación y guardado que comparten los tres programas de acceso
public abstract class MainBase {
    protected final ClubDeportivo club;
    protected final PersistenciaClub persistencia;

    protected MainBase() {
        club = new ClubDeportivo();
        persistencia = new PersistenciaClub();
    }

    public void ejecutar() {
        // Si los datos no se pudieron leer no se continúa: guardar dejaría los archivos incompletos
        if (!cargarDatos()) {
            System.out.println("Revise los archivos de la carpeta datos; no se guardó ningún cambio.");
            return;
        }

        if (prepararAcceso()) {
            guardarDatos();
        }
    }

    // Pide las credenciales, abre la consola del rol y devuelve true si hay cambios que guardar
    protected abstract boolean prepararAcceso();

    protected Usuario iniciarSesion(String titulo) {
        System.out.println("================================");
        System.out.println("   " + titulo);
        System.out.println("================================");

        String login = readString("Login: ");
        String contrasena = readString("Contraseña: ");

        return club.autenticar(login, contrasena);
    }

    protected boolean cargarDatos() {
        try {
            persistencia.cargar(club);
            System.out.println("Datos cargados correctamente.");
            return true;
        } catch (IOException e) {
            System.out.println("No fue posible cargar los datos: " + e.getMessage());
            return false;
        }
    }

    protected void guardarDatos() {
        try {
            persistencia.guardar(club);
            System.out.println("Datos guardados correctamente.");
        } catch (IOException e) {
            System.out.println("No fue posible guardar los datos: " + e.getMessage());
        }
    }
}
