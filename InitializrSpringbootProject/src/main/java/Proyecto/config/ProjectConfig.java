package Proyecto.config;

/**
 *
 * @author darry
 */
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ProjectConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/auth/acceso-denegado").setViewName("auth/acceso-denegado");
        registry.addViewController("/auth/login").setViewName("auth/login");
    }
}
