package tiendaclub.ventas.descuentos;

import clubdeportivo.torneos.BonoDescuento;

public class DescuentoBonoTorneo implements PoliticaDescuento {
    private final BonoDescuento bono;

    public DescuentoBonoTorneo(BonoDescuento bono) {
        if (bono == null) {
            throw new IllegalArgumentException("El bono es obligatorio");
        }

        if (!bono.estaDisponible()) {
            throw new IllegalArgumentException("El bono no está disponible");
        }

        this.bono = bono;
    }

    @Override
    public double calcularDescuento(double subtotal) {
        return bono.calcularDescuento(subtotal);
    }

    public BonoDescuento getBono() {
        return bono;
    }
}
