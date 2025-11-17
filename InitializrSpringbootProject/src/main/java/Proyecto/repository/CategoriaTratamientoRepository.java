package Proyecto.repository;

/**
 *
 * @author darry
 */

import Proyecto.model.CategoriaTratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaTratamientoRepository extends JpaRepository<CategoriaTratamiento, Long> {
    
    Optional<CategoriaTratamiento> findByNombre(String nombre);
    List<CategoriaTratamiento> findByActivoTrue();
    boolean existsByNombre(String nombre);
    List<CategoriaTratamiento> findByNombreContaining(String nombre);
}
