package com.wiredbraincoffee.productapifunctional;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.wiredbraincoffee.productapifunctional.handler.ProductHandler;
import com.wiredbraincoffee.productapifunctional.model.Product;
import com.wiredbraincoffee.productapifunctional.repository.ProductRepository;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class ProductApiFunctionalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApiFunctionalApplication.class, args);
	}
	
	@Bean
	CommandLineRunner init(ReactiveMongoOperations operations, ProductRepository productRepository) {
		return args -> {
			Flux<Product> productFlux = Flux.just(new Product(null, "Big Latte", 2.99),
					new Product(null, "Big Decaf", 2.49),
					new Product(null, "Green Tea", 1.99))
			.flatMap(productRepository::save);
			
			productFlux.thenMany(productRepository.findAll())
					.subscribe(System.out::println);
		};
	}
	
	@Bean
	RouterFunction<ServerResponse> routes(ProductHandler handler) {
		return nest(path("/products"), 
				nest(accept(MediaType.APPLICATION_JSON)
				.or(contentType(MediaType.APPLICATION_JSON))
				.or(accept(MediaType.TEXT_EVENT_STREAM)), 
						route(GET("/"), handler::getAllProducts)
						.andRoute(POST("/"), handler::saveProduct)
						.andRoute(GET("/events"), handler::getProductEvents)
						.andNest(path("/{id}"), 
								route(GET("/"), handler::getProductById)
								.andRoute(PUT("/"), handler::updateProduct)
								.andRoute(DELETE("/"), handler::deleteProduct))));
		
		/* OUTRA ALTERNATIVA PARA O CÓDIGO ACIMA:
		return RouterFunctions.route(GET("/products").and(accept(MediaType.APPLICATION_JSON)), handler::getAllProducts)
				.andRoute(POST("/products").and(contentType(MediaType.APPLICATION_JSON)), handler::saveProduct)
				.andRoute(DELETE("/products").and(accept(MediaType.APPLICATION_JSON)), handler::deleteAllProducts)
				.andRoute(GET("/products/events").and(accept(MediaType.TEXT_EVENT_STREAM)), handler::getProductEvents)
				.andRoute(GET("/products/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::getProductById)
				.andRoute(PUT("/products/{id}").and(contentType(MediaType.APPLICATION_JSON)), handler::updateProduct);
		*/
	}

}
