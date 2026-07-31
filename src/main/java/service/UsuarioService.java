package com.nexowear.Nexowear.service;

import com.nexowear.Nexowear.model.Usuario;
import com.nexowear.Nexowear.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // 🌟 Importante
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder; // 🌟 Inyectamos el encriptador (lo configuraremos en el SecurityConfig)

    public void guardar(Usuario usuario) {
        // 🌟 ENCRIPTAMOS LA CONTRASEÑA ANTES DE GUARDAR EN LA BD
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepo.save(usuario);
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepo.findByUsername(username).orElse(null);
    }

    public Usuario registrar(Usuario usuario) {
        // 🌟 También lo aplicamos aquí por si acaso usas este método
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepo.save(usuario);
    }

    public Usuario autenticar(String username, String password) {
        Optional<Usuario> userOpt = usuarioRepo.findByUsername(username);

        if (userOpt.isPresent()) {
            Usuario user = userOpt.get();
            // 🌟 Usamos matches() porque las contraseñas encriptadas no se pueden comparar con .equals()
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public boolean existeUsuario(String username) {
        return usuarioRepo.findByUsername(username).isPresent();
    }
}