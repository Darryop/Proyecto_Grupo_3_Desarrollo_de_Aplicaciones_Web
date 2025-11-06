package service;

/**
 *
 * @author darry
 */

import model.Resena;
import model.Usuario;
import model.Producto;
import model.Tratamiento;
import model.TipoResena;
import repository.ResenaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ResenaService {
    
    @Autowired
    private ResenaRepository resenaRepository;
    
    public List<Resena> obtenerTodas() {
        return resenaRepository.findAll();
    }
    
    public Optional<Resena> obtenerPorId(Long id) {
        return resenaRepository.findById(id);
    }
    
    public Resena guardar(Resena resena) {
        return resenaRepository.save(resena);
    }
    
    public void eliminar(Long id) {
        resenaRepository.deleteById(id);
    }
    
    public List<Resena> obtenerPorUsuario(Usuario usuario) {
        return resenaRepository.findByUsuario(usuario);
    }
    
    public List<Resena> obtenerPorProducto(Producto producto) {
        return resenaRepository.findByProducto(producto);
    }
    
    public List<Resena> obtenerPorTratamiento(Tratamiento tratamiento) {
        return resenaRepository.findByTratamiento(tratamiento);
    }
    
    public List<Resena> obtenerAprobadas() {
        return resenaRepository.findByAprobadaTrue();
    }
    
    public List<Resena> obtenerPorTipo(TipoResena tipo) {
        return resenaRepository.findByTipo(tipo);
    }
    
    public List<Resena> obtenerResenasDeAltaCalificacion(int calificacionMinima) {
        return resenaRepository.findByCalificacionGreaterThanEqual(calificacionMinima);
    }
    
    public List<Resena> obtenerResenasAprobadasPorProducto(Producto producto) {
        return resenaRepository.findByProductoAndAprobadaTrue(producto);
    }
    
    public List<Resena> obtenerResenasAprobadasPorTratamiento(Tratamiento tratamiento) {
        return resenaRepository.findByTratamientoAndAprobadaTrue(tratamiento);
    }
    
    public void aprobarResena(Long id) {
        Optional<Resena> resenaOpt = resenaRepository.findById(id);
        if (resenaOpt.isPresent()) {
            Resena resena = resenaOpt.get();
            resena.setAprobada(true);
            resenaRepository.save(resena);
        }
    }
    
    public double calcularPromedioCalificacionProducto(Producto producto) {
        List<Resena> resenas = resenaRepository.findByProductoAndAprobadaTrue(producto);
        if (resenas.isEmpty()) return 0.0;
        
        return resenas.stream()
                .mapToInt(Resena::getCalificacion)
                .average()
                .orElse(0.0);
    }
    
    public double calcularPromedioCalificacionTratamiento(Tratamiento tratamiento) {
        List<Resena> resenas = resenaRepository.findByTratamientoAndAprobadaTrue(tratamiento);
        if (resenas.isEmpty()) return 0.0;
        
        return resenas.stream()
                .mapToInt(Resena::getCalificacion)
                .average()
                .orElse(0.0);
    }
}