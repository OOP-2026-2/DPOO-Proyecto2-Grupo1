package usuarios.tipos;

import java.time.LocalDate;

import clubdeportivo.torneos.ParticipanteTorneo;
import usuarios.Usuario;

public class EstudianteUniversidad extends Usuario implements ParticipanteTorneo {
    private final String universidad;
    private final String correoInstitucional;

    public EstudianteUniversidad(String id, String nombre, LocalDate fechaNacimiento, String login,
            String contrasena, String universidad, String correoInstitucional) {
        super(id, nombre, fechaNacimiento, login, contrasena);

        if (universidad == null || universidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La universidad es obligatoria");
        }

        if (correoInstitucional == null || correoInstitucional.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo institucional es obligatorio");
        }

        this.universidad = universidad;
        this.correoInstitucional = correoInstitucional;
    }

    @Override
    public boolean puedeRecibirBono() {
        return false;
    }

    @Override
    public boolean puedeRecibirPremioDinero() {
        return true;
    }

    public String getUniversidad() {
        return universidad;
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }
}