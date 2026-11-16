package clubdeportivo.torneos;

public class PosicionTorneo {
    private final InscripcionTorneo inscripcion;
    private int partidosJugados;
    private int victorias;
    private int empates;
    private int derrotas;
    private int puntos;
    private int posicion;
    private int puntosAFavor;
    private int puntosEnContra;

    public PosicionTorneo(
            InscripcionTorneo inscripcion) {

        this(inscripcion, 0, 0, 0, 0, 0, 0);
    }

    public PosicionTorneo(
            InscripcionTorneo inscripcion,
            int partidosJugados,
            int victorias,
            int empates,
            int derrotas,
            int puntos,
            int posicion) {

        this(inscripcion, partidosJugados, victorias, empates,
                derrotas, puntos, posicion, 0, 0);
    }

    public PosicionTorneo(
            InscripcionTorneo inscripcion,
            int partidosJugados,
            int victorias,
            int empates,
            int derrotas,
            int puntos,
            int posicion,
            int puntosAFavor,
            int puntosEnContra) {

        if (puntosAFavor < 0 || puntosEnContra < 0) {
            throw new IllegalArgumentException(
                    "Los puntos a favor y en contra no pueden ser negativos");
        }

        this.puntosAFavor = puntosAFavor;
        this.puntosEnContra = puntosEnContra;

        if (inscripcion == null) {
            throw new IllegalArgumentException(
                    "La inscripción es obligatoria");
        }

        if (partidosJugados < 0
                || victorias < 0
                || empates < 0
                || derrotas < 0
                || puntos < 0
                || posicion < 0) {

            throw new IllegalArgumentException(
                    "Los valores de la posición "
                            + "no pueden ser negativos");
        }

        this.inscripcion = inscripcion;
        this.partidosJugados = partidosJugados;
        this.victorias = victorias;
        this.empates = empates;
        this.derrotas = derrotas;
        this.puntos = puntos;
        this.posicion = posicion;
    }

    public void registrarVictoria(int puntos) {
        if (puntos < 0) {
            throw new IllegalArgumentException(
                    "Los puntos no pueden ser negativos");
        }

        partidosJugados++;
        victorias++;
        this.puntos += puntos;
    }

    public void registrarEmpate(int puntos) {
        if (puntos < 0) {
            throw new IllegalArgumentException(
                    "Los puntos no pueden ser negativos");
        }

        partidosJugados++;
        empates++;
        this.puntos += puntos;
    }

    public void registrarDerrota() {
        partidosJugados++;
        derrotas++;
    }

    public void cambiarPosicion(int posicion) {
        if (posicion <= 0) {
            throw new IllegalArgumentException(
                    "La posición debe ser mayor que cero");
        }

        this.posicion = posicion;
    }

    public InscripcionTorneo getInscripcion() {
        return inscripcion;
    }

    public int getPartidosJugados() {
        return partidosJugados;
    }

    public int getVictorias() {
        return victorias;
    }

    public int getEmpates() {
        return empates;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getPosicion() {
        return posicion;
    }

    // Guarda el marcador del encuentro para los criterios de desempate
    public void acumularMarcador(int aFavor, int enContra) {
        if (aFavor < 0 || enContra < 0) {
            throw new IllegalArgumentException(
                    "El marcador no puede tener valores negativos");
        }

        puntosAFavor += aFavor;
        puntosEnContra += enContra;
    }

    public int getPuntosAFavor() {
        return puntosAFavor;
    }

    public int getPuntosEnContra() {
        return puntosEnContra;
    }

    public int getDiferencia() {
        return puntosAFavor - puntosEnContra;
    }
}