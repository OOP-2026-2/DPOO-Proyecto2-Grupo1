package clubdeportivo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDate;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import clubdeportivo.deportes.InscripcionAcademia;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.deportes.Nivel;
import clubdeportivo.torneos.FormatoTorneo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.ParticipanteTorneo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.TorneoIndividual;
import usuarios.tipos.ParticipanteExterno;
import usuarios.tipos.Socio;
import usuarios.tipos.empleados.Entrenador;

@DisplayName("Inscripciones en torneos individuales")
public class InscripcionesTorneoTest {
    @Test
    @DisplayName("Sencillos rechaza una inscripción de dos personas")
    void sencillosRechazaDosParticipantes() {
        TorneoIndividual torneo = torneo(Modalidad.SENCILLOS);
        assertThrows(IllegalArgumentException.class,
                () -> torneo.inscribirParticipantes(
                        Arrays.<ParticipanteTorneo>asList(
                                DatosPruebaTorneo.externo("P1"),
                                DatosPruebaTorneo.externo("P2")),
                        TipoCupo.REGULAR));
    }

    @Test
    @DisplayName("Dobles rechaza una inscripción de una sola persona")
    void doblesRechazaUnParticipante() {
        TorneoIndividual torneo = torneo(Modalidad.DOBLES);
        assertThrows(IllegalArgumentException.class,
                () -> torneo.inscribirParticipantes(
                        DatosPruebaTorneo.lista(
                                DatosPruebaTorneo.externo("P1")),
                        TipoCupo.REGULAR));
    }

    @Test
    @DisplayName("El cupo preferencial rechaza a un participante externo")
    void cupoPreferencialRechazaParticipanteExterno() {
        TorneoIndividual torneo = torneo(Modalidad.SENCILLOS);
        assertThrows(IllegalArgumentException.class,
                () -> torneo.inscribirParticipantes(
                        DatosPruebaTorneo.lista(
                                DatosPruebaTorneo.externo("P1")),
                        TipoCupo.PREFERENCIAL));
    }

    @Test
    @DisplayName("Un socio sin academia no está habilitado")
    void socioSinAcademiaNoEstaHabilitado() {
        TorneoIndividual torneo = torneo(Modalidad.SENCILLOS);
        Socio socio = socio("S1");
        assertFalse(torneo.validarParticipante(socio));
        assertThrows(IllegalArgumentException.class,
                () -> torneo.inscribirParticipantes(
                        DatosPruebaTorneo.lista(socio),
                        TipoCupo.REGULAR));
    }

    @Test
    @DisplayName("Un socio con academia y nivel correcto está habilitado")
    void socioConAcademiaYNivelCorrectoEstaHabilitado() {
        TorneoIndividual torneo = torneo(Modalidad.SENCILLOS);
        Socio socio = socio("S1");
        InscripcionAcademia academia = new InscripcionAcademia(
                socio, torneo.getDeporte(), Nivel.INTERMEDIO);
        academia.actualizarPuntaje(90);
        torneo.getDeporte().agregarInscripcion(academia);
        assertTrue(torneo.validarParticipante(socio));
        assertTrue(torneo.cumpleCriterioPreferencial(
                DatosPruebaTorneo.lista(socio)));
        assertEquals(90, torneo.calcularRankingPreferencial(
                DatosPruebaTorneo.lista(socio)), 0.001);
    }

    @Test
    @DisplayName("Un empleado sin turno ese día no paga el torneo competitivo")
    void empleadoSinCruceNoPagaTorneoCompetitivo() {
        TorneoIndividual torneo = DatosPruebaTorneo.torneo(
                "EMPLEADO", TipoTorneo.COMPETITIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, 4);
        Entrenador entrenador = new Entrenador(
                "E1", "Entrenador", LocalDate.of(1990, 1, 1),
                "entrenador", "clave", "COD-E1");
        InscripcionIndividualTorneo inscripcion = torneo
                .inscribirParticipantes(
                        DatosPruebaTorneo.lista(entrenador),
                        TipoCupo.REGULAR);
        assertEquals(0, inscripcion.getValorPagado(), 0.001);
    }

    @Test
    @DisplayName("Un participante externo paga la tarifa competitiva")
    void participanteExternoPagaTarifaCompetitiva() {
        TorneoIndividual torneo = DatosPruebaTorneo.torneo(
                "EXTERNO", TipoTorneo.COMPETITIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                Modalidad.SENCILLOS, 4);
        InscripcionIndividualTorneo inscripcion = torneo
                .inscribirParticipantes(
                        DatosPruebaTorneo.lista(
                                DatosPruebaTorneo.externo("P1")),
                        TipoCupo.REGULAR);
        assertEquals(50000, inscripcion.getValorPagado(), 0.001);
    }

    @Test
    @DisplayName("La inscripción detecta el mismo ID aunque sea otro objeto")
    void inscripcionDetectaMismoIdAunqueSeaOtroObjeto() {
        TorneoIndividual torneo = torneo(Modalidad.DOBLES);
        InscripcionIndividualTorneo inscripcion =
                new InscripcionIndividualTorneo(
                        torneo, TipoCupo.REGULAR, 0);
        ParticipanteExterno primero = DatosPruebaTorneo.externo("MISMO");
        ParticipanteExterno segundo = new ParticipanteExterno(
                "mismo", "Otro", LocalDate.of(2001, 1, 1),
                "otro-login", "clave", "otro-contacto");
        inscripcion.agregarParticipante(primero);
        assertThrows(IllegalArgumentException.class,
                () -> inscripcion.agregarParticipante(segundo));
    }

    @Test
    @DisplayName("Una inscripción no puede retirarse dos veces")
    void inscripcionNoPuedeRetirarseDosVeces() {
        TorneoIndividual torneo = torneo(Modalidad.SENCILLOS);
        InscripcionIndividualTorneo inscripcion =
                DatosPruebaTorneo.inscribirExterno(torneo, "P1");
        inscripcion.retirar();
        assertFalse(inscripcion.estaActiva());
        assertThrows(IllegalStateException.class, inscripcion::retirar);
    }

    private TorneoIndividual torneo(Modalidad modalidad) {
        return DatosPruebaTorneo.torneo(
                "INS-" + modalidad, TipoTorneo.RECREATIVO,
                FormatoTorneo.TODOS_CONTRA_TODOS,
                modalidad, 4);
    }

    private Socio socio(String id) {
        return new Socio(id, "Socio " + id,
                LocalDate.of(2000, 1, 1),
                "login-" + id, "clave");
    }
}
