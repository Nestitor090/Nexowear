package com.nexowear.Nexowear.controller;

import com.nexowear.Nexowear.model.Usuario;
import com.nexowear.Nexowear.service.ProductoService;
import com.nexowear.Nexowear.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    // --- VISTAS PRINCIPALES ---

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "index"; 
    }

    @GetMapping("/productos")
    public String productos(Model model) {
        model.addAttribute("listaProductos", productoService.listarTodos());
        return "productos"; 
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto"; 
    }

    // --- MÉTODOS DE AUTENTICACIÓN (API) ---

    @PostMapping("/auth/registrar")
    @ResponseBody
    public String registrar(@RequestParam String username, @RequestParam String password) {
        try {
            // Verificar si el usuario ya existe para evitar duplicados
            if (usuarioService.buscarPorUsername(username) != null) {
                return "error";
            }
            
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUsername(username);
            nuevoUsuario.setPassword(password); // Idealmente usar BCryptPasswordEncoder aquí
            
            usuarioService.guardar(nuevoUsuario);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public String login(@RequestParam String usuario, @RequestParam String password) {
        Usuario user = usuarioService.buscarPorUsername(usuario);
        
        if (user != null && user.getPassword().equals(password)) {
            return "success";
        }
        return "error";
    }
}