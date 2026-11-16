package clubdeportivo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.torneos.BonoDescuento;
import clubdeportivo.torneos.EncuentroTorneo;
import clubdeportivo.torneos.EstadoBono;
import clubdeportivo.torneos.FormatoTorneo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.TorneoIndividual;
import usuarios.tipos.Socio;

@DisplayName("Reglas de encuentros de torneo y bonos de descuento")
public class EncuentroBonoTest {
    @Test
    @DisplayName("Un encuentro no puede enfrentar una inscripción consigo misma")
    void encuentroRechazaLaMismaInscripcionComoRival() {
        TorneoIndividual torneo = torneoConDosInscripciones();
        InscripcionIndividualTorneo inscripcion =
                (InscripcionIndividualTorneo) torneo.getInscripciones().get(0);
        assertThrows(IllegalArgumentException.class,
                () -> new EncuentroTorneo("E1", LocalDate.now(),
                        LocalTime.of(10, 0), inscripcion, inscripcion));
    }

    @Test
    @DisplayName("Un encuentro no finaliza si no tiene marcador")
    void encuentroNoFinalizaSinMarcador() {
        EncuentroTorneo encuentro = encuentroProgramado();
        assertThrows(IllegalStateException.class, encuentro::finalizar);
    }

    @Test
    @DisplayName("Un encuentro cancelado no acepta resultado")
    void encuentroCanceladoNoAceptaResultado() {
        EncuentroTorneo encuentro = encuentroProgramado();
        encuentro.cancelar();
        assertThrows(IllegalStateException.class,
                () -> encuentro.registrarResultado("2-1"));
    }

    @Test
    @DisplayName("Un encuentro finalizado no puede cancelarse")
    void encuentroFinalizadoNoPuedeCancelarse() {
        EncuentroTorneo encuentro = encuentroProgramado();
        encuentro.registrarResultado("2-1");
        encuentro.finalizar();
        assertThrows(IllegalStateException.class, encuentro::cancelar);
    }

    @Test
    @DisplayName("Un bono vencido no aplica descuento")
    void bonoVencidoNoAplicaDescuento() {
        BonoDescuento bono = bono(
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(1));
        assertFalse(bono.estaDisponible());
        assertEquals(EstadoBono.VENCIDO, bono.getEstado());
        assertEquals(0, bono.calcularDescuento(100000), 0.001);
    }

    @Test
    @DisplayName("Un bono disponible descuenta su porcentaje del subtotal")
    void bonoDisponibleCalculaPorcentaje() {
        BonoDescuento bono = bono(
                LocalDate.now(), LocalDate.now().plusDays(30));
        assertEquals(10000, bono.calcularDescuento(100000), 0.001);
    }

    @Test
    @DisplayName("Un bono no puede usarse dos veces")
    void bonoNoPuedeUsarseDosVeces() {
        BonoDescuento bono = bono(
                LocalDate.now(), LocalDate.now().plusDays(30));
        bono.usar();
        assertEquals(EstadoBono.UTILIZADO, bono.getEstado());
        assertThrows(IllegalStateException.class, bono::usar);
    }

    @Test
    @DisplayName("El bono rechaza un subtotal negativo")
    void bonoRechazaSubtotalNegativo() {
        BonoDescuento bono = bono(
                LocalDate.now(), LocalDate.now().plusDays(30));
        assertThrows(IllegalArgumentException.class,
                () -> bono.calcularDescuento(-1));
    }

    private EncuentroTorneo encuentroProgramado() {
        TorneoIndividual torneo = torneoConDosInscripciones();
        return new EncuentroTorneo("E1", LocalDate.now(),
                LocalTime.of(10, 0),
                torneo.getInscripciones().get(0),
                torneo.getInscripciones().get(1));
    }

    private TorneoIndividual torneoConDosInscripciones() {
        TorneoIndividual torneo = DatosPruebaTorneo.torneo(
                "BONO", TipoTorneo.RECREATIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, 4);
        DatosPruebaTorneo.inscribirExterno(torneo, "P1");
        DatosPruebaTorneo.inscribirExterno(torneo, "P2");
        return torneo;
    }

    private BonoDescuento bono(LocalDate creacion,
            LocalDate vencimiento) {
        TorneoIndividual torneo = torneoConDosInscripciones();
        Socio socio = new Socio("S1", "Socio",
                LocalDate.of(2000, 1, 1), "socio", "clave");
        return new BonoDescuento(socio, 0.10, creacion,
                vencimiento, EstadoBono.DISPONIBLE,
                torneo.getInscripciones().get(0));
    }
}
