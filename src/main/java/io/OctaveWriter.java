package io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Stack;

@Deprecated
public class OctaveWriter {

	private final FileWriter fileWriter;

	private OctaveWriter(File file) throws IOException {
		Optional<File> containingDir = Optional.ofNullable(file.getParentFile());
		if (containingDir.map(dir -> !dir.exists() && !dir.mkdirs()).orElse(false)) {
			throw new IllegalStateException("Could not create dir: " + containingDir);
		}
		this.fileWriter = new FileWriter(file);
	}

	public OctaveWriter(Path path) throws IOException {
		this(path.toFile());
	}

	public void close() throws IOException {
		this.fileWriter.close();
	}

	public void writePositionsThroughTime(Stack<Double> timeStepValues,
	                                      Stack<Double> analyticValues,
	                                      Stack<Double> beemanPositionValues,
	                                      Stack<Double> velocityVerletPositionValues,
	                                      Stack<Double> order5GearPredictorCorrectorPositionValues,
	                                      String positionPlotFile) throws IOException {
		final StringBuilder builder = new StringBuilder();

		// Time step values
		builder.append("x = [").append(Double.toString(timeStepValues.pop()));
		while (!timeStepValues.isEmpty()) {
			builder.append(", ").append(Double.toString(timeStepValues.pop()));
		}
		builder.append("];").append("\n");

		// Analytic values
		builder.append("y1 = [").append(Double.toString(analyticValues.pop()));
		while (!analyticValues.isEmpty()) {
			builder.append(", ").append(Double.toString(analyticValues.pop()));
		}
		builder.append("];").append("\n");

		// Beeman values
		builder.append("y2 = [").append(Double.toString(beemanPositionValues.pop()));
		while (!beemanPositionValues.isEmpty()) {
			builder.append(", ").append(Double.toString(beemanPositionValues.pop()));
		}
		builder.append("];").append("\n");

		// Velocity verlet values
		builder.append("y3 = [").append(Double.toString(velocityVerletPositionValues.pop()));
		while (!velocityVerletPositionValues.isEmpty()) {
			builder.append(", ").append(Double.toString(velocityVerletPositionValues.pop()));
		}
		builder.append("];").append("\n");

		// Order 5 Gear Predictor Corrector values
		builder.append("y4 = [").append(Double.toString(order5GearPredictorCorrectorPositionValues.pop()));
		while (!order5GearPredictorCorrectorPositionValues.isEmpty()) {
			builder.append(", ").append(Double.toString(order5GearPredictorCorrectorPositionValues.pop()));
		}
		builder.append("];").append("\n");

		// Plot
		builder.append("plot(x, y1, \";Analítica;\");").append("\n")
				.append("xlabel(\"Tiempo (s)\");").append("\n")
				.append("ylabel(\"Posición (m)\");").append("\n")
				.append("set(gca, 'ytick', -1:0.2:1);").append("\n")
				.append("hold all").append("\n")
				.append("plot(x, y2, \";Beeman;\", \"color\", 'g','LineStyle','--');").append("\n")
				.append("plot(x, y3, \";Velocity Verlet;\", \"color\", 'r','LineStyle','--');").append("\n")
				.append("plot(x, y4, \";Gear predictor-corrector de orden 5;\", \"color\", 'b','LineStyle','--');").append("\n")
				.append("print(\"").append(positionPlotFile).append("\", \"-dsvg\", \"-F:12\")").append("\n");

		fileWriter.append(builder.toString());
		fileWriter.flush();
	}
}
