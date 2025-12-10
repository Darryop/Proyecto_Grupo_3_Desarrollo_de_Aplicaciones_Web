package Proyecto.service;

/**
 *
 * @author darry
 */

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Proyecto.model.CarritoCompras;
import Proyecto.model.Cita;
import Proyecto.model.EstadoCarrito;
import Proyecto.model.ItemCarrito;
import Proyecto.model.Producto;
import Proyecto.model.TipoItem;
import Proyecto.model.Usuario;
import Proyecto.repository.CarritoComprasRepository;
import Proyecto.repository.ItemCarritoRepository;

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
        
        // Verificar si la cita ya está en el carrito
        Optional<ItemCarrito> itemExistente = itemCarritoRepository.findByCarritoAndCita(carrito, cita);
        
        if (itemExistente.isPresent()) {
            // Si ya está, no hacemos nada (o podrías lanzar una excepción, según tu lógica de negocio)
            // Como es una cita, normalmente no se agregaría dos veces, pero depende de tu sistema.
            // En este caso, devolvemos el item existente.
            return itemExistente.get();
        } else {
            ItemCarrito item = new ItemCarrito();
            item.setCarrito(carrito);
            item.setCita(cita);
            item.setTipo(TipoItem.CITA);
            item.setCantidad(1);
            item.setPrecioUnitario(cita.getTratamiento().getPrecio());
            
            return itemCarritoRepository.save(item);
        }
    }
    
    // En CarritoService.java
    public void eliminarItemDelCarrito(Usuario usuario, Long itemId) {
        CarritoCompras carrito = obtenerCarritoActivo(usuario);
        Optional<ItemCarrito> itemOpt = itemCarritoRepository.findById(itemId);

        if (itemOpt.isPresent() && itemOpt.get().getCarrito().getId().equals(carrito.getId())) {
            itemCarritoRepository.delete(itemOpt.get());
        } else {
            throw new RuntimeException("Ítem no encontrado en el carrito del usuario");
        }
    }
    
    public void actualizarCantidadItem(Usuario usuario, Long itemId, int cantidad) {
        CarritoCompras carrito = obtenerCarritoActivo(usuario);
        Optional<ItemCarrito> itemOpt = itemCarritoRepository.findById(itemId);

        if (itemOpt.isPresent() && itemOpt.get().getCarrito().getId().equals(carrito.getId())) {
            ItemCarrito item = itemOpt.get();

            // Validar que sea un producto (las citas no tienen cantidad ajustable)
            if (item.getTipo() == TipoItem.PRODUCTO && item.getProducto() != null) {
                // Validar que la cantidad no sea menor a 1
                if (cantidad < 1) {
                    throw new RuntimeException("La cantidad no puede ser menor a 1");
                }

                // Validar stock disponible
                if (cantidad > item.getProducto().getStock()) {
                    throw new RuntimeException("Stock insuficiente. Disponible: " + item.getProducto().getStock());
                }

                item.setCantidad(cantidad);
                itemCarritoRepository.save(item);
            } else {
                throw new RuntimeException("Solo se puede ajustar la cantidad de productos");
            }
        } else {
            throw new RuntimeException("Ítem no encontrado en el carrito del usuario");
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