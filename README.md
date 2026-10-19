# eventos-service --- Guía de logging con Quarkus

Este documento resume los conceptos y prácticas de logging trabajados en
`eventos-service`, un servicio desarrollado con **Quarkus 3.40.1**,
Maven y Java.

> **Estado actual:** las pruebas automatizadas pasan (`3` pruebas, `0`
> fallos y `0` errores). La última ejecución mostró errores de conexión
> al exportador OpenTelemetry en `localhost:4317`; la exportación
> requiere un receptor OTLP disponible o debe desactivarse si no se va a
> utilizar.

## Tabla de contenidos

-   [1. Objetivos de aprendizaje](#1-objetivos-de-aprendizaje)
-   [2. Ejecutar el proyecto y las
    pruebas](#2-ejecutar-el-proyecto-y-las-pruebas)
-   [3. API de logging y niveles](#3-api-de-logging-y-niveles)
-   [4. Configuración de niveles y
    categorías](#4-configuración-de-niveles-y-categorías)
-   [5. Formato de consola](#5-formato-de-consola)
-   [6. Logging a archivos y rotación](#6-logging-a-archivos-y-rotación)
-   [7. Configuración por perfiles](#7-configuración-por-perfiles)
-   [8. Correlación de solicitudes con
    MDC](#8-correlación-de-solicitudes-con-mdc)
-   [9. Filtros de logging](#9-filtros-de-logging)
-   [10. Handlers con nombre](#10-handlers-con-nombre)
-   [11. Logging JSON](#11-logging-json)
-   [12. Pruebas automatizadas](#12-pruebas-automatizadas)
-   [13. OpenTelemetry y
    centralización](#13-opentelemetry-y-centralización)
-   [14. Diagnóstico de problemas](#14-diagnóstico-de-problemas)
-   [15. Buenas prácticas](#15-buenas-prácticas)
-   [16. Lista de verificación](#16-lista-de-verificación)

## 1. Objetivos de aprendizaje

Durante el recorrido por la guía de logging de Quarkus se estudiaron
estas capacidades:

-   Registrar eventos de la aplicación con distintos niveles de
    severidad.
-   Configurar niveles globales y específicos para paquetes o
    categorías.
-   Personalizar el formato de salida de consola.
-   Escribir registros en archivos y comprender la rotación.
-   Separar la configuración por perfiles, como desarrollo y producción.
-   Asociar un identificador a una solicitud usando MDC.
-   Filtrar determinados mensajes.
-   Asignar handlers específicos a categorías.
-   Conocer el formato JSON y sus ventajas para procesar logs.
-   Probar endpoints y comportamientos mediante pruebas automatizadas.
-   Comprender cómo se relacionan los logs con OpenTelemetry y las
    plataformas de observabilidad.

## 2. Ejecutar el proyecto y las pruebas

Desde la carpeta raíz de `eventos-service`, en PowerShell:

``` powershell
.\mvnw.cmd test
```

Este comando compila lo necesario y ejecuta las pruebas.

La salida verificada más reciente mostró:

``` text
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

El servicio se inició durante las pruebas en `http://localhost:8081`.

## 3. API de logging y niveles

Quarkus integra JBoss Log Manager y permite registrar mensajes usando la
API de JBoss Logging. También ofrece la API simplificada
`io.quarkus.logging.Log`.

Ejemplo:

``` java
import io.quarkus.logging.Log;

public void procesarSolicitud() {
    Log.info("Solicitud recibida");
    Log.debug("Validación completada");
    Log.warn("La solicitud contiene datos incompletos");
    Log.error("No fue posible procesar la solicitud");
}
```

### Niveles principales

  -----------------------------------------------------------------------
  Nivel                               Uso habitual
  ----------------------------------- -----------------------------------
  `TRACE`                             Información muy detallada para
                                      rastrear la ejecución.

  `DEBUG`                             Detalles útiles durante el
                                      desarrollo y diagnóstico.

  `INFO`                              Eventos normales e importantes de
                                      la aplicación.

  `WARN`                              Situaciones inesperadas que no
                                      necesariamente detienen el proceso.

  `ERROR`                             Fallos que impiden completar una
                                      operación.

  `FATAL`                             Problemas críticos; su
                                      disponibilidad y tratamiento
                                      dependen de la API utilizada.

  `OFF`                               Desactiva el registro para la
                                      configuración correspondiente.

  `ALL`                               Permite todos los niveles en la
                                      configuración correspondiente.
  -----------------------------------------------------------------------

Conviene elegir el nivel según la importancia del evento. No se deben
registrar contraseñas, tokens, datos personales sensibles ni otros
secretos.

## 4. Configuración de niveles y categorías

La configuración principal se encuentra en:

`src/main/resources/application.properties`

Ejemplo de configuración global y por categoría:

``` properties
quarkus.log.level=INFO
quarkus.log.category."com.inklusport".level=DEBUG
```

-   `quarkus.log.level` define el nivel global.
-   `quarkus.log.category."com.inklusport".level` configura el nivel
    para esa categoría o paquete.
-   Una categoría específica permite habilitar mensajes `DEBUG` de una
    parte de la aplicación sin aumentar todo el ruido de logs.

**Importante:** evita declarar la misma propiedad varias veces dentro
del mismo contexto de configuración. Si usas perfiles como `%dev` y
`%prod`, sus valores pueden ser distintos de forma intencional.

## 5. Formato de consola

Se puede personalizar la salida para mostrar la hora, el nivel, la
categoría, el hilo, el identificador de solicitud y el mensaje.

Ejemplo:

``` properties
quarkus.log.console.format=%d{HH:mm:ss} %-5p request.id=%X{request.id} [%c{2.}] (%t) %s%e%n
```

Elementos del formato:

-   `%d{HH:mm:ss}`: hora.
-   `%-5p`: nivel del mensaje.
-   `%X{request.id}`: valor MDC asociado a `request.id`.
-   `%c{2.}`: nombre abreviado de la categoría.
-   `%t`: hilo de ejecución.
-   `%s`: mensaje.
-   `%e`: información de excepción, si existe.
-   `%n`: salto de línea.

Si el formato contiene `request.id=` pero no se ha establecido el valor
MDC, el campo puede aparecer vacío. Esto es esperado para mensajes que
no tienen un identificador asociado.

## 6. Logging a archivos y rotación

El handler de archivo permite conservar registros fuera de la consola.
Por ejemplo:

``` properties
quarkus.log.file.enabled=true
quarkus.log.file.path=logs/eventos-service.log
quarkus.log.file.level=INFO
quarkus.log.file.rotation.max-file-size=10M
quarkus.log.file.rotation.max-backup-index=5
```

Con esta configuración se habilita un archivo de logs y se establece un
límite de tamaño y un máximo de archivos de respaldo. Revisa los nombres
exactos de las propiedades de rotación frente a la versión de Quarkus
del proyecto si modificas esta configuración.

### Consideraciones importantes

-   El handler de archivo está deshabilitado de forma predeterminada.
-   La carpeta de destino debe poder crearse o ser accesible para el
    proceso.
-   No ejecutes simultáneamente varios procesos que intenten rotar el
    mismo archivo.
-   En Windows, un archivo abierto por otro proceso puede impedir que se
    renombre durante la rotación.
-   Si se configura un handler global y otro con nombre apuntando al
    mismo archivo, se puede producir escritura duplicada.

## 7. Configuración por perfiles

Quarkus permite aplicar valores diferentes según el perfil. Por ejemplo:

``` properties
%dev.quarkus.log.level=DEBUG
%prod.quarkus.log.level=INFO
```

La idea es disponer de más detalle durante el desarrollo y mantener una
salida más controlada en producción. Antes de añadir propiedades por
perfil, comprueba si ya existen para evitar confusiones.

## 8. Correlación de solicitudes con MDC

MDC (Mapped Diagnostic Context) permite asociar información contextual a
los mensajes de log, como un identificador de solicitud.

Ejemplo ilustrativo:

``` java
import java.util.UUID;
import org.jboss.logmanager.MDC;

public void procesarConCorrelacion() {
    String requestId = UUID.randomUUID().toString();
    MDC.put("request.id", requestId);

    try {
        // Ejecutar la lógica de negocio y registrar eventos.
    } finally {
        MDC.remove("request.id");
    }
}
```

El `finally` ayuda a retirar el valor al terminar la operación y evita
que el contexto quede asociado accidentalmente a una ejecución posterior
del mismo hilo.

En la configuración de consola se puede mostrar con:

``` properties
quarkus.log.console.format=%d{HH:mm:ss} %-5p request.id=%X{request.id} [%c{2.}] (%t) %s%e%n
```

En el servicio se observó que las solicitudes podían mostrar UUID
distintos, facilitando la correlación de sus mensajes.

## 9. Filtros de logging

Los filtros permiten decidir si un registro debe publicarse en un
handler determinado. En Quarkus se puede registrar un filtro con
`@LoggingFilter` e implementar `java.util.logging.Filter`.

Ejemplo conceptual:

``` java
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import io.quarkus.logging.LoggingFilter;

@LoggingFilter(name = "filtro-consola")
public class FiltroConsola implements Filter {
    @Override
    public boolean isLoggable(LogRecord record) {
        return !record.getMessage().contains("DIAGNOSTICO_INTERNO");
    }
}
```

Para asociarlo a la consola:

``` properties
quarkus.log.console.filter=filtro-consola
```

Este ejemplo ilustra un filtro por contenido. En aplicaciones reales,
los filtros deben diseñarse cuidadosamente para no ocultar errores
importantes ni depender de textos que puedan cambiar.

## 10. Handlers con nombre

Un handler con nombre permite enviar registros de una categoría a un
destino concreto. Ejemplo de configuración:

``` properties
quarkus.log.handler.file.EVENTOS.enabled=true
quarkus.log.handler.file.EVENTOS.path=logs/eventos.log
quarkus.log.handler.file.EVENTOS.level=INFO
quarkus.log.category."com.inklusport".handlers=EVENTOS
quarkus.log.category."com.inklusport".use-parent-handlers=false
```

-   `EVENTOS` es el nombre asignado al handler.
-   `handlers=EVENTOS` vincula ese handler a la categoría indicada.
-   `use-parent-handlers=false` evita que la categoría delegue también
    en los handlers heredados.

Si quieres conservar la salida de consola y escribir además en el
archivo, revisa la jerarquía de handlers y los destinos para evitar
duplicados.

## 11. Logging JSON

El logging JSON estructura cada registro como un objeto con campos, lo
que facilita que sistemas de búsqueda y observabilidad procesen los
logs.

La extensión se puede añadir mediante Maven:

``` powershell
.\mvnw.cmd quarkus:add-extension "-Dextensions=logging-json"
```

Configuración ilustrativa:

``` properties
quarkus.log.console.json.enabled=true
quarkus.log.console.json.pretty-print=false
```

-   `enabled=true` activa el formato JSON para la consola.
-   `pretty-print=false` mantiene la salida compacta, habitual en
    entornos automatizados.

Al activar JSON, la salida puede dejar de utilizar el formato de consola
tradicional. Decide qué formato necesitas y evita configurar ambos
esperando que se combinen automáticamente.

## 12. Pruebas automatizadas

El proyecto contiene pruebas para los recursos REST:

-   `EventoResourceTest`: 2 pruebas.
-   `GreetingResourceTest`: 1 prueba.

Se ejecutan con `.\mvnw.cmd test`. Las pruebas de endpoints pueden
verificar, por ejemplo, una creación válida y el rechazo de datos
incompletos.

En las últimas salidas se observó un comportamiento coherente con la
validación del recurso de eventos:

-   La solicitud incompleta produjo una advertencia que identifica los
    campos faltantes (`nombre` y `usuarioId`).
-   La solicitud aceptada produjo un mensaje informativo.
-   Las tres pruebas terminaron correctamente.

Una prueba que pasa confirma el comportamiento cubierto por esa prueba;
no garantiza por sí sola que toda la configuración de logging, rotación
o exportación externa esté libre de problemas.

## 13. OpenTelemetry y centralización

OpenTelemetry puede ayudar a exportar datos de observabilidad, incluidos
logs cuando esa capacidad está configurada. También es posible
centralizar registros en plataformas que los recopilan y permiten
consultarlos.

En el proyecto se instaló la extensión de OpenTelemetry y se observó que
el servicio intentaba enviar datos a:

``` text
localhost:4317
```

La última ejecución mostró:

``` text
Connection refused: localhost/127.0.0.1:4317
```

Esto indica que no había un receptor disponible en ese endpoint en ese
momento. Tener instalada la extensión no implica que exista un colector
ejecutándose.

### Opciones

1.  **Si no se necesita exportar logs por ahora**, desactivar la
    exportación de logs de OpenTelemetry en la configuración
    correspondiente:

    ``` properties
    quarkus.otel.logs.enabled=false
    ```

2.  **Si se quiere practicar observabilidad**, ejecutar o configurar un
    receptor compatible con OTLP y verificar su endpoint. La guía de
    OpenTelemetry Logging de la misma versión de Quarkus explica
    alternativas para recibir y visualizar los datos.

Después de cambiar la configuración, vuelve a ejecutar las pruebas. No
se debe considerar solucionado el problema hasta comprobar que los
mensajes de conexión rechazada desaparecieron.

## 14. Diagnóstico de problemas

### `Connection refused` en `localhost:4317`

-   Comprueba si hay un colector OTLP escuchando en ese puerto.
-   Si no vas a exportar logs, desactiva `quarkus.otel.logs.enabled`.
-   Reinicia las pruebas y verifica la salida.

### Error al mover `eventos.log` a `eventos.log.1`

-   Detén el modo desarrollo con `Ctrl+C`.
-   Cierra otros procesos que puedan estar utilizando el mismo archivo.
-   Comprueba si dos handlers escriben en el mismo destino.
-   Ejecuta nuevamente las pruebas y verifica que la rotación funcione.

### Advertencias de propiedades duplicadas

-   Busca cada propiedad en `application.properties`.
-   Revisa también las propiedades definidas en perfiles.
-   Mantén una sola declaración por propiedad y contexto, salvo que la
    diferencia entre perfiles sea intencional.

### `request.id` aparece vacío

-   Comprueba que se establece el valor MDC antes de registrar el
    mensaje.
-   Verifica que el formato incluye `%X{request.id}`.
-   Asegúrate de retirar el valor al finalizar el procesamiento.

### No aparecen mensajes `DEBUG`

-   Revisa el nivel global.
-   Comprueba el nivel de la categoría correspondiente.
-   Verifica el nivel mínimo y la configuración del handler.
-   Ten en cuenta que algunos valores de configuración, como el nivel
    mínimo, se aplican durante la compilación.

## 15. Buenas prácticas

1.  Usa `INFO` para eventos relevantes del flujo normal y `WARN` para
    situaciones inesperadas recuperables.
2.  Usa `ERROR` cuando una operación falla y aporta contexto suficiente
    para investigar.
3.  Reserva `DEBUG` y `TRACE` para diagnóstico detallado.
4.  No registres contraseñas, tokens, secretos ni información personal
    sensible.
5.  Incluye identificadores de correlación cuando ayuden a seguir una
    solicitud entre componentes.
6.  Limpia el MDC al finalizar el procesamiento.
7.  Configura rotación y retención de archivos según el entorno.
8.  Evita que varios handlers escriban accidentalmente dos veces al
    mismo archivo.
9.  Usa JSON cuando los logs vayan a ser procesados automáticamente.
10. Mantén los perfiles de desarrollo y producción claramente separados.
11. Comprueba las pruebas automatizadas después de modificar la
    configuración.
12. Trata las advertencias de exportación como problemas independientes
    de los resultados de las pruebas funcionales.

## 16. Lista de verificación

-   [x] Se estudiaron los niveles y la API de logging.
-   [x] Se configuraron categorías y formato de consola.
-   [x] Se trabajó con logging a archivos y rotación.
-   [x] Se revisaron perfiles de configuración.
-   [x] Se trabajó con MDC y `request.id`.
-   [x] Se implementó un ejemplo de filtro de logging.
-   [x] Se revisaron handlers con nombre.
-   [x] Se trabajó con logging JSON.
-   [x] Se añadieron y ejecutaron pruebas automatizadas.
-   [x] Se instaló la extensión de OpenTelemetry.
-   [ ] Verificar que no existan propiedades duplicadas en la
    configuración actual.
-   [ ] Confirmar que la rotación de archivos funcione sin bloqueos.
-   [ ] Resolver la conexión de OpenTelemetry o mantener desactivada su
    exportación de logs si no se necesita.
-   [ ] Ejecutar nuevamente `.\mvnw.cmd test` y comprobar tanto las
    pruebas como los mensajes de logging.

## Referencias oficiales

-   [Guía de Logging de Quarkus
    3.40](https://quarkus.io/version/3.40/guides/logging/)
-   [Guía de OpenTelemetry Logging de Quarkus
    3.40](https://es.quarkus.io/version/3.40/guides/opentelemetry-logging/)
-   [Guía de OpenTelemetry de Quarkus
    3.40](https://quarkus.io/version/3.40/guides/opentelemetry/)
-   [Guía de centralización de logs de Quarkus
    3.40](https://quarkus.io/version/3.40/guides/centralized-log-management/)

------------------------------------------------------------------------

**Proyecto:** `eventos-service`\
**Framework:** Quarkus 3.40.1\
**Build:** Maven Wrapper (`mvnw.cmd`)\
**Propósito de este documento:** resumen de estudio y referencia de las
prácticas de logging implementadas y revisadas durante el aprendizaje.
