package potenday.backend.infra;

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
import potenday.backend.support.ApiResponse;
import potenday.backend.support.ApplicationException;
import potenday.backend.support.ErrorCode;

import java.util.Arrays;
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
                .requestMatchers("/test/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/", "/actuator/**")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/*/auth/register", "/api/*/auth/login", "/api/*/auth/token")
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
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
