import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class KnightIT {

    @Test
    void should_be_able_to_jump_over_pieces() {
        Knight knight = new Knight("a1", Color.WHITE);
        new Knight("a2", Color.BLACK);
        new Knight("b2", Color.BLACK);
        new Knight("b1", Color.BLACK);

        assertTrue(knight.nothingOnThePath("c2"));
        assertTrue(knight.nothingOnThePath("b3"));
    }
}
