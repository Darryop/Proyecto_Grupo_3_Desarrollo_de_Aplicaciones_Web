package service;

/**
 *
 * @author darry
 */

import model.Usuario;
import model.TipoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioService usuarioService;
    
    public boolean autenticar(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioService.obtenerPorEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            return usuario.getPassword().equals(password) && usuario.getActivo();
        }
        return false;
    }
    
    public Usuario registrarCliente(Usuario usuario) {
        usuario.setTipo(TipoUsuario.CLIENTE);
        usuario.setActivo(true);
        return usuarioService.guardar(usuario);
    }
    
    public Optional<Usuario> obtenerUsuarioAutenticado(String email) {
        return usuarioService.obtenerPorEmail(email);
    }
}
