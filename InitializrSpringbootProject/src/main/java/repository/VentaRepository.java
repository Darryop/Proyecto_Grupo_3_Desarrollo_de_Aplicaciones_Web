package repository;

/**
 *
 * @author darry
 */
import model.Venta;
import model.Usuario;
import model.EstadoVenta;
import model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    List<Venta> findByUsuario(Usuario usuario);
    List<Venta> findByEstado(EstadoVenta estado);
    List<Venta> findByMetodoPago(MetodoPago metodoPago);
    List<Venta> findByFechaVentaBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Venta> findByUsuarioAndEstado(Usuario usuario, EstadoVenta estado);
    long countByEstado(EstadoVenta estado);
}
