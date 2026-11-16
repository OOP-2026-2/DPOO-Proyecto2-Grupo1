package clubdeportivo.torneos;

import java.time.LocalDate;

public abstract class InscripcionTorneo {
    private final LocalDate fechaInscripcion;
    private EstadoInscripcionTorneo estado;
    private final TipoCupo tipoCupo;
    private final Torneo torneo;
    private final double valorPagado;

    public InscripcionTorneo(Torneo torneo, TipoCupo tipoCupo, double valorPagado) {
        this(LocalDate.now(), torneo, tipoCupo, valorPagado);
    }

    public InscripcionTorneo(LocalDate fechaInscripcion, Torneo torneo, TipoCupo tipoCupo, double valorPagado) {
        if (fechaInscripcion == null) {
            throw new IllegalArgumentException("La fecha de inscripción es obligatoria");
        }

        if (torneo == null) {
            throw new IllegalArgumentException("El torneo es obligatorio");
        }

        if (tipoCupo == null) {
            throw new IllegalArgumentException("El tipo de cupo es obligatorio");
        }

        if (valorPagado < 0) {
            throw new IllegalArgumentException("El valor pagado no puede ser negativo");
        }

        this.fechaInscripcion = fechaInscripcion;
        this.torneo = torneo;
        this.tipoCupo = tipoCupo;
        this.valorPagado = valorPagado;
        this.estado = EstadoInscripcionTorneo.ACTIVA;
    }

    public boolean estaActiva() {
        return estado == EstadoInscripcionTorneo.ACTIVA;
    }

    public void retirar() {
        if (!estaActiva()) {
            throw new IllegalStateException("La inscripción ya está retirada");
        }

        estado = EstadoInscripcionTorneo.RETIRADA;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public EstadoInscripcionTorneo getEstado() {
        return estado;
    }

    public TipoCupo getTipoCupo() {
        return tipoCupo;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public double getValorPagado() {
        return valorPagado;
    }

    // Nombre de quien juega: el equipo o los participantes
    public abstract String getDescripcion();

    // Indica si el participante puede consultar o retirar esta inscripción
    public abstract boolean perteneceA(ParticipanteTorneo participante);
}