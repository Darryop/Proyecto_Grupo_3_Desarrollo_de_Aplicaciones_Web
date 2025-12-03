package Proyecto.service;

/**
 *
 * @author darry
 */

import Proyecto.model.CarritoCompras;
import Proyecto.model.Usuario;
import Proyecto.model.EstadoCarrito;
import Proyecto.model.ItemCarrito;
import Proyecto.model.Producto;
import Proyecto.model.Cita;
import Proyecto.model.TipoItem;
import Proyecto.repository.CarritoComprasRepository;
import Proyecto.repository.ItemCarritoRepository;
import jakarta.transaction.Transactional;
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
    
    // NUEVO: Actualizar cantidad por itemId
    @Transactional
    public void actualizarCantidadItem(Long itemId, int nuevaCantidad) {
        Optional<ItemCarrito> itemOpt = itemCarritoRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            ItemCarrito item = itemOpt.get();
            if (nuevaCantidad > 0) {
                item.setCantidad(nuevaCantidad);
                itemCarritoRepository.save(item);
            } else {
                // Si la cantidad es 0 o menor, eliminar el item
                itemCarritoRepository.delete(item);
            }
        } else {
            throw new RuntimeException("Item no encontrado en el carrito con ID: " + itemId);
        }
    }
    
    // NUEVO: Eliminar item por itemId
    @Transactional
    public void eliminarItem(Long itemId) {
        Optional<ItemCarrito> itemOpt = itemCarritoRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            itemCarritoRepository.delete(itemOpt.get());
        } else {
            throw new RuntimeException("Item no encontrado en el carrito con ID: " + itemId);
        }
    }
    
    // MÉTODO EXISTENTE: Actualizar cantidad por producto y usuario
    @Transactional
    public void actualizarCantidadProducto(Usuario usuario, Producto producto, int nuevaCantidad) {
        CarritoCompras carrito = obtenerCarritoActivo(usuario);
        
        Optional<ItemCarrito> itemExistente = itemCarritoRepository.findByCarritoAndProducto(carrito, producto);
        
        if (itemExistente.isPresent()) {
            ItemCarrito item = itemExistente.get();
            if (nuevaCantidad > 0) {
                item.setCantidad(nuevaCantidad);
                itemCarritoRepository.save(item);
            } else {
                // Si la cantidad es 0 o menor, eliminar el producto
                itemCarritoRepository.delete(item);
            }
        }
    }
    
    @Transactional
    public void eliminarProductoDelCarrito(Usuario usuario, Producto producto) {
        CarritoCompras carrito = obtenerCarritoActivo(usuario);
        Optional<ItemCarrito> itemOpt = itemCarritoRepository.findByCarritoAndProducto(carrito, producto);
        if (itemOpt.isPresent()) {
            itemCarritoRepository.delete(itemOpt.get());
        }
    }
    
    @Transactional
    public void eliminarCitaDelCarrito(Usuario usuario, Cita cita) {
        CarritoCompras carrito = obtenerCarritoActivo(usuario);
        Optional<ItemCarrito> itemOpt = itemCarritoRepository.findByCarritoAndCita(carrito, cita);
        if (itemOpt.isPresent()) {
            itemCarritoRepository.delete(itemOpt.get());
        }
    }
    
    @Transactional
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