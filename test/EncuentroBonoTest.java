import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDate;
import java.time.LocalTime;
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

public class EncuentroBonoTest {
        @Test
        void encuentroRechazaLaMismaInscripcionComoRival() {
                TorneoIndividual torneo = torneoConDosInscripciones();
                InscripcionIndividualTorneo inscripcion = (InscripcionIndividualTorneo) torneo.getInscripciones()
                                .get(0);
                assertThrows(IllegalArgumentException.class,
                                () -> new EncuentroTorneo("E1", LocalDate.now(),
                                                LocalTime.of(10, 0), inscripcion, inscripcion));
        }

        @Test
        void encuentroNoFinalizaSinMarcador() {
                EncuentroTorneo encuentro = encuentroProgramado();
                assertThrows(IllegalStateException.class, encuentro::finalizar);
        }

        @Test
        void encuentroCanceladoNoAceptaResultado() {
                EncuentroTorneo encuentro = encuentroProgramado();
                encuentro.cancelar();
                assertThrows(IllegalStateException.class,
                                () -> encuentro.registrarResultado("2-1"));
        }

        @Test
        void encuentroFinalizadoNoPuedeCancelarse() {
                EncuentroTorneo encuentro = encuentroProgramado();
                encuentro.registrarResultado("2-1");
                encuentro.finalizar();
                assertThrows(IllegalStateException.class, encuentro::cancelar);
        }

        @Test
        void bonoVencidoNoAplicaDescuento() {
                BonoDescuento bono = bono(
                                LocalDate.now().minusDays(10),
                                LocalDate.now().minusDays(1));
                assertFalse(bono.estaDisponible());
                assertEquals(EstadoBono.VENCIDO, bono.getEstado());
                assertEquals(0, bono.calcularDescuento(100000), 0.001);
        }

        @Test
        void bonoDisponibleCalculaPorcentaje() {
                BonoDescuento bono = bono(
                                LocalDate.now(), LocalDate.now().plusDays(30));
                assertEquals(10000, bono.calcularDescuento(100000), 0.001);
        }

        @Test
        void bonoNoPuedeUsarseDosVeces() {
                BonoDescuento bono = bono(
                                LocalDate.now(), LocalDate.now().plusDays(30));
                bono.usar();
                assertEquals(EstadoBono.UTILIZADO, bono.getEstado());
                assertThrows(IllegalStateException.class, bono::usar);
        }

        @Test
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
