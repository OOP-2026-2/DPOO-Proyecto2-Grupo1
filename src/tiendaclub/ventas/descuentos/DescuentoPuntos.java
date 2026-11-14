package tiendaclub.ventas.descuentos;

public class DescuentoPuntos implements PoliticaDescuento {
    private final double puntosRedimidos;

    public DescuentoPuntos(double puntosRedimidos) {
        if (puntosRedimidos <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de puntos debe ser mayor que cero");
        }

        this.puntosRedimidos = puntosRedimidos;
    }

    @Override
    public double calcularDescuento(double subtotal) {
        if (subtotal < 0) {
            throw new IllegalArgumentException(
                    "El subtotal no puede ser negativo");
        }

        return puntosRedimidos;
    }

    public double getPuntosRedimidos() {
        return puntosRedimidos;
    }
}
