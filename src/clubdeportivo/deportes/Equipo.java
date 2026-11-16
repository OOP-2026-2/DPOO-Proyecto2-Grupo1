package clubdeportivo.deportes;

import java.util.ArrayList;
import java.util.List;
import usuarios.tipos.Socio;

public class Equipo {
    private String nombre;
    private Categoria categoria;
    private int cupoMaximo;
    private boolean activo;
    private double puntajeHistorico;
    private DeporteConjunto deporte;
    private List<InscripcionEquipo> inscripciones;

    public Equipo(String nombre, Categoria categoria, int cupoMaximo, DeporteConjunto deporte) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del equipo es obligatorio");
        }

        if (categoria == null) {
            throw new IllegalArgumentException(
                    "La categoría es obligatoria");
        }

        if (cupoMaximo <= 0) {
            throw new IllegalArgumentException(
                    "El cupo máximo debe ser mayor que cero");
        }

        if (deporte == null) {
            throw new IllegalArgumentException(
                    "El deporte es obligatorio");
        }

        this.nombre = nombre;
        this.categoria = categoria;
        this.cupoMaximo = cupoMaximo;
        this.activo = true;
        this.puntajeHistorico = 0;
        this.deporte = deporte;
        this.inscripciones = new ArrayList<InscripcionEquipo>();
    }

    public void agregarInscripcion(
            InscripcionEquipo inscripcion) {

        if (inscripcion == null) {
            throw new IllegalArgumentException(
                    "La inscripción no puede ser nula");
        }

        if (!activo) {
            throw new IllegalStateException(
                    "No se pueden registrar inscripciones en un equipo inactivo");
        }

        if (inscripcion.getEquipo() != this) {
            throw new IllegalArgumentException(
                    "La inscripción pertenece a otro equipo");
        }

        if (esIntegrante(inscripcion.getSocio())) {
            throw new IllegalArgumentException(
                    "El socio ya está inscrito en el equipo");
        }

        if (!tieneCupo()) {
            throw new IllegalStateException(
                    "El equipo no tiene cupos disponibles");
        }

        inscripciones.add(inscripcion);
    }

    // Inscripciones leídas del archivo: el estado del equipo y el cupo ya se validaron al crearlas
    public void cargarInscripcion(
            InscripcionEquipo inscripcion) {

        if (inscripcion == null
                || inscripcion.getEquipo() != this) {

            throw new IllegalArgumentException(
                    "La inscripción pertenece a otro equipo");
        }

        if (esIntegrante(inscripcion.getSocio())) {
            throw new IllegalArgumentException(
                    "El socio ya está inscrito en el equipo");
        }

        inscripciones.add(inscripcion);
    }

    // Un socio retirado deja de ser integrante y puede volver a inscribirse
    public boolean esIntegrante(Socio socio) {
        InscripcionEquipo existente = buscarInscripcion(socio);

        return existente != null
                && existente.getEstado() != EstadoInscripcion.RETIRADA;
    }

    public boolean tieneCupo() {
        return contarMiembros() < cupoMaximo;
    }

    // Los suspendidos siguen ocupando su cupo; solo los retirados lo liberan
    public int contarMiembros() {

        int miembros = 0;

        for (InscripcionEquipo inscripcion : inscripciones) {

            if (inscripcion.getEstado() != EstadoInscripcion.RETIRADA) {
                miembros++;
            }
        }

        return miembros;
    }

    public boolean cumpleMinimoJugadores() {
        return deporte.cumpleMinimoJugadores(
                getJugadoresHabilitados().size());
    }

    public int calcularJugadoresFaltantes() {
        int faltantes = deporte.getMinimoJugadores()
                - getJugadoresHabilitados().size();

        return Math.max(0, faltantes);
    }

    public InscripcionEquipo buscarInscripcion(
            Socio socio) {

        if (socio == null) {
            return null;
        }

        InscripcionEquipo encontrada = null;

        // Si el socio se retiró y volvió a entrar, cuenta la inscripción vigente
        for (InscripcionEquipo inscripcion : inscripciones) {

            if (inscripcion.getSocio() == socio) {
                encontrada = inscripcion;

                if (inscripcion.getEstado() != EstadoInscripcion.RETIRADA) {
                    return inscripcion;
                }
            }
        }

        return encontrada;
    }

    public List<Socio> getJugadoresHabilitados() {

        List<Socio> jugadoresHabilitados = new ArrayList<Socio>();

        for (InscripcionEquipo inscripcion : inscripciones) {

            if (inscripcion.estaActiva()) {
                jugadoresHabilitados.add(
                        inscripcion.getSocio());
            }
        }

        return jugadoresHabilitados;
    }

    public String getNombre() {
        return nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public boolean isActivo() {
        return activo;
    }

    public DeporteConjunto getDeporte() {
        return deporte;
    }

    public List<InscripcionEquipo> getInscripciones() {

        return new ArrayList<InscripcionEquipo>(
                inscripciones);
    }

    public void activar() {
        activo = true;
    }

    public void desactivar() {
        activo = false;
    }

    public void actualizarPuntajeHistorico(
            double puntajeHistorico) {

        if (puntajeHistorico < 0) {
            throw new IllegalArgumentException(
                    "El puntaje histórico no puede ser negativo");
        }

        this.puntajeHistorico = puntajeHistorico;
    }

    // Los resultados del equipo (partidos de competencia y torneos) suman al histórico
    public void sumarPuntajeHistorico(double puntos) {
        if (puntos < 0) {
            throw new IllegalArgumentException(
                    "Los puntos a sumar no pueden ser negativos");
        }

        this.puntajeHistorico += puntos;
    }

    public double getPuntajeHistorico() {
        return puntajeHistorico;
    }

    @Override
    public String toString() {
        return nombre + " - " + categoria
                + " - " + deporte.getNombre();
    }
}
