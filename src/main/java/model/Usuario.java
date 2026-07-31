package com.nexowear.Nexowear.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario implements UserDetails { // 🌟 Paso 1: Implementar UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]{4,20}$",
            message = "El usuario solo puede contener letras, números o guiones bajos (entre 4 y 20 caracteres y sin espacios)."
    )
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, incluir una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&)."
    )
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "Por favor, introduce un correo electrónico válido.")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "El nombre completo es obligatorio.")
    @Column(nullable = false)
    private String nombre;

    // 🌟 Paso 2: Añadir un campo de rol. Por defecto todos serán 'USER'. Luego podrás cambiarlo a 'ADMIN'.
    @Column(nullable = false)
    private String rol = "USER";

    // Constructor vacío obligatorio para JPA
    public Usuario() {}

    // Constructor rápido actualizado
    public Usuario(String username, String password, String email, String nombre) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nombre = nombre;
        this.rol = "USER";
    }

    // =========================================================================
    // 🌟 MÉTODOS OBLIGATORIOS DE USERDETAILS (Lógica de Spring Security)
    // =========================================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convierte tu string 'rol' (ej: USER o ADMIN) en un permiso que Spring entienda (ROLE_USER o ROLE_ADMIN)
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // La cuenta no expira
    }

    @Override
    public boolean isAccountNonLocked() { // 🌟 El cambio está aquí: de 'Locked' a 'NonLocked'
        return true; // Retorna true porque la cuenta NO está bloqueada
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Las credenciales no expiran
    }

    @Override
    public boolean isEnabled() {
        return true; // El usuario está activo
    }
}