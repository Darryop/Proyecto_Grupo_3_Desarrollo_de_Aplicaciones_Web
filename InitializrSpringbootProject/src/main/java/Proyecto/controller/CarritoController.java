package Proyecto.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Proyecto.model.Cita;
import Proyecto.model.ItemCarrito;
import Proyecto.model.Producto;
import Proyecto.model.Usuario;
import Proyecto.service.CarritoService;
import Proyecto.service.CitaService;
import Proyecto.service.ProductoService;
import Proyecto.service.UsuarioService;

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

    // Método para obtener el usuario actual autenticado
    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        
        try {
            String username = authentication.getName();
            Optional<Usuario> usuarioOpt = usuarioService.obtenerPorEmail(username);
            return usuarioOpt.orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping
    public String verCarrito(Model model) {
        Usuario usuario = obtenerUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        List<ItemCarrito> items = carritoService.obtenerItemsDelCarrito(usuario);
        double total = carritoService.calcularTotalCarrito(usuario);
        int carritoCount = items.size();

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("carritoCount", carritoCount);

        return "carrito/ver";
    }
    
    @PostMapping("/actualizar-cantidad/{itemId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarCantidad(@PathVariable Long itemId,
                                                                 @RequestParam int cantidad,
                                                                 RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioAutenticado();
        Map<String, Object> response = new HashMap<>();

        if (usuario == null) {
            response.put("success", false);
            response.put("message", "Usuario no autenticado");
            return ResponseEntity.status(401).body(response);
        }

        try {
            carritoService.actualizarCantidadItem(usuario, itemId, cantidad);

            // Calcular nuevo total
            double nuevoTotal = carritoService.calcularTotalCarrito(usuario);

            response.put("success", true);
            response.put("message", "Cantidad actualizada");
            response.put("nuevoTotal", nuevoTotal);
            response.put("nuevaCantidad", cantidad);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/eliminar/{itemId}")
    public String eliminarItemDelCarrito(@PathVariable Long itemId,
                                        RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        try {
            // Usar el nuevo método del servicio
            carritoService.eliminarItemDelCarrito(usuario, itemId);
            redirectAttributes.addFlashAttribute("mensaje", "Ítem eliminado del carrito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el ítem: " + e.getMessage());
        }
        return "redirect:/carrito";
    }

    @GetMapping("/count")
    @ResponseBody
    public int obtenerContadorCarrito() {
        Usuario usuario = obtenerUsuarioAutenticado();
        if (usuario == null) {
            return 0;
        }
        List<ItemCarrito> items = carritoService.obtenerItemsDelCarrito(usuario);
        return items.size();
    }

    @PostMapping("/agregar/producto/{productoId}")
    public String agregarProducto(@PathVariable Long productoId,
                                  @RequestParam(defaultValue = "1") int cantidad,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioAutenticado();
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
        Usuario usuario = obtenerUsuarioAutenticado();
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

    @PutMapping("/item/{itemId}/cantidad")
    @ResponseBody
    public ResponseEntity<String> actualizarCantidad(@PathVariable Long itemId,
                                                     @RequestParam int change) {
        // Este método requiere una implementación en el servicio para actualizar la cantidad de un item
        // Por ahora, devolvemos un error 501 (No implementado)
        return ResponseEntity.status(501).body("Funcionalidad no implementada aún");
    }

    @DeleteMapping("/item/{itemId}")
    @ResponseBody
    public ResponseEntity<String> eliminarItem(@PathVariable Long itemId) {
        // Este método requiere una implementación en el servicio para eliminar un item del carrito
        // Por ahora, devolvemos un error 501 (No implementado)
        return ResponseEntity.status(501).body("Funcionalidad no implementada aún");
    }

    @PostMapping("/vaciar")
    public String vaciarCarrito(RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        carritoService.vaciarCarrito(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "Carrito vaciado");

        return "redirect:/carrito";
    }
}