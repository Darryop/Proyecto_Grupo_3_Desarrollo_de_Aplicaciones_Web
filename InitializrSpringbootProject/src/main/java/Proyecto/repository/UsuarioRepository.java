package Proyecto.repository;

/**
 *
 * @author darry
 */

import Proyecto.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailAndActivoTrue(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByActivoTrue();
    List<Usuario> findByNombreContainingOrApellidoContaining(String nombre, String apellido);
    
    // Nuevo método para buscar usuarios por rol
    List<Usuario> findByRoles_Nombre(String rolNombre);
}