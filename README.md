# Challenge Comercia - Sistema de Alquiler de Coches

Proyecto Java multi-modulo con Spring Boot para gestionar alquileres de coches, calculo de precios y puntos de
fidelidad.

## Versiones y requisitos tecnicos

### Java

- **Version usada por el proyecto:** `21`
- **Fuente:** propiedad `java.version` en `pom.xml`

### Spring Boot

- **Version usada por el proyecto:** `3.4.5`
- **Fuente:** parent `spring-boot-starter-parent` en `pom.xml`

### Maven

- **Version en el repositorio:** no esta fijada explicitamente (no hay Maven Wrapper ni regla `requireMavenVersion` en
  los `pom.xml`).
- **Uso esperado:** Maven 3 (recomendado `3.9.x` para entorno local moderno).
- **Fuente operativa:** scripts en `0-docs/scripts` que esperan los path de `JAVA_HOME` y `MAVEN_HOME` configurado
  internamente para que no haga falta tocar las variables de entorno.

## Arquitectura del proyecto

El repositorio esta organizado como **multi-modulo Maven** con separacion por capas:

- `api/`: capa de entrada HTTP (Spring Boot).
    - Contiene `controllers`, `exceptions`, `config` y la clase `ComerciaApplication`.
    - Genera el ejecutable `comercia-challenge.jar`.
- `domain/`: capa de logica de negocio.
    - Contiene `service` y `dto`.
    - Depende de `model`.
- `model/`: capa de persistencia y modelo de datos.
    - Contiene `entity` y `repository`.
- `to_deploy/`: carpeta de salida para despliegue local.
    - El build copia aqui el JAR final.
- `0-docs/`: documentacion y scripts auxiliares.

### Flujo de dependencias entre modulos

`api` -> `domain` -> `model`

El `pom.xml` raiz agrega estos modulos y centraliza propiedades comunes (como `java.version`).

## Como funcionan los scripts de `0-docs/scripts`

> Nota: en tu mensaje mencionas "documents"; en este repo la carpeta equivalente es `0-docs/scripts`.

### 1) `build-and-run-local.bat`

Script de compilacion + ejecucion. Hace lo siguiente:

1. Define variables de entorno locales para la ejecucion:
    - `JAVA_HOME={your_java_21_path_here}`
    - `MAVEN_HOME={your_maven_path_here}`
    - actualiza `PATH`
2. Calcula la raiz del proyecto desde la ubicacion del script.
3. Ejecuta build con Maven:
    - `mvn clean install -DskipTests`
4. Entra en `to_deploy` y verifica que exista `comercia-challenge.jar`.
5. Ejecuta el JAR con Java 21 y debug remoto habilitado en puerto `5005`:
    - `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005`
6. Si se pasa `--skip-run`, compila pero no arranca la app.

### 2) `run-local.bat`

Script de ejecucion (sin build). Hace lo siguiente:

1. Define `JAVA_HOME` y actualiza `PATH`.
2. Va a `to_deploy`.
3. Verifica que exista `comercia-challenge.jar`.
4. Arranca el JAR con debug remoto en puerto `5005`.
5. Si se pasa `--skip-run`, termina sin ejecutar.

## Preparacion rapida

1. Edita ambos scripts y reemplaza:
    - `{your_java_21_path_here}` por tu ruta de Java 21
    - `{your_maven_path_here}` por tu ruta de Maven (solo en `build-and-run-local.bat`)
2. Ejecuta desde Windows:

```bat
0-docs\scripts\build-and-run-local.bat
```

Si solo quieres levantar un JAR ya generado:

```bat
0-docs\scripts\run-local.bat
```

## Artefacto generado

El artefacto final esperado para despliegue local es:

- `to_deploy/comercia-challenge.jar`

Se ha subido la ultima version del .jar para facilitar pruebas, pero el proceso de build local es recomendado para
asegurar que todo esta correcto en tu entorno.

---

## Casos de uso / Ejemplos de API

**Base URL:** `http://localhost:8080/comercia-api`

> Los ejemplos de abajo estan preparados para **Windows PowerShell**. Por eso usan `curl.exe` en lugar de `curl`,
> ya que en PowerShell `curl` puede resolverse como alias de `Invoke-WebRequest`.
>
> Importante: en los `POST` de `curl.exe`, el JSON debe ir con las comillas dobles **escapadas** como `\"...\"`.
> Si no se escapan, PowerShell altera el payload y la API responde `400 Bad Request`.

---

### Crear alquiler

#### `POST /alquiler` — Alquiler de 1 coche

```powershell
curl.exe -X POST "http://localhost:8080/comercia-api/alquiler" -H "Content-Type: application/json" --data-raw '{\"clienteId\":2,\"cocheIds\":[4],\"fechaInicio\":\"2027-02-01\",\"fechaFin\":\"2027-02-05\"}'
```

#### `POST /alquiler` — Alquiler de 3 coches

```powershell
curl.exe -X POST "http://localhost:8080/comercia-api/alquiler" -H "Content-Type: application/json" --data-raw '{\"clienteId\":1,\"cocheIds\":[1,2,3],\"fechaInicio\":\"2027-02-10\",\"fechaFin\":\"2027-02-15\"}'
```

#### `POST /alquiler` — Alquiler de 3 coches con tramos de precio (>7 dias y >30 dias)

```powershell
curl.exe -X POST "http://localhost:8080/comercia-api/alquiler" -H "Content-Type: application/json" --data-raw '{\"clienteId\":3,\"cocheIds\":[5,6,7],\"fechaInicio\":\"2027-03-01\",\"fechaFin\":\"2027-04-10\"}'
```

> Este caso cubre mas de 30 dias de alquiler, activando los tres tramos de precio de SUV y Small.

---

### Devolucion de coches

#### `POST /alquiler/devolucion` — Devolucion antes de la fecha fin (sin recargo)

```powershell
curl.exe -X POST "http://localhost:8080/comercia-api/alquiler/devolucion" -H "Content-Type: application/json" --data-raw '{\"alquilerId\":1,\"fechaDevolucion\":\"2026-01-03\"}'
```

#### `POST /alquiler/devolucion` — Devolucion en el ultimo dia pactado (sin recargo)

```powershell
curl.exe -X POST "http://localhost:8080/comercia-api/alquiler/devolucion" -H "Content-Type: application/json" --data-raw '{\"alquilerId\":7,\"fechaDevolucion\":\"2026-02-04\"}'
```

#### `POST /alquiler/devolucion` — Devolucion tardia (con recargo por dias extra)

```powershell
curl.exe -X POST "http://localhost:8080/comercia-api/alquiler/devolucion" -H "Content-Type: application/json" --data-raw '{\"alquilerId\":71,\"fechaDevolucion\":\"2027-01-20\"}'
```

> Este caso activa el calculo de recargo por dias de retraso segun el tipo de cada coche del alquiler.

---

### Consultar coches disponibles

#### `GET /alquiler/disponibles` — Con filtro de tipo

```powershell
curl.exe "http://localhost:8080/comercia-api/alquiler/disponibles?fechaInicio=2027-01-10&fechaFin=2027-01-20&cocheTipoId=SUV"
```

#### `GET /alquiler/disponibles` — Sin filtro de tipo (todos los tipos)

```powershell
curl.exe "http://localhost:8080/comercia-api/alquiler/disponibles?fechaInicio=2027-01-10&fechaFin=2027-01-20"
```

> El endpoint devuelve un Page paginado. Puedes añadir `&page=0&size=10&sort=matricula,asc` para controlar la
> paginacion.

