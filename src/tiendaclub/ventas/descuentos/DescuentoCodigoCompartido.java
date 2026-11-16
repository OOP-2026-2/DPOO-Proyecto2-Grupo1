package tiendaclub.ventas.descuentos;

public class DescuentoCodigoCompartido
        implements PoliticaDescuento {

    private static final double PORCENTAJE = 0.08;
    private final String codigo;

    public DescuentoCodigoCompartido(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El código compartido es obligatorio");
        }

        this.codigo = codigo.trim();
    }

    // Solo para ventas antiguas guardadas sin el código: una venta nueva con este
    // descuento se rechaza al procesarla, porque no hay código que validar
    public DescuentoCodigoCompartido() {
        this.codigo = null;
    }

    @Override
    public double calcularDescuento(
            double subtotal) {

        return subtotal * PORCENTAJE;
    }

    public String getCodigo() {
        return codigo;
    }
}
