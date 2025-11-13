package Proyecto.model;

/**
 *
 * @author darry
 */

import jakarta.persistence.*;

@Entity
@Table(name = "items_carrito")
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    private CarritoCompras carrito;
    
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;
    
    @ManyToOne
    @JoinColumn(name = "cita_id")
    private Cita cita;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoItem tipo;
    
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;
    
    public ItemCarrito() {
        this.cantidad = 1;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public CarritoCompras getCarrito() { return carrito; }
    public void setCarrito(CarritoCompras carrito) { this.carrito = carrito; }
    
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    
    public Cita getCita() { return cita; }
    public void setCita(Cita cita) { this.cita = cita; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public TipoItem getTipo() { return tipo; }
    public void setTipo(TipoItem tipo) { this.tipo = tipo; }
    
    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
}