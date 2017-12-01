package at.ac.univie.clustering.data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import com.opencsv.CSVReader;

public class CsvWorker implements DataWorker {

    private final String filename;
    private final String shortFilename;
    private final char delimiter;
    private final boolean header;
    private int nTuple = 0;
    private int dimension = 0;
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
        // TODO: option for decimal symbol
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
        dimension = countDimension();
        nTuple = countTuples();

        reader = new CSVReader(new FileReader(filename), delimiter);
        if (header) {
            reader.readNext();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see at.ac.univie.clustering.data.DataWorker#getName()
     */
    @Override
    public String getName() {
        return shortFilename;
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
        return f.exists() && !f.isDirectory();
    }

    /**
     * @return
     */
    private boolean fileReadable() {
        File f = new File(filename);
        return f.canRead() && Files.isReadable(FileSystems.getDefault().getPath(f.getAbsolutePath()));
    }

    /*
     * (non-Javadoc)
     *
     * @see at.ac.univie.clustering.data.DataWorker#countTuples()
     */
    private int countTuples() throws IOException {
        int nTuple = 0;

        file = new File(filename);
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
            CSVReader cr = new CSVReader(new FileReader(filename), delimiter);
            String[] stringTuple;
            if (header)
                stringTuple = cr.readNext(); // maybe file has unusual header
            stringTuple = cr.readNext();
            dimension = stringTuple.length;
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
    public double[] readTuple() throws IOException, ParseException {
        double[] tuple;
        current_position++;
        if (current_position > nTuple) {
            return null;
        }

        String[] stringTuple = reader.readNext();
        tuple = new double[stringTuple.length];
        for (int i = 0; i < stringTuple.length; i++) {
            tuple[i] = decimalFormat.parse(stringTuple[i]).doubleValue();
        }
        if (tuple.length != dimension) {
            throw new IOException("ERROR: Tuple with differeng dimension than originally determined at line "
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
}
