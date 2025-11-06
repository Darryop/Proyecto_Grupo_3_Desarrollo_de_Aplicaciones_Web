package model;

/**
 *
 * @author darry
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "tratamiento_id", nullable = false)
    private Tratamiento tratamiento;
    
    @Column(name = "fecha_cita", nullable = false)
    private LocalDateTime fechaCita;
    
    @Enumerated(EnumType.STRING)
    private EstadoCita estado;
    
    private String notas;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    // ENUM para estados de cita
    public enum EstadoCita {
        PENDIENTE, CONFIRMADA, COMPLETADA, CANCELADA
    }
    
    public Cita() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoCita.PENDIENTE;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public Tratamiento getTratamiento() { return tratamiento; }
    public void setTratamiento(Tratamiento tratamiento) { this.tratamiento = tratamiento; }
    
    public LocalDateTime getFechaCita() { return fechaCita; }
    public void setFechaCita(LocalDateTime fechaCita) { this.fechaCita = fechaCita; }
    
    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }
    
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}