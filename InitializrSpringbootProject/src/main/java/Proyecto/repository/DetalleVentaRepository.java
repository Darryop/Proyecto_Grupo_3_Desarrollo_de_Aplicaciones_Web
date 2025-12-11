package Proyecto.repository;

import Proyecto.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByVentaId(Long ventaId);
    List<DetalleVenta> findByProductoId(Long productoId);
    List<DetalleVenta> findByTratamientoId(Long tratamientoId);
}