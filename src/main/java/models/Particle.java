package models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

public class Particle implements Cloneable {

	private final int id;
	private double radius;
	private double mass;
	private double normalForce;
	private Vector2D position;
	private Vector2D velocity;
	private Vector2D force;
	private Set<Particle> neighbours;

	public Particle(int id, double radius, double mass) {
		this.id = id;
		this.radius = radius;
		this.mass = mass;
		this.normalForce = 0.0;
		this.neighbours = new HashSet<>();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Particle particle = (Particle) o;
		return id == particle.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("###.0000000000");
		return df.format(position.getX()) + " "
				+ df.format(position.getY()) + " "
				+ df.format(velocity.getX()) + " "
				+ df.format(velocity.getY()) + " "
				+ radius + " "
				+ mass + " ";
	}

	public int getId() {
		return id;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getMass() {
		return mass;
	}

	public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}

	public Vector2D getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2D velocity) {
		this.velocity = velocity;
	}

	public Vector2D getForce() {
		return force;
	}

	public void setForce(Vector2D force) {
		this.force = force;
	}

	public Set<Particle> getNeighbours() {
		return neighbours;
	}

	public void clearNeighbours() {
		this.neighbours = new HashSet<>();
	}

	public double getDistanceBetween(Particle particle) {
		Vector2D particlePosition = particle.getPosition();
		return Math.sqrt(Math.pow(position.getX() - particlePosition.getX(), 2)
				+ Math.pow(position.getY() - particlePosition.getY(), 2));
	}

	public void addNeighbour(Particle neighbour) {
		this.neighbours.add(neighbour);
	}

	public double calculatePressure() {
		return Math.abs(this.normalForce) / calculatePerimeter();
	}

	public void setNormalForce(double neighbourNormalForce) {
		this.normalForce = neighbourNormalForce;
	}

	public void resetNormalForce() {
		this.normalForce = 0.0;
	}

	public double getKineticEnergy() {
		return 0.5 * mass * velocity.getNormSq();
	}

	private double calculatePerimeter() {
		return 2 * Math.PI * radius;
	}

}
