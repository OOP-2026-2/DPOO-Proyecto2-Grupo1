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
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import clubdeportivo.competencias.Competencia;
import clubdeportivo.competencias.FormatoCompetencia;
import clubdeportivo.competencias.TipoCompetencia;
import clubdeportivo.deportes.Categoria;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.DeporteIndividual;
import clubdeportivo.deportes.Equipo;
import clubdeportivo.deportes.EstadoInscripcion;
import clubdeportivo.deportes.InscripcionAcademia;
import clubdeportivo.deportes.InscripcionEquipo;
import clubdeportivo.deportes.Nivel;
import clubdeportivo.deportes.Posicion;
import clubdeportivo.partidos.PartidoOficial;
import clubdeportivo.torneos.EstadoInscripcionTorneo;
import clubdeportivo.torneos.FormatoTorneo;
import clubdeportivo.torneos.InscripcionEquipoTorneo;
import clubdeportivo.torneos.PremioRecreativo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.TorneoConjunto;
import usuarios.tipos.Socio;

@DisplayName("Equipos, miembros y academias")
public class EquiposYAcademiasTest {
    // Fútbol de prueba: dos jugadores bastan para jugar y los equipos admiten cinco integrantes
    private static final int MINIMO_JUGADORES = 2;
    private static final int CUPO_EQUIPO = 5;
    private static final int EDAD_MAYORES = 20;

    private ClubDeportivo club;
    private DeporteConjunto futbol;
    private Posicion delantero;
    private Posicion arquero;
    private Equipo equipo;
    private int consecutivoSocios;

    @BeforeEach
    void prepararClub() {
        club = new ClubDeportivo();
        futbol = new DeporteConjunto("Fútbol", MINIMO_JUGADORES, 3, 1, true);
        delantero = new Posicion("Delantero");
        arquero = new Posicion("Arquero");
        futbol.agregarPosicion(delantero);
        futbol.agregarPosicion(arquero);
        club.registrarDeporteConjunto(futbol);
        equipo = registrarEquipo("Séneca FC");
        consecutivoSocios = 0;
    }

    @Nested
    @DisplayName("Registro de deportes y equipos")
    class RegistroDeEquipos {

        @Test
        @DisplayName("Un deporte de conjunto sin posiciones se rechaza")
        void deporteSinPosiciones() {
            DeporteConjunto sinPosiciones = new DeporteConjunto("Rugby", 7, 4, 2, true);

            assertThrows(IllegalArgumentException.class,
                    () -> club.registrarDeporteConjunto(sinPosiciones));
        }

        @Test
        @DisplayName("El equipo se crea vacío e indica cuántos jugadores le faltan")
        void equipoNuevoEstaIncompleto() {
            assertAll("equipo vacío",
                    () -> assertEquals(0, equipo.contarMiembros()),
                    () -> assertFalse(equipo.cumpleMinimoJugadores()),
                    () -> assertEquals(MINIMO_JUGADORES, equipo.calcularJugadoresFaltantes()));
        }

        @Test
        @DisplayName("Al registrar los miembros mínimos el equipo queda completo")
        void equipoConMiembrosSuficientes() {
            for (int i = 1; i <= MINIMO_JUGADORES; i++) {
                inscribir(nuevoSocio(), equipo, i);
            }

            assertAll("equipo completo",
                    () -> assertTrue(equipo.cumpleMinimoJugadores()),
                    () -> assertEquals(0, equipo.calcularJugadoresFaltantes()));
        }

        @Test
        @DisplayName("Un nombre de equipo repetido se rechaza aunque sea de otro deporte")
        void nombreRepetidoEnOtroDeporte() {
            DeporteConjunto baloncesto = new DeporteConjunto("Baloncesto", 5, 2, 0, false);
            baloncesto.agregarPosicion(new Posicion("Base"));
            club.registrarDeporteConjunto(baloncesto);

            assertThrows(IllegalArgumentException.class,
                    () -> club.registrarEquipo(new Equipo("Séneca FC", Categoria.MAYORES,
                            CUPO_EQUIPO, baloncesto)));
        }
    }

    @Nested
    @DisplayName("Miembros de un equipo")
    class Miembros {

        @Test
        @DisplayName("Un socio no puede estar en dos equipos activos de la misma disciplina y categoría")
        void noEstaEnDosEquiposDeLaMismaCategoria() {
            Socio socio = nuevoSocio();
            inscribir(socio, equipo, 9);
            Equipo otro = registrarEquipo("Séneca B");

            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> inscribir(socio, otro, 9));

            assertTrue(error.getMessage().contains("Séneca FC"));
        }

        @Test
        @DisplayName("Si su equipo fue dado de baja, el socio puede unirse a otro de la misma categoría")
        void equipoDadoDeBajaLiberaAlSocio() {
            Socio socio = nuevoSocio();
            inscribir(socio, equipo, 9);
            club.darDeBajaEquipo(equipo);
            Equipo nuevo = registrarEquipo("Séneca Nuevo");

            InscripcionEquipo inscripcion = inscribir(socio, nuevo, 9);

            assertTrue(inscripcion.estaActiva());
        }

        @Test
        @DisplayName("Un socio retirado puede volver a inscribirse en el mismo equipo")
        void socioRetiradoVuelve() {
            Socio socio = nuevoSocio();
            inscribir(socio, equipo, 9);
            club.retirarSocioDeEquipo(socio, equipo);

            InscripcionEquipo nueva = inscribir(socio, equipo, 7);

            assertAll("reingreso",
                    () -> assertSame(nueva, equipo.buscarInscripcion(socio)),
                    () -> assertTrue(equipo.esIntegrante(socio)),
                    () -> assertEquals(1, equipo.contarMiembros()));
        }

        @Test
        @DisplayName("La camiseta de un retirado queda libre; la de un integrante no")
        void camisetaRepetida() {
            Socio primero = nuevoSocio();
            inscribir(primero, equipo, 10);

            assertThrows(IllegalArgumentException.class, () -> inscribir(nuevoSocio(), equipo, 10));

            club.retirarSocioDeEquipo(primero, equipo);
            assertTrue(inscribir(nuevoSocio(), equipo, 10).estaActiva());
        }

        @Test
        @DisplayName("Una posición que no es del deporte se rechaza")
        void posicionDeOtroDeporte() {
            Socio socio = nuevoSocio();

            assertThrows(IllegalArgumentException.class,
                    () -> club.inscribirSocioEnEquipo(socio, equipo, new Posicion("Pívot"), 5));
        }

        @Test
        @DisplayName("Un equipo con el cupo lleno no recibe más integrantes")
        void equipoLleno() {
            for (int i = 1; i <= CUPO_EQUIPO; i++) {
                inscribir(nuevoSocio(), equipo, i);
            }

            assertThrows(IllegalStateException.class, () -> inscribir(nuevoSocio(), equipo, 99));
        }

        @Test
        @DisplayName("Las inscripciones del socio incluyen las suspendidas y no las retiradas")
        void consultarEquiposDelSocio() {
            Socio socio = nuevoSocio();
            InscripcionEquipo inscripcion = inscribir(socio, equipo, 9);
            inscripcion.suspender();
            Equipo otroDeporte = registrarEquipoDeBaloncesto();
            inscribir(socio, otroDeporte, 4);
            club.retirarSocioDeEquipo(socio, otroDeporte);

            List<InscripcionEquipo> equipos = club.consultarEquiposDe(socio);

            assertEquals(1, equipos.size());
            assertEquals(EstadoInscripcion.SUSPENDIDA, equipos.get(0).getEstado());
        }
    }

    @Nested
    @DisplayName("Academias de deportes individuales")
    class Academias {
        private DeporteIndividual tenis;

        @BeforeEach
        void registrarTenis() {
            tenis = new DeporteIndividual("Tenis", 3);
            club.registrarDeporteIndividual(tenis);
        }

        @Test
        @DisplayName("El socio queda inscrito con su nivel")
        void inscribeSocio() {
            Socio socio = nuevoSocio();

            InscripcionAcademia inscripcion = club.inscribirSocioEnAcademia(socio, tenis, Nivel.AVANZADO);

            assertAll("academia",
                    () -> assertEquals(Nivel.AVANZADO, inscripcion.getNivel()),
                    () -> assertSame(inscripcion, tenis.buscarInscripcionActiva(socio)),
                    () -> assertEquals(1, club.consultarAcademiasDe(socio).size()));
        }

        @Test
        @DisplayName("Un socio suspendido en la academia no puede inscribirse otra vez")
        void noSeInscribeDosVeces() {
            Socio socio = nuevoSocio();
            club.inscribirSocioEnAcademia(socio, tenis, Nivel.PRINCIPIANTE).suspender();

            assertThrows(IllegalArgumentException.class,
                    () -> club.inscribirSocioEnAcademia(socio, tenis, Nivel.PRINCIPIANTE));
        }

        @Test
        @DisplayName("Después de retirarse el socio puede volver a la academia")
        void vuelveDespuesDeRetirarse() {
            Socio socio = nuevoSocio();
            club.inscribirSocioEnAcademia(socio, tenis, Nivel.PRINCIPIANTE).retirar();

            assertTrue(club.inscribirSocioEnAcademia(socio, tenis, Nivel.INTERMEDIO).estaActiva());
        }

        @Test
        @DisplayName("El ranking de la academia ordena por puntaje de mayor a menor")
        void rankingDeAcademia() {
            InscripcionAcademia bajo = club.inscribirSocioEnAcademia(nuevoSocio(), tenis, Nivel.INTERMEDIO);
            InscripcionAcademia alto = club.inscribirSocioEnAcademia(nuevoSocio(), tenis, Nivel.INTERMEDIO);
            bajo.sumarPuntaje(3);
            alto.sumarPuntaje(9);

            List<InscripcionAcademia> ranking = club.consultarRankingAcademia(tenis);

            assertSame(alto, ranking.get(0));
            assertSame(bajo, ranking.get(1));
        }
    }

    @Nested
    @DisplayName("Dar de baja un equipo")
    class DarDeBaja {

        @Test
        @DisplayName("El equipo queda inactivo y se retiran sus inscripciones en torneos abiertos y competencias")
        void retiraInscripciones() {
            completar(equipo);
            TorneoConjunto torneo = registrarTorneo("TB-1");
            InscripcionEquipoTorneo enTorneo = club.inscribirEquipoEnTorneo(torneo, equipo, TipoCupo.REGULAR);
            Competencia liga = registrarLiga();
            club.inscribirEquipoEnCompetencia(liga, equipo);

            club.darDeBajaEquipo(equipo);

            assertAll("baja del equipo",
                    () -> assertFalse(equipo.isActivo()),
                    () -> assertEquals(EstadoInscripcionTorneo.RETIRADA, enTorneo.getEstado()),
                    () -> assertEquals(EstadoInscripcion.RETIRADA, liga.buscarInscripcion(equipo).getEstado()));
        }

        @Test
        @DisplayName("Se rechaza si el equipo juega un torneo en curso")
        void rechazaConTorneoEnCurso() {
            completar(equipo);
            Equipo rival = registrarEquipo("Rival");
            completar(rival);
            TorneoConjunto torneo = registrarTorneo("TB-2");
            club.inscribirEquipoEnTorneo(torneo, equipo, TipoCupo.REGULAR);
            club.inscribirEquipoEnTorneo(torneo, rival, TipoCupo.REGULAR);
            club.iniciarTorneo(torneo);

            assertThrows(IllegalStateException.class, () -> club.darDeBajaEquipo(equipo));
            assertTrue(equipo.isActivo());
        }

        @Test
        @DisplayName("Se rechaza si el equipo tiene partidos oficiales programados")
        void rechazaConPartidosPendientes() {
            completar(equipo);
            Competencia liga = registrarLiga();
            club.inscribirEquipoEnCompetencia(liga, equipo);
            club.registrarPartido(new PartidoOficial(LocalDate.now().plusDays(3), equipo, liga,
                    "Universidad Rival", "Cancha principal"));

            assertThrows(IllegalStateException.class, () -> club.darDeBajaEquipo(equipo));
        }

        @Test
        @DisplayName("Un equipo ya dado de baja no se da de baja otra vez ni recibe miembros")
        void equipoInactivo() {
            club.darDeBajaEquipo(equipo);

            assertAll("equipo inactivo",
                    () -> assertThrows(IllegalStateException.class, () -> club.darDeBajaEquipo(equipo)),
                    () -> assertThrows(IllegalStateException.class, () -> inscribir(nuevoSocio(), equipo, 3)));
        }
    }

    @Test
    @DisplayName("El ranking de equipos ordena por puntaje histórico y omite los dados de baja")
    void rankingDeEquipos() {
        Equipo segundo = registrarEquipo("Segundo");
        Equipo inactivo = registrarEquipo("Inactivo");
        equipo.sumarPuntajeHistorico(3);
        segundo.sumarPuntajeHistorico(7);
        inactivo.sumarPuntajeHistorico(20);
        club.darDeBajaEquipo(inactivo);

        List<Equipo> ranking = club.consultarRankingEquipos(futbol);

        assertAll("ranking",
                () -> assertEquals(2, ranking.size()),
                () -> assertSame(segundo, ranking.get(0)),
                () -> assertSame(equipo, ranking.get(1)));
    }

    // Utilidades

    private Socio nuevoSocio() {
        consecutivoSocios++;
        String id = "S-" + consecutivoSocios;
        Socio socio = new Socio(id, "Socio " + consecutivoSocios,
                LocalDate.now().minusYears(EDAD_MAYORES), "login-" + id, "clave");
        club.registrarUsuario(socio);
        return socio;
    }

    private Equipo registrarEquipo(String nombre) {
        Equipo nuevo = new Equipo(nombre, Categoria.MAYORES, CUPO_EQUIPO, futbol);
        club.registrarEquipo(nuevo);
        return nuevo;
    }

    private Equipo registrarEquipoDeBaloncesto() {
        DeporteConjunto baloncesto = new DeporteConjunto("Baloncesto", 5, 2, 0, false);
        Posicion base = new Posicion("Base");
        baloncesto.agregarPosicion(base);
        club.registrarDeporteConjunto(baloncesto);
        Equipo nuevo = new Equipo("Séneca Basket", Categoria.MAYORES, CUPO_EQUIPO, baloncesto);
        club.registrarEquipo(nuevo);
        return nuevo;
    }

    private InscripcionEquipo inscribir(Socio socio, Equipo destino, int camiseta) {
        return club.inscribirSocioEnEquipo(socio, destino, destino.getDeporte().getPosiciones().get(0),
                camiseta);
    }

    private void completar(Equipo destino) {
        for (int i = 1; i <= MINIMO_JUGADORES; i++) {
            inscribir(nuevoSocio(), destino, i);
        }
    }

    private TorneoConjunto registrarTorneo(String id) {
        LocalDate fecha = LocalDate.now();
        TorneoConjunto torneo = new TorneoConjunto(id, "Torneo " + id, fecha,
                LocalTime.of(9, 0), LocalTime.of(12, 0), FormatoTorneo.TODOS_CONTRA_TODOS, 4,
                TipoTorneo.RECREATIVO, LocalDateTime.of(fecha.minusDays(1), LocalTime.of(8, 0)),
                new PremioRecreativo(0.10, 30), 0, futbol, Categoria.MAYORES);
        club.registrarTorneo(torneo);
        return torneo;
    }

    private Competencia registrarLiga() {
        Competencia liga = new Competencia("Liga ASCUN", "ASCUN", LocalDate.now().getYear(),
                Categoria.MAYORES, TipoCompetencia.ASCUN, FormatoCompetencia.LIGA, futbol);
        club.registrarCompetencia(liga);
        return liga;
    }
}
