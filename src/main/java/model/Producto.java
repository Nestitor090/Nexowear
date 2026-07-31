package com.nexowear.Nexowear.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data // Genera getters, setters, toString y equals automáticamente
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

    @Column(nullable = false)
    private String categoria; // Ejemplo: "mujer", "hombre", "ninos"

    @Column(name = "es_destacado", nullable = false, columnDefinition = "boolean default false")
    private boolean destacado;

    @Column(name = "es_nuevo", nullable = false, columnDefinition = "boolean default false")
    private boolean nuevo;

    // 🌟 NUEVOS ATRIBUTOS PARA LOS FILTROS PREMIUM (Faltaba declararlos aquí)
    @Column(nullable = true) // Cambia a false si obligatoriamente cada prenda debe tener tipo
    private String tipo;      // Ejemplo: "blusa", "polo", "casaca"

    @Column(nullable = true)
    private String color;     // Ejemplo: "negro", "rojo", "blanco"

    @Column(nullable = true)
    private String talla;     // Ejemplo: "S", "M", "L"

    @Column(nullable = true)
    private String marca;     // Ejemplo: "Nexo Wear"

    @Column(nullable = true)
    private Integer stock;    // Cantidad disponible

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Constructor vacío (Obligatorio para JPA)
    public Producto() {}

    // Constructor con parámetros (¡Ahora sí compilará perfecto!)
    public Producto(String nombre, Double precio, String imagen, String categoria,
                    boolean destacado, boolean nuevo, String tipo, String color,
                    String talla, String marca, Integer stock) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        this.categoria = categoria;
        this.destacado = destacado;
        this.nuevo = nuevo;

        // Inicialización de los nuevos atributos para filtros
        this.tipo = tipo;
        this.color = color;
        this.talla = talla;
        this.marca = marca;
        this.stock = stock;

        // Captura automáticamente la fecha y hora exacta en la que se crea la prenda
        this.fechaCreacion = java.time.LocalDateTime.now();
    }
}