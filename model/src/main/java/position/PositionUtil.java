package position;

import java.util.ArrayList;
import java.util.List;

public class PositionUtil {

    public static List<String> generateAllPositions() {
        List<String> allSquares = new ArrayList<>(64);
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                allSquares.add(posToSquare(i, j));
            }
        }
        return allSquares;
    }

    public static String posToSquare(int i, int j) {
        return Character.toString(PositionUtil.intToChar(i)) + intToInt(j);
    }

    private static char intToChar(int i) {
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

    private static int intToInt(int i) {
        if (i < 1 || i > 8)
            throw new IndexOutOfBoundsException(i);
        return i;
    }

    public static int getX(String position) {
        return PositionUtil.charToInt(position.charAt(0));
    }

    private static int charToInt(char c) {
        return switch (Character.toLowerCase(c)) {
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

    public static int getY(String position) {
        return Integer.valueOf(Character.toString(position.charAt(1)));
    }

}
