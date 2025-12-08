package Proyecto.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    private String telefono;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    private Boolean activo = true;

    // Enum para el tipo de usuario (de la base de datos)
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoUsuario tipo;  // Cambiado de Rol a TipoUsuario

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    // Constructores
    public Usuario() {
        this.fechaRegistro = LocalDateTime.now();
        this.activo = true;
    }

    public Usuario(String email, String password, String nombre, String apellido) {
        this();
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    // Método helper para verificar si tiene un rol específico
    public boolean tieneRol(String rolNombre) {
        if (roles == null) return false;
        return roles.stream().anyMatch(rol -> rol.getNombre().equals(rolNombre));
    }

    // Enumeración para el tipo de usuario
    public enum TipoUsuario {
        CLIENTE, ADMIN
    }

    // Getters y Setters (Lombok debería generarlos, pero por si acaso)

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    // El resto de getters y setters los genera Lombok con @Data
    
    
}