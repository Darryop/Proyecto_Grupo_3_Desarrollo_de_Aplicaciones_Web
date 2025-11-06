package repository;

/**
 *
 * @author darry
 */

import model.Tratamiento;
import model.CategoriaTratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {
    
    Optional<Tratamiento> findByCodigoTratamiento(String codigoTratamiento);
    List<Tratamiento> findByCategoria(CategoriaTratamiento categoria);
    List<Tratamiento> findByActivoTrue();
    List<Tratamiento> findByCategoriaAndActivoTrue(CategoriaTratamiento categoria);
    List<Tratamiento> findByNombreContaining(String nombre);
    List<Tratamiento> findByPrecioBetween(Double precioMin, Double precioMax);
    boolean existsByCodigoTratamiento(String codigoTratamiento);
}
