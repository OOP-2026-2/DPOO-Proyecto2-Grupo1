package clubdeportivo.torneos;

import java.time.LocalDate;

public class PremioMonetario implements PremioTorneo {

    private final InscripcionTorneo ganador;
    private final double valor;
    private final LocalDate fecha;
    private boolean entregado;

    public PremioMonetario(
            InscripcionTorneo ganador,
            double valor) {

        this(
                ganador,
                valor,
                LocalDate.now(),
                false);
    }

    public PremioMonetario(
            InscripcionTorneo ganador,
            double valor,
            LocalDate fecha,
            boolean entregado) {

        if (ganador == null) {
            throw new IllegalArgumentException(
                    "La inscripción ganadora es obligatoria");
        }

        if (valor <= 0) {
            throw new IllegalArgumentException(
                    "El valor del premio debe ser mayor que cero");
        }

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha del premio es obligatoria");
        }

        this.ganador = ganador;
        this.valor = valor;
        this.fecha = fecha;
        this.entregado = entregado;
    }

    public void entregar() {
        if (entregado) {
            throw new IllegalStateException(
                    "El premio ya fue entregado");
        }

        entregado = true;
    }

    @Override
    public InscripcionTorneo getGanador() {
        return ganador;
    }

    @Override
    public double getValor() {
        return valor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    @Override
    public boolean fueEntregado() {
        return entregado;
    }
}