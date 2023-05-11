package apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private static final String[] PERMIT_ROUTE_LIST = {
            "/api-docs/**",
            "/services/*/api-docs",
    };

    @Bean
    @Order(1)
    public SecurityWebFilterChain securityApiExchanges(
            ServerHttpSecurity serverHttpSecurity
    ) {
        serverHttpSecurity
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/services/*/api/**"))
                .authorizeExchange(exchange -> exchange.anyExchange().authenticated())
                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);
        return serverHttpSecurity.build();
    }

    @Bean
    @Order(2)
    public SecurityWebFilterChain securityActuatorExchanges(
            ServerHttpSecurity serverHttpSecurity,
            ReactiveAuthenticationManager reactiveAuthenticationManager
    ) {
        serverHttpSecurity
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/actuator/**"))
                .authorizeExchange(exchange -> exchange.anyExchange().authenticated())
                .authenticationManager(reactiveAuthenticationManager)
                .httpBasic();
        return serverHttpSecurity.build();
    }

    @Bean
    @Order(3)
    public SecurityWebFilterChain securityOpenApiExchanges(
            ServerHttpSecurity serverHttpSecurity,
            ReactiveAuthenticationManager reactiveAuthenticationManager
    ) {
        serverHttpSecurity
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/webjars/**"))
                .authorizeExchange(exchange -> exchange.anyExchange().authenticated())
                .authenticationManager(reactiveAuthenticationManager)
                .httpBasic();
        return serverHttpSecurity.build();
    }

    @Bean
    @Order(4)
    public SecurityWebFilterChain securityAnyExchange(
            ServerHttpSecurity serverHttpSecurity
    ) {
        serverHttpSecurity
                .authorizeExchange()
                .pathMatchers(PERMIT_ROUTE_LIST)
                .permitAll()
                .anyExchange()
                .authenticated();
        return serverHttpSecurity.build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return new UserDetailsRepositoryReactiveAuthenticationManager(getInMemoryUserDetails());
    }

    @Bean
    public MapReactiveUserDetailsService getInMemoryUserDetails() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin")
                .roles("ADMIN")
                .build();
        return new MapReactiveUserDetailsService(admin);
    }
}
