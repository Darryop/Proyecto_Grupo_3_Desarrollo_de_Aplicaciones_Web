package controller;

/**
 *
 * @author darry
 */

import model.Producto;
import model.CategoriaProducto;
import service.ProductoService;
import service.CategoriaProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private CategoriaProductoService categoriaService;
    
    @GetMapping
    public String listarProductos(Model model) {
        List<Producto> productos = productoService.obtenerActivos();
        model.addAttribute("productos", productos);
        return "productos/lista";
    }
    
    @GetMapping("/{id}")
    public String verProducto(@PathVariable Long id, Model model) {
        Optional<Producto> productoOpt = productoService.obtenerPorId(id);
        if (productoOpt.isPresent()) {
            model.addAttribute("producto", productoOpt.get());
            return "productos/detalle";
        } else {
            return "redirect:/productos";
        }
    }
    
    @GetMapping("/categoria/{categoriaId}")
    public String productosPorCategoria(@PathVariable Long categoriaId, Model model) {
        Optional<CategoriaProducto> categoriaOpt = categoriaService.obtenerPorId(categoriaId);
        if (categoriaOpt.isPresent()) {
            List<Producto> productos = productoService.obtenerPorCategoria(categoriaOpt.get());
            model.addAttribute("productos", productos);
            model.addAttribute("categoria", categoriaOpt.get());
            return "productos/lista";
        } else {
            return "redirect:/productos";
        }
    }
    
    @GetMapping("/buscar")
    public String buscarProductos(@RequestParam String query, Model model) {
        List<Producto> productos = productoService.buscarPorNombre(query);
        model.addAttribute("productos", productos);
        model.addAttribute("query", query);
        return "productos/lista";
    }
}
