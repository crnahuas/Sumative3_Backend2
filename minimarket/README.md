# MINIMARKET PLUS - OpenAPI y HATEOAS

Backend de la actividad sumativa de Semana 8 de Desarrollo Backend II. El proyecto documenta la API con OpenAPI 3 y Swagger UI, e incorpora respuestas HATEOAS en Producto, Carrito, Inventario y Usuario sin retirar la autenticación de Spring Security.

## Tecnologías

- Java 17
- Spring Boot 3.4.1
- Spring Web, Spring Data JPA y Spring Security
- Spring HATEOAS
- springdoc-openapi 2.7.0
- H2
- Maven Wrapper

## Ejecución local

```bash
./mvnw spring-boot:run
```

La aplicación utiliza `http://localhost:8081`.

Rutas de documentación:

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- Contrato OpenAPI JSON: `http://localhost:8081/v3/api-docs`

## Acceso de demostración

Al iniciar una base vacía se crea un usuario local para poder probar los recursos protegidos:

- Usuario: `admin`
- Contraseña: `admin123`
- Rol: `ROLE_ADMIN`

Las credenciales pueden reemplazarse sin modificar el código:

```bash
export MINIMARKET_ADMIN_USERNAME=otro_usuario
export MINIMARKET_ADMIN_PASSWORD=otra_clave
./mvnw spring-boot:run
```

Estas credenciales predeterminadas son solo para la demostración académica local.

## Recursos documentados

| Recurso | Ruta base | Operaciones | Enlaces HATEOAS |
|---|---|---|---|
| Producto | `/api/productos` | GET, POST, GET por id, PUT y DELETE | `self`, `productos` |
| Carrito | `/api/carrito` | GET, POST, GET por id, PUT y DELETE | `self`, `carrito`, `usuario`, `producto` |
| Inventario | `/api/inventario` | GET, POST, GET por id, PUT y DELETE | `self`, `inventario`, `producto` |
| Usuario | `/api/usuarios` | GET, POST, GET por id, PUT y DELETE | `self`, `usuarios` |

Los controladores usan `@Operation`, `@ApiResponse` y `@ApiResponses`. Las representaciones se generan mediante `EntityModel`, `CollectionModel` y ensambladores separados para no mezclar la construcción de enlaces con la lógica de persistencia.

## Pruebas

```bash
./mvnw test
```

Las pruebas comprueban el arranque del contexto, la publicación de Swagger UI y `/v3/api-docs`, la presencia de los cuatro contratos requeridos, el esquema de sesión y los enlaces HATEOAS entre recursos.

## Evidencias y Postman

- [Informe técnico](docs/INFORME_SEMANA_8.md)
- [Guía de capturas](docs/GUIA_CAPTURAS.md)
- [OpenAPI exportado](docs/evidencias/openapi.json)
- [Captura de Swagger UI](docs/evidencias/swagger-ui.png)
- [Captura completa de Swagger UI](docs/evidencias/swagger-ui-completa.png)
- [Swagger Carrito con ejemplo](docs/evidencias/swagger-carrito-detalle.png)
- [Estructura y dependencias](docs/evidencias/estructura-y-pom.png)
- [Ensamblador HATEOAS](docs/evidencias/carrito-model-assembler.png)
- [Respuesta HATEOAS](docs/evidencias/respuesta-hateoas-carrito.png)
- [Pruebas Maven](docs/evidencias/pruebas-maven.png)
- [Colección Postman](docs/postman/Minimarket_Plus_Semana_8.postman_collection.json)
- [Entorno Postman](docs/postman/Minimarket_Local.postman_environment.json)

Importa la colección y el entorno, ejecuta las carpetas en orden y deja activado el manejo de cookies de Postman. El login crea `JSESSIONID` y las solicitudes posteriores reutilizan esa sesión.
