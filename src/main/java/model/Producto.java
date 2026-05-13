package com.nexowear.Nexowear.model;

import jakarta.persistence.*;
import lombok.Data; // Opcional: si usas Lombok para ahorrar Getters y Setters

@Entity
@Table(name = "productos")
@Data // Genera getters, setters y constructor automáticamente
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private String imagen; // Ejemplo: "ropa1.jpg"

    // IMPORTANTE: Este campo es necesario para los filtros de productos.html
    @Column(nullable = false)
    private String categoria; // Ejemplo: "polos", "casacas", "hoodies"

    // Constructor vacío (Obligatorio para JPA)
    public Producto() {}

    // Constructor con campos (Útil para pruebas rápidas)
    public Producto(String nombre, Double precio, String imagen, String categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        this.categoria = categoria;
    }

    // Si no usas Lombok, mantén tus Getters y Setters aquí abajo...
}