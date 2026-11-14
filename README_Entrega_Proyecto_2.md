# Proyecto 2 Club Deportivo Séneca

## Contenido de la entrega documental

- `Documentacion_Final_Proyecto_2_Club_Deportivo_Seneca.docx`: documento editable con requisitos, historias de usuario, reglas, diseño, pruebas, ejecución, trazabilidad y anexo de inteligencia artificial.
- `Documentacion_Final_Proyecto_2_Club_Deportivo_Seneca.pdf`: versión lista para lectura y entrega.
- `DiagramasSecuencia/Diagramas_Secuencia_Proyecto_2.drawio.xml`: fuente editable de Draw.io con doce páginas.
- `DiagramasSecuencia/*.png`: una imagen legible por cada diagrama de secuencia.

## Ejecución en Eclipse

1. Seleccionar **File > Import > Maven > Existing Maven Projects**.
2. Elegir la carpeta que contiene `pom.xml`.
3. Ejecutar `src/Main.java` para escoger el rol, o ejecutar `consola.MainAdministrador`, `consola.MainEmpleado` o `consola.MainParticipante`.
4. Salir mediante la opción `0` para guardar los cambios en la carpeta `datos`.
5. Ejecutar la carpeta `test` mediante **Run As > JUnit Test**.

## Diagramas de secuencia

El archivo editable contiene una página para cada flujo: autenticación, creación de torneo, consulta, inscripción individual, inscripción de equipo, retiro, liberación de cupos e inicio, registro de resultado, finalización y premios, compra con bono, participación de empleado y persistencia automática.
