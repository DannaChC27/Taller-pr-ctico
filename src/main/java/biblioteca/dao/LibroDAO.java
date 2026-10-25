package biblioteca.dao;

import biblioteca.db.ConexionDB;
import biblioteca.model.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * DAO (Data Access Object): único lugar donde se escribe SQL.
 * Todas las consultas usan PreparedStatement y try-with-resources,
 * así se evita la inyección SQL y se cierran los recursos automáticamente.
 */
public class LibroDAO {

    private static final Logger logger = Logger.getLogger(LibroDAO.class.getName());

    // --------------------------------------------------------
    // CREATE
    // --------------------------------------------------------

    public void insertar(Libro libro) throws SQLException {
        String sql = "INSERT INTO libros (codigo, titulo, autor, precio, cantidad) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, libro.getCodigo());
            ps.setString(2, libro.getTitulo());
            ps.setString(3, libro.getAutor());
            ps.setDouble(4, libro.getPrecio());
            ps.setInt(5, libro.getCantidad());
            ps.executeUpdate();

            logger.info("Libro creado - Código: " + libro.getCodigo()
                + ", Título: " + libro.getTitulo()
                + ", Precio: " + libro.getPrecio()
                + ", Cantidad: " + libro.getCantidad());
        }
    }

    // --------------------------------------------------------
    // READ
    // --------------------------------------------------------

    public List<Libro> listar() throws SQLException {
        String sql = "SELECT codigo, titulo, autor, precio, cantidad FROM libros ORDER BY codigo";
        List<Libro> libros = new ArrayList<>();

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                libros.add(mapear(rs));
            }
        }
        logger.info("Se consultó la lista de libros (" + libros.size() + " registros).");
        return libros;
    }

    /** Busca por código exacto o por coincidencia parcial en el título. */
    public List<Libro> buscar(String termino) throws SQLException {
        String sql = "SELECT codigo, titulo, autor, precio, cantidad FROM libros "
                   + "WHERE LOWER(codigo) = LOWER(?) OR LOWER(titulo) LIKE LOWER(?) "
                   + "ORDER BY codigo";
        List<Libro> libros = new ArrayList<>();

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, termino.trim());
            ps.setString(2, "%" + termino.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapear(rs));
                }
            }
        }
        logger.info("Búsqueda '" + termino + "' - " + libros.size() + " resultado(s).");
        return libros;
    }

    public Libro buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT codigo, titulo, autor, precio, cantidad FROM libros WHERE codigo = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo.trim());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public boolean existe(String codigo) throws SQLException {
        return buscarPorCodigo(codigo) != null;
    }

    // --------------------------------------------------------
    // UPDATE
    // --------------------------------------------------------

    public boolean actualizar(Libro libro) throws SQLException {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, precio = ?, cantidad = ? WHERE codigo = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setDouble(3, libro.getPrecio());
            ps.setInt(4, libro.getCantidad());
            ps.setString(5, libro.getCodigo());

            boolean actualizado = ps.executeUpdate() > 0;
            if (actualizado) {
                logger.info("Libro actualizado - Código: " + libro.getCodigo()
                    + ", Título: " + libro.getTitulo()
                    + ", Precio: " + libro.getPrecio()
                    + ", Cantidad: " + libro.getCantidad());
            }
            return actualizado;
        }
    }

    // --------------------------------------------------------
    // DELETE
    // --------------------------------------------------------

    public boolean eliminar(String codigo) throws SQLException {
        String sql = "DELETE FROM libros WHERE codigo = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo.trim());

            boolean eliminado = ps.executeUpdate() > 0;
            if (eliminado) {
                logger.info("Libro eliminado - Código: " + codigo.trim());
            }
            return eliminado;
        }
    }

    // --------------------------------------------------------
    // Utilidad
    // --------------------------------------------------------

    private Libro mapear(ResultSet rs) throws SQLException {
        return new Libro(
            rs.getString("codigo"),
            rs.getString("titulo"),
            rs.getString("autor"),
            rs.getDouble("precio"),
            rs.getInt("cantidad")
        );
    }
}
