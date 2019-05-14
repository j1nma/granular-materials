package models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ParticleGenerator {

	private static final double MAX_TRIES = 10000;

	public List<Particle> generate(int numberOfParticles, double areaLength, double areaWidth, double minDiameter, double maxDiameter, double mass) {
		List<Particle> particles = new LinkedList<>();

		// Run until max number of tries is reached for a particle allocation at silo area
		boolean flag = true;
		for (int i = 0; i < numberOfParticles && flag; i++) {

			boolean validPosition = false;
			int tries = 0;

			Particle p = new Particle(i + 1, 0.0, mass);

			while (!validPosition) {
				tries++;
				if (tries == MAX_TRIES) {
					flag = false;
					break;
				}

				validPosition = setNewRandomPosition(particles,
						p,
						new Vector2D(0.0, areaWidth),
						new Vector2D(areaLength / 10, areaLength * 1.1),
						(minDiameter + (maxDiameter - minDiameter) * new Random().nextDouble()) / 2);
			}
			if (validPosition)
				particles.add(p);
		}

		return particles;
	}

	/**
	 * Assign new random position to particle at empty space between xRange and yRange
	 *
	 * @param particles
	 * @param particle
	 * @param xRange
	 * @param yRange
	 * @return true if successful
	 */
	public static boolean setNewRandomPosition(List<Particle> particles,
	                                           Particle particle,
	                                           Vector2D xRange,
	                                           Vector2D yRange,
	                                           double radius) {
		Random r = new Random();
		double x, y;

		// Generate random position
		x = r.nextDouble() * (xRange.getY() - 2 * radius) + radius + xRange.getX();
		double areaLength = yRange.getY() - yRange.getX();
		y = r.nextDouble() * (areaLength - 2 * radius) + radius + yRange.getX();

		int j = 0;
		boolean validPosition = true;

		// Check new position is valid with all previous ones
		while (j < particles.size() && validPosition) {
			Particle otherParticle = ((Particle) particles.toArray()[j]);
			validPosition = isValidPosition(otherParticle.getPosition().getX(),
					otherParticle.getPosition().getY(),
					otherParticle.getRadius(),
					x, y, radius);
			j++;
		}

		if (validPosition) {
			particle.setRadius(radius);
			particle.setPosition(new Vector2D(x, y));
			particle.setVelocity(Vector2D.ZERO);
		}

		return validPosition;
	}

	private static boolean isValidPosition(double otherX, double otherY, double otherRadius, double newX, double newY, double newRadius) {
		return (Math.pow(otherX - newX, 2) + Math.pow(otherY - newY, 2)) > Math.pow(otherRadius + newRadius, 2);
	}
}
