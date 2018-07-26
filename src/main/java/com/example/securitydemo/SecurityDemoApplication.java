package com.example.securitydemo;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;
/**
 * http://www.baeldung.com/spring-security-5-reactive
 * @author frank
 *
 */

@SpringBootApplication
public class SecurityDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityDemoApplication.class, args);
	}
}

@Configuration
class WebConfiguration {

	Mono<ServerResponse> message(ServerRequest serverRequest) {
		Mono<String> publisedUser = serverRequest.principal().map(p -> "Hello, " + p.getName() + " !");
		return ServerResponse.ok().body(publisedUser, String.class);
	}

	Mono<ServerResponse> userName(ServerRequest serverRequest) {
		Mono<UserDetails> detailsMono = serverRequest.principal()
				.map(p -> UserDetails.class.cast(Authentication.class.cast(p).getPrincipal()));
		return ServerResponse.ok().body(detailsMono, UserDetails.class);
	}

	@Bean
	RouterFunction<?> routes() {
		return route(GET("/message"), this::message).andRoute(GET("/users/{userName}"), this::userName);
	}

	/*
	 * @Bean RouterFunction<?> routes() { return
	 * RouterFunctions.route(RequestPredicates.GET("/message"), this::message); }
	 */

}

// @Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {

	// @SuppressWarnings("deprecation")
	// @Bean
	// public static NoOpPasswordEncoder passwordEncoder() {
	// return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	// }
	// Mono<ServerResponse> message(ServerRequest serverRequest) {
	// return ServerResponse.ok().body(Mono.just("Hello World!"), String.class);
	// }

	@Bean
	public MapReactiveUserDetailsService userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder().username("frank").password("frank").roles("USER").build();

		UserDetails mike = User.withDefaultPasswordEncoder().username("mike").password("mike").roles("USER").build();

		return new MapReactiveUserDetailsService(user, mike);
	}
	// @Bean
	// ReactiveUserDetailsService userDetailsRepository() {
	// UserDetails frank =
	// User.withUsername("frank").roles("User").password("frank").build();
	// UserDetails mike =
	// User.withUsername("mike").roles("User").password("mike").build();
	// return new MapReactiveUserDetailsService(frank,mike);
	// }

	@Bean
	public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
		return http.authorizeExchange().pathMatchers("/users/{userName}")
				.access(new ReactiveAuthorizationManager<AuthorizationContext>() {
					@Override
					public Mono<AuthorizationDecision> check(Mono<Authentication> mono,
							AuthorizationContext context) {
						String uname = (String) context.getVariables().get("userName");
//						return null;
						return mono.map(auth -> auth.getName().equals(uname))
								.map(AuthorizationDecision::new);

					}
				}).anyExchange().authenticated().and().formLogin().and().build();
	}

	// @Bean
	// SecurityWebFilterChain security(HttpSecurity security) {
	// return security.authorizeExchange().
	//
	// }

	/*
	 * @Bean RouterFunction<?> routes() { return
	 * RouterFunctions.route(RequestPredicates.GET("/message"), this::message); }
	 */

}