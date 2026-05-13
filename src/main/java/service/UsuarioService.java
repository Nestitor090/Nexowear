package com.nexowear.Nexowear.service;

import com.nexowear.Nexowear.model.Usuario;
import com.nexowear.Nexowear.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    // --- MÉTODOS COMPATIBLES CON TU CONTROLLER ---

    public void guardar(Usuario usuario) {
        usuarioRepo.save(usuario);
    }

    public Usuario buscarPorUsername(String username) {
        // Como tu Repository devuelve un Optional, lo manejamos así:
        return usuarioRepo.findByUsername(username).orElse(null);
    }

    // --- TUS MÉTODOS ACTUALES (PUEDES MANTENERLOS SI QUIERES) ---

    public Usuario registrar(Usuario usuario) {
        return usuarioRepo.save(usuario);
    }

    public Usuario autenticar(String username, String password) {
        Optional<Usuario> userOpt = usuarioRepo.findByUsername(username);
        
        if (userOpt.isPresent()) {
            Usuario user = userOpt.get();
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public boolean existeUsuario(String username) {
        return usuarioRepo.findByUsername(username).isPresent();
    }
}