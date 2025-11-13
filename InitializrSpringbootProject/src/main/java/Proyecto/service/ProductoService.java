package Proyecto.service;

/**
 *
 * @author darry
 */

import Proyecto.model.Producto;
import Proyecto.model.CategoriaProducto;
import Proyecto.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }
    
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }
    
    public Optional<Producto> obtenerPorCodigo(String codigo) {
        return productoRepository.findByCodigoProducto(codigo);
    }
    
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }
    
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
    
    public List<Producto> obtenerActivos() {
        return productoRepository.findByActivoTrue();
    }
    
    public List<Producto> obtenerPorCategoria(CategoriaProducto categoria) {
        return productoRepository.findByCategoriaAndActivoTrue(categoria);
    }
    
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContaining(nombre);
    }
    
    public List<Producto> obtenerConStock() {
        return productoRepository.findByStockGreaterThan(0);
    }
    
    public boolean existeCodigo(String codigo) {
        return productoRepository.existsByCodigoProducto(codigo);
    }
}
