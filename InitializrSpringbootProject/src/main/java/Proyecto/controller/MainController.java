package Proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Proyecto.model.Usuario;
import Proyecto.model.Producto;
import Proyecto.model.Tratamiento;
import Proyecto.service.ProductoService;
import Proyecto.service.TratamientoService;
import Proyecto.service.AuthService;

import java.util.List;

@Controller
public class MainController {
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private TratamientoService tratamientoService;
    
    @Autowired
    private AuthService authService;
    
    @GetMapping("/")
    public String home(Model model) {
        // Para la navegación activa
        model.addAttribute("activePage", "inicio");
        
        // Obtener productos destacados
        List<Producto> productosDestacados = productoService.obtenerActivos()
            .stream()
            .limit(6)
            .toList();
        
        // Obtener tratamientos populares
        List<Tratamiento> tratamientosPopulares = tratamientoService.obtenerActivos()
            .stream()
            .limit(4)
            .toList();
        
        model.addAttribute("productosDestacados", productosDestacados);
        model.addAttribute("tratamientosPopulares", tratamientosPopulares);
        
        return "index";
    }
    
    @GetMapping("/nosotros")
    public String nosotros(Model model) {
        model.addAttribute("activePage", "nosotros");
        return "nosotros";
    }
    
    @GetMapping("/servicios")
    public String servicios(Model model) {
        model.addAttribute("activePage", "servicios");
        List<Tratamiento> tratamientos = tratamientoService.obtenerActivos();
        model.addAttribute("tratamientos", tratamientos);
        return "servicios";
    }
    
    @GetMapping("/contacto")
    public String contacto(Model model) {
        model.addAttribute("activePage", "contacto");
        return "contacto";
    }
    
    
}