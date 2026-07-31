package com.nexowear.Nexowear.repository;

import com.nexowear.Nexowear.model.Usuario;
import com.nexowear.Nexowear.model.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarritoRepository extends JpaRepository<CarritoItem, Long> {
    List<CarritoItem> findByUsuario(Usuario usuario);
}