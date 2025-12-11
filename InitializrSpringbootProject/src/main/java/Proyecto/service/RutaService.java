package Proyecto.service;

/**
 *
 * @author darry
 */

import Proyecto.model.Ruta;
import Proyecto.repository.RutaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RutaService {

    @Autowired
    private RutaRepository rutaRepository;

    public List<Ruta> getRutas() {
        return rutaRepository.findAllByOrderByRequiereRolAsc();
    }

    public Ruta guardar(Ruta ruta) {
        return rutaRepository.save(ruta);
    }
    
    public List<Ruta> obtenerTodas() {
        return rutaRepository.findAll();
    }
    
    public void eliminar(Long id) {
        rutaRepository.deleteById(id);
    }
}
