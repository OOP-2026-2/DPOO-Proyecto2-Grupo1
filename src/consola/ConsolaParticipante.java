package consola;

import static consola.InputHandler.readDate;
import static consola.InputHandler.readDouble;
import static consola.InputHandler.readEnum;
import static consola.InputHandler.readEnumOpcional;
import static consola.InputHandler.readInt;
import static consola.InputHandler.readString;
import static consola.InputHandler.readTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import clubdeportivo.ClubDeportivo;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.Equipo;
import clubdeportivo.deportes.InscripcionAcademia;
import clubdeportivo.deportes.InscripcionEquipo;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.entrenamientos.RegistroAsistencia;
import clubdeportivo.entrenamientos.SesionEntrenamiento;
import clubdeportivo.instalaciones.Instalacion;
import clubdeportivo.instalaciones.Reserva;
import clubdeportivo.instalaciones.TipoInstalacion;
import clubdeportivo.membresias.PagoMensualidad;
import clubdeportivo.torneos.BonoDescuento;
import clubdeportivo.torneos.EstadoTorneo;
import clubdeportivo.torneos.InscripcionEquipoTorneo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.InscripcionTorneo;
import clubdeportivo.torneos.ParticipanteTorneo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.Torneo;
import clubdeportivo.torneos.TorneoConjunto;
import clubdeportivo.torneos.TorneoIndividual;
import tiendaclub.ventas.descuentos.DescuentoBonoTorneo;
import tiendaclub.ventas.descuentos.DescuentoCodigoCompartido;
import tiendaclub.ventas.descuentos.DescuentoPuntos;
import tiendaclub.ventas.descuentos.SinDescuento;
import usuarios.Comprador;
import usuarios.Usuario;
import usuarios.tipos.Socio;
import utilidades.excepciones.InventarioInsuficienteException;
import utilidades.excepciones.ReservaNoDisponibleException;

public class ConsolaParticipante extends ConsolaBase {
    private final ParticipanteTorneo participante;

    public ConsolaParticipante(ClubDeportivo club, ParticipanteTorneo participante) {
        super(club);

        if (participante == null) {
            throw new IllegalArgumentException("El participante es obligatorio");
        }

        this.participante = participante;
    }

    @Override
    protected void mostrarMenu() {
        System.out.println();
        System.out.println("--- CONSOLA DEL PARTICIPANTE ---");
        System.out.println("Participante: " + participante.getNombre());
        System.out.println("1. Consultar torneos");
        System.out.println("2. Inscribirse en torneo individual");
        System.out.println("3. Inscribir equipo en torneo");
        System.out.println("4. Consultar mis inscripciones");
        System.out.println("5. Retirar inscripción");
        System.out.println("6. Consultar bonos");
        System.out.println("7. Comprar con bono");
        System.out.println("8. Comprar en tienda");
        System.out.println("9. Comprar en cafetería");
        System.out.println("10. Comprar redimiendo puntos");
        System.out.println("11. Comprar con código compartido");

        if (esSocio()) {
            System.out.println("12. Consultar disponibilidad de instalación");
            System.out.println("13. Reservar instalación");
            System.out.println("14. Consultar mis academias y equipos");
            System.out.println("15. Consultar mis sesiones de entrenamiento");
            System.out.println("16. Consultar mis pagos de mensualidad");
        }

        System.out.println("0. Salir");
    }

    @Override
    protected boolean ejecutarOpcion(int opcion)
            throws InventarioInsuficienteException, ReservaNoDisponibleException {

        if (opcion >= 12 && opcion <= 16 && !esSocio()) {
            return false;
        }

        switch (opcion) {
            case 1:
                consultarTorneos();
                return true;
            case 2:
                inscribirseEnTorneoIndividual();
                return true;
            case 3:
                inscribirEquipoEnTorneo();
                return true;
            case 4:
                consultarMisInscripciones();
                return true;
            case 5:
                retirarInscripcion();
                return true;
            case 6:
                consultarBonos();
                return true;
            case 7:
                comprarConBono();
                return true;
            case 8:
                comprarSinDescuento(false);
                return true;
            case 9:
                comprarSinDescuento(true);
                return true;
            case 10:
                comprarConPuntos();
                return true;
            case 11:
                comprarConCodigoCompartido();
                return true;
            case 12:
                consultarDisponibilidadInstalacion();
                return true;
            case 13:
                registrarReserva();
                return true;
            case 14:
                consultarAcademiasYEquipos();
                return true;
            case 15:
                consultarSesiones();
                return true;
            case 16:
                consultarPagos();
                return true;
            default:
                return false;
        }
    }

    private boolean esSocio() {
        return participante instanceof Socio;
    }

    // Torneos

    private void consultarTorneos() {
        titulo("TORNEOS");

        TipoTorneo tipo = readEnumOpcional("Tipo de torneo:", TipoTorneo.values());
        EstadoTorneo estado = readEnumOpcional("Estado del torneo:", EstadoTorneo.values());
        List<Torneo> torneos = club.consultarTorneos(tipo, estado);

        if (torneos.isEmpty()) {
            System.out.println("No hay torneos que coincidan con los filtros.");
            return;
        }

        for (Torneo torneo : torneos) {
            mostrarTorneo(torneo);
            System.out.println("Tarifa para ti: "
                    + dinero(torneo.calcularValorInscripcion(participante)));

            if (torneo instanceof TorneoIndividual) {
                String motivo = ((TorneoIndividual) torneo).motivoRechazo(participante);
                System.out.println("¿Puede participar?: "
                        + (motivo == null ? "Sí" : "No, " + motivo));
            }

            mostrarHistorialTorneo(torneo);
        }
    }

    private List<Torneo> torneosAbiertos(boolean individuales) {
        List<Torneo> resultado = new ArrayList<Torneo>();

        for (Torneo torneo : club.consultarTorneos(null, EstadoTorneo.ABIERTO)) {
            if ((torneo instanceof TorneoIndividual) == individuales) {
                resultado.add(torneo);
            }
        }

        return resultado;
    }

    private void inscribirseEnTorneoIndividual() {
        Torneo seleccionado = seleccionarTorneo(torneosAbiertos(true));

        if (seleccionado == null) {
            return;
        }

        TorneoIndividual torneo = (TorneoIndividual) seleccionado;
        List<ParticipanteTorneo> participantes = new ArrayList<ParticipanteTorneo>();
        participantes.add(participante);

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

    private void inscribirEquipoEnTorneo() {
        if (!esSocio()) {
            System.out.println("Solo un socio puede inscribir el equipo al que pertenece.");
            return;
        }

        Socio socio = (Socio) participante;
        List<Equipo> misEquipos = new ArrayList<Equipo>();

        for (InscripcionEquipo inscripcion : club.consultarEquiposDe(socio)) {
            if (inscripcion.estaActiva() && inscripcion.getEquipo().isActivo()) {
                misEquipos.add(inscripcion.getEquipo());
            }
        }

        if (misEquipos.isEmpty()) {
            System.out.println("No perteneces activamente a ningún equipo.");
            return;
        }

        Torneo seleccionado = seleccionarTorneo(torneosAbiertos(false));

        if (seleccionado == null) {
            return;
        }

        TorneoConjunto torneo = (TorneoConjunto) seleccionado;

        System.out.println("Tus equipos:");

        for (int i = 0; i < misEquipos.size(); i++) {
            String motivo = torneo.motivoRechazo(misEquipos.get(i));

            System.out.println((i + 1) + ". " + misEquipos.get(i).getNombre()
                    + (motivo == null ? " | Puede inscribirse" : " | No puede: " + motivo));
        }

        Equipo equipo = elegirDeLista(misEquipos, "Número del equipo");

        if (equipo == null) {
            return;
        }

        TipoCupo tipoCupo = seleccionarTipoCupo();
        InscripcionEquipoTorneo inscripcion = club.inscribirEquipoEnTorneo(
                torneo, equipo, tipoCupo, socio);

        System.out.println("Equipo inscrito correctamente.");
        System.out.println("Valor pagado: " + dinero(inscripcion.getValorPagado()));
    }

    private void consultarMisInscripciones() {
        List<InscripcionTorneo> inscripciones = club.consultarInscripcionesDe(participante);

        if (inscripciones.isEmpty()) {
            System.out.println("No tienes inscripciones en torneos.");
            return;
        }

        titulo("MIS INSCRIPCIONES");
        mostrarInscripciones(inscripciones);
    }

    private void retirarInscripcion() {
        List<InscripcionTorneo> activas = new ArrayList<InscripcionTorneo>();

        for (InscripcionTorneo inscripcion : club.consultarInscripcionesDe(participante)) {
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
            club.retirarInscripcionTorneo(participante, inscripcion);
            System.out.println("Inscripción retirada correctamente.");
        }
    }

    // Bonos y compras

    private void consultarBonos() {
        if (!esSocio()) {
            System.out.println("Este tipo de participante no recibe bonos.");
            return;
        }

        List<BonoDescuento> bonos = ((Socio) participante).getBonos();

        if (bonos.isEmpty()) {
            System.out.println("No tienes bonos registrados.");
            return;
        }

        titulo("MIS BONOS");
        int numero = 1;

        for (BonoDescuento bono : bonos) {
            System.out.println(numero + ". " + bono.getDescripcion());
            numero++;
        }
    }

    private void comprarConBono() throws InventarioInsuficienteException {
        if (!esSocio()) {
            System.out.println("Solo los socios pueden utilizar bonos de torneo.");
            return;
        }

        Socio socio = (Socio) participante;
        List<BonoDescuento> bonos = socio.getBonosDisponibles();

        if (bonos.isEmpty()) {
            System.out.println("No tienes bonos disponibles.");
            return;
        }

        titulo("BONOS DISPONIBLES");

        for (int i = 0; i < bonos.size(); i++) {
            System.out.println((i + 1) + ". " + bonos.get(i).getDescripcion());
        }

        BonoDescuento bono = elegirDeLista(bonos, "Número del bono");

        if (bono != null) {
            registrarCompra(socio, elegirCafeteria(), new DescuentoBonoTorneo(bono));
        }
    }

    private void comprarSinDescuento(boolean cafeteria) throws InventarioInsuficienteException {
        Comprador comprador = obtenerComprador();

        if (comprador != null) {
            registrarCompra(comprador, cafeteria, new SinDescuento());
        }
    }

    // 1 punto equivale a 1 peso de descuento
    private void comprarConPuntos() throws InventarioInsuficienteException {
        if (!esSocio()) {
            System.out.println("Solo los socios pueden redimir puntos.");
            return;
        }

        Socio socio = (Socio) participante;

        if (socio.getPuntosFidelidad() <= 0) {
            System.out.println("No tienes puntos disponibles.");
            return;
        }

        System.out.println("Puntos disponibles: " + puntos(socio.getPuntosFidelidad())
                + " (1 punto = $1 de descuento)");

        double puntos = readDouble("Puntos que desea redimir: ");

        if (puntos <= 0 || puntos > socio.getPuntosFidelidad()) {
            System.out.println("La cantidad de puntos no es válida.");
            return;
        }

        registrarCompra(socio, elegirCafeteria(), new DescuentoPuntos(puntos));
        System.out.println("Puntos acumulados: " + puntos(socio.getPuntosFidelidad()));
    }

    private void comprarConCodigoCompartido() throws InventarioInsuficienteException {
        Comprador comprador = obtenerComprador();

        if (comprador == null) {
            return;
        }

        String codigo = readString("Código compartido: ");

        if (club.buscarEmpleadoPorCodigoDescuento(codigo) == null) {
            System.out.println("El código compartido no es válido.");
            return;
        }

        registrarCompra(comprador, elegirCafeteria(), new DescuentoCodigoCompartido(codigo));
    }

    private boolean elegirCafeteria() {
        System.out.println("1. Tienda");
        System.out.println("2. Cafetería");

        return InputHandler.readIntEnRango("Tipo de compra: ", 1, 2) == 2;
    }

    private Comprador obtenerComprador() {
        if (!(participante instanceof Comprador)) {
            System.out.println("Este tipo de participante no puede realizar compras.");
            return null;
        }

        return (Comprador) participante;
    }

    // Instalaciones (solo socios)

    private void consultarDisponibilidadInstalacion() {
        titulo("CONSULTAR DISPONIBILIDAD");
        Instalacion instalacion = seleccionarInstalacion();

        if (instalacion == null) {
            return;
        }

        LocalDate fecha = readDate("Fecha (AAAA-MM-DD): ");
        LocalTime horaInicio = readTime("Hora de inicio (HH:MM): ");
        int duracion = readInt("Duración en minutos: ");

        System.out.println(club.consultarDisponibilidad(instalacion, fecha, horaInicio, duracion)
                ? "La instalación está disponible."
                : "La instalación no está disponible (horario del club "
                        + club.getHoraApertura() + " - " + club.getHoraCierre() + ").");
    }

    private void registrarReserva() throws ReservaNoDisponibleException {
        titulo("RESERVAR INSTALACIÓN");
        Instalacion instalacion = seleccionarInstalacion();

        if (instalacion == null) {
            return;
        }

        LocalDate fecha = readDate("Fecha (AAAA-MM-DD): ");
        LocalTime horaInicio = readTime("Hora de inicio (HH:MM): ");
        int duracion = readInt("Duración en minutos: ");
        int numeroJugadores = readInt("Número de jugadores: ");
        Reserva reserva;

        // En una cancha de conjunto el deporte define el mínimo de jugadores
        if (instalacion.getTipo() == TipoInstalacion.CANCHA_CONJUNTO) {
            DeporteConjunto deporte = seleccionarDeporteConjunto();

            if (deporte == null) {
                return;
            }

            reserva = new Reserva((Socio) participante, instalacion, fecha,
                    horaInicio, duracion, numeroJugadores, deporte);
        } else {
            Modalidad modalidad = readEnum("Modalidad:", Modalidad.values());

            reserva = new Reserva((Socio) participante, instalacion, fecha,
                    horaInicio, duracion, numeroJugadores, modalidad);
        }

        club.registrarReserva(reserva);
        System.out.println("Reserva registrada correctamente.");
        System.out.println("Horario: " + reserva.getHoraInicio()
                + " - " + reserva.calcularHoraFin());
    }

    private Instalacion seleccionarInstalacion() {
        if (club.getInstalaciones().isEmpty()) {
            System.out.println("No hay instalaciones registradas.");
            return null;
        }

        System.out.println("Instalaciones:");

        for (Instalacion instalacion : club.getInstalaciones()) {
            System.out.println("- " + instalacion);
        }

        Instalacion instalacion = club.buscarInstalacion(readString("ID de la instalación: "));

        if (instalacion == null) {
            System.out.println("No existe esa instalación.");
        }

        return instalacion;
    }

    private DeporteConjunto seleccionarDeporteConjunto() {
        if (club.getDeportesConjunto().isEmpty()) {
            System.out.println("No hay deportes de conjunto registrados.");
            return null;
        }

        System.out.println("Deportes de conjunto:");

        for (DeporteConjunto deporte : club.getDeportesConjunto()) {
            System.out.println("- " + deporte.getNombre()
                    + " (mínimo " + deporte.getMinimoJugadores() + " jugadores)");
        }

        DeporteConjunto deporte = club.buscarDeporteConjunto(readString("Deporte: "));

        if (deporte == null) {
            System.out.println("No existe ese deporte de conjunto.");
        }

        return deporte;
    }

    // Academias, equipos, sesiones y pagos del socio

    private void consultarAcademiasYEquipos() {
        Socio socio = (Socio) participante;

        titulo("MIS ACADEMIAS");
        List<InscripcionAcademia> academias = club.consultarAcademiasDe(socio);

        if (academias.isEmpty()) {
            System.out.println("No estás inscrito en ninguna academia.");
        }

        for (InscripcionAcademia inscripcion : academias) {
            System.out.println("- " + inscripcion.getDeporte().getNombre()
                    + " | Nivel: " + inscripcion.getNivel()
                    + " | Puntaje de ranking: " + inscripcion.getPuntajeRanking()
                    + " | Estado: " + inscripcion.getEstado());
        }

        titulo("MIS EQUIPOS");
        List<InscripcionEquipo> equipos = club.consultarEquiposDe(socio);

        if (equipos.isEmpty()) {
            System.out.println("No perteneces a ningún equipo.");
        }

        for (InscripcionEquipo inscripcion : equipos) {
            Equipo equipo = inscripcion.getEquipo();

            System.out.println("- " + equipo.getNombre()
                    + " (" + equipo.getDeporte().getNombre() + ", " + equipo.getCategoria() + ")"
                    + (equipo.isActivo() ? "" : " [dado de baja]")
                    + " | Posición: " + inscripcion.getPosicion()
                    + " | Camiseta: " + inscripcion.getNumeroCamiseta()
                    + " | Estado: " + inscripcion.getEstado()
                    + (inscripcion.estaSuspendida()
                            ? " (" + inscripcion.getPartidosSuspension() + " partido(s) de suspensión)"
                            : "")
                    + " | Puntaje del equipo: " + equipo.getPuntajeHistorico());
        }
    }

    private void consultarSesiones() {
        Socio socio = (Socio) participante;
        List<SesionEntrenamiento> sesiones = club.consultarSesionesDe(socio);

        if (sesiones.isEmpty()) {
            System.out.println("No tienes sesiones de entrenamiento.");
            return;
        }

        titulo("MIS SESIONES DE ENTRENAMIENTO");
        mostrarSesiones(sesiones, false);

        System.out.println("Mi asistencia:");

        for (SesionEntrenamiento sesion : sesiones) {
            RegistroAsistencia registro = sesion.buscarRegistro(socio);

            System.out.println("- " + sesion.getFecha() + " " + sesion.getHoraInicio() + ": "
                    + (registro == null ? "Sin registro" : registro.getEstado()
                            + (registro.getNotaDesempeno() == null
                                    ? ""
                                    : " | Nota " + registro.getNotaDesempeno())
                            + (registro.getComentario() == null
                                    ? ""
                                    : " | " + registro.getComentario())));
        }
    }

    private void consultarPagos() {
        List<PagoMensualidad> pagos = club.consultarPagosDe((Socio) participante);

        if (pagos.isEmpty()) {
            System.out.println("No tienes pagos de mensualidad registrados.");
            return;
        }

        titulo("MIS PAGOS DE MENSUALIDAD");

        for (PagoMensualidad pago : pagos) {
            System.out.println("- Periodo " + pago.getPeriodo()
                    + " | Pagado el " + pago.getFecha()
                    + " | Valor: " + dinero(pago.getValor()));
        }
    }
}
