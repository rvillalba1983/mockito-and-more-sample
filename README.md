# mockito-and-more-sample

[![CI](https://github.com/rvillalba1983/mockito-and-more-sample/actions/workflows/ci.yml/badge.svg)](https://github.com/rvillalba1983/mockito-and-more-sample/actions/workflows/ci.yml)

Ejemplos prácticos de testing en Java con **Mockito**, **Podam**, **Datafaker** y **MockMvcTester** sobre una pequeña API de Spring Boot.

## Stack

| | Versión |
|---|---|
| Java | 25 |
| Spring Boot | 4.1 |
| Mockito / JUnit 5 / AssertJ | gestionadas por Spring Boot |
| Podam | 8.0.2 |
| Datafaker | 2.7.0 |

## La aplicación

Una API mínima con un único endpoint:

```
GET /sample/{id}
```

```json
{
  "message": "The id is IDABC123",
  "address": "Street Dummy IdAbc123"
}
```

- `ToolboxController` expone el endpoint.
- `ToolboxService` construye la respuesta: si el id empieza por `Id` lo pasa a mayúsculas; si no, a minúsculas.
- `AddressExternalService` simula una dependencia externa (una API de direcciones). Es la pieza que se sustituye por un mock en los tests.

## Qué enseña cada test

| Test | Tipo | Qué muestra |
|---|---|---|
| `ToolboxServiceTest` | Unitario (sin Spring) | `@Mock` + `@InjectMocks`, estilo BDD (`given` / `then`), `ArgumentCaptor`, verificación de interacciones, simular excepciones, y datos de prueba con Podam y con Datafaker |
| `ToolboxControllerTest` | Unitario (sin Spring) | Probar un controlador llamando al método directamente |
| `ToolboxControllerWebMvcTest` | Slice de Spring MVC | `@WebMvcTest` + `@MockitoBean` para probar rutas, códigos HTTP y JSON con `MockMvcTester` (aserciones AssertJ) |
| `ToolboxApplicationTest` | Integración | Que el contexto completo de Spring arranca |

### Mockito

Sustituye las dependencias de la clase bajo test por dobles controlados. Los ejemplos usan la API BDD:

```java
given(addressExternalService.findAddress(id)).willReturn(address);

var result = toolboxService.process(id);

then(addressExternalService).should().findAddress(id);
then(addressExternalService).shouldHaveNoMoreInteractions();
```

Desde JDK 21 la JVM avisa si un agente se carga dinámicamente, y en futuras versiones dejará de permitirlo. Por eso el `pom.xml` registra Mockito como `-javaagent` en Surefire, como recomienda la propia documentación de Mockito.

### Podam

Genera POJOs rellenos con datos aleatorios. Viene bien cuando un objeto tiene muchos campos y al test solo le importan unos pocos:

```java
var expected = podamFactory.manufacturePojoWithFullData(SampleResponse.class);
```

La configuración compartida (tamaño de colecciones, memoización) está en `ToolboxPodamFactory`.

### Datafaker

Genera datos *realistas* (direcciones, documentos de identidad, nombres...). Los tests se leen mejor y se parecen más a casos reales:

```java
var id = faker.idNumber().valid();
var address = faker.address().fullAddress();
```

### MockMvcTester + @MockitoBean

`@MockitoBean` es el sustituto de `@MockBean`, que está deprecado desde Spring Boot 3.4. `MockMvcTester` permite escribir las aserciones HTTP con AssertJ:

```java
assertThat(mvc.get().uri("/sample/{id}", id))
        .hasStatus(HttpStatus.OK)
        .bodyJson().extractingPath("$.message").isEqualTo(expectedMessage);
```

## Ejecutar

```bash
mvn verify                 # compila y lanza todos los tests
mvn spring-boot:run        # arranca la API en http://localhost:8080
curl localhost:8080/sample/Id123
```
