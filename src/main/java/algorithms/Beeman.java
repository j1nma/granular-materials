package algorithms;

import models.Force;

@Deprecated
public class Beeman implements IntegrationMethod {

	private double mass;
	private double currentPosition;
	private double currentVelocity;
	private double previousAcceleration;
	private Force force;

	public Beeman(double mass,
	              double initialPosition,
	              double initialVelocity,
	              double previousAcceleration,
	              Force force) {
		this.mass = mass;
		this.currentPosition = initialPosition;
		this.currentVelocity = initialVelocity;
		this.previousAcceleration = previousAcceleration;
		this.force = force;
	}

	public double updatePosition(double dt) {
		double currentAcceleration;
		double nextPosition;
		double nextVelocity;
		double nextAcceleration;

		// Calculate current acceleration
		currentAcceleration = force.F(currentPosition, currentVelocity) / mass;

		// Calculate next position
		nextPosition = X(dt, currentAcceleration);

		// Calculate next acceleration
		nextAcceleration = force.F(nextPosition, currentVelocity) / mass;

		// Calculate next velocity
		nextVelocity = Vcorrector(dt, nextAcceleration, currentAcceleration);

		this.currentPosition = nextPosition;
		this.currentVelocity = nextVelocity;
		this.previousAcceleration = currentAcceleration;
		return nextPosition;
	}

	private double X(double dt, double currentAcceleration) {
		return this.currentPosition + this.currentVelocity * dt + (2.0 / 3.0) * currentAcceleration * Math.pow(dt, 2) - (1.0 / 6.0) * this.previousAcceleration * Math.pow(dt, 2);
	}

	private double Vcorrector(double dt, double nextAcceleration, double currentAcceleration) {
		return this.currentVelocity + (1.0 / 3.0) * nextAcceleration * dt + (5.0 / 6.0) * currentAcceleration * dt - (1.0 / 6.0) * this.previousAcceleration * dt;
	}

}
