package algorithms.neighbours;

import models.Particle;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.List;

class CellIndexMethod {

	/**
	 * Cells from 0 to MxM - 1.
	 * Each one has a list of CellParticles from that cell number.
	 * A CellParticle contains a Particle and the cell's position.
	 */
	private static List<List<CellParticle>> cells = new ArrayList<>();
	private static int M;

	static void run(List<Particle> particles,
	                double boxSide,
	                int matrixSize) {

		cells = new ArrayList<>();
		M = matrixSize;

		for (int i = 0; i < M * M; i++)
			cells.add(new ArrayList<>());

		for (Particle p : particles) {
			// Calculate particle's cell indexes
			double cellX = Math.floor(p.getPosition().getX() / (boxSide / M));
			double cellY = Math.floor(p.getPosition().getY() / (boxSide / M));

			// Calculate particle's cell number
			int cellNumber = (int) (cellY * M + cellX);

			// Add particle to that cell with cell position
			try {
				cells.get(cellNumber).add(new CellParticle(p, new Vector2D(cellX, cellY)));
			} catch (IndexOutOfBoundsException e) {
				int a = 0;
			}
		}

		for (List<CellParticle> cellParticles : cells) {
			for (CellParticle cp : cellParticles) {
				double cellX = cp.cellPosition.getX();
				double cellY = cp.cellPosition.getY();

				// Check neighbouring cells from inverted up-side down L shape
				visitNeighbour(cp.particle, cellX, cellY);
				visitNeighbour(cp.particle, cellX, cellY + 1);
				visitNeighbour(cp.particle, cellX + 1, cellY + 1);
				visitNeighbour(cp.particle, cellX + 1, cellY);
				visitNeighbour(cp.particle, cellX + 1, cellY - 1);
			}
		}
	}

	private static void visitNeighbour(Particle particle, double cellX, double cellY) {

		if (cellX >= M || cellX < 0 || cellY >= M || cellY < 0) {
			return;
		}

		int neighbourCellNumber = (int) (cellY * M + cellX);

		List<CellParticle> neighbourCellParticles = cells.get(neighbourCellNumber);

		for (CellParticle neighbourCellParticle : neighbourCellParticles) {
			Particle neighbourParticle = neighbourCellParticle.particle;
			if (neighbourParticle.getId() != particle.getId()) {
				double distance = particle.getDistanceBetween(neighbourParticle);
				if (distance < particle.getRadius() + neighbourParticle.getRadius()) {
					// Mutually add both particles as neighbours
					particle.addNeighbour(neighbourParticle);
					neighbourParticle.addNeighbour(particle);
				}
			}
		}
	}

	private static class CellParticle {
		Particle particle;
		Vector2D cellPosition;

		CellParticle(Particle particle, Vector2D cellPosition) {
			this.particle = particle;
			this.cellPosition = cellPosition;
		}
	}
}
