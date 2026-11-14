package clubdeportivo.torneos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import usuarios.tipos.Empleado;

public abstract class Torneo {
    private final String id;
    private final String nombre;
    private final LocalDate fechaInicio;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private final FormatoTorneo formato;
    private final int capacidad;
    private final TipoTorneo tipo;
    private EstadoTorneo estado;
    private final int cuposPreferenciales;
    private final int cuposRegulares;
    private final LocalDateTime fechaLiberacionPreferenciales;
    private final List<InscripcionTorneo> inscripciones;
    private final List<EncuentroTorneo> encuentros;
    private final List<PosicionTorneo> posiciones;
    private final PoliticaPremio politicaPremio;
    private final List<PremioTorneo> premiosOtorgados;
    private final double tarifaInscripcion;
    private boolean cuposPreferencialesLiberados;

    public Torneo(String id, String nombre, LocalDate fechaInicio,
            LocalTime horaInicio, LocalTime horaFin,
            FormatoTorneo formato, int capacidad, TipoTorneo tipo,
            LocalDateTime fechaLiberacionPreferenciales,
            PoliticaPremio politicaPremio, double tarifaInscripcion) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El ID del torneo es obligatorio");
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del torneo es obligatorio");
        }

        if (fechaInicio == null || horaInicio == null
                || horaFin == null) {

            throw new IllegalArgumentException(
                    "La fecha y las horas del torneo son obligatorias");
        }

        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException(
                    "La hora final debe ser posterior a la hora inicial");
        }

        if (formato == null || tipo == null) {
            throw new IllegalArgumentException(
                    "El formato y el tipo del torneo son obligatorios");
        }

        if (capacidad < 2) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser de al menos dos inscripciones");
        }

        if (fechaLiberacionPreferenciales == null) {
            throw new IllegalArgumentException(
                    "La fecha de liberación de cupos es obligatoria");
        }

        LocalDateTime inicioTorneo = LocalDateTime.of(
                fechaInicio, horaInicio);

        if (fechaLiberacionPreferenciales.isAfter(inicioTorneo)) {
            throw new IllegalArgumentException(
                    "Los cupos preferenciales deben liberarse antes del torneo");
        }

        if (politicaPremio == null) {
            throw new IllegalArgumentException(
                    "La política de premio es obligatoria");
        }

        if (tipo == TipoTorneo.RECREATIVO
                && tarifaInscripcion != 0) {

            throw new IllegalArgumentException(
                    "Los torneos recreativos no tienen costo de inscripción");
        }

        if (tipo == TipoTorneo.COMPETITIVO
                && tarifaInscripcion <= 0) {

            throw new IllegalArgumentException(
                    "La tarifa competitiva debe ser mayor que cero");
        }

        this.id = id;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.formato = formato;
        this.capacidad = capacidad;
        this.tipo = tipo;
        this.estado = EstadoTorneo.ABIERTO;
        this.cuposPreferenciales = calcularCuposPreferenciales();
        this.cuposRegulares = calcularCuposRegulares();
        this.fechaLiberacionPreferenciales = fechaLiberacionPreferenciales;
        this.inscripciones = new ArrayList<InscripcionTorneo>();
        this.encuentros = new ArrayList<EncuentroTorneo>();
        this.posiciones = new ArrayList<PosicionTorneo>();
        this.politicaPremio = politicaPremio;
        this.premiosOtorgados = new ArrayList<PremioTorneo>();
        this.tarifaInscripcion = tarifaInscripcion;
        this.cuposPreferencialesLiberados = false;
    }

    public int calcularCuposPreferenciales() {
        return (int) Math.ceil(capacidad * 0.20);
    }

    public int calcularCuposRegulares() {
        return capacidad - calcularCuposPreferenciales();
    }

    public boolean tieneCupoDisponible() {
        actualizarLiberacionCupos();

        return cantidadInscripcionesActivas() < capacidad;
    }

    public boolean tieneCupoRegular() {
        actualizarLiberacionCupos();

        if (cuposPreferencialesLiberados) {
            return tieneCupoDisponible();
        }

        return contarInscripciones(TipoCupo.REGULAR) < cuposRegulares;
    }

    public boolean tieneCupoPreferencial() {
        actualizarLiberacionCupos();

        if (cuposPreferencialesLiberados) {
            return false;
        }

        return contarInscripciones(TipoCupo.PREFERENCIAL) < cuposPreferenciales;
    }

    public int cantidadCuposDisponibles() {
        return capacidad
                - cantidadInscripcionesActivas();
    }

    public void agregarInscripcion(
            InscripcionTorneo inscripcion) {

        if (estado != EstadoTorneo.ABIERTO) {
            throw new IllegalStateException(
                    "El torneo no acepta nuevas inscripciones");
        }

        if (inscripcion == null
                || inscripcion.getTorneo() != this) {

            throw new IllegalArgumentException(
                    "La inscripción pertenece a otro torneo");
        }

        if (!inscripcion.estaActiva()) {
            throw new IllegalArgumentException(
                    "La inscripción debe estar activa");
        }

        if (!tieneCupoDisponible()) {
            throw new IllegalStateException(
                    "El torneo no tiene cupos disponibles");
        }

        if (inscripcion.getTipoCupo() == TipoCupo.PREFERENCIAL
                && !tieneCupoPreferencial()) {

            throw new IllegalStateException(
                    "No hay cupos preferenciales disponibles");
        }

        if (inscripcion.getTipoCupo() == TipoCupo.REGULAR
                && !tieneCupoRegular()) {

            throw new IllegalStateException(
                    "No hay cupos regulares disponibles");
        }

        if (!inscripciones.contains(inscripcion)) {
            inscripciones.add(inscripcion);
        }
    }

    public void retirarInscripcion(
            InscripcionTorneo inscripcion) {

        if (estado != EstadoTorneo.ABIERTO) {
            throw new IllegalStateException(
                    "No se puede retirar una inscripción "
                            + "después de iniciar el torneo");
        }

        if (inscripcion == null
                || !inscripciones.contains(inscripcion)) {

            throw new IllegalArgumentException(
                    "La inscripción no pertenece al torneo");
        }

        inscripcion.retirar();
    }

    public List<InscripcionTorneo> getInscripcionesActivas() {
        List<InscripcionTorneo> activas = new ArrayList<InscripcionTorneo>();

        for (InscripcionTorneo inscripcion : inscripciones) {
            if (inscripcion.estaActiva()) {
                activas.add(inscripcion);
            }
        }

        return activas;
    }

    public int cantidadInscripcionesActivas() {
        return getInscripcionesActivas().size();
    }

    public void liberarCuposPreferenciales() {
        if (estado != EstadoTorneo.ABIERTO) {
            throw new IllegalStateException(
                    "Los cupos solo se pueden liberar "
                            + "antes de iniciar el torneo");
        }

        cuposPreferencialesLiberados = true;
    }

    private void actualizarLiberacionCupos() {
        if (!cuposPreferencialesLiberados
                && !LocalDateTime.now().isBefore(
                        fechaLiberacionPreferenciales)) {

            cuposPreferencialesLiberados = true;
        }
    }

    private int contarInscripciones(
            TipoCupo tipoCupo) {

        int cantidad = 0;

        for (InscripcionTorneo inscripcion : inscripciones) {
            if (inscripcion.estaActiva()
                    && inscripcion.getTipoCupo() == tipoCupo) {

                cantidad++;
            }
        }

        return cantidad;
    }

    public boolean tieneParticipantesSuficientes() {
        return cantidadInscripcionesActivas() >= 2;
    }

    public boolean puedeIniciar() {
        return estado == EstadoTorneo.ABIERTO
                && tieneParticipantesSuficientes()
                && horarioValido();
    }

    public void generarEncuentros() {
        if (!encuentros.isEmpty()) {
            throw new IllegalStateException(
                    "Los encuentros ya fueron generados");
        }

        List<InscripcionTorneo> activas = getInscripcionesActivas();

        if (activas.size() < 2) {
            throw new IllegalStateException(
                    "No hay suficientes participantes "
                            + "para generar encuentros");
        }

        for (InscripcionTorneo inscripcion : activas) {
            agregarPosicion(
                    new PosicionTorneo(inscripcion));
        }

        int numeroEncuentro = 1;

        if (formato == FormatoTorneo.TODOS_CONTRA_TODOS) {
            for (int i = 0; i < activas.size(); i++) {
                for (int j = i + 1; j < activas.size(); j++) {

                    EncuentroTorneo encuentro = new EncuentroTorneo(
                            id + "-E" + numeroEncuentro,
                            fechaInicio,
                            horaInicio,
                            activas.get(i),
                            activas.get(j));

                    agregarEncuentro(encuentro);
                    numeroEncuentro++;
                }
            }
        } else {
            for (int i = 0; i + 1 < activas.size(); i += 2) {

                EncuentroTorneo encuentro = new EncuentroTorneo(
                        id + "-E" + numeroEncuentro,
                        fechaInicio,
                        horaInicio,
                        activas.get(i),
                        activas.get(i + 1));

                agregarEncuentro(encuentro);
                numeroEncuentro++;
            }
        }
    }

    public void iniciar() {
        if (!puedeIniciar()) {
            throw new IllegalStateException(
                    "El torneo no cumple las condiciones para iniciar");
        }

        if (encuentros.isEmpty()) {
            generarEncuentros();
        }

        estado = EstadoTorneo.EN_CURSO;
    }

    public void agregarEncuentro(
            EncuentroTorneo encuentro) {

        if (encuentro == null) {
            throw new IllegalArgumentException(
                    "El encuentro no puede ser nulo");
        }

        if (encuentro.getInscripcionLocal()
                .getTorneo() != this
                || encuentro.getInscripcionVisitante()
                        .getTorneo() != this) {

            throw new IllegalArgumentException(
                    "El encuentro pertenece a otro torneo");
        }

        for (EncuentroTorneo existente : encuentros) {
            if (existente.getId().equalsIgnoreCase(
                    encuentro.getId())) {

                throw new IllegalArgumentException(
                        "Ya existe un encuentro con ese ID");
            }
        }

        encuentros.add(encuentro);
    }

    public void agregarPosicion(
            PosicionTorneo posicion) {

        if (posicion == null
                || posicion.getInscripcion()
                        .getTorneo() != this) {

            throw new IllegalArgumentException(
                    "La posición pertenece a otro torneo");
        }

        for (PosicionTorneo existente : posiciones) {
            if (existente.getInscripcion() == posicion.getInscripcion()) {

                throw new IllegalArgumentException(
                        "La inscripción ya tiene una posición");
            }
        }

        posiciones.add(posicion);
    }

    public void actualizarPosiciones(
            EncuentroTorneo encuentro) {

        if (encuentro == null
                || !encuentros.contains(encuentro)) {

            throw new IllegalArgumentException(
                    "El encuentro no pertenece al torneo");
        }

        if (!encuentro.estaFinalizado()) {
            throw new IllegalStateException(
                    "El encuentro debe estar finalizado");
        }

        String[] resultado = encuentro.getMarcador()
                .trim().split("\\s*[-:]\\s*");

        if (resultado.length != 2) {
            throw new IllegalArgumentException(
                    "El marcador debe tener el formato 2-1");
        }

        int puntosLocal;
        int puntosVisitante;

        try {
            puntosLocal = Integer.parseInt(resultado[0]);
            puntosVisitante = Integer.parseInt(resultado[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El marcador debe contener números enteros");
        }

        PosicionTorneo posicionLocal = buscarPosicion(
                encuentro.getInscripcionLocal());

        PosicionTorneo posicionVisitante = buscarPosicion(
                encuentro.getInscripcionVisitante());

        if (puntosLocal > puntosVisitante) {
            posicionLocal.registrarVictoria(3);
            posicionVisitante.registrarDerrota();
        } else if (puntosVisitante > puntosLocal) {
            posicionVisitante.registrarVictoria(3);
            posicionLocal.registrarDerrota();
        } else {
            posicionLocal.registrarEmpate(1);
            posicionVisitante.registrarEmpate(1);
        }

        posiciones.sort(
                Comparator.comparingInt(
                        PosicionTorneo::getPuntos)
                        .reversed());

        for (int i = 0; i < posiciones.size(); i++) {
            posiciones.get(i).cambiarPosicion(i + 1);
        }
    }

    private PosicionTorneo buscarPosicion(
            InscripcionTorneo inscripcion) {

        for (PosicionTorneo posicion : posiciones) {
            if (posicion.getInscripcion() == inscripcion) {

                return posicion;
            }
        }

        throw new IllegalStateException(
                "La inscripción no tiene una posición");
    }

    public boolean todosLosEncuentrosFinalizados() {
        if (encuentros.isEmpty()) {
            return false;
        }

        for (EncuentroTorneo encuentro : encuentros) {
            if (encuentro.getEstado() == EstadoEncuentro.PROGRAMADO) {

                return false;
            }
        }

        return true;
    }

    public InscripcionTorneo determinarGanador() {
        if (posiciones.isEmpty()) {
            throw new IllegalStateException(
                    "El torneo no tiene posiciones");
        }

        posiciones.sort(
                Comparator.comparingInt(
                        PosicionTorneo::getPuntos)
                        .reversed());

        return posiciones.get(0).getInscripcion();
    }

    public boolean puedeFinalizar() {
        return estado == EstadoTorneo.EN_CURSO
                && todosLosEncuentrosFinalizados();
    }

    public void finalizar() {
        if (!puedeFinalizar()) {
            throw new IllegalStateException(
                    "El torneo no cumple las condiciones para finalizar");
        }

        otorgarPremios(determinarGanador());
        estado = EstadoTorneo.FINALIZADO;
    }

    public List<PremioTorneo> otorgarPremios(
            InscripcionTorneo ganador) {

        List<PremioTorneo> premios = politicaPremio.generarPremios(
                ganador,
                calcularRecaudo());

        premiosOtorgados.addAll(premios);

        return new ArrayList<PremioTorneo>(
                premios);
    }

    public void cargarInscripcion(
            InscripcionTorneo inscripcion) {

        if (inscripcion == null
                || inscripcion.getTorneo() != this) {

            throw new IllegalArgumentException(
                    "La inscripción pertenece a otro torneo");
        }

        if (!inscripciones.contains(inscripcion)) {
            inscripciones.add(inscripcion);
        }
    }

    public void cargarEncuentro(
            EncuentroTorneo encuentro) {

        if (encuentro == null
                || encuentro.getInscripcionLocal()
                        .getTorneo() != this
                || encuentro.getInscripcionVisitante()
                        .getTorneo() != this) {

            throw new IllegalArgumentException(
                    "El encuentro pertenece a otro torneo");
        }

        encuentros.add(encuentro);
    }

    public void cargarPosicion(
            PosicionTorneo posicion) {

        if (posicion == null
                || posicion.getInscripcion()
                        .getTorneo() != this) {

            throw new IllegalArgumentException(
                    "La posición pertenece a otro torneo");
        }

        posiciones.add(posicion);
    }

    public void cargarPremio(
            PremioTorneo premio) {

        if (premio == null
                || premio.getGanador()
                        .getTorneo() != this) {

            throw new IllegalArgumentException(
                    "El premio pertenece a otro torneo");
        }

        premiosOtorgados.add(premio);
    }

    public void cargarEstado(
            EstadoTorneo estado,
            boolean cuposPreferencialesLiberados) {

        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio");
        }

        this.estado = estado;
        this.cuposPreferencialesLiberados = cuposPreferencialesLiberados;
    }

    public boolean horarioValido() {
        return horaFin.isAfter(horaInicio);
    }

    public double calcularRecaudo() {
        double recaudo = 0;

        for (InscripcionTorneo inscripcion : inscripciones) {
            if (inscripcion.estaActiva()) {
                recaudo += inscripcion.getValorPagado();
            }
        }

        return recaudo;
    }

    public double calcularValorInscripcion(
            ParticipanteTorneo participante) {

        if (participante == null) {
            throw new IllegalArgumentException(
                    "El participante es obligatorio");
        }

        if (tipo == TipoTorneo.RECREATIVO
                || participante instanceof Empleado) {

            return 0;
        }

        return tarifaInscripcion;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public FormatoTorneo getFormato() {
        return formato;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public TipoTorneo getTipo() {
        return tipo;
    }

    public EstadoTorneo getEstado() {
        return estado;
    }

    public int getCuposPreferenciales() {
        return cuposPreferenciales;
    }

    public int getCuposRegulares() {
        return cuposRegulares;
    }

    public LocalDateTime getFechaLiberacionPreferenciales() {
        return fechaLiberacionPreferenciales;
    }

    public PoliticaPremio getPoliticaPremio() {
        return politicaPremio;
    }

    public boolean isCuposPreferencialesLiberados() {
        return cuposPreferencialesLiberados;
    }

    public double getTarifaInscripcion() {
        return tarifaInscripcion;
    }

    public List<InscripcionTorneo> getInscripciones() {
        return new ArrayList<InscripcionTorneo>(
                inscripciones);
    }

    public List<EncuentroTorneo> getEncuentros() {
        return new ArrayList<EncuentroTorneo>(
                encuentros);
    }

    public List<PosicionTorneo> getPosiciones() {
        return new ArrayList<PosicionTorneo>(
                posiciones);
    }

    public List<PremioTorneo> getPremiosOtorgados() {
        return new ArrayList<PremioTorneo>(
                premiosOtorgados);
    }
}