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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.deportes.Nivel;
import clubdeportivo.torneos.EncuentroTorneo;
import clubdeportivo.torneos.EstadoTorneo;
import clubdeportivo.torneos.FormatoTorneo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.InscripcionTorneo;
import clubdeportivo.torneos.PremioCompetitivo;
import clubdeportivo.torneos.PremioRecreativo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.TorneoIndividual;
import usuarios.tipos.ParticipanteExterno;

@DisplayName("Reglas generales de los torneos")
public class TorneoReglasTest {
    // Tamaños de torneo usados en los escenarios: tres para todos contra todos, cuatro para dos rondas
    private static final int CAPACIDAD_TRES = 3;
    private static final int CAPACIDAD_CUATRO = 4;
    private static final int CAPACIDAD_SEIS = 6;

    @Test
    @DisplayName("Se rechaza un torneo cuya hora final no es posterior a la inicial")
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
    @DisplayName("Se rechaza un torneo recreativo con tarifa")
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
    @DisplayName("Se rechaza un torneo competitivo sin tarifa")
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
    @DisplayName("No inicia un torneo con una sola inscripción")
    void noIniciaConUnaSolaInscripcion() {
        TorneoIndividual torneo = DatosPruebaTorneo.torneoDeHoy(
                "UNICO", TipoTorneo.RECREATIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, 4);
        DatosPruebaTorneo.inscribirExterno(torneo, "P1");
        assertFalse(torneo.puedeIniciar());
        assertThrows(IllegalStateException.class, torneo::iniciar);
    }

    @Test
    @DisplayName("Se rechaza un participante inscrito dos veces")
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
    @DisplayName("Se rechaza la inscripción cuando no hay cupo")
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
    @DisplayName("No se permite retirar una inscripción después de iniciar")
    void noPermiteRetiroDespuesDeIniciar() {
        TorneoIndividual torneo = DatosPruebaTorneo.torneoDeHoy(
                "RETIRO", TipoTorneo.RECREATIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, 4);
        InscripcionIndividualTorneo inscripcion =
                DatosPruebaTorneo.inscribirExterno(torneo, "P1");
        DatosPruebaTorneo.inscribirExterno(torneo, "P2");
        torneo.iniciar();
        assertThrows(IllegalStateException.class,
                () -> torneo.retirarInscripcion(inscripcion));
    }

    @Test
    @DisplayName("Todos contra todos con tres participantes genera tres encuentros")
    void generaTresEncuentrosParaTresParticipantes() {
        TorneoIndividual torneo = DatosPruebaTorneo.torneoDeHoy(
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

    @ParameterizedTest(name = "Marcador \"{0}\"")
    @ValueSource(strings = { "dos a uno", "3", "2-1-0", "-1", "a-b", "-2-1" })
    @DisplayName("Se rechaza un marcador con formato inválido")
    void rechazaMarcadorConFormatoInvalido(String marcador) {
        TorneoIndividual torneo = prepararTorneo("MARCADOR",
                FormatoTorneo.TODOS_CONTRA_TODOS);
        EncuentroTorneo encuentro = torneo.getEncuentros().get(0);

        assertThrows(IllegalArgumentException.class,
                () -> torneo.registrarResultadoEncuentro(
                        encuentro, marcador));
        assertFalse(encuentro.estaFinalizado());
    }

    @Test
    @DisplayName("Con los mismos puntos gana quien tiene mejor diferencia de marcador")
    void desempataPorDiferenciaDeMarcador() {
        TorneoIndividual torneo = DatosPruebaTorneo.torneoDeHoy("DESEMPATE",
                TipoTorneo.RECREATIVO, FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, CAPACIDAD_TRES);
        torneo.liberarCuposPreferenciales();
        InscripcionIndividualTorneo primero = DatosPruebaTorneo.inscribirExterno(torneo, "D1");
        InscripcionIndividualTorneo segundo = DatosPruebaTorneo.inscribirExterno(torneo, "D2");
        InscripcionIndividualTorneo tercero = DatosPruebaTorneo.inscribirExterno(torneo, "D3");
        torneo.iniciar();

        // Cada uno gana un partido: empatan en puntos y decide la diferencia
        registrar(torneo, primero, segundo, "1-0");
        registrar(torneo, segundo, tercero, "5-0");
        registrar(torneo, tercero, primero, "1-0");

        assertSame(segundo, torneo.determinarGanador());
        assertAll("tabla ordenada por diferencia",
                () -> assertEquals(segundo, torneo.getPosiciones().get(0).getInscripcion()),
                () -> assertEquals(4, torneo.getPosiciones().get(0).getDiferencia()),
                () -> assertEquals(1, torneo.getPosiciones().get(0).getPosicion()));
    }

    @Test
    @DisplayName("Eliminación directa con cuatro inscritos genera la final al cerrar la primera ronda")
    void eliminacionDirectaGeneraSiguienteRonda() {
        TorneoIndividual torneo = DatosPruebaTorneo.torneoDeHoy("RONDAS",
                TipoTorneo.RECREATIVO, FormatoTorneo.ELIMINACION_DIRECTA,
                Modalidad.SENCILLOS, CAPACIDAD_CUATRO);
        torneo.liberarCuposPreferenciales();

        for (int i = 1; i <= CAPACIDAD_CUATRO; i++) {
            DatosPruebaTorneo.inscribirExterno(torneo, "R" + i);
        }

        torneo.iniciar();
        assertEquals(CAPACIDAD_CUATRO / 2, torneo.getEncuentros().size());

        torneo.registrarResultadoEncuentro(torneo.getEncuentros().get(0), "2-0");
        torneo.registrarResultadoEncuentro(torneo.getEncuentros().get(1), "0-2");

        assertEquals(CAPACIDAD_CUATRO / 2 + 1, torneo.getEncuentros().size());
        EncuentroTorneo finalTorneo = torneo.getEncuentros().get(2);
        assertEquals(2, finalTorneo.getRonda());

        torneo.registrarResultadoEncuentro(finalTorneo, "3-1");
        torneo.finalizar();

        assertSame(finalTorneo.getInscripcionLocal(), torneo.determinarGanador());
    }

    @Test
    @DisplayName("No se registra un resultado si el torneo no está en curso")
    void noRegistraResultadoSiTorneoNoEstaEnCurso() {
        TorneoIndividual torneo = prepararTorneo("CERRADO",
                FormatoTorneo.TODOS_CONTRA_TODOS);
        EncuentroTorneo encuentro = torneo.getEncuentros().get(0);
        torneo.registrarResultadoEncuentro(encuentro, "1-0");
        torneo.finalizar();

        assertThrows(IllegalStateException.class,
                () -> torneo.registrarResultadoEncuentro(encuentro, "2-0"));
    }

    @Test
    @DisplayName("Un torneo no se puede iniciar antes de su fecha")
    void noIniciaAntesDeSuFecha() {
        TorneoIndividual futuro = DatosPruebaTorneo.torneo("FUTURO", TipoTorneo.RECREATIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS, CAPACIDAD_CUATRO);
        DatosPruebaTorneo.inscribirExterno(futuro, "F1");
        DatosPruebaTorneo.inscribirExterno(futuro, "F2");

        IllegalStateException error = assertThrows(IllegalStateException.class, futuro::iniciar);

        assertAll("torneo futuro",
                () -> assertFalse(futuro.puedeIniciar()),
                () -> assertTrue(error.getMessage().contains(futuro.getFechaInicio().toString())),
                () -> assertEquals(EstadoTorneo.ABIERTO, futuro.getEstado()));
    }

    @ParameterizedTest(name = "{0} participantes")
    @ValueSource(ints = { 2, 3, 4, 5, 6 })
    @DisplayName("En todos contra todos cada pareja juega una vez y nadie tiene dos encuentros a la misma hora")
    void todosContraTodosSinCruces(int participantes) {
        TorneoIndividual torneo = DatosPruebaTorneo.torneoDeHoy("CRUCES", TipoTorneo.RECREATIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS, CAPACIDAD_SEIS);
        torneo.liberarCuposPreferenciales();
        List<InscripcionIndividualTorneo> inscritos = new ArrayList<InscripcionIndividualTorneo>();

        for (int i = 1; i <= participantes; i++) {
            inscritos.add(DatosPruebaTorneo.inscribirExterno(torneo, "C" + i));
        }

        torneo.iniciar();

        assertEquals(participantes * (participantes - 1) / 2, torneo.getEncuentros().size());

        for (InscripcionIndividualTorneo inscrito : inscritos) {
            Set<LocalTime> horas = new HashSet<LocalTime>();
            Set<InscripcionTorneo> rivales = new HashSet<InscripcionTorneo>();

            for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
                if (encuentro.getInscripcionLocal() == inscrito
                        || encuentro.getInscripcionVisitante() == inscrito) {

                    assertTrue(horas.add(encuentro.getHoraInicio()), "dos encuentros a la misma hora");
                    rivales.add(encuentro.getInscripcionLocal() == inscrito
                            ? encuentro.getInscripcionVisitante()
                            : encuentro.getInscripcionLocal());
                }
            }

            assertEquals(participantes - 1, rivales.size());
        }

        for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
            assertAll("horario del encuentro",
                    () -> assertEquals(torneo.getFechaInicio(), encuentro.getFecha()),
                    () -> assertFalse(encuentro.getHoraInicio().isBefore(torneo.getHoraInicio())),
                    () -> assertTrue(encuentro.getHoraInicio().isBefore(torneo.getHoraFin())));
        }
    }

    @Test
    @DisplayName("En eliminación directa cada ronda se juega el mismo día en una franja posterior")
    void eliminacionDirectaPorFranjas() {
        TorneoIndividual torneo = DatosPruebaTorneo.torneoDeHoy("FRANJAS", TipoTorneo.RECREATIVO,
                FormatoTorneo.ELIMINACION_DIRECTA, Modalidad.SENCILLOS, CAPACIDAD_CUATRO);
        torneo.liberarCuposPreferenciales();

        for (int i = 1; i <= CAPACIDAD_CUATRO; i++) {
            DatosPruebaTorneo.inscribirExterno(torneo, "F" + i);
        }

        torneo.iniciar();
        torneo.registrarResultadoEncuentro(torneo.getEncuentros().get(0), "1-0");
        torneo.registrarResultadoEncuentro(torneo.getEncuentros().get(1), "1-0");
        EncuentroTorneo finalTorneo = torneo.getEncuentros().get(2);

        assertAll("rondas",
                () -> assertEquals(2, torneo.calcularTotalRondas()),
                () -> assertEquals(torneo.getHoraInicio(), torneo.getEncuentros().get(0).getHoraInicio()),
                () -> assertEquals(torneo.getEncuentros().get(0).getHoraInicio(),
                        torneo.getEncuentros().get(1).getHoraInicio()),
                () -> assertEquals(torneo.calcularHoraRonda(2), finalTorneo.getHoraInicio()),
                () -> assertTrue(finalTorneo.getHoraInicio().isAfter(torneo.getHoraInicio())),
                () -> assertEquals(torneo.getFechaInicio(), finalTorneo.getFecha()));
    }

    @Test
    @DisplayName("Los premios se otorgan una sola vez: un torneo finalizado no se vuelve a finalizar")
    void premiosUnaSolaVez() {
        TorneoIndividual torneo = DatosPruebaTorneo.torneoDeHoy("PREMIO", TipoTorneo.COMPETITIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS, CAPACIDAD_CUATRO);
        DatosPruebaTorneo.inscribirExterno(torneo, "PR1");
        DatosPruebaTorneo.inscribirExterno(torneo, "PR2");
        torneo.iniciar();
        torneo.registrarResultadoEncuentro(torneo.getEncuentros().get(0), "2-1");
        torneo.finalizar();
        int premios = torneo.getPremiosOtorgados().size();

        assertThrows(IllegalStateException.class, torneo::finalizar);
        assertEquals(premios, torneo.getPremiosOtorgados().size());
    }

    private void registrar(TorneoIndividual torneo, InscripcionIndividualTorneo local,
            InscripcionIndividualTorneo visitante, String marcador) {

        for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
            boolean mismoOrden = encuentro.getInscripcionLocal() == local
                    && encuentro.getInscripcionVisitante() == visitante;
            boolean ordenInverso = encuentro.getInscripcionLocal() == visitante
                    && encuentro.getInscripcionVisitante() == local;

            if (mismoOrden) {
                torneo.registrarResultadoEncuentro(encuentro, marcador);
                return;
            }

            if (ordenInverso) {
                String[] partes = marcador.split("-");
                torneo.registrarResultadoEncuentro(encuentro, partes[1] + "-" + partes[0]);
                return;
            }
        }
    }

    @Test
    @DisplayName("Se rechaza un empate en eliminación directa")
    void rechazaEmpateEnEliminacionDirecta() {
        TorneoIndividual torneo = prepararTorneo("ELIMINACION",
                FormatoTorneo.ELIMINACION_DIRECTA);
        EncuentroTorneo encuentro = torneo.getEncuentros().get(0);
        assertThrows(IllegalArgumentException.class,
                () -> torneo.registrarResultadoEncuentro(
                        encuentro, "1-1"));
    }

    @Test
    @DisplayName("No finaliza un torneo con un encuentro pendiente")
    void noFinalizaConEncuentroPendiente() {
        TorneoIndividual torneo = prepararTorneo("PENDIENTE",
                FormatoTorneo.TODOS_CONTRA_TODOS);
        assertFalse(torneo.puedeFinalizar());
        assertThrows(IllegalStateException.class, torneo::finalizar);
        assertEquals(EstadoTorneo.EN_CURSO, torneo.getEstado());
    }

    @Test
    @DisplayName("Finaliza cuando todos los encuentros terminaron")
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
        TorneoIndividual torneo = DatosPruebaTorneo.torneoDeHoy(
                id, TipoTorneo.RECREATIVO, formato,
                Modalidad.SENCILLOS, 4);
        DatosPruebaTorneo.inscribirExterno(torneo, id + "-P1");
        DatosPruebaTorneo.inscribirExterno(torneo, id + "-P2");
        torneo.iniciar();
        return torneo;
    }
}
