package clubdeportivo.partidos.estadisticas;

import clubdeportivo.partidos.Partido;
import usuarios.tipos.Socio;

public class EstadisticaFutbol extends EstadisticaJugador {
    public static final int AMARILLAS_PARA_SUSPENSION = 2;

    private int goles;
    private int asistencias;
    private int tarjetasAmarillas;
    private int tarjetasRojas;

    public EstadisticaFutbol(Socio socio, Partido partido) {
        super(socio, partido);
    }

    public void registrarGol() {
        goles++;
    }

    public void registrarAsistencia() {
        asistencias++;
    }

    public void registrarTarjetaAmarilla() {
        tarjetasAmarillas++;
    }

    public void registrarTarjetaRoja() {
        tarjetasRojas++;
    }

    public int getGoles() {
        return goles;
    }

    public int getAsistencias() {
        return asistencias;
    }

    public int getTarjetasAmarillas() {
        return tarjetasAmarillas;
    }

    public int getTarjetasRojas() {
        return tarjetasRojas;
    }

    // Una roja o dos amarillas en el mismo partido suspenden al jugador
    @Override
    public boolean ameritaSuspension() {
        return tarjetasRojas >= 1
                || tarjetasAmarillas >= AMARILLAS_PARA_SUSPENSION;
    }
}
