package consola;

import static consola.InputHandler.readBoolean;
import static consola.InputHandler.readDouble;
import static consola.InputHandler.readEnum;
import static consola.InputHandler.readEnumOpcional;
import static consola.InputHandler.readString;
import static consola.InputHandler.readTextoLibre;
import static consola.InputHandler.readTime;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import clubdeportivo.ClubDeportivo;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.entrenamientos.EstadoAsistencia;
import clubdeportivo.entrenamientos.RegistroAsistencia;
import clubdeportivo.entrenamientos.SesionEntrenamiento;
import clubdeportivo.torneos.EstadoTorneo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.InscripcionTorneo;
import clubdeportivo.torneos.ParticipanteTorneo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.Torneo;
import clubdeportivo.torneos.TorneoIndividual;
import clubdeportivo.turnos.DiaSemana;
import clubdeportivo.turnos.SolicitudCambioTurno;
import clubdeportivo.turnos.Turno;
import tiendaclub.inventario.SugerenciaProducto;
import tiendaclub.ventas.descuentos.DescuentoEmpleado;
import usuarios.Usuario;
import usuarios.tipos.Empleado;
import usuarios.tipos.Socio;
import usuarios.tipos.empleados.Entrenador;
import utilidades.excepciones.InventarioInsuficienteException;

public class ConsolaEmpleado extends ConsolaBase {
    private final Empleado empleado;

    public ConsolaEmpleado(ClubDeportivo club, Empleado empleado) {
        super(club);

        if (empleado == null) {
            throw new IllegalArgumentException("El empleado es obligatorio");
        }

        this.empleado = empleado;
    }

    @Override
    protected void mostrarMenu() {
        System.out.println();
        System.out.println("--- CONSOLA DEL EMPLEADO ---");
        System.out.println("Empleado: " + empleado.getNombre());
        System.out.println("1. Consultar torneos individuales");
        System.out.println("2. Inscribirse en torneo");
        System.out.println("3. Consultar mis inscripciones");
        System.out.println("4. Retirar inscripción");
        System.out.println("5. Consultar mis turnos");
        System.out.println("6. Solicitar cambio de turno");
        System.out.println("7. Sugerir producto");
        System.out.println("8. Consultar mis solicitudes de cambio");
        System.out.println("9. Comprar en tienda");
        System.out.println("10. Comprar en cafetería");

        if (empleado instanceof Entrenador) {
            System.out.println("11. Consultar mis sesiones de entrenamiento");
            System.out.println("12. Registrar asistencia y evaluación");
        }

        System.out.println("0. Salir");
    }

    @Override
    protected boolean ejecutarOpcion(int opcion) throws InventarioInsuficienteException {
        switch (opcion) {
            case 1:
                consultarTorneos();
                return true;
            case 2:
                inscribirseEnTorneo();
                return true;
            case 3:
                consultarInscripciones();
                return true;
            case 4:
                retirarInscripcion();
                return true;
            case 5:
                consultarTurnos();
                return true;
            case 6:
                solicitarCambioTurno();
                return true;
            case 7:
                registrarSugerencia();
                return true;
            case 8:
                consultarSolicitudesCambio();
                return true;
            case 9:
                registrarCompra(empleado, false, new DescuentoEmpleado());
                return true;
            case 10:
                registrarCompra(empleado, true, new DescuentoEmpleado());
                return true;
            case 11:
                if (!(empleado instanceof Entrenador)) {
                    return false;
                }

                consultarMisSesiones();
                return true;
            case 12:
                if (!(empleado instanceof Entrenador)) {
                    return false;
                }

                registrarAsistencia();
                return true;
            default:
                return false;
        }
    }

    private void consultarTorneos() {
        titulo("TORNEOS INDIVIDUALES");

        TipoTorneo tipo = readEnumOpcional("Tipo de torneo:", TipoTorneo.values());
        EstadoTorneo estado = readEnumOpcional("Estado del torneo:", EstadoTorneo.values());
        boolean encontrado = false;

        for (Torneo torneo : club.consultarTorneos(tipo, estado)) {
            if (torneo instanceof TorneoIndividual) {
                TorneoIndividual individual = (TorneoIndividual) torneo;
                String motivo = individual.motivoRechazo(empleado);

                mostrarTorneo(individual);
                System.out.println("¿Puede participar?: "
                        + (motivo == null ? "Sí" : "No, " + motivo));
                System.out.println("Valor de inscripción: "
                        + dinero(individual.calcularValorInscripcion(empleado)));
                mostrarHistorialTorneo(individual);
                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("No hay torneos individuales que coincidan con los filtros.");
        }
    }

    private void inscribirseEnTorneo() {
        List<Torneo> abiertos = new ArrayList<Torneo>();

        // Los empleados solo participan en torneos individuales
        for (Torneo torneo : club.consultarTorneos(null, EstadoTorneo.ABIERTO)) {
            if (torneo instanceof TorneoIndividual) {
                abiertos.add(torneo);
            }
        }

        Torneo seleccionado = seleccionarTorneo(abiertos);

        if (seleccionado == null) {
            return;
        }

        TorneoIndividual torneo = (TorneoIndividual) seleccionado;
        List<ParticipanteTorneo> participantes = new ArrayList<ParticipanteTorneo>();
        participantes.add(empleado);

        if (torneo.getModalidad() == Modalidad.DOBLES) {
            Usuario pareja = club.buscarUsuario(readString("ID de la pareja: "));

            if (!(pareja instanceof ParticipanteTorneo)) {
                System.out.println("No existe un participante con ese ID.");
                return;
            }

            participantes.add((ParticipanteTorneo) pareja);
        }

        TipoCupo tipoCupo = seleccionarTipoCupo();
        InscripcionIndividualTorneo inscripcion = club.inscribirParticipantesEnTorneo(
                torneo, participantes, tipoCupo);

        System.out.println("Inscripción registrada correctamente.");
        System.out.println("Valor pagado: " + dinero(inscripcion.getValorPagado()));
    }

    private void consultarInscripciones() {
        List<InscripcionTorneo> inscripciones = club.consultarInscripcionesDe(empleado);

        if (inscripciones.isEmpty()) {
            System.out.println("No tienes inscripciones en torneos.");
            return;
        }

        titulo("MIS INSCRIPCIONES");
        mostrarInscripciones(inscripciones);
    }

    private void retirarInscripcion() {
        List<InscripcionTorneo> activas = new ArrayList<InscripcionTorneo>();

        for (InscripcionTorneo inscripcion : club.consultarInscripcionesDe(empleado)) {
            if (inscripcion.estaActiva()
                    && inscripcion.getTorneo().getEstado() == EstadoTorneo.ABIERTO) {
                activas.add(inscripcion);
            }
        }

        if (activas.isEmpty()) {
            System.out.println("No tienes inscripciones activas en torneos abiertos.");
            return;
        }

        mostrarInscripciones(activas);
        InscripcionTorneo inscripcion = elegirDeLista(activas, "Número de la inscripción");

        if (inscripcion != null) {
            club.retirarInscripcionTorneo(empleado, inscripcion);
            System.out.println("Inscripción retirada correctamente.");
        }
    }

    private void consultarTurnos() {
        titulo("MIS TURNOS");

        if (empleado.getTurnos().isEmpty()) {
            System.out.println("No tienes turnos asignados.");
            return;
        }

        int numero = 1;

        for (Turno turno : empleado.getTurnos()) {
            System.out.println(numero + ". " + turno);
            numero++;
        }
    }

    private void solicitarCambioTurno() {
        if (empleado.getTurnos().isEmpty()) {
            System.out.println("No tienes turnos asignados.");
            return;
        }

        consultarTurnos();
        Turno turnoOriginal = elegirDeLista(empleado.getTurnos(), "Número del turno que desea cambiar");

        if (turnoOriginal == null) {
            return;
        }

        DiaSemana nuevoDia = readEnum("Nuevo día:", DiaSemana.values());
        LocalTime nuevaHoraInicio = readTime("Nueva hora de inicio (HH:MM): ");
        LocalTime nuevaHoraFin = readTime("Nueva hora de finalización (HH:MM): ");

        SolicitudCambioTurno solicitud = club.solicitarCambioTurno(
                empleado, turnoOriginal, nuevoDia, nuevaHoraInicio, nuevaHoraFin);

        System.out.println("Solicitud registrada correctamente.");
        System.out.println("Estado: " + solicitud.getEstado());
    }

    private void registrarSugerencia() {
        String nombre = readString("Nombre del producto sugerido: ");
        String descripcion = readTextoLibre("Descripción del producto: ");

        club.registrarSugerencia(new SugerenciaProducto(nombre, descripcion, empleado));
        System.out.println("Sugerencia registrada correctamente.");
    }

    private void consultarSolicitudesCambio() {
        List<SolicitudCambioTurno> solicitudes = empleado.getSolicitudesCambio();

        if (solicitudes.isEmpty()) {
            System.out.println("No tienes solicitudes de cambio registradas.");
            return;
        }

        titulo("MIS SOLICITUDES DE CAMBIO");
        int numero = 1;

        for (SolicitudCambioTurno solicitud : solicitudes) {
            System.out.println(numero + ". " + solicitud);
            numero++;
        }
    }

    private void consultarMisSesiones() {
        List<SesionEntrenamiento> sesiones = club.consultarSesionesDe((Entrenador) empleado);

        if (sesiones.isEmpty()) {
            System.out.println("No tienes sesiones de entrenamiento programadas.");
            return;
        }

        titulo("MIS SESIONES");
        mostrarSesiones(sesiones, true);
    }

    // El entrenador marca la asistencia de cada socio y, si asistió, puede calificarlo de 1 a 5
    private void registrarAsistencia() {
        Entrenador entrenador = (Entrenador) empleado;
        List<SesionEntrenamiento> sesiones = club.consultarSesionesDe(entrenador);

        if (sesiones.isEmpty()) {
            System.out.println("No tienes sesiones de entrenamiento programadas.");
            return;
        }

        titulo("REGISTRAR ASISTENCIA");
        mostrarSesiones(sesiones, false);
        SesionEntrenamiento sesion = elegirDeLista(sesiones, "Número de la sesión");

        if (sesion == null) {
            return;
        }

        List<Socio> socios = sesion.getSociosHabilitados();

        if (socios.isEmpty()) {
            System.out.println("La sesión no tiene socios habilitados para asistir.");
            return;
        }

        boolean continuar = true;

        while (continuar) {
            System.out.println("Socios de la sesión:");

            for (int i = 0; i < socios.size(); i++) {
                RegistroAsistencia registro = sesion.buscarRegistro(socios.get(i));

                System.out.println((i + 1) + ". " + socios.get(i).getNombre()
                        + (registro == null ? " | Sin registro" : " | " + registro));
            }

            Socio socio = elegirDeLista(socios, "Número del socio");

            if (socio == null) {
                return;
            }

            EstadoAsistencia estado = readEnum("Asistencia:", new EstadoAsistencia[] {
                    EstadoAsistencia.ASISTIO, EstadoAsistencia.TARDE, EstadoAsistencia.AUSENTE });
            Double nota = null;
            String comentario = null;

            if (estado != EstadoAsistencia.AUSENTE
                    && readBoolean("¿Desea evaluar su desempeño? (s/n): ")) {

                nota = readDouble("Nota de 1 a 5: ");
                comentario = readTextoLibre("Comentario: ");
            }

            try {
                RegistroAsistencia registro = club.registrarAsistenciaSesion(
                        entrenador, sesion, socio, estado, nota, comentario);
                System.out.println("Registrado: " + registro);
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("No fue posible registrar: " + e.getMessage());
            }

            continuar = readBoolean("¿Desea registrar otro socio? (s/n): ");
        }
    }
}
