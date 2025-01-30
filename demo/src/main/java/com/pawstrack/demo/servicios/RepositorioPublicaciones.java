package com.pawstrack.demo.servicios;

import com.pawstrack.demo.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioPublicaciones extends JpaRepository<Publicacion, Integer>{
    
}
