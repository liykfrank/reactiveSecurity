package com.example.securitydemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import sun.misc.Request;

@SpringBootApplication
public class SecurityDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityDemoApplication.class, args);
	}
}

@Configuration
class WebConfiguration{

	@Bean
	RouterFunction<?> routes(){
		return RouterFunctions.route(RequestPredicates.GET("/message"), new HandlerFunction<ServerResponse>(){

			@Override
			public Mono<ServerResponse> handle(ServerRequest serverRequest){
				return ServerResponse.ok().body(Mono.just("Hello World!"),String.class);
			}

		});
	}
}