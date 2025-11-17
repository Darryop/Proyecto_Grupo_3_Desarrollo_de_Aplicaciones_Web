package Proyecto.repository;

/**
 *
 * @author darry
 */

import Proyecto.model.Producto;
import Proyecto.model.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Buscar producto por código
    Optional<Producto> findByCodigoProducto(String codigoProducto);
    
    // Buscar productos por categoría
    List<Producto> findByCategoria(CategoriaProducto categoria);
    
    // Buscar productos activos
    List<Producto> findByActivoTrue();
    
    // Buscar productos con stock disponible
    List<Producto> findByStockGreaterThan(int stock);
    
    // Buscar productos por nombre (búsqueda parcial)
    List<Producto> findByNombreContaining(String nombre);
    
    // Buscar productos por categoría y activos
    List<Producto> findByCategoriaAndActivoTrue(CategoriaProducto categoria);
    
    // Buscar productos por rango de precio
    List<Producto> findByPrecioBetween(Double precioMin, Double precioMax);
    
    // Verificar si existe producto por código
    boolean existsByCodigoProducto(String codigoProducto);
}
