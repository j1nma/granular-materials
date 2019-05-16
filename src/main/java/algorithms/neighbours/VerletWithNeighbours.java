package algorithms.neighbours;

import models.Particle;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class VerletWithNeighbours implements IntegrationMethodWithNeighbours {

	private Vector2D previousPosition;

	VerletWithNeighbours(Vector2D initialPosition) {
		this.previousPosition = initialPosition;
	}

	public void updatePosition(Particle particle, double dt) {
		final Vector2D currentForce = particle.getForce();

		final Vector2D predictedPosition = particle.getPosition()
				.scalarMultiply(2)
				.subtract(previousPosition)
				.add(currentForce.scalarMultiply(Math.pow(dt, 2) / particle.getMass()));

		final Vector2D predictedVelocity = predictedPosition
				.subtract(previousPosition)
				.scalarMultiply(1.0 / (2.0 * dt));

		previousPosition = particle.getPosition();

		particle.setPosition(predictedPosition);
		particle.setVelocity(predictedVelocity);
	}
}
