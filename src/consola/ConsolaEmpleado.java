package consola;

import static consola.InputHandler.readInt;
import static consola.InputHandler.readString;
import static consola.InputHandler.readTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import clubdeportivo.ClubDeportivo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.InscripcionTorneo;
import clubdeportivo.torneos.ParticipanteTorneo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.Torneo;
import clubdeportivo.torneos.TorneoIndividual;
import clubdeportivo.turnos.DiaSemana;
import clubdeportivo.turnos.SolicitudCambioTurno;
import clubdeportivo.turnos.Turno;
import tiendaclub.inventario.SugerenciaProducto;
import usuarios.Usuario;
import usuarios.tipos.Empleado;

public class ConsolaEmpleado implements Subconsola {
    private final ClubDeportivo club;
    private final Empleado empleado;

    public ConsolaEmpleado(ClubDeportivo club, Empleado empleado) {
        if (club == null || empleado == null) {
            throw new IllegalArgumentException("El club y el empleado son obligatorios");
        }

        this.club = club;
        this.empleado = empleado;
    }

    @Override
    public void ejecutar() {
        boolean ejecutando = true;

        while (ejecutando) {
            mostrarMenu();
            int opcion = readInt("Seleccione una opción: ");

            try {
                switch (opcion) {
                    case 1:
                        consultarTorneos();
                        break;
                    case 2:
                        inscribirseEnTorneo();
                        break;
                    case 3:
                        consultarInscripciones();
                        break;
                    case 4:
                        retirarInscripcion();
                        break;
                    case 5:
                        consultarTurnos();
                        break;
                    case 6:
                        solicitarCambioTurno();
                        break;
                    case 7:
                        registrarSugerencia();
                        break;
                    case 0:
                        ejecutando = false;
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("No fue posible realizar la operación: " + e.getMessage());
            }
        }
    }

    private void mostrarMenu() {
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
        System.out.println("0. Salir");
    }

    private void consultarTorneos() {
        System.out.println();
        System.out.println("--- TORNEOS INDIVIDUALES ---");

        boolean encontrado = false;

        for (Torneo torneo : club.getTorneos()) {
            if (torneo instanceof TorneoIndividual) {
                TorneoIndividual individual = (TorneoIndividual) torneo;

                System.out.println();
                System.out.println("ID: " + individual.getId());
                System.out.println("Nombre: " + individual.getNombre());
                System.out.println("Deporte: " + individual.getDeporte().getNombre());
                System.out.println("Nivel: " + individual.getNivel());
                System.out.println("Modalidad: " + individual.getModalidad());
                System.out.println("Fecha: " + individual.getFechaInicio());
                System.out.println("Horario: " + individual.getHoraInicio()
                        + " - " + individual.getHoraFin());
                System.out.println("Estado: " + individual.getEstado());
                System.out.println("Puede participar: "
                        + (individual.validarParticipante(empleado) ? "Sí" : "No"));
                System.out.println("Valor de inscripción: "
                        + individual.calcularValorInscripcion(empleado));

                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("No hay torneos individuales registrados.");
        }
    }

    private void inscribirseEnTorneo() {
        TorneoIndividual torneo = seleccionarTorneoIndividual();

        if (torneo == null) {
            return;
        }

        List<ParticipanteTorneo> participantes = new ArrayList<ParticipanteTorneo>();
        participantes.add(empleado);

        if (torneo.getModalidad() == clubdeportivo.deportes.Modalidad.DOBLES) {
            String idPareja = readString("ID de la pareja: ");
            Usuario usuarioPareja = club.buscarUsuario(idPareja);

            if (!(usuarioPareja instanceof ParticipanteTorneo)) {
                System.out.println("No existe un participante con ese ID.");
                return;
            }

            participantes.add((ParticipanteTorneo) usuarioPareja);
        }

        TipoCupo tipoCupo = seleccionarTipoCupo();

        InscripcionIndividualTorneo inscripcion = club.inscribirParticipantesEnTorneo(
                torneo, participantes, tipoCupo);

        System.out.println("Inscripción registrada correctamente.");
        System.out.println("Valor pagado: " + inscripcion.getValorPagado());
    }

    private void consultarInscripciones() {
        List<InscripcionTorneo> inscripciones = buscarInscripciones();

        if (inscripciones.isEmpty()) {
            System.out.println("No tienes inscripciones en torneos.");
            return;
        }

        System.out.println();
        System.out.println("--- MIS INSCRIPCIONES ---");

        int numero = 1;

        for (InscripcionTorneo inscripcion : inscripciones) {
            System.out.println(numero + ". Torneo: "
                    + inscripcion.getTorneo().getNombre()
                    + " | Estado: " + inscripcion.getEstado()
                    + " | Cupo: " + inscripcion.getTipoCupo()
                    + " | Valor: " + inscripcion.getValorPagado());

            numero++;
        }
    }

    private void retirarInscripcion() {
        List<InscripcionTorneo> activas = new ArrayList<InscripcionTorneo>();

        for (InscripcionTorneo inscripcion : buscarInscripciones()) {
            if (inscripcion.estaActiva()) {
                activas.add(inscripcion);
            }
        }

        if (activas.isEmpty()) {
            System.out.println("No tienes inscripciones activas.");
            return;
        }

        for (int i = 0; i < activas.size(); i++) {
            System.out.println((i + 1) + ". "
                    + activas.get(i).getTorneo().getNombre());
        }

        int opcion = readInt("Número de la inscripción: ");

        if (opcion < 1 || opcion > activas.size()) {
            System.out.println("La inscripción seleccionada no es válida.");
            return;
        }

        club.retirarInscripcionTorneo(activas.get(opcion - 1));
        System.out.println("Inscripción retirada correctamente.");
    }

    private void consultarTurnos() {
        System.out.println();
        System.out.println("--- MIS TURNOS ---");

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
        int opcion = readInt("Número del turno que desea cambiar: ");

        if (opcion < 1 || opcion > empleado.getTurnos().size()) {
            System.out.println("El turno seleccionado no es válido.");
            return;
        }

        Turno turnoOriginal = empleado.getTurnos().get(opcion - 1);

        System.out.println("Días disponibles:");

        for (DiaSemana dia : DiaSemana.values()) {
            System.out.println("- " + dia);
        }

        DiaSemana nuevoDia = DiaSemana.valueOf(
                readString("Nuevo día: ").toUpperCase());

        LocalTime nuevaHoraInicio = readTime(
                "Nueva hora de inicio (HH:MM): ");

        LocalTime nuevaHoraFin = readTime(
                "Nueva hora de finalización (HH:MM): ");

        SolicitudCambioTurno solicitud = club.solicitarCambioTurno(
                empleado,
                turnoOriginal,
                nuevoDia,
                nuevaHoraInicio,
                nuevaHoraFin);

        System.out.println("Solicitud registrada correctamente.");
        System.out.println("Estado: " + solicitud.getEstado());
    }

    private void registrarSugerencia() {
        String nombre = readString(
                "Nombre del producto sugerido: ");

        String descripcion = readString(
                "Descripción del producto: ");

        SugerenciaProducto sugerencia = new SugerenciaProducto(
                nombre,
                descripcion,
                empleado);

        club.registrarSugerencia(sugerencia);
        System.out.println("Sugerencia registrada correctamente.");
    }

    private TorneoIndividual seleccionarTorneoIndividual() {
        boolean encontrado = false;

        System.out.println("Torneos individuales:");

        for (Torneo torneo : club.getTorneos()) {
            if (torneo instanceof TorneoIndividual) {
                System.out.println("- " + torneo.getId()
                        + " | " + torneo.getNombre()
                        + " | " + torneo.getEstado());

                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("No hay torneos individuales registrados.");
            return null;
        }

        String id = readString("ID del torneo: ");
        Torneo torneo = club.buscarTorneo(id);

        if (!(torneo instanceof TorneoIndividual)) {
            System.out.println("El torneo seleccionado no es individual.");
            return null;
        }

        return (TorneoIndividual) torneo;
    }

    private TipoCupo seleccionarTipoCupo() {
        System.out.println("Tipos de cupo:");

        for (TipoCupo tipoCupo : TipoCupo.values()) {
            System.out.println("- " + tipoCupo);
        }

        return TipoCupo.valueOf(
                readString("Tipo de cupo: ").toUpperCase());
    }

    private List<InscripcionTorneo> buscarInscripciones() {
        List<InscripcionTorneo> resultado = new ArrayList<InscripcionTorneo>();

        for (Torneo torneo : club.getTorneos()) {
            for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
                if (inscripcion instanceof InscripcionIndividualTorneo) {
                    InscripcionIndividualTorneo individual = (InscripcionIndividualTorneo) inscripcion;

                    if (individual.contieneParticipante(empleado)) {
                        resultado.add(inscripcion);
                    }
                }
            }
        }

        return resultado;
    }
}