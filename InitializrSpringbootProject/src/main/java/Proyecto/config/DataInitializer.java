package Proyecto.config;

/**
 *
 * @author darry
 */



import Proyecto.model.CategoriaProducto;
import Proyecto.model.CategoriaTratamiento;
import Proyecto.model.ConfiguracionCitas;
import Proyecto.model.Producto;
import Proyecto.model.Rol;
import Proyecto.model.Ruta;
import Proyecto.model.Tratamiento;
import Proyecto.model.Usuario;
import Proyecto.repository.CategoriaProductoRepository;
import Proyecto.repository.CategoriaTratamientoRepository;
import Proyecto.repository.ConfiguracionCitasRepository;
import Proyecto.repository.ProductoRepository;
import Proyecto.repository.RolRepository;
import Proyecto.repository.TratamientoRepository;
import Proyecto.repository.UsuarioRepository;
import Proyecto.service.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaProductoRepository categoriaProductoRepository;

    @Autowired
    private CategoriaTratamientoRepository categoriaTratamientoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private ConfiguracionCitasRepository configuracionCitasRepository;

    @Autowired
    private RutaService rutaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        inicializarDatosSeguridad();
        inicializarDatos();
    }

    private void inicializarDatosSeguridad() {
        // Crear roles si no existen
        if (rolRepository.count() == 0) {
            Rol roleCliente = new Rol();
            roleCliente.setNombre("ROLE_CLIENTE");
            roleCliente.setDescripcion("Usuario cliente que puede comprar productos y agendar citas");
            rolRepository.save(roleCliente);

            Rol roleAdmin = new Rol();
            roleAdmin.setNombre("ROLE_ADMIN");
            roleAdmin.setDescripcion("Administrador con acceso completo al sistema");
            rolRepository.save(roleAdmin);

            Rol roleEmpleado = new Rol();
            roleEmpleado.setNombre("ROLE_EMPLEADO");
            roleEmpleado.setDescripcion("Empleado de la estética con permisos limitados");
            rolRepository.save(roleEmpleado);

            System.out.println("✅ Roles creados exitosamente");
        }

        // Crear rutas de seguridad si no existen
        if (rutaService.getRutas().isEmpty()) {
            Rol adminRole = rolRepository.findByNombre("ROLE_ADMIN").get();
            
            Ruta rutaAdmin = new Ruta();
            rutaAdmin.setRuta("/admin/**");
            rutaAdmin.setRequiereRol(true);
            rutaAdmin.setRol(adminRole);
            rutaService.guardar(rutaAdmin);

            System.out.println("✅ Rutas de seguridad creadas exitosamente");
        }

        // Crear usuario administrador si no existe
        if (usuarioRepository.findByEmail("admin@kvestetica.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@kvestetica.com");
            admin.setPassword(passwordEncoder.encode("1234")); // Contraseña encriptada
            admin.setNombre("Administrador");
            admin.setApellido("Principal");
            admin.setTelefono("0000000000");
            
            // Asignar rol de administrador
            Rol adminRole = rolRepository.findByNombre("ROLE_ADMIN").get();
            admin.setRoles(Set.of(adminRole));
            
            usuarioRepository.save(admin);
            System.out.println("✅ Usuario administrador creado exitosamente");
        }

        // Crear usuario cliente de prueba si no existe
        if (usuarioRepository.findByEmail("cliente@ejemplo.com").isEmpty()) {
            Usuario cliente = new Usuario();
            cliente.setEmail("cliente@ejemplo.com");
            cliente.setPassword(passwordEncoder.encode("1234")); // Contraseña encriptada
            cliente.setNombre("Juan");
            cliente.setApellido("Pérez");
            cliente.setTelefono("3001234567");
            
            // Asignar rol de cliente
            Rol clienteRole = rolRepository.findByNombre("ROLE_CLIENTE").get();
            cliente.setRoles(Set.of(clienteRole));
            
            usuarioRepository.save(cliente);
            System.out.println("✅ Usuario cliente de prueba creado exitosamente");
        }
    }

    private void inicializarDatos() {
        // 1. Crear categorías de productos
        if (categoriaProductoRepository.count() == 0) {
            CategoriaProducto cat1 = new CategoriaProducto();
            cat1.setNombre("Cuidado Facial");
            cat1.setDescripcion("Productos para el cuidado y tratamiento facial");
            categoriaProductoRepository.save(cat1);

            CategoriaProducto cat2 = new CategoriaProducto();
            cat2.setNombre("Maquillaje");
            cat2.setDescripcion("Productos de maquillaje y cosméticos");
            categoriaProductoRepository.save(cat2);

            CategoriaProducto cat3 = new CategoriaProducto();
            cat3.setNombre("Cuidado Corporal");
            cat3.setDescripcion("Productos para el cuidado del cuerpo");
            categoriaProductoRepository.save(cat3);

            System.out.println("✅ Categorías de productos creadas exitosamente");
        }

        // 2. Crear productos de ejemplo
        if (productoRepository.count() == 0) {
            CategoriaProducto categoriaFacial = categoriaProductoRepository.findByNombre("Cuidado Facial").get();
            CategoriaProducto categoriaMaquillaje = categoriaProductoRepository.findByNombre("Maquillaje").get();
            CategoriaProducto categoriaCorporal = categoriaProductoRepository.findByNombre("Cuidado Corporal").get();

            Producto p1 = new Producto();
            p1.setCodigoProducto("PROD-001");
            p1.setNombre("Crema Hidratante Intensa");
            p1.setDescripcion("Crema hidratante para piel seca con ácido hialurónico");
            p1.setPrecio(25.99);
            p1.setStock(50);
            p1.setCategoria(categoriaFacial);
            p1.setImagenUrl("/images/crema-hidratante.jpg");
            productoRepository.save(p1);

            Producto p2 = new Producto();
            p2.setCodigoProducto("PROD-002");
            p2.setNombre("Base de Maquillaje Larga Duración");
            p2.setDescripcion("Base de maquillaje de cobertura completa y larga duración");
            p2.setPrecio(35.50);
            p2.setStock(30);
            p2.setCategoria(categoriaMaquillaje);
            p2.setImagenUrl("/images/base-maquillaje.jpg");
            productoRepository.save(p2);

            Producto p3 = new Producto();
            p3.setCodigoProducto("PROD-003");
            p3.setNombre("Aceite Corporal Nutritivo");
            p3.setDescripcion("Aceite corporal nutritivo con aceite de almendras y vitamina E");
            p3.setPrecio(18.75);
            p3.setStock(40);
            p3.setCategoria(categoriaCorporal);
            p3.setImagenUrl("/images/aceite-corporal.jpg");
            productoRepository.save(p3);

            System.out.println("✅ Productos de ejemplo creados exitosamente");
        }

        // 3. Crear categorías de tratamientos
        if (categoriaTratamientoRepository.count() == 0) {
            CategoriaTratamiento cat1 = new CategoriaTratamiento();
            cat1.setNombre("Faciales");
            cat1.setDescripcion("Tratamientos especializados para el rostro");
            categoriaTratamientoRepository.save(cat1);

            CategoriaTratamiento cat2 = new CategoriaTratamiento();
            cat2.setNombre("Corporales");
            cat2.setDescripcion("Tratamientos para el cuerpo");
            categoriaTratamientoRepository.save(cat2);

            CategoriaTratamiento cat3 = new CategoriaTratamiento();
            cat3.setNombre("Depilación");
            cat3.setDescripcion("Servicios de depilación");
            categoriaTratamientoRepository.save(cat3);

            System.out.println("✅ Categorías de tratamientos creadas exitosamente");
        }

        // 4. Crear tratamientos de ejemplo
        if (tratamientoRepository.count() == 0) {
            CategoriaTratamiento catFacial = categoriaTratamientoRepository.findByNombre("Faciales").get();
            CategoriaTratamiento catCorporal = categoriaTratamientoRepository.findByNombre("Corporales").get();
            CategoriaTratamiento catDepilacion = categoriaTratamientoRepository.findByNombre("Depilación").get();

            Tratamiento t1 = new Tratamiento();
            t1.setCodigoTratamiento("TRAT-001");
            t1.setNombre("Limpieza Facial Profunda");
            t1.setDescripcion("Limpieza profunda del rostro con extracción de impurezas");
            t1.setPrecio(50.00);
            t1.setDuracionMinutos(60);
            t1.setCategoria(catFacial);
            tratamientoRepository.save(t1);

            Tratamiento t2 = new Tratamiento();
            t2.setCodigoTratamiento("TRAT-002");
            t2.setNombre("Masaje Relajante");
            t2.setDescripcion("Masaje corporal relajante con aceites esenciales");
            t2.setPrecio(75.00);
            t2.setDuracionMinutos(90);
            t2.setCategoria(catCorporal);
            tratamientoRepository.save(t2);

            Tratamiento t3 = new Tratamiento();
            t3.setCodigoTratamiento("TRAT-003");
            t3.setNombre("Depilación Láser Facial");
            t3.setDescripcion("Sesión de depilación láser para zona facial");
            t3.setPrecio(120.00);
            t3.setDuracionMinutos(45);
            t3.setCategoria(catDepilacion);
            tratamientoRepository.save(t3);

            System.out.println("✅ Tratamientos de ejemplo creados exitosamente");
        }

        // 5. Asegurar configuración de citas
        if (configuracionCitasRepository.count() == 0) {
            ConfiguracionCitas config = new ConfiguracionCitas();
            configuracionCitasRepository.save(config);
            System.out.println("✅ Configuración de citas creada exitosamente");
        }

        System.out.println("🎉 Todos los datos de prueba inicializados correctamente");
    }
}