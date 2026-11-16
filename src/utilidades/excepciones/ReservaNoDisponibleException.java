package utilidades.excepciones;

// Se lanza cuando la instalación no se puede reservar en el horario pedido.
// Es verificada (checked): quien la provoca debe capturarla o declararla con throws
public class ReservaNoDisponibleException extends Exception {
    private static final long serialVersionUID = 1L;

    public ReservaNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
