

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clubdeportivo.ClubDeportivo;
import clubdeportivo.deportes.Categoria;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.DeporteIndividual;
import clubdeportivo.deportes.Equipo;
import clubdeportivo.deportes.InscripcionAcademia;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.deportes.Nivel;
import clubdeportivo.deportes.Posicion;
import clubdeportivo.torneos.BonoDescuento;
import clubdeportivo.torneos.EncuentroTorneo;
import clubdeportivo.torneos.EstadoTorneo;
import clubdeportivo.torneos.FormatoTorneo;
import clubdeportivo.torneos.InscripcionEquipoTorneo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.ParticipanteTorneo;
import clubdeportivo.torneos.PremioCompetitivo;
import clubdeportivo.torneos.PremioMonetario;
import clubdeportivo.torneos.PremioRecreativo;
import clubdeportivo.torneos.PremioTorneo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.Torneo;
import clubdeportivo.torneos.TorneoConjunto;
import clubdeportivo.torneos.TorneoIndividual;
import clubdeportivo.turnos.DiaSemana;
import clubdeportivo.turnos.Turno;
import tiendaclub.inventario.ArticuloTienda;
import tiendaclub.inventario.Producto;
import tiendaclub.inventario.UbicacionInventario;
import tiendaclub.ventas.descuentos.DescuentoBonoTorneo;
import tiendaclub.ventas.tipos.VentaTienda;
import usuarios.tipos.ParticipanteExterno;
import usuarios.tipos.Socio;
import usuarios.tipos.empleados.Entrenador;
import utilidades.persistencia.PersistenciaClub;

public class Proyecto2Tests {
    private ClubDeportivo club;
    private DeporteIndividual tenis;
    private LocalDate fechaTorneo;

    @BeforeEach
    void prepararPrueba() {
        club = new ClubDeportivo();
        tenis = new DeporteIndividual("Tenis", 20);
        club.registrarDeporteIndividual(tenis);
        fechaTorneo = LocalDate.now().plusDays(10);
    }

    @Test
    void rfT01CrearTorneoInterno() {
        TorneoIndividual torneo = crearTorneoIndividual(
                "T01", TipoTorneo.RECREATIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, 0);

        club.registrarTorneo(torneo);

        assertSame(torneo, club.buscarTorneo("T01"));
        assertEquals(EstadoTorneo.ABIERTO, torneo.getEstado());
    }

    @Test
    void rfT02ConsultarTorneosConFiltros() {
        TorneoIndividual recreativo = crearTorneoIndividual(
                "T02-R", TipoTorneo.RECREATIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, 0);
        TorneoIndividual competitivo = crearTorneoIndividual(
                "T02-C", TipoTorneo.COMPETITIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, 50000);
        club.registrarTorneo(recreativo);
        club.registrarTorneo(competitivo);

        List<Torneo> resultado = club.consultarTorneos(
                TipoTorneo.COMPETITIVO,
                EstadoTorneo.ABIERTO);

        assertEquals(1, resultado.size());
        assertSame(competitivo, resultado.get(0));
    }

    @Test
    void rfT03InscribirEquipoEnTorneoConjunto() {
        DeporteConjunto futbol = new DeporteConjunto(
                "Fútbol", 1, 3, 1, true);
        Posicion delantero = new Posicion("Delantero");
        futbol.agregarPosicion(delantero);
        club.registrarDeporteConjunto(futbol);

        Equipo equipo = new Equipo(
                "Séneca", Categoria.MAYORES, 20, futbol);
        club.registrarEquipo(equipo);
        Socio socio = crearSocio("S-EQUIPO", 20);
        club.inscribirSocioEnEquipo(
                socio, equipo, delantero, 10);

        TorneoConjunto torneo = new TorneoConjunto(
                "T03", "Torneo de fútbol", fechaTorneo,
                LocalTime.of(10, 0), LocalTime.of(12, 0),
                FormatoTorneo.TODOS_CONTRA_TODOS, 4,
                TipoTorneo.RECREATIVO,
                fechaLiberacion(), new PremioRecreativo(0.10, 30),
                0, futbol, Categoria.MAYORES);
        club.registrarTorneo(torneo);

        InscripcionEquipoTorneo inscripcion = club
                .inscribirEquipoEnTorneo(
                        torneo, equipo, TipoCupo.REGULAR);

        assertSame(equipo, inscripcion.getEquipo());
        assertEquals(1, torneo.cantidadInscripcionesActivas());
    }

    @Test
    void rfT04InscribirParejaEnTorneoIndividual() {
        ParticipanteExterno primero = crearExterno("P1");
        ParticipanteExterno segundo = crearExterno("P2");
        TorneoIndividual torneo = crearTorneoIndividual(
                "T04", TipoTorneo.RECREATIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.DOBLES, 0);
        club.registrarTorneo(torneo);

        List<ParticipanteTorneo> pareja = Arrays
                .<ParticipanteTorneo>asList(primero, segundo);
        InscripcionIndividualTorneo inscripcion = club
                .inscribirParticipantesEnTorneo(
                        torneo, pareja, TipoCupo.REGULAR);

        assertEquals(2, inscripcion.getParticipantes().size());
        assertTrue(inscripcion.contieneParticipante(primero));
        assertTrue(inscripcion.contieneParticipante(segundo));
    }

    @Test
    void rfT05RetirarInscripcionAntesDelInicio() {
        ParticipanteExterno participante = crearExterno("P3");
        TorneoIndividual torneo = crearTorneoIndividual(
                "T05", TipoTorneo.RECREATIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, 0);
        club.registrarTorneo(torneo);
        InscripcionIndividualTorneo inscripcion = club
                .inscribirParticipantesEnTorneo(
                        torneo,
                        listaParticipante(participante),
                        TipoCupo.REGULAR);

        club.retirarInscripcionTorneo(inscripcion);

        assertFalse(inscripcion.estaActiva());
        assertEquals(0, torneo.cantidadInscripcionesActivas());
    }

    @Test
    void rfT06GestionarCuposPreferenciales() {
        Socio socio = crearSocioAcademia("S-PREF", 20, 100);
        TorneoIndividual torneo = crearTorneoIndividual(
                "T06", TipoTorneo.RECREATIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, 0);
        club.registrarTorneo(torneo);

        club.inscribirParticipantesEnTorneo(
                torneo,
                listaParticipante(socio),
                TipoCupo.PREFERENCIAL);

        assertEquals(1, torneo.calcularCuposPreferenciales());
        assertEquals(1, torneo.cantidadInscripcionesActivas());
        club.liberarCuposPreferenciales(torneo);
        assertFalse(torneo.tieneCupoPreferencial());
    }

    @Test
    void rfT07RechazarEmpleadoConCruceDeTurno() {
        TorneoIndividual torneo = crearTorneoIndividual(
                "T07", TipoTorneo.COMPETITIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, 50000);
        Entrenador entrenador = new Entrenador(
                "E1", "Entrenador", LocalDate.of(1990, 1, 1),
                "entrenador", "clave", "COD-E1");
        club.registrarUsuario(entrenador);
        DiaSemana dia = DiaSemana.values()[
                fechaTorneo.getDayOfWeek().getValue() - 1];
        club.asignarTurno(entrenador,
                new Turno(dia, LocalTime.of(9, 0),
                        LocalTime.of(11, 0)));

        assertFalse(torneo.validarParticipante(entrenador));
        assertThrows(IllegalArgumentException.class,
                () -> torneo.inscribirParticipantes(
                        listaParticipante(entrenador),
                        TipoCupo.REGULAR));
    }

    @Test
    void rfT08RegistrarEncuentroResultadoYPosiciones() {
        TorneoIndividual torneo = prepararTorneoConDosExternos(
                "T08", TipoTorneo.RECREATIVO, 0);

        club.iniciarTorneo(torneo);
        EncuentroTorneo encuentro = torneo.getEncuentros().get(0);
        club.registrarResultadoEncuentro(encuentro, "2-1");

        assertTrue(encuentro.estaFinalizado());
        assertEquals("2-1", encuentro.getMarcador());
        assertEquals(2, torneo.getPosiciones().size());
        assertEquals(3, torneo.getPosiciones().get(0).getPuntos());
    }

    @Test
    void rfT09FinalizarTorneoYOtorgarPremio() {
        TorneoIndividual torneo = prepararTorneoConDosExternos(
                "T09", TipoTorneo.COMPETITIVO, 100000);
        club.iniciarTorneo(torneo);
        club.registrarResultadoEncuentro(
                torneo.getEncuentros().get(0), "3-1");

        List<PremioTorneo> premios = club.finalizarTorneo(torneo);

        assertEquals(EstadoTorneo.FINALIZADO, torneo.getEstado());
        assertEquals(1, premios.size());
        assertTrue(premios.get(0) instanceof PremioMonetario);
        assertEquals(100000, premios.get(0).getValor(), 0.001);
        assertTrue(premios.get(0).fueEntregado());
    }

    @Test
    void rfT10ConsultarEstadoEHistorial() {
        TorneoIndividual torneo = prepararTorneoConDosExternos(
                "T10", TipoTorneo.RECREATIVO, 0);
        club.iniciarTorneo(torneo);
        club.registrarResultadoEncuentro(
                torneo.getEncuentros().get(0), "1-0");

        assertEquals(2, torneo.getInscripciones().size());
        assertEquals(1, torneo.getEncuentros().size());
        assertEquals(2, torneo.getPosiciones().size());
        assertNotNull(torneo.determinarGanador());
    }

    @Test
    void rfT11PersistirTorneos() throws IOException {
        Path carpeta = Files.createTempDirectory(
                "persistencia-club-");
        ParticipanteExterno participante = crearExterno("P-PERSISTE");
        TorneoIndividual torneo = crearTorneoIndividual(
                "T11", TipoTorneo.RECREATIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, 0);
        club.registrarTorneo(torneo);
        club.inscribirParticipantesEnTorneo(
                torneo,
                listaParticipante(participante),
                TipoCupo.REGULAR);
        PersistenciaClub persistencia = new PersistenciaClub(carpeta);
        persistencia.guardar(club);

        ClubDeportivo cargado = new ClubDeportivo();
        persistencia.cargar(cargado);

        assertNotNull(cargado.buscarTorneo("T11"));
        assertNotNull(cargado.buscarUsuario("P-PERSISTE"));
        assertEquals(1, cargado.buscarTorneo("T11")
                .getInscripciones().size());
    }

    @Test
    void rfT12AutenticarUsuario() {
        ParticipanteExterno participante = crearExterno("P-AUTH");

        assertSame(participante,
                club.autenticar("login-P-AUTH", "clave"));
        assertNull(club.autenticar("login-P-AUTH", "incorrecta"));
    }

    @Test
    void rfT13IniciarTorneoYGenerarEncuentros() {
        TorneoIndividual torneo = prepararTorneoConDosExternos(
                "T13", TipoTorneo.RECREATIVO, 0);

        club.iniciarTorneo(torneo);

        assertEquals(EstadoTorneo.EN_CURSO, torneo.getEstado());
        assertEquals(1, torneo.getEncuentros().size());
        assertEquals(2, torneo.getPosiciones().size());
    }

    @Test
    void rfT14AplicarBonoRecreativo() {
        Socio socio = crearSocioAcademia("S-BONO", 20, 50);
        ParticipanteExterno rival = crearExterno("P-RIVAL");
        TorneoIndividual torneo = crearTorneoIndividual(
                "T14", TipoTorneo.RECREATIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, 0);
        club.registrarTorneo(torneo);
        club.inscribirParticipantesEnTorneo(
                torneo, listaParticipante(socio), TipoCupo.REGULAR);
        club.inscribirParticipantesEnTorneo(
                torneo, listaParticipante(rival), TipoCupo.REGULAR);
        club.iniciarTorneo(torneo);
        club.registrarResultadoEncuentro(
                torneo.getEncuentros().get(0), "2-0");
        club.finalizarTorneo(torneo);
        BonoDescuento bono = socio.getBonosDisponibles().get(0);

        Producto camiseta = new ArticuloTienda(
                "Camiseta", 50000, "Ropa");
        UbicacionInventario tienda = new UbicacionInventario(
                "Tienda");
        club.registrarProducto(camiseta);
        club.registrarUbicacionInventario(tienda);
        club.reabastecerProducto(camiseta, tienda, 5);
        VentaTienda venta = new VentaTienda(
                LocalDate.now(), socio,
                new DescuentoBonoTorneo(bono));
        venta.agregarDetalle(camiseta, 1);
        venta.finalizarVenta();
        club.procesarVenta(venta, tienda);

        assertFalse(bono.estaDisponible());
        assertTrue(venta.getDescuento() > 0);
        assertEquals(4, club.buscarExistencia(
                camiseta, tienda).getCantidad());
    }

    @Test
    void rfT15RegistrarParticipanteExterno() {
        ParticipanteExterno participante = crearExterno("P-EXTERNO");

        assertSame(participante,
                club.buscarUsuario("P-EXTERNO"));
        assertTrue(participante instanceof ParticipanteTorneo);
        assertEquals("contacto-P-EXTERNO",
                participante.getContacto());
    }

    private TorneoIndividual crearTorneoIndividual(
            String id, TipoTorneo tipo,
            FormatoTorneo formato, Modalidad modalidad,
            double tarifa) {

        return new TorneoIndividual(
                id,
                "Torneo " + id,
                fechaTorneo,
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                formato,
                5,
                tipo,
                fechaLiberacion(),
                tipo == TipoTorneo.RECREATIVO
                        ? new PremioRecreativo(0.10, 30)
                        : new PremioCompetitivo(0.50),
                tarifa,
                tenis,
                Nivel.INTERMEDIO,
                modalidad);
    }

    private TorneoIndividual prepararTorneoConDosExternos(
            String id, TipoTorneo tipo, double tarifa) {

        TorneoIndividual torneo = crearTorneoIndividual(
                id, tipo,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, tarifa);
        ParticipanteExterno primero = crearExterno(id + "-P1");
        ParticipanteExterno segundo = crearExterno(id + "-P2");
        club.registrarTorneo(torneo);
        club.inscribirParticipantesEnTorneo(
                torneo, listaParticipante(primero),
                TipoCupo.REGULAR);
        club.inscribirParticipantesEnTorneo(
                torneo, listaParticipante(segundo),
                TipoCupo.REGULAR);
        return torneo;
    }

    private ParticipanteExterno crearExterno(String id) {
        ParticipanteExterno participante = new ParticipanteExterno(
                id,
                "Participante " + id,
                LocalDate.of(2000, 1, 1),
                "login-" + id,
                "clave",
                "contacto-" + id);
        club.registrarUsuario(participante);
        return participante;
    }

    private Socio crearSocio(String id, int edad) {
        Socio socio = new Socio(
                id,
                "Socio " + id,
                LocalDate.now().minusYears(edad),
                "login-" + id,
                "clave");
        club.registrarUsuario(socio);
        return socio;
    }

    private Socio crearSocioAcademia(
            String id, int edad, double puntaje) {

        Socio socio = crearSocio(id, edad);
        InscripcionAcademia inscripcion = club
                .inscribirSocioEnAcademia(
                        socio, tenis, Nivel.INTERMEDIO);
        inscripcion.actualizarPuntaje(puntaje);
        return socio;
    }

    private List<ParticipanteTorneo> listaParticipante(
            ParticipanteTorneo participante) {

        List<ParticipanteTorneo> participantes =
                new ArrayList<ParticipanteTorneo>();
        participantes.add(participante);
        return participantes;
    }

    private LocalDateTime fechaLiberacion() {
        return LocalDateTime.of(
                fechaTorneo.minusDays(2),
                LocalTime.of(8, 0));
    }
}
