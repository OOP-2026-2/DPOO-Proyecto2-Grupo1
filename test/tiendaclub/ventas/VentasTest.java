package tiendaclub.ventas;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubdeportivo.ClubDeportivo;
import tiendaclub.inventario.ArticuloTienda;
import tiendaclub.inventario.Bebida;
import tiendaclub.inventario.TipoMovimiento;
import tiendaclub.inventario.UbicacionInventario;
import tiendaclub.ventas.descuentos.DescuentoCodigoCompartido;
import tiendaclub.ventas.descuentos.DescuentoPuntos;
import tiendaclub.ventas.descuentos.SinDescuento;
import tiendaclub.ventas.tipos.VentaCafeteria;
import tiendaclub.ventas.tipos.VentaTienda;
import usuarios.tipos.Socio;
import usuarios.tipos.empleados.Entrenador;
import utilidades.excepciones.InventarioInsuficienteException;

@DisplayName("Ventas de la tienda y la cafetería")
public class VentasTest {
    // Reglas del enunciado: IVA del 19 % en tienda, impoconsumo del 8 % y propina sugerida del 10 %
    // en cafetería, y el socio acumula el 2 % del total como puntos (1 punto = 1 peso)
    private static final double IVA_TIENDA = 0.19;
    private static final double IMPUESTO_CAFETERIA = 0.08;
    private static final double PROPINA = 0.10;
    private static final double PORCENTAJE_PUNTOS = 0.02;
    private static final double PRECIO_CAMISETA = 40000;
    private static final double PRECIO_CAFE = 5000;
    private static final int EXISTENCIAS = 3;

    private ClubDeportivo club;
    private Socio socio;
    private ArticuloTienda camiseta;
    private Bebida cafe;
    private UbicacionInventario bodega;

    @BeforeEach
    void prepararInventario() {
        club = new ClubDeportivo();
        socio = new Socio("S1", "Socio Uno", LocalDate.of(1998, 4, 4), "socio1", "clave");
        club.registrarUsuario(socio);
        camiseta = new ArticuloTienda("Camiseta", PRECIO_CAMISETA, "Ropa");
        cafe = new Bebida("Café", PRECIO_CAFE, true);
        bodega = new UbicacionInventario("Bodega");
        club.registrarProducto(camiseta);
        club.registrarProducto(cafe);
        club.registrarUbicacionInventario(bodega);
        club.reabastecerProducto(camiseta, bodega, EXISTENCIAS);
        club.reabastecerProducto(cafe, bodega, EXISTENCIAS);
    }

    @Test
    @DisplayName("Cada rubro vende solo sus productos")
    void rubrosSoloVendenSusProductos() {
        VentaTienda tienda = new VentaTienda(LocalDate.now(), socio);
        VentaCafeteria cafeteria = new VentaCafeteria(LocalDate.now(), socio);

        assertAll("rubros",
                () -> assertThrows(IllegalArgumentException.class, () -> tienda.agregarDetalle(cafe, 1)),
                () -> assertThrows(IllegalArgumentException.class, () -> cafeteria.agregarDetalle(camiseta, 1)),
                () -> assertEquals(VentaTienda.RUBRO, tienda.getRubro()),
                () -> assertEquals(VentaCafeteria.RUBRO, cafeteria.getRubro()));
    }

    @Test
    @DisplayName("Una compra en tienda descuenta el inventario, registra la salida y suma puntos al socio")
    void compraEnTienda() throws InventarioInsuficienteException {
        VentaTienda venta = new VentaTienda(LocalDate.now(), socio, new SinDescuento());
        venta.agregarDetalle(camiseta, 1);
        venta.finalizarVenta();

        club.procesarVenta(venta, bodega);

        double totalEsperado = PRECIO_CAMISETA * (1 + IVA_TIENDA);
        assertAll("compra",
                () -> assertEquals(totalEsperado, venta.getTotal(), 0.001),
                () -> assertEquals(EXISTENCIAS - 1, club.buscarExistencia(camiseta, bodega).getCantidad()),
                () -> assertEquals(1, club.contarUnidadesMovidas(TipoMovimiento.SALIDA)),
                () -> assertEquals(totalEsperado * PORCENTAJE_PUNTOS, socio.getPuntosFidelidad(), 0.001));
    }

    @Test
    @DisplayName("La propina de la cafetería se suma al total como cargo adicional")
    void propinaDeCafeteria() throws InventarioInsuficienteException {
        VentaCafeteria venta = new VentaCafeteria(LocalDate.now(), socio, new SinDescuento());
        venta.agregarDetalle(cafe, 2);
        venta.finalizarVenta(true);

        club.procesarVenta(venta, bodega);

        double subtotal = 2 * PRECIO_CAFE;
        assertAll("cafetería",
                () -> assertEquals(subtotal * PROPINA, venta.getCargosAdicionales(), 0.001),
                () -> assertEquals(subtotal * (1 + IMPUESTO_CAFETERIA + PROPINA), venta.getTotal(), 0.001));
    }

    @Test
    @DisplayName("Sin unidades suficientes la venta se rechaza y el inventario no cambia")
    void inventarioInsuficiente() {
        VentaTienda venta = new VentaTienda(LocalDate.now(), socio);
        venta.agregarDetalle(camiseta, EXISTENCIAS + 1);

        assertThrows(InventarioInsuficienteException.class, () -> club.procesarVenta(venta, bodega));
        assertAll("sin cambios",
                () -> assertEquals(EXISTENCIAS, club.buscarExistencia(camiseta, bodega).getCantidad()),
                () -> assertTrue(club.getVentas().isEmpty()));
    }

    @Test
    @DisplayName("No se redimen más puntos que el subtotal ni más de los que tiene el socio")
    void limitesDePuntos() {
        socio.agregarPuntos(PRECIO_CAMISETA * 2);
        VentaTienda excedeSubtotal = new VentaTienda(LocalDate.now(), socio,
                new DescuentoPuntos(PRECIO_CAMISETA + 1));
        excedeSubtotal.agregarDetalle(camiseta, 1);
        VentaTienda excedePuntos = new VentaTienda(LocalDate.now(), socio,
                new DescuentoPuntos(PRECIO_CAMISETA * 3));
        excedePuntos.agregarDetalle(camiseta, 3);

        assertAll("puntos",
                () -> assertThrows(IllegalArgumentException.class, () -> club.procesarVenta(excedeSubtotal, bodega)),
                () -> assertThrows(IllegalArgumentException.class, () -> club.procesarVenta(excedePuntos, bodega)));
    }

    @Test
    @DisplayName("Redimir puntos descuenta 1 peso por punto y los resta al socio")
    void redimirPuntos() throws InventarioInsuficienteException {
        double puntos = 1000;
        socio.agregarPuntos(puntos);
        VentaTienda venta = new VentaTienda(LocalDate.now(), socio, new DescuentoPuntos(puntos));
        venta.agregarDetalle(camiseta, 1);
        venta.finalizarVenta();

        club.procesarVenta(venta, bodega);

        assertEquals(puntos, venta.getDescuento(), 0.001);
        assertEquals(venta.getPuntosGenerados(), socio.getPuntosFidelidad(), 0.001);
    }

    @Test
    @DisplayName("El código compartido debe pertenecer a un empleado del club")
    void codigoCompartido() throws InventarioInsuficienteException {
        club.registrarUsuario(new Entrenador("E1", "Entrenador", LocalDate.of(1990, 1, 1), "e1", "clave",
                "SENECA10"));
        VentaTienda invalida = new VentaTienda(LocalDate.now(), socio, new DescuentoCodigoCompartido("NO-EXISTE"));
        invalida.agregarDetalle(camiseta, 1);
        VentaTienda valida = new VentaTienda(LocalDate.now(), socio, new DescuentoCodigoCompartido("SENECA10"));
        valida.agregarDetalle(camiseta, 1);

        assertThrows(IllegalArgumentException.class, () -> club.procesarVenta(invalida, bodega));
        club.procesarVenta(valida, bodega);
        assertTrue(valida.getDescuento() > 0);
    }

    @Test
    @DisplayName("Un traslado sin unidades suficientes en el origen se rechaza")
    void trasladoInsuficiente() {
        UbicacionInventario cafeteria = new UbicacionInventario("Cafetería");
        club.registrarUbicacionInventario(cafeteria);

        assertThrows(InventarioInsuficienteException.class,
                () -> club.trasladarProducto(cafe, bodega, cafeteria, EXISTENCIAS + 1));
    }
}
