package clubdeportivo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import clubdeportivo.deportes.Categoria;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.DeporteIndividual;
import clubdeportivo.deportes.Equipo;
import clubdeportivo.deportes.InscripcionAcademia;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.deportes.Nivel;
import clubdeportivo.deportes.Posicion;
import clubdeportivo.torneos.BonoDescuento;
import clubdeportivo.torneos.EncuentroTorneo;
import clubdeportivo.torneos.EstadoInscripcionTorneo;
import clubdeportivo.torneos.EstadoTorneo;
import clubdeportivo.torneos.FormatoTorneo;
import clubdeportivo.torneos.InscripcionEquipoTorneo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.InscripcionTorneo;
import clubdeportivo.torneos.ParticipanteTorneo;
import clubdeportivo.torneos.PremioCompetitivo;
import clubdeportivo.torneos.PremioMonetario;
import clubdeportivo.torneos.PremioRecreativo;
import clubdeportivo.torneos.PremioTorneo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.Torneo;
import clubdeportivo.torneos.TorneoConjunto;
import clubdeportivo.torneos.TorneoIndividual;
import clubdeportivo.turnos.DiaSemana;
import clubdeportivo.turnos.Turno;
import tiendaclub.inventario.ArticuloTienda;
import tiendaclub.inventario.Producto;
import tiendaclub.inventario.UbicacionInventario;
import tiendaclub.ventas.descuentos.DescuentoBonoTorneo;
import tiendaclub.ventas.tipos.VentaTienda;
import usuarios.Usuario;
import usuarios.tipos.Empleado;
import usuarios.tipos.EstudianteUniversidad;
import usuarios.tipos.ParticipanteExterno;
import usuarios.tipos.Socio;
import usuarios.tipos.empleados.Administrador;
import usuarios.tipos.empleados.Entrenador;
import utilidades.persistencia.PersistenciaClub;

/*
 * Una clase anidada por historia de usuario (HU-T01 a HU-T15 del documento de diseño).
 * Cada prueba corresponde a un criterio "Dado / Cuando / Entonces".
 */
@DisplayName("Historias de usuario de torneos (Proyecto 2)")
public class Proyecto2IntegracionTest {
    // Datos de los escenarios: los torneos se juegan de 10:00 a 12:00. Como un torneo solo
    // se inicia desde su fecha, se programan para hoy; los de cupos preferenciales, en diez días
    private static final int DIAS_HASTA_TORNEO = 10;
    private static final int CAPACIDAD = 5;
    private static final int CAPACIDAD_HISTORIA_T01 = 12;
    private static final double TARIFA_COMPETITIVA = 50000;
    private static final double PORCENTAJE_RECAUDO = 0.50;
    private static final double PORCENTAJE_BONO = 0.10;
    private static final int DIAS_VIGENCIA_BONO = 30;
    private static final int EDAD_MAYOR = 20;

    private ClubDeportivo club;
    private DeporteIndividual tenis;
    private LocalDate fechaTorneo;
    private LocalDate fechaFutura;
    private Path carpetaTemporal;

    @BeforeEach
    void prepararClub() {
        club = new ClubDeportivo();
        tenis = new DeporteIndividual("Tenis", 20);
        club.registrarDeporteIndividual(tenis);
        fechaTorneo = LocalDate.now();
        fechaFutura = LocalDate.now().plusDays(DIAS_HASTA_TORNEO);
    }

    @AfterEach
    void borrarArchivosTemporales() throws IOException {
        if (carpetaTemporal == null) {
            return;
        }

        File[] archivos = carpetaTemporal.toFile().listFiles();

        if (archivos != null) {
            for (File archivo : archivos) {
                Files.deleteIfExists(archivo.toPath());
            }
        }

        Files.deleteIfExists(carpetaTemporal);
    }

    @Nested
    @DisplayName("HU-T01 (Administrador) Crear un torneo interno")
    class CrearTorneo {

        @Test
        @DisplayName("Con datos válidos el torneo queda registrado en estado ABIERTO")
        void registraTorneoAbierto() {
            TorneoIndividual torneo = crearTorneoIndividual("T01", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS);

            club.registrarTorneo(torneo);

            assertAll("torneo registrado",
                    () -> assertSame(torneo, club.buscarTorneo("T01")),
                    () -> assertEquals(EstadoTorneo.ABIERTO, torneo.getEstado()));
        }

        @Test
        @DisplayName("Con capacidad 12 se reservan 3 cupos preferenciales y 9 regulares")
        void calculaCupos() {
            TorneoIndividual torneo = new TorneoIndividual("T01-C", "Cupos", fechaTorneo,
                    LocalTime.of(10, 0), LocalTime.of(12, 0), FormatoTorneo.TODOS_CONTRA_TODOS,
                    CAPACIDAD_HISTORIA_T01, TipoTorneo.RECREATIVO, fechaLiberacion(),
                    new PremioRecreativo(PORCENTAJE_BONO, DIAS_VIGENCIA_BONO), 0,
                    tenis, Nivel.INTERMEDIO, Modalidad.SENCILLOS);

            // El 20 % de 12 es 2,4 y se redondea hacia arriba
            assertAll("cupos",
                    () -> assertEquals(3, torneo.getCuposPreferenciales()),
                    () -> assertEquals(9, torneo.getCuposRegulares()));
        }

        @Test
        @DisplayName("Un torneo recreativo con tarifa mayor que cero se rechaza")
        void rechazaRecreativoConTarifa() {
            assertThrows(IllegalArgumentException.class,
                    () -> new TorneoIndividual("T01-R", "Recreativo", fechaTorneo,
                            LocalTime.of(10, 0), LocalTime.of(12, 0),
                            FormatoTorneo.TODOS_CONTRA_TODOS, CAPACIDAD, TipoTorneo.RECREATIVO,
                            fechaLiberacion(), new PremioRecreativo(PORCENTAJE_BONO, DIAS_VIGENCIA_BONO),
                            TARIFA_COMPETITIVA, tenis, Nivel.INTERMEDIO, Modalidad.SENCILLOS));
        }

        @Test
        @DisplayName("Un torneo fuera del horario del club se rechaza")
        void rechazaTorneoFueraDelHorario() {
            TorneoIndividual nocturno = new TorneoIndividual("T01-N", "Nocturno", fechaTorneo,
                    club.getHoraCierre(), club.getHoraCierre().plusHours(1),
                    FormatoTorneo.TODOS_CONTRA_TODOS, CAPACIDAD, TipoTorneo.RECREATIVO,
                    fechaLiberacion(), new PremioRecreativo(PORCENTAJE_BONO, DIAS_VIGENCIA_BONO),
                    0, tenis, Nivel.INTERMEDIO, Modalidad.SENCILLOS);

            assertThrows(IllegalArgumentException.class, () -> club.registrarTorneo(nocturno));
            assertNull(club.buscarTorneo("T01-N"));
        }

        @Test
        @DisplayName("Un torneo con fecha pasada se rechaza")
        void rechazaFechaPasada() {
            TorneoIndividual pasado = crearTorneoIndividual("T01-P", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS, fechaTorneo.minusDays(2));

            assertThrows(IllegalArgumentException.class, () -> club.registrarTorneo(pasado));
            assertNull(club.buscarTorneo("T01-P"));
        }

        @Test
        @DisplayName("Un identificador repetido se rechaza")
        void rechazaIdentificadorRepetido() {
            club.registrarTorneo(crearTorneoIndividual("T01-D", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS));

            assertThrows(IllegalArgumentException.class,
                    () -> club.registrarTorneo(crearTorneoIndividual("T01-D", TipoTorneo.RECREATIVO,
                            FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS)));
        }
    }

    @Nested
    @DisplayName("HU-T02 (Usuario autenticado) Consultar torneos por tipo y estado")
    class ConsultarTorneos {
        private TorneoIndividual recreativo;
        private TorneoIndividual competitivo;

        @BeforeEach
        void registrarTorneos() {
            recreativo = crearTorneoIndividual("T02-R", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS);
            competitivo = crearTorneoIndividual("T02-C", TipoTorneo.COMPETITIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS);
            club.registrarTorneo(recreativo);
            club.registrarTorneo(competitivo);
        }

        @Test
        @DisplayName("Al filtrar por COMPETITIVO solo se muestran los competitivos")
        void filtraPorTipo() {
            List<Torneo> resultado = club.consultarTorneos(TipoTorneo.COMPETITIVO, null);

            assertEquals(1, resultado.size());
            assertSame(competitivo, resultado.get(0));
        }

        @Test
        @DisplayName("Sin filtros se muestran todos los torneos")
        void sinFiltrosMuestraTodos() {
            assertEquals(2, club.consultarTorneos(null, null).size());
        }

        @Test
        @DisplayName("Un torneo seleccionado da acceso a inscripciones, encuentros, posiciones y premios")
        void historialDelTorneo() {
            inscribirExterno(recreativo, "T02-P1");
            inscribirExterno(recreativo, "T02-P2");
            club.iniciarTorneo(recreativo);
            club.registrarResultadoEncuentro(recreativo.getEncuentros().get(0), "2-1");
            club.finalizarTorneo(recreativo);

            assertAll("historial",
                    () -> assertEquals(2, recreativo.getInscripciones().size()),
                    () -> assertEquals(1, recreativo.getEncuentros().size()),
                    () -> assertEquals(2, recreativo.getPosiciones().size()),
                    () -> assertEquals(EstadoTorneo.FINALIZADO,
                            club.consultarTorneos(null, EstadoTorneo.FINALIZADO).get(0).getEstado()));
        }
    }

    @Nested
    @DisplayName("HU-T03 (Participante responsable de equipo) Inscribir un equipo en un torneo de conjunto")
    class InscribirEquipo {
        private DeporteConjunto futbol;
        private Posicion delantero;
        private Equipo equipo;
        private Socio capitan;
        private TorneoConjunto torneo;

        @BeforeEach
        void prepararEquipo() {
            futbol = new DeporteConjunto("Fútbol", 2, 3, 1, true);
            delantero = new Posicion("Delantero");
            futbol.agregarPosicion(delantero);
            club.registrarDeporteConjunto(futbol);
            equipo = new Equipo("Séneca", Categoria.MAYORES, CAPACIDAD, futbol);
            club.registrarEquipo(equipo);
            capitan = crearSocio("S-CAPITAN");
            club.inscribirSocioEnEquipo(capitan, equipo, delantero, 10);
            torneo = new TorneoConjunto("T03", "Torneo de fútbol", fechaTorneo,
                    LocalTime.of(10, 0), LocalTime.of(12, 0), FormatoTorneo.TODOS_CONTRA_TODOS,
                    CAPACIDAD, TipoTorneo.RECREATIVO, fechaLiberacion(),
                    new PremioRecreativo(PORCENTAJE_BONO, DIAS_VIGENCIA_BONO), 0, futbol,
                    Categoria.MAYORES);
            club.registrarTorneo(torneo);
        }

        @Test
        @DisplayName("Un equipo activo que cumple deporte, categoría y mínimo queda inscrito")
        void inscribeEquipoCompleto() {
            club.inscribirSocioEnEquipo(crearSocio("S-COMPANERO"), equipo, delantero, 9);

            InscripcionEquipoTorneo inscripcion = club.inscribirEquipoEnTorneo(
                    torneo, equipo, TipoCupo.REGULAR, capitan);

            assertAll("inscripción del equipo",
                    () -> assertSame(equipo, inscripcion.getEquipo()),
                    () -> assertTrue(inscripcion.estaActiva()),
                    () -> assertEquals(1, torneo.cantidadInscripcionesActivas()));
        }

        @Test
        @DisplayName("Un equipo sin el mínimo de jugadores se rechaza indicando cuántos tiene")
        void rechazaEquipoIncompleto() {
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> club.inscribirEquipoEnTorneo(torneo, equipo, TipoCupo.REGULAR, capitan));

            assertTrue(error.getMessage().contains("1 jugadores"));
        }

        @Test
        @DisplayName("Si el equipo ya está inscrito se rechaza la nueva solicitud")
        void rechazaEquipoRepetido() {
            club.inscribirSocioEnEquipo(crearSocio("S-COMPANERO"), equipo, delantero, 9);
            club.inscribirEquipoEnTorneo(torneo, equipo, TipoCupo.REGULAR, capitan);

            assertThrows(IllegalArgumentException.class,
                    () -> club.inscribirEquipoEnTorneo(torneo, equipo, TipoCupo.REGULAR, capitan));
        }

        @Test
        @DisplayName("Sin puntaje histórico se rechaza el cupo preferencial")
        void rechazaPreferencialSinPuntaje() {
            club.inscribirSocioEnEquipo(crearSocio("S-COMPANERO"), equipo, delantero, 9);

            assertThrows(IllegalArgumentException.class,
                    () -> club.inscribirEquipoEnTorneo(torneo, equipo, TipoCupo.PREFERENCIAL, capitan));
        }

        @Test
        @DisplayName("Un socio que no es integrante no puede inscribir al equipo")
        void rechazaSocioAjeno() {
            club.inscribirSocioEnEquipo(crearSocio("S-COMPANERO"), equipo, delantero, 9);
            Socio ajeno = crearSocio("S-AJENO");

            assertThrows(IllegalArgumentException.class,
                    () -> club.inscribirEquipoEnTorneo(torneo, equipo, TipoCupo.REGULAR, ajeno));
            assertEquals(0, torneo.cantidadInscripcionesActivas());
        }
    }

    @Nested
    @DisplayName("HU-T04 (Participante) Inscribirme solo o con pareja en un torneo individual")
    class InscribirIndividual {

        @Test
        @DisplayName("En SENCILLOS se registra una persona válida")
        void inscribeSencillos() {
            TorneoIndividual torneo = registrar(crearTorneoIndividual("T04-S", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS));

            InscripcionIndividualTorneo inscripcion = inscribirExterno(torneo, "T04-P1");

            assertEquals(1, inscripcion.getParticipantes().size());
        }

        @Test
        @DisplayName("En DOBLES se registra la pareja")
        void inscribePareja() {
            TorneoIndividual torneo = registrar(crearTorneoIndividual("T04-D", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.DOBLES));
            ParticipanteExterno primero = crearExterno("T04-P1");
            ParticipanteExterno segundo = crearExterno("T04-P2");

            InscripcionIndividualTorneo inscripcion = club.inscribirParticipantesEnTorneo(torneo,
                    Arrays.<ParticipanteTorneo>asList(primero, segundo), TipoCupo.REGULAR);

            assertAll("pareja inscrita",
                    () -> assertTrue(inscripcion.contieneParticipante(primero)),
                    () -> assertTrue(inscripcion.contieneParticipante(segundo)));
        }

        @Test
        @DisplayName("Si la cantidad no corresponde a la modalidad se rechaza")
        void rechazaCantidadIncorrecta() {
            TorneoIndividual torneo = registrar(crearTorneoIndividual("T04-X", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.DOBLES));

            assertThrows(IllegalArgumentException.class,
                    () -> club.inscribirParticipantesEnTorneo(torneo,
                            lista(crearExterno("T04-SOLO")), TipoCupo.REGULAR));
        }
    }

    @Nested
    @DisplayName("HU-T05 (Participante inscrito) Retirar mi inscripción antes del inicio")
    class RetirarInscripcion {
        private TorneoIndividual torneo;
        private ParticipanteExterno participante;
        private InscripcionIndividualTorneo inscripcion;

        @BeforeEach
        void inscribir() {
            torneo = registrar(crearTorneoIndividual("T05", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS));
            participante = crearExterno("T05-P1");
            inscripcion = club.inscribirParticipantesEnTorneo(torneo, lista(participante),
                    TipoCupo.REGULAR);
        }

        @Test
        @DisplayName("El participante consulta sus inscripciones desde el club")
        void consultaSusInscripciones() {
            List<InscripcionTorneo> inscripciones = club.consultarInscripcionesDe(participante);

            assertEquals(1, inscripciones.size());
            assertSame(inscripcion, inscripciones.get(0));
        }

        @Test
        @DisplayName("Con el torneo ABIERTO la inscripción pasa a RETIRADA y libera el cupo")
        void retiraYLiberaCupo() {
            int cuposAntes = torneo.cantidadCuposDisponibles();

            club.retirarInscripcionTorneo(participante, inscripcion);

            assertAll("retiro",
                    () -> assertEquals(EstadoInscripcionTorneo.RETIRADA, inscripcion.getEstado()),
                    () -> assertEquals(cuposAntes + 1, torneo.cantidadCuposDisponibles()));
        }

        @Test
        @DisplayName("Con el torneo EN_CURSO se rechaza el retiro")
        void rechazaRetiroEnCurso() {
            inscribirExterno(torneo, "T05-P2");
            club.iniciarTorneo(torneo);

            assertThrows(IllegalStateException.class,
                    () -> club.retirarInscripcionTorneo(participante, inscripcion));
        }

        @Test
        @DisplayName("Una inscripción ya retirada no se puede retirar otra vez")
        void rechazaRetiroRepetido() {
            club.retirarInscripcionTorneo(participante, inscripcion);

            assertThrows(IllegalStateException.class,
                    () -> club.retirarInscripcionTorneo(inscripcion));
        }

        @Test
        @DisplayName("Nadie puede retirar una inscripción ajena")
        void rechazaRetiroAjeno() {
            ParticipanteExterno otro = crearExterno("T05-OTRO");

            assertThrows(IllegalArgumentException.class,
                    () -> club.retirarInscripcionTorneo(otro, inscripcion));
            assertTrue(inscripcion.estaActiva());
        }
    }

    @Nested
    @DisplayName("HU-T06 (Administrador) Reservar y liberar cupos preferenciales")
    class CuposPreferenciales {

        @Test
        @DisplayName("El 20 % de la capacidad se separa como cupo preferencial, redondeado hacia arriba")
        void separaCuposPreferenciales() {
            TorneoIndividual torneo = crearTorneoIndividual("T06-C", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS);

            assertEquals((int) Math.ceil(CAPACIDAD * 0.20), torneo.getCuposPreferenciales());
        }

        @Test
        @DisplayName("Un socio con ranking mayor que cero puede tomar el cupo preferencial")
        void socioConRankingTomaPreferencial() {
            TorneoIndividual torneo = registrar(crearTorneoIndividual("T06-S", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS, fechaFutura));
            Socio socio = crearSocioAcademia("S-PREF", 100);

            InscripcionIndividualTorneo inscripcion = club.inscribirParticipantesEnTorneo(torneo,
                    lista(socio), TipoCupo.PREFERENCIAL);

            assertEquals(TipoCupo.PREFERENCIAL, inscripcion.getTipoCupo());
        }

        @Test
        @DisplayName("Al liberarse, los cupos preferenciales sin usar se ocupan como regulares")
        void liberadosCuentanComoRegulares() {
            TorneoIndividual torneo = registrar(crearTorneoIndividual("T06-L", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS, fechaFutura));

            for (int i = 1; i <= torneo.getCuposRegulares(); i++) {
                inscribirExterno(torneo, "T06-R" + i);
            }

            assertThrows(IllegalStateException.class, () -> inscribirExterno(torneo, "T06-EXTRA"));

            club.liberarCuposPreferenciales(torneo);
            inscribirExterno(torneo, "T06-EXTRA2");

            assertEquals(CAPACIDAD, torneo.cantidadInscripcionesActivas());
        }
    }

    @Nested
    @DisplayName("HU-T07 (Empleado) Participar en un torneo individual cuando no esté de turno")
    class EmpleadoEnTorneo {
        private TorneoIndividual torneo;
        private Entrenador entrenador;
        private DiaSemana diaTorneo;

        @BeforeEach
        void prepararTorneo() {
            torneo = registrar(crearTorneoIndividual("T07", TipoTorneo.COMPETITIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS));
            entrenador = new Entrenador("E1", "Entrenador Uno", LocalDate.of(1990, 1, 1),
                    "entrenador", "clave", "COD-E1");
            club.registrarUsuario(entrenador);
            diaTorneo = DiaSemana.values()[fechaTorneo.getDayOfWeek().getValue() - 1];
        }

        @Test
        @DisplayName("Sin turno ese día la inscripción se registra y no paga en un torneo competitivo")
        void inscribeEmpleadoSinTurno() {
            DiaSemana otroDia = DiaSemana.values()[(diaTorneo.ordinal() + 1) % DiaSemana.values().length];
            club.asignarTurno(entrenador, new Turno(otroDia, LocalTime.of(8, 0), LocalTime.of(12, 0)));

            InscripcionIndividualTorneo inscripcion = club.inscribirParticipantesEnTorneo(torneo,
                    lista(entrenador), TipoCupo.REGULAR);

            assertEquals(0, inscripcion.getValorPagado());
        }

        @Test
        @DisplayName("Con turno el día del torneo se rechaza, aunque el turno sea en otro horario")
        void rechazaEmpleadoConTurnoEseDia() {
            club.asignarTurno(entrenador, new Turno(diaTorneo, LocalTime.of(14, 0), LocalTime.of(18, 0)));

            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> club.inscribirParticipantesEnTorneo(torneo, lista(entrenador), TipoCupo.REGULAR));

            assertTrue(error.getMessage().contains("tiene turno"));
        }

        @Test
        @DisplayName("Un administrador no es empleado ni participante: no entra a la consola del empleado")
        void rechazaAdministrador() {
            club.registrarUsuario(new Administrador("A1", "Admin", LocalDate.of(1985, 5, 5),
                    "admin", "clave"));
            Usuario usuario = club.autenticar("admin", "clave");

            assertAll("administrador",
                    () -> assertFalse(usuario instanceof Empleado),
                    () -> assertFalse(usuario instanceof ParticipanteTorneo));
        }
    }

    @Nested
    @DisplayName("HU-T08 (Administrador) Registrar el resultado de un encuentro")
    class RegistrarResultado {

        @Test
        @DisplayName("Con el torneo EN_CURSO y un marcador válido se actualizan las posiciones")
        void actualizaPosiciones() {
            TorneoIndividual torneo = prepararTorneoConDosExternos("T08", TipoTorneo.RECREATIVO);
            club.iniciarTorneo(torneo);
            EncuentroTorneo encuentro = torneo.getEncuentros().get(0);

            club.registrarResultadoEncuentro(encuentro, "2-1");

            assertAll("posiciones",
                    () -> assertTrue(encuentro.estaFinalizado()),
                    () -> assertEquals(Torneo.PUNTOS_VICTORIA_POR_DEFECTO,
                            torneo.getPosiciones().get(0).getPuntos()),
                    () -> assertEquals(1, torneo.getPosiciones().get(0).getDiferencia()));
        }

        @Test
        @DisplayName("En eliminación directa se rechaza un empate")
        void rechazaEmpate() {
            TorneoIndividual torneo = prepararTorneoConDosExternos("T08-E", TipoTorneo.RECREATIVO,
                    FormatoTorneo.ELIMINACION_DIRECTA);
            club.iniciarTorneo(torneo);

            assertThrows(IllegalArgumentException.class,
                    () -> club.registrarResultadoEncuentro(torneo.getEncuentros().get(0), "1-1"));
        }

        @Test
        @DisplayName("Un encuentro que no pertenece a un torneo del club se rechaza")
        void rechazaEncuentroAjeno() {
            TorneoIndividual ajeno = crearTorneoIndividual("T08-AJENO", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS);
            ajeno.inscribirParticipantes(lista(new ParticipanteExterno("X1", "X1",
                    LocalDate.of(2000, 1, 1), "x1", "clave", "x1")), TipoCupo.REGULAR);
            ajeno.inscribirParticipantes(lista(new ParticipanteExterno("X2", "X2",
                    LocalDate.of(2000, 1, 1), "x2", "clave", "x2")), TipoCupo.REGULAR);
            ajeno.iniciar();

            assertThrows(IllegalArgumentException.class,
                    () -> club.registrarResultadoEncuentro(ajeno.getEncuentros().get(0), "1-0"));
        }
    }

    @Nested
    @DisplayName("HU-T09 (Usuario autenticado) Consultar el estado y el historial de un torneo")
    class HistorialTorneo {
        private TorneoIndividual torneo;

        @BeforeEach
        void prepararTorneo() {
            torneo = prepararTorneoConDosExternos("T09", TipoTorneo.COMPETITIVO);
        }

        @Test
        @DisplayName("ABIERTO: se muestran las inscripciones actuales")
        void abiertoMuestraInscripciones() {
            assertAll("torneo abierto",
                    () -> assertEquals(EstadoTorneo.ABIERTO, torneo.getEstado()),
                    () -> assertEquals(2, torneo.getInscripcionesActivas().size()),
                    () -> assertTrue(torneo.getEncuentros().isEmpty()));
        }

        @Test
        @DisplayName("EN_CURSO: se muestran encuentros y posiciones")
        void enCursoMuestraEncuentros() {
            club.iniciarTorneo(torneo);

            assertAll("torneo en curso",
                    () -> assertEquals(1, torneo.getEncuentros().size()),
                    () -> assertEquals(2, torneo.getPosiciones().size()));
        }

        @Test
        @DisplayName("FINALIZADO: se conserva todo el historial con sus nombres y premios")
        void finalizadoConservaHistorial() {
            club.iniciarTorneo(torneo);
            club.registrarResultadoEncuentro(torneo.getEncuentros().get(0), "1-0");
            club.finalizarTorneo(torneo);
            EncuentroTorneo encuentro = torneo.getEncuentros().get(0);

            assertAll("historial completo",
                    () -> assertEquals("1-0", encuentro.getMarcador()),
                    () -> assertEquals("Participante T09-P1",
                            encuentro.getInscripcionLocal().getDescripcion()),
                    () -> assertEquals(2, torneo.getPosiciones().size()),
                    () -> assertFalse(torneo.getPremiosOtorgados().isEmpty()));
        }
    }

    @Nested
    @DisplayName("HU-T10 (Administrador) Finalizar un torneo y otorgar sus premios")
    class FinalizarTorneo {

        @Test
        @DisplayName("Con todos los encuentros terminados se determina el ganador y se generan premios")
        void determinaGanadorYPremios() {
            TorneoIndividual torneo = prepararTorneoConDosExternos("T10", TipoTorneo.COMPETITIVO);
            club.iniciarTorneo(torneo);
            club.registrarResultadoEncuentro(torneo.getEncuentros().get(0), "0-2");

            List<PremioTorneo> premios = club.finalizarTorneo(torneo);

            assertAll("premios",
                    () -> assertEquals(EstadoTorneo.FINALIZADO, torneo.getEstado()),
                    () -> assertEquals(1, premios.size()),
                    () -> assertSame(torneo.getEncuentros().get(0).getInscripcionVisitante(),
                            premios.get(0).getGanador()));
        }

        @Test
        @DisplayName("Un torneo finalizado no se vuelve a finalizar ni repite los premios")
        void premiosUnaSolaVez() {
            Socio socio = crearSocioAcademia("S-T10", 10);
            TorneoIndividual torneo = registrar(crearTorneoIndividual("T10-U", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS));
            club.inscribirParticipantesEnTorneo(torneo, lista(socio), TipoCupo.REGULAR);
            inscribirExterno(torneo, "T10-U-RIVAL");
            club.iniciarTorneo(torneo);
            EncuentroTorneo encuentro = torneo.getEncuentros().get(0);
            club.registrarResultadoEncuentro(encuentro,
                    encuentro.getInscripcionLocal().perteneceA(socio) ? "2-0" : "0-2");
            club.finalizarTorneo(torneo);

            assertThrows(IllegalStateException.class, () -> club.finalizarTorneo(torneo));
            assertAll("un solo bono",
                    () -> assertEquals(1, torneo.getPremiosOtorgados().size()),
                    () -> assertEquals(1, socio.getBonos().size()));
        }

        @Test
        @DisplayName("Un participante externo que gana un torneo recreativo no recibe bono")
        void externoNoRecibeBono() {
            TorneoIndividual torneo = prepararTorneoConDosExternos("T10-R", TipoTorneo.RECREATIVO);
            club.iniciarTorneo(torneo);
            club.registrarResultadoEncuentro(torneo.getEncuentros().get(0), "2-0");

            assertTrue(club.finalizarTorneo(torneo).isEmpty());
        }

        @Test
        @DisplayName("Con un encuentro pendiente se rechaza la finalización")
        void rechazaConPendientes() {
            TorneoIndividual torneo = prepararTorneoConDosExternos("T10-P", TipoTorneo.RECREATIVO);
            club.iniciarTorneo(torneo);

            assertThrows(IllegalStateException.class, () -> club.finalizarTorneo(torneo));
        }

        @Test
        @DisplayName("En un torneo competitivo el premio sale del recaudo")
        void premioCompetitivoSaleDelRecaudo() {
            TorneoIndividual torneo = prepararTorneoConDosExternos("T10-C", TipoTorneo.COMPETITIVO);
            club.iniciarTorneo(torneo);
            club.registrarResultadoEncuentro(torneo.getEncuentros().get(0), "3-1");
            double recaudo = torneo.calcularRecaudo();

            List<PremioTorneo> premios = club.finalizarTorneo(torneo);

            assertAll("premio en dinero",
                    () -> assertTrue(premios.get(0) instanceof PremioMonetario),
                    () -> assertEquals(recaudo * PORCENTAJE_RECAUDO, premios.get(0).getValor(), 0.001),
                    () -> assertTrue(premios.get(0).fueEntregado()));
        }

        @Test
        @DisplayName("Si gana un empleado no se le entrega dinero")
        void empleadoGanadorNoRecibeDinero() {
            TorneoIndividual torneo = registrar(crearTorneoIndividual("T10-E", TipoTorneo.COMPETITIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS));
            Entrenador entrenador = new Entrenador("E-GANA", "Entrenador", LocalDate.of(1990, 1, 1),
                    "gana", "clave", "COD-GANA");
            club.registrarUsuario(entrenador);
            club.inscribirParticipantesEnTorneo(torneo, lista(entrenador), TipoCupo.REGULAR);
            inscribirExterno(torneo, "T10-E-RIVAL");
            club.iniciarTorneo(torneo);
            EncuentroTorneo encuentro = torneo.getEncuentros().get(0);
            boolean entrenadorEsLocal = encuentro.getInscripcionLocal().perteneceA(entrenador);
            club.registrarResultadoEncuentro(encuentro, entrenadorEsLocal ? "2-0" : "0-2");

            List<PremioTorneo> premios = club.finalizarTorneo(torneo);

            assertFalse(premios.get(0).fueEntregado());
        }
    }

    @Nested
    @DisplayName("HU-T11 (Socio ganador) Usar mi bono recreativo en una compra")
    class UsarBono {
        private static final double PRECIO_CAMISETA = 50000;
        private static final int UNIDADES = 5;

        private Socio ganador;
        private BonoDescuento bono;
        private Producto camiseta;
        private UbicacionInventario tienda;

        @BeforeEach
        void ganarBono() {
            ganador = crearSocioAcademia("S-BONO", 50);
            TorneoIndividual torneo = registrar(crearTorneoIndividual("T11", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS));
            club.inscribirParticipantesEnTorneo(torneo, lista(ganador), TipoCupo.REGULAR);
            inscribirExterno(torneo, "T11-RIVAL");
            club.iniciarTorneo(torneo);
            EncuentroTorneo encuentro = torneo.getEncuentros().get(0);
            boolean socioEsLocal = encuentro.getInscripcionLocal().perteneceA(ganador);
            club.registrarResultadoEncuentro(encuentro, socioEsLocal ? "2-0" : "0-2");
            club.finalizarTorneo(torneo);
            bono = ganador.getBonosDisponibles().get(0);

            camiseta = new ArticuloTienda("Camiseta", PRECIO_CAMISETA, "Ropa");
            tienda = new UbicacionInventario("Tienda");
            club.registrarProducto(camiseta);
            club.registrarUbicacionInventario(tienda);
            club.reabastecerProducto(camiseta, tienda, UNIDADES);
        }

        @Test
        @DisplayName("Con un bono propio y disponible se aplica el descuento y el bono queda usado")
        void aplicaBono() throws Exception {
            VentaTienda venta = new VentaTienda(LocalDate.now(), ganador, new DescuentoBonoTorneo(bono));
            venta.agregarDetalle(camiseta, 1);
            venta.finalizarVenta();

            club.procesarVenta(venta, tienda);

            assertAll("compra con bono",
                    () -> assertFalse(bono.estaDisponible()),
                    () -> assertEquals(PRECIO_CAMISETA * PORCENTAJE_BONO, venta.getDescuento(), 0.001),
                    () -> assertEquals(UNIDADES - 1, club.buscarExistencia(camiseta, tienda).getCantidad()));
        }

        @Test
        @DisplayName("Un bono ya usado se rechaza")
        void rechazaBonoUsado() throws Exception {
            VentaTienda primera = new VentaTienda(LocalDate.now(), ganador, new DescuentoBonoTorneo(bono));
            primera.agregarDetalle(camiseta, 1);
            club.procesarVenta(primera, tienda);

            assertThrows(IllegalArgumentException.class, () -> new DescuentoBonoTorneo(bono));
        }

        @Test
        @DisplayName("El bono de otro socio se rechaza")
        void rechazaBonoAjeno() {
            Socio otro = crearSocio("S-OTRO");
            VentaTienda venta = new VentaTienda(LocalDate.now(), otro, new DescuentoBonoTorneo(bono));
            venta.agregarDetalle(camiseta, 1);

            assertThrows(IllegalArgumentException.class, () -> club.procesarVenta(venta, tienda));
            assertTrue(bono.estaDisponible());
        }
    }

    @Nested
    @DisplayName("HU-T12 (Usuario) Autenticarme con mis credenciales")
    class Autenticar {

        @Test
        @DisplayName("Con credenciales válidas se obtiene el usuario de su rol")
        void credencialesValidas() {
            ParticipanteExterno participante = crearExterno("T12");

            assertSame(participante, club.autenticar("login-T12", "clave"));
        }

        @Test
        @DisplayName("Con credenciales incorrectas no se obtiene ningún usuario")
        void credencialesIncorrectas() {
            crearExterno("T12-X");

            assertNull(club.autenticar("login-T12-X", "incorrecta"));
        }

        @Test
        @DisplayName("Un usuario de otro rol no corresponde al tipo que exige la consola")
        void otroRolSeRechaza() {
            Administrador administrador = new Administrador("A-T12", "Admin", LocalDate.of(1980, 1, 1),
                    "admin-t12", "clave");
            club.registrarUsuario(administrador);
            Usuario autenticado = club.autenticar("admin-t12", "clave");

            assertAll("roles",
                    () -> assertFalse(autenticado instanceof Empleado),
                    () -> assertFalse(autenticado instanceof ParticipanteTorneo));
        }
    }

    @Nested
    @DisplayName("HU-T13 (Administrador) Iniciar un torneo y generar sus encuentros")
    class IniciarTorneo {

        @Test
        @DisplayName("Todos contra todos crea un encuentro por cada pareja de inscritos")
        void todosContraTodos() {
            TorneoIndividual torneo = registrar(crearTorneoIndividual("T13", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS));
            int inscritos = 3;

            for (int i = 1; i <= inscritos; i++) {
                inscribirExterno(torneo, "T13-P" + i);
            }

            club.iniciarTorneo(torneo);

            assertAll("inicio",
                    () -> assertEquals(EstadoTorneo.EN_CURSO, torneo.getEstado()),
                    () -> assertEquals(inscritos * (inscritos - 1) / 2, torneo.getEncuentros().size()));
        }

        @Test
        @DisplayName("Eliminación directa genera la primera ronda")
        void eliminacionDirecta() {
            TorneoIndividual torneo = prepararTorneoConDosExternos("T13-E", TipoTorneo.RECREATIVO,
                    FormatoTorneo.ELIMINACION_DIRECTA);

            club.iniciarTorneo(torneo);

            assertEquals(1, torneo.getEncuentros().get(0).getRonda());
        }

        @Test
        @DisplayName("Un torneo no se puede iniciar antes de su fecha")
        void rechazaInicioAnticipado() {
            TorneoIndividual torneo = registrar(crearTorneoIndividual("T13-F", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS, fechaFutura));
            inscribirExterno(torneo, "T13-F1");
            inscribirExterno(torneo, "T13-F2");

            assertThrows(IllegalStateException.class, () -> club.iniciarTorneo(torneo));
            assertEquals(EstadoTorneo.ABIERTO, torneo.getEstado());
        }

        @Test
        @DisplayName("Con tres inscritos los encuentros quedan en horas distintas del día del torneo")
        void encuentrosSinCruces() {
            TorneoIndividual torneo = registrar(crearTorneoIndividual("T13-H", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS));

            for (int i = 1; i <= 3; i++) {
                inscribirExterno(torneo, "T13-H" + i);
            }

            club.iniciarTorneo(torneo);
            List<EncuentroTorneo> encuentros = torneo.getEncuentros();

            // Con tres inscritos cada uno juega dos encuentros: ninguno puede coincidir en hora
            assertAll("horarios",
                    () -> assertEquals(3, encuentros.size()),
                    () -> assertFalse(encuentros.get(0).getHoraInicio().equals(encuentros.get(1).getHoraInicio())),
                    () -> assertFalse(encuentros.get(1).getHoraInicio().equals(encuentros.get(2).getHoraInicio())),
                    () -> assertFalse(encuentros.get(0).getHoraInicio().equals(encuentros.get(2).getHoraInicio())),
                    () -> assertEquals(fechaTorneo, encuentros.get(2).getFecha()));
        }

        @Test
        @DisplayName("Con una sola inscripción se rechaza el inicio")
        void rechazaUnaInscripcion() {
            TorneoIndividual torneo = registrar(crearTorneoIndividual("T13-U", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS));
            inscribirExterno(torneo, "T13-SOLO");

            assertThrows(IllegalStateException.class, () -> club.iniciarTorneo(torneo));
        }
    }

    @Nested
    @DisplayName("HU-T14 (Administrador) Registrar estudiantes y participantes externos")
    class RegistrarEstudiantesYExternos {

        @Test
        @DisplayName("Un estudiante de la institución del club puede participar")
        void estudianteDeLaInstitucion() {
            TorneoIndividual torneo = registrar(crearTorneoIndividual("T14", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS));
            EstudianteUniversidad estudiante = crearEstudiante("EST-1", club.getInstitucion());

            InscripcionIndividualTorneo inscripcion = club.inscribirParticipantesEnTorneo(torneo,
                    lista(estudiante), TipoCupo.REGULAR);

            assertTrue(inscripcion.contieneParticipante(estudiante));
        }

        @Test
        @DisplayName("Un estudiante de otra institución no puede inscribirse")
        void estudianteDeOtraInstitucion() {
            TorneoIndividual torneo = registrar(crearTorneoIndividual("T14-O", TipoTorneo.RECREATIVO,
                    FormatoTorneo.TODOS_CONTRA_TODOS, Modalidad.SENCILLOS));
            EstudianteUniversidad estudiante = crearEstudiante("EST-2", "Otra Universidad");

            assertThrows(IllegalArgumentException.class,
                    () -> club.inscribirParticipantesEnTorneo(torneo, lista(estudiante), TipoCupo.REGULAR));
        }

        @Test
        @DisplayName("Un participante externo queda registrado con su contacto")
        void registraExterno() {
            ParticipanteExterno externo = crearExterno("EXT-1");

            assertAll("externo",
                    () -> assertSame(externo, club.buscarUsuario("EXT-1")),
                    () -> assertEquals("contacto-EXT-1", externo.getContacto()));
        }

        @Test
        @DisplayName("Un identificador o un login repetido se rechaza")
        void rechazaDuplicados() {
            crearExterno("EXT-DUP");

            assertAll("duplicados",
                    () -> assertThrows(IllegalArgumentException.class, () -> crearExterno("EXT-DUP")),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> club.registrarUsuario(new ParticipanteExterno("OTRO-ID", "Otro",
                                    LocalDate.of(2000, 1, 1), "login-EXT-DUP", "clave", "c"))));
        }
    }

    @Nested
    @DisplayName("HU-T15 (Usuario) Conservar los datos entre ejecuciones")
    class ConservarDatos {

        @Test
        @DisplayName("Si los archivos existen la persistencia reconstruye el club")
        void reconstruyeElClub() throws IOException {
            carpetaTemporal = Files.createTempDirectory("hu-t15-");
            TorneoIndividual torneo = prepararTorneoConDosExternos("T15", TipoTorneo.RECREATIVO);
            club.iniciarTorneo(torneo);
            club.registrarResultadoEncuentro(torneo.getEncuentros().get(0), "2-1");
            PersistenciaClub persistencia = new PersistenciaClub(carpetaTemporal);
            persistencia.guardar(club);

            ClubDeportivo cargado = new ClubDeportivo();
            persistencia.cargar(cargado);
            Torneo leido = cargado.buscarTorneo("T15");

            assertAll("club reconstruido",
                    () -> assertNotNull(cargado.buscarUsuario("T15-P1")),
                    () -> assertEquals(EstadoTorneo.EN_CURSO, leido.getEstado()),
                    () -> assertEquals("2-1", leido.getEncuentros().get(0).getMarcador()),
                    () -> assertEquals(1, leido.getPosiciones().get(0).getDiferencia()));
        }

        @Test
        @DisplayName("Si un archivo no existe su colección inicia vacía")
        void archivoInexistente() throws IOException {
            carpetaTemporal = Files.createTempDirectory("hu-t15-vacia-");
            ClubDeportivo vacio = new ClubDeportivo();

            new PersistenciaClub(carpetaTemporal).cargar(vacio);

            assertAll("club vacío",
                    () -> assertTrue(vacio.getUsuarios().isEmpty()),
                    () -> assertTrue(vacio.getTorneos().isEmpty()));
        }

        @Test
        @DisplayName("Al guardar se escriben los mismos archivos de la carpeta de datos")
        void guardaEnLosMismosArchivos() throws IOException {
            carpetaTemporal = Files.createTempDirectory("hu-t15-guardar-");
            crearExterno("T15-G");
            PersistenciaClub persistencia = new PersistenciaClub(carpetaTemporal);

            persistencia.guardar(club);
            persistencia.guardar(club);

            assertTrue(Files.exists(carpetaTemporal.resolve("participantes_externos.txt")));
            assertEquals(1, Files.readAllLines(carpetaTemporal.resolve("participantes_externos.txt")).size());
        }
    }

    // Utilidades de los escenarios

    private TorneoIndividual crearTorneoIndividual(String id, TipoTorneo tipo,
            FormatoTorneo formato, Modalidad modalidad) {

        return crearTorneoIndividual(id, tipo, formato, modalidad, fechaTorneo);
    }

    private TorneoIndividual crearTorneoIndividual(String id, TipoTorneo tipo,
            FormatoTorneo formato, Modalidad modalidad, LocalDate fecha) {

        return new TorneoIndividual(id, "Torneo " + id, fecha,
                LocalTime.of(10, 0), LocalTime.of(12, 0), formato, CAPACIDAD, tipo,
                LocalDateTime.of(fecha.minusDays(2), LocalTime.of(8, 0)),
                tipo == TipoTorneo.RECREATIVO
                        ? new PremioRecreativo(PORCENTAJE_BONO, DIAS_VIGENCIA_BONO)
                        : new PremioCompetitivo(PORCENTAJE_RECAUDO),
                tipo == TipoTorneo.RECREATIVO ? 0 : TARIFA_COMPETITIVA,
                tenis, Nivel.INTERMEDIO, modalidad);
    }

    private TorneoIndividual registrar(TorneoIndividual torneo) {
        club.registrarTorneo(torneo);
        return torneo;
    }

    private TorneoIndividual prepararTorneoConDosExternos(String id, TipoTorneo tipo) {
        return prepararTorneoConDosExternos(id, tipo, FormatoTorneo.TODOS_CONTRA_TODOS);
    }

    private TorneoIndividual prepararTorneoConDosExternos(String id, TipoTorneo tipo,
            FormatoTorneo formato) {

        TorneoIndividual torneo = registrar(crearTorneoIndividual(id, tipo, formato, Modalidad.SENCILLOS));
        inscribirExterno(torneo, id + "-P1");
        inscribirExterno(torneo, id + "-P2");
        return torneo;
    }

    private InscripcionIndividualTorneo inscribirExterno(TorneoIndividual torneo, String id) {
        return club.inscribirParticipantesEnTorneo(torneo, lista(crearExterno(id)), TipoCupo.REGULAR);
    }

    private ParticipanteExterno crearExterno(String id) {
        ParticipanteExterno participante = new ParticipanteExterno(id, "Participante " + id,
                LocalDate.of(2000, 1, 1), "login-" + id, "clave", "contacto-" + id);
        club.registrarUsuario(participante);
        return participante;
    }

    private EstudianteUniversidad crearEstudiante(String id, String universidad) {
        EstudianteUniversidad estudiante = new EstudianteUniversidad(id, "Estudiante " + id,
                LocalDate.of(2003, 3, 3), "login-" + id, "clave", universidad, id + "@uniandes.edu.co");
        club.registrarUsuario(estudiante);
        return estudiante;
    }

    private Socio crearSocio(String id) {
        Socio socio = new Socio(id, "Socio " + id, LocalDate.now().minusYears(EDAD_MAYOR),
                "login-" + id, "clave");
        club.registrarUsuario(socio);
        return socio;
    }

    private Socio crearSocioAcademia(String id, double puntaje) {
        Socio socio = crearSocio(id);
        InscripcionAcademia inscripcion = club.inscribirSocioEnAcademia(socio, tenis, Nivel.INTERMEDIO);
        inscripcion.actualizarPuntaje(puntaje);
        return socio;
    }

    private List<ParticipanteTorneo> lista(ParticipanteTorneo participante) {
        List<ParticipanteTorneo> participantes = new ArrayList<ParticipanteTorneo>();
        participantes.add(participante);
        return participantes;
    }

    private LocalDateTime fechaLiberacion() {
        return LocalDateTime.of(fechaTorneo.minusDays(2), LocalTime.of(8, 0));
    }
}
