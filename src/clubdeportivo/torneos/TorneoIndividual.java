package clubdeportivo.torneos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import clubdeportivo.deportes.DeporteIndividual;
import clubdeportivo.deportes.InscripcionAcademia;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.deportes.Nivel;
import clubdeportivo.turnos.DiaSemana;
import clubdeportivo.turnos.Turno;
import usuarios.tipos.Empleado;
import usuarios.tipos.EstudianteUniversidad;
import usuarios.tipos.ParticipanteExterno;
import usuarios.tipos.Socio;
import usuarios.tipos.empleados.Administrador;
import usuarios.tipos.empleados.Entrenador;
import usuarios.tipos.empleados.Fisioterapeuta;

public class TorneoIndividual extends Torneo {
    private final DeporteIndividual deporte;
    private final Nivel nivel;
    private final Modalidad modalidad;

    public TorneoIndividual(String id, String nombre, LocalDate fechaInicio, LocalTime horaInicio, LocalTime horaFin,
            FormatoTorneo formato, int capacidad, TipoTorneo tipo, LocalDateTime fechaLiberacionPreferenciales,
            PoliticaPremio politicaPremio, double tarifaInscripcion, DeporteIndividual deporte, Nivel nivel,
            Modalidad modalidad) {
        super(id, nombre, fechaInicio, horaInicio, horaFin, formato, capacidad, tipo,
                fechaLiberacionPreferenciales, politicaPremio, tarifaInscripcion);

        if (deporte == null) {
            throw new IllegalArgumentException("El deporte individual es obligatorio");
        }

        if (nivel == null || modalidad == null) {
            throw new IllegalArgumentException("El nivel y la modalidad son obligatorios");
        }

        this.deporte = deporte;
        this.nivel = nivel;
        this.modalidad = modalidad;
    }

    public boolean validarParticipante(ParticipanteTorneo participante) {
        return motivoRechazo(participante) == null;
    }

    /*
     * Devuelve por qué el participante no puede inscribirse
     * o null si cumple las condiciones del torneo.
     */
    public String motivoRechazo(ParticipanteTorneo participante) {
        if (participante == null) {
            return "El participante es obligatorio";
        }

        if (participante instanceof Administrador) {
            return "Los administradores no pueden participar en torneos";
        }

        if (participante instanceof Socio) {
            return motivoRechazoSocio((Socio) participante);
        }

        if (participante instanceof Empleado) {
            Empleado empleado = (Empleado) participante;

            if (!(empleado instanceof Entrenador || empleado instanceof Fisioterapeuta)) {
                return "Solo los entrenadores y fisioterapeutas pueden participar";
            }

            if (estaDeTurno(empleado)) {
                return empleado.getNombre() + " tiene turno el "
                        + obtenerDiaTorneo() + ", día del torneo";
            }

            return null;
        }

        if (participante instanceof EstudianteUniversidad
                || participante instanceof ParticipanteExterno) {
            return null;
        }

        return "Este tipo de usuario no puede participar en torneos individuales";
    }

    private String motivoRechazoSocio(Socio socio) {
        InscripcionAcademia inscripcion = deporte.buscarInscripcionActiva(socio);

        if (inscripcion == null) {
            return "El socio " + socio.getNombre()
                    + " no tiene una inscripción activa en la academia de "
                    + deporte.getNombre();
        }

        if (inscripcion.getNivel() != nivel) {
            return "El socio " + socio.getNombre() + " está en el nivel "
                    + inscripcion.getNivel() + " y el torneo es de nivel " + nivel;
        }

        return null;
    }

    // Regla del documento (HU-T07): el empleado no puede tener turno el día del torneo
    private boolean estaDeTurno(Empleado empleado) {
        DiaSemana diaTorneo = obtenerDiaTorneo();

        for (Turno turno : empleado.getTurnos()) {
            if (turno.getDiaSemana() == diaTorneo) {
                return true;
            }
        }

        return false;
    }

    private DiaSemana obtenerDiaTorneo() {
        int numeroDia = getFechaInicio().getDayOfWeek().getValue();
        return DiaSemana.values()[numeroDia - 1];
    }

    @Override
    protected void actualizarDesempenoParticipantes() {
        for (PosicionTorneo posicion : getPosiciones()) {
            if (posicion.getPuntos() <= 0) {
                continue;
            }

            InscripcionIndividualTorneo inscripcion =
                    (InscripcionIndividualTorneo) posicion.getInscripcion();

            for (ParticipanteTorneo participante : inscripcion.getParticipantes()) {
                if (participante instanceof Socio) {
                    InscripcionAcademia academia = deporte.buscarInscripcionActiva(
                            (Socio) participante);

                    if (academia != null) {
                        academia.sumarPuntaje(posicion.getPuntos());
                    }
                }
            }
        }
    }

    public InscripcionIndividualTorneo inscribirParticipantes(List<ParticipanteTorneo> participantes,
            TipoCupo tipoCupo) {
        if (participantes == null) {
            throw new IllegalArgumentException("La lista de participantes es obligatoria");
        }

        int cantidadEsperada = modalidad == Modalidad.SENCILLOS ? 1 : 2;

        if (participantes.size() != cantidadEsperada) {
            throw new IllegalArgumentException("La cantidad de participantes no corresponde a la modalidad");
        }

        if (tipoCupo == null) {
            throw new IllegalArgumentException("El tipo de cupo es obligatorio");
        }

        if (tipoCupo == TipoCupo.PREFERENCIAL
                && !cumpleCriterioPreferencial(participantes)) {

            throw new IllegalArgumentException(
                    "El cupo preferencial requiere ranking deportivo registrado para todos los participantes");
        }

        double valorPagado = 0;
        InscripcionIndividualTorneo inscripcion = new InscripcionIndividualTorneo(this, tipoCupo, 0);

        for (ParticipanteTorneo participante : participantes) {
            String motivo = motivoRechazo(participante);

            if (motivo != null) {
                throw new IllegalArgumentException(motivo);
            }

            if (estaInscrito(participante)) {
                throw new IllegalArgumentException(participante.getNombre()
                        + " ya está inscrito en el torneo");
            }

            inscripcion.agregarParticipante(participante);
            valorPagado += calcularValorInscripcion(participante);
        }

        if (!inscripcion.cantidadParticipantesValida(modalidad)) {
            throw new IllegalArgumentException("La inscripción no cumple la modalidad del torneo");
        }

        inscripcion = crearInscripcionConValor(participantes, tipoCupo, valorPagado);
        agregarInscripcion(inscripcion);
        return inscripcion;
    }

    public boolean cumpleCriterioPreferencial(
            List<ParticipanteTorneo> participantes) {

        if (participantes == null || participantes.isEmpty()) {
            return false;
        }

        for (ParticipanteTorneo participante : participantes) {
            if (!(participante instanceof Socio)) {
                return false;
            }

            InscripcionAcademia inscripcion = buscarInscripcionAcademia(
                    (Socio) participante);

            if (inscripcion == null
                    || inscripcion.getPuntajeRanking() <= 0) {

                return false;
            }
        }

        return true;
    }

    public double calcularRankingPreferencial(
            List<ParticipanteTorneo> participantes) {

        if (!cumpleCriterioPreferencial(participantes)) {
            return 0;
        }

        double total = 0;

        for (ParticipanteTorneo participante : participantes) {
            total += buscarInscripcionAcademia(
                    (Socio) participante).getPuntajeRanking();
        }

        return total / participantes.size();
    }

    private InscripcionAcademia buscarInscripcionAcademia(
            Socio socio) {

        for (InscripcionAcademia inscripcion : deporte.getInscripciones()) {
            if (inscripcion.getSocio() == socio
                    && inscripcion.estaActiva()
                    && inscripcion.getNivel() == nivel) {

                return inscripcion;
            }
        }

        return null;
    }

    private InscripcionIndividualTorneo crearInscripcionConValor(List<ParticipanteTorneo> participantes,
            TipoCupo tipoCupo, double valorPagado) {
        InscripcionIndividualTorneo inscripcion = new InscripcionIndividualTorneo(this, tipoCupo, valorPagado);

        for (ParticipanteTorneo participante : participantes) {
            inscripcion.agregarParticipante(participante);
        }

        return inscripcion;
    }

    private boolean estaInscrito(ParticipanteTorneo participante) {
        for (InscripcionTorneo inscripcion : getInscripcionesActivas()) {
            if (inscripcion instanceof InscripcionIndividualTorneo) {
                InscripcionIndividualTorneo individual = (InscripcionIndividualTorneo) inscripcion;

                if (individual.contieneParticipante(participante)) {
                    return true;
                }
            }
        }

        return false;
    }

    public DeporteIndividual getDeporte() {
        return deporte;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }
}
