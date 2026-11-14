package clubdeportivo.torneos;

public interface ParticipanteTorneo {
    String getId();

    String getNombre();

    boolean puedeRecibirBono();

    boolean puedeRecibirPremioDinero();
}