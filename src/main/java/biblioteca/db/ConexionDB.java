package biblioteca.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Gestiona la conexión JDBC con SQLite, crea la tabla
 * y carga datos iniciales la primera vez que se ejecuta.
 */
public class ConexionDB {

    private static final Logger logger = Logger.getLogger(ConexionDB.class.getName());

    // La base de datos es un solo archivo, creado en la carpeta de ejecución.
    private static final String URL = "jdbc:sqlite:biblioteca.db";

    private ConexionDB() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /** Crea la tabla si no existe y, si está vacía, inserta libros de ejemplo. */
    public static void inicializar() throws SQLException {
        String crearTabla =
            "CREATE TABLE IF NOT EXISTS libros (" +
            "  codigo   TEXT PRIMARY KEY," +
            "  titulo   TEXT NOT NULL," +
            "  autor    TEXT NOT NULL," +
            "  precio   REAL NOT NULL CHECK (precio >= 0)," +
            "  cantidad INTEGER NOT NULL CHECK (cantidad >= 0)" +
            ")";

        try (Connection con = getConnection();
             Statement st = con.createStatement()) {

            st.execute(crearTabla);
            logger.info("Tabla 'libros' verificada/creada.");

            if (estaVacia(con)) {
                cargarDatosIniciales(con);
            }
        }
    }

    private static boolean estaVacia(Connection con) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM libros")) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    private static void cargarDatosIniciales(Connection con) throws SQLException {
        String sql = "INSERT INTO libros (codigo, titulo, autor, precio, cantidad) VALUES (?, ?, ?, ?, ?)";

        Object[][] datos = {
            {"LIB-001", "Cien años de soledad", "Gabriel García Márquez", 24.90, 5},
            {"LIB-002", "El amor en los tiempos del cólera", "Gabriel García Márquez", 22.50, 3},
            {"LIB-003", "Crónica de una muerte anunciada", "Gabriel García Márquez", 15.00, 4},
            {"LIB-004", "Don Quijote de la Mancha", "Miguel de Cervantes", 29.90, 2},
            {"LIB-005", "El principito", "Antoine de Saint-Exupéry", 12.00, 10},
            {"LIB-006", "Rayuela", "Julio Cortázar", 19.80, 3},
            {"LIB-007", "Pedro Páramo", "Juan Rulfo", 14.50, 6},
            {"LIB-008", "La vorágine", "José Eustasio Rivera", 16.75, 2}
        };

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Object[] fila : datos) {
                ps.setString(1, (String) fila[0]);
                ps.setString(2, (String) fila[1]);
                ps.setString(3, (String) fila[2]);
                ps.setDouble(4, (Double) fila[3]);
                ps.setInt(5, (Integer) fila[4]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        logger.info("Datos iniciales cargados: " + datos.length + " libros.");
    }
}
