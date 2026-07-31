package com.nexowear.Nexowear.repository;

import com.nexowear.Nexowear.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Usar Optional es una buena práctica para evitar NullPointerException 
    // si el usuario no existe en la base de datos.
    Optional<Usuario> findByUsername(String username);

    // Útil para validar que no se repitan correos en el registro
    boolean existsByUsername(String username);
}
