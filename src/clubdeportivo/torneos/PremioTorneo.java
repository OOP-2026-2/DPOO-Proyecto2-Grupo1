package clubdeportivo.torneos;

public interface PremioTorneo {
    InscripcionTorneo getGanador();

    double getValor();

    boolean fueEntregado();
}