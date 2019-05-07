import com.google.devtools.common.options.OptionsParser;
import io.SimulationOptions;
import models.ParticleGenerator;

import java.io.File;
import java.util.Collections;

public class App {

	private static final String OUTPUT_DIRECTORY = "./output";
	private static final String POSITIONS_PLOT_FILE = OUTPUT_DIRECTORY + "/positions.svg";

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
				|| options.mass <= 0) {
			printUsage(parser);
		}

		ParticleGenerator particleGenerator = new ParticleGenerator();
		System.out.println(particleGenerator.generate(3, 200, 0.02, 0.03, 0.01));

//		// Run algorithm
//		TimeStepDrivenMolecularDynamics.run(
//				options.limitTime,
//				options.deltaT,
//				options.printDeltaT,
//				options.k,
//				options.vdc,
//				options.initialPosition,
//				options.mass,
//				POSITIONS_PLOT_FILE
//		);
	}

	private static void printUsage(OptionsParser parser) {
		System.out.println("Usage: java -jar granular-materials-1.0-SNAPSHOT.jar OPTIONS");
		System.out.println(parser.describeOptions(Collections.emptyMap(),
				OptionsParser.HelpVerbosity.LONG));
	}

}
