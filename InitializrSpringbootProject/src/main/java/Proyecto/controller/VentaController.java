package Proyecto.controller;

/**
 *
 * @author darry
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Proyecto.model.MetodoPago;
import Proyecto.model.Usuario;
import Proyecto.service.CarritoService;
import Proyecto.service.UsuarioService;
import Proyecto.service.VentaService;

@Controller
@RequestMapping("/ventas")
public class VentaController {
    
    @Autowired
    private VentaService ventaService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private CarritoService carritoService;
    
    // TEMPORAL: Para pruebas
    private Usuario obtenerUsuarioTemporal() {
        return usuarioService.obtenerPorId(1L).orElse(null);
    }
    
    // Página de confirmación de pago (GET)
    @GetMapping("/confirmacion")
    public String mostrarConfirmacion(Model model) {
        Usuario usuario = obtenerUsuarioTemporal();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        // Obtener información del carrito
        var items = carritoService.obtenerItemsDelCarrito(usuario);
        double total = carritoService.calcularTotalCarrito(usuario);
        
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("metodosPago", MetodoPago.values()); // Para mostrar en un select
        
        return "ventas/confirmacion"; // Crear esta vista
    }
    
    // Procesar la venta (POST)
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