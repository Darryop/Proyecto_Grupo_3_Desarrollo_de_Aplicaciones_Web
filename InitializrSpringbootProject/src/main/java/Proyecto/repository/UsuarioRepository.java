package Proyecto.repository;

/**
 *
 * @author darry
 */

import Proyecto.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailAndActivoTrue(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByActivoTrue();
    List<Usuario> findByNombreContainingOrApellidoContaining(String nombre, String apellido);
    
    // NUEVO MÉTODO: Cargar usuarios con sus roles
    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles")
    List<Usuario> findAllWithRoles();
    
    // Método para buscar por rol
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.roles r WHERE r.nombre = :rolNombre")
    List<Usuario> findByRoles_Nombre(String rolNombre);
}