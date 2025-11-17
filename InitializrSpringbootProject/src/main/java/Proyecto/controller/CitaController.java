package Proyecto.controller;

/**
 *
 * @author darry
 */

import Proyecto.model.Cita;
import Proyecto.model.Tratamiento;
import Proyecto.model.Usuario;
import Proyecto.service.CitaService;
import Proyecto.service.TratamientoService;
import Proyecto.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/citas")
public class CitaController {
    
    @Autowired
    private CitaService citaService;
    
    @Autowired
    private TratamientoService tratamientoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    // TEMPORAL: Para pruebas
    private Usuario obtenerUsuarioTemporal() {
        Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(1L);
        return usuarioOpt.orElse(null);
    }
    
    @GetMapping
    public String listarCitas(Model model) {
        Usuario usuario = obtenerUsuarioTemporal();
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
        Usuario usuario = obtenerUsuarioTemporal();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        List<Tratamiento> tratamientos = tratamientoService.obtenerActivos();
        model.addAttribute("cita", new Cita());
        model.addAttribute("tratamientos", tratamientos);
        
        if (tratamientoId != null) {
            model.addAttribute("tratamientoSeleccionado", tratamientoId);
        }
        
        return "citas/formulario"; // Cambiado para coincidir con la estructura
    }
    
    @PostMapping("/nueva")
    public String crearCita(@ModelAttribute Cita cita,
                           @RequestParam Long tratamientoId,
                           @RequestParam String fechaCita,
                           RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioTemporal();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        try {
            Optional<Tratamiento> tratamientoOpt = tratamientoService.obtenerPorId(tratamientoId);
            if (tratamientoOpt.isPresent()) {
                cita.setUsuario(usuario);
                cita.setTratamiento(tratamientoOpt.get());
                cita.setFechaCita(LocalDateTime.parse(fechaCita));
                
                citaService.guardar(cita);
                redirectAttributes.addFlashAttribute("mensaje", "Cita agendada exitosamente");
                return "redirect:/citas";
            } else {
                redirectAttributes.addFlashAttribute("error", "Tratamiento no encontrado");
                return "redirect:/citas/nueva";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al agendar cita: " + e.getMessage());
            return "redirect:/citas/nueva";
        }
    }
    
    @PostMapping("/cancelar/{id}")
    public String cancelarCita(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Cita> citaOpt = citaService.obtenerPorId(id);
        if (citaOpt.isPresent()) {
            Cita cita = citaOpt.get();
            cita.setEstado(Cita.EstadoCita.CANCELADA);
            citaService.guardar(cita);
            redirectAttributes.addFlashAttribute("mensaje", "Cita cancelada");
        } else {
            redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
        }
        return "redirect:/citas";
    }
}
