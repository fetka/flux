package com.example.flux;

import static java.time.Duration.ofSeconds;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class FluxApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		long currentTimeMillis = System.currentTimeMillis() + 10000;
		ConnectableFlux<Object> publish = Flux.create(fluxSink -> {
					while(currentTimeMillis >= System.currentTimeMillis()) {
						fluxSink.next(System.currentTimeMillis());
					}
					System.out.println("****  end  *****");
				})
				.sample(ofSeconds(2))
				.publish();

		publish.subscribe(System.out::println);
//		publish.subscribe(System.out::println);
		publish.connect();
	}
}
