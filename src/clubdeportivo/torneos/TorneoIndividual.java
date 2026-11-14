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
        if (participante == null || participante instanceof Administrador) {
            return false;
        }

        if (participante instanceof Socio) {
            return socioHabilitado((Socio) participante);
        }

        if (participante instanceof Empleado) {
            Empleado empleado = (Empleado) participante;
            boolean tipoPermitido = empleado instanceof Entrenador || empleado instanceof Fisioterapeuta;
            return tipoPermitido && !estaDeTurno(empleado);
        }

        return participante instanceof EstudianteUniversidad || participante instanceof ParticipanteExterno;
    }

    private boolean socioHabilitado(Socio socio) {
        for (InscripcionAcademia inscripcion : deporte.getInscripciones()) {
            if (inscripcion.getSocio() == socio && inscripcion.estaActiva() && inscripcion.getNivel() == nivel) {
                return true;
            }
        }

        return false;
    }

    private boolean estaDeTurno(Empleado empleado) {
        int numeroDia = getFechaInicio().getDayOfWeek().getValue();
        DiaSemana diaTorneo = DiaSemana.values()[numeroDia - 1];

        for (Turno turno : empleado.getTurnos()) {
            if (turno.getDiaSemana() == diaTorneo) {
                return true;
            }
        }

        return false;
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

        double valorPagado = 0;
        InscripcionIndividualTorneo inscripcion = new InscripcionIndividualTorneo(this, tipoCupo, 0);

        for (ParticipanteTorneo participante : participantes) {
            if (!validarParticipante(participante)) {
                throw new IllegalArgumentException("Uno de los participantes no cumple las condiciones del torneo");
            }

            if (estaInscrito(participante)) {
                throw new IllegalArgumentException("Uno de los participantes ya está inscrito en el torneo");
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