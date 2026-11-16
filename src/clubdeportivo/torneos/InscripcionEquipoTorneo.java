package clubdeportivo.torneos;

import java.time.LocalDate;

import clubdeportivo.deportes.Equipo;
import usuarios.tipos.Socio;

public class InscripcionEquipoTorneo
        extends InscripcionTorneo {

    private final Equipo equipo;

    public InscripcionEquipoTorneo(
            Torneo torneo,
            TipoCupo tipoCupo,
            double valorPagado,
            Equipo equipo) {

        this(
                LocalDate.now(),
                torneo,
                tipoCupo,
                valorPagado,
                equipo);
    }

    public InscripcionEquipoTorneo(
            LocalDate fechaInscripcion,
            Torneo torneo,
            TipoCupo tipoCupo,
            double valorPagado,
            Equipo equipo) {

        super(
                fechaInscripcion,
                torneo,
                tipoCupo,
                valorPagado);

        if (equipo == null) {
            throw new IllegalArgumentException(
                    "El equipo es obligatorio");
        }

        this.equipo = equipo;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    @Override
    public String getDescripcion() {
        return equipo.getNombre();
    }

    // Solo un integrante activo del equipo puede gestionar la inscripción
    @Override
    public boolean perteneceA(ParticipanteTorneo participante) {
        return participante instanceof Socio
                && equipo.getJugadoresHabilitados().contains(participante);
    }
}