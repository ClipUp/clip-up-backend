package potenday.backend.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import potenday.backend.application.AuthService;
import potenday.backend.support.exception.ApplicationException;
import potenday.backend.support.exception.ErrorCode;
import potenday.backend.support.response.ApiResponse;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
class SecurityConfig {

    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/admin/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/", "/actuator/**")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/*/auth/email/**", "/api/*/auth/register", "/api/*/auth/login", "/api/*/auth/token")
                .permitAll()
                .anyRequest()
                .authenticated())
            .addFilterBefore(new AuthFilter(authService), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint()))
            .build();
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            ApplicationException exception = ErrorCode.UNAUTHORIZED.toException();
            ApiResponse<?> apiResponse = ApiResponse.of(exception);
            response.setStatus(apiResponse.status().value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        };
    }

    private CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());

        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            String origin = request.getHeader("Origin");

            if (origin != null) {
                config.setAllowedOrigins(List.of(origin));
            }

            config.setAllowedMethods(List.of("HEAD", "POST", "GET", "DELETE", "PUT"));
            config.setAllowedHeaders(List.of("*"));
            config.setAllowCredentials(true);

            return config;
        };
    }

}
