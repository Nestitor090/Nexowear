package com.nexowear.Nexowear.service;

import com.nexowear.Nexowear.model.Producto;
import com.nexowear.Nexowear.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepo;

    // Obtener todo el catálogo
    public List<Producto> listarTodos() {
        return productoRepo.findAll();
    }

    // NUEVO: Filtrar por categoría (Polos, Casacas, etc.)
    public List<Producto> listarPorCategoria(String categoria) {
        return productoRepo.findByCategoria(categoria);
    }

    // NUEVO: Buscar por nombre para la barra de búsqueda
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepo.findByNombreContainingIgnoreCase(nombre);
    }

    // NUEVO: Guardar o actualizar (útil si luego creas un panel de admin)
    public void guardar(Producto producto) {
        productoRepo.save(producto);
    }

    // 🌟 NUEVO MÉTODO: Eliminar producto por ID (Para el AdminController)
    public void eliminar(Long id) {
        productoRepo.deleteById(id);
    }

    public List<Producto> obtenerDestacados() {
        return productoRepo.findByDestacadoTrue(); // Cambio a productoRepo
    }

    public List<Producto> obtenerNuevos() {
        return productoRepo.findByNuevoTrue(); // Cambio a productoRepo
    }
    public Producto obtenerPorId(Long id) {
        return productoRepo.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }
}