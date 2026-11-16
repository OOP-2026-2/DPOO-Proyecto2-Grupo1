# Proyecto 2 Club Deportivo Séneca

## Contenido de la entrega documental

- `Documentacion_Final_Proyecto_2_Club_Deportivo_Seneca.docx`: documento editable con requisitos, historias de usuario, reglas, diseño, pruebas, ejecución, trazabilidad y anexo de inteligencia artificial.
- `Documentacion_Final_Proyecto_2_Club_Deportivo_Seneca.pdf`: versión lista para lectura y entrega.
- `DiagramasSecuencia/Diagramas_Secuencia_Proyecto_2.drawio.xml`: fuente editable de Draw.io con doce páginas.
- `DiagramasSecuencia/*.png`: una imagen legible por cada diagrama de secuencia.

## Ejecución en Eclipse

Configuración según la guía del curso (sección 1.1): proyecto Java normal con JUnit 5 en el Build Path.

1. **File > Import > General > Existing Projects into Workspace**, elegir la carpeta del proyecto y finalizar. Los archivos `.project` y `.classpath` ya dejan `src` y `test` como carpetas de código fuente, `bin` como salida y la biblioteca **JUnit 5** en el Build Path.
   - Si se crea el proyecto a mano: **File > New > Java Project** (JavaSE-1.8 o JavaSE-17), quitar la carpeta `src` que Eclipse crea, copiar las carpetas `src`, `test` y `datos` del repositorio, marcar `src` y `test` como *Source Folder* y agregar JUnit 5 con **Build Path > Add Libraries... > JUnit > JUnit 5**.
2. Para probar con datos de ejemplo, copiar la carpeta `datos_ejemplo` en la raíz del proyecto con el nombre `datos`.
3. Ejecutar `consola.Main` (`src/consola/Main.java`) con **Run As > Java Application** para escoger el rol, o ejecutar `consola.MainAdministrador`, `consola.MainEmpleado` o `consola.MainParticipante`.
4. Salir mediante la opción `0` para guardar los cambios en la carpeta `datos`. Si algún archivo de `datos` tiene información inválida, el programa lo informa y no guarda nada, para no sobrescribir los datos.
5. Ejecutar las pruebas con clic derecho sobre la carpeta `test` > **Run As > JUnit Test** (232 pruebas). La guía paso a paso de las pruebas por consola está en `GUIA_PRUEBAS_CONSOLA.md`.

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
