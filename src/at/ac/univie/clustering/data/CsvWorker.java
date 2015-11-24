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
	
	/*
	 * TODO: class-wide reader instance, use instance to determine current position,
	 * tests!,
	 */

	private String filename;
	private char delimiter;
	private boolean header;
	private int nTuple = 0;
	private int dimension = 0;
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

		dimension = countDimension();
		nTuple = countTuples();

		csv = new CSVParser(delimiter);
		br = new BufferedReader(new FileReader(filename));
		if (header)
			br.readLine();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.ac.univie.clustering.data.DataWorker#getDimensions()
	 */
	@Override
	public int getDimension() {
		return dimension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.ac.univie.clustering.data.DataWorker#getnTuple()
	 */
	@Override
	public int getnTuple() {
		return nTuple;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.ac.univie.clustering.data.DataWorker#countTuples()
	 */
	private int countTuples() throws IOException {
		int nTuple = 0;

		File file = new File(filename);
		FileReader fr = new FileReader(file);
		LineNumberReader lnr = new LineNumberReader(fr);
		String line;

		while ((line = lnr.readLine()) != null) {
			if (line.isEmpty()) {
				break;
			}
			nTuple++;
		}
		lnr.close();

		if (this.header && nTuple > 0)
			nTuple -= 1;
		return nTuple;
	}

	/**
	 * @return
	 */
	private int countDimension() {
		int dimension = 0;
		try {
			CSVParser csv = new CSVParser(delimiter);
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = "";
			line = br.readLine();
			if (header)
				line = br.readLine(); // maybe file has unusual header
			br.close();
			dimension = csv.parseLine(line).length;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dimension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.ac.univie.clustering.data.DataWorker#getCurPosition()
	 */
	@Override
	public int getCurPosition() {
		return current_position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.ac.univie.clustering.data.DataWorker#readTuple()
	 */
	@Override
	public float[] readTuple() throws IOException, NumberFormatException {
		float[] tuple = null;
		
		current_position++;

		String line = br.readLine();
		
		if(current_position > nTuple){
			return null;
		}
		
		String[] stringTuple = csv.parseLine(line);
		
		tuple = new float[stringTuple.length];
		for (int i = 0; i < stringTuple.length; i++) {
			tuple[i] = Float.parseFloat(stringTuple[i].replace(',', '.'));
		}
		
		if (tuple.length != dimension) {
			throw new IOException("ERROR: Tuple with differeng dimension than originally determined at line "
					+ current_position + ".");
		}
		
		return tuple;
	}

}
