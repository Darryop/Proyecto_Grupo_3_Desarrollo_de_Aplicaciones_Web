package Proyecto.service;

/**
 *
 * @author darry
 */
import Proyecto.model.Usuario;
import Proyecto.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service("userDetailsService")
public class UsuarioDetailsService implements UserDetailsService {
    
    @Autowired
    private PasswordEncoder passwordEncoder; // Añade esto

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired(required = false)
    private HttpSession session;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        // DEBUG: Ver toda la información
        System.out.println("=== DEBUG UserDetailsService ===");
        System.out.println("Email buscado: " + email);
        System.out.println("Usuario encontrado: " + usuario.getEmail());
        System.out.println("Password en DB: " + usuario.getPassword());
        System.out.println("Tipo de usuario (tipo): " + usuario.getRoles());
        System.out.println("Activo: " + usuario.getActivo());
        
        // Verificar si tiene roles
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            System.out.println("⚠️ ADVERTENCIA: El usuario NO TIENE ROLES asignados");
            // Verificar en la base de datos directamente
            System.out.println("Comprobando en la base de datos...");
        } else {
            System.out.println("Roles encontrados: " + usuario.getRoles().size());
            usuario.getRoles().forEach(rol -> 
                System.out.println("  - Rol: " + rol.getNombre()));
        }
        
        // Probar la contraseña
        System.out.println("=== PRUEBA DE CONTRASEÑA ===");
        System.out.println("Password ingresado (debería ser '1234'): [no se muestra por seguridad]");
        System.out.println("PasswordEncoder: " + passwordEncoder.getClass().getName());
        
        List<GrantedAuthority> authorities = usuario.getRoles().stream()
            .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
            .collect(Collectors.toList());
        
        // Si no tiene roles pero tiene tipo, asignar rol basado en tipo
        if (authorities.isEmpty() && usuario.getRoles()!= null) {
            System.out.println("⚠️ Usando tipo de usuario para asignar rol...");
            if ("ADMIN".equals(usuario.getRoles())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            } else if ("CLIENTE".equals(usuario.getRoles())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_CLIENTE"));
            }
            System.out.println("Authorities asignadas desde tipo: " + authorities);
        }
        
        return new org.springframework.security.core.userdetails.User(
            usuario.getEmail(),
            usuario.getPassword(),
            authorities
        );
    }
}
