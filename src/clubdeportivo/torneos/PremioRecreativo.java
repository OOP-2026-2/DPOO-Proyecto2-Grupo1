package clubdeportivo.torneos;

import java.util.ArrayList;
import java.util.List;
import usuarios.tipos.Socio;

public class PremioRecreativo implements PoliticaPremio {
    private final double porcentajeBono;
    private final int diasVigencia;

    public PremioRecreativo(double porcentajeBono, int diasVigencia) {
        if (porcentajeBono <= 0 || porcentajeBono > 1) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 1");
        }

        if (diasVigencia <= 0) {
            throw new IllegalArgumentException("Los días de vigencia deben ser mayores que cero");
        }

        this.porcentajeBono = porcentajeBono;
        this.diasVigencia = diasVigencia;
    }

    @Override
    public List<PremioTorneo> generarPremios(InscripcionTorneo ganador, double recaudo) {
        if (ganador == null) {
            throw new IllegalArgumentException("La inscripción ganadora es obligatoria");
        }

        List<PremioTorneo> premios = new ArrayList<PremioTorneo>();

        if (ganador instanceof InscripcionIndividualTorneo) {
            InscripcionIndividualTorneo inscripcion = (InscripcionIndividualTorneo) ganador;

            for (ParticipanteTorneo participante : inscripcion.getParticipantes()) {
                agregarBono(participante, ganador, premios);
            }
        }

        if (ganador instanceof InscripcionEquipoTorneo) {
            InscripcionEquipoTorneo inscripcion = (InscripcionEquipoTorneo) ganador;

            for (Socio socio : inscripcion.getEquipo().getJugadoresHabilitados()) {
                agregarBono(socio, ganador, premios);
            }
        }

        return premios;
    }

    private void agregarBono(ParticipanteTorneo participante, InscripcionTorneo ganador,
            List<PremioTorneo> premios) {
        if (participante instanceof Socio && participante.puedeRecibirBono()) {
            Socio socio = (Socio) participante;
            BonoDescuento bono = new BonoDescuento(socio, porcentajeBono, diasVigencia, ganador);
            socio.agregarBono(bono);
            premios.add(bono);
        }
    }

    public double getPorcentajeBono() {
        return porcentajeBono;
    }

    public int getDiasVigencia() {
        return diasVigencia;
    }
}
