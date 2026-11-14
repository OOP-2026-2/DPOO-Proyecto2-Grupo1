import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.deportes.Nivel;
import clubdeportivo.torneos.EncuentroTorneo;
import clubdeportivo.torneos.EstadoTorneo;
import clubdeportivo.torneos.FormatoTorneo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.PremioCompetitivo;
import clubdeportivo.torneos.PremioRecreativo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.TorneoIndividual;
import usuarios.tipos.ParticipanteExterno;

public class TorneoReglasTest {
        @Test
        void rechazaHorarioSinDuracion() {
                LocalDate fecha = DatosPruebaTorneo.fechaTorneo();
                assertThrows(IllegalArgumentException.class,
                                () -> new TorneoIndividual("HORARIO", "Horario", fecha,
                                                LocalTime.of(12, 0), LocalTime.of(12, 0),
                                                FormatoTorneo.TODOS_CONTRA_TODOS, 4,
                                                TipoTorneo.RECREATIVO,
                                                LocalDateTime.of(fecha.minusDays(1), LocalTime.NOON),
                                                new PremioRecreativo(0.10, 30), 0,
                                                DatosPruebaTorneo.tenis(), Nivel.INTERMEDIO,
                                                Modalidad.SENCILLOS));
        }

        @Test
        void rechazaTorneoRecreativoConTarifa() {
                LocalDate fecha = DatosPruebaTorneo.fechaTorneo();
                assertThrows(IllegalArgumentException.class,
                                () -> new TorneoIndividual("TARIFA-R", "Recreativo", fecha,
                                                LocalTime.of(10, 0), LocalTime.of(12, 0),
                                                FormatoTorneo.TODOS_CONTRA_TODOS, 4,
                                                TipoTorneo.RECREATIVO,
                                                LocalDateTime.of(fecha.minusDays(1), LocalTime.NOON),
                                                new PremioRecreativo(0.10, 30), 10000,
                                                DatosPruebaTorneo.tenis(), Nivel.INTERMEDIO,
                                                Modalidad.SENCILLOS));
        }

        @Test
        void rechazaTorneoCompetitivoSinTarifa() {
                LocalDate fecha = DatosPruebaTorneo.fechaTorneo();
                assertThrows(IllegalArgumentException.class,
                                () -> new TorneoIndividual("TARIFA-C", "Competitivo", fecha,
                                                LocalTime.of(10, 0), LocalTime.of(12, 0),
                                                FormatoTorneo.TODOS_CONTRA_TODOS, 4,
                                                TipoTorneo.COMPETITIVO,
                                                LocalDateTime.of(fecha.minusDays(1), LocalTime.NOON),
                                                new PremioCompetitivo(0.50), 0,
                                                DatosPruebaTorneo.tenis(), Nivel.INTERMEDIO,
                                                Modalidad.SENCILLOS));
        }

        @Test
        void noIniciaConUnaSolaInscripcion() {
                TorneoIndividual torneo = DatosPruebaTorneo.torneo(
                                "UNICO", TipoTorneo.RECREATIVO,
                                FormatoTorneo.TODOS_CONTRA_TODOS,
                                Modalidad.SENCILLOS, 4);
                DatosPruebaTorneo.inscribirExterno(torneo, "P1");
                assertFalse(torneo.puedeIniciar());
                assertThrows(IllegalStateException.class, torneo::iniciar);
        }

        @Test
        void rechazaParticipanteDuplicado() {
                TorneoIndividual torneo = DatosPruebaTorneo.torneo(
                                "DUPLICADO", TipoTorneo.RECREATIVO,
                                FormatoTorneo.TODOS_CONTRA_TODOS,
                                Modalidad.SENCILLOS, 4);
                ParticipanteExterno participante = DatosPruebaTorneo.externo("P1");
                torneo.inscribirParticipantes(DatosPruebaTorneo.lista(participante),
                                TipoCupo.REGULAR);
                assertThrows(IllegalArgumentException.class,
                                () -> torneo.inscribirParticipantes(
                                                DatosPruebaTorneo.lista(participante),
                                                TipoCupo.REGULAR));
        }

        @Test
        void rechazaInscripcionCuandoNoHayCupo() {
                TorneoIndividual torneo = DatosPruebaTorneo.torneo(
                                "LLENO", TipoTorneo.RECREATIVO,
                                FormatoTorneo.TODOS_CONTRA_TODOS,
                                Modalidad.SENCILLOS, 2);
                torneo.liberarCuposPreferenciales();
                DatosPruebaTorneo.inscribirExterno(torneo, "P1");
                DatosPruebaTorneo.inscribirExterno(torneo, "P2");
                assertEquals(0, torneo.cantidadCuposDisponibles());
                assertThrows(IllegalStateException.class,
                                () -> DatosPruebaTorneo.inscribirExterno(torneo, "P3"));
        }

        @Test
        void noPermiteRetiroDespuesDeIniciar() {
                TorneoIndividual torneo = DatosPruebaTorneo.torneo(
                                "RETIRO", TipoTorneo.RECREATIVO,
                                FormatoTorneo.TODOS_CONTRA_TODOS,
                                Modalidad.SENCILLOS, 4);
                InscripcionIndividualTorneo inscripcion = DatosPruebaTorneo.inscribirExterno(torneo, "P1");
                DatosPruebaTorneo.inscribirExterno(torneo, "P2");
                torneo.iniciar();
                assertThrows(IllegalStateException.class,
                                () -> torneo.retirarInscripcion(inscripcion));
        }

        @Test
        void generaTresEncuentrosParaTresParticipantes() {
                TorneoIndividual torneo = DatosPruebaTorneo.torneo(
                                "TRES", TipoTorneo.RECREATIVO,
                                FormatoTorneo.TODOS_CONTRA_TODOS,
                                Modalidad.SENCILLOS, 4);
                torneo.liberarCuposPreferenciales();
                DatosPruebaTorneo.inscribirExterno(torneo, "P1");
                DatosPruebaTorneo.inscribirExterno(torneo, "P2");
                DatosPruebaTorneo.inscribirExterno(torneo, "P3");
                torneo.iniciar();
                assertEquals(3, torneo.getEncuentros().size());
                assertEquals(3, torneo.getPosiciones().size());
        }

        @Test
        void rechazaMarcadorConFormatoInvalido() {
                TorneoIndividual torneo = prepararTorneo("MARCADOR",
                                FormatoTorneo.TODOS_CONTRA_TODOS);
                EncuentroTorneo encuentro = torneo.getEncuentros().get(0);
                assertThrows(IllegalArgumentException.class,
                                () -> torneo.registrarResultadoEncuentro(
                                                encuentro, "dos a uno"));
        }

        @Test
        void rechazaEmpateEnEliminacionDirecta() {
                TorneoIndividual torneo = prepararTorneo("ELIMINACION",
                                FormatoTorneo.ELIMINACION_DIRECTA);
                EncuentroTorneo encuentro = torneo.getEncuentros().get(0);
                assertThrows(IllegalArgumentException.class,
                                () -> torneo.registrarResultadoEncuentro(
                                                encuentro, "1-1"));
        }

        @Test
        void noFinalizaConEncuentroPendiente() {
                TorneoIndividual torneo = prepararTorneo("PENDIENTE",
                                FormatoTorneo.TODOS_CONTRA_TODOS);
                assertFalse(torneo.puedeFinalizar());
                assertThrows(IllegalStateException.class, torneo::finalizar);
                assertEquals(EstadoTorneo.EN_CURSO, torneo.getEstado());
        }

        @Test
        void finalizaCuandoTodosLosEncuentrosTerminaron() {
                TorneoIndividual torneo = prepararTorneo("FINALIZA",
                                FormatoTorneo.TODOS_CONTRA_TODOS);
                torneo.registrarResultadoEncuentro(
                                torneo.getEncuentros().get(0), "2-0");
                assertTrue(torneo.puedeFinalizar());
                torneo.finalizar();
                assertEquals(EstadoTorneo.FINALIZADO, torneo.getEstado());
        }

        private TorneoIndividual prepararTorneo(String id,
                        FormatoTorneo formato) {
                TorneoIndividual torneo = DatosPruebaTorneo.torneo(
                                id, TipoTorneo.RECREATIVO, formato,
                                Modalidad.SENCILLOS, 4);
                DatosPruebaTorneo.inscribirExterno(torneo, id + "-P1");
                DatosPruebaTorneo.inscribirExterno(torneo, id + "-P2");
                torneo.iniciar();
                return torneo;
        }
}
