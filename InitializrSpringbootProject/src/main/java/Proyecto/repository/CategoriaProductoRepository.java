package Proyecto.repository;

/**
 *
 * @author darry
 */


import Proyecto.model.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Long> {
    
    // Buscar categoría por nombre
    Optional<CategoriaProducto> findByNombre(String nombre);
    
    // Buscar categorías activas
    List<CategoriaProducto> findByActivoTrue();
    
    // Verificar si existe categoría por nombre
    boolean existsByNombre(String nombre);
    
    // Buscar categorías por nombre (búsqueda parcial)
    List<CategoriaProducto> findByNombreContaining(String nombre);
}