package at.ac.univie.bang.data;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import org.junit.Assert;

public class CsvWorkerTest {
	
	private static String CSV_FILE = "test/resources/4_col_no_h.csv";
	private static String CSV_FILE_HEADER = "test/resources/4_col_h.csv";

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
		
		int[] x = new int[]{1,2,3,4};
		assertArrayEquals(x, csv.readTuple());
		assertEquals(1, csv.getCurPosition(), 0);
		
		x = new int[]{5,6,7,8};
		assertArrayEquals(x, csv.readTuple());
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
		
		int[] x = new int[]{1,2,3,4};
		assertArrayEquals(x, csv.readTuple());
		assertEquals(1, csv.getCurPosition(), 0);
		
		x = new int[]{5,6,7,8};
		assertArrayEquals(x, csv.readTuple());
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

}
