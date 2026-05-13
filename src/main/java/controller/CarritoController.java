package com.nexowear.Nexowear.controller;

import com.nexowear.Nexowear.model.CarritoItem;
import com.nexowear.Nexowear.model.Producto;
import com.nexowear.Nexowear.model.Usuario;
import com.nexowear.Nexowear.repository.CarritoRepository;
import com.nexowear.Nexowear.repository.ProductoRepository;
import com.nexowear.Nexowear.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoRepository carritoRepo;
    @Autowired
    private ProductoRepository productoRepo;
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarAlCarrito(@RequestParam String username, @RequestParam Long productoId) {
        Usuario usuario = usuarioService.buscarPorUsername(username);
        if (usuario == null) return ResponseEntity.status(401).body("Debe iniciar sesión");

        Producto producto = productoRepo.findById(productoId).orElse(null);
        if (producto == null) return ResponseEntity.badRequest().body("Producto no encontrado");

        CarritoItem item = new CarritoItem();
        item.setUsuario(usuario);
        item.setProducto(producto);
        item.setCantidad(1);

        carritoRepo.save(item);
        return ResponseEntity.ok("Producto agregado al carrito");
    }

    @GetMapping("/listar")
    public List<CarritoItem> listarCarrito(@RequestParam String username) {
        Usuario usuario = usuarioService.buscarPorUsername(username);
        return carritoRepo.findByUsuario(usuario);
    }

    // --- NUEVO MÉTODO PARA ELIMINAR ---
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarDelCarrito(@PathVariable Long id) {
        try {
            // Verificamos si existe antes de intentar borrar
            if (carritoRepo.existsById(id)) {
                carritoRepo.deleteById(id);
                return ResponseEntity.ok("Producto eliminado correctamente");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El ítem no existe");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el producto");
        }
    }
}