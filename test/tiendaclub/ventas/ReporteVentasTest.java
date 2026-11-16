package tiendaclub.ventas;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubdeportivo.ClubDeportivo;
import tiendaclub.inventario.ArticuloTienda;
import tiendaclub.inventario.Bebida;
import tiendaclub.inventario.Snack;
import tiendaclub.ventas.tipos.VentaCafeteria;
import tiendaclub.ventas.tipos.VentaTienda;
import usuarios.tipos.Socio;

@DisplayName("Reporte de ventas por rango de fechas")
public class ReporteVentasTest {
    // Tres días seguidos con ventas: el rango del reporte cubre solo los dos primeros
    private static final LocalDate DIA_UNO = LocalDate.of(2026, 10, 1);
    private static final LocalDate DIA_DOS = DIA_UNO.plusDays(1);
    private static final LocalDate DIA_FUERA = DIA_UNO.plusDays(5);
    private static final double PRECIO_BALON = 80000;
    private static final double PRECIO_JUGO = 6000;
    private static final double PRECIO_GALLETA = 3000;
    // La cafetería sugiere una propina del 10 % sobre la base de la venta
    private static final double PROPINA_SUGERIDA = 0.10;

    private ClubDeportivo club;
    private Socio socio;
    private ArticuloTienda balon;
    private Bebida jugo;
    private Snack galleta;

    @BeforeEach
    public void registrarVentas() {
        club = new ClubDeportivo();
        socio = new Socio("S1", "Socio", LocalDate.of(1995, 5, 5), "socio", "clave");
        club.registrarUsuario(socio);
        balon = new ArticuloTienda("Balón", PRECIO_BALON, "Implementos");
        jugo = new Bebida("Jugo", PRECIO_JUGO, false);
        galleta = new Snack("Galleta", PRECIO_GALLETA, Collections.<String>emptyList());

        club.registrarVenta(ventaTienda(DIA_UNO, 1));
        club.registrarVenta(ventaCafeteria(DIA_DOS, 2, true));
        club.registrarVenta(ventaTienda(DIA_FUERA, 3));
    }

    @Test
    @DisplayName("El dinero se separa por rubro e incluye las ventas de los dos extremos del rango")
    public void dineroPorRubro() {
        ReporteVentas reporte = club.generarReporteVentas(DIA_UNO, DIA_DOS);
        ResumenRubro tienda = reporte.buscarRubro(VentaTienda.RUBRO);
        ResumenRubro cafeteria = reporte.buscarRubro(VentaCafeteria.RUBRO);

        assertAll("rubros",
                () -> assertEquals(2, reporte.getCantidadVentas()),
                () -> assertEquals(1, tienda.getCantidadVentas()),
                () -> assertEquals(PRECIO_BALON, tienda.getSubtotal(), 0.001),
                () -> assertEquals(1, cafeteria.getCantidadVentas()),
                () -> assertEquals(tienda.getTotal() + cafeteria.getTotal(), reporte.getTotalVendido(), 0.001),
                () -> assertEquals(cafeteria.getSubtotal() * PROPINA_SUGERIDA, cafeteria.getCargosAdicionales(), 0.001));
    }

    @Test
    @DisplayName("Un rubro sin ventas en el periodo aparece en cero")
    public void rubroSinVentas() {
        ReporteVentas reporte = club.generarReporteVentas(DIA_UNO, DIA_UNO);

        assertAll("cafetería sin ventas",
                () -> assertEquals(2, reporte.getRubros().size()),
                () -> assertEquals(0, reporte.buscarRubro(VentaCafeteria.RUBRO).getCantidadVentas()),
                () -> assertEquals(0, reporte.buscarRubro(VentaCafeteria.RUBRO).getTotal(), 0.001));
    }

    @Test
    @DisplayName("Las unidades y el dinero se agrupan por tipo de producto")
    public void porTipoDeProducto() {
        ReporteVentas reporte = club.generarReporteVentas(DIA_UNO, DIA_DOS);

        assertAll("tipos de producto",
                () -> assertEquals(Integer.valueOf(1), reporte.getUnidadesPorTipoProducto().get("Artículo de tienda")),
                () -> assertEquals(Integer.valueOf(2), reporte.getUnidadesPorTipoProducto().get("Bebida")),
                () -> assertEquals(Integer.valueOf(1), reporte.getUnidadesPorTipoProducto().get("Snack")),
                () -> assertEquals(2 * PRECIO_JUGO, reporte.getVentasPorTipoProducto().get("Bebida"), 0.001));
    }

    @Test
    @DisplayName("Una fecha final anterior a la inicial se rechaza")
    public void rangoInvertido() {
        assertThrows(IllegalArgumentException.class, () -> club.generarReporteVentas(DIA_DOS, DIA_UNO));
    }

    @Test
    @DisplayName("El club informa entre qué fechas hay ventas registradas")
    public void fechasRegistradas() {
        assertAll("fechas",
                () -> assertEquals(DIA_UNO, club.getFechaPrimeraVenta()),
                () -> assertEquals(DIA_FUERA, club.getFechaUltimaVenta()),
                () -> assertNull(new ClubDeportivo().getFechaPrimeraVenta()));
    }

    private VentaTienda ventaTienda(LocalDate fecha, int balones) {
        VentaTienda venta = new VentaTienda(fecha, socio);
        venta.agregarDetalle(balon, balones);
        venta.finalizarVenta();
        return venta;
    }

    private VentaCafeteria ventaCafeteria(LocalDate fecha, int jugos, boolean propina) {
        VentaCafeteria venta = new VentaCafeteria(fecha, socio);
        venta.agregarDetalle(jugo, jugos);
        venta.agregarDetalle(galleta, 1);
        venta.finalizarVenta(propina);
        return venta;
    }
}
