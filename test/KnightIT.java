import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class KnightIT {

    @Test
    void should_be_able_to_jump_over_pieces() {
        Knight knight = new Knight(Color.WHITE);
        new Knight(Color.BLACK);
        new Knight(Color.BLACK);
        new Knight(Color.BLACK);

        assertTrue(knight.squaresOnThePath("c2").isEmpty());
        assertTrue(knight.squaresOnThePath("b3").isEmpty());
    }
}
