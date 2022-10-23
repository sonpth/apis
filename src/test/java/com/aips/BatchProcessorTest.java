package com.aips;

import com.aips.model.BatchOutput;
import com.aips.model.DataEntry;
import com.aips.model.QuitePeriod;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.aips.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class BatchProcessorTest {
    private BatchProcessor harness = new BatchProcessor();

    @Test
    public void testProvidedTestCase() throws IOException {
        String output = "target/provided.out.txt";
        BatchOutput bo = harness.process("src/test/resources/provided.txt", output);
        assertEquals(398, bo.getTotal());
        DataEntry entry;

        entry = bo.getTop().get(0);
        assertEquals("2021-12-01T07:30:00", format(entry.getTimestamp()));
        assertEquals(46, entry.getTotal());

        entry = bo.getTop().get(1);
        assertEquals("2021-12-01T08:00:00", format(entry.getTimestamp()));
        assertEquals(42, entry.getTotal());

        entry = bo.getTop().get(2);
        assertEquals("2021-12-08T18:00:00", format(entry.getTimestamp()));
        assertEquals(33, entry.getTotal());

        assertTrue(compare("src/test/resources/provided.expected.txt", output));
    }

    @Test
    public void testSingleDayTestCase() throws IOException {
        String output = "target/one_day.out.txt";
        BatchOutput bo = harness.process("src/test/resources/one_day.txt", output);
        assertEquals(91, bo.getTotal());

        assertTrue(compare("src/test/resources/one_day.expected.txt", output));
    }

    @Test
    public void testThreeDaysTestCase() throws IOException {
        // given
        String output = "target/three_whole_day.out.txt";

        // when
        BatchOutput bo = harness.process("src/test/resources/three_whole_day.txt", output);

        // then
        assertEquals(6694, bo.getTotal());
        assertEquals(102, bo.getTop().get(0).getTotal());
        assertEquals(101, bo.getTop().get(1).getTotal());
        assertEquals(100, bo.getTop().get(2).getTotal());
        assertEquals("2022-06-02T13:30", bo.getQuitePeriod().getStart().toString());
        assertEquals("2022-06-02T14:30", bo.getQuitePeriod().getEnd().toString());

        assertTrue(compare("src/test/resources/three_whole_day.expected.txt", output));
    }

    @Test
    public void testGetQuitePeriod_allEntries(){
        // given
        List<DataEntry> prev = new ArrayList<>(Arrays.asList(
                new DataEntry("2021-12-01T06:00:00", 5),
                new DataEntry("2021-12-01T06:30:00", 1)
        ));
        QuitePeriod quitePeriod;

        // when
        quitePeriod = harness.getQuitePeriod(prev, new DataEntry("2021-12-01T07:00:00", 2));
        assertEquals(8, quitePeriod.getTotal());

        // when
        quitePeriod = harness.getQuitePeriod(prev, new DataEntry("2021-12-01T07:30:00", 4));
        // then
        assertEquals(7, quitePeriod.getTotal());
        assertEquals("2021-12-01T06:30:00", format(quitePeriod.getStart()));
        assertEquals("2021-12-01T07:30:00", format(quitePeriod.getEnd()));
    }

    @Test
    public void testGetQuitePeriod_bottomGap(){
        // given
        List<DataEntry> prev = new ArrayList<>(Arrays.asList(
                new DataEntry("2021-12-01T06:00:00", 5),
                new DataEntry("2021-12-01T06:30:00", 1)
        ));

        QuitePeriod quitePeriod;

        // when
        quitePeriod = harness.getQuitePeriod(prev, new DataEntry("2021-12-01T07:00:00", 2));
        // then
        assertEquals(8, quitePeriod.getTotal());
        assertEquals("2021-12-01T06:00:00", format(quitePeriod.getStart()));
        assertEquals("2021-12-01T07:00:00", format(quitePeriod.getEnd()));

        // when
        quitePeriod = harness.getQuitePeriod(prev, new DataEntry("2021-12-01T08:00:00", 1));
        // then
        assertNull(quitePeriod);
    }

    @Test
    public void testGetQuitePeriod_topGap(){
        // given
        QuitePeriod quitePeriod = null;
        List<DataEntry> prev = new ArrayList<>();

        // when
        quitePeriod = harness.getQuitePeriod(prev, new DataEntry("2021-12-01T05:00:00", 1));
        quitePeriod = harness.getQuitePeriod(prev, new DataEntry("2021-12-01T06:30:00", 1));
        quitePeriod = harness.getQuitePeriod(prev, new DataEntry("2021-12-01T07:00:00", 2));
        quitePeriod = harness.getQuitePeriod(prev, new DataEntry("2021-12-01T07:30:00", 10));

        // then
        assertEquals(13, quitePeriod.getTotal());
        assertEquals("2021-12-01T06:30:00", format(quitePeriod.getStart()));
        assertEquals("2021-12-01T07:30:00", format(quitePeriod.getEnd()));
    }


    @Test
    public void testGetQuitePeriod_nonExistent(){
        // given
        QuitePeriod quitePeriod = null;
        List<DataEntry> prev = new ArrayList<>();

        quitePeriod = harness.getQuitePeriod(prev, new DataEntry("2021-11-30T04:30:00", 1));
        assertNull(quitePeriod);

        quitePeriod = harness.getQuitePeriod(prev, new DataEntry("2021-12-01T05:00:00", 2));
        assertNull(quitePeriod);

        quitePeriod = harness.getQuitePeriod(prev, new DataEntry("2021-12-01T05:30:00", 2));
        assertNull(quitePeriod);

        quitePeriod = harness.getQuitePeriod(prev, new DataEntry("2021-12-01T07:00:00", 1));
        assertNull(quitePeriod);
    }

    public static void main(String[] args) throws  Exception{
        TestUtils.generateData("whole_ten_year", 3650);
    }
}