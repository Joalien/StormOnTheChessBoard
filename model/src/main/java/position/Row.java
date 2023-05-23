package position;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum Row implements Comparable<Row> {
    One(1, "1"),
    Two(2, "2"),
    Three(3, "3"),
    Four(4, "4"),
    Five(5, "5"),
    Six(6, "6"),
    Seven(7, "7"),
    Height(8, "8");

    private final Integer rowNumber;
    private final String rowName;

    Row(Integer rowNumber, String rowName) {
        this.rowNumber = rowNumber;
        this.rowName = rowName;
    }

    public Optional<Row> next() {
        return Arrays.stream(Row.values())
                .filter(row -> row.getRowNumber() == this.getRowNumber() + 1)
                .findFirst();
    }

    public Optional<Row> previous() {
        return Arrays.stream(Row.values())
                .filter(row -> row.getRowNumber() == this.getRowNumber() - 1)
                .findFirst();
    }

    public static Row fromNumber(int rowNumber) {
        return Arrays.stream(Row.values())
                .filter(row -> row.getRowNumber() == rowNumber)
                .findFirst()
                .orElseThrow(IndexOutOfBoundsException::new);
    }
}
