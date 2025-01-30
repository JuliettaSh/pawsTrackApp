package com.pawstrack.demo.controladores;

import com.pawstrack.demo.model.Publicacion;
import com.pawstrack.demo.model.PublicacionDto;
import com.pawstrack.demo.servicios.RepositorioPublicaciones;
import jakarta.validation.Valid;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/publicaciones")
public class ControladorPublicacion {

    @Autowired
    private RepositorioPublicaciones repo;

    @GetMapping({"", "/"})
    public String mostrarListaPublicaciones(Model modelo) {
        List<Publicacion> publicaciones = repo.findAll();
        modelo.addAttribute("publicaciones", publicaciones);
        return "publicaciones/index";
    }

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        PublicacionDto publiDto = new PublicacionDto();
        modelo.addAttribute("publiDto", publiDto);
        return "publicaciones/CrearPublicacion";
    }

    @PostMapping("/crear")
    public String crearPublicacion(@Valid @ModelAttribute PublicacionDto publicDto, BindingResult resultado) {
        if (publicDto.getArchivoFoto().isEmpty()) {
            resultado.addError(new FieldError("publicDto", "archivoFoto", "El archivo de la imagen es requerido..."));
        }

        if (resultado.hasErrors()) {
            return "publicaciones/CrearPublicacion";
        }
        //guardar la imagen
        MultipartFile foto = publicDto.getArchivoFoto();
        //Date crearFecha = new Date();
        String guardarNombreFoto = foto.getOriginalFilename();
        try {
            String guardarDir = "public/imagenes/";
            Path guardarRuta = Paths.get(guardarDir);
            if (!Files.exists(guardarRuta)) {
                Files.createDirectories(guardarRuta);
            }
            try (InputStream inputStream = foto.getInputStream()) {
                Files.copy(inputStream, Paths.get(guardarDir + guardarNombreFoto), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        Publicacion publicacion = new Publicacion();
        publicacion.setNombreMascota(publicDto.getNombre_mascota());
        publicacion.setTelefono(publicDto.getTelefono());
        publicacion.setDireccion(publicDto.getDireccion());
        publicacion.setDescripcion(publicDto.getDescripcion());
        publicacion.setArchivoFoto(guardarNombreFoto);

        repo.save(publicacion);

        return "redirect:/publicaciones";

    }
    //funcion para editar la publicacion

    @GetMapping("/editar")
    public String mostrarPaginaEditar(Model modelo, @RequestParam int id){
        try {
            Publicacion publicacion = repo.findById(id).get();//lo convierte en tipo publicacion
            modelo.addAttribute("publicacion", publicacion);
            
            PublicacionDto publiDto = new PublicacionDto();
            publiDto.setNombre_mascota(publicacion.getNombreMascota());
            publiDto.setTelefono(publicacion.getTelefono());
            publiDto.setDireccion(publicacion.getDireccion());
            publiDto.setDescripcion(publicacion.getDescripcion());
            
            modelo.addAttribute("publiDto", publiDto);
        } catch (Exception e) {
            System.out.println("Excepcion: " + e.getMessage());
            return "redirect:/publicaciones";
        }
        
        
        
         return "publicaciones/EditarPublicacion";

    }
    
    @PostMapping("/editar")
    public String actualizarPublicacion(Model modelo, @RequestParam int id, @Valid @ModelAttribute PublicacionDto publicDto, BindingResult resultado){
        
        try {
            Publicacion publicacion = repo.findById(id).get();
            modelo.addAttribute("publicacion", publicacion);
            
            //validar si hay errores
            if (resultado.hasErrors()) {
                return "publicaciones/EditarPublicacion";
            }
            if (!publicDto.getArchivoFoto().isEmpty()) {
                //se borra la imagen vieja
                String cargarRuta = "public/imagenes/";
                Path viejaRutaFoto = Paths.get(cargarRuta + publicacion.getArchivoFoto());
                try {
                    Files.delete(viejaRutaFoto);
                } catch (Exception e) {
                    System.out.println("Excepcion: " + e.getMessage());
                }
                //guardar la imagen nueva
                MultipartFile foto = publicDto.getArchivoFoto();
                String nombreFoto = foto.getOriginalFilename();
                try (InputStream inputStream = foto.getInputStream()){
                    Files.copy(inputStream, Paths.get(cargarRuta + nombreFoto),
                    StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                }
                publicacion.setArchivoFoto(nombreFoto);
            }
            
            //actualizamos lo demas
            publicacion.setNombreMascota(publicDto.getNombre_mascota());
            publicacion.setTelefono(publicDto.getTelefono());
            publicacion.setDireccion(publicDto.getDireccion());
            publicacion.setDescripcion(publicDto.getDescripcion());
            repo.save(publicacion);
        } catch (Exception e) {
            System.out.println("Excepcion: " + e.getMessage());
        }
        
        return "redirect:/publicaciones";
    }
    //eliminar publicacion
    @GetMapping("/borrar")
    public String borrarPublicacion(@RequestParam int id){
        try {
            Publicacion publicacion = repo.findById(id).get();
            
            //borrar la foto
            Path rutaFoto = Paths.get("public/imagenes/" + publicacion.getArchivoFoto());
            try {
                Files.delete(rutaFoto);
            } catch (Exception e) {
                System.out.println("Excepcion: "+e.getMessage());
            }
            //borramos la publicacion
            repo.delete(publicacion);
        } catch (Exception e) {
            System.out.println("Excepcion: "+e.getMessage());
        }
        return "redirect:/publicaciones";
    }
    
}
