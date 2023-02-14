package br.com.apssystem.algafood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class AlgafoodApiApplication {

    public static void main(String[] args) {
    	log.info("Iniciando a api algfood");
        SpringApplication.run(AlgafoodApiApplication.class, args);
        log.info("API algfood iniciada e pronta para receber requisições");
    }

}
