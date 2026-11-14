package clubdeportivo.torneos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import clubdeportivo.deportes.Categoria;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.Equipo;

public class TorneoConjunto extends Torneo {
    private final DeporteConjunto deporte;
    private final Categoria categoria;

    public TorneoConjunto(String id, String nombre, LocalDate fechaInicio, LocalTime horaInicio, LocalTime horaFin,
            FormatoTorneo formato, int capacidad, TipoTorneo tipo, LocalDateTime fechaLiberacionPreferenciales,
            PoliticaPremio politicaPremio, double tarifaInscripcion, DeporteConjunto deporte, Categoria categoria) {
        super(id, nombre, fechaInicio, horaInicio, horaFin, formato, capacidad, tipo,
                fechaLiberacionPreferenciales, politicaPremio, tarifaInscripcion);

        if (deporte == null) {
            throw new IllegalArgumentException("El deporte de conjunto es obligatorio");
        }

        if (categoria == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }

        this.deporte = deporte;
        this.categoria = categoria;
    }

    public boolean validarEquipo(Equipo equipo) {
        if (equipo == null || !equipo.isActivo()) {
            return false;
        }

        if (equipo.getDeporte() != deporte || equipo.getCategoria() != categoria) {
            return false;
        }

        return deporte.cumpleMinimoJugadores(equipo.getJugadoresHabilitados().size());
    }

    public InscripcionEquipoTorneo inscribirEquipo(Equipo equipo, TipoCupo tipoCupo) {
        if (!validarEquipo(equipo)) {
            throw new IllegalArgumentException("El equipo no cumple las condiciones del torneo");
        }

        if (tipoCupo == null) {
            throw new IllegalArgumentException("El tipo de cupo es obligatorio");
        }

        if (tipoCupo == TipoCupo.PREFERENCIAL
                && !cumpleCriterioPreferencial(equipo)) {

            throw new IllegalArgumentException(
                    "El cupo preferencial requiere un puntaje histórico del equipo mayor que cero");
        }

        for (InscripcionTorneo inscripcion : getInscripcionesActivas()) {
            if (inscripcion instanceof InscripcionEquipoTorneo) {
                InscripcionEquipoTorneo existente = (InscripcionEquipoTorneo) inscripcion;

                if (existente.getEquipo() == equipo) {
                    throw new IllegalArgumentException("El equipo ya está inscrito en el torneo");
                }
            }
        }

        double valorPagado = getTipo() == TipoTorneo.RECREATIVO ? 0 : getTarifaInscripcion();
        InscripcionEquipoTorneo inscripcion = new InscripcionEquipoTorneo(this, tipoCupo, valorPagado, equipo);
        agregarInscripcion(inscripcion);
        return inscripcion;
    }

    public boolean cumpleCriterioPreferencial(
            Equipo equipo) {

        return validarEquipo(equipo)
                && equipo.getPuntajeHistorico() > 0;
    }

    public DeporteConjunto getDeporte() {
        return deporte;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}
