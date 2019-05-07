package algorithms.neighbours;

import models.Particle;
import models.neighbours.SumOfForces;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class BeemanWithNeighbours implements IntegrationMethodWithNeighbours {

	private Vector2D previousAcceleration;
	private SumOfForces force;

	public BeemanWithNeighbours(Vector2D previousAcceleration,
	                            SumOfForces force) {
		this.previousAcceleration = previousAcceleration;
		this.force = force;
	}

	public void updatePosition(Particle particle, double dt) {
		Vector2D currentAcceleration;
		Vector2D nextPosition;
		Vector2D nextVelocity;
		Vector2D nextAcceleration;

		// Calculate current acceleration
		currentAcceleration = force.sumOfForces(particle, particle.getNeighbours()).scalarMultiply(1.0 / particle.getMass());

		// Calculate next position
		nextPosition = X(dt, currentAcceleration, particle);

		particle.setPosition(nextPosition);

		// Calculate next acceleration
		nextAcceleration = force.sumOfForces(particle, particle.getNeighbours()).scalarMultiply(1.0 / particle.getMass());

		// Calculate next velocity
		nextVelocity = Vcorrector(dt, nextAcceleration, currentAcceleration, particle);

		this.previousAcceleration = currentAcceleration;

		particle.setPosition(nextPosition);
		particle.setVelocity(nextVelocity);
	}

	private Vector2D X(double dt, Vector2D currentAcceleration, Particle particle) {
		return particle.getPosition()
				.add(particle.getVelocity()
						.scalarMultiply(dt))
				.add(currentAcceleration
						.scalarMultiply(dt * dt * 2.0 / 3.0))
				.subtract(previousAcceleration
						.scalarMultiply(dt * dt / 6.0));
	}

	private Vector2D Vcorrector(double dt, Vector2D nextAcceleration, Vector2D currentAcceleration, Particle particle) {
		return particle.getVelocity()
				.add(nextAcceleration
						.scalarMultiply(dt / 3.0))
				.add(currentAcceleration
						.scalarMultiply(dt * 5.0 / 6.0))
				.subtract(previousAcceleration
						.scalarMultiply(dt / 6.0));
	}
}
