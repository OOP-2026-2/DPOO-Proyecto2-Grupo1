package clubdeportivo.partidos.estadisticas;

import java.text.Normalizer;
import clubdeportivo.partidos.Partido;
import usuarios.tipos.Socio;

public abstract class EstadisticaJugador {
    private Socio jugador;
    private Partido partido;

    /*
     * Crea la estadística que corresponde al deporte del partido según su nombre.
     * Por ejemplo, "Fútbol" usa EstadisticaFutbol y "Tenis de mesa" usa EstadisticaTenisMesa.
     */
    public static EstadisticaJugador crearPara(String nombreDeporte, Socio jugador, Partido partido) {
        if (nombreDeporte == null) {
            throw new IllegalArgumentException("El deporte es obligatorio");
        }

        String nombre = Normalizer.normalize(nombreDeporte, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();

        if (nombre.contains("futbol") || nombre.contains("futsal")) {
            return new EstadisticaFutbol(jugador, partido);
        }

        if (nombre.contains("balon") || nombre.contains("basket") || nombre.contains("basquet")) {
            return new EstadisticaBaloncesto(jugador, partido);
        }

        if (nombre.contains("volei") || nombre.contains("voley") || nombre.contains("volley")) {
            return new EstadisticaVoleibol(jugador, partido);
        }

        if (nombre.contains("mesa") || nombre.contains("ping")) {
            return new EstadisticaTenisMesa(jugador, partido);
        }

        if (nombre.contains("tenis") || nombre.contains("padel")) {
            return new EstadisticaTenisPadel(jugador, partido);
        }

        throw new IllegalArgumentException(
                "No hay estadísticas definidas para el deporte " + nombreDeporte);
    }

    public EstadisticaJugador(Socio jugador, Partido partido) {
        if (jugador == null) {
            throw new IllegalArgumentException(
                    "El jugador es obligatorio");
        }

        if (partido == null) {
            throw new IllegalArgumentException(
                    "El partido es obligatorio");
        }

        this.jugador = jugador;
        this.partido = partido;
    }

    // Solo las estadísticas con tarjetas pueden generar suspensiones
    public boolean ameritaSuspension() {
        return false;
    }

    public boolean correspondeA(Socio jugador) {
        return this.jugador == jugador;
    }

    // Por defecto la estadística es de un deporte de conjunto; las de tenis la sobrescriben
    public boolean esDeDeporteIndividual() {
        return false;
    }

    public Socio getJugador() {
        return jugador;
    }

    public Partido getPartido() {
        return partido;
    }
}