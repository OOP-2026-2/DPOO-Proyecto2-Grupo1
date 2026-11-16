package consola;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import clubdeportivo.deportes.Nivel;

@DisplayName("Lectura de datos por consola")
public class InputHandlerTest {
    private PrintStream salidaOriginal;
    private ByteArrayOutputStream salida;

    @BeforeEach
    void capturarSalida() {
        salidaOriginal = System.out;
        salida = new ByteArrayOutputStream();
        System.setOut(new PrintStream(salida));
    }

    @AfterEach
    void restaurarSalida() {
        System.setOut(salidaOriginal);
    }

    @ParameterizedTest(name = "\"{0}\"")
    @ValueSource(strings = { "a;b", "a|b", "a,b" })
    @DisplayName("Un nombre con separadores de archivo se rechaza y se vuelve a pedir")
    void nombreConSeparadores(String invalido) {
        entrada(invalido, "valido");

        assertEquals("valido", InputHandler.readString("Nombre: "));
        assertTrue(salida.toString().contains("no puede contener"));
    }

    @Test
    @DisplayName("Un comentario admite comas pero no punto y coma")
    void textoLibreAdmiteComas() {
        entrada("bien; mal", "Buen trabajo, sigue así");

        assertEquals("Buen trabajo, sigue así", InputHandler.readTextoLibre("Comentario: "));
    }

    @Test
    @DisplayName("Un número decimal se puede escribir con coma")
    void decimalConComa() {
        entrada("0,25");

        assertEquals(0.25, InputHandler.readDouble("Valor: "), 0.0001);
    }

    @Test
    @DisplayName("Un entero inválido se vuelve a pedir")
    void enteroInvalido() {
        entrada("siete", "7");

        assertEquals(7, InputHandler.readInt("Número: "));
    }

    @Test
    @DisplayName("Un entero fuera del rango se vuelve a pedir")
    void enteroFueraDeRango() {
        entrada("9", "2");

        assertEquals(2, InputHandler.readIntEnRango("Opción: ", 1, 3));
    }

    @Test
    @DisplayName("Una opción de enumeración se elige por número o por nombre")
    void enumeracionPorNumeroONombre() {
        entrada("2", "avanzado", "9", "principiante");
        Nivel[] niveles = Nivel.values();

        assertEquals(niveles[1], InputHandler.readEnum("Nivel:", niveles));
        assertEquals(Nivel.AVANZADO, InputHandler.readEnum("Nivel:", niveles));
        assertEquals(Nivel.PRINCIPIANTE, InputHandler.readEnum("Nivel:", niveles));
    }

    @Test
    @DisplayName("En un filtro la opción 0 significa todos")
    void filtroTodos() {
        entrada("0");

        assertNull(InputHandler.readEnumOpcional("Nivel:", Nivel.values()));
    }

    @Test
    @DisplayName("Un periodo inválido se vuelve a pedir")
    void periodoInvalido() {
        entrada("2026-13", "2026-11");

        assertEquals("2026-11", InputHandler.readPeriodo("Periodo: "));
    }

    @Test
    @DisplayName("Una respuesta distinta de s o n se vuelve a pedir")
    void respuestaSiNo() {
        entrada("tal vez", "sí");

        assertTrue(InputHandler.readBoolean("¿Confirma? "));
    }

    @Test
    @DisplayName("Si se acaba la entrada se informa con una excepción")
    void finDeLaEntrada() {
        entrada();

        assertThrows(IllegalStateException.class, () -> InputHandler.readString("Nombre: "));
    }

    private void entrada(String... lineas) {
        new InputHandler(new Scanner(String.join("\n", lineas) + (lineas.length > 0 ? "\n" : "")));
    }
}
