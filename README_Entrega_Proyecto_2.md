# Proyecto 2 Club Deportivo Séneca

## Contenido de la entrega documental

- `Documentacion_Final_Proyecto_2_Club_Deportivo_Seneca.docx`: documento editable con requisitos, historias de usuario, reglas, diseño, pruebas, ejecución, trazabilidad y anexo de inteligencia artificial.
- `Documentacion_Final_Proyecto_2_Club_Deportivo_Seneca.pdf`: versión lista para lectura y entrega.
- `DiagramasSecuencia/Diagramas_Secuencia_Proyecto_2.drawio.xml`: fuente editable de Draw.io con doce páginas.
- `DiagramasSecuencia/*.png`: una imagen legible por cada diagrama de secuencia.

## Ejecución en Eclipse

1. Seleccionar **File > Import > Maven > Existing Maven Projects**.
2. Elegir la carpeta que contiene `pom.xml`.
3. (Opcional) Para probar con datos de ejemplo, copiar la carpeta `datos_ejemplo` en la raíz del proyecto con el nombre `datos`.
4. Ejecutar `consola.Main` (`src/consola/Main.java`) para escoger el rol, o ejecutar `consola.MainAdministrador`, `consola.MainEmpleado` o `consola.MainParticipante`.
5. Salir mediante la opción `0` para guardar los cambios en la carpeta `datos`. Si algún archivo de `datos` tiene información inválida, el programa lo informa y no guarda nada, para no sobrescribir los datos.
6. Ejecutar la carpeta `test` mediante **Run As > JUnit Test**.

## Usuarios de los datos de ejemplo

| Rol | Login | Contraseña |
| --- | --- | --- |
| Administrador | `admin` | `admin123` |
| Entrenador | `carlos` / `diana` | `carlos123` / `diana123` |
| Fisioterapeuta | `mateo` | `mateo123` |
| Socio | `ana`, `juan`, `camila`, `andres`, `valentina`, `santiago`, `isabela`, `tomas` | el login seguido de `123` (por ejemplo `ana123`) |
| Estudiante universitario | `sofia` | `sofia123` |
| Participante externo | `pedro` | `pedro123` |

Los datos incluyen dos equipos de fútbol (uno incompleto), academias de tenis y tenis de mesa, una liga ASCUN con un partido jugado (un jugador quedó suspendido por tarjeta roja), dos torneos abiertos para el 29 de noviembre de 2026, el torneo «Relámpago de Tenis de mesa» del 15 de noviembre de 2026 con tres inscritos (ya se puede iniciar desde la opción 5 del administrador), una sesión de entrenamiento, inventario en tres ubicaciones, ventas de varios días para el reporte por fechas y pagos de mensualidad.

## Diagramas de secuencia

El archivo editable contiene una página para cada flujo: autenticación, creación de torneo, consulta, inscripción individual, inscripción de equipo, retiro, liberación de cupos e inicio, registro de resultado, finalización y premios, compra con bono, participación de empleado y persistencia automática.
