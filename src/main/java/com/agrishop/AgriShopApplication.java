package com.agrishop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AgriShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgriShopApplication.class, args);
    }
}