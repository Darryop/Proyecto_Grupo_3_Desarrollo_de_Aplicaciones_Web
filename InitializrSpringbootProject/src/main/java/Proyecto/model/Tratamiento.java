package Proyecto.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tratamientos")
public class Tratamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "codigo_tratamiento", unique = true, nullable = false)
    private String codigoTratamiento;
    
    @Column(nullable = false)
    private String nombre;
    
    private String descripcion;
    
    @Column(nullable = false)
    private Double precio;
    
    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaTratamiento categoria;
    
    @Column(name = "imagen_url")
    private String imagenUrl;
    
    private Boolean activo;
    
    @Column(name = "requiere_consulta")
    private Boolean requiereConsulta;
    
    @OneToMany(mappedBy = "tratamiento")
    private List<Cita> citas;
    
    public Tratamiento() {
        this.activo = true;
        this.requiereConsulta = false;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCodigoTratamiento() { return codigoTratamiento; }
    public void setCodigoTratamiento(String codigoTratamiento) { this.codigoTratamiento = codigoTratamiento; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    
    public CategoriaTratamiento getCategoria() { return categoria; }
    public void setCategoria(CategoriaTratamiento categoria) { this.categoria = categoria; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public Boolean getRequiereConsulta() { return requiereConsulta; }
    public void setRequiereConsulta(Boolean requiereConsulta) { this.requiereConsulta = requiereConsulta; }
    
    public List<Cita> getCitas() { return citas; }
    public void setCitas(List<Cita> citas) { this.citas = citas; }
}