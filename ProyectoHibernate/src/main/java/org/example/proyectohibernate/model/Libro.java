package org.example.proyectohibernate.model;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name="libros", schema = "catalogos")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "autor", nullable = false, length = 100)
    private String autor;

    @Column(name = "anio_publicacion", nullable = false)
    private int anio;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    public Libro(long id, String titulo, String autor, int anio, BigDecimal precio) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.anio = anio;
        this.precio = precio;
    }

    public long getId() {

        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getAnio() {
        return anio;
    }

    public BigDecimal getPrecio() {
        return precio;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setAnio(int anioPublicacion) {
        this.anio = anioPublicacion;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Libro() {

    }
}