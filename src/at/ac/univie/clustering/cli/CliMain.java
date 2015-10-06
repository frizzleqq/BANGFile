package at.ac.univie.clustering.cli;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import at.ac.univie.clustering.data.CsvWorker;
import at.ac.univie.clustering.data.DataWorker;
import at.ac.univie.clustering.method.Clustering;
import at.ac.univie.clustering.method.bang.BangClustering;

public class CliMain {
	
	private static Options options = new Options();
	
	private static String filename = "src/resources/test.csv";
	private static char delimiter = ';';
	private static boolean header = false;
	private static int bucketsize = 17;
	private static int neighbourhood = 0;
	private static int clusterPercent = 50;
	private static boolean bangAlias = false;
	
	private static final int ERR_EXCEPTION = 1;
	private static final int ERR_PARAM = 2;

	private static void parse_options(String[] args) {

		options.addOption("h", "help", false, "show help.");
		options.addOption("f", true, "filename");
		options.addOption("d", true, "delimiter");
		options.addOption(null,"header", false, "header");
		options.addOption("s", true, "bucketsize");
		options.addOption("n", true, "neighbourhood");
		options.addOption("c", true, "cluster-percent");
		options.addOption("a", false, "alias");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if (cmd.hasOption("h") || cmd.hasOption("help")) {
			help();
		}

		if (cmd.hasOption("header")) {
			header = true;
		}

		if (cmd.hasOption("f"))
			filename = cmd.getOptionValue("f");
		
		if (cmd.hasOption("d"))
			delimiter = cmd.getOptionValue("d").charAt(0);

		if (cmd.hasOption("s"))
			bucketsize = Integer.parseInt(cmd.getOptionValue("s"));
			if (bucketsize < 4){
				System.err.println("'bucketsize' has to be at least 4");
				System.exit(ERR_PARAM);
			}

		if (cmd.hasOption("n"))
			neighbourhood = Integer.parseInt(cmd.getOptionValue("n"));

		if (cmd.hasOption("c")) {
			clusterPercent = Integer.parseInt(cmd.getOptionValue("s"));
			if (clusterPercent < 0 || clusterPercent > 100) {
				System.err.println("ClusterPercent has to be between 0 and 100.");
				System.exit(ERR_PARAM);
			}
		}

		if (cmd.hasOption("a"))
			bangAlias = true;

	}

	private static void help() {
		String header = "Bang Clustering:\n\n";
		String footer = "\nTODO: write more help.";
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Bang", header, options, footer, true);
		System.exit(0);
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		parse_options(args);
		
		DataWorker data = null;
		
		try {
			data = new CsvWorker(filename, delimiter, header);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			System.exit(ERR_EXCEPTION);
		}
		
		// checking preconditions
		int dimension = data.getDimensions();
		if (dimension == 0){
			System.err.println("Could not determine dimensions of provided data.");
			System.exit(ERR_EXCEPTION);
		} else if (dimension < 2){
			System.err.println("Could not determine at least 2 dimensions.");
			System.exit(ERR_EXCEPTION);
		}
		//TODO: is records really needed information at start?
		int records = data.getRecords();
		if (records == 0){
			System.err.println("Could not determine amount of records of provided data.");
			System.exit(ERR_EXCEPTION);
		}
		
		Clustering cluster = null;
		
		cluster = new BangClustering(dimension);
		//bucketsize, clusterPercent, bangAlias, neighbourhood -> constructor or method?
		
		//this should go to BangClustering
		if (neighbourhood == 0)
			neighbourhood = dimension - 1;

		try {
			cluster.readData(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			System.exit(ERR_EXCEPTION);
		}

	}
}
