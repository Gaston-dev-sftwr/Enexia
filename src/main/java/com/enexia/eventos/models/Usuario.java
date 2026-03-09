package com.enexia.eventos.models;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {

    /// Attributes ///

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    private String nombre;
    private String apellido;


    @Column(unique = true)
    private String email;

    private String password;
    private boolean estado;


    ///  CONSTRUCTORS ///

    public Usuario() {
        this.estado = true; // Por defecto activo
    }



    public Usuario(String nombre, String apellido, String email, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.estado = true;
    }


    // Getters y Setters

    public Long getID() { return ID; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public boolean isEstado() { return estado; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setEstado(boolean estado) { this.estado = estado; }
}