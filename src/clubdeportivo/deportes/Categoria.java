package clubdeportivo.deportes;

public enum Categoria {
    SUB_13,
    SUB_15,
    SUB_17,
    MAYORES;

    public boolean admiteEdad(int edad) {
        if (edad < 0) {
            return false;
        }

        switch (this) {
            case SUB_13:
                return edad <= 12;
            case SUB_15:
                return edad >= 13 && edad <= 14;
            case SUB_17:
                return edad >= 15 && edad <= 16;
            case MAYORES:
                return edad >= 17;
            default:
                return false;
        }
    }
}