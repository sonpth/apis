package com.aips;

import com.aips.model.BatchOutput;
import com.aips.model.DataEntry;
import com.aips.model.QuitePeriod;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class BatchProcessor {
    private static final int TOP_SIZE = 3;        // The number of top busier unit we are interested in
    private static final int CONTINUOUS_SIZE = 3; // The required number of valid continuous entries to make up a valid period.
    public static final String DATA_ENTRY_PATTERN = "%s %d\n";

    public static void main(String[] args) throws Exception {
        BatchProcessor processor = new BatchProcessor();
        long start = System.currentTimeMillis();
        System.out.println(processor.process(args[0], args[1]));
        System.out.println("Elapsed: " + (System.currentTimeMillis() - start) + " ms.");
    }

    /**
     * Run the process.
     *
     * @param input - the path to input file
     * @param output - the path to outputØ file
     * @return
     * @throws IOException
     */
    public BatchOutput process(String input, String output) throws IOException  {
        try (BufferedReader in = new BufferedReader(new FileReader(input));
                FileWriter out = new FileWriter(output)){
            String line;
            PriorityQueue<DataEntry> pq = new PriorityQueue<>((e1, e2) -> e1.getTotal() - e2.getTotal());
            int total  = 0; // total cars seen since the beginning of the file.
            int currentDateTotal = 0; // total cars seen since the beginning of the current date.
            LocalDate currentDate = null;
            List<DataEntry> continuous = new LinkedList<>();
            QuitePeriod currentQuitePeriod = QuitePeriod.builder().total(Integer.MAX_VALUE).build();

            while ((line = in.readLine()) != null) {
                String[] buf = line.split(" ");
                DataEntry entry = new DataEntry(buf[0], Integer.valueOf(buf[1]));
                pq.add(entry);
                if (pq.size() > TOP_SIZE) {
                    pq.remove();
                }

                final LocalDate entryLocalDate = entry.getTimestamp().toLocalDate();
                if (currentDate == null) {
                    currentDate = entryLocalDate;
                } else if (!currentDate.equals(entryLocalDate)) {
                    /* new date */
                    out.write(String.format(DATA_ENTRY_PATTERN, currentDate, currentDateTotal));
                    currentDate = entryLocalDate;
                    currentDateTotal = 0;
                }

                currentDateTotal += entry.getTotal();
                total += entry.getTotal();

                QuitePeriod quitePeriod = getQuitePeriod(continuous, entry);
                if (quitePeriod != null && quitePeriod.getTotal() < currentQuitePeriod.getTotal()) {
                    currentQuitePeriod = quitePeriod;
                }
            }

            out.write(String.format(DATA_ENTRY_PATTERN, currentDate, currentDateTotal));

            List<DataEntry> top = new ArrayList(pq);
            Collections.sort(top, (e1, e2) -> e2.getTotal() - e1.getTotal());

            return BatchOutput.builder()
                    .top(top)
                    .total(total)
                    .quitePeriod(currentQuitePeriod)
                    .build();
        }
    }

    /**
     * Given a list of data entries we saw before, and the latest entry, returns the quite period.
     *
     * @param continuous - a valid continuous entries
     * @param last - the last entry
     * @return a valid period, otherwise return {@code null}
     */
    protected QuitePeriod getQuitePeriod(List<DataEntry> continuous, DataEntry last) {
        if (!continuous.isEmpty()
                && ChronoUnit.MINUTES.between(continuous.get(continuous.size() - 1).getTimestamp(), last.getTimestamp()) != 30) {
            continuous.clear();
        }

        continuous.add(last);
        if (continuous.size() > CONTINUOUS_SIZE) {
            continuous.remove(0);
        } else if (continuous.size() < CONTINUOUS_SIZE) {
            return null;
        }

        int sum = continuous.stream().map(e -> e.getTotal()).reduce(0, Integer::sum);
        return QuitePeriod.builder()
                .start(continuous.get(0).getTimestamp())
                .end(continuous.get(continuous.size() - 1).getTimestamp())
                .total(sum)
                .build();
    }
}
