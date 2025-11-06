package service;

/**
 *
 * @author darry
 */

import model.Venta;
import model.Usuario;
import model.CarritoCompras;
import model.ItemCarrito;
import model.EstadoVenta;
import model.MetodoPago;
import model.EstadoCarrito;
import repository.VentaRepository;
import repository.CarritoComprasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VentaService {
    
    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private CarritoComprasRepository carritoRepository;
    
    @Autowired
    private CarritoService carritoService;
    
    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
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