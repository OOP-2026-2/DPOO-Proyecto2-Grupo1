package clubdeportivo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.DeporteIndividual;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.deportes.Posicion;
import clubdeportivo.instalaciones.Instalacion;
import clubdeportivo.instalaciones.Reserva;
import clubdeportivo.instalaciones.TipoInstalacion;
import usuarios.tipos.Socio;
import utilidades.excepciones.ReservaNoDisponibleException;

@DisplayName("Reservas de instalaciones")
public class ReservasTest {
    // Tenis: el deporte permite una sola cancha reservada a la vez
    private static final int MAXIMO_CANCHAS_TENIS = 1;
    private static final int DURACION = 60;
    private static final LocalTime HORA = LocalTime.of(10, 0);
    private static final int JUGADORES_SENCILLOS = 2;
    private static final int MINIMO_FUTBOL = 10;

    private ClubDeportivo club;
    private Socio socio;
    private DeporteIndividual tenis;
    private Instalacion canchaTenis;
    private Instalacion canchaFutbol;
    private DeporteConjunto futbol;
    private LocalDate fecha;

    @BeforeEach
    void prepararInstalaciones() {
        club = new ClubDeportivo();
        fecha = LocalDate.now().plusDays(3);
        socio = new Socio("S1", "Socio Uno", LocalDate.of(2000, 1, 1), "socio1", "clave");
        club.registrarUsuario(socio);
        tenis = new DeporteIndividual("Tenis", MAXIMO_CANCHAS_TENIS);
        club.registrarDeporteIndividual(tenis);
        canchaTenis = new Instalacion("TENIS-1", TipoInstalacion.CANCHA_TENIS, 4, false, tenis);
        club.registrarInstalacion(canchaTenis);
        futbol = new DeporteConjunto("Fútbol", MINIMO_FUTBOL, 3, 1, true);
        futbol.agregarPosicion(new Posicion("Jugador"));
        club.registrarDeporteConjunto(futbol);
        canchaFutbol = new Instalacion("FUTBOL-1", TipoInstalacion.CANCHA_CONJUNTO, 22, false);
        club.registrarInstalacion(canchaFutbol);
    }

    @Test
    @DisplayName("Una reserva válida queda registrada en el club y en la instalación")
    void reservaValida() throws ReservaNoDisponibleException {
        Reserva reserva = new Reserva(socio, canchaTenis, fecha, HORA, DURACION, JUGADORES_SENCILLOS,
                Modalidad.SENCILLOS);

        club.registrarReserva(reserva);

        assertAll("reserva",
                () -> assertEquals(1, club.getReservas().size()),
                () -> assertTrue(canchaTenis.getReservas().contains(reserva)),
                () -> assertFalse(club.consultarDisponibilidad(canchaTenis, fecha, HORA, DURACION)));
    }

    @ParameterizedTest(name = "{0} jugadores")
    @ValueSource(ints = { 1, 3, 4 })
    @DisplayName("En sencillos de tenis solo se admiten dos jugadores")
    void jugadoresInvalidosEnSencillos(int jugadores) {
        assertThrows(IllegalArgumentException.class, () -> new Reserva(socio, canchaTenis, fecha, HORA,
                DURACION, jugadores, Modalidad.SENCILLOS));
    }

    @Test
    @DisplayName("Una reserva fuera del horario del club no está disponible")
    void fueraDelHorario() {
        Reserva nocturna = new Reserva(socio, canchaTenis, fecha, club.getHoraCierre().minusMinutes(30),
                DURACION, JUGADORES_SENCILLOS, Modalidad.SENCILLOS);

        assertThrows(ReservaNoDisponibleException.class, () -> club.registrarReserva(nocturna));
        assertFalse(club.consultarDisponibilidad(canchaTenis, fecha, club.getHoraCierre(), DURACION));
    }

    @Test
    @DisplayName("Una reserva que se cruza con otra en la misma instalación se rechaza")
    void cruceDeReservas() throws ReservaNoDisponibleException {
        club.registrarReserva(new Reserva(socio, canchaTenis, fecha, HORA, DURACION, JUGADORES_SENCILLOS,
                Modalidad.SENCILLOS));
        Reserva cruzada = new Reserva(socio, canchaTenis, fecha, HORA.plusMinutes(30), DURACION,
                JUGADORES_SENCILLOS, Modalidad.SENCILLOS);

        assertThrows(ReservaNoDisponibleException.class, () -> club.registrarReserva(cruzada));
    }

    @Test
    @DisplayName("El deporte no puede tener más canchas reservadas al tiempo que su máximo")
    void maximoDeInstalacionesDelDeporte() throws ReservaNoDisponibleException {
        Instalacion otraCancha = new Instalacion("TENIS-2", TipoInstalacion.CANCHA_TENIS, 4, false, tenis);
        club.registrarInstalacion(otraCancha);
        club.registrarReserva(new Reserva(socio, canchaTenis, fecha, HORA, DURACION, JUGADORES_SENCILLOS,
                Modalidad.SENCILLOS));
        Reserva segunda = new Reserva(socio, otraCancha, fecha, HORA, DURACION, JUGADORES_SENCILLOS,
                Modalidad.SENCILLOS);

        assertThrows(ReservaNoDisponibleException.class, () -> club.registrarReserva(segunda));
    }

    @Test
    @DisplayName("No se reserva en una fecha pasada")
    void fechaPasada() {
        Reserva pasada = new Reserva(socio, canchaTenis, LocalDate.now().minusDays(1), HORA, DURACION,
                JUGADORES_SENCILLOS, Modalidad.SENCILLOS);

        assertThrows(IllegalArgumentException.class, () -> club.registrarReserva(pasada));
    }

    @Test
    @DisplayName("La cancha de conjunto exige el mínimo de jugadores de su deporte")
    void canchaDeConjuntoExigeMinimo() throws ReservaNoDisponibleException {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new Reserva(socio, canchaFutbol, fecha, HORA, DURACION, MINIMO_FUTBOL - 1, futbol));
        Reserva completa = new Reserva(socio, canchaFutbol, fecha, HORA, DURACION, MINIMO_FUTBOL, futbol);
        club.registrarReserva(completa);

        assertTrue(error.getMessage().contains(String.valueOf(MINIMO_FUTBOL)));
        assertEquals(futbol, completa.getDeporteConjunto());
    }

    @Test
    @DisplayName("La cancha de conjunto se reserva indicando un deporte registrado")
    void canchaDeConjuntoSinDeporte() {
        DeporteConjunto noRegistrado = new DeporteConjunto("Rugby", 2, 4, 2, true);
        Reserva sinDeporte = new Reserva(socio, canchaFutbol, fecha, HORA, DURACION, MINIMO_FUTBOL,
                Modalidad.DOBLES);
        Reserva otroDeporte = new Reserva(socio, canchaFutbol, fecha, HORA, DURACION, MINIMO_FUTBOL,
                noRegistrado);

        assertAll("deporte obligatorio",
                () -> assertThrows(IllegalArgumentException.class, () -> club.registrarReserva(sinDeporte)),
                () -> assertThrows(IllegalArgumentException.class, () -> club.registrarReserva(otroDeporte)),
                () -> assertTrue(club.getReservas().isEmpty()));
    }
}
