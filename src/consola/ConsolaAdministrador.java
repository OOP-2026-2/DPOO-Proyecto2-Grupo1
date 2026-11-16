package consola;

import static consola.InputHandler.readBoolean;
import static consola.InputHandler.readDate;
import static consola.InputHandler.readDouble;
import static consola.InputHandler.readEnum;
import static consola.InputHandler.readEnumOpcional;
import static consola.InputHandler.readInt;
import static consola.InputHandler.readIntEnRango;
import static consola.InputHandler.readNonNegativeInt;
import static consola.InputHandler.readPeriodo;
import static consola.InputHandler.readString;
import static consola.InputHandler.readTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import clubdeportivo.ClubDeportivo;
import clubdeportivo.competencias.Competencia;
import clubdeportivo.competencias.FormatoCompetencia;
import clubdeportivo.competencias.InscripcionCompetencia;
import clubdeportivo.competencias.PosicionTabla;
import clubdeportivo.competencias.TipoCompetencia;
import clubdeportivo.partidos.estadisticas.EstadisticaBaloncesto;
import clubdeportivo.partidos.estadisticas.EstadisticaFutbol;
import clubdeportivo.partidos.estadisticas.EstadisticaJugador;
import clubdeportivo.partidos.estadisticas.EstadisticaTenisMesa;
import clubdeportivo.partidos.estadisticas.EstadisticaTenisPadel;
import clubdeportivo.partidos.estadisticas.EstadisticaVoleibol;
import clubdeportivo.partidos.EstadoPartido;
import clubdeportivo.partidos.Partido;
import clubdeportivo.partidos.PartidoInterno;
import clubdeportivo.partidos.PartidoOficial;
import clubdeportivo.partidos.PartidoPractica;
import clubdeportivo.partidos.PropositoPartido;
import clubdeportivo.deportes.Categoria;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.DeporteIndividual;
import clubdeportivo.deportes.Equipo;
import clubdeportivo.deportes.EstadoInscripcion;
import clubdeportivo.deportes.InscripcionAcademia;
import clubdeportivo.deportes.InscripcionEquipo;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.deportes.Nivel;
import clubdeportivo.deportes.Posicion;
import clubdeportivo.entrenamientos.SesionEntrenamiento;
import clubdeportivo.entrenamientos.TipoSesion;
import clubdeportivo.instalaciones.Instalacion;
import clubdeportivo.instalaciones.TipoInstalacion;
import clubdeportivo.membresias.PagoMensualidad;
import clubdeportivo.torneos.BonoDescuento;
import clubdeportivo.torneos.EncuentroTorneo;
import clubdeportivo.torneos.EstadoEncuentro;
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
import tiendaclub.inventario.Producto;
import tiendaclub.inventario.Snack;
import tiendaclub.inventario.SugerenciaProducto;
import tiendaclub.inventario.TipoMovimiento;
import tiendaclub.inventario.UbicacionInventario;
import tiendaclub.ventas.ReporteVentas;
import tiendaclub.ventas.ResumenRubro;
import usuarios.Usuario;
import usuarios.tipos.Empleado;
import usuarios.tipos.EstudianteUniversidad;
import usuarios.tipos.ParticipanteExterno;
import usuarios.tipos.Socio;
import usuarios.tipos.empleados.Administrador;
import usuarios.tipos.empleados.Entrenador;
import usuarios.tipos.empleados.Fisioterapeuta;
import utilidades.excepciones.InventarioInsuficienteException;

public class ConsolaAdministrador extends ConsolaBase {
    private final Administrador administrador;

    public ConsolaAdministrador(ClubDeportivo club, Administrador administrador) {
        super(club);

        if (administrador == null) {
            throw new IllegalArgumentException("El administrador es obligatorio");
        }

        this.administrador = administrador;
    }

    @Override
    protected void mostrarMenu() {
        System.out.println();
        System.out.println("--- CONSOLA DEL ADMINISTRADOR ---");
        System.out.println("Administrador: " + administrador.getNombre());
        System.out.println("[Torneos]");
        System.out.println("1. Registrar torneo de conjunto");
        System.out.println("2. Registrar torneo individual");
        System.out.println("3. Consultar torneos");
        System.out.println("4. Liberar cupos preferenciales");
        System.out.println("5. Iniciar torneo");
        System.out.println("6. Registrar resultado de un encuentro");
        System.out.println("7. Finalizar torneo");
        System.out.println("[Inventario y reportes]");
        System.out.println("8. Trasladar producto entre ubicaciones");
        System.out.println("9. Consultar reportes");
        System.out.println("[Solicitudes]");
        System.out.println("10. Gestionar solicitudes de cambio de turno");
        System.out.println("11. Gestionar sugerencias de productos");
        System.out.println("[Usuarios, deportes e instalaciones]");
        System.out.println("12. Registrar usuario");
        System.out.println("13. Consultar usuarios");
        System.out.println("14. Registrar deporte de conjunto");
        System.out.println("15. Registrar deporte individual");
        System.out.println("16. Registrar equipo y sus miembros");
        System.out.println("17. Registrar instalación");
        System.out.println("18. Consultar deportes, equipos e instalaciones");
        System.out.println("[Productos]");
        System.out.println("19. Registrar producto");
        System.out.println("20. Registrar ubicación de inventario");
        System.out.println("21. Reabastecer producto");
        System.out.println("22. Consultar inventario completo");
        System.out.println("[Turnos]");
        System.out.println("23. Asignar turno a empleado");
        System.out.println("24. Consultar turnos de empleados");
        System.out.println("[Equipos y academias]");
        System.out.println("25. Agregar o retirar miembros de un equipo");
        System.out.println("26. Dar de baja un equipo");
        System.out.println("27. Inscribir socio en academia");
        System.out.println("[Entrenamientos]");
        System.out.println("28. Programar sesión de entrenamiento");
        System.out.println("29. Consultar sesiones de entrenamiento");
        System.out.println("[Competencias y partidos]");
        System.out.println("30. Registrar competencia");
        System.out.println("31. Inscribir equipo en competencia y registrar su nómina");
        System.out.println("32. Programar partido");
        System.out.println("33. Registrar resultado y estadísticas de un partido");
        System.out.println("34. Consultar competencias y partidos");
        System.out.println("[Mensualidades]");
        System.out.println("35. Registrar pago de mensualidad");
        System.out.println("36. Consultar pagos de mensualidad");
        System.out.println("0. Salir");
    }

    @Override
    protected boolean ejecutarOpcion(int opcion) throws InventarioInsuficienteException {
        switch (opcion) {
            case 1: registrarTorneoConjunto(); return true;
            case 2: registrarTorneoIndividual(); return true;
            case 3: consultarTorneos(); return true;
            case 4: liberarCupos(); return true;
            case 5: iniciarTorneo(); return true;
            case 6: registrarResultadoEncuentro(); return true;
            case 7: finalizarTorneo(); return true;
            case 8: trasladarInventario(); return true;
            case 9: consultarReportes(); return true;
            case 10: gestionarSolicitudesCambioTurno(); return true;
            case 11: gestionarSugerenciasProducto(); return true;
            case 12: registrarUsuario(); return true;
            case 13: consultarUsuarios(); return true;
            case 14: registrarDeporteConjunto(); return true;
            case 15: registrarDeporteIndividual(); return true;
            case 16: registrarEquipo(); return true;
            case 17: registrarInstalacion(); return true;
            case 18: consultarDeportesEquiposEInstalaciones(); return true;
            case 19: registrarProducto(); return true;
            case 20: registrarUbicacionInventario(); return true;
            case 21: reabastecerProducto(); return true;
            case 22: consultarInventario(); return true;
            case 23: asignarTurno(); return true;
            case 24: consultarTurnos(); return true;
            case 25: gestionarMiembrosEquipo(); return true;
            case 26: darDeBajaEquipo(); return true;
            case 27: inscribirSocioEnAcademia(); return true;
            case 28: programarSesion(); return true;
            case 29: consultarSesiones(); return true;
            case 30: registrarCompetencia(); return true;
            case 31: inscribirEquipoEnCompetencia(); return true;
            case 32: programarPartido(); return true;
            case 33: registrarResultadoPartido(); return true;
            case 34: consultarCompetenciasYPartidos(); return true;
            case 35: registrarPagoMensualidad(); return true;
            case 36: consultarPagosMensualidad(); return true;
            default: return false;
        }
    }

    // ===================== Usuarios =====================

    private void registrarUsuario() {
        titulo("REGISTRAR USUARIO");
        System.out.println("1. Socio");
        System.out.println("2. Estudiante universitario");
        System.out.println("3. Participante externo");
        System.out.println("4. Entrenador");
        System.out.println("5. Fisioterapeuta");
        System.out.println("6. Administrador");

        int tipo = readIntEnRango("Tipo de usuario: ", 1, 6);
        String id = readString("ID: ");
        String nombre = readString("Nombre: ");
        LocalDate fechaNacimiento = readDate("Fecha de nacimiento (AAAA-MM-DD): ");
        String login = readString("Login: ");
        String contrasena = readString("Contraseña: ");
        Usuario usuario;

        switch (tipo) {
            case 1:
                usuario = new Socio(id, nombre, fechaNacimiento, login, contrasena);
                break;
            case 2:
                usuario = new EstudianteUniversidad(id, nombre, fechaNacimiento, login,
                        contrasena, readString("Universidad: "),
                        readString("Correo institucional: "));
                break;
            case 3:
                usuario = new ParticipanteExterno(id, nombre, fechaNacimiento, login,
                        contrasena, readString("Contacto: "));
                break;
            case 4:
                usuario = new Entrenador(id, nombre, fechaNacimiento, login,
                        contrasena, readString("Código de descuento: "));
                break;
            case 5:
                usuario = new Fisioterapeuta(id, nombre, fechaNacimiento, login,
                        contrasena, readString("Código de descuento: "));
                break;
            default:
                usuario = new Administrador(id, nombre, fechaNacimiento, login, contrasena);
        }

        club.registrarUsuario(usuario);
        System.out.println("Usuario registrado correctamente.");
    }

    private void consultarUsuarios() {
        titulo("USUARIOS REGISTRADOS");

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
                System.out.println("  Código de descuento: "
                        + ((Empleado) usuario).getCodigoDescuento());
            } else if (usuario instanceof EstudianteUniversidad) {
                EstudianteUniversidad estudiante = (EstudianteUniversidad) usuario;
                System.out.println("  Universidad: " + estudiante.getUniversidad()
                        + " | Correo: " + estudiante.getCorreoInstitucional());
            } else if (usuario instanceof ParticipanteExterno) {
                System.out.println("  Contacto: " + ((ParticipanteExterno) usuario).getContacto());
            } else if (usuario instanceof Socio) {
                System.out.println("  Puntos de fidelidad: "
                        + puntos(((Socio) usuario).getPuntosFidelidad()));
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

        return "Administrador";
    }

    private Socio leerSocio() {
        Usuario usuario = club.buscarUsuario(readString("ID del socio: "));

        if (!(usuario instanceof Socio)) {
            System.out.println("No existe un socio con ese ID.");
            return null;
        }

        return (Socio) usuario;
    }

    private void mostrarSocios() {
        System.out.println("Socios registrados:");

        for (Usuario usuario : club.getUsuarios()) {
            if (usuario instanceof Socio) {
                System.out.println("- " + usuario.getId() + " | " + usuario.getNombre()
                        + " | Nacimiento: " + usuario.getFechaNacimiento());
            }
        }
    }

    // ===================== Deportes, equipos e instalaciones =====================

    private void registrarDeporteConjunto() {
        titulo("REGISTRAR DEPORTE DE CONJUNTO");

        String nombre = readString("Nombre del deporte: ");
        int minimoJugadores = readInt("Mínimo de jugadores: ");
        int puntosVictoria = readInt("Puntos por victoria: ");
        boolean permiteEmpate = readBoolean("¿El deporte permite empates? (s/n): ");
        int puntosEmpate = permiteEmpate ? readInt("Puntos por empate: ") : 0;

        DeporteConjunto deporte = new DeporteConjunto(
                nombre, minimoJugadores, puntosVictoria, puntosEmpate, permiteEmpate);

        // Cada integrante de un equipo juega en una posición, por eso se exige al menos una
        int cantidadPosiciones = readIntEnRango("Cantidad de posiciones (al menos 1): ", 1, 50);

        for (int i = 1; i <= cantidadPosiciones; i++) {
            deporte.agregarPosicion(new Posicion(readString("Posición #" + i + ": ")));
        }

        club.registrarDeporteConjunto(deporte);
        System.out.println("Deporte de conjunto registrado correctamente.");
    }

    private void registrarDeporteIndividual() {
        titulo("REGISTRAR DEPORTE INDIVIDUAL");

        String nombre = readString("Nombre del deporte: ");
        int maximoInstalaciones = readInt("Máximo de instalaciones reservadas al mismo tiempo: ");

        club.registrarDeporteIndividual(new DeporteIndividual(nombre, maximoInstalaciones));
        System.out.println("Deporte individual registrado correctamente.");
    }

    private DeporteConjunto seleccionarDeporteConjunto() {
        if (club.getDeportesConjunto().isEmpty()) {
            System.out.println("Primero debe registrar un deporte de conjunto.");
            return null;
        }

        System.out.println("Deportes de conjunto:");

        for (DeporteConjunto deporte : club.getDeportesConjunto()) {
            System.out.println("- " + deporte.getNombre()
                    + " (mínimo " + deporte.getMinimoJugadores() + " jugadores)");
        }

        DeporteConjunto deporte = club.buscarDeporteConjunto(readString("Nombre del deporte: "));

        if (deporte == null) {
            System.out.println("No existe ese deporte de conjunto.");
        }

        return deporte;
    }

    private DeporteIndividual seleccionarDeporteIndividual() {
        if (club.getDeportesIndividuales().isEmpty()) {
            System.out.println("Primero debe registrar un deporte individual.");
            return null;
        }

        System.out.println("Deportes individuales:");

        for (DeporteIndividual deporte : club.getDeportesIndividuales()) {
            System.out.println("- " + deporte.getNombre());
        }

        DeporteIndividual deporte = club.buscarDeporteIndividual(
                readString("Nombre del deporte individual: "));

        if (deporte == null) {
            System.out.println("No existe ese deporte individual.");
        }

        return deporte;
    }

    // El equipo se crea vacío y enseguida se registran sus miembros
    private void registrarEquipo() {
        titulo("REGISTRAR EQUIPO");
        DeporteConjunto deporte = seleccionarDeporteConjunto();

        if (deporte == null) {
            return;
        }

        String nombre = readString("Nombre del equipo: ");
        Categoria categoria = readEnum("Categoría:", Categoria.values());
        int cupoMaximo = readInt("Cupo máximo de integrantes: ");
        Equipo equipo = new Equipo(nombre, categoria, cupoMaximo, deporte);

        club.registrarEquipo(equipo);
        System.out.println("Equipo registrado correctamente. Ahora registre sus miembros.");

        agregarMiembros(equipo);
        mostrarEstadoPlantilla(equipo);
    }

    private void agregarMiembros(Equipo equipo) {
        mostrarSocios();

        while (equipo.tieneCupo()
                && readBoolean("¿Desea inscribir un miembro en " + equipo.getNombre() + "? (s/n): ")) {

            Socio socio = leerSocio();

            if (socio == null) {
                continue;
            }

            List<Posicion> posiciones = equipo.getDeporte().getPosiciones();
            System.out.println("Posiciones:");

            for (int i = 0; i < posiciones.size(); i++) {
                System.out.println((i + 1) + ". " + posiciones.get(i));
            }

            Posicion posicion = elegirDeLista(posiciones, "Número de la posición");

            if (posicion == null) {
                continue;
            }

            int camiseta = readInt("Número de camiseta: ");

            try {
                club.inscribirSocioEnEquipo(socio, equipo, posicion, camiseta);
                System.out.println(socio.getNombre() + " quedó inscrito en " + equipo.getNombre() + ".");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("No fue posible inscribir al socio: " + e.getMessage());
            }
        }

        if (!equipo.tieneCupo()) {
            System.out.println("El equipo completó su cupo máximo.");
        }
    }

    private void mostrarEstadoPlantilla(Equipo equipo) {
        int habilitados = equipo.getJugadoresHabilitados().size();

        if (equipo.cumpleMinimoJugadores()) {
            System.out.println("El equipo tiene " + habilitados
                    + " jugadores habilitados: cumple el mínimo del deporte.");
        } else {
            System.out.println("Aviso: no hay miembros suficientes. El equipo tiene "
                    + habilitados + " jugadores habilitados y "
                    + equipo.getDeporte().getNombre() + " exige "
                    + equipo.getDeporte().getMinimoJugadores()
                    + " (faltan " + equipo.calcularJugadoresFaltantes() + ").");
        }
    }

    private Equipo seleccionarEquipo(boolean soloActivos) {
        List<Equipo> equipos = new ArrayList<Equipo>();

        for (Equipo equipo : club.getEquipos()) {
            if (!soloActivos || equipo.isActivo()) {
                equipos.add(equipo);
            }
        }

        if (equipos.isEmpty()) {
            System.out.println("No hay equipos registrados.");
            return null;
        }

        System.out.println("Equipos:");

        for (Equipo equipo : equipos) {
            System.out.println("- " + equipo.getNombre()
                    + " | " + equipo.getDeporte().getNombre()
                    + " | " + equipo.getCategoria()
                    + " | Integrantes: " + equipo.contarMiembros() + "/" + equipo.getCupoMaximo()
                    + (equipo.isActivo() ? "" : " | Dado de baja"));
        }

        Equipo equipo = club.buscarEquipo(readString("Nombre del equipo: "));

        if (equipo == null || !equipos.contains(equipo)) {
            System.out.println("No hay un equipo con ese nombre en la lista.");
            return null;
        }

        return equipo;
    }

    private void mostrarMiembros(Equipo equipo) {
        System.out.println("Integrantes de " + equipo.getNombre() + ":");
        boolean hayMiembros = false;

        for (InscripcionEquipo inscripcion : equipo.getInscripciones()) {
            if (inscripcion.getEstado() != EstadoInscripcion.RETIRADA) {
                hayMiembros = true;
                System.out.println("- " + inscripcion.getSocio().getId()
                        + " | " + inscripcion.getSocio().getNombre()
                        + " | " + inscripcion.getPosicion()
                        + " | Camiseta " + inscripcion.getNumeroCamiseta()
                        + " | " + inscripcion.getEstado()
                        + (inscripcion.estaSuspendida()
                                ? " (" + inscripcion.getPartidosSuspension() + " partido(s))"
                                : ""));
            }
        }

        if (!hayMiembros) {
            System.out.println("- Sin integrantes");
        }
    }

    private void gestionarMiembrosEquipo() {
        titulo("MIEMBROS DE UN EQUIPO");
        Equipo equipo = seleccionarEquipo(true);

        if (equipo == null) {
            return;
        }

        mostrarMiembros(equipo);
        System.out.println("1. Agregar miembros");
        System.out.println("2. Retirar un miembro");

        if (readIntEnRango("Opción: ", 1, 2) == 1) {
            agregarMiembros(equipo);
        } else {
            Socio socio = leerSocio();

            if (socio != null) {
                club.retirarSocioDeEquipo(socio, equipo);
                System.out.println(socio.getNombre() + " fue retirado del equipo.");
            }
        }

        mostrarEstadoPlantilla(equipo);
    }

    private void darDeBajaEquipo() {
        titulo("DAR DE BAJA UN EQUIPO");
        Equipo equipo = seleccionarEquipo(true);

        if (equipo == null) {
            return;
        }

        if (!readBoolean("¿Confirma que desea dar de baja a " + equipo.getNombre() + "? (s/n): ")) {
            System.out.println("Operación cancelada.");
            return;
        }

        club.darDeBajaEquipo(equipo);
        System.out.println("El equipo " + equipo.getNombre() + " fue dado de baja.");
        System.out.println("Se retiraron sus inscripciones en torneos abiertos y en competencias.");
    }

    private void inscribirSocioEnAcademia() {
        titulo("INSCRIBIR SOCIO EN ACADEMIA");
        DeporteIndividual deporte = seleccionarDeporteIndividual();

        if (deporte == null) {
            return;
        }

        mostrarSocios();
        Socio socio = leerSocio();

        if (socio == null) {
            return;
        }

        Nivel nivel = readEnum("Nivel:", Nivel.values());
        club.inscribirSocioEnAcademia(socio, deporte, nivel);
        System.out.println(socio.getNombre() + " quedó inscrito en la academia de "
                + deporte.getNombre() + " (" + nivel + ").");
    }

    private void registrarInstalacion() {
        titulo("REGISTRAR INSTALACIÓN");

        String id = readString("ID de la instalación: ");
        TipoInstalacion tipo = readEnum("Tipo de instalación:", TipoInstalacion.values());
        int capacidadMaxima = readInt("Capacidad máxima: ");
        boolean techada = readBoolean("¿Es techada? (s/n): ");
        DeporteIndividual disciplina = null;

        if (tipo != TipoInstalacion.CANCHA_CONJUNTO
                && !club.getDeportesIndividuales().isEmpty()
                && readBoolean("¿Pertenece a un deporte individual? (s/n): ")) {

            disciplina = seleccionarDeporteIndividual();

            if (disciplina == null) {
                return;
            }
        }

        club.registrarInstalacion(new Instalacion(id, tipo, capacidadMaxima, techada, disciplina));
        System.out.println("Instalación registrada correctamente.");
    }

    private void consultarDeportesEquiposEInstalaciones() {
        titulo("DEPORTES, EQUIPOS E INSTALACIONES");

        System.out.println("Deportes de conjunto:");

        if (club.getDeportesConjunto().isEmpty()) {
            System.out.println("- No hay deportes registrados.");
        }

        for (DeporteConjunto deporte : club.getDeportesConjunto()) {
            System.out.println("- " + deporte.getNombre()
                    + " | Mínimo de jugadores: " + deporte.getMinimoJugadores()
                    + " | Victoria: " + deporte.getPuntosVictoria() + " pts"
                    + (deporte.isPermiteEmpate()
                            ? " | Empate: " + deporte.getPuntosEmpate() + " pts"
                            : " | Sin empates")
                    + " | Posiciones: " + deporte.getPosiciones());

            for (Equipo equipo : deporte.getEquipos()) {
                System.out.println("  Equipo: " + equipo.getNombre()
                        + " | Categoría: " + equipo.getCategoria()
                        + " | Integrantes: " + equipo.contarMiembros() + "/" + equipo.getCupoMaximo()
                        + " | Habilitados: " + equipo.getJugadoresHabilitados().size()
                        + " | Puntaje: " + equipo.getPuntajeHistorico()
                        + " | " + (equipo.isActivo() ? "Activo" : "Dado de baja"));
            }
        }

        System.out.println("Deportes individuales:");

        if (club.getDeportesIndividuales().isEmpty()) {
            System.out.println("- No hay deportes registrados.");
        }

        for (DeporteIndividual deporte : club.getDeportesIndividuales()) {
            System.out.println("- " + deporte.getNombre()
                    + " | Máximo de instalaciones: " + deporte.getMaximoInstalaciones()
                    + " | Instalaciones asociadas: " + deporte.getInstalaciones().size()
                    + " | Socios en la academia: " + deporte.getSociosActivos().size());
        }

        System.out.println("Instalaciones:");

        if (club.getInstalaciones().isEmpty()) {
            System.out.println("- No hay instalaciones registradas.");
        }

        for (Instalacion instalacion : club.getInstalaciones()) {
            System.out.println("- " + instalacion
                    + " | Disciplina: " + (instalacion.getDisciplina() == null
                            ? "Sin disciplina individual"
                            : instalacion.getDisciplina().getNombre()));
        }
    }

    // ===================== Productos e inventario =====================

    private void registrarProducto() {
        titulo("REGISTRAR PRODUCTO");

        club.registrarProducto(crearProducto(readString("Nombre del producto: ")));
        System.out.println("Producto registrado correctamente.");
    }

    private Producto crearProducto(String nombre) {
        System.out.println("Tipos de producto:");
        System.out.println("1. Artículo de tienda");
        System.out.println("2. Bebida");
        System.out.println("3. Snack");

        int tipo = readIntEnRango("Tipo de producto: ", 1, 3);
        double precio = readDouble("Precio: ");

        if (tipo == 1) {
            return new ArticuloTienda(nombre, precio, readString("Categoría: "));
        }

        if (tipo == 2) {
            return new Bebida(nombre, precio, readBoolean("¿La bebida es caliente? (s/n): "));
        }

        int cantidadAlergenos = readNonNegativeInt("Cantidad de alérgenos: ");
        List<String> alergenos = new ArrayList<String>();

        for (int i = 1; i <= cantidadAlergenos; i++) {
            alergenos.add(readString("Alérgeno #" + i + ": "));
        }

        return new Snack(nombre, precio, alergenos);
    }

    private void registrarUbicacionInventario() {
        titulo("REGISTRAR UBICACIÓN DE INVENTARIO");

        club.registrarUbicacionInventario(new UbicacionInventario(readString("Nombre de la ubicación: ")));
        System.out.println("Ubicación registrada correctamente.");
    }

    private Producto seleccionarProducto() {
        if (club.getProductos().isEmpty()) {
            System.out.println("No hay productos registrados.");
            return null;
        }

        System.out.println("Productos:");

        for (Producto producto : club.getProductos()) {
            System.out.println("- " + producto.getNombre()
                    + " (" + producto.getTipoProducto() + ") | Precio: "
                    + dinero(producto.getPrecio()));
        }

        Producto producto = club.buscarProducto(readString("Nombre del producto: "));

        if (producto == null) {
            System.out.println("No existe ese producto.");
        }

        return producto;
    }

    private UbicacionInventario seleccionarUbicacion(String prompt) {
        if (club.getUbicacionesInventario().isEmpty()) {
            System.out.println("No hay ubicaciones de inventario registradas.");
            return null;
        }

        System.out.println("Ubicaciones:");

        for (UbicacionInventario ubicacion : club.getUbicacionesInventario()) {
            System.out.println("- " + ubicacion.getNombre());
        }

        UbicacionInventario ubicacion = club.buscarUbicacionInventario(readString(prompt));

        if (ubicacion == null) {
            System.out.println("No existe esa ubicación.");
        }

        return ubicacion;
    }

    private void reabastecerProducto() {
        titulo("REABASTECER PRODUCTO");
        Producto producto = seleccionarProducto();

        if (producto == null) {
            return;
        }

        UbicacionInventario ubicacion = seleccionarUbicacion("Nombre de la ubicación: ");

        if (ubicacion == null) {
            return;
        }

        club.reabastecerProducto(producto, ubicacion, readInt("Cantidad que desea agregar: "));
        System.out.println("Producto reabastecido correctamente.");
    }

    private void consultarInventario() {
        titulo("INVENTARIO COMPLETO");

        if (club.getProductos().isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }

        for (Producto producto : club.getProductos()) {
            System.out.println();
            System.out.println("Producto: " + producto.getNombre()
                    + " | Precio: " + dinero(producto.getPrecio())
                    + " | Tipo: " + producto.getTipoProducto());

            boolean tieneExistencias = false;

            for (UbicacionInventario ubicacion : club.getUbicacionesInventario()) {
                ExistenciaInventario existencia = club.buscarExistencia(producto, ubicacion);

                if (existencia != null) {
                    tieneExistencias = true;
                    System.out.println("- " + ubicacion.getNombre() + ": " + existencia.getCantidad());
                }
            }

            if (!tieneExistencias) {
                System.out.println("- Sin existencias registradas.");
            }
        }
    }

    private void trasladarInventario() throws InventarioInsuficienteException {
        titulo("TRASLADAR INVENTARIO");
        Producto producto = seleccionarProducto();

        if (producto == null) {
            return;
        }

        UbicacionInventario origen = seleccionarUbicacion("Ubicación de origen: ");

        if (origen == null) {
            return;
        }

        UbicacionInventario destino = club.buscarUbicacionInventario(readString("Ubicación de destino: "));

        if (destino == null) {
            System.out.println("No existe la ubicación de destino.");
            return;
        }

        club.trasladarProducto(producto, origen, destino, readInt("Cantidad que desea trasladar: "));

        System.out.println("Traslado registrado correctamente.");
        System.out.println("Cantidad en " + origen.getNombre() + ": "
                + club.buscarExistencia(producto, origen).getCantidad());
        System.out.println("Cantidad en " + destino.getNombre() + ": "
                + club.buscarExistencia(producto, destino).getCantidad());
    }

    // ===================== Turnos y solicitudes =====================

    private void asignarTurno() {
        titulo("ASIGNAR TURNO");
        Empleado empleado = seleccionarEmpleado(false);

        if (empleado == null) {
            return;
        }

        DiaSemana dia = readEnum("Día de la semana:", DiaSemana.values());
        LocalTime horaInicio = readTime("Hora de inicio (HH:MM): ");
        LocalTime horaFin = readTime("Hora de finalización (HH:MM): ");

        club.asignarTurno(empleado, new Turno(dia, horaInicio, horaFin));
        System.out.println("Turno asignado correctamente.");
    }

    private Empleado seleccionarEmpleado(boolean soloEntrenadores) {
        List<Empleado> empleados = new ArrayList<Empleado>();

        for (Usuario usuario : club.getUsuarios()) {
            if (usuario instanceof Empleado
                    && (!soloEntrenadores || usuario instanceof Entrenador)) {
                empleados.add((Empleado) usuario);
            }
        }

        if (empleados.isEmpty()) {
            System.out.println(soloEntrenadores
                    ? "No hay entrenadores registrados."
                    : "No hay empleados registrados.");
            return null;
        }

        System.out.println(soloEntrenadores ? "Entrenadores:" : "Empleados:");

        for (Empleado empleado : empleados) {
            System.out.println("- " + empleado.getId() + " | " + empleado.getNombre()
                    + " | " + nombreTipoUsuario(empleado));
        }

        Usuario usuario = club.buscarUsuario(readString("ID: "));

        if (!(usuario instanceof Empleado) || !empleados.contains(usuario)) {
            System.out.println("No hay una persona con ese ID en la lista.");
            return null;
        }

        return (Empleado) usuario;
    }

    private void consultarTurnos() {
        titulo("TURNOS DE EMPLEADOS");
        boolean hayEmpleados = false;

        for (Usuario usuario : club.getUsuarios()) {
            if (!(usuario instanceof Empleado)) {
                continue;
            }

            hayEmpleados = true;
            Empleado empleado = (Empleado) usuario;
            System.out.println();
            System.out.println(empleado.getId() + " | " + empleado.getNombre()
                    + " | " + nombreTipoUsuario(empleado));

            if (empleado.getTurnos().isEmpty()) {
                System.out.println("- Sin turnos asignados.");
            }

            for (Turno turno : empleado.getTurnos()) {
                System.out.println("- " + turno);
            }
        }

        if (!hayEmpleados) {
            System.out.println("No hay empleados registrados.");
        }
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

        titulo("SOLICITUDES DE CAMBIO DE TURNO");

        for (int i = 0; i < pendientes.size(); i++) {
            SolicitudCambioTurno solicitud = pendientes.get(i);
            System.out.println((i + 1) + ". " + solicitud
                    + " | Conserva cobertura: " + (club.validarCobertura(solicitud) ? "Sí" : "No"));
        }

        SolicitudCambioTurno solicitud = elegirDeLista(pendientes, "Número de la solicitud");

        if (solicitud == null) {
            return;
        }

        System.out.println("1. Aprobar");
        System.out.println("2. Rechazar");

        if (readIntEnRango("Decisión: ", 1, 2) == 1) {
            club.aprobarCambioTurno(solicitud, administrador);
            System.out.println("Solicitud aprobada correctamente.");
        } else {
            club.rechazarCambioTurno(solicitud, administrador);
            System.out.println("Solicitud rechazada correctamente.");
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

        titulo("SUGERENCIAS DE PRODUCTOS");

        for (int i = 0; i < pendientes.size(); i++) {
            SugerenciaProducto sugerencia = pendientes.get(i);
            System.out.println((i + 1) + ". " + sugerencia.getNombre()
                    + " | " + sugerencia.getDescripcion()
                    + " | Proponente: " + sugerencia.getProponente().getNombre());
        }

        SugerenciaProducto sugerencia = elegirDeLista(pendientes, "Número de la sugerencia");

        if (sugerencia == null) {
            return;
        }

        System.out.println("1. Aprobar y crear producto");
        System.out.println("2. Rechazar");

        if (readIntEnRango("Decisión: ", 1, 2) == 1) {
            club.aprobarSugerencia(sugerencia, administrador, crearProducto(sugerencia.getNombre()));
            System.out.println("Sugerencia aprobada y producto creado.");
        } else {
            club.rechazarSugerencia(sugerencia, administrador);
            System.out.println("Sugerencia rechazada correctamente.");
        }
    }

    // ===================== Torneos =====================

    private void registrarTorneoConjunto() {
        titulo("REGISTRAR TORNEO DE CONJUNTO");
        DeporteConjunto deporte = seleccionarDeporteConjunto();

        if (deporte == null) {
            return;
        }

        Categoria categoria = readEnum("Categoría:", Categoria.values());
        DatosTorneo datos = leerDatosTorneo();

        club.registrarTorneo(new TorneoConjunto(datos.id, datos.nombre, datos.fechaInicio,
                datos.horaInicio, datos.horaFin, datos.formato, datos.capacidad, datos.tipo,
                datos.fechaLiberacion, datos.politicaPremio, datos.tarifaInscripcion,
                deporte, categoria));
        System.out.println("Torneo de conjunto registrado correctamente.");
    }

    private void registrarTorneoIndividual() {
        titulo("REGISTRAR TORNEO INDIVIDUAL");
        DeporteIndividual deporte = seleccionarDeporteIndividual();

        if (deporte == null) {
            return;
        }

        Nivel nivel = readEnum("Nivel:", Nivel.values());
        Modalidad modalidad = readEnum("Modalidad:", Modalidad.values());
        DatosTorneo datos = leerDatosTorneo();

        club.registrarTorneo(new TorneoIndividual(datos.id, datos.nombre, datos.fechaInicio,
                datos.horaInicio, datos.horaFin, datos.formato, datos.capacidad, datos.tipo,
                datos.fechaLiberacion, datos.politicaPremio, datos.tarifaInscripcion,
                deporte, nivel, modalidad));
        System.out.println("Torneo individual registrado correctamente.");
    }

    private DatosTorneo leerDatosTorneo() {
        String id = readString("ID del torneo: ");
        String nombre = readString("Nombre del torneo: ");
        LocalDate fechaInicio = readDate("Fecha de inicio (AAAA-MM-DD): ");
        System.out.println("Horario del club: " + club.getHoraApertura() + " - " + club.getHoraCierre());
        LocalTime horaInicio = readTime("Hora de inicio (HH:MM): ");
        LocalTime horaFin = readTime("Hora de finalización (HH:MM): ");
        FormatoTorneo formato = readEnum("Formato:", FormatoTorneo.values());
        int capacidad = readInt("Capacidad de inscripciones: ");
        TipoTorneo tipo = readEnum("Tipo de torneo:", TipoTorneo.values());
        LocalDate fechaLiberacion = readDate("Fecha de liberación de cupos preferenciales (AAAA-MM-DD): ");
        LocalTime horaLiberacion = readTime("Hora de liberación de cupos (HH:MM): ");
        PoliticaPremio politicaPremio;
        double tarifaInscripcion;

        if (tipo == TipoTorneo.RECREATIVO) {
            double porcentaje = readDouble("Porcentaje del bono, por ejemplo 0,10: ");
            int diasVigencia = readInt("Días de vigencia del bono: ");
            politicaPremio = new PremioRecreativo(porcentaje, diasVigencia);
            tarifaInscripcion = 0;
        } else {
            double porcentajeRecaudo = readDouble("Porcentaje del recaudo para el premio, por ejemplo 0,50: ");
            politicaPremio = new PremioCompetitivo(porcentajeRecaudo);
            tarifaInscripcion = readDouble("Tarifa de inscripción: ");
        }

        return new DatosTorneo(id, nombre, fechaInicio, horaInicio, horaFin, formato,
                capacidad, tipo, LocalDateTime.of(fechaLiberacion, horaLiberacion),
                politicaPremio, tarifaInscripcion);
    }

    private void consultarTorneos() {
        titulo("TORNEOS REGISTRADOS");

        TipoTorneo tipo = readEnumOpcional("Tipo de torneo:", TipoTorneo.values());
        EstadoTorneo estado = readEnumOpcional("Estado del torneo:", EstadoTorneo.values());
        List<Torneo> torneos = club.consultarTorneos(tipo, estado);

        if (torneos.isEmpty()) {
            System.out.println("No hay torneos que coincidan con los filtros.");
            return;
        }

        for (Torneo torneo : torneos) {
            mostrarTorneo(torneo);
            System.out.println("Recaudo: " + dinero(torneo.calcularRecaudo())
                    + " | Ocupación: " + String.format("%.1f", torneo.calcularPorcentajeOcupacion()) + "%");
            mostrarHistorialTorneo(torneo);
        }
    }

    private void liberarCupos() {
        Torneo torneo = seleccionarTorneo(club.consultarTorneos(null, EstadoTorneo.ABIERTO));

        if (torneo != null) {
            club.liberarCuposPreferenciales(torneo);
            System.out.println("Cupos preferenciales liberados correctamente.");
        }
    }

    private void iniciarTorneo() {
        Torneo torneo = seleccionarTorneo(club.consultarTorneos(null, EstadoTorneo.ABIERTO));

        if (torneo == null) {
            return;
        }

        club.iniciarTorneo(torneo);
        System.out.println("Torneo iniciado correctamente. Encuentros generados:");

        for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
            System.out.println("- " + describirEncuentro(encuentro));
        }
    }

    private void registrarResultadoEncuentro() {
        Torneo torneo = seleccionarTorneo(club.consultarTorneos(null, EstadoTorneo.EN_CURSO));

        if (torneo == null) {
            return;
        }

        List<EncuentroTorneo> pendientes = new ArrayList<EncuentroTorneo>();

        for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
            if (encuentro.getEstado() == EstadoEncuentro.PROGRAMADO) {
                pendientes.add(encuentro);
            }
        }

        if (pendientes.isEmpty()) {
            System.out.println("El torneo no tiene encuentros pendientes; ya puede finalizarse.");
            return;
        }

        System.out.println("Encuentros pendientes:");

        for (int i = 0; i < pendientes.size(); i++) {
            System.out.println((i + 1) + ". " + describirEncuentro(pendientes.get(i)));
        }

        EncuentroTorneo encuentro = elegirDeLista(pendientes, "Número del encuentro");

        if (encuentro == null) {
            return;
        }

        String marcador = readString("Marcador local-visitante, por ejemplo 2-1: ");
        List<EncuentroTorneo> anteriores = torneo.getEncuentros();

        club.registrarResultadoEncuentro(encuentro, marcador);
        System.out.println("Resultado registrado correctamente.");

        // En eliminación directa, al cerrar una ronda se generan los encuentros de la siguiente
        for (EncuentroTorneo nuevo : torneo.getEncuentros()) {
            if (!anteriores.contains(nuevo)) {
                System.out.println("Nuevo encuentro: " + describirEncuentro(nuevo));
            }
        }
    }

    private void finalizarTorneo() {
        Torneo torneo = seleccionarTorneo(club.consultarTorneos(null, EstadoTorneo.EN_CURSO));

        if (torneo == null) {
            return;
        }

        club.finalizarTorneo(torneo);
        System.out.println("Torneo finalizado correctamente.");
        mostrarPremios(torneo);
        System.out.println(torneo instanceof TorneoConjunto
                ? "El puntaje histórico de cada equipo sumó los puntos que obtuvo en el torneo."
                : "El puntaje de ranking de cada socio en su academia sumó los puntos que obtuvo.");
    }

    // ===================== Reportes =====================

    private void consultarReportes() {
        titulo("REPORTES DEL ADMINISTRADOR");
        System.out.println("1. Torneos por estado");
        System.out.println("2. Participación en torneos");
        System.out.println("3. Premios y bonos");
        System.out.println("4. Movimientos de inventario");
        System.out.println("5. Ventas por rango de fechas (dinero por rubro)");
        System.out.println("6. Ranking de equipos y academias");
        System.out.println("7. Jugadores suspendidos");

        switch (readIntEnRango("Seleccione un reporte: ", 1, 7)) {
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
                reporteMovimientos();
                break;
            case 5:
                reporteVentas();
                break;
            case 6:
                reporteRanking();
                break;
            default:
                reporteSuspendidos();
        }
    }

    private void reporteTorneosPorEstado() {
        titulo("TORNEOS POR ESTADO");

        for (EstadoTorneo estado : EstadoTorneo.values()) {
            System.out.println(estado + ": " + club.consultarTorneos(null, estado).size());
        }

        System.out.println("Total de torneos: " + club.getTorneos().size());
    }

    private void reporteParticipacionTorneos() {
        titulo("PARTICIPACIÓN EN TORNEOS");

        if (club.getTorneos().isEmpty()) {
            System.out.println("No hay torneos registrados.");
            return;
        }

        for (Torneo torneo : club.getTorneos()) {
            System.out.println(torneo.getNombre()
                    + " | Activas: " + torneo.cantidadInscripcionesActivas()
                    + " | Retiradas: " + torneo.cantidadInscripcionesRetiradas()
                    + " | Capacidad: " + torneo.getCapacidad()
                    + " | Ocupación: " + String.format("%.1f", torneo.calcularPorcentajeOcupacion()) + "%");
        }
    }

    private void reportePremiosYBonos() {
        titulo("PREMIOS Y BONOS");

        int premiosMonetarios = 0;
        int premiosEntregados = 0;
        int bonos = 0;
        int bonosDisponibles = 0;
        double valorPremios = 0;

        for (Torneo torneo : club.getTorneos()) {
            for (PremioTorneo premio : torneo.getPremiosOtorgados()) {
                System.out.println("- " + torneo.getNombre() + ": " + premio.getDescripcion());

                if (premio instanceof PremioMonetario) {
                    premiosMonetarios++;

                    if (premio.fueEntregado()) {
                        premiosEntregados++;
                        valorPremios += premio.getValor();
                    }
                } else if (premio instanceof BonoDescuento) {
                    bonos++;

                    if (((BonoDescuento) premio).estaDisponible()) {
                        bonosDisponibles++;
                    }
                }
            }
        }

        System.out.println("Premios en dinero: " + premiosMonetarios
                + " (entregados: " + premiosEntregados + ", valor entregado: " + dinero(valorPremios) + ")");
        System.out.println("Bonos generados: " + bonos
                + " (disponibles: " + bonosDisponibles
                + ", usados o vencidos: " + (bonos - bonosDisponibles) + ")");
    }

    private void reporteMovimientos() {
        titulo("MOVIMIENTOS DE INVENTARIO");

        for (TipoMovimiento tipo : TipoMovimiento.values()) {
            System.out.println(tipo + ": " + club.contarUnidadesMovidas(tipo) + " unidades");
        }
    }

    // El usuario escoge el rango; se informa entre qué fechas hay ventas registradas
    private void reporteVentas() {
        titulo("REPORTE DE VENTAS");

        if (club.getVentas().isEmpty()) {
            System.out.println("No hay ventas registradas.");
            return;
        }

        System.out.println("Hay " + club.getVentas().size() + " ventas registradas entre el "
                + club.getFechaPrimeraVenta() + " y el " + club.getFechaUltimaVenta() + ".");

        LocalDate desde = readDate("Fecha inicial (AAAA-MM-DD): ");
        LocalDate hasta = readDate("Fecha final (AAAA-MM-DD): ");
        ReporteVentas reporte = club.generarReporteVentas(desde, hasta);

        System.out.println();
        System.out.println("Ventas del " + reporte.getDesde() + " al " + reporte.getHasta()
                + ": " + reporte.getCantidadVentas());

        for (ResumenRubro rubro : reporte.getRubros()) {
            System.out.println("[" + rubro.getRubro() + "] Ventas: " + rubro.getCantidadVentas()
                    + " | Subtotal: " + dinero(rubro.getSubtotal())
                    + " | Descuentos: " + dinero(rubro.getDescuentos())
                    + " | Impuestos: " + dinero(rubro.getImpuestos())
                    + " | Propinas: " + dinero(rubro.getCargosAdicionales())
                    + " | Dinero recibido: " + dinero(rubro.getTotal()));
        }

        System.out.println("Total recibido en el periodo: " + dinero(reporte.getTotalVendido()));

        Map<String, Integer> unidades = reporte.getUnidadesPorTipoProducto();
        Map<String, Double> ventasPorTipo = reporte.getVentasPorTipoProducto();

        if (!unidades.isEmpty()) {
            System.out.println("Por tipo de producto (antes de descuentos e impuestos):");

            for (Map.Entry<String, Integer> entrada : unidades.entrySet()) {
                System.out.println("- " + entrada.getKey() + ": " + entrada.getValue()
                        + " unidades | " + dinero(ventasPorTipo.get(entrada.getKey())));
            }
        }
    }

    private void reporteRanking() {
        titulo("RANKING DE EQUIPOS");

        for (DeporteConjunto deporte : club.getDeportesConjunto()) {
            System.out.println(deporte.getNombre() + ":");
            int posicion = 1;

            for (Equipo equipo : club.consultarRankingEquipos(deporte)) {
                System.out.println("  " + posicion + ". " + equipo.getNombre()
                        + " (" + equipo.getCategoria() + ") | Puntaje: " + equipo.getPuntajeHistorico());
                posicion++;
            }
        }

        titulo("RANKING DE ACADEMIAS");

        for (DeporteIndividual deporte : club.getDeportesIndividuales()) {
            System.out.println(deporte.getNombre() + ":");
            int posicion = 1;

            for (InscripcionAcademia inscripcion : club.consultarRankingAcademia(deporte)) {
                System.out.println("  " + posicion + ". " + inscripcion.getSocio().getNombre()
                        + " (" + inscripcion.getNivel() + ") | Puntaje: " + inscripcion.getPuntajeRanking());
                posicion++;
            }
        }
    }

    private void reporteSuspendidos() {
        titulo("JUGADORES SUSPENDIDOS");
        List<InscripcionEquipo> suspendidos = club.consultarJugadoresSuspendidos();

        if (suspendidos.isEmpty()) {
            System.out.println("No hay jugadores suspendidos.");
        }

        for (InscripcionEquipo inscripcion : suspendidos) {
            System.out.println("- " + inscripcion.getSocio().getNombre()
                    + " | " + inscripcion.getEquipo().getNombre()
                    + " | Partidos oficiales por cumplir: " + inscripcion.getPartidosSuspension());
        }
    }

    // ===================== Entrenamientos =====================

    private void programarSesion() {
        titulo("PROGRAMAR SESIÓN DE ENTRENAMIENTO");
        Empleado empleado = seleccionarEmpleado(true);

        if (empleado == null) {
            return;
        }

        Instalacion instalacion = seleccionarInstalacion();

        if (instalacion == null) {
            return;
        }

        LocalDate fecha = readDate("Fecha (AAAA-MM-DD): ");
        LocalTime horaInicio = readTime("Hora de inicio (HH:MM): ");
        int duracion = readInt("Duración en minutos: ");

        System.out.println("1. Entrenamiento de un equipo");
        System.out.println("2. Entrenamiento de una academia (deporte individual)");
        SesionEntrenamiento sesion;
        Socio socioIndividual = null;

        if (readIntEnRango("Tipo de entrenamiento: ", 1, 2) == 1) {
            Equipo equipo = seleccionarEquipo(true);

            if (equipo == null) {
                return;
            }

            sesion = new SesionEntrenamiento(fecha, horaInicio, duracion,
                    (Entrenador) empleado, instalacion, TipoSesion.GRUPAL);
            sesion.asignarEquipo(equipo);
        } else {
            DeporteIndividual deporte = seleccionarDeporteIndividual();

            if (deporte == null) {
                return;
            }

            TipoSesion tipo = readEnum("Tipo de sesión:", new TipoSesion[] {
                    TipoSesion.INDIVIDUAL, TipoSesion.GRUPO_PEQUENO });

            if (tipo == TipoSesion.INDIVIDUAL) {
                socioIndividual = leerSocio();

                if (socioIndividual == null) {
                    return;
                }
            }

            sesion = new SesionEntrenamiento(fecha, horaInicio, duracion,
                    (Entrenador) empleado, instalacion, tipo);
            sesion.asignarDeporteIndividual(deporte);
        }

        club.programarSesion(sesion);

        // La sesión individual queda asignada a su socio desde el principio
        if (socioIndividual != null) {
            club.registrarAsistencia(sesion, socioIndividual);
        }

        System.out.println("Sesión programada: " + sesion);
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

    private void consultarSesiones() {
        titulo("SESIONES DE ENTRENAMIENTO");
        List<SesionEntrenamiento> sesiones = readBoolean("¿Desea filtrar por fecha? (s/n): ")
                ? club.consultarSesiones(readDate("Fecha (AAAA-MM-DD): "))
                : club.getSesionesEntrenamiento();

        if (sesiones.isEmpty()) {
            System.out.println("No hay sesiones programadas.");
            return;
        }

        mostrarSesiones(sesiones, true);
    }

    // ===================== Competencias y partidos =====================

    private void registrarCompetencia() {
        titulo("REGISTRAR COMPETENCIA");
        DeporteConjunto deporte = seleccionarDeporteConjunto();

        if (deporte == null) {
            return;
        }

        String nombre = readString("Nombre de la competencia: ");
        String organizador = readString("Organizador: ");
        int anio = readInt("Año: ");
        Categoria categoria = readEnum("Categoría:", Categoria.values());
        TipoCompetencia tipo = readEnum("Tipo de competencia:", TipoCompetencia.values());
        FormatoCompetencia formato = readEnum("Formato:", FormatoCompetencia.values());

        club.registrarCompetencia(new Competencia(nombre, organizador, anio, categoria,
                tipo, formato, deporte));
        System.out.println("Competencia registrada correctamente.");
    }

    private Competencia seleccionarCompetencia() {
        List<Competencia> competencias = club.getCompetencias();

        if (competencias.isEmpty()) {
            System.out.println("No hay competencias registradas.");
            return null;
        }

        System.out.println("Competencias:");

        for (int i = 0; i < competencias.size(); i++) {
            Competencia competencia = competencias.get(i);
            System.out.println((i + 1) + ". " + competencia.getNombre() + " " + competencia.getAnio()
                    + " | " + competencia.getDeporte().getNombre()
                    + " | " + competencia.getCategoria());
        }

        return elegirDeLista(competencias, "Número de la competencia");
    }

    private void inscribirEquipoEnCompetencia() {
        titulo("INSCRIBIR EQUIPO EN COMPETENCIA");
        Competencia competencia = seleccionarCompetencia();

        if (competencia == null) {
            return;
        }

        Equipo equipo = seleccionarEquipo(true);

        if (equipo == null) {
            return;
        }

        InscripcionCompetencia inscripcion = competencia.buscarInscripcion(equipo);

        if (inscripcion == null) {
            inscripcion = club.inscribirEquipoEnCompetencia(competencia, equipo);
            System.out.println("Equipo inscrito correctamente.");
        } else {
            System.out.println("El equipo ya está inscrito (" + inscripcion.getEstado()
                    + "); puede completar su nómina.");
        }

        List<Socio> disponibles = new ArrayList<Socio>();

        for (Socio socio : equipo.getJugadoresHabilitados()) {
            if (!inscripcion.estaEnNomina(socio)) {
                disponibles.add(socio);
            }
        }

        while (!disponibles.isEmpty()
                && readBoolean("¿Desea agregar un jugador a la nómina? (s/n): ")) {

            for (int i = 0; i < disponibles.size(); i++) {
                System.out.println((i + 1) + ". " + disponibles.get(i).getNombre());
            }

            Socio socio = elegirDeLista(disponibles, "Número del jugador");

            if (socio != null) {
                club.agregarJugadorANomina(competencia, equipo, socio);
                disponibles.remove(socio);
                System.out.println(socio.getNombre() + " quedó en la nómina.");
            }
        }

        System.out.println("Nómina: " + nombres(inscripcion.getNomina()));
    }

    private String nombres(List<Socio> socios) {
        if (socios.isEmpty()) {
            return "sin jugadores (juegan todos los integrantes del equipo)";
        }

        StringBuilder texto = new StringBuilder();

        for (Socio socio : socios) {
            if (texto.length() > 0) {
                texto.append(", ");
            }

            texto.append(socio.getNombre());
        }

        return texto.toString();
    }

    private void programarPartido() {
        titulo("PROGRAMAR PARTIDO");
        System.out.println("1. Partido oficial de una competencia");
        System.out.println("2. Partido interno entre equipos del club");
        System.out.println("3. Partido de práctica de un deporte individual");
        Partido partido;

        switch (readIntEnRango("Tipo de partido: ", 1, 3)) {
            case 1:
                partido = crearPartidoOficial();
                break;
            case 2:
                partido = crearPartidoInterno();
                break;
            default:
                partido = crearPartidoPractica();
        }

        if (partido != null) {
            club.registrarPartido(partido);
            System.out.println("Partido programado: " + describirPartido(partido));
        }
    }

    private PartidoOficial crearPartidoOficial() {
        Competencia competencia = seleccionarCompetencia();

        if (competencia == null) {
            return null;
        }

        List<Equipo> inscritos = new ArrayList<Equipo>();

        for (InscripcionCompetencia inscripcion : competencia.getInscripciones()) {
            if (inscripcion.estaActiva()) {
                inscritos.add(inscripcion.getEquipo());
            }
        }

        if (inscritos.isEmpty()) {
            System.out.println("La competencia no tiene equipos del club inscritos.");
            return null;
        }

        for (int i = 0; i < inscritos.size(); i++) {
            System.out.println((i + 1) + ". " + inscritos.get(i).getNombre());
        }

        Equipo equipo = elegirDeLista(inscritos, "Número del equipo del club");

        if (equipo == null) {
            return null;
        }

        LocalDate fecha = readDate("Fecha (AAAA-MM-DD): ");
        String rival = readString("Institución rival: ");
        String lugar = readString("Lugar: ");

        return new PartidoOficial(fecha, equipo, competencia, rival, lugar);
    }

    private PartidoInterno crearPartidoInterno() {
        System.out.println("Equipo local:");
        Equipo local = seleccionarEquipo(true);

        if (local == null) {
            return null;
        }

        System.out.println("Equipo visitante:");
        Equipo visitante = seleccionarEquipo(true);

        if (visitante == null) {
            return null;
        }

        LocalDate fecha = readDate("Fecha (AAAA-MM-DD): ");
        PropositoPartido proposito = readEnum("Propósito:", PropositoPartido.values());

        return new PartidoInterno(fecha, local, visitante, proposito);
    }

    private PartidoPractica crearPartidoPractica() {
        DeporteIndividual deporte = seleccionarDeporteIndividual();

        if (deporte == null) {
            return null;
        }

        Modalidad modalidad = readEnum("Modalidad:", Modalidad.values());
        LocalDate fecha = readDate("Fecha (AAAA-MM-DD): ");
        PartidoPractica partido = new PartidoPractica(fecha, deporte, modalidad);
        int jugadores = modalidad == Modalidad.SENCILLOS ? 2 : 4;

        System.out.println("Juegan socios de la academia o entrenadores. Se necesitan "
                + jugadores + " participantes.");

        while (!partido.cantidadParticipantesValida()) {
            Usuario usuario = club.buscarUsuario(readString("ID del participante: "));

            if (usuario == null) {
                System.out.println("No existe un usuario con ese ID.");
                continue;
            }

            try {
                partido.agregarParticipante(usuario);
                System.out.println(usuario.getNombre() + " agregado.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("No fue posible agregarlo: " + e.getMessage());
            }
        }

        return partido;
    }

    private String describirPartido(Partido partido) {
        String descripcion;

        if (partido instanceof PartidoOficial) {
            PartidoOficial oficial = (PartidoOficial) partido;
            descripcion = "Oficial " + oficial.getCompetencia().getNombre()
                    + " | " + oficial.getEquipo().getNombre()
                    + " vs " + oficial.getInstitucionRival()
                    + " en " + oficial.getLugar();
        } else if (partido instanceof PartidoInterno) {
            PartidoInterno interno = (PartidoInterno) partido;
            descripcion = "Interno (" + interno.getProposito() + ") | "
                    + interno.getEquipoLocal().getNombre()
                    + " vs " + interno.getEquipoVisitante().getNombre();
        } else {
            PartidoPractica practica = (PartidoPractica) partido;
            StringBuilder participantes = new StringBuilder();

            for (Usuario usuario : practica.getParticipantes()) {
                if (participantes.length() > 0) {
                    participantes.append(", ");
                }

                participantes.append(usuario.getNombre());
            }

            descripcion = "Práctica de " + practica.getDeporte().getNombre()
                    + " (" + practica.getModalidad() + ") | " + participantes;
        }

        return partido.getFecha() + " | " + descripcion + " | " + partido.getEstado()
                + (partido.getMarcadorFinal() == null ? "" : " | Marcador " + partido.getMarcadorFinal());
    }

    private void registrarResultadoPartido() {
        titulo("RESULTADO DE UN PARTIDO");
        List<Partido> pendientes = club.consultarPartidos(EstadoPartido.PROGRAMADO);

        if (pendientes.isEmpty()) {
            System.out.println("No hay partidos programados.");
            return;
        }

        for (int i = 0; i < pendientes.size(); i++) {
            System.out.println((i + 1) + ". " + describirPartido(pendientes.get(i)));
        }

        Partido partido = elegirDeLista(pendientes, "Número del partido");

        if (partido == null) {
            return;
        }

        System.out.println("1. Registrar resultado");
        System.out.println("2. Cancelar partido");

        if (readIntEnRango("Opción: ", 1, 2) == 2) {
            club.cancelarPartido(partido);
            System.out.println("Partido cancelado.");
            return;
        }

        String marcador = readString(partido instanceof PartidoOficial
                ? "Marcador del club-rival, por ejemplo 3-1: "
                : "Marcador, por ejemplo 3-1: ");
        List<EstadisticaJugador> estadisticas = leerEstadisticas(partido);
        List<Socio> suspendidos = club.registrarResultadoPartido(partido, marcador, estadisticas);

        System.out.println("Resultado registrado: " + describirPartido(partido));

        if (partido instanceof PartidoOficial) {
            PartidoOficial oficial = (PartidoOficial) partido;
            System.out.println("Puntos obtenidos: "
                    + oficial.getCompetencia().calcularPuntosObtenidos(oficial)
                    + " | Puntaje histórico de " + oficial.getEquipo().getNombre()
                    + ": " + oficial.getEquipo().getPuntajeHistorico());

            for (Socio socio : suspendidos) {
                System.out.println("Suspendido para el próximo partido oficial: " + socio.getNombre());
            }
        }
    }

    // Jugadores que pueden tener estadísticas en el partido
    private List<Socio> jugadoresDe(Partido partido) {
        List<Socio> jugadores = new ArrayList<Socio>();

        if (partido instanceof PartidoOficial) {
            PartidoOficial oficial = (PartidoOficial) partido;
            List<Socio> nomina = oficial.getCompetencia()
                    .buscarInscripcion(oficial.getEquipo()).getNomina();

            // Solo los habilitados: los suspendidos o retirados no juegan aunque estén en la nómina
            for (Socio socio : oficial.getEquipo().getJugadoresHabilitados()) {
                if (nomina.isEmpty() || nomina.contains(socio)) {
                    jugadores.add(socio);
                }
            }
        } else if (partido instanceof PartidoInterno) {
            PartidoInterno interno = (PartidoInterno) partido;
            jugadores.addAll(interno.getEquipoLocal().getJugadoresHabilitados());
            jugadores.addAll(interno.getEquipoVisitante().getJugadoresHabilitados());
        } else {
            for (Usuario usuario : ((PartidoPractica) partido).getParticipantes()) {
                if (usuario instanceof Socio) {
                    jugadores.add((Socio) usuario);
                }
            }
        }

        return jugadores;
    }

    private String nombreDeporte(Partido partido) {
        if (partido instanceof PartidoOficial) {
            return ((PartidoOficial) partido).getCompetencia().getDeporte().getNombre();
        }

        if (partido instanceof PartidoInterno) {
            return ((PartidoInterno) partido).getEquipoLocal().getDeporte().getNombre();
        }

        return ((PartidoPractica) partido).getDeporte().getNombre();
    }

    private List<EstadisticaJugador> leerEstadisticas(Partido partido) {
        List<EstadisticaJugador> estadisticas = new ArrayList<EstadisticaJugador>();
        List<Socio> jugadores = jugadoresDe(partido);

        while (!jugadores.isEmpty()
                && readBoolean("¿Desea registrar estadísticas de un jugador? (s/n): ")) {

            for (int i = 0; i < jugadores.size(); i++) {
                System.out.println((i + 1) + ". " + jugadores.get(i).getNombre());
            }

            Socio jugador = elegirDeLista(jugadores, "Número del jugador");

            if (jugador == null) {
                continue;
            }

            try {
                EstadisticaJugador estadistica = EstadisticaJugador.crearPara(
                        nombreDeporte(partido), jugador, partido);

                llenarEstadistica(estadistica);
                estadisticas.add(estadistica);
                jugadores.remove(jugador);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return estadisticas;
            }
        }

        return estadisticas;
    }

    private void llenarEstadistica(EstadisticaJugador estadistica) {
        if (estadistica instanceof EstadisticaFutbol) {
            EstadisticaFutbol futbol = (EstadisticaFutbol) estadistica;

            for (int i = readNonNegativeInt("Goles: "); i > 0; i--) {
                futbol.registrarGol();
            }

            for (int i = readNonNegativeInt("Asistencias: "); i > 0; i--) {
                futbol.registrarAsistencia();
            }

            for (int i = readNonNegativeInt("Tarjetas amarillas: "); i > 0; i--) {
                futbol.registrarTarjetaAmarilla();
            }

            for (int i = readNonNegativeInt("Tarjetas rojas: "); i > 0; i--) {
                futbol.registrarTarjetaRoja();
            }
        } else if (estadistica instanceof EstadisticaBaloncesto) {
            EstadisticaBaloncesto baloncesto = (EstadisticaBaloncesto) estadistica;
            baloncesto.agregarPuntos(readNonNegativeInt("Puntos: "));

            for (int i = readNonNegativeInt("Rebotes: "); i > 0; i--) {
                baloncesto.registrarRebote();
            }
        } else if (estadistica instanceof EstadisticaVoleibol) {
            EstadisticaVoleibol voleibol = (EstadisticaVoleibol) estadistica;

            for (int i = readNonNegativeInt("Aces: "); i > 0; i--) {
                voleibol.registrarAce();
            }

            for (int i = readNonNegativeInt("Bloqueos: "); i > 0; i--) {
                voleibol.registrarBloqueo();
            }
        } else if (estadistica instanceof EstadisticaTenisMesa) {
            EstadisticaTenisMesa tenisMesa = (EstadisticaTenisMesa) estadistica;

            for (int i = readNonNegativeInt("Sets ganados: "); i > 0; i--) {
                tenisMesa.registrarSetGanado();
            }

            tenisMesa.agregarPuntos(readNonNegativeInt("Puntos: "));
        } else if (estadistica instanceof EstadisticaTenisPadel) {
            EstadisticaTenisPadel tenis = (EstadisticaTenisPadel) estadistica;

            for (int i = readNonNegativeInt("Sets ganados: "); i > 0; i--) {
                tenis.registrarSetGanado();
            }

            for (int i = readNonNegativeInt("Games ganados: "); i > 0; i--) {
                tenis.registrarGameGanado();
            }

            for (int i = readNonNegativeInt("Aces: "); i > 0; i--) {
                tenis.registrarAce();
            }

            for (int i = readNonNegativeInt("Errores no forzados: "); i > 0; i--) {
                tenis.registrarErrorNoForzado();
            }
        }
    }

    private String describirEstadistica(EstadisticaJugador estadistica) {
        String nombre = estadistica.getJugador().getNombre() + ": ";

        if (estadistica instanceof EstadisticaFutbol) {
            EstadisticaFutbol futbol = (EstadisticaFutbol) estadistica;
            return nombre + futbol.getGoles() + " goles, " + futbol.getAsistencias()
                    + " asistencias, " + futbol.getTarjetasAmarillas() + " amarillas, "
                    + futbol.getTarjetasRojas() + " rojas";
        }

        if (estadistica instanceof EstadisticaBaloncesto) {
            EstadisticaBaloncesto baloncesto = (EstadisticaBaloncesto) estadistica;
            return nombre + baloncesto.getPuntos() + " puntos, " + baloncesto.getRebotes() + " rebotes";
        }

        if (estadistica instanceof EstadisticaVoleibol) {
            EstadisticaVoleibol voleibol = (EstadisticaVoleibol) estadistica;
            return nombre + voleibol.getAces() + " aces, " + voleibol.getBloqueos() + " bloqueos";
        }

        if (estadistica instanceof EstadisticaTenisMesa) {
            EstadisticaTenisMesa tenisMesa = (EstadisticaTenisMesa) estadistica;
            return nombre + tenisMesa.getSetsGanados() + " sets, "
                    + tenisMesa.getPuntosTotales() + " puntos";
        }

        EstadisticaTenisPadel tenis = (EstadisticaTenisPadel) estadistica;
        return nombre + tenis.getSetsGanados() + " sets, " + tenis.getGamesGanados() + " games, "
                + tenis.getAces() + " aces, " + tenis.getErroresNoForzados() + " errores no forzados";
    }

    private void consultarCompetenciasYPartidos() {
        titulo("COMPETENCIAS");

        if (club.getCompetencias().isEmpty()) {
            System.out.println("No hay competencias registradas.");
        }

        for (Competencia competencia : club.getCompetencias()) {
            System.out.println();
            System.out.println(competencia.getNombre() + " " + competencia.getAnio()
                    + " | Organiza: " + competencia.getOrganizador()
                    + " | " + competencia.getTipo() + " | " + competencia.getFormato()
                    + " | " + competencia.getDeporte().getNombre()
                    + " | " + competencia.getCategoria());

            for (InscripcionCompetencia inscripcion : competencia.getInscripciones()) {
                System.out.println("  Equipo: " + inscripcion.getEquipo().getNombre()
                        + " | " + inscripcion.getEstado()
                        + " | Nómina: " + nombres(inscripcion.getNomina()));
            }

            if (!competencia.obtenerTablaPosiciones().isEmpty()) {
                System.out.println("  Tabla de posiciones:");
                int posicion = 1;

                for (PosicionTabla fila : competencia.obtenerTablaPosiciones()) {
                    System.out.println("  " + posicion + ". " + fila.getInstitucion()
                            + " | PJ " + fila.getPartidosJugados()
                            + " | V " + fila.getVictorias()
                            + " | E " + fila.getEmpates()
                            + " | D " + fila.getDerrotas()
                            + " | Puntos " + fila.getPuntos());
                    posicion++;
                }
            }
        }

        titulo("PARTIDOS");

        if (club.getPartidos().isEmpty()) {
            System.out.println("No hay partidos registrados.");
        }

        for (Partido partido : club.getPartidos()) {
            System.out.println("- " + describirPartido(partido));

            for (EstadisticaJugador estadistica : partido.getEstadisticas()) {
                System.out.println("    " + describirEstadistica(estadistica));
            }
        }
    }

    // ===================== Mensualidades =====================

    private void registrarPagoMensualidad() {
        titulo("REGISTRAR PAGO DE MENSUALIDAD");
        mostrarSocios();
        Socio socio = leerSocio();

        if (socio == null) {
            return;
        }

        String periodo = readPeriodo("Periodo que paga (AAAA-MM): ");
        double valor = readDouble("Valor pagado: ");

        club.registrarPagoMensualidad(new PagoMensualidad(LocalDate.now(), periodo, valor, socio));
        System.out.println("Pago registrado para " + socio.getNombre() + " (" + periodo + ").");
    }

    private void consultarPagosMensualidad() {
        titulo("PAGOS DE MENSUALIDAD");
        List<PagoMensualidad> pagos = club.getPagosMensualidad();

        if (pagos.isEmpty()) {
            System.out.println("No hay pagos registrados.");
            return;
        }

        double total = 0;

        for (PagoMensualidad pago : pagos) {
            System.out.println("- " + pago.getSocio().getNombre()
                    + " | Periodo " + pago.getPeriodo()
                    + " | Pagado el " + pago.getFecha()
                    + " | " + dinero(pago.getValor()));
            total += pago.getValor();
        }

        System.out.println("Total recaudado: " + dinero(total));
    }

    // Agrupa los datos comunes de los dos tipos de torneo mientras se leen por consola
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

        private DatosTorneo(String id, String nombre, LocalDate fechaInicio,
                LocalTime horaInicio, LocalTime horaFin, FormatoTorneo formato,
                int capacidad, TipoTorneo tipo, LocalDateTime fechaLiberacion,
                PoliticaPremio politicaPremio, double tarifaInscripcion) {

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
