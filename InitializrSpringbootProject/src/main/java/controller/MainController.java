package controller;

/**
 *
 * @author darry
 */

import model.Producto;
import model.Tratamiento;
import service.ProductoService;
import service.TratamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class MainController {
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private TratamientoService tratamientoService;
    
    @GetMapping("/")
    public String home(Model model) {
        // Productos destacados (los primeros 6 productos activos)
        List<Producto> productosDestacados = productoService.obtenerActivos()
                .stream()
                .limit(6)
                .toList();
        
        // Tratamientos populares (los primeros 4 tratamientos activos)
        List<Tratamiento> tratamientosPopulares = tratamientoService.obtenerActivos()
                .stream()
                .limit(4)
                .toList();
        
        model.addAttribute("productosDestacados", productosDestacados);
        model.addAttribute("tratamientosPopulares", tratamientosPopulares);
        
        return "index";
    }
    
    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }
    
    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }
    
    @GetMapping("/servicios")
    public String servicios(Model model) {
        model.addAttribute("tratamientos", tratamientoService.obtenerActivos());
        return "servicios";
    }
}