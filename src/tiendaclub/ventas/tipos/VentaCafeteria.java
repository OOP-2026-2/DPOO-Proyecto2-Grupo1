package tiendaclub.ventas.tipos;

import java.time.LocalDate;

import tiendaclub.inventario.Bebida;
import tiendaclub.inventario.Producto;
import tiendaclub.inventario.Snack;
import tiendaclub.ventas.Venta;
import tiendaclub.ventas.descuentos.PoliticaDescuento;
import usuarios.Comprador;

public class VentaCafeteria extends Venta {
    public static final String RUBRO = "Cafetería";
    private static final double IMPUESTO_CONSUMO = 0.08;
    private static final double PROPINA_SUGERIDA = 0.10;
    private boolean incluirPropina;
    private double propina;

    public VentaCafeteria(LocalDate fecha, Comprador comprador) {
        super(fecha, comprador);
        this.incluirPropina = false;
        this.propina = 0;
    }

    public VentaCafeteria(LocalDate fecha, Comprador comprador, PoliticaDescuento politicaDescuento) {
        super(fecha, comprador, politicaDescuento);
        this.incluirPropina = false;
        this.propina = 0;
    }

    @Override
    public boolean admiteProducto(Producto producto) {
        return producto instanceof Bebida || producto instanceof Snack;
    }

    @Override
    public String getRubro() {
        return RUBRO;
    }

    @Override
    public double getCargosAdicionales() {
        return propina;
    }

    @Override
    public double calcularImpuesto() {
        return calcularBaseConDescuento()
                * IMPUESTO_CONSUMO;
    }

    public double calcularPropina() {
        return calcularBaseConDescuento()
                * PROPINA_SUGERIDA;
    }

    @Override
    public double calcularTotal() {

        double totalSinPropina = super.calcularTotal();

        if (incluirPropina) {
            propina = calcularPropina();
        } else {
            propina = 0;
        }

        establecerTotal(
                totalSinPropina + propina);

        return getTotal();
    }

    public void finalizarVenta(
            boolean incluirPropina) {

        this.incluirPropina = incluirPropina;
        calcularTotal();
    }

    public boolean isIncluirPropina() {
        return incluirPropina;
    }

    public double getPropina() {
        return propina;
    }
}