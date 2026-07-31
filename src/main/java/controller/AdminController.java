package com.nexowear.Nexowear.controller;

import com.nexowear.Nexowear.model.Producto;
import com.nexowear.Nexowear.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductoService productoService;

    // Carga la plantilla HTML de forma pública. El script de JS en el cliente valida visualmente.
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Producto> todosLosProductos = productoService.listarTodos();
        model.addAttribute("productos", todosLosProductos);
        return "admin";
    }

    @GetMapping("/productos/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Long id) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("status", "error", "message", "Producto no encontrado."));
        }
    }
    // 🌟 SE REMOVIÓ EL @PreAuthorize. Ahora la seguridad la controla SecurityConfig de forma global.
    @PostMapping("/productos/agregar")
    @ResponseBody
    public ResponseEntity<?> agregarProducto(@RequestBody Producto nuevoProducto) {
        try {
            productoService.guardar(nuevoProducto);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Producto creado correctamente."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "No se pudo guardar."));
        }
    }

    // 🌟 SE REMOVIÓ EL @PreAuthorize. Ahora la seguridad la controla SecurityConfig de forma global.
    @DeleteMapping("/productos/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminar(id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Producto eliminado."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "No se pudo eliminar."));
        }
    }

    @PutMapping("/productos/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Producto productoDetalles) {
        try {
            // Usamos el nuevo método del servicio
            Producto producto = productoService.obtenerPorId(id);

            // Actualizamos los campos
            producto.setNombre(productoDetalles.getNombre());
            producto.setCategoria(productoDetalles.getCategoria());
            producto.setPrecio(productoDetalles.getPrecio());
            producto.setStock(productoDetalles.getStock());

            // Guardamos los cambios
            productoService.guardar(producto);

            return ResponseEntity.ok(Map.of("status", "success", "message", "Producto actualizado correctamente."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "No se pudo actualizar: " + e.getMessage()));
        }
    }
}