package config;

/**
 *
 * @author darry
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso público a estos recursos
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/auth/**", "/productos/**", "/tratamientos/**", "/servicios", "/nosotros", "/contacto").permitAll()
                // Restringir panel admin solo a administradores
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                // El resto requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .defaultSuccessUrl("/")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            // Deshabilitar CSRF temporalmente para desarrollo
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
