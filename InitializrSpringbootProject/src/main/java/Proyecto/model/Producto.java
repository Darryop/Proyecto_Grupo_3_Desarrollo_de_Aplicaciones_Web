package Proyecto.model;

/**
 *
 * @author darry
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "codigo_producto", unique = true, nullable = false)
    private String codigoProducto;
    
    @Column(nullable = false)
    private String nombre;
    
    private String descripcion;
    
    @Column(nullable = false)
    private Double precio;
    
    @Column(nullable = false)
    private Integer stock;
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaProducto categoria;
    
    @Column(name = "imagen_url")
    private String imagenUrl;
    
    private Boolean activo;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    public Producto() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public CategoriaProducto getCategoria() { return categoria; }
    public void setCategoria(CategoriaProducto categoria) { this.categoria = categoria; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
