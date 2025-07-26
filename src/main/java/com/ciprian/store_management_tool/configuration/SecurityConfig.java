package com.ciprian.store_management_tool.configuration;

import com.ciprian.store_management_tool.exception.AccessDeniedStoreException;
import com.ciprian.store_management_tool.exception.AuthenticationStoreException;
import com.ciprian.store_management_tool.exception.StoreExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final HandlerExceptionResolver handlerExceptionResolver;

    public SecurityConfig(@Qualifier("handlerExceptionResolver")HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Value("${role.admin:ADMIN}")
    private String adminRole;

    @Value("${role.user:USER}")
    private String customerRole;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(requests -> requests
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/products").hasRole(adminRole)
                        .requestMatchers(HttpMethod.PATCH, "/products/**").hasRole(adminRole)
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole(adminRole)
                        .requestMatchers(HttpMethod.GET, "/products/**").hasAnyRole(adminRole, customerRole)
                        .anyRequest()
                        .denyAll()

                )
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                );

        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
        );

        return http.build();
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {

            AuthenticationStoreException storeException;
            if (authException.getMessage() != null && authException.getMessage().contains("expired")) {
                storeException = new AuthenticationStoreException(StoreExceptionType.TOKEN_EXPIRED);
            } else if (authException.getMessage() != null && authException.getMessage().contains("invalid")) {
                storeException = new AuthenticationStoreException(StoreExceptionType.TOKEN_INVALID);
            } else {
                storeException = new AuthenticationStoreException(StoreExceptionType.AUTHENTICATION_REQUIRED);
            }

            handlerExceptionResolver.resolveException(request, response, null, storeException);
        };
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
    }

}
