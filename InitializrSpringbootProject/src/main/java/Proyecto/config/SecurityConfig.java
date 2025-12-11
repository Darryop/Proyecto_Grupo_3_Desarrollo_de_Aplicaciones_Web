package Proyecto.config;

import Proyecto.model.Ruta;
import Proyecto.service.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {   

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Lazy RutaService rutaService) throws Exception {
        var rutas = rutaService.getRutas();
        
        http.authorizeHttpRequests(requests -> {
            // Rutas públicas - CORREGIDAS
            requests.requestMatchers(
                "/", 
                "/css/**", 
                "/js/**", 
                "/images/**", 
                "/webjars/**",  // ← AÑADIR ESTO
                "/auth/**", 
                "/productos/**",  // ← CAMBIAR de /products/** a /productos/**
                "/tratamientos/**", 
                "/servicios", 
                "/servicios/**",  // ← AÑADIR para cualquier subruta
                "/nosotros", 
                "/contacto", 
                "/error"
            ).permitAll();
            
            // Configurar rutas dinámicas desde la base de datos
            for (Ruta ruta : rutas) {
                if (ruta.getRequiereRol()) {
                    requests.requestMatchers(ruta.getRuta()).hasAuthority(ruta.getRol().getNombre());
                } else {
                    requests.requestMatchers(ruta.getRuta()).permitAll();
                }
            }
            
            // Rutas de administración
            requests.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN");
            
            // Cualquier otra ruta requiere autenticación
            requests.anyRequest().authenticated();
        });

        http.formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
        ).logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        ).exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/auth/acceso-denegado")
        ).sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );
        
        // Temporal para desarrollo
        http.csrf(csrf -> csrf.disable());
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build, 
                               @Lazy PasswordEncoder passwordEncoder, 
                               @Lazy UserDetailsService userDetailsService) throws Exception {
        build.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}