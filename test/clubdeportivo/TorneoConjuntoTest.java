package clubdeportivo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import clubdeportivo.deportes.Categoria;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.Equipo;
import clubdeportivo.deportes.InscripcionEquipo;
import clubdeportivo.deportes.Posicion;
import clubdeportivo.torneos.FormatoTorneo;
import clubdeportivo.torneos.PremioRecreativo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.TorneoConjunto;
import usuarios.tipos.Socio;

@DisplayName("Inscripción de equipos en torneos de conjunto")
public class TorneoConjuntoTest {
    private DeporteConjunto futbol;
    private Posicion delantero;
    private TorneoConjunto torneo;

    @BeforeEach
    public void prepararPrueba() {
        futbol = new DeporteConjunto("Fútbol", 2, 3, 1, true);
        delantero = new Posicion("Delantero");
        futbol.agregarPosicion(delantero);
        LocalDate fecha = DatosPruebaTorneo.fechaTorneo();
        torneo = new TorneoConjunto("TC", "Torneo conjunto", fecha,
                LocalTime.of(10, 0), LocalTime.of(12, 0),
                FormatoTorneo.TODOS_CONTRA_TODOS, 4,
                TipoTorneo.RECREATIVO,
                LocalDateTime.of(fecha.minusDays(2), LocalTime.of(8, 0)),
                new PremioRecreativo(0.10, 30), 0,
                futbol, Categoria.MAYORES);
    }

    @Test
    @DisplayName("Un equipo dado de baja no es válido")
    public void equipoInactivoNoEsValido() {
        Equipo equipo = equipoCompleto("Inactivo", Categoria.MAYORES);
        equipo.desactivar();
        assertFalse(torneo.validarEquipo(equipo));
    }

    @Test
    @DisplayName("Un equipo de otra categoría no es válido")
    public void equipoDeOtraCategoriaNoEsValido() {
        Equipo equipo = equipoCompleto("Sub 17", Categoria.SUB_17);
        assertFalse(torneo.validarEquipo(equipo));
    }

    @Test
    @DisplayName("Un equipo sin el mínimo de jugadores no es válido")
    public void equipoSinMinimoDeJugadoresNoEsValido() {
        Equipo equipo = new Equipo(
                "Incompleto", Categoria.MAYORES, 10, futbol);
        agregarJugador(equipo, "S1", 1);
        assertFalse(torneo.validarEquipo(equipo));
    }

    @Test
    @DisplayName("Un equipo completo es válido")
    public void equipoCompletoEsValido() {
        assertTrue(torneo.validarEquipo(
                equipoCompleto("Completo", Categoria.MAYORES)));
    }

    @Test
    @DisplayName("No se inscribe el mismo equipo dos veces")
    public void noPermiteInscribirElMismoEquipoDosVeces() {
        Equipo equipo = equipoCompleto("Duplicado", Categoria.MAYORES);
        torneo.inscribirEquipo(equipo, TipoCupo.REGULAR);
        assertThrows(IllegalArgumentException.class,
                () -> torneo.inscribirEquipo(equipo, TipoCupo.REGULAR));
    }

    @Test
    @DisplayName("El cupo preferencial exige puntaje histórico")
    public void cupoPreferencialExigePuntajeHistorico() {
        Equipo equipo = equipoCompleto("Preferencial", Categoria.MAYORES);
        assertThrows(IllegalArgumentException.class,
                () -> torneo.inscribirEquipo(
                        equipo, TipoCupo.PREFERENCIAL));
        equipo.actualizarPuntajeHistorico(80);
        assertTrue(torneo.cumpleCriterioPreferencial(equipo));
        torneo.inscribirEquipo(equipo, TipoCupo.PREFERENCIAL);
    }

    private Equipo equipoCompleto(String nombre, Categoria categoria) {
        Equipo equipo = new Equipo(nombre, categoria, 10, futbol);
        agregarJugador(equipo, nombre + "-S1", 1);
        agregarJugador(equipo, nombre + "-S2", 2);
        return equipo;
    }

    private void agregarJugador(Equipo equipo, String id, int camiseta) {
        Socio socio = new Socio(id, "Socio " + id,
                LocalDate.of(2000, 1, 1),
                "login-" + id, "clave");
        equipo.agregarInscripcion(new InscripcionEquipo(
                socio, equipo, delantero, camiseta));
    }
}
