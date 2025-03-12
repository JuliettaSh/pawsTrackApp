package com.example.demoPT.Controladores;

import com.example.demoPT.Modelo.Publicacion;
import com.example.demoPT.Modelo.PublicacionDto;
import com.example.demoPT.Modelo.Usuario;
import com.example.demoPT.Repositorios.RepositorioPublicaciones;
import com.example.demoPT.Repositorios.RepositorioUsuario;
import jakarta.validation.Valid;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    @Autowired
    private RepositorioUsuario repoU;

    
    /*
    @GetMapping({"", "/"})
    public String mostrarListaPublicaciones(Model modelo) {
        List<Publicacion> publicaciones = repo.findAll();
        modelo.addAttribute("publicaciones", publicaciones);
        return "index";
    }
   */

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        // Verifica si el usuario está autenticado
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            PublicacionDto publiDto = new PublicacionDto();
            modelo.addAttribute("publiDto", publiDto);
            return "publicaciones/CrearPublicacion";  // Si está autenticado, muestra la página para crear publicación
        } else {
            return "redirect:/login";  // Si no está autenticado, redirige al login
        }
        //PublicacionDto publiDto = new PublicacionDto();
        //modelo.addAttribute("publiDto", publiDto);
        //return "publicaciones/CrearPublicacion";
    }

@PostMapping("/crear")
public String crearPublicacion(@ModelAttribute("publiDto") PublicacionDto publiDto, @RequestParam("archivoFoto") MultipartFile archivoFoto,       Principal principal) {
    // Obtener el usuario autenticado
    String username = principal.getName();
    
    
    Usuario usuario = repoU.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // Crear la publicación
    Publicacion publicacion = new Publicacion();
    publicacion.setNombreMascota(publiDto.getNombre_mascota());
    publicacion.setDireccion(publiDto.getDireccion());
    publicacion.setTelefono(publiDto.getTelefono());
    publicacion.setDescripcion(publiDto.getDescripcion());
    publicacion.setUsuario(usuario); // Asignar usuario autenticado

    // Guardar la foto si es necesario
    if (!archivoFoto.isEmpty()) {
        String nombreArchivo = archivoFoto.getOriginalFilename();
        publicacion.setArchivoFoto(nombreArchivo);
        // Aquí puedes agregar lógica para guardar la foto en un directorio
    }

    // Guardar en la base de datos
    repo.save(publicacion);

    return "redirect:/index";
}

    //funcion para editar la publicacion

    @GetMapping("/editar")
    public String mostrarPaginaEditar(Model modelo, @RequestParam int id) {
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
            return "redirect:/index";
        }

        return "publicaciones/EditarPublicacion";

    }

    @PostMapping("/editar")
    public String actualizarPublicacion(Model modelo, @RequestParam int id, @Valid @ModelAttribute PublicacionDto publicDto, BindingResult resultado) {

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
                try (InputStream inputStream = foto.getInputStream()) {
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

        return "redirect:/index";
    }

    //eliminar publicacion
    @GetMapping("/borrar")
    public String borrarPublicacion(@RequestParam int id) {
        try {
            Publicacion publicacion = repo.findById(id).get();

            //borrar la foto
            Path rutaFoto = Paths.get("public/imagenes/" + publicacion.getArchivoFoto());
            try {
                Files.delete(rutaFoto);
            } catch (Exception e) {
                System.out.println("Excepcion: " + e.getMessage());
            }
            //borramos la publicacion
            repo.delete(publicacion);
        } catch (Exception e) {
            System.out.println("Excepcion: " + e.getMessage());
        }
        return "redirect:/index";
    }

}
