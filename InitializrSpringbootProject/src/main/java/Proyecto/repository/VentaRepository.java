package Proyecto.repository;

/**
 *
 * @author darry
 */
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import Proyecto.model.EstadoVenta;
import Proyecto.model.MetodoPago;
import Proyecto.model.Usuario;
import Proyecto.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    List<Venta> findByUsuario(Usuario usuario);
    List<Venta> findByEstado(EstadoVenta estado);
    List<Venta> findByMetodoPago(MetodoPago metodoPago);
    List<Venta> findByFechaVentaBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Venta> findByUsuarioAndEstado(Usuario usuario, EstadoVenta estado);
    long countByEstado(EstadoVenta estado);
    
    // Método para obtener todas las ventas con relaciones cargadas
    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.usuario LEFT JOIN FETCH v.carrito")
    List<Venta> findAllWithRelations();
}
