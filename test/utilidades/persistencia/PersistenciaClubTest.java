package utilidades.persistencia;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubdeportivo.ClubDeportivo;
import clubdeportivo.competencias.Competencia;
import clubdeportivo.competencias.FormatoCompetencia;
import clubdeportivo.competencias.TipoCompetencia;
import clubdeportivo.deportes.Categoria;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.Equipo;
import clubdeportivo.deportes.EstadoInscripcion;
import clubdeportivo.deportes.InscripcionEquipo;
import clubdeportivo.deportes.Posicion;
import clubdeportivo.instalaciones.Instalacion;
import clubdeportivo.instalaciones.Reserva;
import clubdeportivo.instalaciones.TipoInstalacion;
import clubdeportivo.membresias.PagoMensualidad;
import clubdeportivo.partidos.EstadoPartido;
import clubdeportivo.partidos.Partido;
import clubdeportivo.partidos.PartidoOficial;
import clubdeportivo.partidos.estadisticas.EstadisticaFutbol;
import clubdeportivo.partidos.estadisticas.EstadisticaJugador;
import tiendaclub.inventario.ArticuloTienda;
import tiendaclub.inventario.UbicacionInventario;
import tiendaclub.ventas.tipos.VentaTienda;
import usuarios.tipos.Socio;
import usuarios.tipos.empleados.Entrenador;

@DisplayName("Persistencia del club en archivos de texto")
public class PersistenciaClubTest {
    // Fútbol con dos jugadores mínimos; tres socios permiten jugar aunque uno esté suspendido
    private static final int MINIMO_JUGADORES = 2;
    private static final int PARTIDOS_SUSPENSION = 2;
    private static final double PRECIO_ORIGINAL = 50000;
    private static final double PRECIO_NUEVO = 70000;
    private static final double MENSUALIDAD = 120000;

    private Path carpeta;
    private PersistenciaClub persistencia;
    private ClubDeportivo club;
    private DeporteConjunto futbol;
    private Posicion posicion;

    @BeforeEach
    public void crearCarpetaTemporal() throws IOException {
        carpeta = Files.createTempDirectory("persistencia-club-");
        persistencia = new PersistenciaClub(carpeta);
        club = new ClubDeportivo();
        futbol = new DeporteConjunto("Fútbol", MINIMO_JUGADORES, 3, 1, true);
        posicion = new Posicion("Jugador");
        futbol.agregarPosicion(posicion);
        club.registrarDeporteConjunto(futbol);
    }

    @AfterEach
    public void borrarCarpetaTemporal() throws IOException {
        File[] archivos = carpeta.toFile().listFiles();

        if (archivos != null) {
            for (File archivo : archivos) {
                Files.deleteIfExists(archivo.toPath());
            }
        }

        Files.deleteIfExists(carpeta);
    }

    @Test
    @DisplayName("Las suspensiones, el puntaje y las estadísticas de un partido oficial se conservan")
    public void conservaSuspensionesYPuntaje() throws IOException {
        Equipo equipo = registrarEquipo("Séneca FC", Categoria.MAYORES);
        Socio expulsado = inscribirSocio("S1", equipo, 20);
        inscribirSocio("S2", equipo, 20);
        inscribirSocio("S3", equipo, 20);
        Competencia liga = registrarLiga();
        club.inscribirEquipoEnCompetencia(liga, equipo);
        PartidoOficial partido = new PartidoOficial(LocalDate.now(), equipo, liga, "Rival", "Cancha");
        club.registrarPartido(partido);
        EstadisticaFutbol roja = new EstadisticaFutbol(expulsado, partido);
        roja.registrarTarjetaRoja();
        club.registrarResultadoPartido(partido, "2-1", Arrays.<EstadisticaJugador>asList(roja));
        equipo.buscarInscripcion(expulsado).suspender(PARTIDOS_SUSPENSION);

        ClubDeportivo cargado = guardarYCargar();
        Equipo leido = cargado.buscarEquipo("Séneca FC");
        InscripcionEquipo inscripcion = leido.buscarInscripcion((Socio) cargado.buscarUsuario("S1"));
        Partido partidoLeido = cargado.getPartidos().get(0);

        assertAll("datos de competencia",
                () -> assertEquals(EstadoInscripcion.SUSPENDIDA, inscripcion.getEstado()),
                () -> assertEquals(PARTIDOS_SUSPENSION, inscripcion.getPartidosSuspension()),
                () -> assertEquals(equipo.getPuntajeHistorico(), leido.getPuntajeHistorico(), 0.001),
                () -> assertEquals(EstadoPartido.FINALIZADO, partidoLeido.getEstado()),
                () -> assertEquals(1, partidoLeido.getEstadisticas().size()),
                () -> assertEquals(3, cargado.buscarCompetencia("Liga", LocalDate.now().getYear())
                        .obtenerTablaPosiciones().get(0).getPuntos()));
    }

    @Test
    @DisplayName("Un equipo dado de baja después de jugar se vuelve a cargar con su partido e inscripción retirada")
    public void equipoDadoDeBajaConHistorial() throws IOException {
        Equipo equipo = registrarEquipo("Séneca FC", Categoria.MAYORES);
        inscribirSocio("S1", equipo, 20);
        inscribirSocio("S2", equipo, 20);
        Competencia liga = registrarLiga();
        club.inscribirEquipoEnCompetencia(liga, equipo);
        PartidoOficial partido = new PartidoOficial(LocalDate.now(), equipo, liga, "Rival", "Cancha");
        club.registrarPartido(partido);
        club.registrarResultadoPartido(partido, "0-1", null);
        club.darDeBajaEquipo(equipo);

        ClubDeportivo cargado = guardarYCargar();
        Equipo leido = cargado.buscarEquipo("Séneca FC");

        assertAll("historial del equipo dado de baja",
                () -> assertFalse(leido.isActivo()),
                () -> assertEquals(1, cargado.getPartidos().size()),
                () -> assertEquals(EstadoInscripcion.RETIRADA, cargado.buscarCompetencia("Liga",
                        LocalDate.now().getYear()).buscarInscripcion(leido).getEstado()));
    }

    @Test
    @DisplayName("Un socio que superó la edad de su categoría se sigue cargando en su equipo")
    public void socioQueCumplioAnios() throws IOException {
        Equipo juvenil = registrarEquipo("Séneca Sub 17", Categoria.SUB_17);
        inscribirSocio("S-JOVEN", juvenil, 16);
        persistencia.guardar(club);

        // Simula el paso del tiempo: el socio ahora tiene 18 años
        Path socios = carpeta.resolve("socios.txt");
        String mayor = LocalDate.now().minusYears(18).toString();
        String menor = LocalDate.now().minusYears(16).toString();
        List<String> lineas = new ArrayList<String>();

        for (String linea : Files.readAllLines(socios, StandardCharsets.UTF_8)) {
            lineas.add(linea.replace(menor, mayor));
        }

        Files.write(socios, lineas, StandardCharsets.UTF_8);
        ClubDeportivo cargado = new ClubDeportivo();
        persistencia.cargar(cargado);

        assertEquals(1, cargado.buscarEquipo("Séneca Sub 17").contarMiembros());
    }

    @Test
    @DisplayName("Una reserva de cancha de conjunto conserva su deporte")
    public void reservaConDeporte() throws Exception {
        Instalacion cancha = new Instalacion("CANCHA-1", TipoInstalacion.CANCHA_CONJUNTO, 22, false);
        club.registrarInstalacion(cancha);
        Socio socio = registrarSocio("S-RESERVA", 30);
        club.registrarReserva(new Reserva(socio, cancha, LocalDate.now().plusDays(2), LocalTime.of(10, 0),
                60, MINIMO_JUGADORES, futbol));

        ClubDeportivo cargado = guardarYCargar();
        Reserva leida = cargado.getReservas().get(0);

        assertAll("reserva",
                () -> assertNotNull(leida.getDeporteConjunto()),
                () -> assertEquals("Fútbol", leida.getDeporteConjunto().getNombre()));
    }

    @Test
    @DisplayName("Una venta conserva el precio del día aunque el producto cambie de precio")
    public void ventaConservaPrecioHistorico() throws Exception {
        Socio socio = registrarSocio("S-COMPRA", 30);
        ArticuloTienda gorra = new ArticuloTienda("Gorra", PRECIO_ORIGINAL, "Ropa");
        UbicacionInventario tienda = new UbicacionInventario("Tienda");
        club.registrarProducto(gorra);
        club.registrarUbicacionInventario(tienda);
        club.reabastecerProducto(gorra, tienda, 2);
        VentaTienda venta = new VentaTienda(LocalDate.now(), socio);
        venta.agregarDetalle(gorra, 1);
        venta.finalizarVenta();
        club.procesarVenta(venta, tienda);
        gorra.cambiarPrecio(PRECIO_NUEVO);

        ClubDeportivo cargado = guardarYCargar();

        assertAll("venta histórica",
                () -> assertEquals(PRECIO_ORIGINAL, cargado.getVentas().get(0).getSubtotal(), 0.001),
                () -> assertEquals(PRECIO_NUEVO, cargado.buscarProducto("Gorra").getPrecio(), 0.001));
    }

    @Test
    @DisplayName("Los pagos de mensualidad se conservan en el historial del socio")
    public void conservaPagos() throws IOException {
        Socio socio = registrarSocio("S-PAGO", 30);
        club.registrarPagoMensualidad(new PagoMensualidad(LocalDate.now(), "2026-11", MENSUALIDAD, socio));

        ClubDeportivo cargado = guardarYCargar();
        Socio leido = (Socio) cargado.buscarUsuario("S-PAGO");

        assertEquals(1, cargado.consultarPagosDe(leido).size());
        assertEquals(MENSUALIDAD, cargado.calcularTotalPagos("2026-11"), 0.001);
    }

    @Test
    @DisplayName("Las estadísticas de un jugador que se retiró después del partido se siguen cargando")
    public void estadisticasDeJugadorRetirado() throws IOException {
        Equipo equipo = registrarEquipo("Séneca FC", Categoria.MAYORES);
        Socio goleador = inscribirSocio("S1", equipo, 20);
        inscribirSocio("S2", equipo, 20);
        inscribirSocio("S3", equipo, 20);
        Competencia liga = registrarLiga();
        club.inscribirEquipoEnCompetencia(liga, equipo);
        PartidoOficial partido = new PartidoOficial(LocalDate.now(), equipo, liga, "Rival", "Cancha");
        club.registrarPartido(partido);
        EstadisticaFutbol gol = new EstadisticaFutbol(goleador, partido);
        gol.registrarGol();
        club.registrarResultadoPartido(partido, "1-0", Arrays.<EstadisticaJugador>asList(gol));
        club.retirarSocioDeEquipo(goleador, equipo);

        ClubDeportivo cargado = guardarYCargar();

        assertEquals(1, cargado.getPartidos().get(0).getEstadisticas().size());
    }

    @Test
    @DisplayName("Las ventas guardadas con el formato del Proyecto 1 se cargan con su descuento")
    public void ventaConFormatoAnterior() throws Exception {
        double precio = 50000;
        double descuentoEmpleado = 0.15;
        club.registrarUsuario(new Entrenador("E1", "Entrenador", LocalDate.of(1990, 1, 1), "e1", "clave",
                "COD-E1"));
        club.registrarProducto(new ArticuloTienda("Gorra", precio, "Ropa"));
        persistencia.guardar(club);
        Files.write(carpeta.resolve("ventas.txt"),
                Arrays.asList(LocalDate.now() + ";E1;TIENDA;false;false;Gorra,1"), StandardCharsets.UTF_8);

        ClubDeportivo cargado = new ClubDeportivo();
        persistencia.cargar(cargado);

        assertEquals(precio * descuentoEmpleado, cargado.getVentas().get(0).getDescuento(), 0.001);
    }

    @Test
    @DisplayName("Un archivo con datos inválidos se informa como error de lectura con su causa")
    public void archivoDanado() throws IOException {
        Files.write(carpeta.resolve("socios.txt"),
                Arrays.asList("S1;Socio;no-es-fecha;login;clave;0"), StandardCharsets.UTF_8);

        IOException error = assertThrows(IOException.class, () -> persistencia.cargar(new ClubDeportivo()));

        assertTrue(error.getCause() instanceof RuntimeException);
    }

    // Utilidades

    private ClubDeportivo guardarYCargar() throws IOException {
        persistencia.guardar(club);
        ClubDeportivo cargado = new ClubDeportivo();
        persistencia.cargar(cargado);
        return cargado;
    }

    private Equipo registrarEquipo(String nombre, Categoria categoria) {
        Equipo equipo = new Equipo(nombre, categoria, 10, futbol);
        club.registrarEquipo(equipo);
        return equipo;
    }

    private Socio registrarSocio(String id, int edad) {
        Socio socio = new Socio(id, "Socio " + id, LocalDate.now().minusYears(edad), "login-" + id, "clave");
        club.registrarUsuario(socio);
        return socio;
    }

    private Socio inscribirSocio(String id, Equipo equipo, int edad) {
        Socio socio = registrarSocio(id, edad);
        club.inscribirSocioEnEquipo(socio, equipo, posicion, equipo.getInscripciones().size() + 1);
        return socio;
    }

    private Competencia registrarLiga() {
        Competencia liga = new Competencia("Liga", "ASCUN", LocalDate.now().getYear(), Categoria.MAYORES,
                TipoCompetencia.ASCUN, FormatoCompetencia.LIGA, futbol);
        club.registrarCompetencia(liga);
        return liga;
    }
}
