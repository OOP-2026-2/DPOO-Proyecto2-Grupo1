package utilidades.excepciones;

// Se lanza cuando una ubicación no tiene las unidades que se piden.
// Es verificada (checked): quien la provoca debe capturarla o declararla con throws
public class InventarioInsuficienteException extends Exception {
    private static final long serialVersionUID = 1L;

    public InventarioInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
