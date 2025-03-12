package com.example.demoPT.Repositorios;

import com.example.demoPT.Modelo.Publicacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RepositorioPublicaciones extends JpaRepository<Publicacion, Integer>{
    List<Publicacion> findByUsuarioUsername(String username);
}
