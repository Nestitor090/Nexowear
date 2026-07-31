package com.nexowear.Nexowear.controller;

import java.util.List;
import com.nexowear.Nexowear.model.Producto;
import com.nexowear.Nexowear.model.Usuario;
import com.nexowear.Nexowear.service.ProductoService;
import com.nexowear.Nexowear.service.UsuarioService;
import com.nexowear.Nexowear.security.JwtUtils; // 🌟 Inyección del Utilitario JWT
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager; // 🌟 Inyección de Spring Security
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

@Controller
public class WebController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager; // 🌟 Manejador de Autenticación de Spring

    @Autowired
    private JwtUtils jwtUtils; // 🌟 Generador de componentes JWT

    @GetMapping("/")
    public String home(Model model) {
        List<Producto> productosDestacados = productoService.obtenerDestacados();
        List<Producto> productosNuevos = productoService.obtenerNuevos();

        model.addAttribute("productos", productosDestacados);
        model.addAttribute("nuevosProductos", productosNuevos);

        return "index";
    }

    // 🌟 SECCIÓN MUJER
    @GetMapping("/productos/mujer")
    public String productosMujer(Model model) {
        model.addAttribute("listaProductos", productoService.listarPorCategoria("mujer"));
        model.addAttribute("categoriaActual", "MUJER");
        return "productos";
    }

    // 🌟 SECCIÓN HOMBRE
    @GetMapping("/productos/hombre")
    public String productosHombre(Model model) {
        model.addAttribute("listaProductos", productoService.listarPorCategoria("hombre"));
        model.addAttribute("categoriaActual", "HOMBRE");
        return "productos";
    }

    // 🌟 SECCIÓN NIÑOS
    @GetMapping("/productos/ninos")
    public String productosNinos(Model model) {
        model.addAttribute("listaProductos", productoService.listarPorCategoria("ninos"));
        model.addAttribute("categoriaActual", "NIÑOS");
        return "productos";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

    @PostMapping("/auth/registrar")
    @ResponseBody
    public String registrar(@Valid @ModelAttribute Usuario nuevoUsuario, BindingResult resultado) {
        if (resultado.hasErrors()) {
            if (resultado.getFieldError() != null) {
                return "error: " + resultado.getFieldError().getDefaultMessage();
            }
            return "error: Datos de formulario inválidos.";
        }

        try {
            if (usuarioService.buscarPorUsername(nuevoUsuario.getUsername()) != null) {
                return "error: El nombre de usuario ya está en uso.";
            }
            // Guarda encriptando automáticamente gracias al cambio en tu UsuarioService
            usuarioService.guardar(nuevoUsuario);
            return "success";
        } catch (Exception e) {
            return "error: Ocurrió un error en el servidor.";
        }
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestParam String usuario, @RequestParam String password) {
        try {
            // Spring Security valida las credenciales
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usuario, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generamos el token JWT real
            String token = jwtUtils.generateJwtToken(authentication);

            // 🌟 EXTRAEMOS EL ROL DEL USUARIO AUTENTICADO
            // Recuperamos el objeto principal (tu entidad Usuario que implementa UserDetails)
            com.nexowear.Nexowear.model.Usuario userDetails = (com.nexowear.Nexowear.model.Usuario) authentication.getPrincipal();
            String rolUsuario = userDetails.getRol(); // Obtenemos el "USER" o "ADMIN"

            // Enviamos un JSON con el token y el rol hacia el frontend
            return ResponseEntity.ok(java.util.Map.of(
                    "status", "success",
                    "token", token,
                    "rol", rolUsuario // 🌟 Enviado con éxito al JS
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(java.util.Map.of(
                    "status", "error",
                    "message", "Usuario o contraseña incorrectos."
            ));
        }
    }

}