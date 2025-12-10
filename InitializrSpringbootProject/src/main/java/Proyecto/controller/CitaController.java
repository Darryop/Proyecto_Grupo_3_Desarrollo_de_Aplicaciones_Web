package Proyecto.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Proyecto.model.Cita;
import Proyecto.model.Tratamiento;
import Proyecto.model.Usuario;
import Proyecto.service.CarritoService;
import Proyecto.service.CitaService;
import Proyecto.service.TratamientoService;
import Proyecto.service.UsuarioService;

@Controller
@RequestMapping("/citas")
public class CitaController {
    
    @Autowired
    private CitaService citaService;
    
    @Autowired
    private TratamientoService tratamientoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private CarritoService carritoService;
    
    // Método para obtener el usuario actual autenticado
    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        
        try {
            // Si tu UserDetails implementa una clase personalizada
            String username = authentication.getName();
            Optional<Usuario> usuarioOpt = usuarioService.obtenerPorEmail(username);
            return usuarioOpt.orElse(null);
            
        } catch (Exception e) {
            return null;
        }
    }
    
    @GetMapping
    public String listarCitas(Model model) {
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        List<Cita> citas = citaService.obtenerPorUsuario(usuario);
        model.addAttribute("citas", citas);
        return "citas/lista";
    }
    
    @GetMapping("/nueva")
    public String mostrarFormularioCita(
            @RequestParam(required = false) Long tratamientoId,
            Model model) {
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        List<Tratamiento> tratamientos = tratamientoService.obtenerActivos();
        model.addAttribute("cita", new Cita());
        model.addAttribute("tratamientos", tratamientos);
        model.addAttribute("usuarioActual", usuario); // Agregar usuario al modelo
        
        if (tratamientoId != null) {
            model.addAttribute("tratamientoSeleccionado", tratamientoId);
        }
        
        return "citas/formulario";
    }
    
    @PostMapping("/nueva")
    public String crearCita(@ModelAttribute Cita cita,
                           @RequestParam Long tratamientoId,
                           @RequestParam String fechaCita,
                           RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        try {
            Optional<Tratamiento> tratamientoOpt = tratamientoService.obtenerPorId(tratamientoId);
            if (tratamientoOpt.isPresent()) {
                cita.setUsuario(usuario); // Esto establecerá el usuario autenticado
                cita.setTratamiento(tratamientoOpt.get());
                cita.setFechaCita(LocalDateTime.parse(fechaCita));
                cita.setEstado(Cita.EstadoCita.PENDIENTE); // Asegurar estado inicial
                
                // Guardar la cita
                citaService.guardar(cita);
                
                // Agregar la cita al carrito
                carritoService.agregarCitaAlCarrito(usuario, cita);
                
                redirectAttributes.addFlashAttribute("mensaje", "Cita agendada y agregada al carrito exitosamente");
                return "redirect:/citas";
            } else {
                redirectAttributes.addFlashAttribute("error", "Tratamiento no encontrado");
                return "redirect:/citas/nueva";
            }
        } catch (Exception e) {
            e.printStackTrace(); // Para debugging
            redirectAttributes.addFlashAttribute("error", "Error al agendar cita: " + e.getMessage());
            return "redirect:/citas/nueva";
        }
    }
    
    @PostMapping("/cancelar/{id}")
    public String cancelarCita(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Verificar que la cita pertenece al usuario actual
        Usuario usuario = obtenerUsuarioActual();
        Optional<Cita> citaOpt = citaService.obtenerPorId(id);
        
        if (citaOpt.isPresent()) {
            Cita cita = citaOpt.get();
            
            // Verificar que el usuario es el dueño de la cita
            if (!cita.getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para cancelar esta cita");
                return "redirect:/citas";
            }
            
            cita.setEstado(Cita.EstadoCita.CANCELADA);
            citaService.guardar(cita);
            redirectAttributes.addFlashAttribute("mensaje", "Cita cancelada");
        } else {
            redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
        }
        return "redirect:/citas";
    }
}