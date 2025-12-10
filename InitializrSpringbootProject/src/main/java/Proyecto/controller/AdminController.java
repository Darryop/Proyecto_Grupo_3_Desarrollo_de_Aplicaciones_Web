package Proyecto.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import Proyecto.model.ItemCarrito;
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

    // Modifica el método reporteVentas en AdminController:
    @GetMapping("/ventas")
    public String reporteVentas(Model model) {
        // Obtener todas las ventas con relaciones
        List<Venta> ventas = ventaService.obtenerTodas();
        
        // Calcular estadísticas básicas
        double ingresosTotales = ventaService.calcularIngresosTotales();
        
        // Contar ventas completadas
        long ventasCompletadas = ventas.stream()
            .filter(v -> v.getEstado() != null && v.getEstado() == EstadoVenta.COMPLETADA)
            .count();
        
        // Contar ventas con tarjeta
        long ventasTarjeta = ventas.stream()
            .filter(v -> v.getMetodoPago() != null && v.getMetodoPago() == MetodoPago.TARJETA)
            .count();
        
        // Calcular productos más vendidos usando los datos existentes
        List<Map<String, Object>> productosMasVendidos = calcularProductosMasVendidos(ventas);
        
        // Calcular tratamientos más solicitados usando los datos existentes
        List<Map<String, Object>> tratamientosMasSolicitados = calcularTratamientosMasSolicitados(ventas);
        
        model.addAttribute("ventas", ventas);
        model.addAttribute("ingresosTotales", ingresosTotales);
        model.addAttribute("ventasCompletadas", ventasCompletadas);
        model.addAttribute("ventasTarjeta", ventasTarjeta);
        model.addAttribute("productosMasVendidos", productosMasVendidos);
        model.addAttribute("tratamientosMasSolicitados", tratamientosMasSolicitados);
        
        return "admin/ventas";
    }

    // Agrega estos métodos auxiliares en AdminController:
    private List<Map<String, Object>> calcularProductosMasVendidos(List<Venta> ventas) {
        Map<Long, Map<String, Object>> productosMap = new HashMap<>();
        
        for (Venta venta : ventas) {
            if (venta.getEstado() == EstadoVenta.COMPLETADA && venta.getCarrito() != null 
                && venta.getCarrito().getItems() != null) {
                
                for (ItemCarrito item : venta.getCarrito().getItems()) {
                    if (item.getTipo() != null && item.getTipo().name().equals("PRODUCTO") 
                        && item.getProducto() != null) {
                        
                        Producto producto = item.getProducto();
                        Long productoId = producto.getId();
                        
                        if (!productosMap.containsKey(productoId)) {
                            Map<String, Object> productoInfo = new HashMap<>();
                            productoInfo.put("id", producto.getId());
                            productoInfo.put("nombre", producto.getNombre());
                            productoInfo.put("cantidadVendida", item.getCantidad() != null ? item.getCantidad() : 0);
                            productoInfo.put("totalVendido", item.getPrecioUnitario() != null ? 
                                item.getPrecioUnitario() * item.getCantidad() : 0.0);
                            productosMap.put(productoId, productoInfo);
                        } else {
                            Map<String, Object> productoInfo = productosMap.get(productoId);
                            int cantidadActual = (int) productoInfo.get("cantidadVendida");
                            double totalActual = (double) productoInfo.get("totalVendido");
                            
                            productoInfo.put("cantidadVendida", cantidadActual + item.getCantidad());
                            productoInfo.put("totalVendido", totalActual + 
                                (item.getPrecioUnitario() != null ? 
                                item.getPrecioUnitario() * item.getCantidad() : 0.0));
                        }
                    }
                }
            }
        }
        
        // Ordenar por cantidad vendida (de mayor a menor)
        List<Map<String, Object>> productosOrdenados = new ArrayList<>(productosMap.values());
        productosOrdenados.sort((a, b) -> {
            int cantidadA = (int) a.get("cantidadVendida");
            int cantidadB = (int) b.get("cantidadVendida");
            return Integer.compare(cantidadB, cantidadA); // Orden descendente
        });
        
        // Tomar solo los top 5
        List<Map<String, Object>> topProductos = productosOrdenados.stream()
            .limit(5)
            .collect(Collectors.toList());
        
        // Calcular porcentajes para la barra de progreso
        if (!topProductos.isEmpty()) {
            int maxCantidad = topProductos.stream()
                .mapToInt(p -> (int) p.get("cantidadVendida"))
                .max()
                .orElse(1);
            
            for (Map<String, Object> producto : topProductos) {
                int cantidad = (int) producto.get("cantidadVendida");
                double porcentaje = (cantidad * 100.0) / maxCantidad;
                producto.put("porcentaje", porcentaje);
            }
        }
        
        return topProductos;
    }

    private List<Map<String, Object>> calcularTratamientosMasSolicitados(List<Venta> ventas) {
        Map<Long, Map<String, Object>> tratamientosMap = new HashMap<>();
        
        for (Venta venta : ventas) {
            if (venta.getEstado() == EstadoVenta.COMPLETADA && venta.getCarrito() != null 
                && venta.getCarrito().getItems() != null) {
                
                for (ItemCarrito item : venta.getCarrito().getItems()) {
                    if (item.getTipo() != null && item.getTipo().name().equals("CITA") 
                        && item.getCita() != null && item.getCita().getTratamiento() != null) {
                        
                        Tratamiento tratamiento = item.getCita().getTratamiento();
                        Long tratamientoId = tratamiento.getId();
                        
                        if (!tratamientosMap.containsKey(tratamientoId)) {
                            Map<String, Object> tratamientoInfo = new HashMap<>();
                            tratamientoInfo.put("id", tratamiento.getId());
                            tratamientoInfo.put("nombre", tratamiento.getNombre());
                            tratamientoInfo.put("cantidadSolicitada", 1); // Cada cita es 1 tratamiento
                            tratamientoInfo.put("ingresosGenerados", item.getPrecioUnitario() != null ? 
                                item.getPrecioUnitario() * item.getCantidad() : 0.0);
                            tratamientosMap.put(tratamientoId, tratamientoInfo);
                        } else {
                            Map<String, Object> tratamientoInfo = tratamientosMap.get(tratamientoId);
                            int cantidadActual = (int) tratamientoInfo.get("cantidadSolicitada");
                            double totalActual = (double) tratamientoInfo.get("ingresosGenerados");
                            
                            tratamientoInfo.put("cantidadSolicitada", cantidadActual + 1);
                            tratamientoInfo.put("ingresosGenerados", totalActual + 
                                (item.getPrecioUnitario() != null ? 
                                item.getPrecioUnitario() * item.getCantidad() : 0.0));
                        }
                    }
                }
            }
        }
        
        // Ordenar por cantidad solicitada (de mayor a menor)
        List<Map<String, Object>> tratamientosOrdenados = new ArrayList<>(tratamientosMap.values());
        tratamientosOrdenados.sort((a, b) -> {
            int cantidadA = (int) a.get("cantidadSolicitada");
            int cantidadB = (int) b.get("cantidadSolicitada");
            return Integer.compare(cantidadB, cantidadA); // Orden descendente
        });
        
        // Tomar solo los top 5
        List<Map<String, Object>> topTratamientos = tratamientosOrdenados.stream()
            .limit(5)
            .collect(Collectors.toList());
        
        // Calcular porcentajes para la barra de progreso
        if (!topTratamientos.isEmpty()) {
            int maxCantidad = topTratamientos.stream()
                .mapToInt(p -> (int) p.get("cantidadSolicitada"))
                .max()
                .orElse(1);
            
            for (Map<String, Object> tratamiento : topTratamientos) {
                int cantidad = (int) tratamiento.get("cantidadSolicitada");
                double porcentaje = (cantidad * 100.0) / maxCantidad;
                tratamiento.put("porcentaje", porcentaje);
            }
        }
        
        return topTratamientos;
    }

    // Editar Producto - Mostrar formulario
    @GetMapping("/productos/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Producto> productoOpt = productoService.obtenerPorId(id);
            if (productoOpt.isPresent()) {
                Producto producto = productoOpt.get();
                List<CategoriaProducto> categorias = categoriaProductoService.obtenerTodas();
                
                model.addAttribute("producto", producto);
                model.addAttribute("categorias", categorias);
                return "admin/editar-producto";
            } else {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/admin/productos";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el producto: " + e.getMessage());
            return "redirect:/admin/productos";
        }
    }

    // Editar Producto - Procesar formulario
    @PostMapping("/productos/editar/{id}")
    public String actualizarProducto(@PathVariable Long id,
                                    @ModelAttribute Producto producto,
                                    @RequestParam Long categoriaId,
                                    RedirectAttributes redirectAttributes) {
        try {
            Optional<Producto> productoExistenteOpt = productoService.obtenerPorId(id);
            if (productoExistenteOpt.isPresent()) {
                Producto productoExistente = productoExistenteOpt.get();
                
                // Actualizar campos del producto
                productoExistente.setNombre(producto.getNombre());
                productoExistente.setDescripcion(producto.getDescripcion());
                productoExistente.setPrecio(producto.getPrecio());
                productoExistente.setStock(producto.getStock());
                productoExistente.setActivo(producto.getActivo());
                productoExistente.setCodigoProducto(producto.getCodigoProducto());
                productoExistente.setImagenUrl(producto.getImagenUrl());
                
                // Actualizar categoría
                Optional<CategoriaProducto> categoriaOpt = categoriaProductoService.obtenerPorId(categoriaId);
                if (categoriaOpt.isPresent()) {
                    productoExistente.setCategoria(categoriaOpt.get());
                } else {
                    redirectAttributes.addFlashAttribute("error", "Categoría no encontrada");
                    return "redirect:/admin/productos/editar/" + id;
                }
                
                productoService.guardar(productoExistente);
                redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado exitosamente");
                return "redirect:/admin/lista-productos";
            } else {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/admin/productos";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar producto: " + e.getMessage());
            return "redirect:/admin/productos/editar/" + id;
        }
    }

    // Cambiar estado de producto (activar/desactivar)
    @PutMapping("/productos/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoProducto(@PathVariable Long id, 
                                                @RequestParam Boolean activo) {
        try {
            Optional<Producto> productoOpt = productoService.obtenerPorId(id);
            if (productoOpt.isPresent()) {
                Producto producto = productoOpt.get();
                producto.setActivo(activo);
                productoService.guardar(producto);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Eliminar Producto
    @DeleteMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Producto> productoOpt = productoService.obtenerPorId(id);
            if (productoOpt.isPresent()) {
                productoService.eliminar(id);
                redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar producto: " + e.getMessage());
        }
        return "redirect:/admin/lista-productos";
    }

    // Editar Tratamiento - Mostrar formulario
    @GetMapping("/tratamientos/editar/{id}")
    public String mostrarFormularioEditarTratamiento(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Tratamiento> tratamientoOpt = tratamientoService.obtenerPorId(id);
            if (tratamientoOpt.isPresent()) {
                Tratamiento tratamiento = tratamientoOpt.get();
                List<CategoriaTratamiento> categorias = categoriaTratamientoService.obtenerTodas();
                
                model.addAttribute("tratamiento", tratamiento);
                model.addAttribute("categorias", categorias);
                return "admin/editar-tratamiento";
            } else {
                redirectAttributes.addFlashAttribute("error", "Tratamiento no encontrado");
                return "redirect:/admin/lista-tratamientos";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el tratamiento: " + e.getMessage());
            return "redirect:/admin/lista-tratamientos";
        }
    }

    // Editar Tratamiento - Procesar formulario
    @PostMapping("/tratamientos/editar/{id}")
    public String actualizarTratamiento(@PathVariable Long id,
                                        @ModelAttribute Tratamiento tratamiento,
                                        @RequestParam Long categoriaId,
                                        @RequestParam(required = false) Boolean requiereConsulta,
                                        RedirectAttributes redirectAttributes) {
        try {
            Optional<Tratamiento> tratamientoExistenteOpt = tratamientoService.obtenerPorId(id);
            if (tratamientoExistenteOpt.isPresent()) {
                Tratamiento tratamientoExistente = tratamientoExistenteOpt.get();
                
                // Actualizar campos del tratamiento
                tratamientoExistente.setNombre(tratamiento.getNombre());
                tratamientoExistente.setDescripcion(tratamiento.getDescripcion());
                tratamientoExistente.setPrecio(tratamiento.getPrecio());
                tratamientoExistente.setDuracionMinutos(tratamiento.getDuracionMinutos());
                tratamientoExistente.setActivo(tratamiento.getActivo());
                tratamientoExistente.setCodigoTratamiento(tratamiento.getCodigoTratamiento());
                tratamientoExistente.setImagenUrl(tratamiento.getImagenUrl());
                
                // Manejar campo requiereConsulta
                if (requiereConsulta != null) {
                    tratamientoExistente.setRequiereConsulta(requiereConsulta);
                } else {
                    tratamientoExistente.setRequiereConsulta(false);
                }
                
                // Actualizar categoría
                Optional<CategoriaTratamiento> categoriaOpt = categoriaTratamientoService.obtenerPorId(categoriaId);
                if (categoriaOpt.isPresent()) {
                    tratamientoExistente.setCategoria(categoriaOpt.get());
                } else {
                    redirectAttributes.addFlashAttribute("error", "Categoría no encontrada");
                    return "redirect:/admin/tratamientos/editar/" + id;
                }
                
                tratamientoService.guardar(tratamientoExistente);
                redirectAttributes.addFlashAttribute("mensaje", "Tratamiento actualizado exitosamente");
                return "redirect:/admin/lista-tratamientos";
            } else {
                redirectAttributes.addFlashAttribute("error", "Tratamiento no encontrado");
                return "redirect:/admin/lista-tratamientos";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar tratamiento: " + e.getMessage());
            return "redirect:/admin/tratamientos/editar/" + id;
        }
    }

    // Eliminar Tratamiento
    @DeleteMapping("/tratamientos/eliminar/{id}")
    public String eliminarTratamiento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Tratamiento> tratamientoOpt = tratamientoService.obtenerPorId(id);
            if (tratamientoOpt.isPresent()) {
                tratamientoService.eliminar(id);
                redirectAttributes.addFlashAttribute("mensaje", "Tratamiento eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Tratamiento no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar tratamiento: " + e.getMessage());
        }
        return "redirect:/admin/lista-tratamientos";
    }

    // Editar Usuario - Mostrar formulario
    @GetMapping("/usuarios/editar/{id}")
    public String mostrarFormularioEditarUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(id);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                
                // Obtener todos los roles disponibles (si es necesario)
                // En este caso, los roles están en la tabla 'roles'
                // Podrías necesitar un servicio de roles, pero para simplificar, 
                // podemos usar los roles disponibles en la base de datos
                
                model.addAttribute("usuario", usuario);
                model.addAttribute("tiposUsuario", Usuario.TipoUsuario.values()); // Para mostrar en select
                return "admin/editar-usuario";
            } else {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/admin/usuarios";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el usuario: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    // Editar Usuario - Procesar formulario
    @PostMapping("/usuarios/editar/{id}")
    public String actualizarUsuario(@PathVariable Long id,
                                    @ModelAttribute Usuario usuario,
                                    @RequestParam Usuario.TipoUsuario tipo,
                                    RedirectAttributes redirectAttributes) {
        try {
            Optional<Usuario> usuarioExistenteOpt = usuarioService.obtenerPorId(id);
            if (usuarioExistenteOpt.isPresent()) {
                Usuario usuarioExistente = usuarioExistenteOpt.get();
                
                // Actualizar campos básicos
                usuarioExistente.setNombre(usuario.getNombre());
                usuarioExistente.setApellido(usuario.getApellido());
                usuarioExistente.setEmail(usuario.getEmail());
                usuarioExistente.setTelefono(usuario.getTelefono());
                usuarioExistente.setActivo(usuario.getActivo());
                usuarioExistente.setTipo(tipo);
                
                // Actualizar contraseña solo si se proporciona una nueva
                if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
                    usuarioExistente.setPassword(usuario.getPassword());
                }
                
                usuarioService.guardar(usuarioExistente);
                redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado exitosamente");
                return "redirect:/admin/usuarios";
            } else {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/admin/usuarios";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar usuario: " + e.getMessage());
            return "redirect:/admin/usuarios/editar/" + id;
        }
    }

    // Eliminar Usuario
    @DeleteMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(id);
            if (usuarioOpt.isPresent()) {
                usuarioService.eliminar(id);
                redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}