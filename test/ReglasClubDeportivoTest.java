import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import clubdeportivo.deportes.Categoria;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.DeporteIndividual;
import clubdeportivo.deportes.Equipo;
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

public class ReglasClubDeportivoTest {
        @Test
        void categoriasRespetanLimitesDeEdad() {
                assertTrue(Categoria.SUB_13.admiteEdad(12));
                assertFalse(Categoria.SUB_13.admiteEdad(13));
                assertTrue(Categoria.SUB_15.admiteEdad(13));
                assertTrue(Categoria.SUB_15.admiteEdad(14));
                assertTrue(Categoria.SUB_17.admiteEdad(15));
                assertTrue(Categoria.SUB_17.admiteEdad(16));
                assertTrue(Categoria.MAYORES.admiteEdad(17));
                assertFalse(Categoria.MAYORES.admiteEdad(16));
                assertFalse(Categoria.SUB_13.admiteEdad(-1));
        }

        @Test
        void clubRechazaSocioDeEdadIncorrectaParaEquipo() {
                ClubDeportivo club = new ClubDeportivo();
                DeporteConjunto futbol = new DeporteConjunto(
                                "Fútbol", 1, 3, 1, true);
                Posicion posicion = new Posicion("Delantero");
                futbol.agregarPosicion(posicion);
                club.registrarDeporteConjunto(futbol);
                Equipo equipo = new Equipo(
                                "Sub 13", Categoria.SUB_13, 10, futbol);
                club.registrarEquipo(equipo);
                Socio socio = socio("S1", 17);
                club.registrarUsuario(socio);
                assertThrows(IllegalArgumentException.class,
                                () -> club.inscribirSocioEnEquipo(
                                                socio, equipo, posicion, 10));
        }

        @Test
        void clubAceptaSocioDeEdadCorrectaParaEquipo() {
                ClubDeportivo club = new ClubDeportivo();
                DeporteConjunto futbol = new DeporteConjunto(
                                "Fútbol", 1, 3, 1, true);
                Posicion posicion = new Posicion("Delantero");
                futbol.agregarPosicion(posicion);
                club.registrarDeporteConjunto(futbol);
                Equipo equipo = new Equipo(
                                "Mayores", Categoria.MAYORES, 10, futbol);
                club.registrarEquipo(equipo);
                Socio socio = socio("S1", 20);
                club.registrarUsuario(socio);
                assertEquals(socio, club.inscribirSocioEnEquipo(
                                socio, equipo, posicion, 10).getSocio());
        }

        @Test
        void noProgramaSesionSiEntrenadorNoTieneTurno() {
                DatosSesion datos = prepararSesion();
                assertThrows(IllegalStateException.class,
                                () -> datos.club.programarSesion(datos.sesion));
        }

        @Test
        void noProgramaSesionSiTurnoNoCubreHoraFinal() {
                DatosSesion datos = prepararSesion();
                datos.club.asignarTurno(datos.entrenador,
                                new Turno(datos.dia, LocalTime.of(9, 0),
                                                LocalTime.of(10, 30)));
                assertThrows(IllegalStateException.class,
                                () -> datos.club.programarSesion(datos.sesion));
        }

        @Test
        void programaSesionCuandoTurnoCubreTodoElHorario() {
                DatosSesion datos = prepararSesion();
                datos.club.asignarTurno(datos.entrenador,
                                new Turno(datos.dia, LocalTime.of(9, 0),
                                                LocalTime.of(12, 0)));
                datos.club.programarSesion(datos.sesion);
                assertEquals(1, datos.club.getSesionesEntrenamiento().size());
        }

        @Test
        void rechazaCruceDeSesionesDelMismoEntrenador() {
                DatosSesion datos = prepararSesion();
                datos.club.asignarTurno(datos.entrenador,
                                new Turno(datos.dia, LocalTime.of(9, 0),
                                                LocalTime.of(13, 0)));
                datos.club.programarSesion(datos.sesion);
                Instalacion segunda = new Instalacion(
                                "I2", TipoInstalacion.CANCHA_TENIS,
                                4, true, datos.deporte);
                datos.club.registrarInstalacion(segunda);
                SesionEntrenamiento otra = new SesionEntrenamiento(
                                datos.fecha, LocalTime.of(10, 30), 60,
                                datos.entrenador, segunda, TipoSesion.INDIVIDUAL);
                otra.asignarDeporteIndividual(datos.deporte);
                assertThrows(IllegalStateException.class,
                                () -> datos.club.programarSesion(otra));
        }

        @Test
        void soloEvaluaSocioQueAsistioOLlegoTarde() {
                DatosSesion datos = prepararSesion();
                Socio socio = socio("S1", 20);
                RegistroAsistencia registro = new RegistroAsistencia(
                                datos.sesion, socio, EstadoAsistencia.AUSENTE);
                assertThrows(IllegalStateException.class,
                                () -> registro.registrarEvaluacion(4.5, "Buen trabajo"));
                registro.registrarAsistencia(EstadoAsistencia.TARDE);
                registro.registrarEvaluacion(4.5, "Buen trabajo");
                assertEquals(4.5, registro.getNotaDesempeno(), 0.001);
        }

        @Test
        void evaluacionDebeEstarEntreUnoYCinco() {
                DatosSesion datos = prepararSesion();
                RegistroAsistencia registro = new RegistroAsistencia(
                                datos.sesion, socio("S1", 20),
                                EstadoAsistencia.ASISTIO);
                assertThrows(IllegalArgumentException.class,
                                () -> registro.registrarEvaluacion(6.0, "Nota inválida"));
        }

        private DatosSesion prepararSesion() {
                ClubDeportivo club = new ClubDeportivo();
                DeporteIndividual deporte = new DeporteIndividual("Tenis", 20);
                club.registrarDeporteIndividual(deporte);
                Entrenador entrenador = new Entrenador(
                                "E1", "Entrenador", LocalDate.of(1990, 1, 1),
                                "entrenador", "clave", "COD-E1");
                club.registrarUsuario(entrenador);
                Instalacion instalacion = new Instalacion(
                                "I1", TipoInstalacion.CANCHA_TENIS,
                                4, true, deporte);
                club.registrarInstalacion(instalacion);
                LocalDate fecha = LocalDate.now().plusDays(7);
                SesionEntrenamiento sesion = new SesionEntrenamiento(
                                fecha, LocalTime.of(10, 0), 60,
                                entrenador, instalacion, TipoSesion.INDIVIDUAL);
                sesion.asignarDeporteIndividual(deporte);
                DiaSemana dia = DiaSemana.values()[fecha.getDayOfWeek().getValue() - 1];
                return new DatosSesion(club, deporte, entrenador,
                                fecha, dia, sesion);
        }

        private Socio socio(String id, int edad) {
                return new Socio(id, "Socio " + id,
                                LocalDate.now().minusYears(edad),
                                "login-" + id, "clave");
        }

        private static class DatosSesion {
                private final ClubDeportivo club;
                private final DeporteIndividual deporte;
                private final Entrenador entrenador;
                private final LocalDate fecha;
                private final DiaSemana dia;
                private final SesionEntrenamiento sesion;

                DatosSesion(ClubDeportivo club, DeporteIndividual deporte,
                                Entrenador entrenador, LocalDate fecha,
                                DiaSemana dia, SesionEntrenamiento sesion) {
                        this.club = club;
                        this.deporte = deporte;
                        this.entrenador = entrenador;
                        this.fecha = fecha;
                        this.dia = dia;
                        this.sesion = sesion;
                }
        }
}
