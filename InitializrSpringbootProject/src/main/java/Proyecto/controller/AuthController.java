package Proyecto.controller;

/**
 *
 * @author darry
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Proyecto.model.Usuario;
import Proyecto.service.AuthService;
import Proyecto.service.UsuarioService;

import java.util.Optional;

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