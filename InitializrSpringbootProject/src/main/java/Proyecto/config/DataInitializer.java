package Proyecto.config;

/**
 *
 * @author darry
 */

import Proyecto.service.ProductoService;
import Proyecto.service.CategoriaProductoService;
import Proyecto.service.TratamientoService;
import Proyecto.service.CategoriaTratamientoService;
import Proyecto.service.UsuarioService;
import Proyecto.service.ConfiguracionCitasService;
import Proyecto.model.Producto;
import Proyecto.model.Usuario;
import Proyecto.model.CategoriaTratamiento;
import Proyecto.model.CategoriaProducto;
import Proyecto.model.TipoUsuario;
import Proyecto.model.Tratamiento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaProductoService categoriaProductoService;

    @Autowired
    private CategoriaTratamientoService categoriaTratamientoService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private ConfiguracionCitasService configuracionCitasService;

    @Override
    public void run(String... args) throws Exception {
        inicializarDatos();
    }

    private void inicializarDatos() {
        // 1. Crear usuario administrador
        if (usuarioService.obtenerPorEmail("admin@kvestetica.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@kvestetica.com");
            admin.setPassword("1234");
            admin.setNombre("Administrador");
            admin.setApellido("Principal");
            admin.setTelefono("0000000000");
            admin.setTipo(TipoUsuario.ADMIN);
            usuarioService.guardar(admin);
        }

        // 2. Crear usuario cliente de prueba
        if (usuarioService.obtenerPorEmail("cliente@ejemplo.com").isEmpty()) {
            Usuario cliente = new Usuario();
            cliente.setEmail("cliente@ejemplo.com");
            cliente.setPassword("1234");
            cliente.setNombre("Juan");
            cliente.setApellido("Pérez");
            cliente.setTelefono("3001234567");
            cliente.setTipo(TipoUsuario.CLIENTE);
            usuarioService.guardar(cliente);
        }

        // 3. Crear categorías de productos
        if (categoriaProductoService.obtenerTodas().isEmpty()) {
            CategoriaProducto cat1 = new CategoriaProducto();
            cat1.setNombre("Cuidado Facial");
            cat1.setDescripcion("Productos para el cuidado y tratamiento facial");
            categoriaProductoService.guardar(cat1);

            CategoriaProducto cat2 = new CategoriaProducto();
            cat2.setNombre("Maquillaje");
            cat2.setDescripcion("Productos de maquillaje y cosméticos");
            categoriaProductoService.guardar(cat2);

            CategoriaProducto cat3 = new CategoriaProducto();
            cat3.setNombre("Cuidado Corporal");
            cat3.setDescripcion("Productos para el cuidado del cuerpo");
            categoriaProductoService.guardar(cat3);
        }

        // 4. Crear productos de ejemplo
        if (productoService.obtenerTodos().isEmpty()) {
            CategoriaProducto categoriaFacial = categoriaProductoService.obtenerPorNombre("Cuidado Facial").get();
            CategoriaProducto categoriaMaquillaje = categoriaProductoService.obtenerPorNombre("Maquillaje").get();
            CategoriaProducto categoriaCorporal = categoriaProductoService.obtenerPorNombre("Cuidado Corporal").get();

            Producto p1 = new Producto();
            p1.setCodigoProducto("PROD-001");
            p1.setNombre("Crema Hidratante Intensa");
            p1.setDescripcion("Crema hidratante para piel seca con ácido hialurónico");
            p1.setPrecio(25.99);
            p1.setStock(50);
            p1.setCategoria(categoriaFacial);
            p1.setImagenUrl("/images/crema-hidratante.jpg");
            productoService.guardar(p1);

            Producto p2 = new Producto();
            p2.setCodigoProducto("PROD-002");
            p2.setNombre("Base de Maquillaje Larga Duración");
            p2.setDescripcion("Base de maquillaje de cobertura completa y larga duración");
            p2.setPrecio(35.50);
            p2.setStock(30);
            p2.setCategoria(categoriaMaquillaje);
            p2.setImagenUrl("/images/base-maquillaje.jpg");
            productoService.guardar(p2);

            Producto p3 = new Producto();
            p3.setCodigoProducto("PROD-003");
            p3.setNombre("Aceite Corporal Nutritivo");
            p3.setDescripcion("Aceite corporal nutritivo con aceite de almendras y vitamina E");
            p3.setPrecio(18.75);
            p3.setStock(40);
            p3.setCategoria(categoriaCorporal);
            p3.setImagenUrl("/images/aceite-corporal.jpg");
            productoService.guardar(p3);
        }

        // 5. Crear categorías de tratamientos
        if (categoriaTratamientoService.obtenerTodas().isEmpty()) {
            CategoriaTratamiento cat1 = new CategoriaTratamiento();
            cat1.setNombre("Faciales");
            cat1.setDescripcion("Tratamientos especializados para el rostro");
            categoriaTratamientoService.guardar(cat1);

            CategoriaTratamiento cat2 = new CategoriaTratamiento();
            cat2.setNombre("Corporales");
            cat2.setDescripcion("Tratamientos para el cuerpo");
            categoriaTratamientoService.guardar(cat2);

            CategoriaTratamiento cat3 = new CategoriaTratamiento();
            cat3.setNombre("Depilación");
            cat3.setDescripcion("Servicios de depilación");
            categoriaTratamientoService.guardar(cat3);
        }

        // 6. Crear tratamientos de ejemplo
        if (tratamientoService.obtenerTodos().isEmpty()) {
            CategoriaTratamiento catFacial = categoriaTratamientoService.obtenerPorNombre("Faciales").get();
            CategoriaTratamiento catCorporal = categoriaTratamientoService.obtenerPorNombre("Corporales").get();
            CategoriaTratamiento catDepilacion = categoriaTratamientoService.obtenerPorNombre("Depilación").get();

            Tratamiento t1 = new Tratamiento();
            t1.setCodigoTratamiento("TRAT-001");
            t1.setNombre("Limpieza Facial Profunda");
            t1.setDescripcion("Limpieza profunda del rostro con extracción de impurezas");
            t1.setPrecio(50.00);
            t1.setDuracionMinutos(60);
            t1.setCategoria(catFacial);
            tratamientoService.guardar(t1);

            Tratamiento t2 = new Tratamiento();
            t2.setCodigoTratamiento("TRAT-002");
            t2.setNombre("Masaje Relajante");
            t2.setDescripcion("Masaje corporal relajante con aceites esenciales");
            t2.setPrecio(75.00);
            t2.setDuracionMinutos(90);
            t2.setCategoria(catCorporal);
            tratamientoService.guardar(t2);

            Tratamiento t3 = new Tratamiento();
            t3.setCodigoTratamiento("TRAT-003");
            t3.setNombre("Depilación Láser Facial");
            t3.setDescripcion("Sesión de depilación láser para zona facial");
            t3.setPrecio(120.00);
            t3.setDuracionMinutos(45);
            t3.setCategoria(catDepilacion);
            tratamientoService.guardar(t3);
        }

        // 7. Asegurar configuración de citas
        configuracionCitasService.obtenerConfiguracion();

        System.out.println("✅ Datos de prueba inicializados correctamente");
    }
}
