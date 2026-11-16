package tiendaclub.ventas;

// Dinero que movió un rubro (tienda o cafetería) dentro de un reporte de ventas
public class ResumenRubro {
    private final String rubro;
    private int cantidadVentas;
    private double subtotal;
    private double descuentos;
    private double impuestos;
    private double cargosAdicionales;
    private double total;

    public ResumenRubro(String rubro) {
        if (rubro == null || rubro.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El rubro es obligatorio");
        }

        this.rubro = rubro;
        this.cantidadVentas = 0;
        this.subtotal = 0;
        this.descuentos = 0;
        this.impuestos = 0;
        this.cargosAdicionales = 0;
        this.total = 0;
    }

    public void acumular(Venta venta) {
        if (venta == null) {
            throw new IllegalArgumentException(
                    "La venta es obligatoria");
        }

        if (!venta.getRubro().equalsIgnoreCase(rubro)) {
            throw new IllegalArgumentException(
                    "La venta pertenece al rubro " + venta.getRubro());
        }

        cantidadVentas++;
        subtotal += venta.getSubtotal();
        descuentos += venta.getDescuento();
        impuestos += venta.getImpuesto();
        cargosAdicionales += venta.getCargosAdicionales();
        total += venta.getTotal();
    }

    public String getRubro() {
        return rubro;
    }

    public int getCantidadVentas() {
        return cantidadVentas;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDescuentos() {
        return descuentos;
    }

    public double getImpuestos() {
        return impuestos;
    }

    public double getCargosAdicionales() {
        return cargosAdicionales;
    }

    public double getTotal() {
        return total;
    }
}
