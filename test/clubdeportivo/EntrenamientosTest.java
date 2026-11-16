package clubdeportivo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import clubdeportivo.deportes.Categoria;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.DeporteIndividual;
import clubdeportivo.deportes.Equipo;
import clubdeportivo.deportes.Nivel;
import clubdeportivo.deportes.Posicion;
import clubdeportivo.entrenamientos.EstadoAsistencia;
import clubdeportivo.entrenamientos.RegistroAsistencia;
import clubdeportivo.entrenamientos.SesionEntrenamiento;
import clubdeportivo.entrenamientos.TipoSesion;
import clubdeportivo.instalaciones.Instalacion;
import clubdeportivo.instalaciones.TipoInstalacion;
import clubdeportivo.turnos.DiaSemana;
import clubdeportivo.turnos.Turno;
import usuarios.tipos.Socio;
import usuarios.tipos.empleados.Entrenador;

@DisplayName("Sesiones de entrenamiento: asistencia y evaluación")
public class EntrenamientosTest {
    // La sesión dura una hora y el turno del entrenador la cubre con holgura
    private static final LocalTime INICIO_SESION = LocalTime.of(10, 0);
    private static final int DURACION_MINUTOS = 60;
    private static final double NOTA_VALIDA = 4.5;

    private ClubDeportivo club;
    private Entrenador entrenador;
    private Equipo equipo;
    private Socio jugador;
    private SesionEntrenamiento sesion;
    private Instalacion cancha;
    private LocalDate fecha;

    @BeforeEach
    void programarSesion() {
        club = new ClubDeportivo();
        fecha = LocalDate.now().plusDays(7);
        entrenador = registrarEntrenador("E1");
        cancha = new Instalacion("CANCHA-1", TipoInstalacion.CANCHA_CONJUNTO, 30, false);
        club.registrarInstalacion(cancha);

        DeporteConjunto futbol = new DeporteConjunto("Fútbol", 2, 3, 1, true);
        Posicion posicion = new Posicion("Volante");
        futbol.agregarPosicion(posicion);
        club.registrarDeporteConjunto(futbol);
        equipo = new Equipo("Séneca FC", Categoria.MAYORES, 10, futbol);
        club.registrarEquipo(equipo);
        jugador = registrarSocio("S1");
        club.inscribirSocioEnEquipo(jugador, equipo, posicion, 8);

        sesion = new SesionEntrenamiento(fecha, INICIO_SESION, DURACION_MINUTOS, entrenador, cancha,
                TipoSesion.GRUPAL);
        sesion.asignarEquipo(equipo);
        club.programarSesion(sesion);
    }

    @Test
    @DisplayName("El entrenador registra la asistencia y la evaluación de un integrante")
    void registraAsistenciaYEvaluacion() {
        RegistroAsistencia registro = club.registrarAsistenciaSesion(entrenador, sesion, jugador,
                EstadoAsistencia.ASISTIO, NOTA_VALIDA, "Buena intensidad");

        assertAll("registro",
                () -> assertSame(registro, sesion.buscarRegistro(jugador)),
                () -> assertEquals(EstadoAsistencia.ASISTIO, registro.getEstado()),
                () -> assertEquals(NOTA_VALIDA, registro.getNotaDesempeno(), 0.001),
                () -> assertEquals("Buena intensidad", registro.getComentario()));
    }

    @Test
    @DisplayName("Solo el entrenador a cargo de la sesión puede registrar su asistencia")
    void otroEntrenadorNoRegistra() {
        Entrenador otro = registrarEntrenador("E2");

        assertThrows(IllegalArgumentException.class, () -> club.registrarAsistenciaSesion(otro, sesion,
                jugador, EstadoAsistencia.ASISTIO, null, null));
        assertNull(sesion.buscarRegistro(jugador));
    }

    @ParameterizedTest(name = "Nota {0}")
    @ValueSource(doubles = { 0.5, 5.5 })
    @DisplayName("Una nota fuera de 1 a 5 se rechaza sin dejar registro")
    void notaFueraDeRango(double nota) {
        assertThrows(IllegalArgumentException.class, () -> club.registrarAsistenciaSesion(entrenador,
                sesion, jugador, EstadoAsistencia.ASISTIO, nota, "Fuera de rango"));
        assertNull(sesion.buscarRegistro(jugador));
    }

    @Test
    @DisplayName("No se evalúa a un socio ausente")
    void ausenteNoSeEvalua() {
        assertThrows(IllegalArgumentException.class, () -> club.registrarAsistenciaSesion(entrenador,
                sesion, jugador, EstadoAsistencia.AUSENTE, NOTA_VALIDA, "No vino"));
    }

    @Test
    @DisplayName("Si se corrige a AUSENTE se borra la evaluación anterior")
    void corregirAAusenteBorraNota() {
        club.registrarAsistenciaSesion(entrenador, sesion, jugador, EstadoAsistencia.TARDE,
                NOTA_VALIDA, "Llegó tarde");

        RegistroAsistencia registro = club.registrarAsistenciaSesion(entrenador, sesion, jugador,
                EstadoAsistencia.AUSENTE, null, null);

        assertNull(registro.getNotaDesempeno());
        assertEquals(1, sesion.getRegistros().size());
    }

    @Test
    @DisplayName("La asistencia debe quedar como asistió, tarde o ausente")
    void estadoPendienteNoSeAcepta() {
        assertThrows(IllegalArgumentException.class, () -> club.registrarAsistenciaSesion(entrenador,
                sesion, jugador, EstadoAsistencia.PENDIENTE, null, null));
    }

    @Test
    @DisplayName("Un socio que no es del equipo de la sesión no tiene asistencia")
    void socioAjenoAlEquipo() {
        Socio ajeno = registrarSocio("S-AJENO");

        assertThrows(IllegalArgumentException.class, () -> club.registrarAsistenciaSesion(entrenador,
                sesion, ajeno, EstadoAsistencia.ASISTIO, null, null));
    }

    @Test
    @DisplayName("Una sesión individual es para un solo socio")
    void sesionIndividualUnSocio() {
        DeporteIndividual tenis = new DeporteIndividual("Tenis", 2);
        club.registrarDeporteIndividual(tenis);
        Instalacion canchaTenis = new Instalacion("TENIS-1", TipoInstalacion.CANCHA_TENIS, 4, false, tenis);
        club.registrarInstalacion(canchaTenis);
        Socio otro = registrarSocio("S2");
        club.inscribirSocioEnAcademia(jugador, tenis, Nivel.INTERMEDIO);
        club.inscribirSocioEnAcademia(otro, tenis, Nivel.INTERMEDIO);
        SesionEntrenamiento individual = new SesionEntrenamiento(fecha, INICIO_SESION.plusHours(2),
                DURACION_MINUTOS, entrenador, canchaTenis, TipoSesion.INDIVIDUAL);
        individual.asignarDeporteIndividual(tenis);
        club.programarSesion(individual);
        club.registrarAsistencia(individual, jugador);

        assertThrows(IllegalStateException.class, () -> club.registrarAsistencia(individual, otro));
    }

    @Test
    @DisplayName("El entrenador y el socio consultan las sesiones que les corresponden")
    void consultarSesiones() {
        Socio sinEquipo = registrarSocio("S-SOLO");

        assertAll("consultas",
                () -> assertEquals(1, club.consultarSesionesDe(entrenador).size()),
                () -> assertTrue(club.consultarSesionesDe(registrarEntrenador("E3")).isEmpty()),
                () -> assertEquals(1, club.consultarSesionesDe(jugador).size()),
                () -> assertTrue(club.consultarSesionesDe(sinEquipo).isEmpty()));
    }

    @Test
    @DisplayName("Una sesión que termina después del cierre del club se rechaza")
    void sesionFueraDelHorario() {
        SesionEntrenamiento tarde = new SesionEntrenamiento(fecha, club.getHoraCierre().minusMinutes(30),
                DURACION_MINUTOS, entrenador, cancha, TipoSesion.GRUPAL);
        tarde.asignarEquipo(equipo);

        assertThrows(IllegalArgumentException.class, () -> club.programarSesion(tarde));
    }

    // Utilidades

    private Entrenador registrarEntrenador(String id) {
        Entrenador nuevo = new Entrenador(id, "Entrenador " + id, LocalDate.of(1990, 1, 1),
                "login-" + id, "clave", "COD-" + id);
        club.registrarUsuario(nuevo);
        DiaSemana dia = DiaSemana.values()[fecha.getDayOfWeek().getValue() - 1];
        club.asignarTurno(nuevo, new Turno(dia, LocalTime.of(8, 0), LocalTime.of(14, 0)));
        return nuevo;
    }

    private Socio registrarSocio(String id) {
        Socio socio = new Socio(id, "Socio " + id, LocalDate.now().minusYears(20), "login-" + id, "clave");
        club.registrarUsuario(socio);
        return socio;
    }
}
