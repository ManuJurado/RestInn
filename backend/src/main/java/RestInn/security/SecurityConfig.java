package RestInn.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired private UserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider())
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Archivos estáticos y frontend público
                        .requestMatchers(
                                "/", "/index.html", "/login.html", "/registro", "/recuperar-password",
                                "/detalleHabitacion.html",
                                "/api/habitaciones/reservables", "/api/imagenes/ver/**",
                                "/css/**", "/js/**", "/images/**", "/favicon.ico",
                                "/api/usuarios/**", "/api/habitaciones/{id:[0-9]+}","/home.html","/reservas.html"
                        ).permitAll()

                        // Auth y rutas públicas de API
                        .requestMatchers("/api/auth/login", "/api/public/**").permitAll()

                        // Usuarios autenticados
                        .requestMatchers("/api/usuarios/current", "/api/reservas/mis-reservas").authenticated()

                        // Reservas
                        .requestMatchers("/api/reservas").hasAnyRole("CLIENTE","RECEPCIONISTA","CONSERJE","LIMPIEZA","ADMINISTRADOR")
                        .requestMatchers("/api/reservas/{id}").hasRole("CLIENTE")
                        .requestMatchers("/api/reservas/checkin/**").hasAnyRole("RECEPCIONISTA","ADMINISTRADOR")
                        .requestMatchers("/api/reservas/checkout/**").hasAnyRole("RECEPCIONISTA","ADMINISTRADOR")
                        .requestMatchers("/api/reservas").hasAnyRole("RECEPCIONISTA","ADMINISTRADOR")

                        // Otras rutas por rol
                        .requestMatchers("/api/empleados/limpieza/**").hasAnyRole("LIMPIEZA","ADMINISTRADOR")
                        .requestMatchers("/api/empleados/conserjeria/**").hasAnyRole("CONSERJE","ADMINISTRADOR")
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/clientes/**").hasRole("CLIENTE")

                        // Tdo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.disable());

        return http.build();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://restinn.sytes.net"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
