package controller;

/**
 *
 * @author darry
 */

import model.Tratamiento;
import model.CategoriaTratamiento;
import service.TratamientoService;
import service.CategoriaTratamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tratamientos")
public class TratamientoController {
    
    @Autowired
    private TratamientoService tratamientoService;
    
    @Autowired
    private CategoriaTratamientoService categoriaService;
    
    @GetMapping
    public String listarTratamientos(Model model) {
        List<Tratamiento> tratamientos = tratamientoService.obtenerActivos();
        model.addAttribute("tratamientos", tratamientos);
        return "tratamientos/lista";
    }
    
    @GetMapping("/{id}")
    public String verTratamiento(@PathVariable Long id, Model model) {
        Optional<Tratamiento> tratamientoOpt = tratamientoService.obtenerPorId(id);
        if (tratamientoOpt.isPresent()) {
            model.addAttribute("tratamiento", tratamientoOpt.get());
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
