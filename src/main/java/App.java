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
	private static final String POSITIONS_PLOT_FILE = OUTPUT_DIRECTORY + "/positions.svg";
	private static final String OVITO_FILE = OUTPUT_DIRECTORY + "/ovito_file.txt";

	public static void main(String[] args) {

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
				|| options.mass <= 0
				|| !options.lennardJonesGas
				|| options.staticFile.isEmpty()
				|| options.dynamicFile.isEmpty()) {
			printUsage(parser);
		}

		ParticleGenerator particleGenerator = new ParticleGenerator();
		System.out.println(particleGenerator.generate(3, 200, 0.02, 0.03, 0.01));

//// Run algorithm
//		runAlgorithm(
//				particles,
//				options.limitTime,
//				options.deltaT,
//				options.printDeltaT
//		);
	}

	private static void runAlgorithm(List<Particle> particles,
	                                 double limitTime,
	                                 double deltaT,
	                                 double printDeltaT) throws IOException {

		FileWriter fw = new FileWriter(String.valueOf(Paths.get(OVITO_FILE)));
		BufferedWriter writeFileBuffer = new BufferedWriter(fw);

//		LennardJonesGas.run(
//				particles,
//				writeFileBuffer,
//				eventWriter,
//				limitTime,
//				deltaT,
//				printDeltaT,
//				LEFT_PARTICLES_PLOT_FILE
//		);

		writeFileBuffer.close();

//		OvitoWriter<Particle> ovitoWriter;
//		try {
//			ovitoWriter = new OvitoWriter<>(Paths.get(OVITO_FILE));
//			ovitoWriter.writeBuffer(buffer);
//			ovitoWriter.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	private static void printUsage(OptionsParser parser) {
		System.out.println("Usage: java -jar granular-materials-1.0-SNAPSHOT.jar OPTIONS");
		System.out.println(parser.describeOptions(Collections.emptyMap(),
				OptionsParser.HelpVerbosity.LONG));
	}

}
