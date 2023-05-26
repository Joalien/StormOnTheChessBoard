package fr.kubys;

import fr.kubys.repository.ChessBoardRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ChessBoardRepositoryImpl.class) // FIXME Try to generify me
public class StormOnTheChessBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(StormOnTheChessBoardApplication.class, args);
    }
}