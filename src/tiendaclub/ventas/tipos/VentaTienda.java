package tiendaclub.ventas.tipos;

import java.time.LocalDate;
import tiendaclub.inventario.ArticuloTienda;
import tiendaclub.inventario.Producto;
import tiendaclub.ventas.Venta;
import tiendaclub.ventas.descuentos.PoliticaDescuento;
import usuarios.Comprador;

public class VentaTienda extends Venta {
    public static final String RUBRO = "Tienda";
    private static final double IVA = 0.19;

    public VentaTienda(LocalDate fecha, Comprador comprador) {
        super(fecha, comprador);
    }

    public VentaTienda(
            LocalDate fecha,
            Comprador comprador,
            PoliticaDescuento politicaDescuento) {

        super(
                fecha,
                comprador,
                politicaDescuento);
    }

    @Override
    public boolean admiteProducto(Producto producto) {
        return producto instanceof ArticuloTienda;
    }

    @Override
    public String getRubro() {
        return RUBRO;
    }

    @Override
    public double calcularImpuesto() {
        return calcularBaseConDescuento() * IVA;
    }

    public void finalizarVenta() {
        calcularTotal();
    }
}