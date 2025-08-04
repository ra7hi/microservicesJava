package microservices.order_processing.order_service.config;

import lombok.RequiredArgsConstructor;
import microservices.order_processing.order_service.jwt.JwtAuthenticationFilter;
import microservices.order_processing.order_service.services.CustomUserDetailsServiceImp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsServiceImp customUserDetailsServiceImp;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final OpenApiConfig openApiConfig;

    /**
     * Шифратор паролей, использующий BCrypt.
     *
     * @return экземпляр {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Менеджер аутентификации, используемый Spring Security.
     *
     * @param authConfig конфигурация аутентификации
     * @return {@link AuthenticationManager}
     * @throws Exception при ошибке создания менеджера
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Фильтр аутентификации по JWT, добавляется в цепочку фильтров.
     *
     * @return экземпляр {@link JwtAuthenticationFilter}
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    /**
     * Конфигурация провайдера аутентификации с использованием
     * кастомного сервиса пользователей и BCrypt.
     *
     * @return {@link DaoAuthenticationProvider}
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsServiceImp);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling((exception) ->
                        exception.authenticationEntryPoint(unauthorizedHandler)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests((authorizeRequest) ->
                        authorizeRequest
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/**",
                                        "/swagger-resources/**",
                                        "/swagger-ui.html",
                                        "/webjars/**"
                                ).permitAll()
                                .requestMatchers("/api/order/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/api/users/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                );
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
