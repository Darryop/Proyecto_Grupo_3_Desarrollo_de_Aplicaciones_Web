package Proyecto.controller;

/**
 *
 * @author darry
 */

import Proyecto.model.Producto;
import Proyecto.model.CategoriaProducto;
import Proyecto.service.ProductoService;
import Proyecto.service.CategoriaProductoService;
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
    public String listarProductos(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) Long categoria,
        Model model) {
        
        List<Producto> productos;
        if (query != null && !query.isEmpty()) {
            productos = productoService.buscarPorNombre(query);
        } else if (categoria != null) {
            Optional<CategoriaProducto> categoriaOpt = categoriaService.obtenerPorId(categoria);
            if (categoriaOpt.isPresent()) {
                productos = productoService.obtenerPorCategoria(categoriaOpt.get());
                model.addAttribute("categoriaFiltro", categoria);
            } else {
                productos = productoService.obtenerActivos();
            }
        } else {
            productos = productoService.obtenerActivos();
        }
        
        List<CategoriaProducto> categorias = categoriaService.obtenerActivas();
        
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("busqueda", query);
        model.addAttribute("categoriaFiltro", categoria);
        
        return "productos/lista"; // Cambiado para que coincida con la estructura
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
            model.addAttribute("categoriaFiltro", categoriaId);
            return "productos/lista";
        } else {
            return "redirect:/productos";
        }
    }
    
    @GetMapping("/buscar")
    public String buscarProductos(@RequestParam String query, Model model) {
        List<Producto> productos = productoService.buscarPorNombre(query);
        List<CategoriaProducto> categorias = categoriaService.obtenerActivas();
        
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("query", query);
        model.addAttribute("busqueda", query);
        
        return "productos/lista";
    }
}
