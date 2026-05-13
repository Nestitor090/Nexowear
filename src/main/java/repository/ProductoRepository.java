package com.nexowear.Nexowear.repository;

import com.nexowear.Nexowear.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Método para filtrar productos por categoría (polos, casacas, etc.)
    // Spring Data JPA generará la consulta SQL automáticamente
    List<Producto> findByCategoria(String categoria);

    // Opcional: Si quieres buscar productos por nombre (para la barra de búsqueda)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}