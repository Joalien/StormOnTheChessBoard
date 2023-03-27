import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KnightIT {

    @Test
    void should_be_able_to_jump_over_pieces() {
        Knight knight = new Knight(1, 1, true, 'B');
        new Knight(1, 2, false, 'b');
        new Knight(2, 1, false, 'b');
        new Knight(2, 2, false, 'b');

        Assertions.assertTrue(knight.nothingOnThePath("c2"));
        Assertions.assertTrue(knight.nothingOnThePath("b3"));
    }
}
