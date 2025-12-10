package Proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Proyecto.model.Usuario;
import Proyecto.service.AuthService;
import Proyecto.service.UsuarioService;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }
    
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }
    
    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario,
                           RedirectAttributes redirectAttributes) {
        try {
            // Verificar si el email ya existe
            if (usuarioService.existeEmail(usuario.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "El email ya está registrado");
                return "redirect:/auth/registro";
            }
            
            // Establecer el tipo de usuario como CLIENTE
            usuario.setTipo(Usuario.TipoUsuario.CLIENTE);
            
            // Establecer el usuario como activo
            usuario.setActivo(true);
            
            // Registrar el usuario
            authService.registrarCliente(usuario);
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "¡Registro exitoso! Ahora puedes iniciar sesión");
            return "redirect:/auth/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error en el registro: " + e.getMessage());
            return "redirect:/auth/registro";
        }
    }
    
    // Spring Security maneja automáticamente el logout con /logout
}