package clubdeportivo.torneos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import clubdeportivo.deportes.Modalidad;

public class InscripcionIndividualTorneo
        extends InscripcionTorneo {

    private final List<ParticipanteTorneo> participantes;

    public InscripcionIndividualTorneo(
            Torneo torneo,
            TipoCupo tipoCupo,
            double valorPagado) {

        this(
                LocalDate.now(),
                torneo,
                tipoCupo,
                valorPagado);
    }

    public InscripcionIndividualTorneo(
            LocalDate fechaInscripcion,
            Torneo torneo,
            TipoCupo tipoCupo,
            double valorPagado) {

        super(
                fechaInscripcion,
                torneo,
                tipoCupo,
                valorPagado);

        this.participantes = new ArrayList<ParticipanteTorneo>();
    }

    public void agregarParticipante(
            ParticipanteTorneo participante) {

        if (participante == null) {
            throw new IllegalArgumentException(
                    "El participante es obligatorio");
        }

        if (contieneParticipante(participante)) {
            throw new IllegalArgumentException(
                    "El participante ya está registrado "
                            + "en esta inscripción");
        }

        participantes.add(participante);
    }

    public boolean contieneParticipante(
            ParticipanteTorneo participante) {

        if (participante == null) {
            return false;
        }

        for (ParticipanteTorneo existente : participantes) {
            if (existente.getId().equalsIgnoreCase(
                    participante.getId())) {

                return true;
            }
        }

        return false;
    }

    public boolean cantidadParticipantesValida(
            Modalidad modalidad) {

        if (modalidad == Modalidad.SENCILLOS) {
            return participantes.size() == 1;
        }

        if (modalidad == Modalidad.DOBLES) {
            return participantes.size() == 2;
        }

        return false;
    }

    public List<ParticipanteTorneo> getParticipantes() {
        return new ArrayList<ParticipanteTorneo>(
                participantes);
    }

    @Override
    public String getDescripcion() {
        StringBuilder nombres = new StringBuilder();

        for (ParticipanteTorneo participante : participantes) {
            if (nombres.length() > 0) {
                nombres.append(" / ");
            }

            nombres.append(participante.getNombre());
        }

        return nombres.toString();
    }

    @Override
    public boolean perteneceA(ParticipanteTorneo participante) {
        return contieneParticipante(participante);
    }
}