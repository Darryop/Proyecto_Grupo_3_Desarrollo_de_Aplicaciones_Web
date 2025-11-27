package Proyecto.repository;

/**
 *
 * @author darry
 */


import Proyecto.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RutaRepository extends JpaRepository<Ruta, Long> {
    List<Ruta> findAllByOrderByRequiereRolAsc();
}
