package at.ac.univie.clustering.dataWorkers;

import java.io.IOException;

/**
 * Factory to produce objects extending the DataWorker class.
 * <br>
 * To add a class to the Factory, it needs have its own create method added as a class method.
 *
 * @author Florian Fritz (florian.fritzi@gmail.com)
 * @version 1.0
 */
public class DataWorkerFactory {

    /**
     * Create CsvWorker object with provided parameters.
     *
     * @param filename  full path to file
     * @param delimiter delimiter separating values of a line(tuple)
     * @param decimal   decimal symbol of numeric values
     * @param header    true if first line contains header
     * @return  CsvWorker object
     * @throws IOException  If file content cannot be read.
     */
    public static DataWorker createCsvWorker(String filename, char delimiter, char decimal, boolean header) throws IOException{
        return new CsvWorker(filename, delimiter, decimal, header);
    }

}
