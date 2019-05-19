import algorithms.neighbours.GravitationalGranularSilo;
import com.google.devtools.common.options.OptionsParser;
import io.SimulationOptions;
import models.Particle;
import models.ParticleGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class App {

	private static final String OUTPUT_DIRECTORY = "./output";
	private static final String OVITO_FILE = OUTPUT_DIRECTORY + "/ovito_file";
	private static final String ENERGY_FILE_NAME = OUTPUT_DIRECTORY + "/energy_file";
	private static final String FLOW_FILE_NAME = OUTPUT_DIRECTORY + "/flow_file";

	private static final int N = 300;
	private static final double MIN_PARTICLE_DIAMETER = 0.02;
	private static final double MAX_PARTICLE_DIAMETER = 0.03;
	private static final double PARTICLE_MASS = 0.01;

	private static ParticleGenerator particleGenerator = new ParticleGenerator();

	public static void main(String[] args) throws IOException {

		// Create output directories
		new File(OUTPUT_DIRECTORY).mkdirs();

		// Parse command line options
		OptionsParser parser = OptionsParser.newOptionsParser(SimulationOptions.class);
		parser.parseAndExitUponError(args);
		SimulationOptions options = parser.getOptions(SimulationOptions.class);
		assert options != null;
		if (options.limitTime <= 0
				|| options.deltaT <= 0
				|| options.printDeltaT <= 0
				|| options.length <= 0
				|| options.width <= 0
				|| options.diameter < 0
				|| options.kN <= 0
				|| options.kT <= 0) {
			printUsage(parser);
		}

		if (!(options.length > options.width && options.width > options.diameter)) {
			System.out.println("L > W > D");
			printUsage(parser);
		}

		if (!parser.containsExplicitOption("deltaT")) {
			options.deltaT = 0.01 * Math.sqrt(PARTICLE_MASS / options.kN);
			System.out.println("Delta t: " + options.deltaT);
		}

		runAlgorithm(
				particleGenerator.generate(N, options.length, options.width, MIN_PARTICLE_DIAMETER, MAX_PARTICLE_DIAMETER, PARTICLE_MASS),
				options.limitTime,
				options.deltaT,
				options.printDeltaT,
				options.length,
				options.width,
				options.diameter,
				options.kN,
				options.kT
		);
	}

	private static void runAlgorithm(List<Particle> particles,
	                                 double limitTime,
	                                 double deltaT,
	                                 double printDeltaT,
	                                 double length,
	                                 double width,
	                                 double diameter,
	                                 double kN,
	                                 double kT) throws IOException {

		FileWriter fw = new FileWriter(String.valueOf(Paths.get(OVITO_FILE + "_D=" + diameter + ".txt")));
		BufferedWriter writeFileBuffer = new BufferedWriter(fw);

		FileWriter fw2;
		BufferedWriter energyFileBuffer;
		if (diameter == 0.0) {
			fw2 = new FileWriter(String.valueOf(Paths.get(ENERGY_FILE_NAME + "_D=0.0_kT=" + kT + ".txt")));
			energyFileBuffer = new BufferedWriter(fw2);
		} else {
			fw2 = new FileWriter(String.valueOf(Paths.get(ENERGY_FILE_NAME + "_D=" + diameter + ".txt")));
			energyFileBuffer = new BufferedWriter(fw2);
		}

		FileWriter fw3 = new FileWriter(String.valueOf(Paths.get(FLOW_FILE_NAME + "_D=" + diameter + ".txt")));
		BufferedWriter flowFileBuffer = new BufferedWriter(fw3);

		GravitationalGranularSilo.run(
				particles,
				writeFileBuffer,
				energyFileBuffer,
				flowFileBuffer,
				limitTime,
				deltaT,
				printDeltaT,
				length,
				width,
				diameter,
				kN,
				kT
		);

		writeFileBuffer.close();
		energyFileBuffer.close();
		flowFileBuffer.close();
	}

	private static void printUsage(OptionsParser parser) {
		System.out.println("Usage: java -jar granular-materials-1.0-SNAPSHOT.jar OPTIONS");
		System.out.println(parser.describeOptions(Collections.emptyMap(),
				OptionsParser.HelpVerbosity.LONG));
	}

}
