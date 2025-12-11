package Proyecto.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;
    
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;
    
    @ManyToOne
    @JoinColumn(name = "tratamiento_id")
    private Tratamiento tratamiento;
    
    @ManyToOne
    @JoinColumn(name = "cita_id")
    private Cita cita;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoItem tipo;
    
    // Constructores
    public DetalleVenta() {}
    
    public DetalleVenta(ItemCarrito itemCarrito, Venta venta) {
        this.venta = venta;
        this.cantidad = itemCarrito.getCantidad();
        
        // Asegurar que precioUnitario y subtotal sean BigDecimal
        this.precioUnitario = itemCarrito.getPrecioUnitario() != null ? 
            BigDecimal.valueOf(itemCarrito.getPrecioUnitario()) : BigDecimal.ZERO;
        
        this.subtotal = this.precioUnitario.multiply(BigDecimal.valueOf(this.cantidad));
        this.tipo = itemCarrito.getTipo();
        
        if (itemCarrito.getTipo() == TipoItem.PRODUCTO && itemCarrito.getProducto() != null) {
            this.producto = itemCarrito.getProducto();
            // Si es producto, también podemos guardar el tratamiento como null
            this.tratamiento = null;
            this.cita = null;
        } else if (itemCarrito.getTipo() == TipoItem.CITA && itemCarrito.getCita() != null) {
            this.cita = itemCarrito.getCita();
            if (itemCarrito.getCita().getTratamiento() != null) {
                this.tratamiento = itemCarrito.getCita().getTratamiento();
            }
            this.producto = null;
        }
    }
    
    // Getters y Setters

    public Long getId() {
        return id;
    }

    public Venta getVenta() {
        return venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public Cita getCita() {
        return cita;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public TipoItem getTipo() {
        return tipo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setTipo(TipoItem tipo) {
        this.tipo = tipo;
    }
}