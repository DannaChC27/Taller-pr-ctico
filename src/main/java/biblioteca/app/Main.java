package biblioteca.app;

import biblioteca.dao.LibroDAO;
import biblioteca.db.ConexionDB;
import biblioteca.model.Libro;
import biblioteca.util.LogConfig;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Capa de presentación (consola) del Sistema de Gestión de Biblioteca.
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final Scanner scanner = new Scanner(System.in);
    private static final LibroDAO dao = new LibroDAO();

    // ========================================================
    // PROGRAMA PRINCIPAL
    // ========================================================

    public static void main(String[] args) {

        LogConfig.configurar();

        try {
            ConexionDB.inicializar();
        } catch (SQLException e) {
            logger.severe("No se pudo inicializar la base de datos: " + e.getMessage());
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
            return;
        }

        logger.info("Sistema iniciado.");

        while (true) {

            mostrarMenu();

            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    registrarLibro();
                    break;

                case "2":
                    mostrarLibros();
                    break;

                case "3":
                    buscarLibros();
                    break;

                case "4":
                    actualizarLibro();
                    break;

                case "5":
                    eliminarLibro();
                    break;

                case "6":
                    logger.info("Sistema cerrado por el usuario.");
                    System.out.println("\nPrograma finalizado.");
                    return;

                default:
                    logger.warning("El usuario seleccionó una opción inválida: " + opcion);
                    System.out.println("\nOpción inválida. Intente nuevamente.\n");
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("================================");
        System.out.println("     SISTEMA DE BIBLIOTECA");
        System.out.println("================================");
        System.out.println("1. Registrar libro");
        System.out.println("2. Mostrar libros");
        System.out.println("3. Buscar libro (código o título)");
        System.out.println("4. Actualizar libro");
        System.out.println("5. Eliminar libro");
        System.out.println("6. Salir");
        System.out.println("================================");
    }

    // --------------------------------------------------------
    // CREATE - Registrar libro (RF1)
    // --------------------------------------------------------

    private static void registrarLibro() {
        try {
            String codigo = leerTextoObligatorio("Ingrese el código del libro: ");

            if (dao.existe(codigo)) {
                logger.warning("Intento de registrar un código duplicado: " + codigo);
                System.out.println("\nYa existe un libro con ese código.\n");
                return;
            }

            String titulo = leerTextoObligatorio("Ingrese el título: ");
            String autor = leerTextoObligatorio("Ingrese el autor: ");
            double precio = leerPrecio("Ingrese el precio: ");
            int cantidad = leerCantidad("Ingrese la cantidad disponible: ");

            dao.insertar(new Libro(codigo, titulo, autor, precio, cantidad));
            System.out.println("\nLibro registrado correctamente.\n");

        } catch (SQLException e) {
            manejarErrorBD("registrar el libro", e);
        }
    }

    // --------------------------------------------------------
    // READ - Mostrar todos (RF2)
    // --------------------------------------------------------

    private static void mostrarLibros() {
        try {
            imprimirLista(dao.listar(), "No hay libros registrados.");
        } catch (SQLException e) {
            manejarErrorBD("consultar los libros", e);
        }
    }

    // --------------------------------------------------------
    // READ - Buscar por código o título (RF2)
    // --------------------------------------------------------

    private static void buscarLibros() {
        try {
            String termino = leerTextoObligatorio("Ingrese el código o parte del título: ");
            imprimirLista(dao.buscar(termino), "No se encontraron libros con ese criterio.");
        } catch (SQLException e) {
            manejarErrorBD("buscar libros", e);
        }
    }

    // --------------------------------------------------------
    // UPDATE - Actualizar libro (RF3)
    // --------------------------------------------------------

    private static void actualizarLibro() {
        try {
            String codigo = leerTextoObligatorio("Ingrese el código del libro: ");
            Libro libro = dao.buscarPorCodigo(codigo);

            if (libro == null) {
                logger.warning("Intento de actualizar libro inexistente - Código: " + codigo);
                System.out.println("\nNo se encontró un libro con ese código.\n");
                return;
            }

            System.out.println("\nLibro encontrado:");
            libro.mostrar();
            System.out.println("(Presione Enter para dejar un campo sin cambios)\n");

            String titulo = leerTextoOpcional("Nuevo título [" + libro.getTitulo() + "]: ");
            if (!titulo.isEmpty()) {
                libro.setTitulo(titulo);
            }

            String autor = leerTextoOpcional("Nuevo autor [" + libro.getAutor() + "]: ");
            if (!autor.isEmpty()) {
                libro.setAutor(autor);
            }

            Double precio = leerPrecioOpcional("Nuevo precio [" + libro.getPrecio() + "]: ");
            if (precio != null) {
                libro.setPrecio(precio);
            }

            Integer cantidad = leerCantidadOpcional("Nueva cantidad [" + libro.getCantidad() + "]: ");
            if (cantidad != null) {
                libro.setCantidad(cantidad);
            }

            dao.actualizar(libro);
            System.out.println("\nLibro actualizado correctamente.\n");

        } catch (SQLException e) {
            manejarErrorBD("actualizar el libro", e);
        }
    }

    // --------------------------------------------------------
    // DELETE - Eliminar libro con confirmación (RF4)
    // --------------------------------------------------------

    private static void eliminarLibro() {
        try {
            String codigo = leerTextoObligatorio("Ingrese el código del libro: ");
            Libro libro = dao.buscarPorCodigo(codigo);

            if (libro == null) {
                logger.warning("Intento de eliminar libro inexistente - Código: " + codigo);
                System.out.println("\nNo se encontró un libro con ese código.\n");
                return;
            }

            System.out.println();
            libro.mostrar();
            System.out.print("\n¿Está seguro de eliminar este libro? (s/n): ");
            String respuesta = scanner.nextLine().trim().toLowerCase();

            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                dao.eliminar(codigo);
                System.out.println("\nLibro eliminado correctamente.\n");
            } else {
                logger.info("Eliminación cancelada por el usuario - Código: " + codigo);
                System.out.println("\nEliminación cancelada.\n");
            }

        } catch (SQLException e) {
            manejarErrorBD("eliminar el libro", e);
        }
    }

    // ========================================================
    // UTILIDADES DE ENTRADA Y SALIDA
    // ========================================================

    private static void imprimirLista(List<Libro> libros, String mensajeVacio) {
        if (libros.isEmpty()) {
            System.out.println("\n" + mensajeVacio + "\n");
            return;
        }

        System.out.println("\n=========== LIBROS ===========");
        for (Libro libro : libros) {
            libro.mostrar();
        }
        System.out.println("==============================\n");
    }

    private static void manejarErrorBD(String accion, SQLException e) {
        logger.severe("Error de base de datos al " + accion + ": " + e.getMessage());
        System.out.println("\nError de base de datos al " + accion + ".\n");
    }

    /** Pide un texto y repite hasta que no esté vacío. */
    private static String leerTextoObligatorio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("Error: este campo no puede estar vacío.");
        }
    }

    private static String leerTextoOpcional(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    /** Pide un precio y repite hasta que sea un número no negativo. */
    private static double leerPrecio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            Double precio = parsearPrecio(entrada);
            if (precio != null) {
                return precio;
            }
        }
    }

    /** Igual que leerPrecio, pero Enter devuelve null (sin cambios). */
    private static Double leerPrecioOpcional(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) {
                return null;
            }
            Double precio = parsearPrecio(entrada);
            if (precio != null) {
                return precio;
            }
        }
    }

    private static Double parsearPrecio(String entrada) {
        try {
            double precio = Double.parseDouble(entrada.replace(',', '.'));
            if (Double.isNaN(precio) || Double.isInfinite(precio)) {
                System.out.println("Error: el precio debe ser un número válido.");
                return null;
            }
            if (precio < 0) {
                logger.warning("Precio negativo rechazado: " + entrada);
                System.out.println("Error: el precio no puede ser negativo.");
                return null;
            }
            return precio;
        } catch (NumberFormatException e) {
            logger.warning("Precio inválido rechazado: " + entrada);
            System.out.println("Error: el precio debe ser un número.");
            return null;
        }
    }

    /** Pide una cantidad y repite hasta que sea un entero no negativo. */
    private static int leerCantidad(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            Integer cantidad = parsearCantidad(entrada);
            if (cantidad != null) {
                return cantidad;
            }
        }
    }

    /** Igual que leerCantidad, pero Enter devuelve null (sin cambios). */
    private static Integer leerCantidadOpcional(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) {
                return null;
            }
            Integer cantidad = parsearCantidad(entrada);
            if (cantidad != null) {
                return cantidad;
            }
        }
    }

    private static Integer parsearCantidad(String entrada) {
        try {
            int cantidad = Integer.parseInt(entrada);
            if (cantidad < 0) {
                logger.warning("Cantidad negativa rechazada: " + entrada);
                System.out.println("Error: la cantidad no puede ser negativa.");
                return null;
            }
            return cantidad;
        } catch (NumberFormatException e) {
            logger.warning("Cantidad inválida rechazada: " + entrada);
            System.out.println("Error: la cantidad debe ser un número entero.");
            return null;
        }
    }
}
