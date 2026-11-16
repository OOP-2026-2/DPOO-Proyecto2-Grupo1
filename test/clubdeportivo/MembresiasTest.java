package clubdeportivo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubdeportivo.membresias.PagoMensualidad;
import usuarios.tipos.Socio;

@DisplayName("Pagos de mensualidad de los socios")
public class MembresiasTest {
    // Valor de la mensualidad y dos periodos consecutivos de prueba
    private static final double MENSUALIDAD = 120000;
    private static final String PERIODO = "2026-11";
    private static final String PERIODO_SIGUIENTE = "2026-12";

    private ClubDeportivo club;
    private Socio socio;

    @BeforeEach
    void registrarSocio() {
        club = new ClubDeportivo();
        socio = new Socio("S1", "Socio Uno", LocalDate.of(1999, 9, 9), "socio1", "clave");
        club.registrarUsuario(socio);
    }

    @Test
    @DisplayName("El pago queda en el club y en el historial del socio")
    void registraPago() {
        PagoMensualidad pago = new PagoMensualidad(LocalDate.now(), PERIODO, MENSUALIDAD, socio);

        club.registrarPagoMensualidad(pago);

        assertAll("pago",
                () -> assertTrue(socio.getPagos().contains(pago)),
                () -> assertEquals(1, club.consultarPagosDe(socio).size()),
                () -> assertEquals(MENSUALIDAD, club.calcularTotalPagos(PERIODO), 0.001));
    }

    @Test
    @DisplayName("Un periodo ya pagado no se paga dos veces")
    void periodoRepetido() {
        club.registrarPagoMensualidad(new PagoMensualidad(LocalDate.now(), PERIODO, MENSUALIDAD, socio));

        assertThrows(IllegalArgumentException.class, () -> club.registrarPagoMensualidad(
                new PagoMensualidad(LocalDate.now(), PERIODO, MENSUALIDAD, socio)));
        assertEquals(1, socio.getPagos().size());
    }

    @Test
    @DisplayName("El total sin periodo suma todos los pagos")
    void totalDeTodosLosPeriodos() {
        club.registrarPagoMensualidad(new PagoMensualidad(LocalDate.now(), PERIODO, MENSUALIDAD, socio));
        club.registrarPagoMensualidad(new PagoMensualidad(LocalDate.now(), PERIODO_SIGUIENTE, MENSUALIDAD,
                socio));

        assertEquals(2 * MENSUALIDAD, club.calcularTotalPagos(null), 0.001);
    }

    @Test
    @DisplayName("No se registra el pago de un socio que no pertenece al club")
    void socioNoRegistrado() {
        Socio externo = new Socio("S2", "Socio Dos", LocalDate.of(1999, 9, 9), "socio2", "clave");

        assertThrows(IllegalArgumentException.class, () -> club.registrarPagoMensualidad(
                new PagoMensualidad(LocalDate.now(), PERIODO, MENSUALIDAD, externo)));
    }

    @Test
    @DisplayName("Un pago debe tener valor mayor que cero")
    void valorInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> new PagoMensualidad(LocalDate.now(), PERIODO, 0, socio));
    }
}
