package repository;

/**
 *
 * @author darry
 */

import model.Cita;
import model.Usuario;
import model.Tratamiento;
import model.Cita.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    
    List<Cita> findByUsuario(Usuario usuario);
    List<Cita> findByTratamiento(Tratamiento tratamiento);
    List<Cita> findByEstado(EstadoCita estado);
    List<Cita> findByFechaCitaBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Cita> findByUsuarioAndEstado(Usuario usuario, EstadoCita estado);
    List<Cita> findByFechaCita(LocalDateTime fechaCita);
    long countByFechaCitaBetweenAndEstado(LocalDateTime inicio, LocalDateTime fin, EstadoCita estado);
}