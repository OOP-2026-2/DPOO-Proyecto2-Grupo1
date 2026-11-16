package clubdeportivo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import clubdeportivo.competencias.Competencia;
import clubdeportivo.competencias.FormatoCompetencia;
import clubdeportivo.competencias.PosicionTabla;
import clubdeportivo.competencias.TipoCompetencia;
import clubdeportivo.deportes.Categoria;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.DeporteIndividual;
import clubdeportivo.deportes.Equipo;
import clubdeportivo.deportes.EstadoInscripcion;
import clubdeportivo.deportes.InscripcionAcademia;
import clubdeportivo.deportes.InscripcionEquipo;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.deportes.Nivel;
import clubdeportivo.deportes.Posicion;
import clubdeportivo.partidos.EstadoPartido;
import clubdeportivo.partidos.PartidoInterno;
import clubdeportivo.partidos.PartidoOficial;
import clubdeportivo.partidos.PartidoPractica;
import clubdeportivo.partidos.PropositoPartido;
import clubdeportivo.partidos.estadisticas.EstadisticaBaloncesto;
import clubdeportivo.partidos.estadisticas.EstadisticaFutbol;
import clubdeportivo.partidos.estadisticas.EstadisticaJugador;
import clubdeportivo.torneos.FormatoTorneo;
import clubdeportivo.torneos.InscripcionEquipoTorneo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.ParticipanteTorneo;
import clubdeportivo.torneos.PremioRecreativo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.TorneoConjunto;
import clubdeportivo.torneos.TorneoIndividual;
import usuarios.tipos.Socio;

@DisplayName("Competencias externas, partidos, suspensiones y puntaje de equipos")
public class CompetenciasYPartidosTest {
    // Reglas del fútbol de prueba: 3 puntos por victoria, 1 por empate y dos jugadores como mínimo
    private static final int PUNTOS_VICTORIA = 3;
    private static final int PUNTOS_EMPATE = 1;
    private static final int MINIMO_JUGADORES = 2;
    private static final int CUPO_EQUIPO = 6;
    private static final String RIVAL = "Universidad Rival";

    private ClubDeportivo club;
    private DeporteConjunto futbol;
    private Posicion jugadorDeCampo;
    private Equipo equipo;
    private Socio primero;
    private Socio segundo;
    private Socio tercero;
    private Competencia liga;
    private int consecutivo;

    @BeforeEach
    void prepararCompetencia() {
        club = new ClubDeportivo();
        futbol = new DeporteConjunto("Fútbol", MINIMO_JUGADORES, PUNTOS_VICTORIA, PUNTOS_EMPATE, true);
        jugadorDeCampo = new Posicion("Jugador de campo");
        futbol.agregarPosicion(jugadorDeCampo);
        club.registrarDeporteConjunto(futbol);

        // Tres jugadores: si uno queda suspendido el equipo conserva el mínimo
        equipo = registrarEquipo("Séneca FC");
        primero = inscribirNuevoSocio(equipo);
        segundo = inscribirNuevoSocio(equipo);
        tercero = inscribirNuevoSocio(equipo);

        liga = new Competencia("Liga ASCUN", "ASCUN", LocalDate.now().getYear(), Categoria.MAYORES,
                TipoCompetencia.ASCUN, FormatoCompetencia.LIGA, futbol);
        club.registrarCompetencia(liga);
        club.inscribirEquipoEnCompetencia(liga, equipo);
    }

    @Nested
    @DisplayName("Inscripción en competencias y nómina")
    class InscripcionYNomina {

        @Test
        @DisplayName("Un equipo con el mínimo de jugadores queda inscrito y activo")
        void inscribeEquipo() {
            assertTrue(liga.buscarInscripcion(equipo).estaActiva());
        }

        @Test
        @DisplayName("Un equipo sin el mínimo se rechaza indicando cuántos jugadores tiene")
        void rechazaEquipoIncompleto() {
            Equipo incompleto = registrarEquipo("Incompleto");
            inscribirNuevoSocio(incompleto);

            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> club.inscribirEquipoEnCompetencia(liga, incompleto));

            assertTrue(error.getMessage().contains("exige " + MINIMO_JUGADORES));
        }

        @Test
        @DisplayName("Un jugador habilitado entra a la nómina; uno suspendido no")
        void nominaRechazaSuspendido() {
            club.agregarJugadorANomina(liga, equipo, primero);
            equipo.buscarInscripcion(segundo).suspender();

            assertAll("nómina",
                    () -> assertTrue(liga.buscarInscripcion(equipo).estaEnNomina(primero)),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> club.agregarJugadorANomina(liga, equipo, segundo)));
        }
    }

    @Nested
    @DisplayName("Resultado de un partido oficial")
    class ResultadoOficial {
        private PartidoOficial partido;

        @BeforeEach
        void programarPartido() {
            partido = programarOficial(LocalDate.now().plusDays(1));
        }

        @Test
        @DisplayName("Una victoria suma sus puntos al puntaje histórico y a la tabla")
        void victoriaSumaPuntos() {
            club.registrarResultadoPartido(partido, "3-1", null);

            assertAll("victoria",
                    () -> assertEquals(EstadoPartido.FINALIZADO, partido.getEstado()),
                    () -> assertEquals(PUNTOS_VICTORIA, equipo.getPuntajeHistorico(), 0.001),
                    () -> assertEquals(PUNTOS_VICTORIA, filaDe(equipo.getNombre()).getPuntos()),
                    () -> assertEquals(0, filaDe(RIVAL).getPuntos()));
        }

        @Test
        @DisplayName("Un empate suma los puntos de empate y una derrota no suma")
        void empateYDerrota() {
            club.registrarResultadoPartido(partido, "1-1", null);
            club.registrarResultadoPartido(programarOficial(LocalDate.now().plusDays(8)), "0-2", null);

            assertEquals(PUNTOS_EMPATE, equipo.getPuntajeHistorico(), 0.001);
        }

        @Test
        @DisplayName("Una tarjeta roja suspende al jugador para el siguiente partido oficial")
        void rojaSuspende() {
            EstadisticaFutbol estadistica = new EstadisticaFutbol(primero, partido);
            estadistica.registrarTarjetaRoja();

            List<Socio> suspendidos = club.registrarResultadoPartido(partido, "2-0",
                    Arrays.<EstadisticaJugador>asList(estadistica));
            InscripcionEquipo inscripcion = equipo.buscarInscripcion(primero);

            assertAll("suspensión",
                    () -> assertEquals(Arrays.asList(primero), suspendidos),
                    () -> assertEquals(EstadoInscripcion.SUSPENDIDA, inscripcion.getEstado()),
                    () -> assertEquals(InscripcionEquipo.PARTIDOS_SUSPENSION_POR_TARJETAS,
                            inscripcion.getPartidosSuspension()),
                    () -> assertFalse(equipo.getJugadoresHabilitados().contains(primero)));
        }

        @ParameterizedTest(name = "{0} amarillas")
        @ValueSource(ints = { EstadisticaFutbol.AMARILLAS_PARA_SUSPENSION, EstadisticaFutbol.AMARILLAS_PARA_SUSPENSION + 1 })
        @DisplayName("Acumular amarillas en un partido también suspende")
        void amarillasSuspenden(int amarillas) {
            EstadisticaFutbol estadistica = new EstadisticaFutbol(segundo, partido);

            for (int i = 0; i < amarillas; i++) {
                estadistica.registrarTarjetaAmarilla();
            }

            club.registrarResultadoPartido(partido, "1-0", Arrays.<EstadisticaJugador>asList(estadistica));

            assertTrue(equipo.buscarInscripcion(segundo).estaSuspendida());
        }

        @Test
        @DisplayName("Una sola amarilla no suspende")
        void unaAmarillaNoSuspende() {
            EstadisticaFutbol estadistica = new EstadisticaFutbol(segundo, partido);
            estadistica.registrarTarjetaAmarilla();

            List<Socio> suspendidos = club.registrarResultadoPartido(partido, "1-0",
                    Arrays.<EstadisticaJugador>asList(estadistica));

            assertTrue(suspendidos.isEmpty());
            assertTrue(equipo.buscarInscripcion(segundo).estaActiva());
        }

        @Test
        @DisplayName("El suspendido no puede tener estadísticas y el partido no queda a medias")
        void suspendidoNoJuega() {
            equipo.buscarInscripcion(primero).suspender();
            EstadisticaFutbol estadistica = new EstadisticaFutbol(primero, partido);
            estadistica.registrarGol();

            assertThrows(IllegalArgumentException.class, () -> club.registrarResultadoPartido(partido,
                    "1-0", Arrays.<EstadisticaJugador>asList(estadistica)));

            assertAll("partido sin cambios",
                    () -> assertEquals(EstadoPartido.PROGRAMADO, partido.getEstado()),
                    () -> assertEquals(0, equipo.getPuntajeHistorico(), 0.001),
                    () -> assertTrue(partido.getEstadisticas().isEmpty()));
        }

        @Test
        @DisplayName("El suspendido cumple su fecha en el siguiente partido oficial y vuelve a estar activo")
        void cumpleLaSuspension() {
            EstadisticaFutbol roja = new EstadisticaFutbol(primero, partido);
            roja.registrarTarjetaRoja();
            club.registrarResultadoPartido(partido, "2-0", Arrays.<EstadisticaJugador>asList(roja));

            club.registrarResultadoPartido(programarOficial(LocalDate.now().plusDays(8)), "1-0", null);

            assertTrue(equipo.buscarInscripcion(primero).estaActiva());
        }

        @Test
        @DisplayName("Un jugador fuera de la nómina no puede tener estadísticas")
        void jugadorFueraDeNomina() {
            club.agregarJugadorANomina(liga, equipo, segundo);
            club.agregarJugadorANomina(liga, equipo, tercero);
            EstadisticaFutbol estadistica = new EstadisticaFutbol(primero, partido);

            assertThrows(IllegalArgumentException.class, () -> club.registrarResultadoPartido(partido,
                    "1-0", Arrays.<EstadisticaJugador>asList(estadistica)));
            assertEquals(EstadoPartido.PROGRAMADO, partido.getEstado());
        }

        @Test
        @DisplayName("Un jugador retirado no puede tener estadísticas aunque siga en la nómina")
        void retiradoEnNominaNoJuega() {
            club.agregarJugadorANomina(liga, equipo, primero);
            club.agregarJugadorANomina(liga, equipo, segundo);
            club.retirarSocioDeEquipo(primero, equipo);
            EstadisticaFutbol gol = new EstadisticaFutbol(primero, partido);
            gol.registrarGol();

            assertThrows(IllegalArgumentException.class, () -> club.registrarResultadoPartido(partido,
                    "1-0", Arrays.<EstadisticaJugador>asList(gol)));
            assertEquals(EstadoPartido.PROGRAMADO, partido.getEstado());
        }

        @Test
        @DisplayName("Un marcador inválido se rechaza sin cambiar el puntaje")
        void marcadorInvalido() {
            assertThrows(IllegalArgumentException.class,
                    () -> club.registrarResultadoPartido(partido, "tres a uno", null));

            assertAll("sin cambios",
                    () -> assertEquals(EstadoPartido.PROGRAMADO, partido.getEstado()),
                    () -> assertEquals(0, equipo.getPuntajeHistorico(), 0.001));
        }

        @Test
        @DisplayName("Un partido finalizado no se registra otra vez")
        void partidoFinalizado() {
            club.registrarResultadoPartido(partido, "1-0", null);

            assertThrows(IllegalStateException.class,
                    () -> club.registrarResultadoPartido(partido, "2-0", null));
            assertEquals(PUNTOS_VICTORIA, equipo.getPuntajeHistorico(), 0.001);
        }

        @Test
        @DisplayName("Un partido cancelado sale de los programados")
        void cancelarPartido() {
            club.cancelarPartido(partido);

            assertAll("cancelado",
                    () -> assertEquals(EstadoPartido.CANCELADO, partido.getEstado()),
                    () -> assertTrue(club.consultarPartidos(EstadoPartido.PROGRAMADO).isEmpty()));
        }
    }

    @Nested
    @DisplayName("Partidos internos y de práctica")
    class InternosYPractica {

        @Test
        @DisplayName("Un partido interno exige el mínimo de jugadores en los dos equipos")
        void internoExigeMinimo() {
            Equipo incompleto = registrarEquipo("Incompleto");
            inscribirNuevoSocio(incompleto);

            assertThrows(IllegalArgumentException.class,
                    () -> club.registrarPartido(new PartidoInterno(LocalDate.now().plusDays(2), equipo,
                            incompleto, PropositoPartido.PRACTICA)));
        }

        @Test
        @DisplayName("Las tarjetas de un partido interno no suspenden ni suman puntaje histórico")
        void internoNoSuspende() {
            Equipo rival = registrarEquipo("Rival interno");
            inscribirNuevoSocio(rival);
            inscribirNuevoSocio(rival);
            PartidoInterno interno = new PartidoInterno(LocalDate.now().plusDays(2), equipo, rival,
                    PropositoPartido.EVALUACION);
            club.registrarPartido(interno);
            EstadisticaFutbol roja = new EstadisticaFutbol(primero, interno);
            roja.registrarTarjetaRoja();

            List<Socio> suspendidos = club.registrarResultadoPartido(interno, "2-1",
                    Arrays.<EstadisticaJugador>asList(roja));

            assertAll("partido interno",
                    () -> assertTrue(suspendidos.isEmpty()),
                    () -> assertTrue(equipo.buscarInscripcion(primero).estaActiva()),
                    () -> assertEquals(0, equipo.getPuntajeHistorico(), 0.001));
        }

        @Test
        @DisplayName("Un jugador retirado del equipo no puede tener estadísticas en un partido interno")
        void retiradoNoJuegaInterno() {
            PartidoInterno interno = programarInterno();
            club.retirarSocioDeEquipo(primero, equipo);
            EstadisticaFutbol gol = new EstadisticaFutbol(primero, interno);
            gol.registrarGol();

            assertThrows(IllegalArgumentException.class, () -> club.registrarResultadoPartido(interno,
                    "1-0", Arrays.<EstadisticaJugador>asList(gol)));
            assertEquals(EstadoPartido.PROGRAMADO, interno.getEstado());
        }

        @Test
        @DisplayName("Un jugador suspendido tampoco tiene estadísticas en un partido interno")
        void suspendidoNoJuegaInterno() {
            PartidoInterno interno = programarInterno();
            equipo.buscarInscripcion(segundo).suspender();
            EstadisticaFutbol gol = new EstadisticaFutbol(segundo, interno);

            assertThrows(IllegalArgumentException.class, () -> club.registrarResultadoPartido(interno,
                    "1-0", Arrays.<EstadisticaJugador>asList(gol)));
        }

        @Test
        @DisplayName("Un partido de práctica no se cierra sin los participantes de su modalidad")
        void practicaIncompleta() {
            DeporteIndividual tenis = new DeporteIndividual("Tenis", 2);
            club.registrarDeporteIndividual(tenis);
            club.inscribirSocioEnAcademia(primero, tenis, Nivel.INTERMEDIO);
            PartidoPractica practica = new PartidoPractica(LocalDate.now().plusDays(1), tenis,
                    Modalidad.SENCILLOS);
            practica.agregarParticipante(primero);
            club.registrarPartido(practica);

            assertThrows(IllegalStateException.class,
                    () -> club.registrarResultadoPartido(practica, "2-0", null));
        }
    }

    @Nested
    @DisplayName("Estadísticas según el deporte")
    class EstadisticasPorDeporte {

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = { "Fútbol", "FUTBOL", "Futsal" })
        @DisplayName("El fútbol usa estadísticas de goles y tarjetas")
        void creaEstadisticaDeFutbol(String deporte) {
            PartidoOficial partido = programarOficial(LocalDate.now().plusDays(1));

            assertTrue(EstadisticaJugador.crearPara(deporte, primero, partido) instanceof EstadisticaFutbol);
        }

        @Test
        @DisplayName("El baloncesto usa estadísticas de puntos y rebotes")
        void creaEstadisticaDeBaloncesto() {
            PartidoOficial partido = programarOficial(LocalDate.now().plusDays(1));

            assertTrue(EstadisticaJugador.crearPara("Baloncesto", primero, partido)
                    instanceof EstadisticaBaloncesto);
        }

        @Test
        @DisplayName("Un deporte sin estadísticas definidas se rechaza")
        void deporteSinEstadisticas() {
            PartidoOficial partido = programarOficial(LocalDate.now().plusDays(1));

            assertThrows(IllegalArgumentException.class,
                    () -> EstadisticaJugador.crearPara("Ajedrez", primero, partido));
        }
    }

    @Nested
    @DisplayName("Puntaje y ranking por torneos internos")
    class PuntajePorTorneos {

        @Test
        @DisplayName("Al finalizar un torneo de conjunto cada equipo suma los puntos que obtuvo")
        void torneoDeConjuntoSumaPuntaje() {
            Equipo rival = registrarEquipo("Rival torneo");
            inscribirNuevoSocio(rival);
            inscribirNuevoSocio(rival);
            LocalDate fecha = LocalDate.now();
            TorneoConjunto torneo = new TorneoConjunto("TC-PUNTAJE", "Copa interna", fecha,
                    LocalTime.of(9, 0), LocalTime.of(12, 0), FormatoTorneo.TODOS_CONTRA_TODOS, 4,
                    TipoTorneo.RECREATIVO, LocalDateTime.of(fecha.minusDays(1), LocalTime.of(8, 0)),
                    new PremioRecreativo(0.10, 30), 0, futbol, Categoria.MAYORES);
            club.registrarTorneo(torneo);
            InscripcionEquipoTorneo nuestra = club.inscribirEquipoEnTorneo(torneo, equipo, TipoCupo.REGULAR);
            club.inscribirEquipoEnTorneo(torneo, rival, TipoCupo.REGULAR);
            club.iniciarTorneo(torneo);
            boolean somosLocales = torneo.getEncuentros().get(0).getInscripcionLocal() == nuestra;

            club.registrarResultadoEncuentro(torneo.getEncuentros().get(0), somosLocales ? "2-0" : "0-2");
            club.finalizarTorneo(torneo);

            assertAll("puntaje histórico",
                    () -> assertEquals(PUNTOS_VICTORIA, equipo.getPuntajeHistorico(), 0.001),
                    () -> assertEquals(0, rival.getPuntajeHistorico(), 0.001));
        }

        @Test
        @DisplayName("Al finalizar un torneo individual cada socio suma sus puntos en el ranking de la academia")
        void torneoIndividualSumaRanking() {
            DeporteIndividual tenis = new DeporteIndividual("Tenis", 2);
            club.registrarDeporteIndividual(tenis);
            InscripcionAcademia academiaPrimero = club.inscribirSocioEnAcademia(primero, tenis, Nivel.INTERMEDIO);
            club.inscribirSocioEnAcademia(segundo, tenis, Nivel.INTERMEDIO);
            LocalDate fecha = LocalDate.now();
            TorneoIndividual torneo = new TorneoIndividual("TI-RANKING", "Abierto de tenis", fecha,
                    LocalTime.of(9, 0), LocalTime.of(12, 0), FormatoTorneo.TODOS_CONTRA_TODOS, 4,
                    TipoTorneo.RECREATIVO, LocalDateTime.of(fecha.minusDays(1), LocalTime.of(8, 0)),
                    new PremioRecreativo(0.10, 30), 0, tenis, Nivel.INTERMEDIO, Modalidad.SENCILLOS);
            club.registrarTorneo(torneo);
            InscripcionIndividualTorneo inscripcion = club.inscribirParticipantesEnTorneo(torneo,
                    lista(primero), TipoCupo.REGULAR);
            club.inscribirParticipantesEnTorneo(torneo, lista(segundo), TipoCupo.REGULAR);
            club.iniciarTorneo(torneo);
            boolean primeroEsLocal = torneo.getEncuentros().get(0).getInscripcionLocal() == inscripcion;

            club.registrarResultadoEncuentro(torneo.getEncuentros().get(0), primeroEsLocal ? "6-2" : "2-6");
            club.finalizarTorneo(torneo);

            assertEquals(torneo.buscarPosicionDe(inscripcion).getPuntos(),
                    academiaPrimero.getPuntajeRanking(), 0.001);
            assertSame(inscripcion, torneo.determinarGanador());
        }
    }

    // Utilidades

    private Equipo registrarEquipo(String nombre) {
        Equipo nuevo = new Equipo(nombre, Categoria.MAYORES, CUPO_EQUIPO, futbol);
        club.registrarEquipo(nuevo);
        return nuevo;
    }

    private Socio inscribirNuevoSocio(Equipo destino) {
        consecutivo++;
        String id = "J-" + consecutivo;
        Socio socio = new Socio(id, "Jugador " + consecutivo, LocalDate.now().minusYears(22),
                "login-" + id, "clave");
        club.registrarUsuario(socio);
        club.inscribirSocioEnEquipo(socio, destino, jugadorDeCampo, consecutivo);
        return socio;
    }

    private PartidoOficial programarOficial(LocalDate fecha) {
        PartidoOficial partido = new PartidoOficial(fecha, equipo, liga, RIVAL, "Cancha principal");
        club.registrarPartido(partido);
        return partido;
    }

    private PartidoInterno programarInterno() {
        Equipo rival = registrarEquipo("Rival interno " + consecutivo);
        inscribirNuevoSocio(rival);
        inscribirNuevoSocio(rival);
        PartidoInterno interno = new PartidoInterno(LocalDate.now().plusDays(2), equipo, rival,
                PropositoPartido.PRACTICA);
        club.registrarPartido(interno);
        return interno;
    }

    private PosicionTabla filaDe(String institucion) {
        for (PosicionTabla fila : liga.obtenerTablaPosiciones()) {
            if (fila.getInstitucion().equals(institucion)) {
                return fila;
            }
        }

        throw new AssertionError("No hay fila para " + institucion);
    }

    private List<ParticipanteTorneo> lista(ParticipanteTorneo participante) {
        List<ParticipanteTorneo> participantes = new ArrayList<ParticipanteTorneo>();
        participantes.add(participante);
        return participantes;
    }
}
