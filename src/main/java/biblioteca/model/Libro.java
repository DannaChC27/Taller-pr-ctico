package biblioteca.model;

import java.util.Locale;

/**
 * Modelo: representa un libro de la biblioteca.
 * Valida sus datos para que nunca exista un libro con valores incorrectos.
 */
public class Libro {

    private final String codigo;
    private String titulo;
    private String autor;
    private double precio;
    private int cantidad;

    public Libro(String codigo, String titulo, String autor, double precio, int cantidad) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código no puede estar vacío.");
        }
        this.codigo = codigo.trim();
        setTitulo(titulo);
        setAutor(autor);
        setPrecio(precio);
        setCantidad(cantidad);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        this.titulo = titulo.trim();
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("El autor no puede estar vacío.");
        }
        this.autor = autor.trim();
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (Double.isNaN(precio) || Double.isInfinite(precio) || precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
        this.cantidad = cantidad;
    }

    public void mostrar() {
        System.out.println(
            "Código: " + codigo + " | " +
            "Título: " + titulo + " | " +
            "Autor: " + autor + " | " +
            String.format(Locale.US, "Precio: $%,.2f", precio) + " | " +
            "Disponibles: " + cantidad
        );
    }
}
