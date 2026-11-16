package clubdeportivo.instalaciones;

import java.time.LocalDate;
import java.time.LocalTime;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.DeporteIndividual;
import clubdeportivo.deportes.Modalidad;
import usuarios.tipos.Socio;

public class Reserva {
    private final Socio socio;
    private final Instalacion instalacion;
    private final DeporteIndividual disciplina;
    private final LocalDate fecha;
    private final LocalTime horaInicio;
    private final int duracionMinutos;
    private final int numeroJugadores;
    private final Modalidad modalidad;
    private final DeporteConjunto deporteConjunto;

    public Reserva(Socio socio, Instalacion instalacion, LocalDate fecha, LocalTime horaInicio, int duracionMinutos,
            int numeroJugadores, Modalidad modalidad) {
        this(socio, instalacion, fecha, horaInicio, duracionMinutos,
                numeroJugadores, modalidad, null);
    }

    // Canchas de conjunto: el deporte define el mínimo de jugadores de la reserva
    public Reserva(Socio socio, Instalacion instalacion, LocalDate fecha, LocalTime horaInicio, int duracionMinutos,
            int numeroJugadores, DeporteConjunto deporteConjunto) {
        this(socio, instalacion, fecha, horaInicio, duracionMinutos,
                numeroJugadores, null, deporteConjunto);
    }

    public Reserva(Socio socio, Instalacion instalacion, LocalDate fecha, LocalTime horaInicio, int duracionMinutos,
            int numeroJugadores, Modalidad modalidad, DeporteConjunto deporteConjunto) {
        if (socio == null) {
            throw new IllegalArgumentException(
                    "El socio es obligatorio");
        }

        if (instalacion == null) {
            throw new IllegalArgumentException(
                    "La instalación es obligatoria");
        }

        if (fecha == null || horaInicio == null) {
            throw new IllegalArgumentException(
                    "La fecha y la hora de inicio son obligatorias");
        }

        if (duracionMinutos <= 0) {
            throw new IllegalArgumentException(
                    "La duración debe ser mayor que cero");
        }

        boolean canchaConjunto = instalacion.getTipo() == TipoInstalacion.CANCHA_CONJUNTO;

        if (!canchaConjunto && modalidad == null) {
            throw new IllegalArgumentException(
                    "La modalidad es obligatoria");
        }

        if (canchaConjunto && modalidad == null && deporteConjunto == null) {
            throw new IllegalArgumentException(
                    "Para una cancha de conjunto se debe indicar el deporte");
        }

        this.socio = socio;
        this.instalacion = instalacion;
        this.disciplina = instalacion.getDisciplina();
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.duracionMinutos = duracionMinutos;
        this.numeroJugadores = numeroJugadores;
        this.modalidad = modalidad;
        this.deporteConjunto = deporteConjunto;

        // La fecha pasada se rechaza al registrar la reserva en el club, no aquí,
        // para poder cargar el historial guardado
        String motivo = motivoCantidadJugadoresInvalida();

        if (motivo != null) {
            throw new IllegalArgumentException(motivo);
        }
    }

    public LocalTime calcularHoraFin() {
        return horaInicio.plusMinutes(duracionMinutos);
    }

    public boolean esFechaValida() {
        return !fecha.isBefore(LocalDate.now());
    }

    public boolean cantidadJugadoresValida() {
        return motivoCantidadJugadoresInvalida() == null;
    }

    // Devuelve por qué la cantidad de jugadores no sirve, o null si es válida
    public String motivoCantidadJugadoresInvalida() {
        if (!instalacion.admiteJugadores(numeroJugadores)) {
            return "La instalación admite entre 1 y "
                    + instalacion.getCapacidadMaxima() + " jugadores";
        }

        if (instalacion.getTipo() == TipoInstalacion.CANCHA_CONJUNTO
                && deporteConjunto != null
                && !deporteConjunto.cumpleMinimoJugadores(numeroJugadores)) {
            return "El deporte " + deporteConjunto.getNombre()
                    + " exige al menos " + deporteConjunto.getMinimoJugadores()
                    + " jugadores para reservar la cancha";
        }

        if (!cantidadSegunModalidadValida()) {
            return "La cantidad de jugadores no corresponde "
                    + "al tipo de instalación y modalidad";
        }

        return null;
    }

    private boolean cantidadSegunModalidadValida() {
        switch (instalacion.getTipo()) {

            case CANCHA_TENIS:
            case MESA_TENIS_MESA:

                if (modalidad == Modalidad.SENCILLOS) {
                    return numeroJugadores == 2;
                }

                if (modalidad == Modalidad.DOBLES) {
                    return numeroJugadores == 4;
                }

                return false;

            case CANCHA_PADEL:

                return modalidad == Modalidad.DOBLES
                        && numeroJugadores == 4;

            case CANCHA_CONJUNTO:

                return numeroJugadores > 0
                        && numeroJugadores <= instalacion.getCapacidadMaxima();

            default:
                return false;
        }
    }

    public boolean seCruzaCon(Reserva otraReserva) {

        if (otraReserva == null) {
            return false;
        }

        boolean mismaInstalacion = instalacion == otraReserva.getInstalacion();

        boolean mismaFecha = fecha.equals(otraReserva.getFecha());

        boolean horariosSuperpuestos = horaInicio.isBefore(otraReserva.calcularHoraFin())
                && otraReserva.getHoraInicio().isBefore(calcularHoraFin());

        return mismaInstalacion
                && mismaFecha
                && horariosSuperpuestos;
    }

    public Socio getSocio() {
        return socio;
    }

    public Instalacion getInstalacion() {
        return instalacion;
    }

    public DeporteIndividual getDisciplina() {
        return disciplina;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public int getNumeroJugadores() {
        return numeroJugadores;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }

    public DeporteConjunto getDeporteConjunto() {
        return deporteConjunto;
    }

    @Override
    public String toString() {
        return fecha
                + " " + horaInicio
                + " - " + calcularHoraFin()
                + " - " + instalacion.getId()
                + " - " + (deporteConjunto != null
                        ? deporteConjunto.getNombre()
                        : String.valueOf(modalidad));
    }
}