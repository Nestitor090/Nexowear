package com.nexowear.Nexowear.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data // Si usas Lombok, esto te ahorra los Getters y Setters
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // RECOMENDACIÓN: Añadir email y nombre completo para un perfil más real
    @Column(unique = true)
    private String email;

    private String nombre; 

    // Constructor vacío obligatorio para JPA
    public Usuario() {}

    // Constructor para registro rápido
    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    // Getters y Setters (si no usas Lombok)
    // ...
}
