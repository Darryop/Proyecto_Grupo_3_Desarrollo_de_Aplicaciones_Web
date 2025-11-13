package Proyecto.service;

/**
 *
 * @author darry
 */

import Proyecto.model.Cita;
import Proyecto.model.Usuario;
import Proyecto.model.Tratamiento;
import Proyecto.model.Cita.EstadoCita;
import Proyecto.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class CitaService {
    
    @Autowired
    private CitaRepository citaRepository;
    
    public List<Cita> obtenerTodas() {
        return citaRepository.findAll();
    }
    
    public Optional<Cita> obtenerPorId(Long id) {
        return citaRepository.findById(id);
    }
    
    public Cita guardar(Cita cita) {
        return citaRepository.save(cita);
    }
    
    public void eliminar(Long id) {
        citaRepository.deleteById(id);
    }
    
    public List<Cita> obtenerPorUsuario(Usuario usuario) {
        return citaRepository.findByUsuario(usuario);
    }
    
    public List<Cita> obtenerPorEstado(EstadoCita estado) {
        return citaRepository.findByEstado(estado);
    }
    
    public List<Cita> obtenerPorUsuarioYEstado(Usuario usuario, EstadoCita estado) {
        return citaRepository.findByUsuarioAndEstado(usuario, estado);
    }
    
    public List<Cita> obtenerCitasEntreFechas(LocalDateTime inicio, LocalDateTime fin) {
        return citaRepository.findByFechaCitaBetween(inicio, fin);
    }
    
    public long contarCitasPorFechaYEstado(LocalDateTime inicio, LocalDateTime fin, EstadoCita estado) {
        return citaRepository.countByFechaCitaBetweenAndEstado(inicio, fin, estado);
    }
    
    public boolean existeCitaEnFecha(LocalDateTime fecha) {
        List<Cita> citas = citaRepository.findByFechaCita(fecha);
        return !citas.isEmpty();
    }
}