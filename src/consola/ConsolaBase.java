package consola;

import static consola.InputHandler.readBoolean;
import static consola.InputHandler.readEnum;
import static consola.InputHandler.readInt;
import static consola.InputHandler.readString;

import java.time.LocalDate;
import java.util.List;

import clubdeportivo.ClubDeportivo;
import clubdeportivo.entrenamientos.RegistroAsistencia;
import clubdeportivo.entrenamientos.SesionEntrenamiento;
import clubdeportivo.torneos.EncuentroTorneo;
import clubdeportivo.torneos.EstadoTorneo;
import clubdeportivo.torneos.InscripcionTorneo;
import clubdeportivo.torneos.PosicionTorneo;
import clubdeportivo.torneos.PremioTorneo;
import clubdeportivo.torneos.TipoCupo;
import clubdeportivo.torneos.Torneo;
import clubdeportivo.torneos.TorneoConjunto;
import clubdeportivo.torneos.TorneoIndividual;
import tiendaclub.inventario.ExistenciaInventario;
import tiendaclub.inventario.Producto;
import tiendaclub.inventario.UbicacionInventario;
import tiendaclub.ventas.Venta;
import tiendaclub.ventas.descuentos.PoliticaDescuento;
import tiendaclub.ventas.detalles.DetalleVenta;
import tiendaclub.ventas.tipos.VentaCafeteria;
import tiendaclub.ventas.tipos.VentaTienda;
import usuarios.Comprador;
import usuarios.tipos.Socio;
import utilidades.excepciones.InventarioInsuficienteException;
import utilidades.excepciones.ReservaNoDisponibleException;

/*
 * Código común a las tres consolas (herencia, guía 4.1): el ciclo del menú,
 * el manejo de errores y las pantallas que comparten (torneos, compras y sesiones).
 * Cada consola solo define su menú y qué hace cada opción.
 */
public abstract class ConsolaBase implements Subconsola {
    protected final ClubDeportivo club;

    protected ConsolaBase(ClubDeportivo club) {
        if (club == null) {
            throw new IllegalArgumentException("El club es obligatorio");
        }

        this.club = club;
    }

    @Override
    public void ejecutar() {
        boolean ejecutando = true;

        while (ejecutando) {
            mostrarMenu();
            int opcion = readInt("Seleccione una opción: ");

            if (opcion == 0) {
                ejecutando = false;
            } else {
                try {
                    if (!ejecutarOpcion(opcion)) {
                        System.out.println("Opción inválida.");
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("No fue posible realizar la operación: " + e.getMessage());
                } catch (InventarioInsuficienteException | ReservaNoDisponibleException e) {
                    System.out.println("No fue posible realizar la operación: " + e.getMessage());
                }
            }
        }
    }

    protected abstract void mostrarMenu();

    // Devuelve false si la opción no existe en el menú de la consola
    protected abstract boolean ejecutarOpcion(int opcion)
            throws InventarioInsuficienteException, ReservaNoDisponibleException;

    // Utilidades de presentación

    protected String dinero(double valor) {
        return String.format("$%,.2f", valor);
    }

    protected String puntos(double valor) {
        return String.format("%.2f", valor);
    }

    protected void titulo(String texto) {
        System.out.println();
        System.out.println("--- " + texto + " ---");
    }

    // Lee un número de la lista mostrada (1..n); 0 cancela y devuelve null
    protected <T> T elegirDeLista(List<T> elementos, String prompt) {
        if (elementos.isEmpty()) {
            return null;
        }

        int numero = InputHandler.readIntEnRango(
                prompt + " (0 para cancelar): ", 0, elementos.size());

        if (numero == 0) {
            return null;
        }

        return elementos.get(numero - 1);
    }

    // Torneos

    protected void mostrarTorneo(Torneo torneo) {
        System.out.println();
        System.out.println("ID: " + torneo.getId() + " | " + torneo.getNombre());
        System.out.println("Tipo: " + torneo.getTipo()
                + " | Formato: " + torneo.getFormato()
                + " | Estado: " + torneo.getEstado());

        if (torneo instanceof TorneoConjunto) {
            TorneoConjunto conjunto = (TorneoConjunto) torneo;
            System.out.println("Deporte: " + conjunto.getDeporte().getNombre()
                    + " | Categoría: " + conjunto.getCategoria());
        } else if (torneo instanceof TorneoIndividual) {
            TorneoIndividual individual = (TorneoIndividual) torneo;
            System.out.println("Deporte: " + individual.getDeporte().getNombre()
                    + " | Nivel: " + individual.getNivel()
                    + " | Modalidad: " + individual.getModalidad());
        }

        System.out.println("Fecha: " + torneo.getFechaInicio()
                + " | Horario: " + torneo.getHoraInicio() + " - " + torneo.getHoraFin());
        System.out.println("Capacidad: " + torneo.getCapacidad()
                + " | Inscripciones activas: " + torneo.cantidadInscripcionesActivas()
                + " | Cupos disponibles: " + torneo.cantidadCuposDisponibles()
                + " (preferenciales: " + torneo.getCuposPreferenciales()
                + (torneo.isCuposPreferencialesLiberados() ? ", ya liberados" : "") + ")");
    }

    protected void mostrarHistorialTorneo(Torneo torneo) {
        System.out.println("Inscripciones:");

        if (torneo.getInscripciones().isEmpty()) {
            System.out.println("- Sin inscripciones");
        }

        for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
            System.out.println("- " + inscripcion.getDescripcion()
                    + " | Cupo: " + inscripcion.getTipoCupo()
                    + " | Estado: " + inscripcion.getEstado());
        }

        if (!torneo.getEncuentros().isEmpty()) {
            System.out.println("Encuentros:");

            for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
                System.out.println("- " + describirEncuentro(encuentro));
            }
        }

        if (!torneo.getPosiciones().isEmpty()) {
            System.out.println("Tabla de posiciones:");

            for (PosicionTorneo posicion : torneo.getPosiciones()) {
                System.out.println("  " + posicion.getPosicion() + ". "
                        + posicion.getInscripcion().getDescripcion()
                        + " | PJ " + posicion.getPartidosJugados()
                        + " | V " + posicion.getVictorias()
                        + " | E " + posicion.getEmpates()
                        + " | D " + posicion.getDerrotas()
                        + " | A favor " + posicion.getPuntosAFavor()
                        + " | En contra " + posicion.getPuntosEnContra()
                        + " | Puntos " + posicion.getPuntos());
            }
        }

        if (torneo.getEstado() == EstadoTorneo.FINALIZADO) {
            mostrarPremios(torneo);
        }
    }

    protected String describirEncuentro(EncuentroTorneo encuentro) {
        return encuentro.getId()
                + " | Ronda " + encuentro.getRonda()
                + " | " + encuentro.getFecha() + " " + encuentro.getHoraInicio()
                + " | " + encuentro.getInscripcionLocal().getDescripcion()
                + " vs " + encuentro.getInscripcionVisitante().getDescripcion()
                + " | " + encuentro.getEstado()
                + (encuentro.getMarcador() == null ? "" : " | Marcador " + encuentro.getMarcador());
    }

    protected void mostrarPremios(Torneo torneo) {
        System.out.println("Ganador: " + torneo.determinarGanador().getDescripcion());
        List<PremioTorneo> premios = torneo.getPremiosOtorgados();

        if (premios.isEmpty()) {
            System.out.println("Premios: ninguno (los bonos son solo para socios y el premio en dinero sale del recaudo)");
            return;
        }

        for (PremioTorneo premio : premios) {
            System.out.println("- " + premio.getDescripcion());
        }
    }

    // Muestra los torneos de la lista y devuelve el que el usuario escoja por ID
    protected Torneo seleccionarTorneo(List<Torneo> torneos) {
        if (torneos.isEmpty()) {
            System.out.println("No hay torneos disponibles.");
            return null;
        }

        System.out.println("Torneos:");

        for (Torneo torneo : torneos) {
            System.out.println("- " + torneo.getId()
                    + " | " + torneo.getNombre()
                    + " | " + torneo.getEstado());
        }

        String id = readString("ID del torneo: ");

        for (Torneo torneo : torneos) {
            if (torneo.getId().equalsIgnoreCase(id)) {
                return torneo;
            }
        }

        System.out.println("No hay un torneo con ese ID en la lista.");
        return null;
    }

    protected TipoCupo seleccionarTipoCupo() {
        return readEnum("Tipos de cupo:", TipoCupo.values());
    }

    protected void mostrarInscripciones(List<InscripcionTorneo> inscripciones) {
        int numero = 1;

        for (InscripcionTorneo inscripcion : inscripciones) {
            System.out.println(numero + ". Torneo: "
                    + inscripcion.getTorneo().getNombre()
                    + " (" + inscripcion.getTorneo().getEstado() + ")"
                    + " | Inscritos: " + inscripcion.getDescripcion()
                    + " | Estado: " + inscripcion.getEstado()
                    + " | Cupo: " + inscripcion.getTipoCupo()
                    + " | Valor: " + dinero(inscripcion.getValorPagado()));

            numero++;
        }
    }

    // Compras en tienda o cafetería

    // Lista lo que hay en inventario y se puede vender en el rubro de la venta
    protected boolean mostrarInventario(Venta venta) {
        boolean hayInventario = false;

        titulo("PRODUCTOS DE " + venta.getRubro().toUpperCase());

        for (UbicacionInventario ubicacion : club.getUbicacionesInventario()) {
            for (ExistenciaInventario existencia : ubicacion.getExistencias()) {
                Producto producto = existencia.getProducto();

                if (existencia.getCantidad() > 0 && venta.admiteProducto(producto)) {
                    hayInventario = true;
                    System.out.println("Producto: " + producto.getNombre()
                            + " (" + producto.getTipoProducto() + ")"
                            + " | Precio: " + dinero(producto.getPrecio())
                            + " | Ubicación: " + ubicacion.getNombre()
                            + " | Cantidad: " + existencia.getCantidad());
                }
            }
        }

        return hayInventario;
    }

    // Una compra puede tener varios productos; todos se toman de la misma ubicación
    protected void registrarCompra(Comprador comprador, boolean cafeteria,
            PoliticaDescuento politica) throws InventarioInsuficienteException {

        Venta venta = cafeteria
                ? new VentaCafeteria(LocalDate.now(), comprador, politica)
                : new VentaTienda(LocalDate.now(), comprador, politica);

        if (!mostrarInventario(venta)) {
            System.out.println("No hay productos disponibles para este tipo de compra.");
            return;
        }

        UbicacionInventario ubicacion = club.buscarUbicacionInventario(
                readString("Ubicación donde compra: "));

        if (ubicacion == null) {
            System.out.println("No existe una ubicación con ese nombre.");
            return;
        }

        boolean agregando = true;

        while (agregando) {
            Producto producto = club.buscarProducto(readString("Nombre del producto: "));

            if (producto == null) {
                System.out.println("No existe un producto con ese nombre.");
            } else {
                int cantidad = InputHandler.readIntEnRango("Cantidad: ", 1, Integer.MAX_VALUE);

                try {
                    venta.agregarDetalle(producto, cantidad);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }

            agregando = readBoolean("¿Desea agregar otro producto? (s/n): ");
        }

        if (venta.getDetalles().isEmpty()) {
            System.out.println("La compra no tiene productos; no se registró.");
            return;
        }

        if (venta instanceof VentaCafeteria) {
            ((VentaCafeteria) venta).finalizarVenta(
                    readBoolean("¿Desea incluir la propina sugerida del 10 %? (s/n): "));
        } else {
            ((VentaTienda) venta).finalizarVenta();
        }

        club.procesarVenta(venta, ubicacion);
        mostrarRecibo(venta);
    }

    protected void mostrarRecibo(Venta venta) {
        titulo("COMPRA REGISTRADA EN " + venta.getRubro().toUpperCase());

        for (DetalleVenta detalle : venta.getDetalles()) {
            System.out.println("- " + detalle.getProducto().getNombre()
                    + " x" + detalle.getCantidad()
                    + " = " + dinero(detalle.getSubtotal()));
        }

        System.out.println("Subtotal: " + dinero(venta.getSubtotal()));
        System.out.println("Descuento: " + dinero(venta.getDescuento()));
        System.out.println("Impuesto: " + dinero(venta.getImpuesto()));

        if (venta.getCargosAdicionales() > 0) {
            System.out.println("Propina: " + dinero(venta.getCargosAdicionales()));
        }

        System.out.println("Total: " + dinero(venta.getTotal()));

        if (venta.getComprador() instanceof Socio) {
            System.out.println("Puntos ganados: " + puntos(venta.getPuntosGenerados()));
        }
    }

    // Sesiones de entrenamiento

    protected void mostrarSesiones(List<SesionEntrenamiento> sesiones, boolean conAsistencia) {
        int numero = 1;

        for (SesionEntrenamiento sesion : sesiones) {
            String grupo = sesion.getEquipo() != null
                    ? "Equipo " + sesion.getEquipo().getNombre()
                    : "Academia de " + sesion.getDeporteIndividual().getNombre();

            System.out.println(numero + ". " + sesion
                    + " | " + grupo
                    + " | Entrenador: " + sesion.getEntrenador().getNombre());

            if (conAsistencia) {
                for (RegistroAsistencia registro : sesion.getRegistros()) {
                    System.out.println("   - " + registro
                            + (registro.getComentario() == null
                                    ? ""
                                    : " (" + registro.getComentario() + ")"));
                }
            }

            numero++;
        }
    }
}
