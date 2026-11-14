package consola;

import static consola.InputHandler.readDate;
import static consola.InputHandler.readDouble;
import static consola.InputHandler.readInt;
import static consola.InputHandler.readString;
import static consola.InputHandler.readTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import clubdeportivo.ClubDeportivo;
import clubdeportivo.deportes.Categoria;
import clubdeportivo.deportes.DeporteConjunto;
import clubdeportivo.deportes.DeporteIndividual;
import clubdeportivo.deportes.Modalidad;
import clubdeportivo.deportes.Nivel;
import clubdeportivo.torneos.EncuentroTorneo;
import clubdeportivo.torneos.EstadoTorneo;
import clubdeportivo.torneos.FormatoTorneo;
import clubdeportivo.torneos.PoliticaPremio;
import clubdeportivo.torneos.PremioCompetitivo;
import clubdeportivo.torneos.PremioRecreativo;
import clubdeportivo.torneos.PremioTorneo;
import clubdeportivo.torneos.TipoTorneo;
import clubdeportivo.torneos.Torneo;
import clubdeportivo.torneos.TorneoConjunto;
import clubdeportivo.torneos.TorneoIndividual;
import usuarios.tipos.empleados.Administrador;

public class ConsolaAdministrador implements Subconsola {
    private final ClubDeportivo club;
    private final Administrador administrador;

    public ConsolaAdministrador(ClubDeportivo club, Administrador administrador) {
        if (club == null || administrador == null) {
            throw new IllegalArgumentException("El club y el administrador son obligatorios");
        }

        this.club = club;
        this.administrador = administrador;
    }

    @Override
    public void ejecutar() {
        boolean ejecutando = true;

        while (ejecutando) {
            mostrarMenu();
            int opcion = readInt("Seleccione una opción: ");

            try {
                switch (opcion) {
                    case 1:
                        registrarTorneoConjunto();
                        break;
                    case 2:
                        registrarTorneoIndividual();
                        break;
                    case 3:
                        consultarTorneos();
                        break;
                    case 4:
                        liberarCupos();
                        break;
                    case 5:
                        iniciarTorneo();
                        break;
                    case 6:
                        registrarResultado();
                        break;
                    case 7:
                        finalizarTorneo();
                        break;
                    case 0:
                        ejecutando = false;
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("No fue posible realizar la operación: " + e.getMessage());
            }
        }
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("--- CONSOLA DEL ADMINISTRADOR ---");
        System.out.println("Administrador: " + administrador.getNombre());
        System.out.println("1. Registrar torneo de conjunto");
        System.out.println("2. Registrar torneo individual");
        System.out.println("3. Consultar torneos");
        System.out.println("4. Liberar cupos preferenciales");
        System.out.println("5. Iniciar torneo");
        System.out.println("6. Registrar resultado");
        System.out.println("7. Finalizar torneo");
        System.out.println("0. Salir");
    }

    private void registrarTorneoConjunto() {
        System.out.println();
        System.out.println("--- REGISTRAR TORNEO DE CONJUNTO ---");

        DatosTorneo datos = leerDatosTorneo();
        String nombreDeporte = readString("Nombre del deporte de conjunto: ");
        DeporteConjunto deporte = club.buscarDeporteConjunto(nombreDeporte);

        if (deporte == null) {
            System.out.println("No existe ese deporte de conjunto.");
            return;
        }

        mostrarCategorias();
        Categoria categoria = Categoria.valueOf(
                readString("Categoría: ").toUpperCase());

        TorneoConjunto torneo = new TorneoConjunto(
                datos.id,
                datos.nombre,
                datos.fechaInicio,
                datos.horaInicio,
                datos.horaFin,
                datos.formato,
                datos.capacidad,
                datos.tipo,
                datos.fechaLiberacion,
                datos.politicaPremio,
                datos.tarifaInscripcion,
                deporte,
                categoria);

        club.registrarTorneo(torneo);
        System.out.println("Torneo de conjunto registrado correctamente.");
    }

    private void registrarTorneoIndividual() {
        System.out.println();
        System.out.println("--- REGISTRAR TORNEO INDIVIDUAL ---");

        DatosTorneo datos = leerDatosTorneo();
        String nombreDeporte = readString("Nombre del deporte individual: ");
        DeporteIndividual deporte = club.buscarDeporteIndividual(nombreDeporte);

        if (deporte == null) {
            System.out.println("No existe ese deporte individual.");
            return;
        }

        mostrarNiveles();
        Nivel nivel = Nivel.valueOf(
                readString("Nivel: ").toUpperCase());

        mostrarModalidades();
        Modalidad modalidad = Modalidad.valueOf(
                readString("Modalidad: ").toUpperCase());

        TorneoIndividual torneo = new TorneoIndividual(
                datos.id,
                datos.nombre,
                datos.fechaInicio,
                datos.horaInicio,
                datos.horaFin,
                datos.formato,
                datos.capacidad,
                datos.tipo,
                datos.fechaLiberacion,
                datos.politicaPremio,
                datos.tarifaInscripcion,
                deporte,
                nivel,
                modalidad);

        club.registrarTorneo(torneo);
        System.out.println("Torneo individual registrado correctamente.");
    }

    private DatosTorneo leerDatosTorneo() {
        String id = readString("ID del torneo: ");
        String nombre = readString("Nombre del torneo: ");
        LocalDate fechaInicio = readDate("Fecha de inicio (AAAA-MM-DD): ");
        LocalTime horaInicio = readTime("Hora de inicio (HH:MM): ");
        LocalTime horaFin = readTime("Hora de finalización (HH:MM): ");

        mostrarFormatos();
        FormatoTorneo formato = FormatoTorneo.valueOf(
                readString("Formato: ").toUpperCase());

        int capacidad = readInt("Capacidad de inscripciones: ");

        mostrarTiposTorneo();
        TipoTorneo tipo = TipoTorneo.valueOf(
                readString("Tipo de torneo: ").toUpperCase());

        LocalDate fechaLiberacion = readDate(
                "Fecha de liberación de cupos (AAAA-MM-DD): ");

        LocalTime horaLiberacion = readTime(
                "Hora de liberación de cupos (HH:MM): ");

        LocalDateTime fechaHoraLiberacion = LocalDateTime.of(
                fechaLiberacion, horaLiberacion);

        PoliticaPremio politicaPremio;
        double tarifaInscripcion;

        if (tipo == TipoTorneo.RECREATIVO) {
            double porcentaje = readDouble(
                    "Porcentaje del bono, por ejemplo 0.10: ");

            int diasVigencia = readInt(
                    "Días de vigencia del bono: ");

            politicaPremio = new PremioRecreativo(
                    porcentaje, diasVigencia);

            tarifaInscripcion = 0;
        } else {
            double porcentajeRecaudo = readDouble(
                    "Porcentaje del recaudo para el premio, por ejemplo 0.50: ");

            politicaPremio = new PremioCompetitivo(
                    porcentajeRecaudo);

            tarifaInscripcion = readDouble(
                    "Tarifa de inscripción: ");
        }

        return new DatosTorneo(
                id,
                nombre,
                fechaInicio,
                horaInicio,
                horaFin,
                formato,
                capacidad,
                tipo,
                fechaHoraLiberacion,
                politicaPremio,
                tarifaInscripcion);
    }

    private void consultarTorneos() {
        System.out.println();
        System.out.println("--- TORNEOS REGISTRADOS ---");

        if (club.getTorneos().isEmpty()) {
            System.out.println("No hay torneos registrados.");
            return;
        }

        for (Torneo torneo : club.getTorneos()) {
            System.out.println();
            System.out.println("ID: " + torneo.getId());
            System.out.println("Nombre: " + torneo.getNombre());
            System.out.println("Tipo: " + torneo.getTipo());
            System.out.println("Formato: " + torneo.getFormato());
            System.out.println("Fecha: " + torneo.getFechaInicio());
            System.out.println("Horario: " + torneo.getHoraInicio()
                    + " - " + torneo.getHoraFin());
            System.out.println("Estado: " + torneo.getEstado());
            System.out.println("Capacidad: " + torneo.getCapacidad());
            System.out.println("Inscripciones activas: "
                    + torneo.cantidadInscripcionesActivas());
            System.out.println("Cupos disponibles: "
                    + torneo.cantidadCuposDisponibles());
            System.out.println("Recaudo: " + torneo.calcularRecaudo());
        }
    }

    private void liberarCupos() {
        Torneo torneo = seleccionarTorneo();

        if (torneo == null) {
            return;
        }

        club.liberarCuposPreferenciales(torneo);
        System.out.println("Cupos preferenciales liberados correctamente.");
    }

    private void iniciarTorneo() {
        Torneo torneo = seleccionarTorneo();

        if (torneo == null) {
            return;
        }

        club.iniciarTorneo(torneo);
        System.out.println("Torneo iniciado correctamente.");

        if (torneo.getEncuentros().isEmpty()) {
            System.out.println("No se generaron encuentros.");
        } else {
            System.out.println("Encuentros generados:");

            for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
                System.out.println("- " + encuentro.getId()
                        + " | " + encuentro.getFecha()
                        + " | " + encuentro.getHoraInicio());
            }
        }
    }

    private void registrarResultado() {
        Torneo torneo = seleccionarTorneo();

        if (torneo == null) {
            return;
        }

        if (torneo.getEstado() != EstadoTorneo.EN_CURSO) {
            System.out.println("El torneo no se encuentra en curso.");
            return;
        }

        EncuentroTorneo encuentro = seleccionarEncuentro(torneo);

        if (encuentro == null) {
            return;
        }

        String marcador = readString(
                "Marcador, por ejemplo 2-1: ");

        club.registrarResultadoEncuentro(
                encuentro, marcador);

        System.out.println("Resultado registrado correctamente.");
    }

    private void finalizarTorneo() {
        Torneo torneo = seleccionarTorneo();

        if (torneo == null) {
            return;
        }

        List<PremioTorneo> premios = club.finalizarTorneo(
                torneo);

        System.out.println("Torneo finalizado correctamente.");
        System.out.println("Premios generados: " + premios.size());

        for (PremioTorneo premio : premios) {
            System.out.println("- Valor del premio: "
                    + premio.getValor());
        }
    }

    private Torneo seleccionarTorneo() {
        if (club.getTorneos().isEmpty()) {
            System.out.println("No hay torneos registrados.");
            return null;
        }

        System.out.println();
        System.out.println("Torneos disponibles:");

        for (Torneo torneo : club.getTorneos()) {
            System.out.println("- " + torneo.getId()
                    + " | " + torneo.getNombre()
                    + " | " + torneo.getEstado());
        }

        String id = readString("ID del torneo: ");
        Torneo torneo = club.buscarTorneo(id);

        if (torneo == null) {
            System.out.println("No existe un torneo con ese ID.");
        }

        return torneo;
    }

    private EncuentroTorneo seleccionarEncuentro(
            Torneo torneo) {

        if (torneo.getEncuentros().isEmpty()) {
            System.out.println("El torneo no tiene encuentros.");
            return null;
        }

        System.out.println("Encuentros disponibles:");

        for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
            System.out.println("- " + encuentro.getId()
                    + " | " + encuentro.getEstado());
        }

        String id = readString("ID del encuentro: ");

        for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
            if (encuentro.getId().equalsIgnoreCase(id)) {
                return encuentro;
            }
        }

        System.out.println("No existe un encuentro con ese ID.");
        return null;
    }

    private void mostrarFormatos() {
        System.out.println("Formatos disponibles:");

        for (FormatoTorneo formato : FormatoTorneo.values()) {
            System.out.println("- " + formato);
        }
    }

    private void mostrarTiposTorneo() {
        System.out.println("Tipos disponibles:");

        for (TipoTorneo tipo : TipoTorneo.values()) {
            System.out.println("- " + tipo);
        }
    }

    private void mostrarCategorias() {
        System.out.println("Categorías disponibles:");

        for (Categoria categoria : Categoria.values()) {
            System.out.println("- " + categoria);
        }
    }

    private void mostrarNiveles() {
        System.out.println("Niveles disponibles:");

        for (Nivel nivel : Nivel.values()) {
            System.out.println("- " + nivel);
        }
    }

    private void mostrarModalidades() {
        System.out.println("Modalidades disponibles:");

        for (Modalidad modalidad : Modalidad.values()) {
            System.out.println("- " + modalidad);
        }
    }

    private static class DatosTorneo {
        private final String id;
        private final String nombre;
        private final LocalDate fechaInicio;
        private final LocalTime horaInicio;
        private final LocalTime horaFin;
        private final FormatoTorneo formato;
        private final int capacidad;
        private final TipoTorneo tipo;
        private final LocalDateTime fechaLiberacion;
        private final PoliticaPremio politicaPremio;
        private final double tarifaInscripcion;

        private DatosTorneo(String id, String nombre,
                LocalDate fechaInicio, LocalTime horaInicio,
                LocalTime horaFin, FormatoTorneo formato,
                int capacidad, TipoTorneo tipo,
                LocalDateTime fechaLiberacion,
                PoliticaPremio politicaPremio,
                double tarifaInscripcion) {

            this.id = id;
            this.nombre = nombre;
            this.fechaInicio = fechaInicio;
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
            this.formato = formato;
            this.capacidad = capacidad;
            this.tipo = tipo;
            this.fechaLiberacion = fechaLiberacion;
            this.politicaPremio = politicaPremio;
            this.tarifaInscripcion = tarifaInscripcion;
        }
    }
}