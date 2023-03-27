import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BoardUtil {

    public static int charToInt(char c) {
        return switch (c) {
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            default -> throw new IndexOutOfBoundsException(c);
        };
    }
     public static char intToChar(int i) {
        return switch (i) {
            case 1 -> 'a';
            case 2 -> 'b';
            case 3 -> 'c';
            case 4 -> 'd';
            case 5 -> 'e';
            case 6 -> 'f';
            case 7 -> 'g';
            case 8 -> 'h';
            default -> throw new IndexOutOfBoundsException(i);
        };
    }

    public static List<String> generateAllSquares() {
        List<String> allSquares = new ArrayList<>(64);
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <=8 ; j++) {
                allSquares.add(Character.toString(BoardUtil.intToChar(i)) + j);
            }
        }
        return allSquares;
    }

}
