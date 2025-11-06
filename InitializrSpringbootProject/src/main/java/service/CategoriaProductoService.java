package service;

/**
 *
 * @author darry
 */

import model.CategoriaProducto;
import repository.CategoriaProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaProductoService {
    
    @Autowired
    private CategoriaProductoRepository categoriaRepository;
    
    public List<CategoriaProducto> obtenerTodas() {
        return categoriaRepository.findAll();
    }
    
    public Optional<CategoriaProducto> obtenerPorId(Long id) {
        return categoriaRepository.findById(id);
    }
    
    public Optional<CategoriaProducto> obtenerPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre);
    }
    
    public CategoriaProducto guardar(CategoriaProducto categoria) {
        return categoriaRepository.save(categoria);
    }
    
    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }
    
    public List<CategoriaProducto> obtenerActivas() {
        return categoriaRepository.findByActivoTrue();
    }
    
    public boolean existeNombre(String nombre) {
        return categoriaRepository.existsByNombre(nombre);
    }
}
