package fr.kubys.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class HistoryEntryDto {
    private final int index;
    private final String color;
    private final String action;
    private final String instant;
}
