package Proyecto.repository;

/**
 *
 * @author darry
 */


import Proyecto.model.ItemCarrito;
import Proyecto.model.CarritoCompras;
import Proyecto.model.Producto;
import Proyecto.model.Cita;
import Proyecto.model.TipoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    
    List<ItemCarrito> findByCarrito(CarritoCompras carrito);
    List<ItemCarrito> findByCarritoAndTipo(CarritoCompras carrito, TipoItem tipo);
    Optional<ItemCarrito> findByCarritoAndProducto(CarritoCompras carrito, Producto producto);
    Optional<ItemCarrito> findByCarritoAndCita(CarritoCompras carrito, Cita cita);
    void deleteByCarritoAndProducto(CarritoCompras carrito, Producto producto);
    void deleteByCarritoAndCita(CarritoCompras carrito, Cita cita);
}
