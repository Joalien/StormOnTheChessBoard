package fr.kubys.core;



import java.util.Arrays;

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

    public static File fromNumber(int fileNumber) {
        return Arrays.stream(File.values())
                .filter(row -> row.getFileNumber() == fileNumber)
                .findFirst()
                .orElseThrow(IndexOutOfBoundsException::new);
    }

    public int distanceTo(File file) {
        return Math.abs(getFileNumber() - file.getFileNumber());
    }

    public boolean isBefore(File fil) {
        return this.compareTo(fil) < 0;
    }

    public String getFileName() {
        return this.fileName;
    }

    public Integer getFileNumber() {
        return this.fileNumber;
    }
}
