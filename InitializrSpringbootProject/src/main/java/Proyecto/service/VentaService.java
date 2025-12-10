package Proyecto.service;

/**
 *
 * @author darry
 */

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Proyecto.model.CarritoCompras;
import Proyecto.model.EstadoCarrito;
import Proyecto.model.EstadoVenta;
import Proyecto.model.ItemCarrito;
import Proyecto.model.MetodoPago;
import Proyecto.model.Usuario;
import Proyecto.model.Venta;
import Proyecto.repository.CarritoComprasRepository;
import Proyecto.repository.VentaRepository;

@Service
public class VentaService {
    
    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private CarritoComprasRepository carritoRepository;
    
    @Autowired
    private CarritoService carritoService;
    
    public List<Venta> obtenerTodas() {
        // Necesitamos cargar todas las relaciones necesarias
        List<Venta> ventas = ventaRepository.findAllWithRelations();
        
        // Para cada venta, cargar las relaciones adicionales si es necesario
        ventas.forEach(venta -> {
            if (venta.getCarrito() != null) {
                // Inicializar la colección items del carrito
                CarritoCompras carrito = carritoRepository.findById(venta.getCarrito().getId())
                    .orElse(null);
                if (carrito != null) {
                    // Cargar items con sus relaciones
                    List<ItemCarrito> items = carrito.getItems();
                    if (items != null) {
                        items.forEach(item -> {
                            // Inicializar relaciones perezosas si es necesario
                            if (item.getProducto() != null) {
                                item.getProducto().getNombre(); // Para inicializar
                            }
                            if (item.getCita() != null && item.getCita().getTratamiento() != null) {
                                item.getCita().getTratamiento().getNombre(); // Para inicializar
                            }
                        });
                    }
                    venta.getCarrito().setItems(items);
                }
            }
        });
        
        return ventas;
    }
    
    public Optional<Venta> obtenerPorId(Long id) {
        return ventaRepository.findById(id);
    }
    
    public Venta guardar(Venta venta) {
        return ventaRepository.save(venta);
    }
    
    public List<Venta> obtenerPorUsuario(Usuario usuario) {
        return ventaRepository.findByUsuario(usuario);
    }
    
    public List<Venta> obtenerPorEstado(EstadoVenta estado) {
        return ventaRepository.findByEstado(estado);
    }
    
    public List<Venta> obtenerVentasEntreFechas(LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.findByFechaVentaBetween(inicio, fin);
    }
    
    public Venta procesarVenta(Usuario usuario, MetodoPago metodoPago) {
        // Obtener carrito activo del usuario
        CarritoCompras carrito = carritoService.obtenerCarritoActivo(usuario);
        List<ItemCarrito> items = carritoService.obtenerItemsDelCarrito(usuario);
        
        if (items.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }
        
        // Calcular total
        double total = carritoService.calcularTotalCarrito(usuario);
        
        // Crear venta
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setCarrito(carrito);
        venta.setTotal(total);
        venta.setMetodoPago(metodoPago);
        venta.setEstado(EstadoVenta.COMPLETADA);
        
        // Actualizar estado del carrito
        carrito.setEstado(EstadoCarrito.PAGADO);
        carritoRepository.save(carrito);
        
        return ventaRepository.save(venta);
    }
    
    public long contarVentasCompletadas() {
        return ventaRepository.countByEstado(EstadoVenta.COMPLETADA);
    }
    
    public double calcularIngresosTotales() {
        List<Venta> ventasCompletadas = ventaRepository.findByEstado(EstadoVenta.COMPLETADA);
        return ventasCompletadas.stream()
                .mapToDouble(Venta::getTotal)
                .sum();
    }
    
}