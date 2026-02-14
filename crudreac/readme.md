http://localhost:8080/swagger-ui.html

crud reactivo..


# 📘 Conceptos Fundamentales de Project Reactor (Mono y Flux)

Project Reactor es la librería de programación reactiva utilizada por Spring WebFlux. Su poder reside en la capacidad de componer operaciones asíncronas y no bloqueantes utilizando una amplia colección de operadores.

A continuación, se presenta el concepto general de los operadores más utilizados, clasificados por su función.

---

## 1. Operadores de Creación y Emisión

Estos operadores definen cómo se inicia un flujo de datos (`Mono` o `Flux`).

| Operador | Concepto General | Propósito |
| :--- | :--- | :--- |
| **`just()`** | Crea un `Mono` o `Flux` a partir de un **valor conocido** o un conjunto fijo de valores. | Fuentes de datos predefinidas. |
| **`fromIterable()`** | Crea un `Flux` a partir de una colección de elementos (`List`, `Set`, etc.). | Adaptar colecciones síncronas a flujos reactivos. |
| **`empty()`** | Crea un `Mono` o `Flux` que inmediatamente emite la señal de **terminación (`onComplete`)** sin emitir ningún elemento. | Representar una respuesta vacía o un resultado de búsqueda nulo. |
| **`error(Throwable)`** | Crea un `Mono` o `Flux` que inmediatamente emite una señal de **error**. | Simular o forzar una condición de error. |
| **`create()`** | Proporciona un API para que un desarrollador pueda crear un flujo (`FluxSink` o `MonoSink`) que emita datos basados en un código asíncrono o API de terceros. | Integrar librerías no reactivas. |

## 2. Operadores de Transformación y Mapeo

Permiten modificar el valor o tipo de los elementos emitidos.

| Operador | Concepto General | Propósito |
| :--- | :--- | :--- |
| **`map()`** | **Transformación Síncrona.** Aplica una función a cada elemento y emite el resultado. Es un mapeo uno a uno. | Convertir un objeto de un tipo a otro (Ej: Entidad a DTO). |
| **`flatMap()`** | **Transformación Asíncrona.** Aplica una función a cada elemento, pero la función debe devolver un nuevo `Mono` o `Flux`. Se usa para encadenar operaciones que son en sí mismas asíncronas. | Realizar una llamada a la base de datos o a otro servicio por cada elemento del flujo principal. |
| **`handle()`** | Es un operador flexible que permite la **transformación, filtrado y eliminación de elementos** en un solo paso, utilizando una API de *callback* que permite emitir 0 o 1 elemento por cada elemento recibido. | Limpiar datos o aplicar lógica compleja de mapeo/filtrado. |

## 3. Operadores de Filtrado

Restringen qué elementos son permitidos para continuar el flujo.

| Operador | Concepto General | Propósito |
| :--- | :--- | :--- |
| **`filter()`** | Emite solo aquellos elementos que cumplen con una condición definida (un predicado). | Descartar elementos no deseados, como productos agotados. |
| **`take()`** | Emite un número máximo de elementos y luego cancela la suscripción. | Limitar los resultados a los primeros N elementos. |
| **`skip()`** | Descarta el primer número N de elementos y comienza a emitir a partir de ese punto. | Paginación simple. |

## 4. Operadores de Combinación

Permiten trabajar con múltiples flujos de datos.

| Operador | Concepto General | Propósito |
| :--- | :--- | :--- |
| **`zip()`** | Combina los elementos de múltiples flujos en tuplas **uno a uno**. Emite cuando todas las fuentes tienen un nuevo elemento disponible. | Sincronizar dos resultados (Ej: `Mono<Usuario>` con `Mono<DetallesPerfil>`). |
| **`merge()`** | Combina elementos de múltiples `Flux`s en un solo `Flux`, emitiendo los elementos tan pronto como llegan. **No garantiza el orden** de los flujos fuente. | Consolidar resultados rápidos de varias fuentes al mismo tiempo. |
| **`concat()`** | Combina flujos **secuencialmente**. El segundo flujo solo comienza a emitir sus elementos una vez que el primer flujo ha completado su emisión. | Garantizar el orden de procesamiento de tareas. |

## 5. Operadores de Tiempo y Control

Permiten controlar la velocidad, concurrencia y manejar latencia.

| Operador | Concepto General | Propósito |
| :--- | :--- | :--- |
| **`delayElements()`** | Introduce un retardo entre la emisión de **cada elemento** del flujo. | Simular latencia o controlar la velocidad de emisión (*rate limiting*). |
| **`timeout()`** | Si el flujo fuente no emite el siguiente elemento dentro de un tiempo límite, el flujo termina con un error de `TimeoutException`. | Prevenir que el sistema se quede "colgado" esperando una respuesta. |
| **`buffer()`** | Agrupa elementos del `Flux` en colecciones (`List`) según un tamaño máximo o un intervalo de tiempo. | Optimizar operaciones por lotes (*batching*) en la base de datos o servicios. |

## 6. Operadores de Error y Terminación

Definen cómo se comporta el flujo cuando hay un error o cuando termina vacío.

| Operador | Concepto General | Propósito |
| :--- | :--- | :--- |
| **`switchIfEmpty()`** | Si el `Mono` o `Flux` de origen termina con la señal `onComplete()` sin emitir ningún elemento, cambia a un **flujo alternativo** (`Publisher` de *fallback*). | Buscar datos en caché si la fuente principal está vacía, o lanzar un error si el elemento es requerido. |
| **`defaultIfEmpty()`** | Si el `Mono` o `Flux` de origen termina vacío, emite un **valor simple por defecto** en lugar de cambiar a un flujo completo. | Proporcionar un valor predeterminado cuando una consulta no devuelve resultados. |
| **`onErrorResume()`** | Si ocurre un error, en lugar de terminar el flujo con el error, el flujo **continúa con un `Publisher` de recuperación** que se ejecuta en su lugar. | Implementar resiliencia y planes de contingencia ante fallos. |

---

## 🔗 Documentación Oficial

Para consultar todos los operadores, su sintaxis y ejemplos detallados, el mejor recurso es la guía de referencia oficial:

**[Guía de Referencia de Project Reactor (Project Reactor Reference Guide)](https://projectreactor.io/docs/core/release/reference/)**