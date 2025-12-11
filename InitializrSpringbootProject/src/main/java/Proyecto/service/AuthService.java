package Proyecto.service;

/**
 *
 * @author darry
 */


import Proyecto.model.Rol;
import Proyecto.model.Usuario;
import Proyecto.repository.RolRepository;
import Proyecto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private RolRepository rolRepository;

    public boolean autenticar(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            return passwordEncoder.matches(password, usuario.getPassword()) && usuario.getActivo();
        }
        return false;
    }

    public Usuario registrarCliente(Usuario usuario) {
        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setActivo(true);
        
        // Asignar rol de cliente
        Rol clienteRole = rolRepository.findByNombre("ROLE_CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));
        usuario.setRoles(Set.of(clienteRole));
        
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> obtenerUsuarioAutenticado(String email) {
        return usuarioRepository.findByEmail(email);
    }
}
