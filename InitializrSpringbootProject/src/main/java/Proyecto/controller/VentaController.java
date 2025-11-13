package Proyecto.controller;

/**
 *
 * @author darry
 */

import Proyecto.model.Usuario;
import Proyecto.model.MetodoPago;
import Proyecto.service.VentaService;
import Proyecto.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventas")
public class VentaController {
    
    @Autowired
    private VentaService ventaService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    // TEMPORAL: Para pruebas
    private Usuario obtenerUsuarioTemporal() {
        return usuarioService.obtenerPorId(1L).orElse(null);
    }
    
    @PostMapping("/procesar")
    public String procesarVenta(@RequestParam MetodoPago metodoPago,
                               RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioTemporal();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        try {
            ventaService.procesarVenta(usuario, metodoPago);
            redirectAttributes.addFlashAttribute("mensaje", "¡Compra realizada exitosamente!");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la venta: " + e.getMessage());
            return "redirect:/carrito";
        }
    }
}
