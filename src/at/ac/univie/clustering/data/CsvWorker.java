package at.ac.univie.clustering.data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import com.opencsv.CSVReader;

public class CsvWorker implements DataWorker {

    private final String filename;
    private final String shortFilename;
    private final char delimiter;
    private final boolean header;
    private int tupleCount;
    private int dimensions;
    private int current_position;
    private DecimalFormat decimalFormat;

    private File file;
    CSVReader reader;

    /**
     * @param filename
     * @param delimiter
     * @param header
     * @throws IOException
     */
    public CsvWorker(String filename, char delimiter, char decimal, boolean header) throws IOException {
        this.filename = filename;
        this.delimiter = delimiter;
        this.header = header;

        // Set formatter with desired decimal symbol
        decimalFormat = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(decimal);
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setDecimalFormatSymbols(symbols);

        if (!fileExists())
            throw new IOException("Could not find file with provided filename.");
        if (!fileReadable())
            throw new IOException("File with provided filename is not readable.");

        file = new File(filename);

        shortFilename = file.getName();
        dimensions = countDimensions();
        tupleCount = countTuples();

        reader = new CSVReader(new FileReader(filename), delimiter);
        if (header) {
            reader.readNext();
        }
    }

    @Override
    public String getName() {
        return shortFilename;
    }

    @Override
    public int getDimensions() {
        return dimensions;
    }

    @Override
    public int getTupleCount() {
        return tupleCount;
    }

    /**
     * Verify that file exists and is not a directory.
     * @return boolean
     */
    private boolean fileExists() {
        File f = new File(filename);
        return f.exists() && !f.isDirectory();
    }

    /**
     * Verify that file is readable.
     * @return boolean
     */
    private boolean fileReadable() {
        File f = new File(filename);
        return f.canRead() && Files.isReadable(FileSystems.getDefault().getPath(f.getAbsolutePath()));
    }

    /**
     * Read entire file and count tuples. Do not count header
     * @return tupleCount
     * @throws IOException
     */
    private int countTuples() throws IOException {
        int tupleCount = 0;

        FileReader fr = new FileReader(new File(filename));
        LineNumberReader lnr = new LineNumberReader(fr);
        String line;

        while ((line = lnr.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            tupleCount++;
        }
        lnr.close();

        if (this.header && tupleCount > 0)
            tupleCount -= 1;
        return tupleCount;
    }

    /**
     * Count dimensions in file based on first data tuple.
     * @return countDimensions
     * @throws IOException
     */
    private int countDimensions() throws IOException {
        int dimensions = 0;
        CSVReader cr = new CSVReader(new FileReader(filename), delimiter);
        String[] stringTuple;
        if (header){
            cr.readNext(); // maybe file has unusual header
        }
        stringTuple = cr.readNext();
        dimensions = stringTuple.length;
        return dimensions;
    }

    @Override
    public int getCurrentPosition() {
        return current_position;
    }

    @Override
    public double[] readTuple() throws IOException, ParseException {
        double[] tuple;
        current_position++;
        if (current_position > tupleCount) {
            return null;
        }

        String[] stringTuple = reader.readNext();
        tuple = new double[stringTuple.length];
        for (int i = 0; i < stringTuple.length; i++) {
            tuple[i] = decimalFormat.parse(stringTuple[i]).doubleValue();
        }
        if (tuple.length != dimensions) {
            throw new IOException("ERROR: Tuple with differeng dimensions than originally determined at line "
                    + current_position + ".");
        }
        return tuple;
    }

    @Override
    public void reset() throws IOException{
        current_position = 0;
        reader = new CSVReader(new FileReader(filename), delimiter);
        if (header) {
            reader.readNext();
        }
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();

        builder.append("\nFilename: " + shortFilename);
        builder.append("\nHeader: " + header);
        builder.append("\nDelimiter: " + delimiter);
        builder.append("\nDecimal symbol: " + decimalFormat);
        builder.append("\nDimensions: " + dimensions);
        builder.append("\nTuples count: " + tupleCount);

        return builder.toString();
    }
}
