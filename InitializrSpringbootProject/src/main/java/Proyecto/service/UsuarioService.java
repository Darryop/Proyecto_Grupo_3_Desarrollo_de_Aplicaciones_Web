package Proyecto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Proyecto.model.Usuario;
import Proyecto.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos() {
        // Si usas FetchType.EAGER, los roles ya se cargan automáticamente
        return usuarioRepository.findAll();
    }
    
    // NUEVO MÉTODO para cargar con roles (CORREGIDO)
    public List<Usuario> obtenerTodosConRoles() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        // Forzar la inicialización de los roles si es Lazy (pero ya es EAGER)
        // Solo como precaución:
        usuarios.forEach(usuario -> {
            if (usuario.getRoles() != null) {
                usuario.getRoles().size(); // Esto fuerza la carga si fuera Lazy
            }
        });
        return usuarios;
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public List<Usuario> obtenerActivos() {
        return usuarioRepository.findByActivoTrue();
    }
}