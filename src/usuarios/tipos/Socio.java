package usuarios.tipos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import clubdeportivo.membresias.PagoMensualidad;
import clubdeportivo.torneos.BonoDescuento;
import clubdeportivo.torneos.ParticipanteTorneo;
import usuarios.Comprador;
import usuarios.Usuario;

public class Socio extends Usuario implements Comprador, ParticipanteTorneo {
    private double puntosFidelidad;
    private final List<PagoMensualidad> pagos;
    private final List<BonoDescuento> bonos;

    public Socio(String id, String nombre, LocalDate fechaNacimiento, String login, String contrasena) {
        super(id, nombre, fechaNacimiento, login, contrasena);
        this.puntosFidelidad = 0;
        this.pagos = new ArrayList<PagoMensualidad>();
        this.bonos = new ArrayList<BonoDescuento>();
    }

    public void agregarPuntos(double puntos) {
        if (puntos > 0) {
            puntosFidelidad += puntos;
        }
    }

    public boolean redimirPuntos(double puntos) {
        if (puntos <= 0 || puntos > puntosFidelidad) {
            return false;
        }

        puntosFidelidad -= puntos;
        return true;
    }

    public void agregarPago(PagoMensualidad pago) {
        if (pago != null && !pagos.contains(pago)) {
            pagos.add(pago);
        }
    }

    public void agregarBono(BonoDescuento bono) {
        if (bono == null) {
            throw new IllegalArgumentException("El bono no puede ser nulo");
        }

        if (bono.getSocio() != this) {
            throw new IllegalArgumentException("El bono pertenece a otro socio");
        }

        if (!bonos.contains(bono)) {
            bonos.add(bono);
        }
    }

    @Override
    public double getPorcentajeDescuento() {
        return 0;
    }

    @Override
    public boolean puedeRecibirBono() {
        return true;
    }

    @Override
    public boolean puedeRecibirPremioDinero() {
        return true;
    }

    public double getPuntosFidelidad() {
        return puntosFidelidad;
    }

    public List<PagoMensualidad> getPagos() {
        return new ArrayList<PagoMensualidad>(pagos);
    }

    public List<BonoDescuento> getBonos() {
        return new ArrayList<BonoDescuento>(bonos);
    }

    public List<BonoDescuento> getBonosDisponibles() {
        List<BonoDescuento> disponibles = new ArrayList<BonoDescuento>();

        for (BonoDescuento bono : bonos) {
            if (bono.estaDisponible()) {
                disponibles.add(bono);
            }
        }

        return disponibles;
    }
}