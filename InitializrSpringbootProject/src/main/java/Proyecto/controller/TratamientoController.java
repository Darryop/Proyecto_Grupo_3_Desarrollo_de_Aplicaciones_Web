package Proyecto.controller;

import Proyecto.model.Tratamiento;
import Proyecto.model.CategoriaTratamiento;
import Proyecto.service.TratamientoService;
import Proyecto.service.CategoriaTratamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // ← AÑADE ESTE IMPORT

@Controller
@RequestMapping("/tratamientos")
public class TratamientoController {
    
    @Autowired
    private TratamientoService tratamientoService;
    
    @Autowired
    private CategoriaTratamientoService categoriaService;
    
    @GetMapping
    public String listarTratamientos(
        @RequestParam(required = false) Long categoria,
        Model model) {

        List<Tratamiento> tratamientos;
        List<CategoriaTratamiento> categorias = categoriaService.obtenerActivas();

        if (categoria != null) {
            Optional<CategoriaTratamiento> categoriaOpt = categoriaService.obtenerPorId(categoria);
            if (categoriaOpt.isPresent()) {
                tratamientos = tratamientoService.obtenerPorCategoria(categoriaOpt.get());
                model.addAttribute("categoriaFiltro", categoria);
            } else {
                tratamientos = tratamientoService.obtenerActivos();
            }
        } else {
            tratamientos = tratamientoService.obtenerActivos();
        }

        model.addAttribute("tratamientos", tratamientos);
        model.addAttribute("categorias", categorias);

        return "tratamientos/lista";
    }
    
    @GetMapping("/{id}")
    public String verTratamiento(@PathVariable Long id, Model model) {
        Optional<Tratamiento> tratamientoOpt = tratamientoService.obtenerPorId(id);
        if (tratamientoOpt.isPresent()) {
            Tratamiento tratamiento = tratamientoOpt.get();
            model.addAttribute("tratamiento", tratamiento);
            
            // Obtener tratamientos relacionados (misma categoría, excluyendo el actual)
            List<Tratamiento> tratamientosRelacionados = tratamientoService
                .obtenerPorCategoria(tratamiento.getCategoria())
                .stream()
                .filter(t -> !t.getId().equals(tratamiento.getId()))
                .limit(3)
                .collect(Collectors.toList());
            model.addAttribute("tratamientosRelacionados", tratamientosRelacionados);
            
            return "tratamientos/detalle";
        } else {
            return "redirect:/tratamientos";
        }
    }
    
    @GetMapping("/categoria/{categoriaId}")
    public String tratamientosPorCategoria(@PathVariable Long categoriaId, Model model) {
        Optional<CategoriaTratamiento> categoriaOpt = categoriaService.obtenerPorId(categoriaId);
        if (categoriaOpt.isPresent()) {
            List<Tratamiento> tratamientos = tratamientoService.obtenerPorCategoria(categoriaOpt.get());
            model.addAttribute("tratamientos", tratamientos);
            model.addAttribute("categoria", categoriaOpt.get());
            return "tratamientos/lista";
        } else {
            return "redirect:/tratamientos";
        }
    }
    
    @GetMapping("/buscar")
    public String buscarTratamientos(@RequestParam String query, Model model) {
        List<Tratamiento> tratamientos = tratamientoService.buscarPorNombre(query);
        model.addAttribute("tratamientos", tratamientos);
        model.addAttribute("query", query);
        return "tratamientos/lista";
    }
}