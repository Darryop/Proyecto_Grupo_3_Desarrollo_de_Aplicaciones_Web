package Proyecto.controller;

/**
 *
 * @author darry
 */

import Proyecto.model.*;
import Proyecto.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private CategoriaProductoService categoriaService;
    
    @Autowired
    private CarritoService carritoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping
    public String listarProductos(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoria,
            Model model) {
        
        model.addAttribute("activePage", "productos");
        
        List<Producto> productos;
        List<CategoriaProducto> categorias = categoriaService.obtenerActivas();
        
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
        
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("busqueda", query);
        model.addAttribute("categoriaFiltro", categoria);
        
        addCartCountToModel(model);
        return "productos/lista";
    }
    
    @GetMapping("/{id}")
    public String verProducto(@PathVariable Long id, Model model) {
        Optional<Producto> productoOpt = productoService.obtenerPorId(id);
        
        if (productoOpt.isPresent()) {
            model.addAttribute("producto", productoOpt.get());
            addCartCountToModel(model);
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
            List<CategoriaProducto> categorias = categoriaService.obtenerActivas();
            
            model.addAttribute("productos", productos);
            model.addAttribute("categorias", categorias);
            model.addAttribute("categoria", categoriaOpt.get());
            model.addAttribute("categoriaFiltro", categoriaId);
            
            addCartCountToModel(model);
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
        
        addCartCountToModel(model);
        return "productos/lista";
    }
    
    private void addCartCountToModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Usuario usuario = usuarioService.obtenerPorEmail(auth.getName()).orElse(null);
            if (usuario != null) {
                int carritoCount = carritoService.obtenerItemsDelCarrito(usuario).size();
                model.addAttribute("carritoCount", carritoCount);
            }
        }
    }
}