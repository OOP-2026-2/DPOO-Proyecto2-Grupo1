package consola;

import static consola.InputHandler.readDate;
import static consola.InputHandler.readBoolean;
import static consola.InputHandler.readDouble;
import static consola.InputHandler.readInt;
import static consola.InputHandler.readString;
import static consola.InputHandler.readTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import clubdeportivo.ClubDeportivo;
import clubdeportivo.deportes.Categoria;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.DeporteIndividual;
import clubdeportivo.deportes.Equipo;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.deportes.Nivel;
import clubdeportivo.deportes.Posicion;
import clubdeportivo.instalaciones.Instalacion;
import clubdeportivo.instalaciones.TipoInstalacion;
import clubdeportivo.torneos.EncuentroTorneo;
import clubdeportivo.torneos.BonoDescuento;
import clubdeportivo.torneos.EstadoTorneo;
import clubdeportivo.torneos.FormatoTorneo;
import clubdeportivo.torneos.PoliticaPremio;
import clubdeportivo.torneos.PremioCompetitivo;
import clubdeportivo.torneos.PremioMonetario;
import clubdeportivo.torneos.PremioRecreativo;
import clubdeportivo.torneos.PremioTorneo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.Torneo;
import clubdeportivo.torneos.TorneoConjunto;
import clubdeportivo.torneos.TorneoIndividual;
import clubdeportivo.turnos.DiaSemana;
import clubdeportivo.turnos.SolicitudCambioTurno;
import clubdeportivo.turnos.Turno;
import tiendaclub.inventario.ArticuloTienda;
import tiendaclub.inventario.Bebida;
import tiendaclub.inventario.ExistenciaInventario;
import tiendaclub.inventario.MovimientoInventario;
import tiendaclub.inventario.Producto;
import tiendaclub.inventario.Snack;
import tiendaclub.inventario.SugerenciaProducto;
import tiendaclub.inventario.TipoMovimiento;
import tiendaclub.inventario.UbicacionInventario;
import tiendaclub.ventas.Venta;
import tiendaclub.ventas.tipos.VentaCafeteria;
import tiendaclub.ventas.tipos.VentaTienda;
import usuarios.Usuario;
import usuarios.tipos.Empleado;
import usuarios.tipos.EstudianteUniversidad;
import usuarios.tipos.ParticipanteExterno;
import usuarios.tipos.Socio;
import usuarios.tipos.empleados.Administrador;
import usuarios.tipos.empleados.Entrenador;
import usuarios.tipos.empleados.Fisioterapeuta;
import utilidades.excepciones.InventarioInsuficienteException;

public class ConsolaAdministrador implements Subconsola {
    private final ClubDeportivo club;
    private final Administrador administrador;

    public ConsolaAdministrador(ClubDeportivo club, Administrador administrador) {
        if (club == null || administrador == null) {
            throw new IllegalArgumentException("El club y el administrador son obligatorios");
        }

        this.club = club;
        this.administrador = administrador;
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
                        registrarTorneoConjunto();
                        break;
                    case 2:
                        registrarTorneoIndividual();
                        break;
                    case 3:
                        consultarTorneos();
                        break;
                    case 4:
                        liberarCupos();
                        break;
                    case 5:
                        iniciarTorneo();
                        break;
                    case 6:
                        registrarResultado();
                        break;
                    case 7:
                        finalizarTorneo();
                        break;
                    case 8:
                        trasladarInventario();
                        break;
                    case 9:
                        consultarReportes();
                        break;
                    case 10:
                        gestionarSolicitudesCambioTurno();
                        break;
                    case 11:
                        gestionarSugerenciasProducto();
                        break;
                    case 12:
                        registrarUsuario();
                        break;
                    case 13:
                        consultarUsuarios();
                        break;
                    case 14:
                        registrarDeporteConjunto();
                        break;
                    case 15:
                        registrarDeporteIndividual();
                        break;
                    case 16:
                        registrarEquipo();
                        break;
                    case 17:
                        registrarInstalacion();
                        break;
                    case 18:
                        consultarDeportesEquiposEInstalaciones();
                        break;
                    case 19:
                        registrarProducto();
                        break;
                    case 20:
                        registrarUbicacionInventario();
                        break;
                    case 21:
                        reabastecerProducto();
                        break;
                    case 22:
                        consultarInventario();
                        break;
                    case 23:
                        asignarTurno();
                        break;
                    case 24:
                        consultarTurnos();
                        break;
                    case 0:
                        ejecutando = false;
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException
                    | InventarioInsuficienteException e) {
                System.out.println("No fue posible realizar la operación: " + e.getMessage());
            }
        }
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("--- CONSOLA DEL ADMINISTRADOR ---");
        System.out.println("Administrador: " + administrador.getNombre());
        System.out.println("1. Registrar torneo de conjunto");
        System.out.println("2. Registrar torneo individual");
        System.out.println("3. Consultar torneos");
        System.out.println("4. Liberar cupos preferenciales");
        System.out.println("5. Iniciar torneo");
        System.out.println("6. Registrar resultado");
        System.out.println("7. Finalizar torneo");
        System.out.println("8. Trasladar producto entre ubicaciones");
        System.out.println("9. Consultar reportes");
        System.out.println("10. Gestionar solicitudes de cambio de turno");
        System.out.println("11. Gestionar sugerencias de productos");
        System.out.println("12. Registrar usuario");
        System.out.println("13. Consultar usuarios");
        System.out.println("14. Registrar deporte de conjunto");
        System.out.println("15. Registrar deporte individual");
        System.out.println("16. Registrar equipo");
        System.out.println("17. Registrar instalación");
        System.out.println("18. Consultar deportes, equipos e instalaciones");
        System.out.println("19. Registrar producto");
        System.out.println("20. Registrar ubicación de inventario");
        System.out.println("21. Reabastecer producto");
        System.out.println("22. Consultar inventario completo");
        System.out.println("23. Asignar turno a empleado");
        System.out.println("24. Consultar turnos de empleados");
        System.out.println("0. Salir");
    }

    private void registrarUsuario() {
        System.out.println();
        System.out.println("--- REGISTRAR USUARIO ---");
        System.out.println("1. Socio");
        System.out.println("2. Estudiante universitario");
        System.out.println("3. Participante externo");
        System.out.println("4. Entrenador");
        System.out.println("5. Fisioterapeuta");
        System.out.println("6. Administrador");

        int tipo = readInt("Tipo de usuario: ");

        if (tipo < 1 || tipo > 6) {
            System.out.println("El tipo de usuario no es válido.");
            return;
        }

        String id = readString("ID: ");
        String nombre = readString("Nombre: ");
        LocalDate fechaNacimiento = readDate(
                "Fecha de nacimiento (AAAA-MM-DD): ");
        String login = readString("Login: ");
        String contrasena = readString("Contraseña: ");
        Usuario usuario;

        switch (tipo) {
            case 1:
                usuario = new Socio(
                        id, nombre, fechaNacimiento,
                        login, contrasena);
                break;
            case 2:
                String universidad = readString("Universidad: ");
                String correoInstitucional = readString(
                        "Correo institucional: ");
                usuario = new EstudianteUniversidad(
                        id, nombre, fechaNacimiento,
                        login, contrasena, universidad,
                        correoInstitucional);
                break;
            case 3:
                String contacto = readString("Contacto: ");
                usuario = new ParticipanteExterno(
                        id, nombre, fechaNacimiento,
                        login, contrasena, contacto);
                break;
            case 4:
                usuario = new Entrenador(
                        id, nombre, fechaNacimiento,
                        login, contrasena,
                        readString("Código de descuento: "));
                break;
            case 5:
                usuario = new Fisioterapeuta(
                        id, nombre, fechaNacimiento,
                        login, contrasena,
                        readString("Código de descuento: "));
                break;
            case 6:
                usuario = new Administrador(
                        id, nombre, fechaNacimiento,
                        login, contrasena);
                break;
            default:
                return;
        }

        club.registrarUsuario(usuario);
        System.out.println("Usuario registrado correctamente.");
    }

    private void consultarUsuarios() {
        System.out.println();
        System.out.println("--- USUARIOS REGISTRADOS ---");

        if (club.getUsuarios().isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        for (Usuario usuario : club.getUsuarios()) {
            System.out.println("- ID: " + usuario.getId()
                    + " | Nombre: " + usuario.getNombre()
                    + " | Tipo: " + nombreTipoUsuario(usuario)
                    + " | Login: " + usuario.getLogin());

            if (usuario instanceof Empleado) {
                Empleado empleado = (Empleado) usuario;
                System.out.println("  Código de descuento: "
                        + empleado.getCodigoDescuento());
            } else if (usuario instanceof EstudianteUniversidad) {
                EstudianteUniversidad estudiante = (EstudianteUniversidad) usuario;
                System.out.println("  Universidad: "
                        + estudiante.getUniversidad()
                        + " | Correo: "
                        + estudiante.getCorreoInstitucional());
            } else if (usuario instanceof ParticipanteExterno) {
                ParticipanteExterno externo = (ParticipanteExterno) usuario;
                System.out.println("  Contacto: "
                        + externo.getContacto());
            }
        }
    }

    private String nombreTipoUsuario(Usuario usuario) {
        if (usuario instanceof Socio) {
            return "Socio";
        }

        if (usuario instanceof EstudianteUniversidad) {
            return "Estudiante universitario";
        }

        if (usuario instanceof ParticipanteExterno) {
            return "Participante externo";
        }

        if (usuario instanceof Entrenador) {
            return "Entrenador";
        }

        if (usuario instanceof Fisioterapeuta) {
            return "Fisioterapeuta";
        }

        if (usuario instanceof Administrador) {
            return "Administrador";
        }

        return usuario.getClass().getSimpleName();
    }

    private void registrarDeporteConjunto() {
        System.out.println();
        System.out.println("--- REGISTRAR DEPORTE DE CONJUNTO ---");

        String nombre = readString("Nombre del deporte: ");
        int minimoJugadores = readInt("Mínimo de jugadores: ");
        int puntosVictoria = readInt("Puntos por victoria: ");
        boolean permiteEmpate = readBoolean(
                "¿El deporte permite empates? (s/n): ");
        int puntosEmpate = 0;

        if (permiteEmpate) {
            puntosEmpate = readInt("Puntos por empate: ");
        }

        DeporteConjunto deporte = new DeporteConjunto(
                nombre, minimoJugadores, puntosVictoria,
                puntosEmpate, permiteEmpate);

        int cantidadPosiciones = readInt(
                "Cantidad de posiciones: ");

        if (cantidadPosiciones < 0) {
            throw new IllegalArgumentException(
                    "La cantidad de posiciones no puede ser negativa");
        }

        for (int i = 1; i <= cantidadPosiciones; i++) {
            deporte.agregarPosicion(new Posicion(
                    readString("Posición #" + i + ": ")));
        }

        club.registrarDeporteConjunto(deporte);
        System.out.println(
                "Deporte de conjunto registrado correctamente.");
    }

    private void registrarDeporteIndividual() {
        System.out.println();
        System.out.println("--- REGISTRAR DEPORTE INDIVIDUAL ---");

        String nombre = readString("Nombre del deporte: ");
        int maximoInstalaciones = readInt(
                "Máximo de instalaciones: ");
        DeporteIndividual deporte = new DeporteIndividual(
                nombre, maximoInstalaciones);

        club.registrarDeporteIndividual(deporte);
        System.out.println(
                "Deporte individual registrado correctamente.");
    }

    private void registrarEquipo() {
        System.out.println();
        System.out.println("--- REGISTRAR EQUIPO ---");

        if (club.getDeportesConjunto().isEmpty()) {
            System.out.println(
                    "Primero debe registrar un deporte de conjunto.");
            return;
        }

        System.out.println("Deportes de conjunto disponibles:");
        for (DeporteConjunto deporte : club.getDeportesConjunto()) {
            System.out.println("- " + deporte.getNombre());
        }

        DeporteConjunto deporte = club.buscarDeporteConjunto(
                readString("Nombre del deporte: "));

        if (deporte == null) {
            System.out.println("No existe ese deporte de conjunto.");
            return;
        }

        String nombre = readString("Nombre del equipo: ");
        mostrarCategorias();
        Categoria categoria = Categoria.valueOf(
                readString("Categoría: ").toUpperCase());
        int cupoMaximo = readInt("Cupo máximo: ");
        Equipo equipo = new Equipo(
                nombre, categoria, cupoMaximo, deporte);

        club.registrarEquipo(equipo);
        System.out.println("Equipo registrado correctamente.");
    }

    private void registrarInstalacion() {
        System.out.println();
        System.out.println("--- REGISTRAR INSTALACIÓN ---");

        String id = readString("ID de la instalación: ");
        System.out.println("Tipos de instalación disponibles:");

        for (TipoInstalacion tipo : TipoInstalacion.values()) {
            System.out.println("- " + tipo);
        }

        TipoInstalacion tipo = TipoInstalacion.valueOf(
                readString("Tipo: ").toUpperCase());
        int capacidadMaxima = readInt("Capacidad máxima: ");
        boolean techada = readBoolean("¿Es techada? (s/n): ");
        DeporteIndividual disciplina = null;

        if (!club.getDeportesIndividuales().isEmpty()
                && readBoolean(
                        "¿Pertenece a un deporte individual? (s/n): ")) {

            System.out.println("Deportes individuales disponibles:");
            for (DeporteIndividual deporte : club.getDeportesIndividuales()) {

                System.out.println("- " + deporte.getNombre());
            }

            disciplina = club.buscarDeporteIndividual(
                    readString("Nombre del deporte individual: "));

            if (disciplina == null) {
                System.out.println(
                        "No existe ese deporte individual.");
                return;
            }
        }

        Instalacion instalacion = new Instalacion(
                id, tipo, capacidadMaxima, techada, disciplina);
        club.registrarInstalacion(instalacion);
        System.out.println("Instalación registrada correctamente.");
    }

    private void consultarDeportesEquiposEInstalaciones() {
        System.out.println();
        System.out.println(
                "--- DEPORTES, EQUIPOS E INSTALACIONES ---");

        System.out.println("Deportes de conjunto:");
        if (club.getDeportesConjunto().isEmpty()) {
            System.out.println("- No hay deportes registrados.");
        }

        for (DeporteConjunto deporte : club.getDeportesConjunto()) {
            System.out.println("- " + deporte.getNombre()
                    + " | Mínimo de jugadores: "
                    + deporte.getMinimoJugadores()
                    + " | Posiciones: "
                    + deporte.getPosiciones());

            for (Equipo equipo : deporte.getEquipos()) {
                System.out.println("  Equipo: " + equipo.getNombre()
                        + " | Categoría: " + equipo.getCategoria()
                        + " | Cupo: " + equipo.getCupoMaximo()
                        + " | Activo: "
                        + (equipo.isActivo() ? "Sí" : "No"));
            }
        }

        System.out.println("Deportes individuales:");
        if (club.getDeportesIndividuales().isEmpty()) {
            System.out.println("- No hay deportes registrados.");
        }

        for (DeporteIndividual deporte : club.getDeportesIndividuales()) {

            System.out.println("- " + deporte.getNombre()
                    + " | Máximo de instalaciones: "
                    + deporte.getMaximoInstalaciones()
                    + " | Instalaciones asociadas: "
                    + deporte.getInstalaciones().size());
        }

        System.out.println("Instalaciones:");
        if (club.getInstalaciones().isEmpty()) {
            System.out.println("- No hay instalaciones registradas.");
        }

        for (Instalacion instalacion : club.getInstalaciones()) {
            System.out.println("- " + instalacion
                    + " | Disciplina: "
                    + (instalacion.getDisciplina() == null
                            ? "Sin disciplina individual"
                            : instalacion.getDisciplina().getNombre()));
        }
    }

    private void registrarProducto() {
        System.out.println();
        System.out.println("--- REGISTRAR PRODUCTO ---");

        String nombre = readString("Nombre del producto: ");
        Producto producto = crearProducto(nombre);
        club.registrarProducto(producto);
        System.out.println("Producto registrado correctamente.");
    }

    private void registrarUbicacionInventario() {
        System.out.println();
        System.out.println("--- REGISTRAR UBICACIÓN DE INVENTARIO ---");

        String nombre = readString("Nombre de la ubicación: ");
        club.registrarUbicacionInventario(
                new UbicacionInventario(nombre));
        System.out.println("Ubicación registrada correctamente.");
    }

    private void reabastecerProducto() {
        System.out.println();
        System.out.println("--- REABASTECER PRODUCTO ---");

        if (club.getProductos().isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }

        if (club.getUbicacionesInventario().isEmpty()) {
            System.out.println(
                    "No hay ubicaciones de inventario registradas.");
            return;
        }

        System.out.println("Productos disponibles:");
        for (Producto producto : club.getProductos()) {
            System.out.println("- " + producto.getNombre()
                    + " | Precio: " + producto.getPrecio());
        }

        Producto producto = club.buscarProducto(
                readString("Nombre del producto: "));

        if (producto == null) {
            System.out.println("No existe ese producto.");
            return;
        }

        System.out.println("Ubicaciones disponibles:");
        for (UbicacionInventario ubicacion : club.getUbicacionesInventario()) {

            System.out.println("- " + ubicacion.getNombre());
        }

        UbicacionInventario ubicacion = club.buscarUbicacionInventario(
                readString("Nombre de la ubicación: "));

        if (ubicacion == null) {
            System.out.println("No existe esa ubicación.");
            return;
        }

        int cantidad = readInt("Cantidad que desea agregar: ");
        club.reabastecerProducto(producto, ubicacion, cantidad);
        System.out.println("Producto reabastecido correctamente.");
    }

    private void consultarInventario() {
        System.out.println();
        System.out.println("--- INVENTARIO COMPLETO ---");

        if (club.getProductos().isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }

        for (Producto producto : club.getProductos()) {
            System.out.println();
            System.out.println("Producto: " + producto.getNombre()
                    + " | Precio: " + producto.getPrecio()
                    + " | Tipo: "
                    + producto.getClass().getSimpleName());

            boolean tieneExistencias = false;

            for (UbicacionInventario ubicacion : club.getUbicacionesInventario()) {

                ExistenciaInventario existencia = club.buscarExistencia(
                        producto, ubicacion);

                if (existencia != null) {
                    tieneExistencias = true;
                    System.out.println("- " + ubicacion.getNombre()
                            + ": " + existencia.getCantidad());
                }
            }

            if (!tieneExistencias) {
                System.out.println("- Sin existencias registradas.");
            }
        }
    }

    private void asignarTurno() {
        System.out.println();
        System.out.println("--- ASIGNAR TURNO ---");

        boolean hayEmpleados = false;
        System.out.println("Empleados disponibles:");

        for (Usuario usuario : club.getUsuarios()) {
            if (usuario instanceof Empleado) {
                hayEmpleados = true;
                System.out.println("- " + usuario.getId()
                        + " | " + usuario.getNombre()
                        + " | " + nombreTipoUsuario(usuario));
            }
        }

        if (!hayEmpleados) {
            System.out.println("No hay empleados registrados.");
            return;
        }

        Usuario usuario = club.buscarUsuario(
                readString("ID del empleado: "));

        if (!(usuario instanceof Empleado)) {
            System.out.println(
                    "No existe un empleado con ese ID.");
            return;
        }

        System.out.println("Días disponibles:");
        for (DiaSemana dia : DiaSemana.values()) {
            System.out.println("- " + dia);
        }

        DiaSemana dia = DiaSemana.valueOf(
                readString("Día de la semana: ").toUpperCase());
        LocalTime horaInicio = readTime(
                "Hora de inicio (HH:MM): ");
        LocalTime horaFin = readTime(
                "Hora de finalización (HH:MM): ");
        Turno turno = new Turno(dia, horaInicio, horaFin);

        club.asignarTurno((Empleado) usuario, turno);
        System.out.println("Turno asignado correctamente.");
    }

    private void consultarTurnos() {
        System.out.println();
        System.out.println("--- TURNOS DE EMPLEADOS ---");

        boolean hayEmpleados = false;

        for (Usuario usuario : club.getUsuarios()) {
            if (!(usuario instanceof Empleado)) {
                continue;
            }

            hayEmpleados = true;
            Empleado empleado = (Empleado) usuario;
            System.out.println();
            System.out.println(empleado.getId()
                    + " | " + empleado.getNombre()
                    + " | " + nombreTipoUsuario(empleado));

            if (empleado.getTurnos().isEmpty()) {
                System.out.println("- Sin turnos asignados.");
            } else {
                for (Turno turno : empleado.getTurnos()) {
                    System.out.println("- " + turno);
                }
            }
        }

        if (!hayEmpleados) {
            System.out.println("No hay empleados registrados.");
        }
    }

    private void registrarTorneoConjunto() {
        System.out.println();
        System.out.println("--- REGISTRAR TORNEO DE CONJUNTO ---");

        DatosTorneo datos = leerDatosTorneo();
        String nombreDeporte = readString("Nombre del deporte de conjunto: ");
        DeporteConjunto deporte = club.buscarDeporteConjunto(nombreDeporte);

        if (deporte == null) {
            System.out.println("No existe ese deporte de conjunto.");
            return;
        }

        mostrarCategorias();
        Categoria categoria = Categoria.valueOf(
                readString("Categoría: ").toUpperCase());

        TorneoConjunto torneo = new TorneoConjunto(
                datos.id,
                datos.nombre,
                datos.fechaInicio,
                datos.horaInicio,
                datos.horaFin,
                datos.formato,
                datos.capacidad,
                datos.tipo,
                datos.fechaLiberacion,
                datos.politicaPremio,
                datos.tarifaInscripcion,
                deporte,
                categoria);

        club.registrarTorneo(torneo);
        System.out.println("Torneo de conjunto registrado correctamente.");
    }

    private void registrarTorneoIndividual() {
        System.out.println();
        System.out.println("--- REGISTRAR TORNEO INDIVIDUAL ---");

        DatosTorneo datos = leerDatosTorneo();
        String nombreDeporte = readString("Nombre del deporte individual: ");
        DeporteIndividual deporte = club.buscarDeporteIndividual(nombreDeporte);

        if (deporte == null) {
            System.out.println("No existe ese deporte individual.");
            return;
        }

        mostrarNiveles();
        Nivel nivel = Nivel.valueOf(
                readString("Nivel: ").toUpperCase());

        mostrarModalidades();
        Modalidad modalidad = Modalidad.valueOf(
                readString("Modalidad: ").toUpperCase());

        TorneoIndividual torneo = new TorneoIndividual(
                datos.id,
                datos.nombre,
                datos.fechaInicio,
                datos.horaInicio,
                datos.horaFin,
                datos.formato,
                datos.capacidad,
                datos.tipo,
                datos.fechaLiberacion,
                datos.politicaPremio,
                datos.tarifaInscripcion,
                deporte,
                nivel,
                modalidad);

        club.registrarTorneo(torneo);
        System.out.println("Torneo individual registrado correctamente.");
    }

    private DatosTorneo leerDatosTorneo() {
        String id = readString("ID del torneo: ");
        String nombre = readString("Nombre del torneo: ");
        LocalDate fechaInicio = readDate("Fecha de inicio (AAAA-MM-DD): ");
        LocalTime horaInicio = readTime("Hora de inicio (HH:MM): ");
        LocalTime horaFin = readTime("Hora de finalización (HH:MM): ");

        mostrarFormatos();
        FormatoTorneo formato = FormatoTorneo.valueOf(
                readString("Formato: ").toUpperCase());

        int capacidad = readInt("Capacidad de inscripciones: ");

        mostrarTiposTorneo();
        TipoTorneo tipo = TipoTorneo.valueOf(
                readString("Tipo de torneo: ").toUpperCase());

        LocalDate fechaLiberacion = readDate(
                "Fecha de liberación de cupos (AAAA-MM-DD): ");

        LocalTime horaLiberacion = readTime(
                "Hora de liberación de cupos (HH:MM): ");

        LocalDateTime fechaHoraLiberacion = LocalDateTime.of(
                fechaLiberacion, horaLiberacion);

        PoliticaPremio politicaPremio;
        double tarifaInscripcion;

        if (tipo == TipoTorneo.RECREATIVO) {
            double porcentaje = readDouble(
                    "Porcentaje del bono, por ejemplo 0.10: ");

            int diasVigencia = readInt(
                    "Días de vigencia del bono: ");

            politicaPremio = new PremioRecreativo(
                    porcentaje, diasVigencia);

            tarifaInscripcion = 0;
        } else {
            double porcentajeRecaudo = readDouble(
                    "Porcentaje del recaudo para el premio, por ejemplo 0.50: ");

            politicaPremio = new PremioCompetitivo(
                    porcentajeRecaudo);

            tarifaInscripcion = readDouble(
                    "Tarifa de inscripción: ");
        }

        return new DatosTorneo(
                id,
                nombre,
                fechaInicio,
                horaInicio,
                horaFin,
                formato,
                capacidad,
                tipo,
                fechaHoraLiberacion,
                politicaPremio,
                tarifaInscripcion);
    }

    private void consultarTorneos() {
        System.out.println();
        System.out.println("--- TORNEOS REGISTRADOS ---");

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
            System.out.println("Capacidad: " + torneo.getCapacidad());
            System.out.println("Inscripciones activas: "
                    + torneo.cantidadInscripcionesActivas());
            System.out.println("Cupos disponibles: "
                    + torneo.cantidadCuposDisponibles());
            System.out.println("Recaudo: " + torneo.calcularRecaudo());
        }
    }

    private void liberarCupos() {
        Torneo torneo = seleccionarTorneo();

        if (torneo == null) {
            return;
        }

        club.liberarCuposPreferenciales(torneo);
        System.out.println("Cupos preferenciales liberados correctamente.");
    }

    private void iniciarTorneo() {
        Torneo torneo = seleccionarTorneo();

        if (torneo == null) {
            return;
        }

        club.iniciarTorneo(torneo);
        System.out.println("Torneo iniciado correctamente.");

        if (torneo.getEncuentros().isEmpty()) {
            System.out.println("No se generaron encuentros.");
        } else {
            System.out.println("Encuentros generados:");

            for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
                System.out.println("- " + encuentro.getId()
                        + " | " + encuentro.getFecha()
                        + " | " + encuentro.getHoraInicio());
            }
        }
    }

    private void registrarResultado() {
        Torneo torneo = seleccionarTorneo();

        if (torneo == null) {
            return;
        }

        if (torneo.getEstado() != EstadoTorneo.EN_CURSO) {
            System.out.println("El torneo no se encuentra en curso.");
            return;
        }

        EncuentroTorneo encuentro = seleccionarEncuentro(torneo);

        if (encuentro == null) {
            return;
        }

        String marcador = readString(
                "Marcador, por ejemplo 2-1: ");

        club.registrarResultadoEncuentro(
                encuentro, marcador);

        System.out.println("Resultado registrado correctamente.");
    }

    private void finalizarTorneo() {
        Torneo torneo = seleccionarTorneo();

        if (torneo == null) {
            return;
        }

        List<PremioTorneo> premios = club.finalizarTorneo(
                torneo);

        System.out.println("Torneo finalizado correctamente.");
        System.out.println("Premios generados: " + premios.size());

        for (PremioTorneo premio : premios) {
            System.out.println("- Valor del premio: "
                    + premio.getValor()
                    + " | Entregado: "
                    + (premio.fueEntregado() ? "Sí" : "No"));
        }
    }

    private void trasladarInventario() {
        System.out.println();
        System.out.println("--- TRASLADAR INVENTARIO ---");

        if (!mostrarInventario()) {
            System.out.println("No hay productos disponibles para trasladar.");
            return;
        }

        String nombreProducto = readString("Nombre del producto: ");
        Producto producto = club.buscarProducto(nombreProducto);

        if (producto == null) {
            System.out.println("No existe un producto con ese nombre.");
            return;
        }

        String nombreOrigen = readString("Ubicación de origen: ");
        UbicacionInventario origen = club.buscarUbicacionInventario(
                nombreOrigen);

        if (origen == null) {
            System.out.println("No existe la ubicación de origen.");
            return;
        }

        String nombreDestino = readString("Ubicación de destino: ");
        UbicacionInventario destino = club.buscarUbicacionInventario(
                nombreDestino);

        if (destino == null) {
            System.out.println("No existe la ubicación de destino.");
            return;
        }

        int cantidad = readInt("Cantidad que desea trasladar: ");

        club.trasladarProducto(
                producto, origen, destino, cantidad);

        ExistenciaInventario existenciaOrigen = club.buscarExistencia(
                producto, origen);
        ExistenciaInventario existenciaDestino = club.buscarExistencia(
                producto, destino);

        System.out.println("Traslado registrado correctamente.");
        System.out.println("Cantidad en " + origen.getNombre()
                + ": " + existenciaOrigen.getCantidad());
        System.out.println("Cantidad en " + destino.getNombre()
                + ": " + existenciaDestino.getCantidad());
    }

    private boolean mostrarInventario() {
        boolean hayInventario = false;

        System.out.println("Inventario disponible:");

        for (UbicacionInventario ubicacion : club.getUbicacionesInventario()) {
            for (ExistenciaInventario existencia : ubicacion.getExistencias()) {
                if (existencia.getCantidad() > 0) {
                    hayInventario = true;
                    System.out.println("- Producto: "
                            + existencia.getProducto().getNombre()
                            + " | Ubicación: " + ubicacion.getNombre()
                            + " | Cantidad: " + existencia.getCantidad());
                }
            }
        }

        return hayInventario;
    }

    private void consultarReportes() {
        System.out.println();
        System.out.println("--- REPORTES DEL ADMINISTRADOR ---");
        System.out.println("1. Torneos por estado");
        System.out.println("2. Participación en torneos");
        System.out.println("3. Premios y bonos");
        System.out.println("4. Movimientos y ventas");

        int opcion = readInt("Seleccione un reporte: ");

        switch (opcion) {
            case 1:
                reporteTorneosPorEstado();
                break;
            case 2:
                reporteParticipacionTorneos();
                break;
            case 3:
                reportePremiosYBonos();
                break;
            case 4:
                reporteMovimientosYVentas();
                break;
            default:
                System.out.println("Reporte inválido.");
        }
    }

    private void reporteTorneosPorEstado() {
        System.out.println();
        System.out.println("--- TORNEOS POR ESTADO ---");

        for (EstadoTorneo estado : EstadoTorneo.values()) {
            int cantidad = 0;

            for (Torneo torneo : club.getTorneos()) {
                if (torneo.getEstado() == estado) {
                    cantidad++;
                }
            }

            System.out.println(estado + ": " + cantidad);
        }

        System.out.println("Total de torneos: "
                + club.getTorneos().size());
    }

    private void reporteParticipacionTorneos() {
        System.out.println();
        System.out.println("--- PARTICIPACIÓN EN TORNEOS ---");

        if (club.getTorneos().isEmpty()) {
            System.out.println("No hay torneos registrados.");
            return;
        }

        for (Torneo torneo : club.getTorneos()) {
            int activas = torneo.cantidadInscripcionesActivas();
            int retiradas = torneo.getInscripciones().size() - activas;
            double ocupacion = torneo.getCapacidad() == 0
                    ? 0
                    : activas * 100.0 / torneo.getCapacidad();

            System.out.println("Torneo: " + torneo.getNombre());
            System.out.println("Inscripciones activas: " + activas);
            System.out.println("Inscripciones retiradas: " + retiradas);
            System.out.println("Capacidad: " + torneo.getCapacidad());
            System.out.println("Ocupación: " + ocupacion + "%");
        }
    }

    private void reportePremiosYBonos() {
        System.out.println();
        System.out.println("--- PREMIOS Y BONOS ---");

        int premiosMonetarios = 0;
        int premiosEntregados = 0;
        int bonos = 0;
        int bonosDisponibles = 0;
        double valorPremios = 0;

        for (Torneo torneo : club.getTorneos()) {
            for (PremioTorneo premio : torneo.getPremiosOtorgados()) {
                if (premio instanceof PremioMonetario) {
                    premiosMonetarios++;
                    valorPremios += premio.getValor();

                    if (premio.fueEntregado()) {
                        premiosEntregados++;
                    }
                } else if (premio instanceof BonoDescuento) {
                    bonos++;

                    if (((BonoDescuento) premio).estaDisponible()) {
                        bonosDisponibles++;
                    }
                }
            }
        }

        System.out.println("Premios monetarios: " + premiosMonetarios);
        System.out.println("Premios monetarios entregados: "
                + premiosEntregados);
        System.out.println("Valor total de premios monetarios: "
                + valorPremios);
        System.out.println("Bonos generados: " + bonos);
        System.out.println("Bonos disponibles: " + bonosDisponibles);
        System.out.println("Bonos utilizados o vencidos: "
                + (bonos - bonosDisponibles));
    }

    private void reporteMovimientosYVentas() {
        System.out.println();
        System.out.println("--- MOVIMIENTOS Y VENTAS ---");

        int unidadesReabastecidas = 0;
        int unidadesTrasladadas = 0;
        int unidadesVendidas = 0;

        for (MovimientoInventario movimiento : club.getMovimientosInventario()) {
            if (movimiento.getTipo() == TipoMovimiento.REABASTECIMIENTO) {
                unidadesReabastecidas += movimiento.getCantidad();
            } else if (movimiento.getTipo() == TipoMovimiento.TRASLADO) {
                unidadesTrasladadas += movimiento.getCantidad();
            } else if (movimiento.getTipo() == TipoMovimiento.SALIDA) {
                unidadesVendidas += movimiento.getCantidad();
            }
        }

        int ventasTienda = 0;
        int ventasCafeteria = 0;
        double descuentos = 0;
        double impuestos = 0;
        double ingresos = 0;

        for (Venta venta : club.getVentas()) {
            if (venta instanceof VentaTienda) {
                ventasTienda++;
            } else if (venta instanceof VentaCafeteria) {
                ventasCafeteria++;
            }

            descuentos += venta.getDescuento();
            impuestos += venta.getImpuesto();
            ingresos += venta.getTotal();
        }

        System.out.println("Unidades reabastecidas: "
                + unidadesReabastecidas);
        System.out.println("Unidades trasladadas: "
                + unidadesTrasladadas);
        System.out.println("Unidades vendidas: " + unidadesVendidas);
        System.out.println("Ventas de tienda: " + ventasTienda);
        System.out.println("Ventas de cafetería: " + ventasCafeteria);
        System.out.println("Descuentos aplicados: " + descuentos);
        System.out.println("Impuestos recaudados: " + impuestos);
        System.out.println("Ingresos totales: " + ingresos);
    }

    private void gestionarSolicitudesCambioTurno() {
        List<SolicitudCambioTurno> pendientes = new ArrayList<SolicitudCambioTurno>();

        for (SolicitudCambioTurno solicitud : club.getSolicitudesCambioTurno()) {

            if (solicitud.estaPendiente()) {
                pendientes.add(solicitud);
            }
        }

        if (pendientes.isEmpty()) {
            System.out.println("No hay solicitudes pendientes.");
            return;
        }

        System.out.println();
        System.out.println("--- SOLICITUDES DE CAMBIO DE TURNO ---");

        for (int i = 0; i < pendientes.size(); i++) {
            SolicitudCambioTurno solicitud = pendientes.get(i);

            System.out.println((i + 1) + ". " + solicitud);
            System.out.println("Conserva cobertura: "
                    + (club.validarCobertura(solicitud) ? "Sí" : "No"));
        }

        int numero = readInt("Número de la solicitud: ");

        if (numero < 1 || numero > pendientes.size()) {
            System.out.println("La solicitud seleccionada no es válida.");
            return;
        }

        System.out.println("1. Aprobar");
        System.out.println("2. Rechazar");
        int decision = readInt("Decisión: ");
        SolicitudCambioTurno solicitud = pendientes.get(numero - 1);

        if (decision == 1) {
            club.aprobarCambioTurno(solicitud, administrador);
            System.out.println("Solicitud aprobada correctamente.");
        } else if (decision == 2) {
            club.rechazarCambioTurno(solicitud, administrador);
            System.out.println("Solicitud rechazada correctamente.");
        } else {
            System.out.println("La decisión no es válida.");
        }
    }

    private void gestionarSugerenciasProducto() {
        List<SugerenciaProducto> pendientes = new ArrayList<SugerenciaProducto>();

        for (SugerenciaProducto sugerencia : club.getSugerenciasProducto()) {

            if (sugerencia.estaPendiente()) {
                pendientes.add(sugerencia);
            }
        }

        if (pendientes.isEmpty()) {
            System.out.println("No hay sugerencias pendientes.");
            return;
        }

        System.out.println();
        System.out.println("--- SUGERENCIAS DE PRODUCTOS ---");

        for (int i = 0; i < pendientes.size(); i++) {
            SugerenciaProducto sugerencia = pendientes.get(i);

            System.out.println((i + 1) + ". " + sugerencia.getNombre()
                    + " | " + sugerencia.getDescripcion()
                    + " | Proponente: "
                    + sugerencia.getProponente().getNombre());
        }

        int numero = readInt("Número de la sugerencia: ");

        if (numero < 1 || numero > pendientes.size()) {
            System.out.println("La sugerencia seleccionada no es válida.");
            return;
        }

        System.out.println("1. Aprobar y crear producto");
        System.out.println("2. Rechazar");
        int decision = readInt("Decisión: ");
        SugerenciaProducto sugerencia = pendientes.get(numero - 1);

        if (decision == 1) {
            Producto producto = crearProductoSugerido(sugerencia);
            club.aprobarSugerencia(
                    sugerencia, administrador, producto);
            System.out.println("Sugerencia aprobada y producto creado.");
        } else if (decision == 2) {
            club.rechazarSugerencia(sugerencia, administrador);
            System.out.println("Sugerencia rechazada correctamente.");
        } else {
            System.out.println("La decisión no es válida.");
        }
    }

    private Producto crearProductoSugerido(
            SugerenciaProducto sugerencia) {

        return crearProducto(sugerencia.getNombre());
    }

    private Producto crearProducto(String nombre) {

        System.out.println("Tipos de producto:");
        System.out.println("1. Artículo de tienda");
        System.out.println("2. Bebida");
        System.out.println("3. Snack");

        int tipo = readInt("Tipo de producto: ");
        double precio = readDouble("Precio: ");

        if (tipo == 1) {
            String categoria = readString("Categoría: ");
            return new ArticuloTienda(
                    nombre, precio, categoria);
        }

        if (tipo == 2) {
            boolean caliente = readBoolean(
                    "¿La bebida es caliente? (s/n): ");
            return new Bebida(
                    nombre, precio, caliente);
        }

        if (tipo == 3) {
            int cantidadAlergenos = readInt(
                    "Cantidad de alérgenos: ");
            List<String> alergenos = new ArrayList<String>();

            if (cantidadAlergenos < 0) {
                throw new IllegalArgumentException(
                        "La cantidad de alérgenos no puede ser negativa");
            }

            for (int i = 1; i <= cantidadAlergenos; i++) {
                alergenos.add(readString(
                        "Alérgeno #" + i + ": "));
            }

            return new Snack(
                    nombre, precio, alergenos);
        }

        throw new IllegalArgumentException(
                "El tipo de producto no es válido");
    }

    private Torneo seleccionarTorneo() {
        if (club.getTorneos().isEmpty()) {
            System.out.println("No hay torneos registrados.");
            return null;
        }

        System.out.println();
        System.out.println("Torneos disponibles:");

        for (Torneo torneo : club.getTorneos()) {
            System.out.println("- " + torneo.getId()
                    + " | " + torneo.getNombre()
                    + " | " + torneo.getEstado());
        }

        String id = readString("ID del torneo: ");
        Torneo torneo = club.buscarTorneo(id);

        if (torneo == null) {
            System.out.println("No existe un torneo con ese ID.");
        }

        return torneo;
    }

    private EncuentroTorneo seleccionarEncuentro(
            Torneo torneo) {

        if (torneo.getEncuentros().isEmpty()) {
            System.out.println("El torneo no tiene encuentros.");
            return null;
        }

        System.out.println("Encuentros disponibles:");

        for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
            System.out.println("- " + encuentro.getId()
                    + " | " + encuentro.getEstado());
        }

        String id = readString("ID del encuentro: ");

        for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
            if (encuentro.getId().equalsIgnoreCase(id)) {
                return encuentro;
            }
        }

        System.out.println("No existe un encuentro con ese ID.");
        return null;
    }

    private void mostrarFormatos() {
        System.out.println("Formatos disponibles:");

        for (FormatoTorneo formato : FormatoTorneo.values()) {
            System.out.println("- " + formato);
        }
    }

    private void mostrarTiposTorneo() {
        System.out.println("Tipos disponibles:");

        for (TipoTorneo tipo : TipoTorneo.values()) {
            System.out.println("- " + tipo);
        }
    }

    private void mostrarCategorias() {
        System.out.println("Categorías disponibles:");

        for (Categoria categoria : Categoria.values()) {
            System.out.println("- " + categoria);
        }
    }

    private void mostrarNiveles() {
        System.out.println("Niveles disponibles:");

        for (Nivel nivel : Nivel.values()) {
            System.out.println("- " + nivel);
        }
    }

    private void mostrarModalidades() {
        System.out.println("Modalidades disponibles:");

        for (Modalidad modalidad : Modalidad.values()) {
            System.out.println("- " + modalidad);
        }
    }

    private static class DatosTorneo {
        private final String id;
        private final String nombre;
        private final LocalDate fechaInicio;
        private final LocalTime horaInicio;
        private final LocalTime horaFin;
        private final FormatoTorneo formato;
        private final int capacidad;
        private final TipoTorneo tipo;
        private final LocalDateTime fechaLiberacion;
        private final PoliticaPremio politicaPremio;
        private final double tarifaInscripcion;

        private DatosTorneo(String id, String nombre,
                LocalDate fechaInicio, LocalTime horaInicio,
                LocalTime horaFin, FormatoTorneo formato,
                int capacidad, TipoTorneo tipo,
                LocalDateTime fechaLiberacion,
                PoliticaPremio politicaPremio,
                double tarifaInscripcion) {

            this.id = id;
            this.nombre = nombre;
            this.fechaInicio = fechaInicio;
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
            this.formato = formato;
            this.capacidad = capacidad;
            this.tipo = tipo;
            this.fechaLiberacion = fechaLiberacion;
            this.politicaPremio = politicaPremio;
            this.tarifaInscripcion = tarifaInscripcion;
        }
    }
}