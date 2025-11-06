package service;

/**
 *
 * @author darry
 */

import model.CarritoCompras;
import model.Usuario;
import model.EstadoCarrito;
import model.ItemCarrito;
import model.Producto;
import model.Cita;
import model.TipoItem;
import repository.CarritoComprasRepository;
import repository.ItemCarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {
    
    @Autowired
    private CarritoComprasRepository carritoRepository;
    
    @Autowired
    private ItemCarritoRepository itemCarritoRepository;
    
    public CarritoCompras obtenerCarritoActivo(Usuario usuario) {
        Optional<CarritoCompras> carritoOpt = carritoRepository.findByUsuarioAndEstado(usuario, EstadoCarrito.ACTIVO);
        return carritoOpt.orElseGet(() -> crearNuevoCarrito(usuario));
    }
    
    private CarritoCompras crearNuevoCarrito(Usuario usuario) {
        CarritoCompras carrito = new CarritoCompras();
        carrito.setUsuario(usuario);
        return carritoRepository.save(carrito);
    }
    
    public ItemCarrito agregarProductoAlCarrito(Usuario usuario, Producto producto, int cantidad) {
        CarritoCompras carrito = obtenerCarritoActivo(usuario);
        
        Optional<ItemCarrito> itemExistente = itemCarritoRepository.findByCarritoAndProducto(carrito, producto);
        
        if (itemExistente.isPresent()) {
            ItemCarrito item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            return itemCarritoRepository.save(item);
        } else {
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setTipo(TipoItem.PRODUCTO);
            nuevoItem.setPrecioUnitario(producto.getPrecio());
            return itemCarritoRepository.save(nuevoItem);
        }
    }
    
    public ItemCarrito agregarCitaAlCarrito(Usuario usuario, Cita cita) {
        CarritoCompras carrito = obtenerCarritoActivo(usuario);
        
        Optional<ItemCarrito> itemExistente = itemCarritoRepository.findByCarritoAndCita(carrito, cita);
        
        if (itemExistente.isPresent()) {
            return itemExistente.get(); // Ya existe en el carrito
        } else {
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setCita(cita);
            nuevoItem.setCantidad(1);
            nuevoItem.setTipo(TipoItem.CITA);
            nuevoItem.setPrecioUnitario(cita.getTratamiento().getPrecio());
            return itemCarritoRepository.save(nuevoItem);
        }
    }
    
    public List<ItemCarrito> obtenerItemsDelCarrito(Usuario usuario) {
        CarritoCompras carrito = obtenerCarritoActivo(usuario);
        return itemCarritoRepository.findByCarrito(carrito);
    }
    
    public void eliminarProductoDelCarrito(Usuario usuario, Producto producto) {
        CarritoCompras carrito = obtenerCarritoActivo(usuario);
        itemCarritoRepository.deleteByCarritoAndProducto(carrito, producto);
    }
    
    public void eliminarCitaDelCarrito(Usuario usuario, Cita cita) {
        CarritoCompras carrito = obtenerCarritoActivo(usuario);
        itemCarritoRepository.deleteByCarritoAndCita(carrito, cita);
    }
    
    public void vaciarCarrito(Usuario usuario) {
        CarritoCompras carrito = obtenerCarritoActivo(usuario);
        List<ItemCarrito> items = itemCarritoRepository.findByCarrito(carrito);
        itemCarritoRepository.deleteAll(items);
    }
    
    public double calcularTotalCarrito(Usuario usuario) {
        List<ItemCarrito> items = obtenerItemsDelCarrito(usuario);
        return items.stream()
                .mapToDouble(item -> item.getPrecioUnitario() * item.getCantidad())
                .sum();
    }
}