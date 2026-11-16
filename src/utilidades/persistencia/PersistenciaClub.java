package utilidades.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import clubdeportivo.ClubDeportivo;
import clubdeportivo.competencias.Competencia;
import clubdeportivo.competencias.FormatoCompetencia;
import clubdeportivo.competencias.InscripcionCompetencia;
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
import clubdeportivo.entrenamientos.EstadoAsistencia;
import clubdeportivo.entrenamientos.RegistroAsistencia;
import clubdeportivo.entrenamientos.SesionEntrenamiento;
import clubdeportivo.entrenamientos.TipoSesion;
import clubdeportivo.instalaciones.Instalacion;
import clubdeportivo.instalaciones.Reserva;
import clubdeportivo.instalaciones.TipoInstalacion;
import clubdeportivo.membresias.PagoMensualidad;
import clubdeportivo.torneos.BonoDescuento;
import clubdeportivo.torneos.EncuentroTorneo;
import clubdeportivo.torneos.EstadoBono;
import clubdeportivo.torneos.EstadoEncuentro;
import clubdeportivo.torneos.EstadoInscripcionTorneo;
import clubdeportivo.torneos.EstadoTorneo;
import clubdeportivo.torneos.FormatoTorneo;
import clubdeportivo.torneos.InscripcionEquipoTorneo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.InscripcionTorneo;
import clubdeportivo.torneos.ParticipanteTorneo;
import clubdeportivo.torneos.PoliticaPremio;
import clubdeportivo.torneos.PosicionTorneo;
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
import clubdeportivo.turnos.EstadoSolicitud;
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
import tiendaclub.ventas.descuentos.DescuentoCodigoCompartido;
import tiendaclub.ventas.descuentos.DescuentoBonoTorneo;
import tiendaclub.ventas.descuentos.DescuentoPuntos;
import tiendaclub.ventas.descuentos.DescuentoEmpleado;
import tiendaclub.ventas.descuentos.PoliticaDescuento;
import tiendaclub.ventas.descuentos.SinDescuento;
import tiendaclub.ventas.detalles.DetalleVenta;
import tiendaclub.ventas.tipos.VentaCafeteria;
import tiendaclub.ventas.tipos.VentaTienda;
import usuarios.Comprador;
import usuarios.Usuario;
import usuarios.tipos.Empleado;
import usuarios.tipos.EstudianteUniversidad;
import usuarios.tipos.ParticipanteExterno;
import usuarios.tipos.Socio;
import usuarios.tipos.empleados.Administrador;
import usuarios.tipos.empleados.Entrenador;
import usuarios.tipos.empleados.Fisioterapeuta;

public class PersistenciaClub {
    private final Path carpeta;
    private final Path archivoSocios;
    private final Path archivoDeportesConjunto;
    private final Path archivoDeportesIndividuales;
    private final Path archivoEquipos;
    private final Path archivoInscripcionesEquipo;
    private final Path archivoInscripcionesAcademia;
    private final Path archivoProductos;
    private final Path archivoInventario;
    private final Path archivoMovimientos;
    private final Path archivoPagos;
    private final Path archivoInstalaciones;
    private final Path archivoReservas;
    private final Path archivoVentas;
    private final Path archivoEmpleados;
    private final Path archivoSolicitudesTurno;
    private final Path archivoTurnos;
    private final Path archivoCompetencias;
    private final Path archivoInscripcionesCompetencia;
    private final Path archivoPartidos;
    private final Path archivoEstadisticas;
    private final Path archivoSesiones;
    private final Path archivoAsistencias;
    private final Path archivoSugerencias;
    private final Path archivoEstudiantes;
    private final Path archivoParticipantesExternos;
    private final Path archivoTorneos;
    private final Path archivoInscripcionesTorneo;
    private final Path archivoEncuentrosTorneo;
    private final Path archivoPosicionesTorneo;
    private final Path archivoPremiosTorneo;

    public PersistenciaClub() {
        this(Paths.get("datos"));
    }

    public PersistenciaClub(Path carpeta) {
        if (carpeta == null) {
            throw new IllegalArgumentException(
                    "La carpeta de persistencia es obligatoria");
        }

        this.carpeta = carpeta;
        archivoSocios = carpeta.resolve("socios.txt");
        archivoDeportesConjunto = carpeta.resolve("deportes_conjunto.txt");
        archivoDeportesIndividuales = carpeta.resolve("deportes_individuales.txt");
        archivoEquipos = carpeta.resolve("equipos.txt");
        archivoInscripcionesEquipo = carpeta.resolve("inscripciones_equipo.txt");
        archivoInscripcionesAcademia = carpeta.resolve("inscripciones_academia.txt");
        archivoProductos = carpeta.resolve("productos.txt");
        archivoInventario = carpeta.resolve("inventario.txt");
        archivoMovimientos = carpeta.resolve("movimientos.txt");
        archivoPagos = carpeta.resolve("pagos.txt");
        archivoInstalaciones = carpeta.resolve("instalaciones.txt");
        archivoReservas = carpeta.resolve("reservas.txt");
        archivoVentas = carpeta.resolve("ventas.txt");
        archivoEmpleados = carpeta.resolve("empleados.txt");
        archivoSolicitudesTurno = carpeta.resolve("solicitudes_turno.txt");
        archivoTurnos = carpeta.resolve("turnos.txt");
        archivoCompetencias = carpeta.resolve("competencias.txt");
        archivoInscripcionesCompetencia = carpeta.resolve("inscripciones_competencia.txt");
        archivoPartidos = carpeta.resolve("partidos.txt");
        archivoEstadisticas = carpeta.resolve("estadisticas.txt");
        archivoSesiones = carpeta.resolve("sesiones.txt");
        archivoAsistencias = carpeta.resolve("asistencias.txt");
        archivoSugerencias = carpeta.resolve("sugerencias.txt");
        archivoEstudiantes = carpeta.resolve("estudiantes.txt");
        archivoParticipantesExternos = carpeta.resolve("participantes_externos.txt");
        archivoTorneos = carpeta.resolve("torneos.txt");
        archivoInscripcionesTorneo = carpeta.resolve("inscripciones_torneo.txt");
        archivoEncuentrosTorneo = carpeta.resolve("encuentros_torneo.txt");
        archivoPosicionesTorneo = carpeta.resolve("posiciones_torneo.txt");
        archivoPremiosTorneo = carpeta.resolve("premios_torneo.txt");
    }

    public void guardar(ClubDeportivo club) throws IOException {
        if (club == null) {
            throw new IllegalArgumentException("El club es obligatorio");
        }

        Files.createDirectories(carpeta);
        guardarSocios(club);
        guardarEmpleados(club);
        guardarEstudiantes(club);
        guardarParticipantesExternos(club);
        guardarTurnos(club);
        guardarSolicitudesTurno(club);
        guardarDeportesConjunto(club);
        guardarDeportesIndividuales(club);
        guardarEquipos(club);
        guardarInscripcionesEquipo(club);
        guardarInscripcionesAcademia(club);
        guardarCompetencias(club);
        guardarInscripcionesCompetencia(club);
        guardarPartidos(club);
        guardarEstadisticas(club);
        guardarProductos(club);
        guardarInventario(club);
        guardarMovimientos(club);
        guardarPagos(club);
        guardarInstalaciones(club);
        guardarReservas(club);
        guardarSesiones(club);
        guardarAsistencias(club);
        guardarSugerencias(club);
        guardarVentas(club);
        guardarTorneos(club);
        guardarInscripcionesTorneo(club);
        guardarEncuentrosTorneo(club);
        guardarPosicionesTorneo(club);
        guardarPremiosTorneo(club);
    }

    public void cargar(ClubDeportivo club) throws IOException {
        if (club == null) {
            throw new IllegalArgumentException("El club es obligatorio");
        }

        Files.createDirectories(carpeta);

        // Un dato inválido no debe cerrar el programa: se informa como error de lectura
        // conservando la causa original (guía 4.4, capturar y re-lanzar)
        try {
            cargarSocios(club);
            cargarEmpleados(club);
            cargarEstudiantes(club);
            cargarParticipantesExternos(club);
            cargarTurnos(club);
            cargarSolicitudesTurno(club);
            cargarDeportesConjunto(club);
            cargarDeportesIndividuales(club);
            cargarEquipos(club);
            cargarInscripcionesEquipo(club);
            cargarInscripcionesAcademia(club);
            cargarCompetencias(club);
            cargarInscripcionesCompetencia(club);
            cargarPartidos(club);
            cargarEstadisticas(club);
            cargarProductos(club);
            cargarInventario(club);
            cargarInstalaciones(club);
            cargarMovimientos(club);
            cargarPagos(club);
            cargarReservas(club);
            cargarSesiones(club);
            cargarAsistencias(club);
            cargarSugerencias(club);
            cargarTorneos(club);
            cargarInscripcionesTorneo(club);
            cargarEncuentrosTorneo(club);
            cargarPosicionesTorneo(club);
            cargarPremiosTorneo(club);
            cargarVentas(club);

            // Los partidos solo se pueden reconstruir con equipos e inscripciones activas;
            // por eso las bajas y retiros se aplican al final
            cargarEstadosInscripcionesCompetencia(club);
            cargarEstadosEquipos(club);
        } catch (RuntimeException e) {
            throw new IOException(
                    "Los archivos de datos tienen información inválida: "
                            + e.getMessage(), e);
        }
    }

    // Socios

    private void guardarSocios(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoSocios)) {
            for (Usuario usuario : club.getUsuarios()) {
                if (usuario instanceof Socio) {
                    Socio socio = (Socio) usuario;

                    escritor.write(socio.getId() + ";"
                            + socio.getNombre() + ";"
                            + socio.getFechaNacimiento() + ";"
                            + socio.getLogin() + ";"
                            + socio.getContrasena() + ";"
                            + socio.getPuntosFidelidad());

                    escritor.newLine();
                }
            }
        }
    }

    private void cargarSocios(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoSocios)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoSocios)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 6) {
                    continue;
                }

                Socio socio = new Socio(
                        datos[0],
                        datos[1],
                        LocalDate.parse(datos[2]),
                        datos[3],
                        datos[4]);

                socio.agregarPuntos(Double.parseDouble(datos[5]));
                club.registrarUsuario(socio);
            }
        }
    }

    // Deportes de conjunto

    private void guardarDeportesConjunto(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoDeportesConjunto)) {
            for (DeporteConjunto deporte : club.getDeportesConjunto()) {
                StringBuilder posiciones = new StringBuilder();

                for (Posicion posicion : deporte.getPosiciones()) {
                    if (posiciones.length() > 0) {
                        posiciones.append(",");
                    }

                    posiciones.append(posicion.getNombre());
                }

                escritor.write(deporte.getNombre() + ";"
                        + deporte.getMinimoJugadores() + ";"
                        + posiciones + ";"
                        + deporte.getPuntosVictoria() + ";"
                        + deporte.getPuntosEmpate() + ";"
                        + deporte.isPermiteEmpate());

                escritor.newLine();
            }
        }
    }

    private void cargarDeportesConjunto(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoDeportesConjunto)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoDeportesConjunto)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 6) {
                    continue;
                }

                DeporteConjunto deporte = new DeporteConjunto(
                        datos[0],
                        Integer.parseInt(datos[1]),
                        Integer.parseInt(datos[3]),
                        Integer.parseInt(datos[4]),
                        Boolean.parseBoolean(datos[5]));

                if (!datos[2].trim().isEmpty()) {
                    String[] posiciones = datos[2].split(",");

                    for (String nombrePosicion : posiciones) {
                        deporte.agregarPosicion(
                                new Posicion(nombrePosicion));
                    }
                }

                club.registrarDeporteConjunto(deporte);
            }
        }
    }

    // Deportes individuales

    private void guardarDeportesIndividuales(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoDeportesIndividuales)) {
            for (DeporteIndividual deporte : club.getDeportesIndividuales()) {
                escritor.write(deporte.getNombre() + ";"
                        + deporte.getMaximoInstalaciones());

                escritor.newLine();
            }
        }
    }

    private void cargarDeportesIndividuales(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoDeportesIndividuales)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoDeportesIndividuales)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 2) {
                    continue;
                }

                DeporteIndividual deporte = new DeporteIndividual(
                        datos[0],
                        Integer.parseInt(datos[1]));

                club.registrarDeporteIndividual(deporte);
            }
        }
    }

    // Equipos

    private void guardarEquipos(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoEquipos)) {
            for (DeporteConjunto deporte : club.getDeportesConjunto()) {
                for (Equipo equipo : deporte.getEquipos()) {
                    escritor.write(deporte.getNombre() + ";"
                            + equipo.getNombre() + ";"
                            + equipo.getCategoria() + ";"
                            + equipo.getCupoMaximo() + ";"
                            + equipo.isActivo() + ";"
                            + equipo.getPuntajeHistorico());

                    escritor.newLine();
                }
            }
        }
    }

    private void cargarEquipos(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoEquipos)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoEquipos)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 5) {
                    continue;
                }

                DeporteConjunto deporte = club.buscarDeporteConjunto(datos[0]);

                if (deporte == null) {
                    continue;
                }

                Equipo equipo = new Equipo(
                        datos[1],
                        Categoria.valueOf(datos[2]),
                        Integer.parseInt(datos[3]),
                        deporte);

                if (datos.length >= 6) {
                    equipo.actualizarPuntajeHistorico(
                            Double.parseDouble(datos[5]));
                }

                club.registrarEquipo(equipo);
            }
        }
    }

    private void cargarEstadosEquipos(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoEquipos)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoEquipos)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 5) {
                    continue;
                }

                DeporteConjunto deporte = club.buscarDeporteConjunto(datos[0]);

                Equipo equipo = club.buscarEquipo(datos[1]);

                if (deporte == null || equipo == null
                        || equipo.getDeporte() != deporte) {
                    continue;
                }

                if (!Boolean.parseBoolean(datos[4])) {
                    equipo.desactivar();
                }
            }
        }
    }

    // Inscripciones en equipos

    private void guardarInscripcionesEquipo(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoInscripcionesEquipo)) {
            for (DeporteConjunto deporte : club.getDeportesConjunto()) {
                for (Equipo equipo : deporte.getEquipos()) {
                    for (InscripcionEquipo inscripcion : equipo.getInscripciones()) {
                        escritor.write(inscripcion.getSocio().getId() + ";"
                                + deporte.getNombre() + ";"
                                + equipo.getNombre() + ";"
                                + inscripcion.getPosicion().getNombre() + ";"
                                + inscripcion.getNumeroCamiseta() + ";"
                                + inscripcion.getEstado() + ";"
                                + inscripcion.getPartidosSuspension());

                        escritor.newLine();
                    }
                }
            }
        }
    }

    private void cargarInscripcionesEquipo(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoInscripcionesEquipo)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoInscripcionesEquipo)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 6) {
                    continue;
                }

                Usuario usuario = club.buscarUsuario(datos[0]);

                DeporteConjunto deporte = club.buscarDeporteConjunto(datos[1]);

                Equipo equipo = club.buscarEquipo(datos[2]);

                if (!(usuario instanceof Socio)
                        || deporte == null
                        || equipo == null
                        || equipo.getDeporte() != deporte) {
                    continue;
                }

                Posicion posicion = null;

                for (Posicion existente : deporte.getPosiciones()) {
                    if (existente.getNombre()
                            .equalsIgnoreCase(datos[3])) {
                        posicion = existente;
                        break;
                    }
                }

                if (posicion == null) {
                    continue;
                }

                InscripcionEquipo inscripcion = club.cargarInscripcionEquipo(
                        (Socio) usuario,
                        equipo,
                        posicion,
                        Integer.parseInt(datos[4]));

                EstadoInscripcion estado = EstadoInscripcion.valueOf(datos[5]);

                if (estado == EstadoInscripcion.SUSPENDIDA) {
                    int partidos = datos.length >= 7
                            ? Integer.parseInt(datos[6])
                            : InscripcionEquipo.PARTIDOS_SUSPENSION_POR_TARJETAS;

                    inscripcion.suspender(Math.max(1, partidos));
                } else if (estado == EstadoInscripcion.RETIRADA) {
                    inscripcion.retirar();
                }
            }
        }
    }

    // Inscripciones en academias

    private void guardarInscripcionesAcademia(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoInscripcionesAcademia)) {
            for (DeporteIndividual deporte : club.getDeportesIndividuales()) {
                for (InscripcionAcademia inscripcion : deporte.getInscripciones()) {
                    escritor.write(inscripcion.getSocio().getId() + ";"
                            + deporte.getNombre() + ";"
                            + inscripcion.getNivel() + ";"
                            + inscripcion.getPuntajeRanking() + ";"
                            + inscripcion.getEstado());

                    escritor.newLine();
                }
            }
        }
    }

    private void cargarInscripcionesAcademia(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoInscripcionesAcademia)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoInscripcionesAcademia)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 5) {
                    continue;
                }

                Usuario usuario = club.buscarUsuario(datos[0]);

                DeporteIndividual deporte = club.buscarDeporteIndividual(datos[1]);

                if (!(usuario instanceof Socio)
                        || deporte == null) {
                    continue;
                }

                InscripcionAcademia inscripcion = club.inscribirSocioEnAcademia(
                        (Socio) usuario,
                        deporte,
                        Nivel.valueOf(datos[2]));

                inscripcion.actualizarPuntaje(
                        Double.parseDouble(datos[3]));

                EstadoInscripcion estado = EstadoInscripcion.valueOf(datos[4]);

                if (estado == EstadoInscripcion.SUSPENDIDA) {
                    inscripcion.suspender();
                } else if (estado == EstadoInscripcion.RETIRADA) {
                    inscripcion.retirar();
                }
            }
        }
    }

    // Productos

    private void guardarProductos(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoProductos)) {
            for (Producto producto : club.getProductos()) {
                if (producto instanceof ArticuloTienda) {
                    ArticuloTienda articulo = (ArticuloTienda) producto;

                    escritor.write("ARTICULO;"
                            + articulo.getNombre() + ";"
                            + articulo.getPrecio() + ";"
                            + articulo.getCategoria());
                } else if (producto instanceof Bebida) {
                    Bebida bebida = (Bebida) producto;

                    escritor.write("BEBIDA;"
                            + bebida.getNombre() + ";"
                            + bebida.getPrecio() + ";"
                            + bebida.isCaliente());
                } else if (producto instanceof Snack) {
                    Snack snack = (Snack) producto;
                    StringBuilder alergenos = new StringBuilder();

                    for (String alergeno : snack.getAlergenos()) {
                        if (alergenos.length() > 0) {
                            alergenos.append(",");
                        }
                        alergenos.append(alergeno);
                    }

                    escritor.write("SNACK;"
                            + snack.getNombre() + ";"
                            + snack.getPrecio() + ";"
                            + alergenos);
                } else {
                    continue;
                }

                escritor.newLine();
            }
        }
    }

    private void cargarProductos(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoProductos)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoProductos)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 3) {
                    continue;
                }

                Producto producto;

                if (datos.length >= 4 && datos[0].equals("ARTICULO")) {
                    producto = new ArticuloTienda(
                            datos[1], Double.parseDouble(datos[2]), datos[3]);
                } else if (datos.length >= 4 && datos[0].equals("BEBIDA")) {
                    producto = new Bebida(
                            datos[1], Double.parseDouble(datos[2]),
                            Boolean.parseBoolean(datos[3]));
                } else if (datos.length >= 4 && datos[0].equals("SNACK")) {
                    List<String> alergenos = new ArrayList<String>();

                    if (!datos[3].trim().isEmpty()) {
                        String[] listaAlergenos = datos[3].split(",");

                        for (String alergeno : listaAlergenos) {
                            alergenos.add(alergeno);
                        }
                    }

                    producto = new Snack(
                            datos[1], Double.parseDouble(datos[2]), alergenos);
                } else {
                    producto = new ArticuloTienda(
                            datos[0], Double.parseDouble(datos[1]), datos[2]);
                }

                club.registrarProducto(producto);
            }
        }
    }

    // Inventario

    private void guardarInventario(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoInventario)) {
            for (UbicacionInventario ubicacion : club.getUbicacionesInventario()) {

                for (ExistenciaInventario existencia : ubicacion.getExistencias()) {

                    escritor.write(
                            existencia.getProducto().getNombre() + ";"
                                    + ubicacion.getNombre() + ";"
                                    + existencia.getCantidad());

                    escritor.newLine();
                }
            }
        }
    }

    private void cargarInventario(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoInventario)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoInventario)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 3) {
                    continue;
                }

                Producto producto = club.buscarProducto(datos[0]);

                if (producto == null) {
                    continue;
                }

                UbicacionInventario ubicacion = club.buscarUbicacionInventario(datos[1]);

                if (ubicacion == null) {
                    ubicacion = new UbicacionInventario(datos[1]);

                    club.registrarUbicacionInventario(ubicacion);
                }

                ExistenciaInventario existencia = new ExistenciaInventario(
                        producto,
                        ubicacion,
                        Integer.parseInt(datos[2]));

                ubicacion.agregarExistencia(existencia);
            }
        }
    }

    // Movimientos

    private void guardarMovimientos(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoMovimientos)) {
            for (MovimientoInventario movimiento : club.getMovimientosInventario()) {

                String origen = "";
                String destino = "";

                if (movimiento.getOrigen() != null) {
                    origen = movimiento.getOrigen().getNombre();
                }

                if (movimiento.getDestino() != null) {
                    destino = movimiento.getDestino().getNombre();
                }

                escritor.write(movimiento.getFecha() + ";"
                        + movimiento.getCantidad() + ";"
                        + movimiento.getTipo() + ";"
                        + movimiento.getProducto().getNombre() + ";"
                        + origen + ";"
                        + destino);

                escritor.newLine();
            }
        }
    }

    private void cargarMovimientos(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoMovimientos)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoMovimientos)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 6) {
                    continue;
                }

                Producto producto = club.buscarProducto(datos[3]);

                if (producto == null) {
                    continue;
                }

                UbicacionInventario origen = null;
                UbicacionInventario destino = null;

                if (!datos[4].isEmpty()) {
                    origen = club.buscarUbicacionInventario(datos[4]);
                }

                if (!datos[5].isEmpty()) {
                    destino = club.buscarUbicacionInventario(datos[5]);
                }

                MovimientoInventario movimiento = new MovimientoInventario(
                        LocalDate.parse(datos[0]),
                        Integer.parseInt(datos[1]),
                        TipoMovimiento.valueOf(datos[2]),
                        producto,
                        origen,
                        destino);

                club.registrarMovimiento(movimiento);
            }
        }
    }

    // Pagos

    private void guardarPagos(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoPagos)) {
            for (PagoMensualidad pago : club.getPagosMensualidad()) {

                escritor.write(pago.getFecha() + ";"
                        + pago.getPeriodo() + ";"
                        + pago.getValor() + ";"
                        + pago.getSocio().getId());

                escritor.newLine();
            }
        }
    }

    private void cargarPagos(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoPagos)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoPagos)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 4) {
                    continue;
                }

                Usuario usuario = club.buscarUsuario(datos[3]);

                if (!(usuario instanceof Socio)) {
                    continue;
                }

                PagoMensualidad pago = new PagoMensualidad(
                        LocalDate.parse(datos[0]),
                        datos[1],
                        Double.parseDouble(datos[2]),
                        (Socio) usuario);

                club.registrarPagoMensualidad(pago);
            }
        }
    }

    // Instalaciones

    private void guardarInstalaciones(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoInstalaciones)) {
            for (Instalacion instalacion : club.getInstalaciones()) {

                escritor.write(instalacion.getTipo() + ";"
                        + instalacion.getId() + ";"
                        + instalacion.getCapacidadMaxima() + ";"
                        + instalacion.isTechada() + ";"
                        + (instalacion.getDisciplina() == null ? "" : instalacion.getDisciplina().getNombre()));

                escritor.newLine();
            }
        }
    }

    private void cargarInstalaciones(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoInstalaciones)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoInstalaciones)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 4) {
                    continue;
                }

                DeporteIndividual disciplina = null;

                if (datos.length > 4) {
                    disciplina = club.buscarDeporteIndividual(datos[4]);
                }

                Instalacion instalacion = new Instalacion(
                        datos[1],
                        TipoInstalacion.valueOf(datos[0]),
                        Integer.parseInt(datos[2]),
                        Boolean.parseBoolean(datos[3]),
                        disciplina);

                club.registrarInstalacion(instalacion);
            }
        }
    }

    // Reservas

    private void guardarReservas(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoReservas)) {
            for (Reserva reserva : club.getReservas()) {
                escritor.write(reserva.getSocio().getId() + ";"
                        + reserva.getInstalacion().getId() + ";"
                        + reserva.getFecha() + ";"
                        + reserva.getHoraInicio() + ";"
                        + reserva.getDuracionMinutos() + ";"
                        + reserva.getNumeroJugadores() + ";"
                        + (reserva.getModalidad() == null ? "" : reserva.getModalidad().name()) + ";"
                        + (reserva.getDeporteConjunto() == null
                                ? ""
                                : reserva.getDeporteConjunto().getNombre()));

                escritor.newLine();
            }
        }
    }

    private void cargarReservas(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoReservas)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoReservas)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 7) {
                    continue;
                }

                Usuario usuario = club.buscarUsuario(datos[0]);

                Instalacion instalacion = club.buscarInstalacion(datos[1]);

                LocalDate fecha = LocalDate.parse(datos[2]);

                if (!(usuario instanceof Socio)
                        || instalacion == null) {
                    continue;
                }

                Modalidad modalidad = datos[6].trim().isEmpty()
                        || datos[6].equals("null")
                                ? null
                                : Modalidad.valueOf(datos[6]);

                DeporteConjunto deporte = datos.length >= 8 && !datos[7].trim().isEmpty()
                        ? club.buscarDeporteConjunto(datos[7])
                        : null;

                Reserva reserva = new Reserva(
                        (Socio) usuario,
                        instalacion,
                        fecha,
                        LocalTime.parse(datos[3]),
                        Integer.parseInt(datos[4]),
                        Integer.parseInt(datos[5]),
                        modalidad,
                        deporte);

                club.cargarReserva(reserva);
            }
        }
    }

    // Ventas

    private void guardarVentas(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoVentas)) {
            for (Venta venta : club.getVentas()) {
                if (!(venta.getComprador() instanceof Usuario)) {
                    continue;
                }

                String tipoVenta;

                if (venta instanceof VentaCafeteria) {
                    tipoVenta = "CAFETERIA";
                } else {
                    tipoVenta = "TIENDA";
                }

                String tipoDescuento;
                String datoDescuento = "";

                if (venta.getPoliticaDescuento() instanceof DescuentoBonoTorneo) {
                    tipoDescuento = "BONO_TORNEO";
                    BonoDescuento bono = ((DescuentoBonoTorneo) venta.getPoliticaDescuento()).getBono();
                    datoDescuento = String.valueOf(bono.getSocio().getBonos().indexOf(bono));
                } else if (venta.getPoliticaDescuento() instanceof DescuentoPuntos) {
                    tipoDescuento = "PUNTOS";
                    datoDescuento = String.valueOf(
                            ((DescuentoPuntos) venta.getPoliticaDescuento())
                                    .getPuntosRedimidos());
                } else if (venta.getPoliticaDescuento() instanceof DescuentoCodigoCompartido) {
                    tipoDescuento = "CODIGO_COMPARTIDO";
                    String codigo = ((DescuentoCodigoCompartido) venta.getPoliticaDescuento())
                            .getCodigo();
                    datoDescuento = codigo == null ? "" : codigo;
                } else if (venta.getPoliticaDescuento() instanceof DescuentoEmpleado) {
                    tipoDescuento = "EMPLEADO";
                } else {
                    tipoDescuento = "SIN_DESCUENTO";
                }

                boolean incluyePropina = venta instanceof VentaCafeteria
                        && ((VentaCafeteria) venta)
                                .getPropina() > 0;

                StringBuilder detalles = new StringBuilder();

                for (DetalleVenta detalle : venta.getDetalles()) {

                    if (detalles.length() > 0) {
                        detalles.append("|");
                    }

                    detalles.append(
                            detalle.getProducto().getNombre())
                            .append(",")
                            .append(detalle.getCantidad())
                            .append(",")
                            .append(detalle.getPrecioUnitario());
                }

                Usuario comprador = (Usuario) venta.getComprador();

                escritor.write(venta.getFecha() + ";"
                        + comprador.getId() + ";"
                        + tipoVenta + ";"
                        + tipoDescuento + ";"
                        + datoDescuento + ";"
                        + incluyePropina + ";"
                        + detalles);

                escritor.newLine();
            }
        }
    }

    private void cargarVentas(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoVentas)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoVentas)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 6) {
                    continue;
                }

                boolean formatoNuevo = datos.length >= 7;

                Usuario usuario = club.buscarUsuario(datos[1]);

                if (!(usuario instanceof Comprador)) {
                    continue;
                }

                LocalDate fecha = LocalDate.parse(datos[0]);

                Comprador comprador = (Comprador) usuario;

                boolean usaCodigoCompartido = !formatoNuevo
                        && Boolean.parseBoolean(datos[3]);

                boolean incluyePropina = Boolean.parseBoolean(
                        formatoNuevo ? datos[5] : datos[4]);

                PoliticaDescuento politica = formatoNuevo
                        ? cargarPoliticaDescuento(datos[3], datos[4], usuario)
                        : politicaFormatoAnterior(usaCodigoCompartido, comprador);

                Venta venta;

                if (datos[2].equals("CAFETERIA")) {
                    venta = new VentaCafeteria(fecha, comprador, politica);
                } else {
                    venta = new VentaTienda(fecha, comprador, politica);
                }

                String[] detallesGuardados = datos[formatoNuevo ? 6 : 5].split("\\|");

                for (String detalleGuardado : detallesGuardados) {

                    if (detalleGuardado.trim().isEmpty()) {
                        continue;
                    }

                    String[] datosDetalle = detalleGuardado.split(",", -1);

                    if (datosDetalle.length < 2) {
                        continue;
                    }

                    Producto producto = club.buscarProducto(
                            datosDetalle[0]);

                    if (producto == null) {
                        continue;
                    }

                    DetalleVenta detalle = datosDetalle.length >= 3
                            ? new DetalleVenta(
                                    producto,
                                    Integer.parseInt(datosDetalle[1]),
                                    Double.parseDouble(datosDetalle[2]))
                            : new DetalleVenta(
                                    producto,
                                    Integer.parseInt(datosDetalle[1]));

                    venta.agregarDetalle(detalle);
                }

                if (venta.getDetalles().isEmpty()) {
                    continue;
                }

                if (venta instanceof VentaCafeteria) {
                    ((VentaCafeteria) venta).finalizarVenta(incluyePropina);
                } else {
                    ((VentaTienda) venta).finalizarVenta();
                }

                club.registrarVenta(venta);
            }
        }
    }

    // Ventas guardadas con el formato del Proyecto 1: solo se sabía si se usó un código
    // compartido (sin el código) o si el comprador tenía descuento de empleado
    private PoliticaDescuento politicaFormatoAnterior(boolean usaCodigoCompartido,
            Comprador comprador) {

        if (usaCodigoCompartido) {
            return new DescuentoCodigoCompartido();
        }

        if (comprador.getPorcentajeDescuento() > 0) {
            return new DescuentoEmpleado();
        }

        return new SinDescuento();
    }

    private PoliticaDescuento cargarPoliticaDescuento(
            String tipo, String dato, Usuario comprador) throws IOException {

        if (tipo.equals("CODIGO_COMPARTIDO")) {
            return dato.trim().isEmpty()
                    ? new DescuentoCodigoCompartido()
                    : new DescuentoCodigoCompartido(dato);
        }

        if (tipo.equals("EMPLEADO")) {
            return new DescuentoEmpleado();
        }

        if (tipo.equals("BONO_TORNEO")) {
            if (!(comprador instanceof Socio)) {
                throw new IOException("La venta con bono no pertenece a un socio");
            }

            List<BonoDescuento> bonos = ((Socio) comprador).getBonos();
            int indice = Integer.parseInt(dato);

            if (indice < 0 || indice >= bonos.size()) {
                throw new IOException("No existe el bono utilizado por la venta");
            }

            return new DescuentoBonoTorneo(bonos.get(indice), true);
        }

        if (tipo.equals("PUNTOS")) {
            return new DescuentoPuntos(Double.parseDouble(dato));
        }

        return new SinDescuento();
    }

    // Estadísticas de jugadores

    private void guardarEstadisticas(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoEstadisticas)) {
            int numeroPartido = 1;

            for (Partido partido : club.getPartidos()) {
                for (EstadisticaJugador estadistica : partido.getEstadisticas()) {
                    String tipo;
                    String valores;

                    if (estadistica instanceof EstadisticaBaloncesto) {
                        EstadisticaBaloncesto dato = (EstadisticaBaloncesto) estadistica;
                        tipo = "BALONCESTO";
                        valores = dato.getPuntos() + "," + dato.getRebotes();
                    } else if (estadistica instanceof EstadisticaFutbol) {
                        EstadisticaFutbol dato = (EstadisticaFutbol) estadistica;
                        tipo = "FUTBOL";
                        valores = dato.getGoles() + ","
                                + dato.getAsistencias() + ","
                                + dato.getTarjetasAmarillas() + ","
                                + dato.getTarjetasRojas();
                    } else if (estadistica instanceof EstadisticaTenisMesa) {
                        EstadisticaTenisMesa dato = (EstadisticaTenisMesa) estadistica;
                        tipo = "TENIS_MESA";
                        valores = dato.getSetsGanados() + ","
                                + dato.getPuntosTotales();
                    } else if (estadistica instanceof EstadisticaTenisPadel) {
                        EstadisticaTenisPadel dato = (EstadisticaTenisPadel) estadistica;
                        tipo = "TENIS_PADEL";
                        valores = dato.getSetsGanados() + ","
                                + dato.getGamesGanados() + ","
                                + dato.getAces() + ","
                                + dato.getErroresNoForzados();
                    } else if (estadistica instanceof EstadisticaVoleibol) {
                        EstadisticaVoleibol dato = (EstadisticaVoleibol) estadistica;
                        tipo = "VOLEIBOL";
                        valores = dato.getAces() + "," + dato.getBloqueos();
                    } else {
                        continue;
                    }

                    escritor.write(numeroPartido + ";"
                            + estadistica.getJugador().getId() + ";"
                            + tipo + ";"
                            + valores);
                    escritor.newLine();
                }

                numeroPartido++;
            }
        }
    }

    private void cargarEstadisticas(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoEstadisticas)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoEstadisticas)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 4) {
                    continue;
                }

                int indicePartido = Integer.parseInt(datos[0]) - 1;

                if (indicePartido < 0 || indicePartido >= club.getPartidos().size()) {
                    continue;
                }

                Usuario usuario = club.buscarUsuario(datos[1]);

                if (!(usuario instanceof Socio)) {
                    continue;
                }

                Partido partido = club.getPartidos().get(indicePartido);
                Socio socio = (Socio) usuario;
                String[] valores = datos[3].split(",", -1);
                EstadisticaJugador estadistica;

                if (datos[2].equals("BALONCESTO") && valores.length >= 2) {
                    EstadisticaBaloncesto dato = new EstadisticaBaloncesto(socio, partido);
                    dato.agregarPuntos(Integer.parseInt(valores[0]));

                    for (int i = 0; i < Integer.parseInt(valores[1]); i++) {
                        dato.registrarRebote();
                    }

                    estadistica = dato;
                } else if (datos[2].equals("FUTBOL") && valores.length >= 4) {
                    EstadisticaFutbol dato = new EstadisticaFutbol(socio, partido);

                    for (int i = 0; i < Integer.parseInt(valores[0]); i++) {
                        dato.registrarGol();
                    }
                    for (int i = 0; i < Integer.parseInt(valores[1]); i++) {
                        dato.registrarAsistencia();
                    }
                    for (int i = 0; i < Integer.parseInt(valores[2]); i++) {
                        dato.registrarTarjetaAmarilla();
                    }
                    for (int i = 0; i < Integer.parseInt(valores[3]); i++) {
                        dato.registrarTarjetaRoja();
                    }

                    estadistica = dato;
                } else if (datos[2].equals("TENIS_MESA") && valores.length >= 2) {
                    EstadisticaTenisMesa dato = new EstadisticaTenisMesa(socio, partido);

                    for (int i = 0; i < Integer.parseInt(valores[0]); i++) {
                        dato.registrarSetGanado();
                    }
                    dato.agregarPuntos(Integer.parseInt(valores[1]));
                    estadistica = dato;
                } else if (datos[2].equals("TENIS_PADEL") && valores.length >= 4) {
                    EstadisticaTenisPadel dato = new EstadisticaTenisPadel(socio, partido);

                    for (int i = 0; i < Integer.parseInt(valores[0]); i++) {
                        dato.registrarSetGanado();
                    }
                    for (int i = 0; i < Integer.parseInt(valores[1]); i++) {
                        dato.registrarGameGanado();
                    }
                    for (int i = 0; i < Integer.parseInt(valores[2]); i++) {
                        dato.registrarAce();
                    }
                    for (int i = 0; i < Integer.parseInt(valores[3]); i++) {
                        dato.registrarErrorNoForzado();
                    }

                    estadistica = dato;
                } else if (datos[2].equals("VOLEIBOL") && valores.length >= 2) {
                    EstadisticaVoleibol dato = new EstadisticaVoleibol(socio, partido);

                    for (int i = 0; i < Integer.parseInt(valores[0]); i++) {
                        dato.registrarAce();
                    }
                    for (int i = 0; i < Integer.parseInt(valores[1]); i++) {
                        dato.registrarBloqueo();
                    }

                    estadistica = dato;
                } else {
                    continue;
                }

                partido.cargarEstadistica(estadistica);
            }
        }
    }

    // Sesiones de entrenamiento

    private void guardarSesiones(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoSesiones)) {
            int numero = 1;

            for (SesionEntrenamiento sesion : club.getSesionesEntrenamiento()) {
                String equipo = sesion.getEquipo() == null
                        ? ""
                        : sesion.getEquipo().getNombre();
                String deporte = sesion.getDeporteIndividual() == null
                        ? ""
                        : sesion.getDeporteIndividual().getNombre();

                escritor.write(numero + ";"
                        + sesion.getFecha() + ";"
                        + sesion.getHoraInicio() + ";"
                        + sesion.getDuracionMinutos() + ";"
                        + sesion.getEntrenador().getId() + ";"
                        + sesion.getInstalacion().getId() + ";"
                        + sesion.getTipo() + ";"
                        + equipo + ";"
                        + deporte);
                escritor.newLine();
                numero++;
            }
        }
    }

    private void cargarSesiones(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoSesiones)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoSesiones)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 9) {
                    continue;
                }

                Usuario usuario = club.buscarUsuario(datos[4]);
                Instalacion instalacion = club.buscarInstalacion(datos[5]);

                if (!(usuario instanceof Entrenador) || instalacion == null) {
                    continue;
                }

                SesionEntrenamiento sesion = SesionEntrenamiento.crearDesdePersistencia(
                        LocalDate.parse(datos[1]),
                        LocalTime.parse(datos[2]),
                        Integer.parseInt(datos[3]),
                        (Entrenador) usuario,
                        instalacion,
                        TipoSesion.valueOf(datos[6]));

                if (!datos[7].trim().isEmpty()) {
                    Equipo equipo = club.buscarEquipo(datos[7]);

                    if (equipo == null) {
                        continue;
                    }

                    sesion.asignarEquipoDesdePersistencia(equipo);
                } else {
                    DeporteIndividual deporte = club.buscarDeporteIndividual(datos[8]);

                    if (deporte == null) {
                        continue;
                    }

                    sesion.asignarDeporteIndividual(deporte);
                }

                club.cargarSesion(sesion);
            }
        }
    }

    private void guardarAsistencias(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoAsistencias)) {
            int numeroSesion = 1;

            for (SesionEntrenamiento sesion : club.getSesionesEntrenamiento()) {
                for (RegistroAsistencia registro : sesion.getRegistros()) {
                    String nota = registro.getNotaDesempeno() == null
                            ? ""
                            : registro.getNotaDesempeno().toString();
                    String comentario = registro.getComentario() == null
                            ? ""
                            : registro.getComentario().replace(";", ",");

                    escritor.write(numeroSesion + ";"
                            + registro.getSocio().getId() + ";"
                            + registro.getEstado() + ";"
                            + nota + ";"
                            + comentario);
                    escritor.newLine();
                }

                numeroSesion++;
            }
        }
    }

    private void cargarAsistencias(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoAsistencias)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoAsistencias)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 5) {
                    continue;
                }

                int indiceSesion = Integer.parseInt(datos[0]) - 1;

                if (indiceSesion < 0
                        || indiceSesion >= club.getSesionesEntrenamiento().size()) {
                    continue;
                }

                Usuario usuario = club.buscarUsuario(datos[1]);

                if (!(usuario instanceof Socio)) {
                    continue;
                }

                SesionEntrenamiento sesion = club.getSesionesEntrenamiento()
                        .get(indiceSesion);
                RegistroAsistencia registro = new RegistroAsistencia(
                        sesion,
                        (Socio) usuario,
                        EstadoAsistencia.PENDIENTE);

                sesion.agregarRegistroDesdePersistencia(
                        registro);
                EstadoAsistencia estado = EstadoAsistencia.valueOf(datos[2]);

                if (estado != EstadoAsistencia.PENDIENTE) {
                    registro.registrarAsistencia(estado);
                }

                if (!datos[3].trim().isEmpty()) {
                    registro.registrarEvaluacion(
                            Double.parseDouble(datos[3]), datos[4]);
                }
            }
        }
    }

    // Sugerencias de productos

    private void guardarSugerencias(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoSugerencias)) {
            for (SugerenciaProducto sugerencia : club.getSugerenciasProducto()) {
                String idAdministrador = sugerencia.getAdministrador() == null
                        ? ""
                        : sugerencia.getAdministrador().getId();
                String producto = sugerencia.getProductoCreado() == null
                        ? ""
                        : sugerencia.getProductoCreado().getNombre();

                escritor.write(sugerencia.getNombre() + ";"
                        + sugerencia.getDescripcion().replace(";", ",") + ";"
                        + sugerencia.getProponente().getId() + ";"
                        + sugerencia.getEstado() + ";"
                        + idAdministrador + ";"
                        + producto);
                escritor.newLine();
            }
        }
    }

    private void cargarSugerencias(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoSugerencias)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoSugerencias)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 6) {
                    continue;
                }

                Usuario usuario = club.buscarUsuario(datos[2]);

                if (!(usuario instanceof Empleado)) {
                    continue;
                }

                SugerenciaProducto sugerencia = new SugerenciaProducto(
                        datos[0], datos[1], (Empleado) usuario);
                club.registrarSugerencia(sugerencia);
                EstadoSolicitud estado = EstadoSolicitud.valueOf(datos[3]);

                if (estado != EstadoSolicitud.PENDIENTE) {
                    Usuario usuarioAdministrador = club.buscarUsuario(datos[4]);

                    if (!(usuarioAdministrador instanceof Administrador)) {
                        continue;
                    }

                    Administrador administrador = (Administrador) usuarioAdministrador;

                    if (estado == EstadoSolicitud.APROBADA) {
                        Producto producto = club.buscarProducto(datos[5]);

                        if (producto != null) {
                            sugerencia.aprobar(administrador, producto);
                        }
                    } else {
                        sugerencia.rechazar(administrador);
                    }
                }
            }
        }
    }

    // Competencias

    private void guardarCompetencias(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoCompetencias)) {
            for (Competencia competencia : club.getCompetencias()) {
                escritor.write(competencia.getNombre() + ";"
                        + competencia.getOrganizador() + ";"
                        + competencia.getAnio() + ";"
                        + competencia.getCategoria() + ";"
                        + competencia.getTipo() + ";"
                        + competencia.getFormato() + ";"
                        + competencia.getDeporte().getNombre());
                escritor.newLine();
            }
        }
    }

    private void cargarCompetencias(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoCompetencias)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoCompetencias)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 7) {
                    continue;
                }

                DeporteConjunto deporte = club.buscarDeporteConjunto(datos[6]);

                if (deporte == null) {
                    continue;
                }

                Competencia competencia = new Competencia(
                        datos[0],
                        datos[1],
                        Integer.parseInt(datos[2]),
                        Categoria.valueOf(datos[3]),
                        TipoCompetencia.valueOf(datos[4]),
                        FormatoCompetencia.valueOf(datos[5]),
                        deporte);

                club.registrarCompetencia(competencia);
            }
        }
    }

    private void guardarInscripcionesCompetencia(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoInscripcionesCompetencia)) {
            for (Competencia competencia : club.getCompetencias()) {
                for (InscripcionCompetencia inscripcion : competencia.getInscripciones()) {
                    escritor.write(competencia.getNombre() + ";"
                            + competencia.getAnio() + ";"
                            + inscripcion.getEquipo().getNombre() + ";"
                            + inscripcion.getEstado() + ";"
                            + nominaEnTexto(inscripcion) + ";"
                            + inscripcion.getFechaInscripcion());
                    escritor.newLine();
                }
            }
        }
    }

    private String nominaEnTexto(InscripcionCompetencia inscripcion) {
        StringBuilder nomina = new StringBuilder();

        for (Socio socio : inscripcion.getNomina()) {
            if (nomina.length() > 0) {
                nomina.append(",");
            }

            nomina.append(socio.getId());
        }

        return nomina.toString();
    }

    private void cargarInscripcionesCompetencia(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoInscripcionesCompetencia)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoInscripcionesCompetencia)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 4) {
                    continue;
                }

                Competencia competencia = club.buscarCompetencia(
                        datos[0], Integer.parseInt(datos[1]));
                Equipo equipo = club.buscarEquipo(datos[2]);

                if (competencia == null || equipo == null) {
                    continue;
                }

                LocalDate fechaInscripcion = datos.length >= 6 && !datos[5].trim().isEmpty()
                        ? LocalDate.parse(datos[5])
                        : LocalDate.now();

                InscripcionCompetencia inscripcion = competencia.cargarInscripcion(
                        equipo, fechaInscripcion);

                if (datos.length > 4 && !datos[4].trim().isEmpty()) {
                    for (String idJugador : datos[4].split(",")) {
                        Usuario jugador = club.buscarUsuario(idJugador);

                        if (jugador instanceof Socio) {
                            inscripcion.cargarJugadorNomina((Socio) jugador);
                        }
                    }
                }
            }
        }
    }

    private void cargarEstadosInscripcionesCompetencia(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoInscripcionesCompetencia)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoInscripcionesCompetencia)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 4) {
                    continue;
                }

                Competencia competencia = club.buscarCompetencia(
                        datos[0], Integer.parseInt(datos[1]));
                Equipo equipo = club.buscarEquipo(datos[2]);

                if (competencia == null || equipo == null) {
                    continue;
                }

                InscripcionCompetencia inscripcion = competencia.buscarInscripcion(equipo);
                EstadoInscripcion estado = EstadoInscripcion.valueOf(datos[3]);

                if (estado == EstadoInscripcion.SUSPENDIDA) {
                    inscripcion.suspender();
                } else if (estado == EstadoInscripcion.RETIRADA) {
                    inscripcion.retirar();
                }
            }
        }
    }

    // Partidos

    private void guardarPartidos(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoPartidos)) {
            for (Partido partido : club.getPartidos()) {
                String tipo;
                String datosEspecificos;

                if (partido instanceof PartidoOficial) {
                    PartidoOficial oficial = (PartidoOficial) partido;
                    tipo = "OFICIAL";
                    datosEspecificos = oficial.getEquipo().getNombre() + ","
                            + oficial.getCompetencia().getNombre() + ","
                            + oficial.getCompetencia().getAnio() + ","
                            + oficial.getInstitucionRival() + ","
                            + oficial.getLugar();
                } else if (partido instanceof PartidoInterno) {
                    PartidoInterno interno = (PartidoInterno) partido;
                    tipo = "INTERNO";
                    datosEspecificos = interno.getEquipoLocal().getNombre() + ","
                            + interno.getEquipoVisitante().getNombre() + ","
                            + interno.getProposito();
                } else if (partido instanceof PartidoPractica) {
                    PartidoPractica practica = (PartidoPractica) partido;
                    tipo = "PRACTICA";
                    StringBuilder participantes = new StringBuilder();

                    for (Usuario participante : practica.getParticipantes()) {
                        if (participantes.length() > 0) {
                            participantes.append(",");
                        }
                        participantes.append(participante.getId());
                    }

                    datosEspecificos = practica.getDeporte().getNombre() + ","
                            + practica.getModalidad() + ","
                            + participantes;
                } else {
                    continue;
                }

                String marcador = partido.getMarcadorFinal() == null
                        ? ""
                        : partido.getMarcadorFinal();

                escritor.write(tipo + ";"
                        + partido.getFecha() + ";"
                        + partido.getEstado() + ";"
                        + marcador + ";"
                        + datosEspecificos);
                escritor.newLine();
            }
        }
    }

    private void cargarPartidos(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoPartidos)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoPartidos)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 5) {
                    continue;
                }

                String[] especificos = datos[4].split(",", -1);
                LocalDate fecha = LocalDate.parse(datos[1]);
                Partido partido;

                if (datos[0].equals("OFICIAL") && especificos.length >= 5) {
                    Equipo equipo = club.buscarEquipo(especificos[0]);
                    Competencia competencia = club.buscarCompetencia(
                            especificos[1], Integer.parseInt(especificos[2]));

                    if (equipo == null || competencia == null) {
                        continue;
                    }

                    partido = new PartidoOficial(fecha, equipo, competencia,
                            especificos[3], especificos[4]);
                } else if (datos[0].equals("INTERNO") && especificos.length >= 3) {
                    Equipo local = club.buscarEquipo(especificos[0]);
                    Equipo visitante = club.buscarEquipo(especificos[1]);

                    if (local == null || visitante == null) {
                        continue;
                    }

                    partido = new PartidoInterno(fecha, local, visitante,
                            PropositoPartido.valueOf(especificos[2]));
                } else if (datos[0].equals("PRACTICA") && especificos.length >= 2) {
                    DeporteIndividual deporte = club.buscarDeporteIndividual(especificos[0]);

                    if (deporte == null) {
                        continue;
                    }

                    PartidoPractica practica = new PartidoPractica(
                            fecha, deporte, Modalidad.valueOf(especificos[1]));

                    for (int i = 2; i < especificos.length; i++) {
                        Usuario usuario = club.buscarUsuario(especificos[i]);

                        if (usuario != null) {
                            practica.cargarParticipante(usuario);
                        }
                    }

                    partido = practica;
                } else {
                    continue;
                }

                club.cargarPartido(partido);

                if (!datos[3].trim().isEmpty()) {
                    partido.registrarResultado(datos[3]);
                }

                EstadoPartido estado = EstadoPartido.valueOf(datos[2]);

                if (estado == EstadoPartido.FINALIZADO) {
                    partido.finalizar();
                } else if (estado == EstadoPartido.CANCELADO) {
                    partido.cancelar();
                }
            }
        }
    }

    // Empleados

    private void guardarEmpleados(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoEmpleados)) {
            for (Usuario usuario : club.getUsuarios()) {
                String tipo;
                String codigoDescuento = "";

                if (usuario instanceof Administrador) {
                    tipo = "ADMINISTRADOR";
                } else if (usuario instanceof Entrenador) {
                    tipo = "ENTRENADOR";
                    codigoDescuento = ((Empleado) usuario).getCodigoDescuento();
                } else if (usuario instanceof Fisioterapeuta) {
                    tipo = "FISIOTERAPEUTA";
                    codigoDescuento = ((Empleado) usuario).getCodigoDescuento();
                } else {
                    continue;
                }

                escritor.write(tipo + ";"
                        + usuario.getId() + ";"
                        + usuario.getNombre() + ";"
                        + usuario.getFechaNacimiento() + ";"
                        + usuario.getLogin() + ";"
                        + usuario.getContrasena() + ";"
                        + codigoDescuento);
                escritor.newLine();
            }
        }
    }

    private void cargarEmpleados(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoEmpleados)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoEmpleados)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 7 || club.buscarUsuario(datos[1]) != null) {
                    continue;
                }

                LocalDate fechaNacimiento = LocalDate.parse(datos[3]);
                Usuario empleado;

                if (datos[0].equals("ADMINISTRADOR")) {
                    empleado = new Administrador(datos[1], datos[2], fechaNacimiento,
                            datos[4], datos[5]);
                } else if (datos[0].equals("ENTRENADOR")) {
                    empleado = new Entrenador(datos[1], datos[2], fechaNacimiento,
                            datos[4], datos[5], datos[6]);
                } else if (datos[0].equals("FISIOTERAPEUTA")) {
                    empleado = new Fisioterapeuta(datos[1], datos[2], fechaNacimiento,
                            datos[4], datos[5], datos[6]);
                } else {
                    continue;
                }

                club.registrarUsuario(empleado);
            }
        }
    }

    // Turnos

    private void guardarTurnos(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoTurnos)) {
            for (Usuario usuario : club.getUsuarios()) {
                if (!(usuario instanceof Empleado)) {
                    continue;
                }

                Empleado empleado = (Empleado) usuario;

                for (Turno turno : empleado.getTurnos()) {
                    escritor.write(empleado.getId() + ";"
                            + turno.getDiaSemana() + ";"
                            + turno.getHoraInicio() + ";"
                            + turno.getHoraFin());
                    escritor.newLine();
                }
            }
        }
    }

    private void cargarTurnos(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoTurnos)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoTurnos)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 4) {
                    continue;
                }

                Usuario usuario = club.buscarUsuario(datos[0]);

                if (!(usuario instanceof Empleado)) {
                    continue;
                }

                Empleado empleado = (Empleado) usuario;
                DiaSemana dia = DiaSemana.valueOf(datos[1]);
                LocalTime inicio = LocalTime.parse(datos[2]);
                LocalTime fin = LocalTime.parse(datos[3]);

                if (buscarTurno(empleado, dia, inicio, fin) == null) {
                    club.asignarTurno(empleado, new Turno(dia, inicio, fin));
                }
            }
        }
    }

    private Turno buscarTurno(Empleado empleado, DiaSemana dia,
            LocalTime inicio, LocalTime fin) {

        for (Turno turno : empleado.getTurnos()) {
            if (turno.getDiaSemana() == dia
                    && turno.getHoraInicio().equals(inicio)
                    && turno.getHoraFin().equals(fin)) {
                return turno;
            }
        }

        return null;
    }

    // Solicitudes de cambio de turno

    private void guardarSolicitudesTurno(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoSolicitudesTurno)) {
            for (SolicitudCambioTurno solicitud : club.getSolicitudesCambioTurno()) {
                String idAdministrador = solicitud.getAdministradorDecision() == null
                        ? ""
                        : solicitud.getAdministradorDecision().getId();

                escritor.write(solicitud.getEmpleadoSolicitante().getId() + ";"
                        + solicitud.getTurnoOriginal().getDiaSemana() + ";"
                        + solicitud.getTurnoOriginal().getHoraInicio() + ";"
                        + solicitud.getTurnoOriginal().getHoraFin() + ";"
                        + solicitud.getNuevoDia() + ";"
                        + solicitud.getNuevaHoraInicio() + ";"
                        + solicitud.getNuevaHoraFin() + ";"
                        + solicitud.getEstado() + ";"
                        + idAdministrador + ";"
                        + solicitud.getFechaSolicitud());
                escritor.newLine();
            }
        }
    }

    private void cargarSolicitudesTurno(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoSolicitudesTurno)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoSolicitudesTurno)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length < 9) {
                    continue;
                }

                Usuario usuario = club.buscarUsuario(datos[0]);

                if (!(usuario instanceof Empleado)) {
                    continue;
                }

                Empleado empleado = (Empleado) usuario;
                DiaSemana diaOriginal = DiaSemana.valueOf(datos[1]);
                LocalTime inicioOriginal = LocalTime.parse(datos[2]);
                LocalTime finOriginal = LocalTime.parse(datos[3]);
                EstadoSolicitud estado = EstadoSolicitud.valueOf(datos[7]);
                Administrador administrador = null;

                if (estado != EstadoSolicitud.PENDIENTE) {
                    Usuario usuarioAdministrador = club.buscarUsuario(datos[8]);

                    if (!(usuarioAdministrador instanceof Administrador)) {
                        continue;
                    }

                    administrador = (Administrador) usuarioAdministrador;
                }

                Turno turnoOriginal = new Turno(
                        diaOriginal, inicioOriginal, finOriginal);
                LocalDate fechaSolicitud = datos.length > 9
                        ? LocalDate.parse(datos[9])
                        : LocalDate.now();
                SolicitudCambioTurno solicitud = SolicitudCambioTurno.cargar(
                        empleado,
                        turnoOriginal,
                        DiaSemana.valueOf(datos[4]),
                        LocalTime.parse(datos[5]),
                        LocalTime.parse(datos[6]),
                        fechaSolicitud,
                        estado,
                        administrador);

                club.cargarSolicitudCambio(solicitud);
            }
        }
    }

    private void guardarEstudiantes(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoEstudiantes)) {
            for (Usuario usuario : club.getUsuarios()) {
                if (usuario instanceof EstudianteUniversidad) {
                    EstudianteUniversidad estudiante = (EstudianteUniversidad) usuario;

                    escritor.write(estudiante.getId() + ";"
                            + estudiante.getNombre() + ";"
                            + estudiante.getFechaNacimiento() + ";"
                            + estudiante.getLogin() + ";"
                            + estudiante.getContrasena() + ";"
                            + estudiante.getUniversidad() + ";"
                            + estudiante.getCorreoInstitucional());

                    escritor.newLine();
                }
            }
        }
    }

    private void cargarEstudiantes(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoEstudiantes)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoEstudiantes)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linea.split(";", -1);

                if (partes.length != 7) {
                    continue;
                }

                if (club.buscarUsuario(partes[0]) != null) {
                    continue;
                }

                EstudianteUniversidad estudiante = new EstudianteUniversidad(
                        partes[0],
                        partes[1],
                        LocalDate.parse(partes[2]),
                        partes[3],
                        partes[4],
                        partes[5],
                        partes[6]);

                club.registrarUsuario(estudiante);
            }
        }
    }

    private void guardarParticipantesExternos(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(
                archivoParticipantesExternos)) {

            for (Usuario usuario : club.getUsuarios()) {
                if (usuario instanceof ParticipanteExterno) {
                    ParticipanteExterno participante = (ParticipanteExterno) usuario;

                    escritor.write(participante.getId() + ";"
                            + participante.getNombre() + ";"
                            + participante.getFechaNacimiento() + ";"
                            + participante.getLogin() + ";"
                            + participante.getContrasena() + ";"
                            + participante.getContacto());

                    escritor.newLine();
                }
            }
        }
    }

    private void cargarParticipantesExternos(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoParticipantesExternos)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(
                archivoParticipantesExternos)) {

            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linea.split(";", -1);

                if (partes.length != 6) {
                    continue;
                }

                if (club.buscarUsuario(partes[0]) != null) {
                    continue;
                }

                ParticipanteExterno participante = new ParticipanteExterno(
                        partes[0],
                        partes[1],
                        LocalDate.parse(partes[2]),
                        partes[3],
                        partes[4],
                        partes[5]);

                club.registrarUsuario(participante);
            }
        }
    }

    // Torneos

    private void guardarTorneos(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoTorneos)) {
            for (Torneo torneo : club.getTorneos()) {
                String clase;
                String deporte;
                String clasificacion;
                String modalidad;

                if (torneo instanceof TorneoConjunto) {
                    TorneoConjunto conjunto = (TorneoConjunto) torneo;
                    clase = "CONJUNTO";
                    deporte = conjunto.getDeporte().getNombre();
                    clasificacion = conjunto.getCategoria().name();
                    modalidad = "";
                } else {
                    TorneoIndividual individual = (TorneoIndividual) torneo;
                    clase = "INDIVIDUAL";
                    deporte = individual.getDeporte().getNombre();
                    clasificacion = individual.getNivel().name();
                    modalidad = individual.getModalidad().name();
                }

                PoliticaPremio politica = torneo.getPoliticaPremio();
                String tipoPolitica;
                double parametroUno;
                int parametroDos;

                if (politica instanceof PremioRecreativo) {
                    PremioRecreativo premio = (PremioRecreativo) politica;
                    tipoPolitica = "RECREATIVO";
                    parametroUno = premio.getPorcentajeBono();
                    parametroDos = premio.getDiasVigencia();
                } else {
                    PremioCompetitivo premio = (PremioCompetitivo) politica;
                    tipoPolitica = "COMPETITIVO";
                    parametroUno = premio.getPorcentajeRecaudo();
                    parametroDos = 0;
                }

                escritor.write(clase + ";"
                        + torneo.getId() + ";"
                        + torneo.getNombre() + ";"
                        + torneo.getFechaInicio() + ";"
                        + torneo.getHoraInicio() + ";"
                        + torneo.getHoraFin() + ";"
                        + torneo.getFormato() + ";"
                        + torneo.getCapacidad() + ";"
                        + torneo.getTipo() + ";"
                        + torneo.getEstado() + ";"
                        + torneo.getFechaLiberacionPreferenciales() + ";"
                        + tipoPolitica + ";"
                        + parametroUno + ";"
                        + parametroDos + ";"
                        + torneo.getTarifaInscripcion() + ";"
                        + torneo.isCuposPreferencialesLiberados() + ";"
                        + deporte + ";"
                        + clasificacion + ";"
                        + modalidad);
                escritor.newLine();
            }
        }
    }

    private void cargarTorneos(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoTorneos)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoTorneos)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linea.split(";", -1);

                if (partes.length != 19 || club.buscarTorneo(partes[1]) != null) {
                    continue;
                }

                PoliticaPremio politica;

                if (partes[11].equals("RECREATIVO")) {
                    politica = new PremioRecreativo(
                            Double.parseDouble(partes[12]),
                            Integer.parseInt(partes[13]));
                } else {
                    politica = new PremioCompetitivo(
                            Double.parseDouble(partes[12]));
                }

                Torneo torneo;

                if (partes[0].equals("CONJUNTO")) {
                    DeporteConjunto deporte = club.buscarDeporteConjunto(partes[16]);

                    if (deporte == null) {
                        continue;
                    }

                    torneo = new TorneoConjunto(
                            partes[1], partes[2], LocalDate.parse(partes[3]),
                            LocalTime.parse(partes[4]), LocalTime.parse(partes[5]),
                            FormatoTorneo.valueOf(partes[6]), Integer.parseInt(partes[7]),
                            TipoTorneo.valueOf(partes[8]), LocalDateTime.parse(partes[10]),
                            politica, Double.parseDouble(partes[14]), deporte,
                            Categoria.valueOf(partes[17]));
                } else {
                    DeporteIndividual deporte = club.buscarDeporteIndividual(partes[16]);

                    if (deporte == null) {
                        continue;
                    }

                    torneo = new TorneoIndividual(
                            partes[1], partes[2], LocalDate.parse(partes[3]),
                            LocalTime.parse(partes[4]), LocalTime.parse(partes[5]),
                            FormatoTorneo.valueOf(partes[6]), Integer.parseInt(partes[7]),
                            TipoTorneo.valueOf(partes[8]), LocalDateTime.parse(partes[10]),
                            politica, Double.parseDouble(partes[14]), deporte,
                            Nivel.valueOf(partes[17]), Modalidad.valueOf(partes[18]));
                }

                club.cargarTorneo(torneo);
                torneo.cargarEstado(
                        EstadoTorneo.valueOf(partes[9]),
                        Boolean.parseBoolean(partes[15]));
            }
        }
    }

    private void guardarInscripcionesTorneo(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoInscripcionesTorneo)) {
            for (Torneo torneo : club.getTorneos()) {
                List<InscripcionTorneo> inscripciones = torneo.getInscripciones();

                for (int indice = 0; indice < inscripciones.size(); indice++) {
                    InscripcionTorneo inscripcion = inscripciones.get(indice);
                    String clase;
                    String participantes;

                    if (inscripcion instanceof InscripcionEquipoTorneo) {
                        clase = "EQUIPO";
                        participantes = ((InscripcionEquipoTorneo) inscripcion)
                                .getEquipo().getNombre();
                    } else {
                        clase = "INDIVIDUAL";
                        participantes = obtenerIdsParticipantes(
                                ((InscripcionIndividualTorneo) inscripcion).getParticipantes());
                    }

                    escritor.write(torneo.getId() + ";"
                            + indice + ";"
                            + clase + ";"
                            + inscripcion.getFechaInscripcion() + ";"
                            + inscripcion.getEstado() + ";"
                            + inscripcion.getTipoCupo() + ";"
                            + inscripcion.getValorPagado() + ";"
                            + participantes);
                    escritor.newLine();
                }
            }
        }
    }

    private String obtenerIdsParticipantes(List<ParticipanteTorneo> participantes) {
        StringBuilder resultado = new StringBuilder();

        for (ParticipanteTorneo participante : participantes) {
            if (resultado.length() > 0) {
                resultado.append(",");
            }

            resultado.append(participante.getId());
        }

        return resultado.toString();
    }

    private void cargarInscripcionesTorneo(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoInscripcionesTorneo)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoInscripcionesTorneo)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linea.split(";", -1);

                if (partes.length != 8) {
                    continue;
                }

                Torneo torneo = club.buscarTorneo(partes[0]);

                if (torneo == null) {
                    continue;
                }

                InscripcionTorneo inscripcion;

                if (partes[2].equals("EQUIPO")) {
                    Equipo equipo = club.buscarEquipo(partes[7]);

                    if (equipo == null) {
                        continue;
                    }

                    inscripcion = new InscripcionEquipoTorneo(
                            LocalDate.parse(partes[3]), torneo,
                            TipoCupo.valueOf(partes[5]), Double.parseDouble(partes[6]), equipo);
                } else {
                    InscripcionIndividualTorneo individual = new InscripcionIndividualTorneo(
                            LocalDate.parse(partes[3]), torneo,
                            TipoCupo.valueOf(partes[5]), Double.parseDouble(partes[6]));

                    if (!partes[7].isEmpty()) {
                        String[] ids = partes[7].split(",");

                        for (String id : ids) {
                            Usuario usuario = club.buscarUsuario(id);

                            if (!(usuario instanceof ParticipanteTorneo)) {
                                throw new IOException("No existe el participante de torneo con ID " + id);
                            }

                            individual.agregarParticipante((ParticipanteTorneo) usuario);
                        }
                    }

                    inscripcion = individual;
                }

                torneo.cargarInscripcion(inscripcion);

                if (EstadoInscripcionTorneo.valueOf(partes[4]) == EstadoInscripcionTorneo.RETIRADA) {
                    inscripcion.retirar();
                }
            }
        }
    }

    private void guardarEncuentrosTorneo(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoEncuentrosTorneo)) {
            for (Torneo torneo : club.getTorneos()) {
                List<InscripcionTorneo> inscripciones = torneo.getInscripciones();

                for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
                    escritor.write(torneo.getId() + ";"
                            + encuentro.getId() + ";"
                            + encuentro.getFecha() + ";"
                            + encuentro.getHoraInicio() + ";"
                            + inscripciones.indexOf(encuentro.getInscripcionLocal()) + ";"
                            + inscripciones.indexOf(encuentro.getInscripcionVisitante()) + ";"
                            + encuentro.getRonda() + ";"
                            + encuentro.getEstado() + ";"
                            + (encuentro.getMarcador() == null ? "" : encuentro.getMarcador()));
                    escritor.newLine();
                }
            }
        }
    }

    private void cargarEncuentrosTorneo(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoEncuentrosTorneo)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoEncuentrosTorneo)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linea.split(";", -1);

                if (partes.length != 8 && partes.length != 9) {
                    continue;
                }

                Torneo torneo = club.buscarTorneo(partes[0]);

                if (torneo == null) {
                    continue;
                }

                InscripcionTorneo local = buscarInscripcionTorneo(
                        torneo, Integer.parseInt(partes[4]));
                InscripcionTorneo visitante = buscarInscripcionTorneo(
                        torneo, Integer.parseInt(partes[5]));

                int desplazamiento = partes.length == 9 ? 1 : 0;
                int ronda = partes.length == 9
                        ? Integer.parseInt(partes[6])
                        : 1;

                EncuentroTorneo encuentro = new EncuentroTorneo(
                        partes[1], LocalDate.parse(partes[2]), LocalTime.parse(partes[3]),
                        local, visitante, ronda);

                EstadoEncuentro estado = EstadoEncuentro.valueOf(partes[6 + desplazamiento]);

                if (estado == EstadoEncuentro.FINALIZADO) {
                    encuentro.registrarResultado(partes[7 + desplazamiento]);
                    encuentro.finalizar();
                } else if (estado == EstadoEncuentro.CANCELADO) {
                    encuentro.cancelar();
                }

                torneo.cargarEncuentro(encuentro);
            }
        }
    }

    private void guardarPosicionesTorneo(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoPosicionesTorneo)) {
            for (Torneo torneo : club.getTorneos()) {
                List<InscripcionTorneo> inscripciones = torneo.getInscripciones();

                for (PosicionTorneo posicion : torneo.getPosiciones()) {
                    escritor.write(torneo.getId() + ";"
                            + inscripciones.indexOf(posicion.getInscripcion()) + ";"
                            + posicion.getPartidosJugados() + ";"
                            + posicion.getVictorias() + ";"
                            + posicion.getEmpates() + ";"
                            + posicion.getDerrotas() + ";"
                            + posicion.getPuntos() + ";"
                            + posicion.getPosicion() + ";"
                            + posicion.getPuntosAFavor() + ";"
                            + posicion.getPuntosEnContra());
                    escritor.newLine();
                }
            }
        }
    }

    private void cargarPosicionesTorneo(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoPosicionesTorneo)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoPosicionesTorneo)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linea.split(";", -1);

                if (partes.length != 8 && partes.length != 10) {
                    continue;
                }

                Torneo torneo = club.buscarTorneo(partes[0]);

                if (torneo == null) {
                    continue;
                }

                int aFavor = partes.length == 10 ? Integer.parseInt(partes[8]) : 0;
                int enContra = partes.length == 10 ? Integer.parseInt(partes[9]) : 0;

                PosicionTorneo posicion = new PosicionTorneo(
                        buscarInscripcionTorneo(torneo, Integer.parseInt(partes[1])),
                        Integer.parseInt(partes[2]), Integer.parseInt(partes[3]),
                        Integer.parseInt(partes[4]), Integer.parseInt(partes[5]),
                        Integer.parseInt(partes[6]), Integer.parseInt(partes[7]),
                        aFavor, enContra);

                torneo.cargarPosicion(posicion);
            }
        }
    }

    private void guardarPremiosTorneo(ClubDeportivo club) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoPremiosTorneo)) {
            for (Torneo torneo : club.getTorneos()) {
                List<InscripcionTorneo> inscripciones = torneo.getInscripciones();

                for (PremioTorneo premio : torneo.getPremiosOtorgados()) {
                    int ganador = inscripciones.indexOf(premio.getGanador());

                    if (premio instanceof PremioMonetario) {
                        PremioMonetario monetario = (PremioMonetario) premio;
                        escritor.write("MONETARIO;" + torneo.getId() + ";"
                                + ganador + ";"
                                + monetario.getValor() + ";"
                                + monetario.getFecha() + ";"
                                + monetario.fueEntregado());
                    } else if (premio instanceof BonoDescuento) {
                        BonoDescuento bono = (BonoDescuento) premio;
                        escritor.write("BONO;" + torneo.getId() + ";"
                                + ganador + ";"
                                + bono.getPorcentaje() + ";"
                                + bono.getSocio().getId() + ";"
                                + bono.getFechaCreacion() + ";"
                                + bono.getFechaVencimiento() + ";"
                                + bono.getEstado());
                    }

                    escritor.newLine();
                }
            }
        }
    }

    private void cargarPremiosTorneo(ClubDeportivo club) throws IOException {
        if (!Files.exists(archivoPremiosTorneo)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(archivoPremiosTorneo)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linea.split(";", -1);
                Torneo torneo = partes.length >= 6 ? club.buscarTorneo(partes[1]) : null;

                if (torneo == null) {
                    continue;
                }

                InscripcionTorneo ganador = buscarInscripcionTorneo(
                        torneo, Integer.parseInt(partes[2]));

                if (partes[0].equals("MONETARIO") && partes.length == 6) {
                    PremioMonetario premio = new PremioMonetario(
                            ganador, Double.parseDouble(partes[3]),
                            LocalDate.parse(partes[4]), Boolean.parseBoolean(partes[5]));
                    torneo.cargarPremio(premio);
                } else if (partes[0].equals("BONO") && partes.length == 8) {
                    Usuario usuario = club.buscarUsuario(partes[4]);

                    if (!(usuario instanceof Socio)) {
                        continue;
                    }

                    BonoDescuento bono = new BonoDescuento(
                            (Socio) usuario, Double.parseDouble(partes[3]),
                            LocalDate.parse(partes[5]), LocalDate.parse(partes[6]),
                            EstadoBono.valueOf(partes[7]), ganador);

                    ((Socio) usuario).agregarBono(bono);
                    torneo.cargarPremio(bono);
                }
            }
        }
    }

    private InscripcionTorneo buscarInscripcionTorneo(Torneo torneo, int indice) throws IOException {
        List<InscripcionTorneo> inscripciones = torneo.getInscripciones();

        if (indice < 0 || indice >= inscripciones.size()) {
            throw new IOException("No existe la inscripción " + indice
                    + " del torneo " + torneo.getId());
        }

        return inscripciones.get(indice);
    }

}