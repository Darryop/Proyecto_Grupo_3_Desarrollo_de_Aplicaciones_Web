package Proyecto.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import Proyecto.model.Usuario;
import Proyecto.model.Venta;
import Proyecto.repository.VentaRepository;
import Proyecto.service.UsuarioService;
import Proyecto.service.VentaService;

@Controller
@RequestMapping("/mis-compras")
public class MisComprasController {
    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private VentaService ventaService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    private Usuario obtenerUsuarioActual() {
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
    public String verMisCompras(Model model) {
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        // Usar el método que carga los detalles
        List<Venta> ventas = ventaRepository.findByUsuarioWithDetalles(usuario);
        
        // Calcular estadísticas
        double totalGastado = ventas.stream()
            .mapToDouble(v -> v.getTotal().doubleValue())
            .sum();
        
        long comprasCompletadas = ventas.stream()
            .filter(v -> v.getEstado().name().equals("COMPLETADA"))
            .count();
        
        model.addAttribute("ventas", ventas);
        model.addAttribute("totalGastado", totalGastado);
        model.addAttribute("comprasCompletadas", comprasCompletadas);
        
        return "ventas/mis-compras";
    }
}