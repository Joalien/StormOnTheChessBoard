package position;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum File {
    A("a", 1),
    B("b", 2),
    C("c", 3),
    D("d", 4),
    E("e", 5),
    F("f", 6),
    G("g", 7),
    H("h", 8);

    private final String fileName;
    private final Integer fileNumber;

    File(String fileName, Integer fileNumber) {
        this.fileName = fileName;
        this.fileNumber = fileNumber;
    }

    public Optional<File> next() {
        return Arrays.stream(File.values())
                .filter(row -> row.getFileNumber() == this.getFileNumber() + 1)
                .findFirst();
    }

    public Optional<File> previous() {
        return Arrays.stream(File.values())
                .filter(row -> row.getFileNumber() == this.getFileNumber() - 1)
                .findFirst();
    }

    public static File fromNumber(int fileNumber) {
        return Arrays.stream(File.values())
                .filter(row -> row.getFileNumber() == fileNumber)
                .findFirst()
                .orElseThrow(IndexOutOfBoundsException::new);
    }
}
