package consola;

import static consola.InputHandler.readInt;
import static consola.InputHandler.readBoolean;
import static consola.InputHandler.readDate;
import static consola.InputHandler.readDouble;
import static consola.InputHandler.readString;
import static consola.InputHandler.readTime;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

import clubdeportivo.ClubDeportivo;
import clubdeportivo.deportes.Equipo;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.instalaciones.Instalacion;
import clubdeportivo.instalaciones.Reserva;
import clubdeportivo.torneos.BonoDescuento;
import clubdeportivo.torneos.InscripcionEquipoTorneo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.InscripcionTorneo;
import clubdeportivo.torneos.ParticipanteTorneo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.Torneo;
import clubdeportivo.torneos.TorneoConjunto;
import clubdeportivo.torneos.TorneoIndividual;
import tiendaclub.inventario.ArticuloTienda;
import tiendaclub.inventario.Bebida;
import tiendaclub.inventario.ExistenciaInventario;
import tiendaclub.inventario.Producto;
import tiendaclub.inventario.Snack;
import tiendaclub.inventario.UbicacionInventario;
import tiendaclub.ventas.Venta;
import tiendaclub.ventas.descuentos.DescuentoBonoTorneo;
import tiendaclub.ventas.descuentos.DescuentoCodigoCompartido;
import tiendaclub.ventas.descuentos.DescuentoEmpleado;
import tiendaclub.ventas.descuentos.DescuentoPuntos;
import tiendaclub.ventas.descuentos.PoliticaDescuento;
import tiendaclub.ventas.descuentos.SinDescuento;
import tiendaclub.ventas.tipos.VentaCafeteria;
import tiendaclub.ventas.tipos.VentaTienda;
import usuarios.Comprador;
import usuarios.Usuario;
import usuarios.tipos.Socio;
import usuarios.tipos.Empleado;
import utilidades.excepciones.InventarioInsuficienteException;
import utilidades.excepciones.ReservaNoDisponibleException;

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
                    case 7:
                        comprarConBono();
                        break;
                    case 8:
                        comprarEnTienda();
                        break;
                    case 9:
                        comprarEnCafeteria();
                        break;
                    case 10:
                        comprarConPuntos();
                        break;
                    case 11:
                        comprarConCodigoCompartido();
                        break;
                    case 12:
                        consultarDisponibilidadInstalacion();
                        break;
                    case 13:
                        registrarReserva();
                        break;
                    case 0:
                        ejecutando = false;
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException
                    | InventarioInsuficienteException
                    | ReservaNoDisponibleException e) {
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
        System.out.println("7. Comprar con bono");
        System.out.println("8. Comprar en tienda");
        System.out.println("9. Comprar en cafetería");
        System.out.println("10. Comprar redimiendo puntos");
        System.out.println("11. Comprar con código compartido");

        if (participante instanceof Socio) {
            System.out.println("12. Consultar disponibilidad de instalación");
            System.out.println("13. Reservar instalación");
        }

        System.out.println("0. Salir");
    }

    private void consultarDisponibilidadInstalacion() {
        if (!(participante instanceof Socio)) {
            System.out.println(
                    "Esta opción está disponible únicamente para socios.");
            return;
        }

        System.out.println();
        System.out.println("--- CONSULTAR DISPONIBILIDAD ---");

        if (!mostrarInstalaciones()) {
            return;
        }

        Instalacion instalacion = club.buscarInstalacion(
                readString("ID de la instalación: "));

        if (instalacion == null) {
            System.out.println("No existe esa instalación.");
            return;
        }

        LocalDate fecha = readDate("Fecha (AAAA-MM-DD): ");
        LocalTime horaInicio = readTime(
                "Hora de inicio (HH:MM): ");
        int duracion = readInt("Duración en minutos: ");
        boolean disponible = club.consultarDisponibilidad(
                instalacion, fecha, horaInicio, duracion);

        System.out.println(disponible
                ? "La instalación está disponible."
                : "La instalación no está disponible.");
    }

    private void registrarReserva() {
        if (!(participante instanceof Socio)) {
            System.out.println(
                    "Esta opción está disponible únicamente para socios.");
            return;
        }

        System.out.println();
        System.out.println("--- RESERVAR INSTALACIÓN ---");

        if (!mostrarInstalaciones()) {
            return;
        }

        Instalacion instalacion = club.buscarInstalacion(
                readString("ID de la instalación: "));

        if (instalacion == null) {
            System.out.println("No existe esa instalación.");
            return;
        }

        LocalDate fecha = readDate("Fecha (AAAA-MM-DD): ");
        LocalTime horaInicio = readTime(
                "Hora de inicio (HH:MM): ");
        int duracion = readInt("Duración en minutos: ");
        int numeroJugadores = readInt("Número de jugadores: ");

        System.out.println("Modalidades disponibles:");
        for (Modalidad modalidad : Modalidad.values()) {
            System.out.println("- " + modalidad);
        }

        Modalidad modalidad = Modalidad.valueOf(
                readString("Modalidad: ").toUpperCase());
        Reserva reserva = new Reserva(
                (Socio) participante,
                instalacion,
                fecha,
                horaInicio,
                duracion,
                numeroJugadores,
                modalidad);

        club.registrarReserva(reserva);
        System.out.println("Reserva registrada correctamente.");
        System.out.println("Horario: " + reserva.getHoraInicio()
                + " - " + reserva.calcularHoraFin());
    }

    private boolean mostrarInstalaciones() {
        if (club.getInstalaciones().isEmpty()) {
            System.out.println("No hay instalaciones registradas.");
            return false;
        }

        System.out.println("Instalaciones disponibles:");
        for (Instalacion instalacion : club.getInstalaciones()) {
            System.out.println("- " + instalacion);
        }

        return true;
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

    private void comprarConBono() {
        if (!(participante instanceof Socio)) {
            System.out.println("Solo los socios pueden utilizar bonos de torneo.");
            return;
        }

        Socio socio = (Socio) participante;
        List<BonoDescuento> bonos = socio.getBonosDisponibles();

        if (bonos.isEmpty()) {
            System.out.println("No tienes bonos disponibles.");
            return;
        }

        System.out.println();
        System.out.println("Bonos disponibles:");

        for (int i = 0; i < bonos.size(); i++) {
            BonoDescuento bono = bonos.get(i);

            System.out.println((i + 1)
                    + ". Descuento: "
                    + (bono.getPorcentaje() * 100)
                    + "% | Vencimiento: "
                    + bono.getFechaVencimiento());
        }

        int numeroBono = readInt("Número del bono: ");

        if (numeroBono < 1 || numeroBono > bonos.size()) {
            System.out.println("El bono seleccionado no es válido.");
            return;
        }

        System.out.println("Tipos de compra:");
        System.out.println("1. Tienda");
        System.out.println("2. Cafetería");

        int tipoCompra = readInt("Tipo de compra: ");

        if (tipoCompra != 1 && tipoCompra != 2) {
            System.out.println("El tipo de compra no es válido.");
            return;
        }

        registrarCompra(socio, tipoCompra == 2,
                new DescuentoBonoTorneo(bonos.get(numeroBono - 1)));
    }

    private void comprarEnTienda() {
        Comprador comprador = obtenerComprador();

        if (comprador != null) {
            registrarCompra(comprador, false,
                    crearPoliticaNormal(comprador));
        }
    }

    private void comprarEnCafeteria() {
        Comprador comprador = obtenerComprador();

        if (comprador != null) {
            registrarCompra(comprador, true,
                    crearPoliticaNormal(comprador));
        }
    }

    private PoliticaDescuento crearPoliticaNormal(
            Comprador comprador) {

        if (comprador instanceof Empleado) {
            return new DescuentoEmpleado();
        }

        return new SinDescuento();
    }

    private void comprarConPuntos() {
        if (!(participante instanceof Socio)) {
            System.out.println("Solo los socios pueden redimir puntos.");
            return;
        }

        Socio socio = (Socio) participante;

        if (socio.getPuntosFidelidad() <= 0) {
            System.out.println("No tienes puntos disponibles.");
            return;
        }

        System.out.println("Puntos disponibles: "
                + socio.getPuntosFidelidad());

        double puntos = readDouble("Puntos que desea redimir: ");

        if (puntos <= 0 || puntos > socio.getPuntosFidelidad()) {
            System.out.println("La cantidad de puntos no es válida.");
            return;
        }

        System.out.println("Tipos de compra:");
        System.out.println("1. Tienda");
        System.out.println("2. Cafetería");

        int tipoCompra = readInt("Tipo de compra: ");

        if (tipoCompra != 1 && tipoCompra != 2) {
            System.out.println("El tipo de compra no es válido.");
            return;
        }

        registrarCompra(socio, tipoCompra == 2,
                new DescuentoPuntos(puntos));
    }

    private void comprarConCodigoCompartido() {
        Comprador comprador = obtenerComprador();

        if (comprador == null) {
            return;
        }

        String codigo = readString("Código compartido: ");

        if (club.buscarEmpleadoPorCodigoDescuento(codigo) == null) {
            System.out.println("El código compartido no es válido.");
            return;
        }

        System.out.println("Tipos de compra:");
        System.out.println("1. Tienda");
        System.out.println("2. Cafetería");

        int tipoCompra = readInt("Tipo de compra: ");

        if (tipoCompra != 1 && tipoCompra != 2) {
            System.out.println("El tipo de compra no es válido.");
            return;
        }

        registrarCompra(comprador, tipoCompra == 2,
                new DescuentoCodigoCompartido(codigo));
    }

    private Comprador obtenerComprador() {
        if (!(participante instanceof Comprador)) {
            System.out.println("Este tipo de participante no puede realizar compras.");
            return null;
        }

        return (Comprador) participante;
    }

    private void registrarCompra(Comprador comprador, boolean cafeteria,
            PoliticaDescuento politica) {

        if (!mostrarInventario(cafeteria)) {
            System.out.println("No hay productos disponibles para este tipo de compra.");
            return;
        }

        String nombreProducto = readString("Nombre del producto: ");
        Producto producto = club.buscarProducto(nombreProducto);

        if (producto == null) {
            System.out.println("No existe un producto con ese nombre.");
            return;
        }

        if (!correspondeAlTipoDeCompra(producto, cafeteria)) {
            System.out.println("El producto no corresponde al tipo de compra seleccionado.");
            return;
        }

        String nombreUbicacion = readString("Nombre de la ubicación: ");
        UbicacionInventario ubicacion = club.buscarUbicacionInventario(
                nombreUbicacion);

        if (ubicacion == null) {
            System.out.println("No existe una ubicación con ese nombre.");
            return;
        }

        int cantidad = readInt("Cantidad: ");
        Venta venta;

        if (cafeteria) {
            VentaCafeteria ventaCafeteria = new VentaCafeteria(
                    LocalDate.now(), comprador, politica);

            ventaCafeteria.agregarDetalle(producto, cantidad);
            boolean incluirPropina = readBoolean(
                    "¿Desea incluir la propina sugerida del 10 %? (s/n): ");
            ventaCafeteria.finalizarVenta(incluirPropina);
            venta = ventaCafeteria;
        } else {
            VentaTienda ventaTienda = new VentaTienda(
                    LocalDate.now(), comprador, politica);

            ventaTienda.agregarDetalle(producto, cantidad);
            ventaTienda.finalizarVenta();
            venta = ventaTienda;
        }

        club.procesarVenta(venta, ubicacion);

        System.out.println("Compra registrada correctamente.");
        System.out.println("Subtotal: " + venta.getSubtotal());
        System.out.println("Descuento: " + venta.getDescuento());
        System.out.println("Impuesto: " + venta.getImpuesto());

        if (venta instanceof VentaCafeteria) {
            System.out.println("Propina: "
                    + ((VentaCafeteria) venta).getPropina());
        }

        System.out.println("Total: " + venta.getTotal());
        System.out.println("Puntos generados: " + venta.getPuntosGenerados());

        if (comprador instanceof Socio) {
            System.out.println("Puntos acumulados: "
                    + ((Socio) comprador).getPuntosFidelidad());
        }

        if (politica instanceof DescuentoBonoTorneo) {
            System.out.println("El bono quedó utilizado.");
        }

        if (politica instanceof DescuentoPuntos) {
            System.out.println("Puntos redimidos: "
                    + ((DescuentoPuntos) politica).getPuntosRedimidos());
        }

        if (politica instanceof DescuentoCodigoCompartido) {
            System.out.println("Código compartido aplicado correctamente.");
        }
    }

    private boolean mostrarInventario(boolean cafeteria) {
        boolean hayInventario = false;

        System.out.println();
        System.out.println(cafeteria
                ? "--- PRODUCTOS DE CAFETERÍA ---"
                : "--- ARTÍCULOS DE TIENDA ---");

        for (UbicacionInventario ubicacion : club.getUbicacionesInventario()) {
            for (ExistenciaInventario existencia : ubicacion.getExistencias()) {
                Producto producto = existencia.getProducto();

                if (existencia.getCantidad() > 0
                        && correspondeAlTipoDeCompra(producto, cafeteria)) {

                    hayInventario = true;
                    System.out.println("Producto: " + producto.getNombre()
                            + " | Precio: " + producto.getPrecio()
                            + " | Ubicación: " + ubicacion.getNombre()
                            + " | Cantidad: " + existencia.getCantidad());
                }
            }
        }

        return hayInventario;
    }

    private boolean correspondeAlTipoDeCompra(
            Producto producto, boolean cafeteria) {

        if (cafeteria) {
            return producto instanceof Bebida || producto instanceof Snack;
        }

        return producto instanceof ArticuloTienda;
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
