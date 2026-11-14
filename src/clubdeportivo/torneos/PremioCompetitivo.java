package clubdeportivo.torneos;

import java.util.ArrayList;
import java.util.List;

public class PremioCompetitivo implements PoliticaPremio {
    private final double porcentajeRecaudo;

    public PremioCompetitivo(double porcentajeRecaudo) {
        if (porcentajeRecaudo <= 0 || porcentajeRecaudo > 1) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 1");
        }

        this.porcentajeRecaudo = porcentajeRecaudo;
    }

    @Override
    public List<PremioTorneo> generarPremios(InscripcionTorneo ganador, double recaudo) {
        if (ganador == null) {
            throw new IllegalArgumentException("La inscripción ganadora es obligatoria");
        }

        List<PremioTorneo> premios = new ArrayList<PremioTorneo>();

        double valor = calcularValor(recaudo);

        if (valor > 0) {
            PremioMonetario premio = new PremioMonetario(ganador, valor);

            if (puedeRecibirPremio(ganador)) {
                premio.entregar();
            }

            premios.add(premio);
        }

        return premios;
    }

    private boolean puedeRecibirPremio(InscripcionTorneo ganador) {
        if (ganador instanceof InscripcionEquipoTorneo) {
            return true;
        }

        if (ganador instanceof InscripcionIndividualTorneo) {
            InscripcionIndividualTorneo inscripcion = (InscripcionIndividualTorneo) ganador;

            if (inscripcion.getParticipantes().isEmpty()) {
                return false;
            }

            for (ParticipanteTorneo participante : inscripcion.getParticipantes()) {
                if (!participante.puedeRecibirPremioDinero()) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public double calcularValor(double recaudo) {
        if (recaudo < 0) {
            throw new IllegalArgumentException("El recaudo no puede ser negativo");
        }

        return recaudo * porcentajeRecaudo;
    }

    public double getPorcentajeRecaudo() {
        return porcentajeRecaudo;
    }
}
