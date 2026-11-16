package clubdeportivo.deportes;

import java.time.LocalDate;
import usuarios.tipos.Socio;

public class InscripcionEquipo {
    public static final int PARTIDOS_SUSPENSION_POR_TARJETAS = 1;

    private Socio socio;
    private Equipo equipo;
    private Posicion posicion;
    private int numeroCamiseta;
    private LocalDate fechaInscripcion;
    private EstadoInscripcion estado;
    private int partidosSuspension;

    public InscripcionEquipo(Socio socio, Equipo equipo, Posicion posicion, int numeroCamiseta) {
        if (socio == null) {
            throw new IllegalArgumentException(
                    "El socio es obligatorio");
        }

        if (equipo == null) {
            throw new IllegalArgumentException(
                    "El equipo es obligatorio");
        }

        if (posicion == null) {
            throw new IllegalArgumentException(
                    "La posición es obligatoria");
        }

        if (numeroCamiseta <= 0) {
            throw new IllegalArgumentException(
                    "El número de camiseta debe ser "
                            + "mayor que cero");
        }

        if (!equipo.getDeporte()
                .admitePosicion(posicion)) {

            throw new IllegalArgumentException(
                    "La posición no corresponde al deporte");
        }

        for (InscripcionEquipo existente : equipo.getInscripciones()) {

            boolean mismoNumero = existente.getNumeroCamiseta() == numeroCamiseta;

            // Un jugador suspendido sigue en el equipo y conserva su número
            if (mismoNumero
                    && existente.getEstado() != EstadoInscripcion.RETIRADA) {

                throw new IllegalArgumentException(
                        "El número de camiseta ya está "
                                + "asignado en el equipo");
            }
        }

        this.socio = socio;
        this.equipo = equipo;
        this.posicion = posicion;
        this.numeroCamiseta = numeroCamiseta;
        this.fechaInscripcion = LocalDate.now();
        this.estado = EstadoInscripcion.ACTIVA;
    }

    public boolean estaActiva() {
        return estado == EstadoInscripcion.ACTIVA;
    }

    public void suspender() {
        suspender(PARTIDOS_SUSPENSION_POR_TARJETAS);
    }

    // El jugador queda inhabilitado durante la cantidad de partidos oficiales indicada
    public void suspender(int partidos) {

        if (estado == EstadoInscripcion.RETIRADA) {
            throw new IllegalStateException(
                    "No se puede suspender una inscripción retirada");
        }

        if (partidos <= 0) {
            throw new IllegalArgumentException(
                    "La suspensión debe ser de al menos un partido");
        }

        estado = EstadoInscripcion.SUSPENDIDA;
        partidosSuspension = Math.max(partidosSuspension, partidos);
    }

    public boolean estaSuspendida() {
        return estado == EstadoInscripcion.SUSPENDIDA;
    }

    // Se llama cuando el equipo juega un partido oficial sin este jugador
    public void cumplirPartidoSuspension() {
        if (estado != EstadoInscripcion.SUSPENDIDA) {
            return;
        }

        if (partidosSuspension > 0) {
            partidosSuspension--;
        }

        if (partidosSuspension == 0) {
            estado = EstadoInscripcion.ACTIVA;
        }
    }

    public int getPartidosSuspension() {
        return partidosSuspension;
    }

    public void reactivar() {
        if (estado == EstadoInscripcion.RETIRADA) {
            throw new IllegalStateException(
                    "No se puede reactivar una inscripción retirada");
        }

        if (estado == EstadoInscripcion.ACTIVA) {
            return;
        }

        if (!equipo.isActivo()) {
            throw new IllegalStateException(
                    "No se puede reactivar la inscripción porque el equipo está inactivo");
        }

        // El jugador suspendido nunca dejó su cupo, por eso no se vuelve a verificar
        for (InscripcionEquipo existente : equipo.getInscripciones()) {
            boolean otraInscripcion = existente != this;
            boolean mismoNumero = existente.getNumeroCamiseta() == numeroCamiseta;

            if (otraInscripcion && mismoNumero && existente.estaActiva()) {
                throw new IllegalStateException(
                        "El número de camiseta ya está asignado");
            }
        }

        estado = EstadoInscripcion.ACTIVA;
        partidosSuspension = 0;
    }

    public void retirar() {
        estado = EstadoInscripcion.RETIRADA;
    }

    public Socio getSocio() {
        return socio;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public int getNumeroCamiseta() {
        return numeroCamiseta;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public EstadoInscripcion getEstado() {
        return estado;
    }

    @Override
    public String toString() {

        return socio.getNombre()
                + " - "
                + equipo.getNombre()
                + " - camiseta "
                + numeroCamiseta
                + " - "
                + estado;
    }
}