package clubdeportivo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import clubdeportivo.competencias.Competencia;
import clubdeportivo.competencias.InscripcionCompetencia;
import clubdeportivo.partidos.estadisticas.EstadisticaJugador;
import clubdeportivo.partidos.EstadoPartido;
import clubdeportivo.partidos.Partido;
import clubdeportivo.partidos.PartidoInterno;
import clubdeportivo.partidos.PartidoOficial;
import clubdeportivo.partidos.PartidoPractica;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.DeporteIndividual;
import clubdeportivo.deportes.Equipo;
import clubdeportivo.deportes.EstadoInscripcion;
import clubdeportivo.deportes.InscripcionAcademia;
import clubdeportivo.deportes.InscripcionEquipo;
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
import clubdeportivo.turnos.DiaSemana;
import clubdeportivo.turnos.SolicitudCambioTurno;
import clubdeportivo.turnos.Turno;
import tiendaclub.inventario.Bebida;
import tiendaclub.inventario.ExistenciaInventario;
import tiendaclub.inventario.MovimientoInventario;
import tiendaclub.inventario.Producto;
import tiendaclub.inventario.SugerenciaProducto;
import tiendaclub.inventario.TipoMovimiento;
import tiendaclub.inventario.UbicacionInventario;
import tiendaclub.ventas.ReporteVentas;
import tiendaclub.ventas.Venta;
import tiendaclub.ventas.detalles.DetalleVenta;
import tiendaclub.ventas.tipos.VentaCafeteria;
import tiendaclub.ventas.tipos.VentaTienda;
import usuarios.Usuario;
import usuarios.tipos.Empleado;
import usuarios.tipos.EstudianteUniversidad;
import usuarios.tipos.Socio;
import usuarios.tipos.empleados.Administrador;
import usuarios.tipos.empleados.Entrenador;
import usuarios.tipos.empleados.Fisioterapeuta;
import utilidades.excepciones.InventarioInsuficienteException;
import utilidades.excepciones.ReservaNoDisponibleException;
import clubdeportivo.torneos.Torneo;
import clubdeportivo.torneos.BonoDescuento;
import tiendaclub.ventas.descuentos.DescuentoBonoTorneo;
import tiendaclub.ventas.descuentos.DescuentoCodigoCompartido;
import tiendaclub.ventas.descuentos.DescuentoPuntos;
import clubdeportivo.torneos.EstadoTorneo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.InscripcionEquipoTorneo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.ParticipanteTorneo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.TorneoConjunto;
import clubdeportivo.torneos.TorneoIndividual;
import clubdeportivo.torneos.InscripcionTorneo;
import clubdeportivo.torneos.EncuentroTorneo;
import clubdeportivo.torneos.PremioTorneo;

public class ClubDeportivo {
    private final String nombre;
    private final String institucion;
    private final LocalTime horaApertura;
    private final LocalTime horaCierre;
    private final int minEntrenadoresEnTurno;
    private final int minFisioterapeutasEnTurno;
    private final List<Usuario> usuarios;
    private final List<DeporteConjunto> deportesConjunto;
    private final List<DeporteIndividual> deportesIndividuales;
    private final List<Competencia> competencias;
    private final List<Partido> partidos;
    private final List<Instalacion> instalaciones;
    private final List<Reserva> reservas;
    private final List<SesionEntrenamiento> sesionesEntrenamiento;
    private final List<SolicitudCambioTurno> solicitudesCambioTurno;
    private final List<PagoMensualidad> pagosMensualidad;
    private final List<Producto> productos;
    private final List<UbicacionInventario> ubicacionesInventario;
    private final List<MovimientoInventario> movimientosInventario;
    private final List<SugerenciaProducto> sugerenciasProducto;
    private final List<Venta> ventas;
    private final List<Torneo> torneos;

    public ClubDeportivo() {
        this("Club Deportivo Séneca", "Universidad de los Andes",
                LocalTime.of(6, 0), LocalTime.of(22, 0), 2, 1);
    }

    public ClubDeportivo(String nombre, String institucion, LocalTime horaApertura,
            LocalTime horaCierre, int minEntrenadoresEnTurno, int minFisioterapeutasEnTurno) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del club es obligatorio");
        }

        if (institucion == null || institucion.trim().isEmpty()) {
            throw new IllegalArgumentException("La institución es obligatoria");
        }

        if (horaApertura == null || horaCierre == null) {
            throw new IllegalArgumentException("El horario del club es obligatorio");
        }

        if (!horaCierre.isAfter(horaApertura)) {
            throw new IllegalArgumentException("La hora de cierre debe ser posterior a la hora de apertura");
        }

        if (minEntrenadoresEnTurno < 0 || minFisioterapeutasEnTurno < 0) {
            throw new IllegalArgumentException("Los mínimos no pueden ser negativos");
        }

        this.nombre = nombre;
        this.institucion = institucion;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.minEntrenadoresEnTurno = minEntrenadoresEnTurno;
        this.minFisioterapeutasEnTurno = minFisioterapeutasEnTurno;

        usuarios = new ArrayList<Usuario>();
        deportesConjunto = new ArrayList<DeporteConjunto>();
        deportesIndividuales = new ArrayList<DeporteIndividual>();
        competencias = new ArrayList<Competencia>();
        partidos = new ArrayList<Partido>();
        instalaciones = new ArrayList<Instalacion>();
        reservas = new ArrayList<Reserva>();
        sesionesEntrenamiento = new ArrayList<SesionEntrenamiento>();
        solicitudesCambioTurno = new ArrayList<SolicitudCambioTurno>();
        pagosMensualidad = new ArrayList<PagoMensualidad>();
        productos = new ArrayList<Producto>();
        ubicacionesInventario = new ArrayList<UbicacionInventario>();
        movimientosInventario = new ArrayList<MovimientoInventario>();
        sugerenciasProducto = new ArrayList<SugerenciaProducto>();
        ventas = new ArrayList<Venta>();
        torneos = new ArrayList<Torneo>();
    }

    // Torneos

    public void registrarTorneo(Torneo torneo) {
        if (torneo == null) {
            throw new IllegalArgumentException("El torneo no puede ser nulo");
        }

        if (buscarTorneo(torneo.getId()) != null) {
            throw new IllegalArgumentException("Ya existe un torneo con ese ID");
        }

        if (torneo.getFechaInicio().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha del torneo (" + torneo.getFechaInicio() + ") ya pasó");
        }

        if (torneo.getHoraInicio().isBefore(horaApertura)
                || torneo.getHoraFin().isAfter(horaCierre)) {
            throw new IllegalArgumentException(
                    "El torneo debe realizarse dentro del horario del club ("
                            + horaApertura + " - " + horaCierre + ")");
        }

        torneos.add(torneo);
    }

    // Torneos ya guardados en el archivo: se conservan aunque el horario del club haya cambiado
    public void cargarTorneo(Torneo torneo) {
        if (torneo == null) {
            throw new IllegalArgumentException("El torneo no puede ser nulo");
        }

        if (buscarTorneo(torneo.getId()) != null) {
            throw new IllegalArgumentException("Ya existe un torneo con ese ID");
        }

        torneos.add(torneo);
    }

    public Torneo buscarTorneo(String id) {
        if (id == null) {
            return null;
        }

        for (Torneo torneo : torneos) {
            if (torneo.getId().equalsIgnoreCase(id)) {
                return torneo;
            }
        }

        return null;
    }

    public List<Torneo> consultarTorneos(TipoTorneo tipo, EstadoTorneo estado) {
        List<Torneo> resultado = new ArrayList<Torneo>();

        for (Torneo torneo : torneos) {
            boolean coincideTipo = tipo == null || torneo.getTipo() == tipo;
            boolean coincideEstado = estado == null || torneo.getEstado() == estado;

            if (coincideTipo && coincideEstado) {
                resultado.add(torneo);
            }
        }

        return resultado;
    }

    public InscripcionEquipoTorneo inscribirEquipoEnTorneo(TorneoConjunto torneo, Equipo equipo, TipoCupo tipoCupo) {
        if (torneo == null || equipo == null || tipoCupo == null) {
            throw new IllegalArgumentException("Los datos de la inscripción son obligatorios");
        }

        if (!torneos.contains(torneo)) {
            throw new IllegalArgumentException("El torneo no está registrado en el club");
        }

        return torneo.inscribirEquipo(equipo, tipoCupo);
    }

    // Un socio solo puede inscribir en un torneo al equipo del que es integrante activo
    public InscripcionEquipoTorneo inscribirEquipoEnTorneo(TorneoConjunto torneo, Equipo equipo,
            TipoCupo tipoCupo, Socio solicitante) {

        if (solicitante == null || equipo == null) {
            throw new IllegalArgumentException("El equipo y el socio que lo inscribe son obligatorios");
        }

        InscripcionEquipo integrante = equipo.buscarInscripcion(solicitante);

        if (integrante == null || !integrante.estaActiva()) {
            throw new IllegalArgumentException(
                    solicitante.getNombre() + " no es integrante activo del equipo " + equipo.getNombre());
        }

        return inscribirEquipoEnTorneo(torneo, equipo, tipoCupo);
    }

    public InscripcionIndividualTorneo inscribirParticipantesEnTorneo(TorneoIndividual torneo,
            List<ParticipanteTorneo> participantes, TipoCupo tipoCupo) {

        if (torneo == null || participantes == null || tipoCupo == null) {
            throw new IllegalArgumentException("Los datos de la inscripción son obligatorios");
        }

        if (!torneos.contains(torneo)) {
            throw new IllegalArgumentException("El torneo no está registrado en el club");
        }

        for (ParticipanteTorneo participante : participantes) {
            validarParticipanteRegistrado(participante);
        }

        return torneo.inscribirParticipantes(participantes, tipoCupo);
    }

    private void validarParticipanteRegistrado(ParticipanteTorneo participante) {
        if (!(participante instanceof Usuario)) {
            throw new IllegalArgumentException(
                    "El participante debe ser un usuario del club");
        }

        Usuario usuario = (Usuario) participante;

        if (buscarUsuario(usuario.getId()) != usuario) {
            throw new IllegalArgumentException(
                    "El participante no está registrado en el club");
        }

        if (usuario instanceof EstudianteUniversidad) {
            EstudianteUniversidad estudiante =
                    (EstudianteUniversidad) usuario;

            if (!estudiante.getUniversidad().equalsIgnoreCase(institucion)) {
                throw new IllegalArgumentException(
                        "El estudiante no pertenece a la institución del club");
            }
        }
    }

    public List<Torneo> getTorneos() {
        return new ArrayList<Torneo>(torneos);
    }

    public void retirarInscripcionTorneo(InscripcionTorneo inscripcion) {
        if (inscripcion == null) {
            throw new IllegalArgumentException("La inscripción es obligatoria");
        }

        Torneo torneo = inscripcion.getTorneo();

        if (!torneos.contains(torneo)) {
            throw new IllegalArgumentException("El torneo no está registrado en el club");
        }

        torneo.retirarInscripcion(inscripcion);
    }

    // El participante solo puede retirar una inscripción de la que hace parte
    public void retirarInscripcionTorneo(ParticipanteTorneo solicitante, InscripcionTorneo inscripcion) {
        if (solicitante == null || inscripcion == null) {
            throw new IllegalArgumentException("El participante y la inscripción son obligatorios");
        }

        if (!inscripcion.perteneceA(solicitante)) {
            throw new IllegalArgumentException(
                    "La inscripción no pertenece a " + solicitante.getNombre());
        }

        retirarInscripcionTorneo(inscripcion);
    }

    public List<InscripcionTorneo> consultarInscripcionesDe(ParticipanteTorneo participante) {
        List<InscripcionTorneo> resultado = new ArrayList<InscripcionTorneo>();

        if (participante == null) {
            return resultado;
        }

        for (Torneo torneo : torneos) {
            for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
                if (inscripcion.perteneceA(participante)) {
                    resultado.add(inscripcion);
                }
            }
        }

        return resultado;
    }

    public void liberarCuposPreferenciales(Torneo torneo) {
        validarTorneoRegistrado(torneo);
        torneo.liberarCuposPreferenciales();
    }

    public void iniciarTorneo(Torneo torneo) {
        validarTorneoRegistrado(torneo);
        torneo.iniciar();
    }

    private void validarTorneoRegistrado(Torneo torneo) {
        if (torneo == null) {
            throw new IllegalArgumentException("El torneo es obligatorio");
        }

        if (!torneos.contains(torneo)) {
            throw new IllegalArgumentException("El torneo no está registrado en el club");
        }
    }

    public void registrarResultadoEncuentro(EncuentroTorneo encuentro, String marcador) {
        if (encuentro == null) {
            throw new IllegalArgumentException("El encuentro es obligatorio");
        }

        if (marcador == null || marcador.trim().isEmpty()) {
            throw new IllegalArgumentException("El marcador es obligatorio");
        }

        Torneo torneo = buscarTorneoDelEncuentro(encuentro);

        if (torneo == null) {
            throw new IllegalArgumentException("El encuentro no pertenece a un torneo registrado");
        }

        torneo.registrarResultadoEncuentro(
                encuentro, marcador);
    }

    public List<PremioTorneo> finalizarTorneo(Torneo torneo) {
        validarTorneoRegistrado(torneo);
        torneo.finalizar();
        return torneo.getPremiosOtorgados();
    }

    private Torneo buscarTorneoDelEncuentro(EncuentroTorneo encuentro) {
        for (Torneo torneo : torneos) {
            if (torneo.getEncuentros().contains(encuentro)) {
                return torneo;
            }
        }

        return null;
    }

    // Usuarios

    public void registrarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        if (buscarUsuario(usuario.getId()) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese ID");
        }

        for (Usuario existente : usuarios) {
            if (existente.getLogin().equalsIgnoreCase(
                    usuario.getLogin())) {

                throw new IllegalArgumentException(
                        "Ya existe un usuario con ese login");
            }
        }

        if (usuario instanceof Empleado) {
            Empleado empleado = (Empleado) usuario;

            if (buscarEmpleadoPorCodigoDescuento(
                    empleado.getCodigoDescuento()) != null) {

                throw new IllegalArgumentException(
                        "Ya existe un empleado con ese código de descuento");
            }
        }

        usuarios.add(usuario);
    }

    public Usuario buscarUsuario(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        for (Usuario usuario : usuarios) {
            if (usuario.getId().equalsIgnoreCase(id)) {
                return usuario;
            }
        }

        return null;
    }

    public Empleado buscarEmpleadoPorCodigoDescuento(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }

        for (Usuario usuario : usuarios) {
            if (usuario instanceof Empleado) {
                Empleado empleado = (Empleado) usuario;

                if (empleado.getCodigoDescuento()
                        .equalsIgnoreCase(codigo.trim())) {

                    return empleado;
                }
            }
        }

        return null;
    }

    public Usuario autenticar(String login, String contrasena) {
        if (login == null || contrasena == null
                || login.trim().isEmpty()
                || contrasena.trim().isEmpty()) {

            return null;
        }

        for (Usuario usuario : usuarios) {
            if (usuario.getLogin().equalsIgnoreCase(login)
                    && usuario.validarContrasena(contrasena)) {

                return usuario;
            }
        }

        return null;
    }

    // Deportes

    public void registrarDeporteConjunto(DeporteConjunto deporte) {
        if (deporte == null) {
            throw new IllegalArgumentException(
                    "El deporte no puede ser nulo");
        }

        if (buscarDeporteConjunto(deporte.getNombre()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un deporte de conjunto con ese nombre");
        }

        // Sin posiciones no se podría inscribir a ningún jugador en sus equipos
        if (deporte.getPosiciones().isEmpty()) {
            throw new IllegalArgumentException(
                    "El deporte de conjunto debe tener al menos una posición");
        }

        deportesConjunto.add(deporte);
    }

    public DeporteConjunto buscarDeporteConjunto(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }

        for (DeporteConjunto deporte : deportesConjunto) {
            if (deporte.getNombre().equalsIgnoreCase(nombre)) {
                return deporte;
            }
        }

        return null;
    }

    public void registrarDeporteIndividual(DeporteIndividual deporte) {
        if (deporte == null) {
            throw new IllegalArgumentException(
                    "El deporte no puede ser nulo");
        }

        if (buscarDeporteIndividual(deporte.getNombre()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un deporte individual con ese nombre");
        }

        deportesIndividuales.add(deporte);
    }

    public DeporteIndividual buscarDeporteIndividual(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }

        for (DeporteIndividual deporte : deportesIndividuales) {
            if (deporte.getNombre().equalsIgnoreCase(nombre)) {
                return deporte;
            }
        }

        return null;
    }

    public void registrarEquipo(Equipo equipo) {
        if (equipo == null) {
            throw new IllegalArgumentException(
                    "El equipo no puede ser nulo");
        }

        DeporteConjunto deporte = equipo.getDeporte();

        if (!deportesConjunto.contains(deporte)) {
            throw new IllegalArgumentException(
                    "El deporte del equipo no está registrado");
        }

        // El nombre identifica al equipo en todo el club (búsquedas y archivos)
        if (buscarEquipo(equipo.getNombre()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un equipo con ese nombre");
        }

        deporte.agregarEquipo(equipo);
    }

    // Un equipo dado de baja no juega más: se liberan sus cupos y sus inscripciones
    public void darDeBajaEquipo(Equipo equipo) {
        validarEquipoRegistrado(equipo);

        if (!equipo.isActivo()) {
            throw new IllegalStateException(
                    "El equipo " + equipo.getNombre() + " ya está dado de baja");
        }

        for (Torneo torneo : torneos) {
            if (torneo.getEstado() == EstadoTorneo.EN_CURSO
                    && buscarInscripcionActivaDeEquipo(torneo, equipo) != null) {

                throw new IllegalStateException(
                        "El equipo juega el torneo en curso " + torneo.getNombre()
                                + "; se puede dar de baja cuando el torneo finalice");
            }
        }

        for (Competencia competencia : competencias) {
            if (competencia.tienePartidosPendientes(equipo)) {
                throw new IllegalStateException(
                        "El equipo tiene partidos programados en " + competencia.getNombre()
                                + "; registre su resultado o cancélelos primero");
            }
        }

        for (Partido partido : partidos) {
            if (partido instanceof PartidoInterno
                    && partido.getEstado() == EstadoPartido.PROGRAMADO
                    && juegaPartidoInterno((PartidoInterno) partido, equipo)) {

                throw new IllegalStateException(
                        "El equipo tiene un partido interno programado el "
                                + partido.getFecha() + "; regístrelo o cancélelo primero");
            }
        }

        for (Torneo torneo : torneos) {
            InscripcionTorneo inscripcion = buscarInscripcionActivaDeEquipo(torneo, equipo);

            if (inscripcion != null && torneo.getEstado() == EstadoTorneo.ABIERTO) {
                torneo.retirarInscripcion(inscripcion);
            }
        }

        for (Competencia competencia : competencias) {
            InscripcionCompetencia inscripcion = competencia.buscarInscripcion(equipo);

            if (inscripcion != null
                    && inscripcion.getEstado() != EstadoInscripcion.RETIRADA) {
                inscripcion.retirar();
            }
        }

        equipo.desactivar();
    }

    private InscripcionTorneo buscarInscripcionActivaDeEquipo(Torneo torneo, Equipo equipo) {
        for (InscripcionTorneo inscripcion : torneo.getInscripcionesActivas()) {
            if (inscripcion instanceof InscripcionEquipoTorneo
                    && ((InscripcionEquipoTorneo) inscripcion).getEquipo() == equipo) {
                return inscripcion;
            }
        }

        return null;
    }

    private boolean juegaPartidoInterno(PartidoInterno partido, Equipo equipo) {
        return partido.getEquipoLocal() == equipo
                || partido.getEquipoVisitante() == equipo;
    }

    private void validarEquipoRegistrado(Equipo equipo) {
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo es obligatorio");
        }

        if (buscarEquipo(equipo.getNombre()) != equipo) {
            throw new IllegalArgumentException(
                    "El equipo no está registrado en el club");
        }
    }

    public List<Equipo> getEquipos() {
        List<Equipo> resultado = new ArrayList<Equipo>();

        for (DeporteConjunto deporte : deportesConjunto) {
            resultado.addAll(deporte.getEquipos());
        }

        return resultado;
    }

    // Inscripciones del socio en equipos (activas o suspendidas)
    public List<InscripcionEquipo> consultarEquiposDe(Socio socio) {
        List<InscripcionEquipo> resultado = new ArrayList<InscripcionEquipo>();

        for (Equipo equipo : getEquipos()) {
            InscripcionEquipo inscripcion = equipo.buscarInscripcion(socio);

            if (inscripcion != null
                    && inscripcion.getEstado() != EstadoInscripcion.RETIRADA) {
                resultado.add(inscripcion);
            }
        }

        return resultado;
    }

    // Equipos activos ordenados por puntaje histórico, de mayor a menor (deporte nulo = todos)
    public List<Equipo> consultarRankingEquipos(DeporteConjunto deporte) {
        List<Equipo> ranking = new ArrayList<Equipo>();

        for (Equipo equipo : getEquipos()) {
            if (equipo.isActivo()
                    && (deporte == null || equipo.getDeporte() == deporte)) {
                ranking.add(equipo);
            }
        }

        Collections.sort(ranking, new Comparator<Equipo>() {
            @Override
            public int compare(Equipo uno, Equipo otro) {
                return Double.compare(otro.getPuntajeHistorico(), uno.getPuntajeHistorico());
            }
        });

        return ranking;
    }

    // Integrantes de la academia ordenados por su puntaje de ranking, de mayor a menor
    public List<InscripcionAcademia> consultarRankingAcademia(DeporteIndividual deporte) {
        if (deporte == null || !deportesIndividuales.contains(deporte)) {
            throw new IllegalArgumentException(
                    "El deporte individual no está registrado");
        }

        List<InscripcionAcademia> ranking = new ArrayList<InscripcionAcademia>();

        for (InscripcionAcademia inscripcion : deporte.getInscripciones()) {
            if (inscripcion.getEstado() != EstadoInscripcion.RETIRADA) {
                ranking.add(inscripcion);
            }
        }

        Collections.sort(ranking, new Comparator<InscripcionAcademia>() {
            @Override
            public int compare(InscripcionAcademia una, InscripcionAcademia otra) {
                return Double.compare(otra.getPuntajeRanking(), una.getPuntajeRanking());
            }
        });

        return ranking;
    }

    public List<InscripcionEquipo> consultarJugadoresSuspendidos() {
        List<InscripcionEquipo> suspendidos = new ArrayList<InscripcionEquipo>();

        for (Equipo equipo : getEquipos()) {
            for (InscripcionEquipo inscripcion : equipo.getInscripciones()) {
                if (inscripcion.estaSuspendida()) {
                    suspendidos.add(inscripcion);
                }
            }
        }

        return suspendidos;
    }

    public List<InscripcionAcademia> consultarAcademiasDe(Socio socio) {
        List<InscripcionAcademia> resultado = new ArrayList<InscripcionAcademia>();

        for (DeporteIndividual deporte : deportesIndividuales) {
            for (InscripcionAcademia inscripcion : deporte.getInscripciones()) {
                if (inscripcion.getSocio() == socio
                        && inscripcion.getEstado() != EstadoInscripcion.RETIRADA) {
                    resultado.add(inscripcion);
                }
            }
        }

        return resultado;
    }

    public Equipo buscarEquipo(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }

        for (DeporteConjunto deporte : deportesConjunto) {
            for (Equipo equipo : deporte.getEquipos()) {
                if (equipo.getNombre().equalsIgnoreCase(nombre)) {
                    return equipo;
                }
            }
        }

        return null;
    }

    public InscripcionEquipo inscribirSocioEnEquipo(
            Socio socio, Equipo equipo,
            Posicion posicion, int numeroCamiseta) {

        if (socio == null || equipo == null
                || posicion == null) {

            throw new IllegalArgumentException(
                    "El socio, el equipo y la posición son obligatorios");
        }

        if (buscarUsuario(socio.getId()) != socio) {
            throw new IllegalArgumentException(
                    "El socio no está registrado en el club");
        }

        if (!deportesConjunto.contains(
                equipo.getDeporte())
                || !equipo.getDeporte()
                        .getEquipos().contains(equipo)) {

            throw new IllegalArgumentException(
                    "El equipo no está registrado en el club");
        }

        if (!equipo.isActivo()) {
            throw new IllegalStateException(
                    "El equipo está inactivo");
        }

        int edad = calcularEdad(socio.getFechaNacimiento());

        if (!equipo.getCategoria().admiteEdad(edad)) {
            throw new IllegalArgumentException(
                    "La edad del socio no corresponde a la categoría "
                            + equipo.getCategoria());
        }

        for (Equipo otroEquipo : equipo.getDeporte().getEquipos()) {

            boolean mismaCategoria = otroEquipo
                    .getCategoria()
                    .equals(equipo.getCategoria());

            InscripcionEquipo existente = otroEquipo.buscarInscripcion(socio);

            // Un suspendido sigue siendo integrante; un equipo dado de baja ya no cuenta
            if (mismaCategoria
                    && otroEquipo.isActivo()
                    && existente != null
                    && existente.getEstado() != EstadoInscripcion.RETIRADA) {

                throw new IllegalArgumentException(
                        "El socio ya pertenece al equipo " + otroEquipo.getNombre()
                                + " de esta disciplina y categoría");
            }
        }

        InscripcionEquipo inscripcion = new InscripcionEquipo(
                socio,
                equipo,
                posicion,
                numeroCamiseta);

        equipo.agregarInscripcion(inscripcion);
        return inscripcion;
    }

    // Inscripciones guardadas en el archivo: no se vuelve a revisar la edad,
    // porque el socio pudo cumplir años después de entrar a su categoría
    public InscripcionEquipo cargarInscripcionEquipo(
            Socio socio, Equipo equipo,
            Posicion posicion, int numeroCamiseta) {

        if (socio == null || buscarUsuario(socio.getId()) != socio) {
            throw new IllegalArgumentException(
                    "El socio no está registrado en el club");
        }

        validarEquipoRegistrado(equipo);

        InscripcionEquipo inscripcion = new InscripcionEquipo(
                socio, equipo, posicion, numeroCamiseta);

        equipo.cargarInscripcion(inscripcion);
        return inscripcion;
    }

    public void retirarSocioDeEquipo(Socio socio, Equipo equipo) {
        validarEquipoRegistrado(equipo);

        InscripcionEquipo inscripcion = equipo.buscarInscripcion(socio);

        if (inscripcion == null
                || inscripcion.getEstado() == EstadoInscripcion.RETIRADA) {

            throw new IllegalArgumentException(
                    "El socio no es integrante del equipo " + equipo.getNombre());
        }

        inscripcion.retirar();
    }

    private int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null
                || fechaNacimiento.isAfter(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "La fecha de nacimiento no es válida");
        }

        return Period.between(
                fechaNacimiento,
                LocalDate.now()).getYears();
    }

    public InscripcionAcademia inscribirSocioEnAcademia(
            Socio socio, DeporteIndividual deporte,
            Nivel nivel) {

        if (socio == null || deporte == null
                || nivel == null) {

            throw new IllegalArgumentException(
                    "El socio, el deporte y el nivel son obligatorios");
        }

        if (buscarUsuario(socio.getId()) != socio) {
            throw new IllegalArgumentException(
                    "El socio no está registrado en el club");
        }

        if (!deportesIndividuales.contains(deporte)) {
            throw new IllegalArgumentException(
                    "El deporte individual no está registrado");
        }

        InscripcionAcademia inscripcion = new InscripcionAcademia(
                socio,
                deporte,
                nivel);

        deporte.agregarInscripcion(inscripcion);
        return inscripcion;
    }

    // Competencias y partidos

    public void registrarCompetencia(Competencia competencia) {
        if (competencia == null) {
            throw new IllegalArgumentException(
                    "La competencia no puede ser nula");
        }

        if (!deportesConjunto.contains(
                competencia.getDeporte())) {

            throw new IllegalArgumentException(
                    "El deporte de la competencia no está registrado");
        }

        if (buscarCompetencia(
                competencia.getNombre(),
                competencia.getAnio()) != null) {

            throw new IllegalArgumentException(
                    "Ya existe una competencia con ese nombre y año");
        }

        competencias.add(competencia);
    }

    public Competencia buscarCompetencia(
            String nombre, int anio) {

        if (nombre == null
                || nombre.trim().isEmpty()
                || anio <= 0) {

            return null;
        }

        for (Competencia competencia : competencias) {
            if (competencia.getNombre().equalsIgnoreCase(nombre)
                    && competencia.getAnio() == anio) {

                return competencia;
            }
        }

        return null;
    }

    public void registrarPartido(Partido partido) {
        if (partido == null) {
            throw new IllegalArgumentException(
                    "El partido no puede ser nulo");
        }

        if (partidos.contains(partido)) {
            throw new IllegalArgumentException(
                    "El partido ya está registrado");
        }

        if (partido instanceof PartidoOficial) {
            PartidoOficial oficial = (PartidoOficial) partido;
            Competencia competencia = oficial.getCompetencia();

            if (!competencias.contains(competencia)) {
                throw new IllegalArgumentException(
                        "La competencia no está registrada en el club");
            }

            competencia.programarPartido(oficial);
        } else if (partido instanceof PartidoInterno) {
            PartidoInterno interno = (PartidoInterno) partido;

            if (buscarEquipo(
                    interno.getEquipoLocal().getNombre()) != interno.getEquipoLocal()
                    || buscarEquipo(
                            interno.getEquipoVisitante().getNombre()) != interno.getEquipoVisitante()) {

                throw new IllegalArgumentException(
                        "Los equipos del partido no están registrados");
            }

            validarMinimoJugadores(interno.getEquipoLocal());
            validarMinimoJugadores(interno.getEquipoVisitante());
        } else if (partido instanceof PartidoPractica) {
            PartidoPractica practica = (PartidoPractica) partido;

            if (!deportesIndividuales.contains(
                    practica.getDeporte())) {

                throw new IllegalArgumentException(
                        "El deporte del partido no está registrado");
            }
        }

        partidos.add(partido);
    }

    // Partidos leídos del archivo: los equipos ya cumplían el mínimo cuando se programaron
    public void cargarPartido(Partido partido) {
        if (partido == null) {
            throw new IllegalArgumentException(
                    "El partido no puede ser nulo");
        }

        if (partido instanceof PartidoOficial) {
            PartidoOficial oficial = (PartidoOficial) partido;

            if (!competencias.contains(oficial.getCompetencia())) {
                throw new IllegalArgumentException(
                        "La competencia no está registrada en el club");
            }

            oficial.getCompetencia().programarPartido(oficial);
        }

        if (!partidos.contains(partido)) {
            partidos.add(partido);
        }
    }

    private void validarMinimoJugadores(Equipo equipo) {
        if (!equipo.cumpleMinimoJugadores()) {
            throw new IllegalArgumentException(
                    "El equipo " + equipo.getNombre() + " tiene "
                            + equipo.getJugadoresHabilitados().size()
                            + " jugadores habilitados y el deporte exige "
                            + equipo.getDeporte().getMinimoJugadores());
        }
    }

    public InscripcionCompetencia inscribirEquipoEnCompetencia(
            Competencia competencia, Equipo equipo) {

        validarCompetenciaRegistrada(competencia);
        validarEquipoRegistrado(equipo);

        if (equipo.isActivo()
                && equipo.getDeporte() == competencia.getDeporte()
                && equipo.getCategoria() == competencia.getCategoria()) {

            validarMinimoJugadores(equipo);
        }

        return competencia.inscribirEquipo(equipo);
    }

    public void agregarJugadorANomina(
            Competencia competencia, Equipo equipo, Socio socio) {

        validarCompetenciaRegistrada(competencia);

        InscripcionCompetencia inscripcion = competencia.buscarInscripcion(equipo);

        if (inscripcion == null || !inscripcion.estaActiva()) {
            throw new IllegalArgumentException(
                    "El equipo no tiene una inscripción activa en la competencia");
        }

        InscripcionEquipo integrante = equipo.buscarInscripcion(socio);

        if (integrante != null && integrante.estaSuspendida()) {
            throw new IllegalArgumentException(
                    socio.getNombre() + " está suspendido y no puede entrar a la nómina");
        }

        inscripcion.agregarJugadorNomina(socio);
    }

    private void validarCompetenciaRegistrada(Competencia competencia) {
        if (competencia == null) {
            throw new IllegalArgumentException("La competencia es obligatoria");
        }

        if (!competencias.contains(competencia)) {
            throw new IllegalArgumentException(
                    "La competencia no está registrada en el club");
        }
    }

    // Con estado nulo se devuelven todos los partidos
    public List<Partido> consultarPartidos(EstadoPartido estado) {
        List<Partido> resultado = new ArrayList<Partido>();

        for (Partido partido : partidos) {
            if (estado == null || partido.getEstado() == estado) {
                resultado.add(partido);
            }
        }

        return resultado;
    }

    public void cancelarPartido(Partido partido) {
        validarPartidoRegistrado(partido);
        partido.cancelar();
    }

    /*
     * Registra el marcador, finaliza el partido y guarda las estadísticas.
     * En un partido oficial además:
     * 1. los jugadores que ya estaban suspendidos cumplen esta fecha,
     * 2. se suspende a quien acumuló tarjetas en este partido y
     * 3. el puntaje histórico del equipo sube con los puntos obtenidos.
     * Devuelve los socios que quedaron suspendidos.
     */
    public List<Socio> registrarResultadoPartido(Partido partido, String marcador,
            List<EstadisticaJugador> estadisticas) {

        validarPartidoRegistrado(partido);

        if (partido.getEstado() != EstadoPartido.PROGRAMADO) {
            throw new IllegalStateException(
                    "El partido ya fue finalizado o cancelado");
        }

        List<EstadisticaJugador> lista = estadisticas == null
                ? new ArrayList<EstadisticaJugador>()
                : new ArrayList<EstadisticaJugador>(estadisticas);

        // Todo se valida antes de cambiar el partido, para no dejarlo a medias
        List<Socio> conEstadistica = new ArrayList<Socio>();

        for (EstadisticaJugador estadistica : lista) {
            if (estadistica == null) {
                throw new IllegalArgumentException("La estadística no puede ser nula");
            }

            Socio jugador = estadistica.getJugador();

            if (partido instanceof PartidoOficial) {
                InscripcionEquipo integrante = ((PartidoOficial) partido)
                        .getEquipo().buscarInscripcion(jugador);

                if (integrante != null && integrante.estaSuspendida()) {
                    throw new IllegalArgumentException(
                            jugador.getNombre() + " está suspendido y no pudo jugar este partido");
                }
            }

            partido.validarEstadistica(estadistica);

            if (conEstadistica.contains(jugador)) {
                throw new IllegalArgumentException(
                        jugador.getNombre() + " tiene más de una estadística en el partido");
            }

            conEstadistica.add(jugador);
        }

        partido.registrarResultado(marcador);
        partido.finalizar();

        for (EstadisticaJugador estadistica : lista) {
            partido.agregarEstadistica(estadistica);
        }

        List<Socio> suspendidos = new ArrayList<Socio>();

        if (partido instanceof PartidoOficial) {
            PartidoOficial oficial = (PartidoOficial) partido;
            Equipo equipo = oficial.getEquipo();

            for (InscripcionEquipo inscripcion : equipo.getInscripciones()) {
                inscripcion.cumplirPartidoSuspension();
            }

            for (EstadisticaJugador estadistica : lista) {
                InscripcionEquipo integrante = equipo.buscarInscripcion(
                        estadistica.getJugador());

                if (estadistica.ameritaSuspension()
                        && integrante != null
                        && integrante.getEstado() != EstadoInscripcion.RETIRADA) {

                    integrante.suspender();
                    suspendidos.add(estadistica.getJugador());
                }
            }

            equipo.sumarPuntajeHistorico(
                    oficial.getCompetencia().calcularPuntosObtenidos(oficial));
        }

        return suspendidos;
    }

    private void validarPartidoRegistrado(Partido partido) {
        if (partido == null) {
            throw new IllegalArgumentException("El partido es obligatorio");
        }

        if (!partidos.contains(partido)) {
            throw new IllegalArgumentException(
                    "El partido no está registrado en el club");
        }
    }

    // Entrenamientos y asistencias

    public void programarSesion(
            SesionEntrenamiento sesion) {

        if (sesion == null) {
            throw new IllegalArgumentException(
                    "La sesión no puede ser nula");
        }

        if (sesionesEntrenamiento.contains(sesion)) {
            throw new IllegalArgumentException(
                    "La sesión ya está registrada");
        }

        if (buscarUsuario(
                sesion.getEntrenador().getId()) != sesion.getEntrenador()) {

            throw new IllegalArgumentException(
                    "El entrenador no está registrado en el club");
        }

        if (buscarInstalacion(
                sesion.getInstalacion().getId()) != sesion.getInstalacion()) {

            throw new IllegalArgumentException(
                    "La instalación no está registrada en el club");
        }

        boolean tieneEquipo = sesion.getEquipo() != null;
        boolean tieneDeporteIndividual = sesion
                .getDeporteIndividual() != null;

        if (tieneEquipo == tieneDeporteIndividual) {
            throw new IllegalArgumentException(
                    "La sesión debe corresponder a un equipo "
                            + "o a un deporte individual");
        }

        if (tieneEquipo
                && buscarEquipo(
                        sesion.getEquipo().getNombre()) != sesion.getEquipo()) {

            throw new IllegalArgumentException(
                    "El equipo no está registrado en el club");
        }

        if (tieneDeporteIndividual
                && !deportesIndividuales.contains(
                        sesion.getDeporteIndividual())) {

            throw new IllegalArgumentException(
                    "El deporte individual no está registrado");
        }

        if (sesion.getHoraInicio().isBefore(horaApertura)
                || sesion.calcularHoraFin().isAfter(horaCierre)) {

            throw new IllegalArgumentException(
                    "La sesión debe realizarse dentro "
                            + "del horario del club");
        }

        if (!entrenadorEnTurno(sesion)) {
            throw new IllegalStateException(
                    "El entrenador no tiene un turno asignado "
                            + "que cubra el horario de la sesión");
        }

        if (!consultarDisponibilidad(
                sesion.getInstalacion(),
                sesion.getFecha(),
                sesion.getHoraInicio(),
                sesion.getDuracionMinutos())) {

            throw new IllegalStateException(
                    "La instalación no está disponible "
                            + "en ese horario");
        }

        for (SesionEntrenamiento existente : sesionesEntrenamiento) {

            if (sesion.seCruzaCon(existente)) {
                throw new IllegalStateException(
                        "La sesión se cruza con otra sesión "
                                + "del entrenador o de la instalación");
            }
        }

        sesionesEntrenamiento.add(sesion);
    }

    private boolean entrenadorEnTurno(
            SesionEntrenamiento sesion) {

        DiaSemana dia = DiaSemana.values()[
                sesion.getFecha().getDayOfWeek().getValue() - 1];

        for (Turno turno : sesion.getEntrenador().getTurnos()) {
            if (turno.getDiaSemana() == dia
                    && turno.contieneHorario(
                            sesion.getHoraInicio(),
                            sesion.calcularHoraFin())) {

                return true;
            }
        }

        return false;
    }

    public void registrarSesion(
            SesionEntrenamiento sesion) {

        programarSesion(sesion);
    }

    public void cargarSesion(
            SesionEntrenamiento sesion) {

        if (sesion == null) {
            throw new IllegalArgumentException(
                    "La sesión no puede ser nula");
        }

        if (sesionesEntrenamiento.contains(sesion)) {
            throw new IllegalArgumentException(
                    "La sesión ya está registrada");
        }

        if (buscarUsuario(
                sesion.getEntrenador().getId()) != sesion.getEntrenador()) {

            throw new IllegalArgumentException(
                    "El entrenador no está registrado en el club");
        }

        if (buscarInstalacion(
                sesion.getInstalacion().getId()) != sesion.getInstalacion()) {

            throw new IllegalArgumentException(
                    "La instalación no está registrada en el club");
        }

        boolean tieneEquipo = sesion.getEquipo() != null;
        boolean tieneDeporteIndividual = sesion
                .getDeporteIndividual() != null;

        if (tieneEquipo == tieneDeporteIndividual) {
            throw new IllegalArgumentException(
                    "La sesión debe corresponder a un equipo "
                            + "o a un deporte individual");
        }

        sesionesEntrenamiento.add(sesion);
    }

    public List<SesionEntrenamiento> consultarSesiones(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria");
        }

        List<SesionEntrenamiento> resultado = new ArrayList<SesionEntrenamiento>();

        for (SesionEntrenamiento sesion : sesionesEntrenamiento) {
            if (sesion.getFecha().equals(fecha)) {
                resultado.add(sesion);
            }
        }

        return resultado;
    }

    public RegistroAsistencia registrarAsistencia(
            SesionEntrenamiento sesion,
            Socio socio) {

        if (sesion == null || socio == null) {
            throw new IllegalArgumentException(
                    "La sesión y el socio son obligatorios");
        }

        if (!sesionesEntrenamiento.contains(sesion)) {
            throw new IllegalArgumentException(
                    "La sesión no está registrada en el club");
        }

        if (buscarUsuario(socio.getId()) != socio) {
            throw new IllegalArgumentException(
                    "El socio no está registrado en el club");
        }

        if (sesion.getTipo() == TipoSesion.INDIVIDUAL
                && !sesion.getRegistros().isEmpty()
                && sesion.buscarRegistro(socio) == null) {

            throw new IllegalStateException(
                    "La sesión individual ya tiene asignado un socio");
        }

        RegistroAsistencia registro = new RegistroAsistencia(
                sesion,
                socio,
                EstadoAsistencia.PENDIENTE);

        sesion.agregarRegistro(registro);
        return registro;
    }

    // Solo el entrenador a cargo de la sesión registra la asistencia y la evaluación (nota de 1 a 5)
    public RegistroAsistencia registrarAsistenciaSesion(
            Entrenador entrenador,
            SesionEntrenamiento sesion,
            Socio socio,
            EstadoAsistencia estado,
            Double nota,
            String comentario) {

        if (entrenador == null || sesion == null
                || socio == null || estado == null) {

            throw new IllegalArgumentException(
                    "El entrenador, la sesión, el socio y el estado son obligatorios");
        }

        if (!sesionesEntrenamiento.contains(sesion)) {
            throw new IllegalArgumentException(
                    "La sesión no está registrada en el club");
        }

        if (sesion.getEntrenador() != entrenador) {
            throw new IllegalArgumentException(
                    "Solo el entrenador de la sesión puede registrar su asistencia");
        }

        if (estado == EstadoAsistencia.PENDIENTE) {
            throw new IllegalArgumentException(
                    "Debe indicar si el socio asistió, llegó tarde o faltó");
        }

        boolean asistio = estado == EstadoAsistencia.ASISTIO
                || estado == EstadoAsistencia.TARDE;

        if (nota != null && !asistio) {
            throw new IllegalArgumentException(
                    "Solo se evalúa a un socio que asistió o llegó tarde");
        }

        if (nota != null && (nota < 1 || nota > 5)) {
            throw new IllegalArgumentException(
                    "La nota debe estar entre 1 y 5");
        }

        RegistroAsistencia registro = sesion.buscarRegistro(socio);

        if (registro == null) {
            registro = registrarAsistencia(sesion, socio);
        }

        registro.registrarAsistencia(estado);

        if (nota != null) {
            registro.registrarEvaluacion(nota, comentario);
        }

        return registro;
    }

    public List<SesionEntrenamiento> consultarSesionesDe(Entrenador entrenador) {
        List<SesionEntrenamiento> resultado = new ArrayList<SesionEntrenamiento>();

        for (SesionEntrenamiento sesion : sesionesEntrenamiento) {
            if (sesion.getEntrenador() == entrenador) {
                resultado.add(sesion);
            }
        }

        return resultado;
    }

    // Sesiones a las que el socio puede asistir o en las que ya tiene registro
    public List<SesionEntrenamiento> consultarSesionesDe(Socio socio) {
        List<SesionEntrenamiento> resultado = new ArrayList<SesionEntrenamiento>();

        for (SesionEntrenamiento sesion : sesionesEntrenamiento) {
            if (sesion.estaHabilitado(socio)
                    || sesion.buscarRegistro(socio) != null) {
                resultado.add(sesion);
            }
        }

        return resultado;
    }

    // Instalaciones y reservas

    public void registrarInstalacion(Instalacion instalacion) {
        if (instalacion == null) {
            throw new IllegalArgumentException(
                    "La instalación no puede ser nula");
        }

        if (buscarInstalacion(instalacion.getId()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe una instalación con ese ID");
        }

        if (instalacion.getDisciplina() != null) {
            instalacion.getDisciplina().agregarInstalacion(instalacion);
        }

        instalaciones.add(instalacion);
    }

    public Instalacion buscarInstalacion(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        for (Instalacion instalacion : instalaciones) {
            if (instalacion.getId().equalsIgnoreCase(id)) {
                return instalacion;
            }
        }

        return null;
    }

    public boolean consultarDisponibilidad(
            Instalacion instalacion,
            LocalDate fecha,
            LocalTime inicio,
            int duracionMinutos) {

        if (instalacion == null || fecha == null
                || inicio == null || duracionMinutos <= 0) {
            return false;
        }

        if (buscarInstalacion(instalacion.getId()) != instalacion) {
            return false;
        }

        if (fecha.isBefore(LocalDate.now())) {
            return false;
        }

        LocalTime fin = inicio.plusMinutes(duracionMinutos);

        if (inicio.isBefore(horaApertura)
                || fin.isAfter(horaCierre)) {

            return false;
        }

        if (!instalacion.estaDisponible(
                fecha, inicio, duracionMinutos)) {
            return false;
        }

        for (SesionEntrenamiento sesion : sesionesEntrenamiento) {

            boolean mismaInstalacion = sesion.getInstalacion() == instalacion;

            boolean mismaFecha = sesion.getFecha().equals(fecha);

            boolean seCruzan = inicio.isBefore(sesion.calcularHoraFin())
                    && sesion.getHoraInicio().isBefore(fin);

            if (mismaInstalacion
                    && mismaFecha
                    && seCruzan) {
                return false;
            }
        }

        return true;
    }

    public void registrarReserva(Reserva reserva) throws ReservaNoDisponibleException {
        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva no puede ser nula");
        }

        Instalacion instalacion = reserva.getInstalacion();

        if (buscarInstalacion(instalacion.getId()) != instalacion) {

            throw new IllegalArgumentException(
                    "La instalación no está registrada");
        }

        if (buscarUsuario(reserva.getSocio().getId()) != reserva.getSocio()) {
            throw new IllegalArgumentException(
                    "El socio no está registrado en el club");
        }

        if (!reserva.esFechaValida()) {
            throw new IllegalArgumentException(
                    "No se permiten reservas en fechas pasadas");
        }

        if (reserva.getHoraInicio().isBefore(horaApertura)
                || reserva.calcularHoraFin().isAfter(horaCierre)) {

            throw new ReservaNoDisponibleException(
                    "La reserva debe estar dentro del horario del club ("
                            + horaApertura + " - " + horaCierre + ")");
        }

        DeporteConjunto deporteConjunto = reserva.getDeporteConjunto();

        if (instalacion.getTipo() == TipoInstalacion.CANCHA_CONJUNTO
                && deporteConjunto == null) {

            throw new IllegalArgumentException(
                    "Para reservar una cancha de conjunto se debe indicar el deporte");
        }

        if (deporteConjunto != null
                && !deportesConjunto.contains(deporteConjunto)) {

            throw new IllegalArgumentException(
                    "El deporte de la reserva no está registrado");
        }

        String motivo = reserva.motivoCantidadJugadoresInvalida();

        if (motivo != null) {
            throw new IllegalArgumentException(motivo);
        }

        if (!consultarDisponibilidad(
                instalacion,
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getDuracionMinutos())) {

            throw new ReservaNoDisponibleException(
                    "La instalación no está disponible");
        }

        DeporteIndividual disciplina = reserva.getDisciplina();

        if (disciplina != null
                && !disciplina.permiteNuevaReserva(contarReservasActivas(reserva))) {

            throw new ReservaNoDisponibleException(
                    "La disciplina alcanzó el máximo de instalaciones "
                            + "reservadas al mismo tiempo");
        }

        instalacion.agregarReserva(reserva);
        reservas.add(reserva);
    }

    // Reservas ya guardadas en el archivo: pueden ser de fechas pasadas (historial)
    public void cargarReserva(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva no puede ser nula");
        }

        Instalacion instalacion = reserva.getInstalacion();

        if (buscarInstalacion(instalacion.getId()) != instalacion) {
            throw new IllegalArgumentException(
                    "La instalación no está registrada");
        }

        instalacion.agregarReserva(reserva);
        reservas.add(reserva);
    }

    private int contarReservasActivas(Reserva nueva) {
        int cantidad = 0;

        for (Reserva reserva : reservas) {
            boolean mismaDisciplina = reserva.getDisciplina() == nueva.getDisciplina();

            boolean mismaFecha = reserva.getFecha().equals(nueva.getFecha());

            boolean seCruzan = reserva.getHoraInicio().isBefore(nueva.calcularHoraFin())
                    && nueva.getHoraInicio().isBefore(reserva.calcularHoraFin());

            if (mismaDisciplina && mismaFecha && seCruzan) {
                cantidad++;
            }
        }

        return cantidad;
    }

    public Reserva buscarReservaActiva(Socio socio, LocalDate fecha, LocalTime hora) {
        for (Reserva reserva : reservas) {
            boolean mismoSocio = reserva.getSocio() == socio;

            boolean mismaFecha = reserva.getFecha().equals(fecha);

            boolean enCurso = !hora.isBefore(reserva.getHoraInicio())
                    && hora.isBefore(reserva.calcularHoraFin());

            if (mismoSocio && mismaFecha && enCurso) {
                return reserva;
            }
        }

        return null;
    }

    // Mensualidades

    public void registrarPagoMensualidad(PagoMensualidad pago) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago no puede ser nulo");
        }

        Socio socio = pago.getSocio();

        if (buscarUsuario(socio.getId()) != socio) {
            throw new IllegalArgumentException(
                    "El socio no está registrado en el club");
        }

        for (PagoMensualidad existente : socio.getPagos()) {
            if (existente.correspondeAlPeriodo(pago.getPeriodo())) {
                throw new IllegalArgumentException(
                        socio.getNombre() + " ya pagó la mensualidad de " + pago.getPeriodo());
            }
        }

        pagosMensualidad.add(pago);
        socio.agregarPago(pago);
    }

    public List<PagoMensualidad> consultarPagosDe(Socio socio) {
        List<PagoMensualidad> resultado = new ArrayList<PagoMensualidad>();

        for (PagoMensualidad pago : pagosMensualidad) {
            if (pago.getSocio() == socio) {
                resultado.add(pago);
            }
        }

        return resultado;
    }

    public double calcularTotalPagos(String periodo) {
        double total = 0;

        for (PagoMensualidad pago : pagosMensualidad) {
            if (periodo == null || pago.correspondeAlPeriodo(periodo)) {
                total += pago.getValor();
            }
        }

        return total;
    }

    // Productos y ubicaciones

    public void registrarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }

        if (buscarProducto(producto.getNombre()) != null) {
            throw new IllegalArgumentException("Ya existe un producto con ese nombre");
        }

        productos.add(producto);
    }

    public Producto buscarProducto(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }

        for (Producto producto : productos) {
            if (producto.getNombre().equalsIgnoreCase(nombre)) {
                return producto;
            }
        }

        return null;
    }

    public void registrarUbicacionInventario(UbicacionInventario ubicacion) {
        if (ubicacion == null) {
            throw new IllegalArgumentException("La ubicación no puede ser nula");
        }

        if (buscarUbicacionInventario(ubicacion.getNombre()) != null) {
            throw new IllegalArgumentException("Ya existe una ubicación con ese nombre");
        }

        ubicacionesInventario.add(ubicacion);
    }

    public UbicacionInventario buscarUbicacionInventario(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }

        for (UbicacionInventario ubicacion : ubicacionesInventario) {
            if (ubicacion.getNombre().equalsIgnoreCase(nombre)) {
                return ubicacion;
            }
        }

        return null;
    }

    public ExistenciaInventario buscarExistencia(Producto producto, UbicacionInventario ubicacion) {
        if (producto == null || ubicacion == null) {
            return null;
        }

        return ubicacion.buscarExistencia(producto);
    }

    // Movimientos de inventario

    public void registrarMovimiento(MovimientoInventario movimiento) {
        if (movimiento == null || !movimiento.esValido()) {
            throw new IllegalArgumentException("El movimiento no es válido");
        }

        movimientosInventario.add(movimiento);
    }

    public void reabastecerProducto(Producto producto, UbicacionInventario ubicacion, int cantidad) {
        validarProductoYUbicacion(producto, ubicacion);

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        ExistenciaInventario existencia = buscarExistencia(producto, ubicacion);

        if (existencia == null) {
            existencia = new ExistenciaInventario(producto, ubicacion, cantidad);
            ubicacion.agregarExistencia(existencia);
        } else {
            existencia.aumentarCantidad(cantidad);
        }

        MovimientoInventario movimiento = new MovimientoInventario(
                LocalDate.now(), cantidad, TipoMovimiento.REABASTECIMIENTO,
                producto, null, ubicacion);

        registrarMovimiento(movimiento);
    }

    public void trasladarProducto(Producto producto, UbicacionInventario origen,
            UbicacionInventario destino, int cantidad) throws InventarioInsuficienteException {

        validarProductoYUbicacion(producto, origen);
        validarProductoYUbicacion(producto, destino);

        if (origen == destino) {
            throw new IllegalArgumentException("El origen y destino deben ser diferentes");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        ExistenciaInventario existenciaOrigen = buscarExistencia(producto, origen);

        if (existenciaOrigen == null || !existenciaOrigen.tieneCantidad(cantidad)) {
            throw new InventarioInsuficienteException(
                    "Inventario insuficiente para: " + producto.getNombre());
        }

        ExistenciaInventario existenciaDestino = buscarExistencia(producto, destino);

        existenciaOrigen.disminuirCantidad(cantidad);

        if (existenciaDestino == null) {
            existenciaDestino = new ExistenciaInventario(producto, destino, cantidad);
            destino.agregarExistencia(existenciaDestino);
        } else {
            existenciaDestino.aumentarCantidad(cantidad);
        }

        MovimientoInventario movimiento = new MovimientoInventario(
                LocalDate.now(), cantidad, TipoMovimiento.TRASLADO,
                producto, origen, destino);

        registrarMovimiento(movimiento);
    }

    private void validarProductoYUbicacion(Producto producto, UbicacionInventario ubicacion) {
        if (producto == null || ubicacion == null) {
            throw new IllegalArgumentException("El producto y la ubicación son obligatorios");
        }

        if (!productos.contains(producto)) {
            throw new IllegalArgumentException("El producto no está registrado");
        }

        if (!ubicacionesInventario.contains(ubicacion)) {
            throw new IllegalArgumentException("La ubicación no está registrada");
        }
    }

    // Ventas

    public void registrarVenta(Venta venta) {
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser nula");
        }

        ventas.add(venta);
    }

    public void procesarVenta(Venta venta, UbicacionInventario ubicacion)
            throws InventarioInsuficienteException {
        if (venta == null || ubicacion == null) {
            throw new IllegalArgumentException("La venta y la ubicación son obligatorias");
        }

        if (!ubicacionesInventario.contains(ubicacion)) {
            throw new IllegalArgumentException("La ubicación no está registrada");
        }

        if (venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener productos");
        }

        BonoDescuento bono = validarBonoVenta(venta);
        double puntosRedimidos = validarPuntosVenta(venta);
        validarCodigoCompartido(venta);

        Map<Producto, Integer> cantidades = new HashMap<Producto, Integer>();

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            Integer acumulado = cantidades.get(producto);

            if (acumulado == null) {
                acumulado = 0;
            }

            cantidades.put(producto, acumulado + detalle.getCantidad());
        }

        for (Map.Entry<Producto, Integer> entrada : cantidades.entrySet()) {
            ExistenciaInventario existencia = buscarExistencia(entrada.getKey(), ubicacion);

            if (existencia == null || !existencia.tieneCantidad(entrada.getValue())) {
                throw new InventarioInsuficienteException(
                        "Inventario insuficiente para: " + entrada.getKey().getNombre());
            }
        }

        venta.calcularTotal();

        for (Map.Entry<Producto, Integer> entrada : cantidades.entrySet()) {
            Producto producto = entrada.getKey();
            int cantidad = entrada.getValue();
            ExistenciaInventario existencia = buscarExistencia(producto, ubicacion);

            existencia.disminuirCantidad(cantidad);

            MovimientoInventario movimiento = new MovimientoInventario(
                    LocalDate.now(), cantidad, TipoMovimiento.SALIDA,
                    producto, ubicacion, null);

            registrarMovimiento(movimiento);
        }

        if (bono != null) {
            bono.usar();
        }

        if (venta.getComprador() instanceof Socio) {
            Socio socio = (Socio) venta.getComprador();

            if (puntosRedimidos > 0
                    && !socio.redimirPuntos(puntosRedimidos)) {

                throw new IllegalStateException(
                        "No fue posible redimir los puntos");
            }

            socio.agregarPuntos(venta.getPuntosGenerados());
        }

        ventas.add(venta);
    }

    private BonoDescuento validarBonoVenta(Venta venta) {
        if (!(venta.getPoliticaDescuento() instanceof DescuentoBonoTorneo)) {
            return null;
        }

        if (!(venta.getComprador() instanceof Socio)) {
            throw new IllegalArgumentException(
                    "Solo un socio puede utilizar un bono de torneo");
        }

        DescuentoBonoTorneo descuento =
                (DescuentoBonoTorneo) venta.getPoliticaDescuento();

        BonoDescuento bono = descuento.getBono();
        Socio comprador = (Socio) venta.getComprador();

        if (bono.getSocio() != comprador) {
            throw new IllegalArgumentException(
                    "El bono no pertenece al comprador de la venta");
        }

        if (!comprador.getBonos().contains(bono)) {
            throw new IllegalArgumentException(
                    "El bono no está registrado para el socio");
        }

        if (!bono.estaDisponible()) {
            throw new IllegalArgumentException(
                    "El bono no está disponible");
        }

        return bono;
    }

    private double validarPuntosVenta(Venta venta) {
        if (!(venta.getPoliticaDescuento() instanceof DescuentoPuntos)) {
            return 0;
        }

        if (!(venta.getComprador() instanceof Socio)) {
            throw new IllegalArgumentException(
                    "Solo un socio puede redimir puntos");
        }

        DescuentoPuntos descuento = (DescuentoPuntos) venta.getPoliticaDescuento();
        double puntos = descuento.getPuntosRedimidos();
        Socio socio = (Socio) venta.getComprador();

        if (puntos > socio.getPuntosFidelidad()) {
            throw new IllegalArgumentException(
                    "El socio no tiene suficientes puntos");
        }

        if (puntos > venta.calcularSubtotal()) {
            throw new IllegalArgumentException(
                    "No se pueden redimir más puntos que el subtotal");
        }

        return puntos;
    }

    private void validarCodigoCompartido(Venta venta) {
        if (!(venta.getPoliticaDescuento()
                instanceof DescuentoCodigoCompartido)) {

            return;
        }

        DescuentoCodigoCompartido descuento =
                (DescuentoCodigoCompartido) venta.getPoliticaDescuento();

        if (buscarEmpleadoPorCodigoDescuento(
                descuento.getCodigo()) == null) {

            throw new IllegalArgumentException(
                    "El código de descuento compartido no es válido");
        }
    }

    // Reporte de ventas entre dos fechas (incluidas), con el dinero de cada rubro
    public ReporteVentas generarReporteVentas(LocalDate desde, LocalDate hasta) {
        ReporteVentas reporte = new ReporteVentas(desde, hasta);

        reporte.registrarRubro(VentaTienda.RUBRO);
        reporte.registrarRubro(VentaCafeteria.RUBRO);

        for (Venta venta : ventas) {
            if (reporte.incluye(venta.getFecha())) {
                reporte.agregarVenta(venta);
            }
        }

        return reporte;
    }

    // Devuelve null si no hay ventas registradas
    public LocalDate getFechaPrimeraVenta() {
        LocalDate primera = null;

        for (Venta venta : ventas) {
            if (primera == null || venta.getFecha().isBefore(primera)) {
                primera = venta.getFecha();
            }
        }

        return primera;
    }

    public LocalDate getFechaUltimaVenta() {
        LocalDate ultima = null;

        for (Venta venta : ventas) {
            if (ultima == null || venta.getFecha().isAfter(ultima)) {
                ultima = venta.getFecha();
            }
        }

        return ultima;
    }

    public int contarUnidadesMovidas(TipoMovimiento tipo) {
        int unidades = 0;

        for (MovimientoInventario movimiento : movimientosInventario) {
            if (movimiento.getTipo() == tipo) {
                unidades += movimiento.getCantidad();
            }
        }

        return unidades;
    }

    // Sugerencias de productos

    public void registrarSugerencia(SugerenciaProducto sugerencia) {
        if (sugerencia == null) {
            throw new IllegalArgumentException("La sugerencia no puede ser nula");
        }

        sugerenciasProducto.add(sugerencia);
    }

    public void aprobarSugerencia(SugerenciaProducto sugerencia,
            Administrador administrador, Producto producto) {

        if (sugerencia == null || administrador == null || producto == null) {
            throw new IllegalArgumentException("Los datos de aprobación son obligatorios");
        }

        if (!sugerenciasProducto.contains(sugerencia)) {
            throw new IllegalArgumentException("La sugerencia no está registrada");
        }

        if (!sugerencia.getNombre().equalsIgnoreCase(
                producto.getNombre())) {

            throw new IllegalArgumentException(
                    "El producto debe corresponder a la sugerencia");
        }

        if (buscarProducto(producto.getNombre()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un producto con ese nombre");
        }

        sugerencia.aprobar(administrador, producto);
        registrarProducto(producto);
    }

    public void rechazarSugerencia(SugerenciaProducto sugerencia, Administrador administrador) {
        if (sugerencia == null || administrador == null) {
            throw new IllegalArgumentException("Los datos del rechazo son obligatorios");
        }

        if (!sugerenciasProducto.contains(sugerencia)) {
            throw new IllegalArgumentException("La sugerencia no está registrada");
        }

        sugerencia.rechazar(administrador);
    }

    // Reglas de productos en instalaciones

    public void validarIngresoProducto(Producto producto, Instalacion instalacion) {
        if (producto == null || instalacion == null) {
            throw new IllegalArgumentException("El producto y la instalación son obligatorios");
        }

        if (!(producto instanceof Bebida)) {
            return;
        }

        Bebida bebida = (Bebida) producto;

        if (!bebida.isCaliente()) {
            return;
        }

        boolean esPadel = instalacion.getTipo() == TipoInstalacion.CANCHA_PADEL;
        boolean esMesaTechada = instalacion.getTipo() == TipoInstalacion.MESA_TENIS_MESA
                && instalacion.isTechada();

        if (esPadel || esMesaTechada) {
            throw new IllegalArgumentException(
                    "No se permiten bebidas calientes en esta instalación");
        }
    }

    // Turnos

    public void asignarTurno(
            Empleado empleado, Turno turno) {

        if (empleado == null || turno == null) {
            throw new IllegalArgumentException(
                    "El empleado y el turno son obligatorios");
        }

        if (buscarUsuario(empleado.getId()) != empleado) {
            throw new IllegalArgumentException(
                    "El empleado no está registrado en el club");
        }

        if (turno.getHoraInicio().isBefore(horaApertura)
                || turno.getHoraFin().isAfter(horaCierre)) {

            throw new IllegalArgumentException(
                    "El turno debe estar dentro del horario del club");
        }

        empleado.agregarTurno(turno);
    }

    public SolicitudCambioTurno solicitarCambioTurno(
            Empleado empleado,
            Turno turnoOriginal,
            DiaSemana nuevoDia,
            LocalTime nuevaHoraInicio,
            LocalTime nuevaHoraFin) {

        if (empleado == null) {
            throw new IllegalArgumentException(
                    "El empleado es obligatorio");
        }

        if (buscarUsuario(empleado.getId()) != empleado) {
            throw new IllegalArgumentException(
                    "El empleado no está registrado en el club");
        }

        SolicitudCambioTurno solicitud = empleado.solicitarCambioTurno(
                turnoOriginal,
                nuevoDia,
                nuevaHoraInicio,
                nuevaHoraFin);

        solicitudesCambioTurno.add(solicitud);
        return solicitud;
    }

    public void registrarSolicitudCambio(
            SolicitudCambioTurno solicitud) {

        if (solicitud == null) {
            throw new IllegalArgumentException(
                    "La solicitud no puede ser nula");
        }

        Empleado empleado = solicitud.getEmpleadoSolicitante();

        if (buscarUsuario(empleado.getId()) != empleado) {
            throw new IllegalArgumentException(
                    "El empleado no está registrado en el club");
        }

        if (!empleado.getSolicitudesCambio().contains(solicitud)) {
            throw new IllegalArgumentException(
                    "La solicitud no pertenece al empleado");
        }

        if (!solicitudesCambioTurno.contains(solicitud)) {
            solicitudesCambioTurno.add(solicitud);
        }
    }

    public void cargarSolicitudCambio(
            SolicitudCambioTurno solicitud) {

        if (solicitud == null) {
            throw new IllegalArgumentException(
                    "La solicitud no puede ser nula");
        }

        Empleado empleado = solicitud.getEmpleadoSolicitante();

        if (buscarUsuario(empleado.getId()) != empleado) {
            throw new IllegalArgumentException(
                    "El empleado no está registrado en el club");
        }

        empleado.cargarSolicitudCambio(solicitud);

        if (!solicitudesCambioTurno.contains(solicitud)) {
            solicitudesCambioTurno.add(solicitud);
        }
    }

    public void aprobarCambioTurno(
            SolicitudCambioTurno solicitud,
            Administrador administrador) {

        validarSolicitudYAdministrador(
                solicitud,
                administrador);

        if (!validarCobertura(solicitud)) {
            throw new IllegalStateException(
                    "El cambio no conserva la cobertura mínima "
                            + "o presenta un cruce de horario");
        }

        administrador.aprobarSolicitud(solicitud);

        solicitud.getEmpleadoSolicitante()
                .aplicarCambioTurno(solicitud);
    }

    public void rechazarCambioTurno(
            SolicitudCambioTurno solicitud,
            Administrador administrador) {

        validarSolicitudYAdministrador(
                solicitud,
                administrador);

        administrador.rechazarSolicitud(solicitud);
    }

    private void validarSolicitudYAdministrador(
            SolicitudCambioTurno solicitud,
            Administrador administrador) {

        if (solicitud == null || administrador == null) {
            throw new IllegalArgumentException(
                    "La solicitud y el administrador son obligatorios");
        }

        if (!solicitudesCambioTurno.contains(solicitud)) {
            throw new IllegalArgumentException(
                    "La solicitud no está registrada en el club");
        }

        if (buscarUsuario(administrador.getId()) != administrador) {

            throw new IllegalArgumentException(
                    "El administrador no está registrado en el club");
        }

        if (!solicitud.estaPendiente()) {
            throw new IllegalStateException(
                    "La solicitud ya fue decidida");
        }
    }

    public boolean cumpleCoberturaMinima(Turno turno) {
        if (turno == null) {
            return false;
        }

        int entrenadores = 0;
        int fisioterapeutas = 0;

        for (Usuario usuario : usuarios) {
            if (!(usuario instanceof Empleado)) {
                continue;
            }

            Empleado empleado = (Empleado) usuario;

            if (!trabajaDurante(empleado, turno)) {
                continue;
            }

            if (empleado instanceof Entrenador) {
                entrenadores++;
            } else if (empleado instanceof Fisioterapeuta) {
                fisioterapeutas++;
            }
        }

        return entrenadores >= minEntrenadoresEnTurno
                && fisioterapeutas >= minFisioterapeutasEnTurno;
    }

    private boolean trabajaDurante(Empleado empleado, Turno horario) {
        for (Turno turno : empleado.getTurnos()) {
            if (turno.getDiaSemana() == horario.getDiaSemana()
                    && turno.contieneHorario(
                            horario.getHoraInicio(),
                            horario.getHoraFin())) {

                return true;
            }
        }

        return false;
    }

    public boolean validarCobertura(SolicitudCambioTurno solicitud) {
        if (solicitud == null) {
            return false;
        }

        Turno turnoOriginal = solicitud.getTurnoOriginal();
        Empleado solicitante = solicitud.getEmpleadoSolicitante();

        if (turnoOriginal == null || solicitante == null) {
            return false;
        }

        int entrenadores = 0;
        int fisioterapeutas = 0;

        for (Usuario usuario : usuarios) {
            if (!(usuario instanceof Empleado)) {
                continue;
            }

            Empleado empleado = (Empleado) usuario;
            boolean permanece = empleado != solicitante
                    && trabajaDurante(empleado, turnoOriginal);

            if (!permanece) {
                continue;
            }

            if (empleado instanceof Entrenador) {
                entrenadores++;
            } else if (empleado instanceof Fisioterapeuta) {
                fisioterapeutas++;
            }
        }

        boolean conservaCobertura = entrenadores >= minEntrenadoresEnTurno
                && fisioterapeutas >= minFisioterapeutasEnTurno;

        boolean dentroHorario = !solicitud.getNuevaHoraInicio().isBefore(horaApertura)
                && !solicitud.getNuevaHoraFin().isAfter(horaCierre);

        Turno nuevoTurno = new Turno(solicitud.getNuevoDia(),
                solicitud.getNuevaHoraInicio(), solicitud.getNuevaHoraFin());

        for (Turno turno : solicitante.getTurnos()) {
            if (turno != turnoOriginal && turno.seCruzaCon(nuevoTurno)) {
                return false;
            }
        }

        return conservaCobertura && dentroHorario;
    }

    public boolean empleadoDisponible(Empleado empleado, LocalDate fecha,
            LocalTime horaInicio, LocalTime horaFin) {

        if (empleado == null || fecha == null
                || horaInicio == null || horaFin == null) {

            throw new IllegalArgumentException(
                    "Los datos para consultar la disponibilidad son obligatorios");
        }

        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException(
                    "La hora final debe ser posterior a la inicial");
        }

        DiaSemana dia = DiaSemana.values()[fecha.getDayOfWeek().getValue() - 1];

        for (Turno turno : empleado.getTurnos()) {
            boolean mismoDia = turno.getDiaSemana() == dia;

            boolean horariosSuperpuestos = horaInicio.isBefore(
                    turno.getHoraFin())
                    && turno.getHoraInicio().isBefore(horaFin);

            if (mismoDia && horariosSuperpuestos) {
                return false;
            }
        }

        return true;
    }

    // Getters

    public String getNombre() {
        return nombre;
    }

    public String getInstitucion() {
        return institucion;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public int getMinEntrenadoresEnTurno() {
        return minEntrenadoresEnTurno;
    }

    public int getMinFisioterapeutasEnTurno() {
        return minFisioterapeutasEnTurno;
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<Usuario>(usuarios);
    }

    public List<DeporteConjunto> getDeportesConjunto() {
        return new ArrayList<DeporteConjunto>(deportesConjunto);
    }

    public List<DeporteIndividual> getDeportesIndividuales() {
        return new ArrayList<DeporteIndividual>(deportesIndividuales);
    }

    public List<Competencia> getCompetencias() {
        return new ArrayList<Competencia>(competencias);
    }

    public List<Partido> getPartidos() {
        return new ArrayList<Partido>(partidos);
    }

    public List<Instalacion> getInstalaciones() {
        return new ArrayList<Instalacion>(instalaciones);
    }

    public List<Reserva> getReservas() {
        return new ArrayList<Reserva>(reservas);
    }

    public List<SesionEntrenamiento> getSesionesEntrenamiento() {
        return new ArrayList<SesionEntrenamiento>(sesionesEntrenamiento);
    }

    public List<SesionEntrenamiento> getSesiones() {
        return getSesionesEntrenamiento();
    }

    public List<SolicitudCambioTurno> getSolicitudesCambioTurno() {
        return new ArrayList<SolicitudCambioTurno>(
                solicitudesCambioTurno);
    }

    public List<PagoMensualidad> getPagosMensualidad() {
        return new ArrayList<PagoMensualidad>(pagosMensualidad);
    }

    public List<Producto> getProductos() {
        return new ArrayList<Producto>(productos);
    }

    public List<UbicacionInventario> getUbicacionesInventario() {
        return new ArrayList<UbicacionInventario>(ubicacionesInventario);
    }

    public List<MovimientoInventario> getMovimientosInventario() {
        return new ArrayList<MovimientoInventario>(movimientosInventario);
    }

    public List<SugerenciaProducto> getSugerenciasProducto() {
        return new ArrayList<SugerenciaProducto>(sugerenciasProducto);
    }

    public List<Venta> getVentas() {
        return new ArrayList<Venta>(ventas);
    }
}
