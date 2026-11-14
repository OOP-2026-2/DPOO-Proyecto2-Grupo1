package clubdeportivo.torneos;

import java.util.List;

public interface PoliticaPremio {
    List<PremioTorneo> generarPremios(InscripcionTorneo ganador, double recaudo);
}
