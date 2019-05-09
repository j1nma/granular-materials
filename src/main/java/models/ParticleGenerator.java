package models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ParticleGenerator {

	private static final double MAX_TRIES = 10000;

	public List<Particle> generate(int numberOfParticles, double areaLength, double areaWidth, double minDiameter, double maxDiameter, double mass) {
		List<Particle> particles = new LinkedList<>();
		Random r = new Random();
		double radius = 0.0, x = 0.0, y = 0.0;

		// Run until max number of tries is reached for a particle allocation at silo area
		boolean flag = true;
		for (int i = 0; i < numberOfParticles && flag; i++) {

			boolean validPosition = false;
			int tries = 0;
			while (!validPosition) {
				tries++;
				if (tries == MAX_TRIES) {
					flag = false;
					break;
				}

				// Generate random radius and position
				radius = (minDiameter + (maxDiameter - minDiameter) * r.nextDouble()) / 2;
				x = r.nextDouble() * (areaWidth - 2 * radius) + radius;
				y = r.nextDouble() * (areaLength - 2 * radius) + radius;

				int j = 0;
				validPosition = true;

				// Check new position is valid with all previous ones
				while (j < particles.size() && validPosition) {
					Particle otherParticle = ((Particle) particles.toArray()[j]);
					validPosition = isValidPosition(otherParticle.getPosition().getX(),
							otherParticle.getPosition().getY(),
							otherParticle.getRadius(),
							x, y, radius);
					j++;
				}
			}
			Particle p = new Particle(i + 1, radius, mass);
			p.setPosition(new Vector2D(x, y));
			p.setVelocity(Vector2D.ZERO);
			particles.add(p);
		}

		return particles;
	}

	private boolean isValidPosition(double otherX, double otherY, double otherRadius, double newX, double newY, double newRadius) {
		return (Math.pow(otherX - newX, 2) + Math.pow(otherY - newY, 2)) > Math.pow(otherRadius + newRadius, 2);
	}
}
