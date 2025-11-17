package Proyecto.repository;

/**
 *
 * @author darry
 */

import Proyecto.model.Resena;
import Proyecto.model.Usuario;
import Proyecto.model.Producto;
import Proyecto.model.Tratamiento;
import Proyecto.model.TipoResena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    
    List<Resena> findByUsuario(Usuario usuario);
    List<Resena> findByProducto(Producto producto);
    List<Resena> findByTratamiento(Tratamiento tratamiento);
    List<Resena> findByTipo(TipoResena tipo);
    List<Resena> findByAprobadaTrue();
    List<Resena> findByCalificacionGreaterThanEqual(Integer calificacion);
    List<Resena> findByProductoAndAprobadaTrue(Producto producto);
    List<Resena> findByTratamientoAndAprobadaTrue(Tratamiento tratamiento);
}
