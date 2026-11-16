package clubdeportivo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubdeportivo.turnos.DiaSemana;
import clubdeportivo.turnos.EstadoSolicitud;
import clubdeportivo.turnos.SolicitudCambioTurno;
import clubdeportivo.turnos.Turno;
import tiendaclub.inventario.Bebida;
import tiendaclub.inventario.SugerenciaProducto;
import usuarios.tipos.empleados.Administrador;
import usuarios.tipos.empleados.Entrenador;
import usuarios.tipos.empleados.Fisioterapeuta;

@DisplayName("Turnos de empleados y sugerencias de productos")
public class TurnosYSugerenciasTest {
    // El club por defecto exige dos entrenadores y un fisioterapeuta en cada turno
    private static final LocalTime INICIO = LocalTime.of(8, 0);
    private static final LocalTime FIN = LocalTime.of(12, 0);

    private ClubDeportivo club;
    private Administrador administrador;
    private Entrenador primero;
    private Entrenador segundo;
    private Fisioterapeuta fisioterapeuta;

    @BeforeEach
    public void prepararPersonal() {
        club = new ClubDeportivo();
        administrador = new Administrador("A1", "Admin", LocalDate.of(1980, 1, 1), "admin", "clave");
        primero = new Entrenador("E1", "Entrenador Uno", LocalDate.of(1990, 1, 1), "e1", "clave", "COD1");
        segundo = new Entrenador("E2", "Entrenador Dos", LocalDate.of(1990, 1, 1), "e2", "clave", "COD2");
        fisioterapeuta = new Fisioterapeuta("F1", "Fisio", LocalDate.of(1990, 1, 1), "f1", "clave", "COD3");
        club.registrarUsuario(administrador);
        club.registrarUsuario(primero);
        club.registrarUsuario(segundo);
        club.registrarUsuario(fisioterapeuta);
    }

    @Test
    @DisplayName("Un turno fuera del horario del club se rechaza")
    public void turnoFueraDelHorario() {
        assertThrows(IllegalArgumentException.class, () -> club.asignarTurno(primero,
                new Turno(DiaSemana.LUNES, club.getHoraApertura().minusHours(1), FIN)));
    }

    @Test
    @DisplayName("El turno cumple la cobertura mínima con dos entrenadores y un fisioterapeuta")
    public void coberturaMinima() {
        Turno lunes = new Turno(DiaSemana.LUNES, INICIO, FIN);
        club.asignarTurno(primero, lunes);
        club.asignarTurno(segundo, new Turno(DiaSemana.LUNES, INICIO, FIN));

        assertFalse(club.cumpleCoberturaMinima(lunes));

        club.asignarTurno(fisioterapeuta, new Turno(DiaSemana.LUNES, INICIO, FIN));
        assertTrue(club.cumpleCoberturaMinima(lunes));
    }

    @Test
    @DisplayName("Un cambio que deja el turno sin cobertura no se puede aprobar")
    public void cambioSinCoberturaSeRechaza() {
        Turno turno = new Turno(DiaSemana.LUNES, INICIO, FIN);
        club.asignarTurno(primero, turno);
        club.asignarTurno(segundo, new Turno(DiaSemana.LUNES, INICIO, FIN));
        club.asignarTurno(fisioterapeuta, new Turno(DiaSemana.LUNES, INICIO, FIN));

        SolicitudCambioTurno solicitud = club.solicitarCambioTurno(primero, turno, DiaSemana.MARTES,
                INICIO, FIN);

        assertAll("solicitud",
                () -> assertTrue(solicitud.estaPendiente()),
                () -> assertFalse(club.validarCobertura(solicitud)),
                () -> assertThrows(IllegalStateException.class,
                        () -> club.aprobarCambioTurno(solicitud, administrador)));
    }

    @Test
    @DisplayName("El administrador puede rechazar una solicitud pendiente una sola vez")
    public void rechazarSolicitud() {
        Turno turno = new Turno(DiaSemana.LUNES, INICIO, FIN);
        club.asignarTurno(primero, turno);
        SolicitudCambioTurno solicitud = club.solicitarCambioTurno(primero, turno, DiaSemana.MARTES,
                INICIO, FIN);

        club.rechazarCambioTurno(solicitud, administrador);

        assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado());
        assertThrows(IllegalStateException.class, () -> club.rechazarCambioTurno(solicitud, administrador));
    }

    @Test
    @DisplayName("Una sugerencia aprobada crea el producto en el club")
    public void aprobarSugerencia() {
        SugerenciaProducto sugerencia = new SugerenciaProducto("Termo", "Termo de agua caliente", primero);
        club.registrarSugerencia(sugerencia);

        club.aprobarSugerencia(sugerencia, administrador, new Bebida("Termo", 15000, true));

        assertAll("sugerencia aprobada",
                () -> assertFalse(sugerencia.estaPendiente()),
                () -> assertNotNull(club.buscarProducto("Termo")));
    }

    @Test
    @DisplayName("El producto de la aprobación debe corresponder a la sugerencia")
    public void productoDistintoALaSugerencia() {
        SugerenciaProducto sugerencia = new SugerenciaProducto("Termo", "Termo de agua caliente", primero);
        club.registrarSugerencia(sugerencia);

        assertThrows(IllegalArgumentException.class, () -> club.aprobarSugerencia(sugerencia, administrador,
                new Bebida("Café", 3000, true)));
    }
}
