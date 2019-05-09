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
	private static final String OVITO_FILE = OUTPUT_DIRECTORY + "/ovito_file.txt";

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
				|| options.diameter <= 0
				|| options.kN <= 0
				|| options.vdc <= 0) {
			printUsage(parser);
		}

		if (!(options.length > options.width && options.width > options.diameter)) {
			System.out.println("L > W > D");
			printUsage(parser);
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
				options.kN * 2,
				options.vdc
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
	                                 double kT,
	                                 double gamma) throws IOException {

		FileWriter fw = new FileWriter(String.valueOf(Paths.get(OVITO_FILE)));
		BufferedWriter writeFileBuffer = new BufferedWriter(fw);

		GravitationalGranularSilo.run(
				particles,
				writeFileBuffer,
				limitTime,
				deltaT,
				printDeltaT,
				length,
				width,
				diameter,
				kN,
				kT,
				gamma
		);

		writeFileBuffer.close();
	}

	private static void printUsage(OptionsParser parser) {
		System.out.println("Usage: java -jar granular-materials-1.0-SNAPSHOT.jar OPTIONS");
		System.out.println(parser.describeOptions(Collections.emptyMap(),
				OptionsParser.HelpVerbosity.LONG));
	}

}
