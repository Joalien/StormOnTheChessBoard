package fr.kubys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"fr.kubys"})
public class StormOnTheChessBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(StormOnTheChessBoardApplication.class, args);
    }
}