package com.example.demoPT.Controladores;

import com.example.demoPT.Modelo.Publicacion;
import com.example.demoPT.Modelo.Usuario;
import com.example.demoPT.Repositorios.RepositorioPublicaciones;
import com.example.demoPT.Repositorios.RepositorioUsuario;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller

public class AuthController {

    private final RepositorioUsuario userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private RepositorioPublicaciones repo;

    public AuthController(RepositorioUsuario userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/register")
    public String mostrarRegistro() {
        return "register";
    }

    @GetMapping("/home")
    public String mostrarHome(Model modelo) {
        List<Publicacion> publicaciones = repo.findAll();
        modelo.addAttribute("publicaciones", publicaciones);
        return "home";
    }

    @GetMapping("/index")
    public String mostrarIndex(Model modelo, Principal principal) {
        // Obtener el nombre de usuario autenticado
        String username = principal.getName();

        // Buscar publicaciones solo del usuario autenticado
        List<Publicacion> publicaciones = repo.findByUsuarioUsername(username);

        modelo.addAttribute("publicaciones", publicaciones);
        return "index";
    }

    @PostMapping("/register")
    public String registrarUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        userRepository.save(usuario);
        return "redirect:/login";
    }
}
