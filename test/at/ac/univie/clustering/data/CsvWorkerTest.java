package at.ac.univie.clustering.data;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import org.junit.Assert;

public class CsvWorkerTest {

    private static final String CSV_FILE_NO_HEADER = "test/resources/4d_noheader.csv";
    private static final String CSV_FILE_HEADER = "test/resources/4d_header.csv";
    private static final String CSV_FILE_NO_HEADER_CHAR = "test/resources/char_value.csv";
    private static final String CSV_FILE_NO_HEADER_NULL = "test/resources/null_value.csv";

    @Test
    public void testGetRecords() {
        CsvWorker csv = null;
        try {
            csv = new CsvWorker(CSV_FILE_NO_HEADER, ';', ',', false);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
        assertEquals(2, csv.getnTuple(), 0);
    }

    @Test
    public void testGetRecordsHeader() {
        CsvWorker csv = null;
        try {
            csv = new CsvWorker(CSV_FILE_HEADER, ';', ',', true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
        assertEquals(2, csv.getnTuple(), 0);
    }

    @Test
    public void testGetDimensions() {
        CsvWorker csv = null;
        try {
            csv = new CsvWorker(CSV_FILE_NO_HEADER, ';', ',', false);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
        assertEquals(4, csv.getDimension(), 0);
    }

    @Test
    public void testGetDimensionsHeader() {
        CsvWorker csv = null;
        try {
            csv = new CsvWorker(CSV_FILE_HEADER, ';', ',', true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
        assertEquals(4, csv.getDimension(), 0);
    }

    @Test
    public void testReadTuple() throws IOException, ParseException {
        CsvWorker csv = null;
        try {
            csv = new CsvWorker(CSV_FILE_NO_HEADER, ';', ',', false);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        double[] x = new double[]{0.1, 0.2, 0.3, 0.4};
        assertArrayEquals(x, csv.readTuple(), 0);
        assertEquals(1, csv.getCurPosition(), 0);

        x = new double[]{0.5, 0.6, 0.7, 0.8};
        assertArrayEquals(x, csv.readTuple(), 0);
        assertEquals(2, csv.getCurPosition(), 0);
    }

    @Test
    public void testReadTupleHeader() throws IOException, ParseException {
        CsvWorker csv = null;
        try {
            csv = new CsvWorker(CSV_FILE_HEADER, ';', ',', true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        double[] x = new double[]{0.1, 0.2, 0.3, 0.4};
        assertArrayEquals(x, csv.readTuple(), 0);
        assertEquals(1, csv.getCurPosition(), 0);

        x = new double[]{0.5, 0.6, 0.7, 0.8};
        assertArrayEquals(x, csv.readTuple(), 0);
        assertEquals(2, csv.getCurPosition(), 0);
    }

    @Test
    public void testReadTupleReturnNull() throws IOException, ParseException {
        CsvWorker csv = null;
        try {
            csv = new CsvWorker(CSV_FILE_NO_HEADER, ';', ',', false);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        csv.readTuple();
        csv.readTuple();
        assertEquals(null, csv.readTuple());
    }

    @Test(expected = ParseException.class)
    public void testReadTupleChar() throws IOException, ParseException {
        CsvWorker csv = null;
        try {
            csv = new CsvWorker(CSV_FILE_NO_HEADER_CHAR, ';', ',', false);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        csv.readTuple();
        csv.readTuple();
    }

    @Test(expected = ParseException.class)
    public void testReadTupleNull() throws IOException, ParseException {
        CsvWorker csv = null;
        try {
            csv = new CsvWorker(CSV_FILE_NO_HEADER_NULL, ';', ',', false);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        csv.readTuple();
        csv.readTuple();
    }

    @Test
    public void testFileNotExisting() {
        CsvWorker csv = null;
        try {
            csv = new CsvWorker("notExistingFile.csv", ';', ',', false);
            Assert.fail();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
