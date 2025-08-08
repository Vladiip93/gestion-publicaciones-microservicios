package com.espe.mspr.eurekaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Clase principal que arranca la aplicación Spring Boot.
 * La anotación @EnableEurekaServer convierte esta aplicación en un servidor de
 * descubrimiento de servicios de Eureka.
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServiceApplication.class, args);
    }

}