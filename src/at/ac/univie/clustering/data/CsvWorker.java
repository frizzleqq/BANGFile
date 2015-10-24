package at.ac.univie.clustering.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import com.opencsv.CSVParser;

public class CsvWorker implements DataWorker {

	private String filename;
	private char delimiter;
	private boolean header;
	private int records = 0;
	private int dimensions = 0;
	
	private int current_position;
	
	private CSVParser csv;
	private BufferedReader br;

	/*
	 * public FileWorker(String filename) { this.filename = filename;
	 * this.delimiter = '#'; this.header = false; }
	 * 
	 * public FileWorker(String filename, char delimiter) { this.filename =
	 * filename; this.delimiter = delimiter; this.header = false; }
	 * 
	 * public FileWorker(String filename, boolean header) { this.filename =
	 * filename; this.delimiter = '#'; this.header = header; }
	 */

	/**
	 * @param filename
	 * @param delimiter
	 * @param header
	 * @throws IOException
	 */
	public CsvWorker(String filename, char delimiter, boolean header) throws IOException {
		this.filename = filename;
		this.delimiter = delimiter;
		this.header = header;

		if (!fileExists())
			throw new IOException("Could not find file with provided filename.");
		if (!fileReadable())
			throw new IOException("File with provided filename is not readable.");
		
		csv = new CSVParser(delimiter);
		br = new BufferedReader(new FileReader(filename));
		if(header)
			br.readLine();
		
		records = countRecords();
		dimensions = countDimensions();
	}

	/**
	 * @return
	 */
	private boolean fileExists() {
		File f = new File(filename);
		if (f.exists() && !f.isDirectory())
			return true;
		else
			return false;
	}

	/**
	 * @return
	 */
	private boolean fileReadable() {
		File f = new File(filename);

		// this always returns true in windows
		if (!f.canRead())
			return false;
		if (!Files.isReadable(FileSystems.getDefault().getPath(f.getAbsolutePath())))
			return false;

		return true;
	}

	/* (non-Javadoc)
	 * @see at.ac.univie.clustering.data.DataWorker#getRecords()
	 */
	@Override
	public int getRecords() {
		return records;
	}

	/**
	 * @return
	 */
	public int countRecords() {
		int linenumber = 0;
		try {
			File file = new File(filename);
			FileReader fr = new FileReader(file);
			LineNumberReader lnr = new LineNumberReader(fr);

			while (lnr.readLine() != null) {
				linenumber++;
			}
			lnr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (this.header && linenumber > 0)
			linenumber -= 1;
		return linenumber;
	}

	/* (non-Javadoc)
	 * @see at.ac.univie.clustering.data.DataWorker#getDimensions()
	 */
	@Override
	public int getDimensions() {
		return dimensions;
	}

	/**
	 * @return
	 */
	public int countDimensions() {
		int dimensions = 0;
		try {
			CSVParser csv = new CSVParser(delimiter);
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = "";
			line = br.readLine();
			if (header)
				line = br.readLine(); // maybe file has unusual header
			br.close();
			dimensions = csv.parseLine(line).length;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dimensions;
	}

	/* (non-Javadoc)
	 * @see at.ac.univie.clustering.data.DataWorker#getCurPosition()
	 */
	@Override
	public int getCurPosition() {
		return current_position;
	}

	/* (non-Javadoc)
	 * @see at.ac.univie.clustering.data.DataWorker#readTuple()
	 */
	@Override
	public float[] readTuple() {
		if (current_position >= records)
			return null;
		
		float[] tuple = null;
		try {
			String line = br.readLine();
			current_position++;
			String[] string_tuple = csv.parseLine(line);
			tuple = new float[string_tuple.length];
			for(int i=0; i < string_tuple.length; i++)
			{
				tuple[i] = Float.parseFloat(string_tuple[i].replace(',', '.'));
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		return tuple;
	}

}
