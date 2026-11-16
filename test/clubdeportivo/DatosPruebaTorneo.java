package clubdeportivo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import clubdeportivo.deportes.DeporteIndividual;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.deportes.Nivel;
import clubdeportivo.torneos.FormatoTorneo;
import clubdeportivo.torneos.InscripcionIndividualTorneo;
import clubdeportivo.torneos.ParticipanteTorneo;
import clubdeportivo.torneos.PremioCompetitivo;
import clubdeportivo.torneos.PremioRecreativo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.TorneoIndividual;
import usuarios.tipos.ParticipanteExterno;

final class DatosPruebaTorneo {
    private DatosPruebaTorneo() {
    }

    static LocalDate fechaTorneo() {
        return LocalDate.now().plusDays(10);
    }

    static DeporteIndividual tenis() {
        return new DeporteIndividual("Tenis", 30);
    }

    // Un torneo solo se inicia desde su fecha: los escenarios que lo inician usan hoy
    static LocalDate fechaHoy() {
        return LocalDate.now();
    }

    static TorneoIndividual torneo(String id, TipoTorneo tipo,
            FormatoTorneo formato, Modalidad modalidad, int capacidad) {
        return torneo(id, tipo, formato, modalidad, capacidad, fechaTorneo());
    }

    static TorneoIndividual torneoDeHoy(String id, TipoTorneo tipo,
            FormatoTorneo formato, Modalidad modalidad, int capacidad) {
        return torneo(id, tipo, formato, modalidad, capacidad, fechaHoy());
    }

    private static TorneoIndividual torneo(String id, TipoTorneo tipo,
            FormatoTorneo formato, Modalidad modalidad, int capacidad,
            LocalDate fecha) {
        return new TorneoIndividual(id, "Torneo " + id, fecha,
                LocalTime.of(10, 0), LocalTime.of(12, 0), formato,
                capacidad, tipo,
                LocalDateTime.of(fecha.minusDays(2), LocalTime.of(8, 0)),
                tipo == TipoTorneo.RECREATIVO
                        ? new PremioRecreativo(0.10, 30)
                        : new PremioCompetitivo(0.50),
                tipo == TipoTorneo.RECREATIVO ? 0 : 50000,
                tenis(), Nivel.INTERMEDIO, modalidad);
    }

    static ParticipanteExterno externo(String id) {
        return new ParticipanteExterno(id, "Participante " + id,
                LocalDate.of(2000, 1, 1), "login-" + id,
                "clave", "contacto-" + id);
    }

    static List<ParticipanteTorneo> lista(ParticipanteTorneo participante) {
        List<ParticipanteTorneo> participantes =
                new ArrayList<ParticipanteTorneo>();
        participantes.add(participante);
        return participantes;
    }

    static InscripcionIndividualTorneo inscribirExterno(
            TorneoIndividual torneo, String id) {
        return torneo.inscribirParticipantes(
                lista(externo(id)), TipoCupo.REGULAR);
    }
}
