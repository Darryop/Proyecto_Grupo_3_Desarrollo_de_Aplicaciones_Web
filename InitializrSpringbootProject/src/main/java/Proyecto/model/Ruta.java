package Proyecto.model;

/**
 *
 * @author darry
 */

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Data
@Entity
@Table(name = "rutas")
public class Ruta implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ruta;

    private Boolean requiereRol = false;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    // Constructores
    public Ruta() {}

    public Ruta(String ruta, Boolean requiereRol, Rol rol) {
        this.ruta = ruta;
        this.requiereRol = requiereRol;
        this.rol = rol;
    }
}