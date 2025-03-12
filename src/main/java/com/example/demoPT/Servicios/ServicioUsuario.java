package com.example.demoPT.Servicios;

import com.example.demoPT.Modelo.Usuario;
import com.example.demoPT.Repositorios.RepositorioUsuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ServicioUsuario {
    private final RepositorioUsuario userRepository;
    private final PasswordEncoder passwordEncoder;

    public ServicioUsuario(RepositorioUsuario userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Usuario> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void saveUser(Usuario user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
    }
}

/*

package com.example.demoPT.Servicios;

public class ServicioUsuario {
    
}
*/