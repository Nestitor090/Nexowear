package com.nexowear.Nexowear.controller;

import com.nexowear.Nexowear.model.Usuario;
import com.nexowear.Nexowear.model.CarritoItem;
import com.nexowear.Nexowear.model.Producto;
import com.nexowear.Nexowear.repository.CarritoRepository;
import com.nexowear.Nexowear.repository.ProductoRepository;
import com.nexowear.Nexowear.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

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

        // CORRECCIÓN: Buscar si el usuario ya tiene este producto en su carrito
        List<CarritoItem> carritoActual = carritoRepo.findByUsuario(usuario);
        Optional<CarritoItem> itemExistente = carritoActual.stream()
                .filter(item -> item.getProducto().getId().equals(productoId))
                .findFirst();

        if (itemExistente.isPresent()) {
            // Si ya existe, aumentamos su cantidad en 1
            CarritoItem item = itemExistente.get();
            item.setCantidad(item.getCantidad() + 1);
            carritoRepo.save(item);
            return ResponseEntity.ok("Cantidad del producto aumentada");
        } else {
            // Si es nuevo, lo creamos desde cero con cantidad 1
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.setUsuario(usuario);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(1);
            carritoRepo.save(nuevoItem);
            return ResponseEntity.ok("Producto agregado al carrito");
        }
    }

    @GetMapping("/listar")
    public List<CarritoItem> listarCarrito(@RequestParam String username) {
        Usuario usuario = usuarioService.buscarPorUsername(username);
        return carritoRepo.findByUsuario(usuario);
    }

    // --- NUEVO MÉTODO: ACTUALIZAR CANTIDAD DESDE EL DROPDOWN DEL FRONTEND ---
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarCantidad(@PathVariable Long id, @RequestParam Integer cantidad) {
        try {
            CarritoItem item = carritoRepo.findById(id).orElse(null);
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El ítem no existe");
            }

            if (cantidad <= 0) {
                carritoRepo.deleteById(id);
                return ResponseEntity.ok("Producto eliminado por cantidad cero");
            }

            item.setCantidad(cantidad);
            carritoRepo.save(item);
            return ResponseEntity.ok("Cantidad actualizada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la cantidad");
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarDelCarrito(@PathVariable Long id) {
        try {
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