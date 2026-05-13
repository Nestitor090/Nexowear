package com.nexowear.Nexowear.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "carrito")
@Data
public class CarritoItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamos con tu tabla de usuarios
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relacionamos con tu tabla de productos
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    private Integer cantidad;
}
