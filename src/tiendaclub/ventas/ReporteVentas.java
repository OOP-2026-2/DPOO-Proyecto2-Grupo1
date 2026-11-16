package tiendaclub.ventas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tiendaclub.ventas.detalles.DetalleVenta;

// Ventas de un rango de fechas (ambas incluidas): dinero por rubro y unidades por tipo de producto
public class ReporteVentas {
    private final LocalDate desde;
    private final LocalDate hasta;
    private final List<ResumenRubro> rubros;
    private final Map<String, Integer> unidadesPorTipoProducto;
    private final Map<String, Double> ventasPorTipoProducto;

    public ReporteVentas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException(
                    "Las fechas del reporte son obligatorias");
        }

        if (hasta.isBefore(desde)) {
            throw new IllegalArgumentException(
                    "La fecha final no puede ser anterior a la fecha inicial");
        }

        this.desde = desde;
        this.hasta = hasta;
        this.rubros = new ArrayList<ResumenRubro>();
        this.unidadesPorTipoProducto = new LinkedHashMap<String, Integer>();
        this.ventasPorTipoProducto = new LinkedHashMap<String, Double>();
    }

    // Permite mostrar un rubro aunque no haya tenido ventas en el periodo
    public void registrarRubro(String rubro) {
        if (buscarRubro(rubro) == null) {
            rubros.add(new ResumenRubro(rubro));
        }
    }

    public boolean incluye(LocalDate fecha) {
        return fecha != null
                && !fecha.isBefore(desde)
                && !fecha.isAfter(hasta);
    }

    public void agregarVenta(Venta venta) {
        if (venta == null) {
            throw new IllegalArgumentException(
                    "La venta es obligatoria");
        }

        if (!incluye(venta.getFecha())) {
            throw new IllegalArgumentException(
                    "La venta del " + venta.getFecha()
                            + " está fuera del periodo del reporte");
        }

        registrarRubro(venta.getRubro());
        buscarRubro(venta.getRubro()).acumular(venta);

        for (DetalleVenta detalle : venta.getDetalles()) {
            String tipo = detalle.getProducto().getTipoProducto();
            Integer unidades = unidadesPorTipoProducto.get(tipo);
            Double dinero = ventasPorTipoProducto.get(tipo);

            unidadesPorTipoProducto.put(tipo,
                    (unidades == null ? 0 : unidades) + detalle.getCantidad());
            ventasPorTipoProducto.put(tipo,
                    (dinero == null ? 0 : dinero) + detalle.getSubtotal());
        }
    }

    public ResumenRubro buscarRubro(String rubro) {
        if (rubro == null) {
            return null;
        }

        for (ResumenRubro resumen : rubros) {
            if (resumen.getRubro().equalsIgnoreCase(rubro)) {
                return resumen;
            }
        }

        return null;
    }

    public int getCantidadVentas() {
        int cantidad = 0;

        for (ResumenRubro resumen : rubros) {
            cantidad += resumen.getCantidadVentas();
        }

        return cantidad;
    }

    public double getTotalVendido() {
        double total = 0;

        for (ResumenRubro resumen : rubros) {
            total += resumen.getTotal();
        }

        return total;
    }

    public double getTotalDescuentos() {
        double total = 0;

        for (ResumenRubro resumen : rubros) {
            total += resumen.getDescuentos();
        }

        return total;
    }

    public double getTotalImpuestos() {
        double total = 0;

        for (ResumenRubro resumen : rubros) {
            total += resumen.getImpuestos();
        }

        return total;
    }

    public LocalDate getDesde() {
        return desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public List<ResumenRubro> getRubros() {
        return new ArrayList<ResumenRubro>(rubros);
    }

    public Map<String, Integer> getUnidadesPorTipoProducto() {
        return new LinkedHashMap<String, Integer>(unidadesPorTipoProducto);
    }

    public Map<String, Double> getVentasPorTipoProducto() {
        return new LinkedHashMap<String, Double>(ventasPorTipoProducto);
    }
}
