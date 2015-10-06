package at.ac.univie.clustering.data;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import at.ac.univie.clustering.data.CsvWorker;

import org.junit.Assert;

public class CsvWorkerTest {
	
	private static final String CSV_FILE = "test/resources/4_col_no_h.csv";
	private static final String CSV_FILE_HEADER = "test/resources/4_col_h.csv";
	private static final String CSV_FILE_CHAR = "test/resources/char_value.csv";
	private static final String CSV_FILE_NULL = "test/resources/null_value.csv";
	private static final String CSV_FILE_DIM = "test/resources/wrong_dim.csv";
	
	@Test
	public void testGetRecords() {
		CsvWorker csv = null;
		try {
			csv = new CsvWorker(CSV_FILE, ';', false);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		assertEquals(2, csv.getRecords(), 0);
	}
	
	@Test
	public void testGetRecordsHeader() {
		CsvWorker csv = null;
		try {
			csv = new CsvWorker(CSV_FILE_HEADER, ';', true);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		assertEquals(2, csv.getRecords(), 0);
	}

	@Test
	public void testGetDimensions() {
		CsvWorker csv = null;
		try {
			csv = new CsvWorker(CSV_FILE, ';', false);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		assertEquals(4, csv.getDimensions(), 0);
	}
	
	@Test
	public void testGetDimensionsHeader() {
		CsvWorker csv = null;
		try {
			csv = new CsvWorker(CSV_FILE_HEADER, ';', true);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		assertEquals(4, csv.getDimensions(), 0);
	}
	
	@Test
	public void testReadTuple() {
		CsvWorker csv = null;
		try {
			csv = new CsvWorker(CSV_FILE, ';', false);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		float[] x = new float[]{0.1F,0.2F,0.3F,0.4F};
		assertArrayEquals(x, csv.readTuple(), 0);
		assertEquals(1, csv.getCurPosition(), 0);
		
		x = new float[]{0.5F,0.6F,0.7F,0.8F};
		assertArrayEquals(x, csv.readTuple(), 0);
		assertEquals(2, csv.getCurPosition(), 0);
	}
	
	@Test
	public void testReadTupleHeader() {
		CsvWorker csv = null;
		try {
			csv = new CsvWorker(CSV_FILE_HEADER, ';', true);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		float[] x = new float[]{0.1F,0.2F,0.3F,0.4F};
		assertArrayEquals(x, csv.readTuple(), 0);
		assertEquals(1, csv.getCurPosition(), 0);
		
		x = new float[]{0.5F,0.6F,0.7F,0.8F};
		assertArrayEquals(x, csv.readTuple(), 0);
		assertEquals(2, csv.getCurPosition(), 0);
	}
	
	@Test
	public void testReadTupleReturnNull() {
		CsvWorker csv = null;
		try {
			csv = new CsvWorker(CSV_FILE, ';', false);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		csv.readTuple();
		csv.readTuple();
		assertEquals(null, csv.readTuple());
	}
	
	@Test(expected=NumberFormatException.class)
	public void testReadTupleChar() {
		CsvWorker csv = null;
		try {
			csv = new CsvWorker(CSV_FILE_CHAR, ';', false);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		csv.readTuple();
		csv.readTuple();
	}
	
	@Test(expected=NumberFormatException.class)
	public void testReadTupleNull() {
		CsvWorker csv = null;
		try {
			csv = new CsvWorker(CSV_FILE_NULL, ';', false);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		csv.readTuple();
		csv.readTuple();
	}

}
