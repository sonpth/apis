package com.aips.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Builder
@Getter
public class BatchOutput {
    private int total;
    private List<DataEntry> top;
    private QuitePeriod quitePeriod;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Total cars seen: ").append(total).append("\n")
                .append("Top busiest periods:").append(top).append("\n")
                .append("Quite period: ").append(quitePeriod);

        return sb.toString();
    }
}
