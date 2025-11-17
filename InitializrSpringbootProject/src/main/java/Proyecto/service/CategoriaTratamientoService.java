package Proyecto.service;

/**
 *
 * @author darry
 */

import Proyecto.model.CategoriaTratamiento;
import Proyecto.repository.CategoriaTratamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaTratamientoService {
    
    @Autowired
    private CategoriaTratamientoRepository categoriaRepository;
    
    public List<CategoriaTratamiento> obtenerTodas() {
        return categoriaRepository.findAll();
    }
    
    public Optional<CategoriaTratamiento> obtenerPorId(Long id) {
        return categoriaRepository.findById(id);
    }
    
    public Optional<CategoriaTratamiento> obtenerPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre);
    }
    
    public CategoriaTratamiento guardar(CategoriaTratamiento categoria) {
        return categoriaRepository.save(categoria);
    }
    
    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }
    
    public List<CategoriaTratamiento> obtenerActivas() {
        return categoriaRepository.findByActivoTrue();
    }
    
    public boolean existeNombre(String nombre) {
        return categoriaRepository.existsByNombre(nombre);
    }
}
