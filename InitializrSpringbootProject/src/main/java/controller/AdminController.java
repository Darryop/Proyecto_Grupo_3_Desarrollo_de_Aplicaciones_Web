package controller;

/**
 *
 * @author darry
 */

import model.*;
import service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private CategoriaProductoService categoriaProductoService;
    
    @Autowired
    private TratamientoService tratamientoService;
    
    @Autowired
    private CategoriaTratamientoService categoriaTratamientoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private VentaService ventaService;
    
    @GetMapping
    public String panelAdmin(Model model) {
        // Estadísticas para el dashboard
        long totalProductos = productoService.obtenerTodos().size();
        long totalTratamientos = tratamientoService.obtenerTodos().size();
        long totalUsuarios = usuarioService.obtenerTodos().size();
        long totalVentas = ventaService.contarVentasCompletadas();
        double ingresosTotales = ventaService.calcularIngresosTotales();
        
        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalTratamientos", totalTratamientos);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("ingresosTotales", ingresosTotales);
        
        return "admin/dashboard";
    }
    
    // Gestión de Productos
    @GetMapping("/productos")
    public String gestionProductos(Model model) {
        List<Producto> productos = productoService.obtenerTodos();
        List<CategoriaProducto> categorias = categoriaProductoService.obtenerTodas();
        
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("nuevoProducto", new Producto());
        return "admin/productos";
    }
    
    @PostMapping("/productos")
    public String crearProducto(@ModelAttribute Producto producto,
                               @RequestParam Long categoriaId,
                               RedirectAttributes redirectAttributes) {
        try {
            Optional<CategoriaProducto> categoriaOpt = categoriaProductoService.obtenerPorId(categoriaId);
            if (categoriaOpt.isPresent()) {
                producto.setCategoria(categoriaOpt.get());
                productoService.guardar(producto);
                redirectAttributes.addFlashAttribute("mensaje", "Producto creado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Categoría no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear producto: " + e.getMessage());
        }
        return "redirect:/admin/productos";
    }
    
    // Gestión de Tratamientos
    @GetMapping("/tratamientos")
    public String gestionTratamientos(Model model) {
        List<Tratamiento> tratamientos = tratamientoService.obtenerTodos();
        List<CategoriaTratamiento> categorias = categoriaTratamientoService.obtenerTodas();
        
        model.addAttribute("tratamientos", tratamientos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("nuevoTratamiento", new Tratamiento());
        return "admin/tratamientos";
    }
    
    @PostMapping("/tratamientos")
    public String crearTratamiento(@ModelAttribute Tratamiento tratamiento,
                                  @RequestParam Long categoriaId,
                                  RedirectAttributes redirectAttributes) {
        try {
            Optional<CategoriaTratamiento> categoriaOpt = categoriaTratamientoService.obtenerPorId(categoriaId);
            if (categoriaOpt.isPresent()) {
                tratamiento.setCategoria(categoriaOpt.get());
                tratamientoService.guardar(tratamiento);
                redirectAttributes.addFlashAttribute("mensaje", "Tratamiento creado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Categoría no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear tratamiento: " + e.getMessage());
        }
        return "redirect:/admin/tratamientos";
    }
    
    // Gestión de Usuarios
    @GetMapping("/usuarios")
    public String gestionUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios";
    }
    
    // Reportes de Ventas
    @GetMapping("/ventas")
    public String reporteVentas(Model model) {
        model.addAttribute("ventas", ventaService.obtenerTodas());
        model.addAttribute("ingresosTotales", ventaService.calcularIngresosTotales());
        return "admin/ventas";
    }
}