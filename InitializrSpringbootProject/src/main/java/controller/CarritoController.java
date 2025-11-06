package controller;

/**
 *
 * @author darry
 */

import model.Usuario;
import model.Producto;
import model.Cita;
import service.CarritoService;
import service.ProductoService;
import service.CitaService;
import service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/carrito")
public class CarritoController {
    
    @Autowired
    private CarritoService carritoService;
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private CitaService citaService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    // TEMPORAL: Para pruebas, usaremos un usuario por defecto
    private Usuario obtenerUsuarioTemporal() {
        // En una app real, esto vendría de la sesión
        Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(1L);
        return usuarioOpt.orElse(null);
    }
    
    @GetMapping
    public String verCarrito(Model model) {
        Usuario usuario = obtenerUsuarioTemporal();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("items", carritoService.obtenerItemsDelCarrito(usuario));
        model.addAttribute("total", carritoService.calcularTotalCarrito(usuario));
        return "carrito/ver";
    }
    
    @PostMapping("/agregar/producto/{productoId}")
    public String agregarProducto(@PathVariable Long productoId,
                                 @RequestParam(defaultValue = "1") int cantidad,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioTemporal();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Optional<Producto> productoOpt = productoService.obtenerPorId(productoId);
        if (productoOpt.isPresent()) {
            carritoService.agregarProductoAlCarrito(usuario, productoOpt.get(), cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Producto agregado al carrito");
        } else {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
        }
        
        return "redirect:/productos";
    }
    
    @PostMapping("/agregar/cita/{citaId}")
    public String agregarCita(@PathVariable Long citaId,
                             RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioTemporal();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Optional<Cita> citaOpt = citaService.obtenerPorId(citaId);
        if (citaOpt.isPresent()) {
            carritoService.agregarCitaAlCarrito(usuario, citaOpt.get());
            redirectAttributes.addFlashAttribute("mensaje", "Cita agregada al carrito");
        } else {
            redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
        }
        
        return "redirect:/citas";
    }
    
    @PostMapping("/eliminar/producto/{productoId}")
    public String eliminarProducto(@PathVariable Long productoId,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioTemporal();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Optional<Producto> productoOpt = productoService.obtenerPorId(productoId);
        if (productoOpt.isPresent()) {
            carritoService.eliminarProductoDelCarrito(usuario, productoOpt.get());
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado del carrito");
        }
        
        return "redirect:/carrito";
    }
    
    @PostMapping("/vaciar")
    public String vaciarCarrito(RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioTemporal();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        carritoService.vaciarCarrito(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "Carrito vaciado");
        return "redirect:/carrito";
    }
}
