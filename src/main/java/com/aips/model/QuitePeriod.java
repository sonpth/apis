package com.aips.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@Data
public class QuitePeriod {
    private LocalDateTime start;
    private LocalDateTime end;
    private int total;
}
