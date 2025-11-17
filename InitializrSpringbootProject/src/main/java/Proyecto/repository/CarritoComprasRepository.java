package Proyecto.repository;

/**
 *
 * @author darry
 */


import Proyecto.model.CarritoCompras;
import Proyecto.model.Usuario;
import Proyecto.model.EstadoCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoComprasRepository extends JpaRepository<CarritoCompras, Long> {
    
    Optional<CarritoCompras> findByUsuarioAndEstado(Usuario usuario, EstadoCarrito estado);
    List<CarritoCompras> findByUsuario(Usuario usuario);
    List<CarritoCompras> findByEstado(EstadoCarrito estado);
    boolean existsByUsuarioAndEstado(Usuario usuario, EstadoCarrito estado);
}
