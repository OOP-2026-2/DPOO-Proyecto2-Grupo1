package tiendaclub.ventas.descuentos;

import clubdeportivo.torneos.BonoDescuento;

public class DescuentoBonoTorneo implements PoliticaDescuento {
    private final BonoDescuento bono;
    private final double porcentajeAplicado;

    public DescuentoBonoTorneo(BonoDescuento bono) {
        if (bono == null) {
            throw new IllegalArgumentException("El bono es obligatorio");
        }

        if (!bono.estaDisponible()) {
            throw new IllegalArgumentException("El bono no está disponible");
        }

        this.bono = bono;
        this.porcentajeAplicado = bono.getPorcentaje();
    }

    public DescuentoBonoTorneo(BonoDescuento bono, boolean cargaHistorica) {
        if (bono == null) {
            throw new IllegalArgumentException("El bono es obligatorio");
        }

        if (!cargaHistorica && !bono.estaDisponible()) {
            throw new IllegalArgumentException("El bono no está disponible");
        }

        this.bono = bono;
        this.porcentajeAplicado = bono.getPorcentaje();
    }

    @Override
    public double calcularDescuento(double subtotal) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo");
        }

        return subtotal * porcentajeAplicado;
    }

    public BonoDescuento getBono() {
        return bono;
    }
}
