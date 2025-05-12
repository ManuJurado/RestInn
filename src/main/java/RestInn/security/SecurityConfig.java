package RestInn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1) deshabilitar CSRF (sólo para desarrollo / API sin cookies)
                .csrf(AbstractHttpConfigurer::disable)

                // 2) autorización de rutas
                .authorizeHttpRequests(auth -> auth
                        // permite tod o en /reservas/**
                        .requestMatchers("/reservas/**").permitAll()
                        // para cualquier otra ruta, requiere autenticación
                        .anyRequest().authenticated()
                )

                // 3) activar HTTP Basic (para poder hacer peticiones autenticadas si se requiere)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
