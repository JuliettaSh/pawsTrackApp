package com.pawstrack.demo.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity 
@Table(name = "publicaciones")
public class Publicacion implements Serializable{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id_publicacion;
    
    @Basic
    private String nombreMascota;
    private String direccion;
    private String telefono;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    private String archivoFoto;
    
    
    public Publicacion() {
    }

    public int getId_publicacion() {
        return id_publicacion;
    }

    public void setId_publicacion(int id_publicacion) {
        this.id_publicacion = id_publicacion;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getArchivoFoto() {
        return archivoFoto;
    }

    public void setArchivoFoto(String archivoFoto) {
        this.archivoFoto = archivoFoto;
    }
 
}
