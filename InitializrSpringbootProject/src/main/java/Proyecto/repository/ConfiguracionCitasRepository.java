package Proyecto.repository;

/**
 *
 * @author darry
 */

import Proyecto.model.ConfiguracionCitas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionCitasRepository extends JpaRepository<ConfiguracionCitas, Long> {
    // Solo necesitamos una configuración, así que usaremos el ID 1 por defecto
}
