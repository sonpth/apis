package com.aips.model;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Getter
@Data
public class DataEntry {
    private LocalDateTime timestamp;
    private int total;

    public DataEntry(String date, int total) {
        this.timestamp = LocalDateTime.parse(date, ISO_LOCAL_DATE_TIME);
        this.total = total;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("\n\t");
        sb.append(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(timestamp))
                .append(" ")
                .append(total);

        return sb.toString();
    }
}
