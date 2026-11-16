package clubdeportivo.torneos;

public interface PremioTorneo {
    InscripcionTorneo getGanador();

    double getValor();

    boolean fueEntregado();

    // Texto para mostrar el premio al usuario
    String getDescripcion();
}