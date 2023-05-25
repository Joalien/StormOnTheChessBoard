package fr.kubys.StormOnTheChessBoard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "fr.kubys.repository")
public class StormOnTheChessBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(StormOnTheChessBoardApplication.class, args);
    }
}