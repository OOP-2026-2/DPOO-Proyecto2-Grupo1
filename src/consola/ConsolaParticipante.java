package consola;

import static consola.InputHandler.readInt;
import static consola.InputHandler.readString;

import java.util.ArrayList;
import java.util.List;

import clubdeportivo.ClubDeportivo;
import clubdeportivo.deportes.Equipo;
import clubdeportivo.torneos.BonoDescuento;
import clubdeportivo.torneos.InscripcionEquipoTorneo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.InscripcionTorneo;
import clubdeportivo.torneos.ParticipanteTorneo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.Torneo;
import clubdeportivo.torneos.TorneoConjunto;
import clubdeportivo.torneos.TorneoIndividual;
import usuarios.Usuario;
import usuarios.tipos.Socio;

public class ConsolaParticipante implements Subconsola {
    private final ClubDeportivo club;
    private final ParticipanteTorneo participante;

    public ConsolaParticipante(ClubDeportivo club, ParticipanteTorneo participante) {
        if (club == null || participante == null) {
            throw new IllegalArgumentException("El club y el participante son obligatorios");
        }

        this.club = club;
        this.participante = participante;
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
                        inscribirseEnTorneoIndividual();
                        break;
                    case 3:
                        inscribirEquipoEnTorneo();
                        break;
                    case 4:
                        consultarMisInscripciones();
                        break;
                    case 5:
                        retirarInscripcion();
                        break;
                    case 6:
                        consultarBonos();
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
        System.out.println("--- CONSOLA DEL PARTICIPANTE ---");
        System.out.println("Participante: " + participante.getNombre());
        System.out.println("1. Consultar torneos");
        System.out.println("2. Inscribirse en torneo individual");
        System.out.println("3. Inscribir equipo en torneo");
        System.out.println("4. Consultar mis inscripciones");
        System.out.println("5. Retirar inscripción");
        System.out.println("6. Consultar bonos");
        System.out.println("0. Salir");
    }

    private void consultarTorneos() {
        System.out.println();
        System.out.println("--- TORNEOS DISPONIBLES ---");

        if (club.getTorneos().isEmpty()) {
            System.out.println("No hay torneos registrados.");
            return;
        }

        for (Torneo torneo : club.getTorneos()) {
            System.out.println();
            System.out.println("ID: " + torneo.getId());
            System.out.println("Nombre: " + torneo.getNombre());
            System.out.println("Tipo: " + torneo.getTipo());
            System.out.println("Formato: " + torneo.getFormato());
            System.out.println("Fecha: " + torneo.getFechaInicio());
            System.out.println("Horario: " + torneo.getHoraInicio()
                    + " - " + torneo.getHoraFin());
            System.out.println("Estado: " + torneo.getEstado());
            System.out.println("Cupos disponibles: "
                    + torneo.cantidadCuposDisponibles());
            System.out.println("Tarifa: "
                    + torneo.calcularValorInscripcion(participante));
        }
    }

    private void inscribirseEnTorneoIndividual() {
        TorneoIndividual torneo = seleccionarTorneoIndividual();

        if (torneo == null) {
            return;
        }

        List<ParticipanteTorneo> participantes = new ArrayList<ParticipanteTorneo>();
        participantes.add(participante);

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

    private void inscribirEquipoEnTorneo() {
        if (!(participante instanceof Socio)) {
            System.out.println("Solo un socio puede inscribir el equipo al que pertenece.");
            return;
        }

        TorneoConjunto torneo = seleccionarTorneoConjunto();

        if (torneo == null) {
            return;
        }

        String nombreEquipo = readString("Nombre del equipo: ");
        Equipo equipo = club.buscarEquipo(nombreEquipo);

        if (equipo == null) {
            System.out.println("No existe un equipo con ese nombre.");
            return;
        }

        Socio socio = (Socio) participante;

        if (!equipo.getJugadoresHabilitados().contains(socio)) {
            System.out.println("El socio no pertenece activamente a ese equipo.");
            return;
        }

        TipoCupo tipoCupo = seleccionarTipoCupo();

        InscripcionEquipoTorneo inscripcion = club.inscribirEquipoEnTorneo(
                torneo, equipo, tipoCupo);

        System.out.println("Equipo inscrito correctamente.");
        System.out.println("Valor pagado: " + inscripcion.getValorPagado());
    }

    private void consultarMisInscripciones() {
        List<InscripcionTorneo> inscripciones = buscarInscripcionesParticipante();

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
        List<InscripcionTorneo> inscripciones = buscarInscripcionesActivas();

        if (inscripciones.isEmpty()) {
            System.out.println("No tienes inscripciones activas.");
            return;
        }

        System.out.println("Inscripciones activas:");

        for (int i = 0; i < inscripciones.size(); i++) {
            InscripcionTorneo inscripcion = inscripciones.get(i);

            System.out.println((i + 1) + ". "
                    + inscripcion.getTorneo().getNombre());
        }

        int opcion = readInt("Número de la inscripción: ");

        if (opcion < 1 || opcion > inscripciones.size()) {
            System.out.println("La inscripción seleccionada no es válida.");
            return;
        }

        club.retirarInscripcionTorneo(
                inscripciones.get(opcion - 1));

        System.out.println("Inscripción retirada correctamente.");
    }

    private void consultarBonos() {
        if (!(participante instanceof Socio)) {
            System.out.println("Este tipo de participante no recibe bonos.");
            return;
        }

        Socio socio = (Socio) participante;
        List<BonoDescuento> bonos = socio.getBonos();

        if (bonos.isEmpty()) {
            System.out.println("No tienes bonos registrados.");
            return;
        }

        System.out.println();
        System.out.println("--- MIS BONOS ---");

        int numero = 1;

        for (BonoDescuento bono : bonos) {
            System.out.println(numero + ". Porcentaje: "
                    + bono.getPorcentaje()
                    + " | Vencimiento: " + bono.getFechaVencimiento()
                    + " | Estado: " + bono.getEstado());

            numero++;
        }
    }

    private TorneoIndividual seleccionarTorneoIndividual() {
        System.out.println("Torneos individuales:");

        boolean encontrado = false;

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

    private TorneoConjunto seleccionarTorneoConjunto() {
        System.out.println("Torneos de conjunto:");

        boolean encontrado = false;

        for (Torneo torneo : club.getTorneos()) {
            if (torneo instanceof TorneoConjunto) {
                System.out.println("- " + torneo.getId()
                        + " | " + torneo.getNombre()
                        + " | " + torneo.getEstado());

                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("No hay torneos de conjunto registrados.");
            return null;
        }

        String id = readString("ID del torneo: ");
        Torneo torneo = club.buscarTorneo(id);

        if (!(torneo instanceof TorneoConjunto)) {
            System.out.println("El torneo seleccionado no es de conjunto.");
            return null;
        }

        return (TorneoConjunto) torneo;
    }

    private TipoCupo seleccionarTipoCupo() {
        System.out.println("Tipos de cupo:");

        for (TipoCupo tipoCupo : TipoCupo.values()) {
            System.out.println("- " + tipoCupo);
        }

        return TipoCupo.valueOf(
                readString("Tipo de cupo: ").toUpperCase());
    }

    private List<InscripcionTorneo> buscarInscripcionesParticipante() {
        List<InscripcionTorneo> resultado = new ArrayList<InscripcionTorneo>();

        for (Torneo torneo : club.getTorneos()) {
            for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
                if (perteneceAlParticipante(inscripcion)) {
                    resultado.add(inscripcion);
                }
            }
        }

        return resultado;
    }

    private List<InscripcionTorneo> buscarInscripcionesActivas() {
        List<InscripcionTorneo> resultado = new ArrayList<InscripcionTorneo>();

        for (InscripcionTorneo inscripcion : buscarInscripcionesParticipante()) {
            if (inscripcion.estaActiva()) {
                resultado.add(inscripcion);
            }
        }

        return resultado;
    }

    private boolean perteneceAlParticipante(
            InscripcionTorneo inscripcion) {

        if (inscripcion instanceof InscripcionIndividualTorneo) {
            InscripcionIndividualTorneo individual = (InscripcionIndividualTorneo) inscripcion;

            return individual.contieneParticipante(
                    participante);
        }

        if (inscripcion instanceof InscripcionEquipoTorneo
                && participante instanceof Socio) {

            InscripcionEquipoTorneo equipo = (InscripcionEquipoTorneo) inscripcion;

            return equipo.getEquipo()
                    .getJugadoresHabilitados()
                    .contains((Socio) participante);
        }

        return false;
    }
}
