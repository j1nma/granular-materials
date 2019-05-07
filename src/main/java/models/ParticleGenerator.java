package models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ParticleGenerator {

	public List<Particle> generate(int numberOfParticles, double areaLength, double lowerDiameter, double upperDiameter, double mass) {
		List<Particle> particles = new LinkedList<>();

		// Generate first random diameter
		Random r = new Random();
		double radius = (lowerDiameter + (upperDiameter - lowerDiameter) * r.nextDouble()) / 2;

		// Generate first random position with maximum radius as limit (upperDiameter / 2)
		double x = r.nextDouble() * (areaLength - 2 * (upperDiameter / 2)) + (upperDiameter / 2);
		double y = r.nextDouble() * (areaLength - 2 * (upperDiameter / 2)) + (upperDiameter / 2);

		// Generate first particle
		Particle first = new Particle(0, radius, mass);
		first.setPosition(new Vector2D(x, y));
		first.setVelocity(Vector2D.ZERO);
		particles.add(first);

		for (int i = 1; i < numberOfParticles; i++) {
			boolean validPosition = false;
			while (!validPosition) {
				// Generate random radius and position
				radius = (lowerDiameter + (upperDiameter - lowerDiameter) * r.nextDouble()) / 2;
				x = r.nextDouble() * (areaLength - 2 * (upperDiameter / 2)) + (upperDiameter / 2);
				y = r.nextDouble() * (areaLength - 2 * (upperDiameter / 2)) + (upperDiameter / 2);

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
