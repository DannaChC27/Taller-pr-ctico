# Sistema de Gestión de Biblioteca

Taller práctico de persistencia de datos en Java. Hice una aplicación de consola para manejar los libros de una biblioteca pequeña. Los datos se guardan en una base de datos SQLite, así que no se pierden cuando cierro el programa.

El taller pedía un "Sistema de Gestión de Productos". Yo lo adapté a una biblioteca, entonces en vez de productos manejo libros.


## Qué hace

- Registrar un libro (código, título, autor, precio y cantidad disponible)
- Mostrar todos los libros
- Buscar por código o por parte del título
- Actualizar un libro (si dejo un campo vacío se queda como estaba)
- Eliminar un libro, pidiendo confirmación antes
- Guardar todo en la base de datos y volver a leerlo al abrir el programa otra vez

## Tecnologías

- Java 17
- Maven
- JDBC con SQLite (driver sqlite-jdbc)
- Consola, sin interfaz gráfica

## Estructura del proyecto

```
src/main/java/biblioteca/
    app/Main.java          menú y entrada de datos por consola
    dao/LibroDAO.java      consultas SQL (CRUD)
    db/ConexionDB.java     conexión, creación de la tabla y datos iniciales
    model/Libro.java       clase Libro con sus validaciones
    util/LogConfig.java    configuración del log
pom.xml
run.sh
```

Separé el código en capas para no mezclar el SQL con el menú. Todo el SQL está en `LibroDAO`, el menú está en `Main` y `Libro` solo guarda los datos y los valida.

## Base de datos

Uso SQLite porque es un solo archivo y no hay que instalar nada. Al ejecutar por primera vez se crea `biblioteca.db` y se llena con 8 libros de ejemplo, para poder probar sin tener que registrar todo a mano.

La tabla es esta:

```sql
CREATE TABLE IF NOT EXISTS libros (
    codigo   TEXT PRIMARY KEY,
    titulo   TEXT NOT NULL,
    autor    TEXT NOT NULL,
    precio   REAL NOT NULL CHECK (precio >= 0),
    cantidad INTEGER NOT NULL CHECK (cantidad >= 0)
);
```

Todas las consultas usan `PreparedStatement` para evitar la inyección SQL, y las conexiones se cierran con try-with-resources.

## Validaciones

- El precio no puede ser negativo ni texto
- La cantidad tiene que ser un número entero que no sea negativo
- Título, autor y código no pueden quedar vacíos
- No deja registrar dos libros con el mismo código
- Si se intenta actualizar o eliminar un código que no existe, avisa y no pasa nada

## Log

El programa guarda un registro en `biblioteca.log` con la fecha, el nivel y el mensaje de lo que va pasando (libros creados, actualizados, eliminados, errores y entradas inválidas). Ese archivo se genera solo al ejecutar y no lo subí al repositorio.

## Cómo ejecutarlo

### En GitHub Codespaces

1. En el repositorio, ir a Code, luego a la pestaña Codespaces y darle a "Create codespace on main".
2. Esperar a que termine de cargar. La primera vez demora un poco porque instala Java y Maven.
3. Abrir la terminal y correr:

```
./run.sh
```

Si sale "permission denied", se puede usar `bash run.sh`.

### En mi computador

Hace falta tener Java 17 o más y Maven instalados.

```
mvn package
java -jar target/gestion-biblioteca.jar
```

## Cómo probar la persistencia

1. Ejecutar el programa y registrar un libro nuevo.
2. Salir con la opción 6.
3. Volver a ejecutarlo y mostrar los libros con la opción 2. El libro nuevo sigue ahí.

## Video

Enlace al video: (pendiente)
