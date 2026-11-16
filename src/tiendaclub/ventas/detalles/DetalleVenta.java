package tiendaclub.ventas.detalles;

import tiendaclub.inventario.Producto;

public class DetalleVenta {
    private final Producto producto;
    private final int cantidad;
    private final double precioUnitario;
    private final double subtotal;

    public DetalleVenta(Producto producto, int cantidad) {
        this(producto, cantidad, producto == null ? 0 : producto.getPrecio());
    }

    // Ventas leídas del archivo: conservan el precio que tenía el producto ese día
    public DetalleVenta(Producto producto, int cantidad, double precioUnitario) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero");
        }

        if (precioUnitario < 0) {
            throw new IllegalArgumentException(
                    "El precio unitario no puede ser negativo");
        }

        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = calcularSubtotal();
    }

    public double calcularSubtotal() {
        return precioUnitario * cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    @Override
    public String toString() {
        return producto.getNombre()
                + " x" + cantidad
                + " - $" + subtotal;
    }
}