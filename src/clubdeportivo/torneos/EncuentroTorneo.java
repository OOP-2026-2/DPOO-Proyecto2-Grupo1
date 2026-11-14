package clubdeportivo.torneos;

import java.time.LocalDate;
import java.time.LocalTime;

public class EncuentroTorneo {
    private final String id;
    private final LocalDate fecha;
    private final LocalTime horaInicio;
    private final InscripcionTorneo inscripcionLocal;
    private final InscripcionTorneo inscripcionVisitante;
    private EstadoEncuentro estado;
    private String marcador;

    public EncuentroTorneo(String id, LocalDate fecha, LocalTime horaInicio,
            InscripcionTorneo inscripcionLocal, InscripcionTorneo inscripcionVisitante) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del encuentro es obligatorio");
        }

        if (fecha == null || horaInicio == null) {
            throw new IllegalArgumentException("La fecha y la hora son obligatorias");
        }

        if (inscripcionLocal == null || inscripcionVisitante == null) {
            throw new IllegalArgumentException("Las dos inscripciones son obligatorias");
        }

        if (inscripcionLocal == inscripcionVisitante) {
            throw new IllegalArgumentException("Los participantes del encuentro deben ser diferentes");
        }

        if (inscripcionLocal.getTorneo() != inscripcionVisitante.getTorneo()) {
            throw new IllegalArgumentException("Las inscripciones pertenecen a torneos diferentes");
        }

        this.id = id;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.inscripcionLocal = inscripcionLocal;
        this.inscripcionVisitante = inscripcionVisitante;
        this.estado = EstadoEncuentro.PROGRAMADO;
        this.marcador = null;
    }

    public void registrarResultado(String marcador) {
        if (estado != EstadoEncuentro.PROGRAMADO) {
            throw new IllegalStateException("El encuentro no está programado");
        }

        if (marcador == null || marcador.trim().isEmpty()) {
            throw new IllegalArgumentException("El marcador es obligatorio");
        }

        this.marcador = marcador;
    }

    public void finalizar() {
        if (estado != EstadoEncuentro.PROGRAMADO) {
            throw new IllegalStateException("El encuentro no se puede finalizar");
        }

        if (marcador == null) {
            throw new IllegalStateException("Debe registrar el resultado antes de finalizar");
        }

        estado = EstadoEncuentro.FINALIZADO;
    }

    public void cancelar() {
        if (estado == EstadoEncuentro.FINALIZADO) {
            throw new IllegalStateException("No se puede cancelar un encuentro finalizado");
        }

        estado = EstadoEncuentro.CANCELADO;
    }

    public boolean estaFinalizado() {
        return estado == EstadoEncuentro.FINALIZADO;
    }

    public boolean participantesDiferentes() {
        return inscripcionLocal != inscripcionVisitante;
    }

    public String getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public InscripcionTorneo getInscripcionLocal() {
        return inscripcionLocal;
    }

    public InscripcionTorneo getInscripcionVisitante() {
        return inscripcionVisitante;
    }

    public EstadoEncuentro getEstado() {
        return estado;
    }

    public String getMarcador() {
        return marcador;
    }
}