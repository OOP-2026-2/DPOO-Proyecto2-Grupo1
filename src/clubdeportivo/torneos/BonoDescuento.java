package clubdeportivo.torneos;

import java.time.LocalDate;

import usuarios.tipos.Socio;

public class BonoDescuento
        implements PremioTorneo {

    private final Socio socio;
    private final double porcentaje;
    private final LocalDate fechaCreacion;
    private final LocalDate fechaVencimiento;
    private EstadoBono estado;
    private final InscripcionTorneo inscripcionGanadora;

    public BonoDescuento(
            Socio socio,
            double porcentaje,
            int diasVigencia,
            InscripcionTorneo inscripcionGanadora) {

        this(
                socio,
                porcentaje,
                LocalDate.now(),
                calcularFechaVencimiento(diasVigencia),
                EstadoBono.DISPONIBLE,
                inscripcionGanadora);
    }

    public BonoDescuento(
            Socio socio,
            double porcentaje,
            LocalDate fechaCreacion,
            LocalDate fechaVencimiento,
            EstadoBono estado,
            InscripcionTorneo inscripcionGanadora) {

        if (socio == null) {
            throw new IllegalArgumentException(
                    "El socio es obligatorio");
        }

        if (porcentaje <= 0 || porcentaje > 1) {
            throw new IllegalArgumentException(
                    "El porcentaje debe estar entre 0 y 1");
        }

        if (fechaCreacion == null
                || fechaVencimiento == null
                || estado == null) {

            throw new IllegalArgumentException(
                    "Las fechas y el estado del bono son obligatorios");
        }

        if (fechaVencimiento.isBefore(fechaCreacion)) {
            throw new IllegalArgumentException(
                    "La fecha de vencimiento no puede ser "
                            + "anterior a la creación");
        }

        if (inscripcionGanadora == null) {
            throw new IllegalArgumentException(
                    "La inscripción ganadora es obligatoria");
        }

        this.socio = socio;
        this.porcentaje = porcentaje;
        this.fechaCreacion = fechaCreacion;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
        this.inscripcionGanadora = inscripcionGanadora;
    }

    private static LocalDate calcularFechaVencimiento(
            int diasVigencia) {

        if (diasVigencia <= 0) {
            throw new IllegalArgumentException(
                    "Los días de vigencia deben ser mayores que cero");
        }

        return LocalDate.now().plusDays(
                diasVigencia);
    }

    public double calcularDescuento(
            double subtotal) {

        if (subtotal < 0) {
            throw new IllegalArgumentException(
                    "El subtotal no puede ser negativo");
        }

        if (!estaDisponible()) {
            return 0;
        }

        return subtotal * porcentaje;
    }

    public void usar() {
        if (!estaDisponible()) {
            throw new IllegalStateException(
                    "El bono no está disponible");
        }

        estado = EstadoBono.UTILIZADO;
    }

    public boolean estaDisponible() {
        if (estado == EstadoBono.DISPONIBLE
                && LocalDate.now().isAfter(
                        fechaVencimiento)) {

            estado = EstadoBono.VENCIDO;
        }

        return estado == EstadoBono.DISPONIBLE;
    }

    @Override
    public InscripcionTorneo getGanador() {
        return inscripcionGanadora;
    }

    @Override
    public double getValor() {
        return porcentaje;
    }

    @Override
    public boolean fueEntregado() {
        return true;
    }

    public Socio getSocio() {
        return socio;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public EstadoBono getEstado() {
        estaDisponible();
        return estado;
    }

    @Override
    public String getDescripcion() {
        return "Bono de " + Math.round(porcentaje * 100) + "% para "
                + socio.getNombre() + ", vence el " + fechaVencimiento
                + " (" + getEstado() + ")";
    }
}
