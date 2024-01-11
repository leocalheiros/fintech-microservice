package io.github.leocalheiros.mscartoes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EnableRabbit
@Slf4j
public class MscartoesApplication {

	public static void main(String[] args) {
		log.info("Informação: {}", "teste info");
		log.error("Error: {}", "teste error");
		log.warn("Error: {}", "teste warn");
		SpringApplication.run(MscartoesApplication.class, args);
	}

}
