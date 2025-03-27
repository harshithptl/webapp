package com.example.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.webapp.dao")
@EnableAspectJAutoProxy
@EntityScan(basePackages = "com.example.webapp.model")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
