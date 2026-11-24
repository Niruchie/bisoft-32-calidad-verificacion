# BISOFT-32 - Proyecto final

## Ejecutar las pruebas

```bash
mvn test
```

```bash
mvn test -Dheadless=true
```

## Configuración

`src/test/resources/config.properties` define los valores de la ejecución:

```properties
browser=chrome
run.folder=target
headless=true
```

Precedencia: propiedad del sistema (`-Dbrowser=firefox`) > archivo > valor por defecto.

## Reporte HTML

Cada ejecución genera su reporte dentro de la carpeta de ejecución:

- Suite estable: `<runfolder>/reports/execution.html`
- Perfil de defectos: `<runfolder>/reports/defect.html`

La carpeta se toma de `run.folder` en `config.properties` (en la configuración actual,
`target`, por lo que el reporte queda en `target/reports/`). Si no se define, se usa
`target/runs/<timestamp>`. También se puede fijar por línea de comandos:

```bash
mvn test -Dheadless=true -Drunfolder=target/runs/entrega
mvn test -Pdefectos -Dheadless=true -Drunfolder=target/runs/entrega
```

`-Drun.folder` es un alias de `-Drunfolder`. Las capturas de los fallos se guardan en
`<runfolder>/screenshots/` y se incrustan en el HTML. Los tests se ordenan por nombre:
primero `AttributeBy...` y luego `CP-001`, `CP-002`, ... `CP-049`. El reporte usa assets
locales (`reports/spark/`), por lo que debe abrirse desde la carpeta generada.
