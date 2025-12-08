package Proyecto.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Proyecto.model.CategoriaProducto;
import Proyecto.model.CategoriaTratamiento;
import Proyecto.model.EstadoVenta;
import Proyecto.model.MetodoPago;
import Proyecto.model.Producto;
import Proyecto.model.Tratamiento;
import Proyecto.model.Usuario;
import Proyecto.model.Venta;
import Proyecto.service.CategoriaProductoService;
import Proyecto.service.CategoriaTratamientoService;
import Proyecto.service.ProductoService;
import Proyecto.service.TratamientoService;
import Proyecto.service.UsuarioService;
import Proyecto.service.VentaService;

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
    
    @GetMapping("/lista-productos")
    public String listaProductos(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String estado,
        Model model) {

        List<Producto> productos = productoService.obtenerTodos();
        List<CategoriaProducto> categorias = categoriaProductoService.obtenerTodas();

        // Filtrar productos si hay parámetros de búsqueda
        if (search != null && !search.trim().isEmpty()) {
            String terminoBusqueda = search.toLowerCase().trim();
            productos = productos.stream()
                .filter(p -> 
                    (p.getNombre() != null && p.getNombre().toLowerCase().contains(terminoBusqueda)) ||
                    (p.getCodigoProducto() != null && p.getCodigoProducto().toLowerCase().contains(terminoBusqueda)) ||
                    (p.getCategoria() != null && p.getCategoria().getNombre() != null && 
                     p.getCategoria().getNombre().toLowerCase().contains(terminoBusqueda))
                )
                .collect(Collectors.toList());
        }

        // Filtrar por estado si se especificó
        if (estado != null && !estado.trim().isEmpty()) {
            boolean estadoBooleano = estado.equals("activo");
            productos = productos.stream()
                .filter(p -> p.getActivo() != null && p.getActivo() == estadoBooleano)
                .collect(Collectors.toList());
        }

        // Calcular estadísticas (globales, no filtradas)
        List<Producto> todosProductos = productoService.obtenerTodos();
        long productosActivos = todosProductos.stream()
            .filter(p -> p.getActivo() != null && p.getActivo())
            .count();

        long productosBajoStock = todosProductos.stream()
            .filter(p -> p.getStock() < 10)
            .count();

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalProductos", todosProductos.size());
        model.addAttribute("productosActivos", productosActivos);
        model.addAttribute("productosBajoStock", productosBajoStock);
        model.addAttribute("totalCategorias", categorias.size());

        return "admin/lista-productos";
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
        
        // Crear nuevo tratamiento con valores por defecto
        Tratamiento nuevoTratamiento = new Tratamiento();
        nuevoTratamiento.setActivo(true);
        nuevoTratamiento.setRequiereConsulta(false);
        
        model.addAttribute("tratamientos", tratamientos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("nuevoTratamiento", nuevoTratamiento);
        return "admin/tratamientos";
    }
    
    @PostMapping("/tratamientos")
    public String crearTratamiento(@ModelAttribute Tratamiento tratamiento,
                                @RequestParam Long categoriaId,
                                @RequestParam(required = false) Boolean requiereConsulta,
                                @RequestParam(required = false) String imagenUrl,
                                RedirectAttributes redirectAttributes) {
        try {
            Optional<CategoriaTratamiento> categoriaOpt = categoriaTratamientoService.obtenerPorId(categoriaId);
            if (categoriaOpt.isPresent()) {
                tratamiento.setCategoria(categoriaOpt.get());
                
                // Manejar los campos opcionales
                if (requiereConsulta != null) {
                    tratamiento.setRequiereConsulta(requiereConsulta);
                } else {
                    tratamiento.setRequiereConsulta(false);
                }
                
                if (imagenUrl != null && !imagenUrl.trim().isEmpty()) {
                    tratamiento.setImagenUrl(imagenUrl);
                }
                
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

    // Gestión de Tratamientos (Lista)
    @GetMapping("/lista-tratamientos")
    public String listaTratamientos(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) Long categoriaId,
        Model model) {

        List<Tratamiento> tratamientos = tratamientoService.obtenerTodos();
        List<CategoriaTratamiento> categorias = categoriaTratamientoService.obtenerTodas();

        // Filtrar tratamientos si hay parámetros de búsqueda
        if (search != null && !search.trim().isEmpty()) {
            String terminoBusqueda = search.toLowerCase().trim();
            tratamientos = tratamientos.stream()
                .filter(t -> 
                    (t.getNombre() != null && t.getNombre().toLowerCase().contains(terminoBusqueda)) ||
                    (t.getCodigoTratamiento() != null && t.getCodigoTratamiento().toLowerCase().contains(terminoBusqueda)) ||
                    (t.getCategoria() != null && t.getCategoria().getNombre() != null && 
                    t.getCategoria().getNombre().toLowerCase().contains(terminoBusqueda))
                )
                .collect(Collectors.toList());
        }

        // Filtrar por estado si se especificó
        if (estado != null && !estado.trim().isEmpty()) {
            boolean estadoBooleano = estado.equals("activo");
            tratamientos = tratamientos.stream()
                .filter(t -> t.getActivo() != null && t.getActivo() == estadoBooleano)
                .collect(Collectors.toList());
        }

        // Filtrar por categoría si se especificó
        if (categoriaId != null) {
            tratamientos = tratamientos.stream()
                .filter(t -> t.getCategoria() != null && t.getCategoria().getId().equals(categoriaId))
                .collect(Collectors.toList());
        }

        // Calcular estadísticas (globales, no filtradas)
        List<Tratamiento> todosTratamientos = tratamientoService.obtenerTodos();
        long tratamientosActivos = todosTratamientos.stream()
            .filter(t -> t.getActivo() != null && t.getActivo())
            .count();

        long tratamientosConsulta = todosTratamientos.stream()
            .filter(t -> t.getRequiereConsulta() != null && t.getRequiereConsulta())
            .count();

        model.addAttribute("tratamientos", tratamientos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalTratamientos", todosTratamientos.size());
        model.addAttribute("tratamientosActivos", tratamientosActivos);
        model.addAttribute("tratamientosConsulta", tratamientosConsulta);
        model.addAttribute("totalCategorias", categorias.size());

        return "admin/lista-tratamientos";
    }

    // Endpoint para cambiar estado de tratamiento
    @PutMapping("/tratamientos/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoTratamiento(@PathVariable Long id, 
                                                    @RequestParam Boolean activo) {
        try {
            Optional<Tratamiento> tratamientoOpt = tratamientoService.obtenerPorId(id);
            if (tratamientoOpt.isPresent()) {
                Tratamiento tratamiento = tratamientoOpt.get();
                tratamiento.setActivo(activo);
                tratamientoService.guardar(tratamiento);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/usuarios")
    public String gestionUsuarios(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String rol,
        @RequestParam(required = false) String estado,
        Model model) {

        // Obtener todos los usuarios
        List<Usuario> usuarios = usuarioService.obtenerTodos();

        // Filtrar por búsqueda
        if (search != null && !search.trim().isEmpty()) {
            String terminoBusqueda = search.toLowerCase().trim();
            usuarios = usuarios.stream()
                .filter(usuario -> 
                    (usuario.getNombre() != null && usuario.getNombre().toLowerCase().contains(terminoBusqueda)) ||
                    (usuario.getApellido() != null && usuario.getApellido().toLowerCase().contains(terminoBusqueda)) ||
                    (usuario.getEmail() != null && usuario.getEmail().toLowerCase().contains(terminoBusqueda)) ||
                    (usuario.getTelefono() != null && usuario.getTelefono().toLowerCase().contains(terminoBusqueda))
                )
                .collect(Collectors.toList());
        }

        // Filtrar por rol (CORREGIDO - usando el nombre correcto de la variable)
        if (rol != null && !rol.trim().isEmpty()) {
            if (rol.equals("CLIENTE") || rol.equals("ADMIN")) {
                usuarios = usuarios.stream()
                    .filter(usuario -> usuario.getTipo() != null && 
                                usuario.getTipo().toString().equals(rol))  // Cambiado de name() a toString()
                    .collect(Collectors.toList());
            } else if (rol.equals("EMPLEADO")) {
                // Buscar usuarios con rol EMPLEADO en la tabla usuario_roles
                usuarios = usuarios.stream()
                    .filter(usuario -> usuario.getRoles() != null && 
                        usuario.getRoles().stream().anyMatch(r -> 
                            r.getNombre() != null && r.getNombre().equals("ROLE_EMPLEADO")))
                    .collect(Collectors.toList());
            }
        }

        // Filtrar por estado
        if (estado != null && !estado.trim().isEmpty()) {
            boolean estadoBooleano = estado.equals("activo");
            usuarios = usuarios.stream()
                .filter(usuario -> usuario.getActivo() != null && usuario.getActivo() == estadoBooleano)
                .collect(Collectors.toList());
        }

        // Calcular estadísticas (corregido - usando usuarios filtrados)
        long totalClientes = 0;
        long totalEmpleados = 0;
        long totalAdmins = 0;
        
        for (Usuario usuario : usuarios) {
            if (usuario.getTipo() != null && usuario.getTipo() == Usuario.TipoUsuario.CLIENTE) {
                totalClientes++;
            }
            if (usuario.getTipo() != null && usuario.getTipo() == Usuario.TipoUsuario.ADMIN) {
                totalAdmins++;
            }
            if (usuario.getRoles() != null && 
                usuario.getRoles().stream().anyMatch(r -> 
                    r.getNombre() != null && r.getNombre().equals("ROLE_EMPLEADO"))) {
                totalEmpleados++;
            }
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalEmpleados", totalEmpleados);
        model.addAttribute("totalAdmins", totalAdmins);

        return "admin/usuarios";
    }

    // Endpoint para cambiar estado de usuario
    @PutMapping("/usuarios/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoUsuario(@PathVariable Long id, 
                                                 @RequestParam Boolean activo) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(id);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                usuario.setActivo(activo);
                usuarioService.guardar(usuario);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Reportes de Ventas
    @GetMapping("/ventas")
    public String reporteVentas(Model model) {
        // Obtener todas las ventas
        List<Venta> ventas = ventaService.obtenerTodas();
        
        // Calcular estadísticas
        double ingresosTotales = ventaService.calcularIngresosTotales();
        
        // Contar ventas completadas
        long ventasCompletadas = ventas.stream()
            .filter(v -> v.getEstado() != null && v.getEstado() == EstadoVenta.COMPLETADA)
            .count();
        
        // Contar ventas con tarjeta
        long ventasTarjeta = ventas.stream()
            .filter(v -> v.getMetodoPago() != null && v.getMetodoPago() == MetodoPago.TARJETA)
            .count();
        
        // Obtener productos más vendidos (necesitarás implementar esto)
        List<Producto> productosMasVendidos = new ArrayList<>();
        
        // Obtener tratamientos más solicitados (necesitarás implementar esto)
        List<Tratamiento> tratamientosMasSolicitados = new ArrayList<>();
        
        model.addAttribute("ventas", ventas);
        model.addAttribute("ingresosTotales", ingresosTotales);
        model.addAttribute("ventasCompletadas", ventasCompletadas);
        model.addAttribute("ventasTarjeta", ventasTarjeta);
        model.addAttribute("productosMasVendidos", productosMasVendidos);
        model.addAttribute("tratamientosMasSolicitados", tratamientosMasSolicitados);
        
        return "admin/ventas";
    }
}