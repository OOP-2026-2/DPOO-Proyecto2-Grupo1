package usuarios.tipos;

import java.time.LocalDate;

import clubdeportivo.torneos.ParticipanteTorneo;
import usuarios.Usuario;

public class ParticipanteExterno extends Usuario implements ParticipanteTorneo {
    private final String contacto;

    public ParticipanteExterno(String id, String nombre, LocalDate fechaNacimiento, String login,
            String contrasena, String contacto) {
        super(id, nombre, fechaNacimiento, login, contrasena);

        if (contacto == null || contacto.trim().isEmpty()) {
            throw new IllegalArgumentException("El contacto es obligatorio");
        }

        this.contacto = contacto;
    }

    @Override
    public boolean puedeRecibirBono() {
        return false;
    }

    @Override
    public boolean puedeRecibirPremioDinero() {
        return true;
    }

    public String getContacto() {
        return contacto;
    }
}
