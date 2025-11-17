package Proyecto.service;

/**
 *
 * @author darry
 */

import Proyecto.model.Tratamiento;
import Proyecto.model.CategoriaTratamiento;
import Proyecto.repository.TratamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TratamientoService {
    
    @Autowired
    private TratamientoRepository tratamientoRepository;
    
    public List<Tratamiento> obtenerTodos() {
        return tratamientoRepository.findAll();
    }
    
    public Optional<Tratamiento> obtenerPorId(Long id) {
        return tratamientoRepository.findById(id);
    }
    
    public Optional<Tratamiento> obtenerPorCodigo(String codigo) {
        return tratamientoRepository.findByCodigoTratamiento(codigo);
    }
    
    public Tratamiento guardar(Tratamiento tratamiento) {
        return tratamientoRepository.save(tratamiento);
    }
    
    public void eliminar(Long id) {
        tratamientoRepository.deleteById(id);
    }
    
    public List<Tratamiento> obtenerActivos() {
        return tratamientoRepository.findByActivoTrue();
    }
    
    public List<Tratamiento> obtenerPorCategoria(CategoriaTratamiento categoria) {
        return tratamientoRepository.findByCategoriaAndActivoTrue(categoria);
    }
    
    public List<Tratamiento> buscarPorNombre(String nombre) {
        return tratamientoRepository.findByNombreContaining(nombre);
    }
    
    public List<Tratamiento> obtenerPorRangoPrecio(Double min, Double max) {
        return tratamientoRepository.findByPrecioBetween(min, max);
    }
    
    public boolean existeCodigo(String codigo) {
        return tratamientoRepository.existsByCodigoTratamiento(codigo);
    }
}