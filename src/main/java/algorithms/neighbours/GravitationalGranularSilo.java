package algorithms.neighbours;

import models.Criteria;
import models.Particle;
import models.ParticleGenerator;
import models.TimeCriteria;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class GravitationalGranularSilo {

	private static double boxHeight = 1.0;
	private static double boxWidth = 0.3;
	private static double boxDiameter = 0.15;

	private static final double G = -10;

	private static final double MAX_INTERACTION_RADIUS = 0.03 / 2;

	private static final double MIN_DIAMETER = 0.02;
	private static final double MAX_DIAMETER = 0.03;

	// Initial State
	private static double time = 0.0;

	// Each particle's integration method
	private static final Map<Particle, IntegrationMethodWithNeighbours> particleIntegrationMethods = new HashMap<>();

	public static void run(
			List<Particle> particles,
			BufferedWriter buffer,
			double limitTime,
			double dt,
			double printDeltaT,
			double length,
			double width,
			double diameter,
			double kN,
			double kT) throws IOException {

		boxHeight = length;
		boxWidth = width;
		boxDiameter = diameter;

//		Particle p1 = particles.get(0);
//		Particle p2 = particles.get(1);
//		p1.setPosition(new Vector2D(0.05 - 0.02, 0.5));
////		p1.setPosition(new Vector2D(boxWidth / 2, boxHeight / 1.7));
//		p1.setRadius(0.014);
//		p2.setRadius(0.014);
//		p2.setPosition(new Vector2D(0.05 - 0.02, 0.3));
//		p1.setVelocity(new Vector2D(0, 0));
//		p2.setVelocity(new Vector2D(0, 0));
//		List<Particle> test2particles = new ArrayList<>();
//		test2particles.add(p1);
//		test2particles.add(p2);
//		particles = test2particles;

		// Print to buffer and set dummy particles for Ovito grid
		printFirstFrame(buffer, particles);

		Criteria timeCriteria = new TimeCriteria(0.25);

		// Print frame
		int currentFrame = 1;
		int printFrame = (int) Math.ceil(printDeltaT / dt);


		while (!timeCriteria.isDone(particles, time)) {
			time += dt;

			// Calculate neighbours
			CellIndexMethod.run(particles,
					(boxHeight * 1.1),
					(int) Math.floor((boxHeight * 1.1) / MAX_INTERACTION_RADIUS),
					MAX_INTERACTION_RADIUS);

			// Calculate sum of forces, including fake wall particles
			particles.stream().parallel().forEach(p -> {
				Set<Particle> neighboursCustom = new HashSet<>(p.getNeighbours());
				neighboursCustom = filterNeighbors(p, neighboursCustom);
				addFakeWallParticles(p, neighboursCustom);
				calculateForce(p, neighboursCustom, kN, kT);
			});

			// Only at first frame, initialize previous position of Verlet with Euler
			if (time == dt) {
				particles.forEach(p -> {
					if (time == dt) {
						Vector2D currentForce = p.getForce();
						double posX = p.getPosition().getX() - dt * p.getVelocity().getX();
						double posY = p.getPosition().getY() - dt * p.getVelocity().getY();
						posX += Math.pow(dt, 2) * currentForce.getX() / (2 * p.getMass());
						posY += Math.pow(dt, 2) * currentForce.getY() / (2 * p.getMass());

						particleIntegrationMethods.put(p,
								new VerletWithNeighbours(new Vector2D(posX, posY)));
					}
				});
			} else {
				// Update position
				particles.stream().parallel().forEach(p -> moveParticle(p, dt));
			}

			particles.stream().parallel().forEach(p -> {
				if (p.getPosition().getY() <= 0) {
					relocateParticle(p, particles);
				}
			});

			if ((currentFrame % printFrame) == 0) {
				buffer.write(String.valueOf(particles.size() + 2));
				buffer.newLine();
				buffer.write(String.valueOf(currentFrame));
				buffer.newLine();
				printGridDummyParticles(buffer);
				particles.stream().parallel().forEach(p -> {
					try {
						buffer.write(particleToString(p));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				});
			}

			System.out.println("Current frame: " + currentFrame);
			currentFrame++;
		}
	}

	private static void relocateParticle(Particle particle, List<Particle> particles) {

		int maxTries = 1000;
		int tries = 0;
		while ((tries++ < maxTries) && !ParticleGenerator.setNewRandomPosition(particles,
				particle,
				new Vector2D(0.0, boxWidth),
				new Vector2D(boxHeight * 1.1, boxHeight * 1.1),
				MIN_DIAMETER,
				MAX_DIAMETER)) {
		}

		if (tries == maxTries) { //TODO que haga un random sobre el x mencionado
			particle.setPosition(new Vector2D(boxWidth / 2, boxHeight * 1.1));
			particle.setVelocity(Vector2D.ZERO);
		}

		particleIntegrationMethods.put(particle,
				new VerletWithNeighbours(particle.getPosition()));

	}

	private static Set<Particle> filterNeighbors(Particle particle, Set<Particle> neighbors) {
		HashSet<Particle> set = new HashSet<>();
		for (Particle neighbor : neighbors) {
			if (particle.getPosition().distance(neighbor.getPosition()) <= (particle.getRadius() + neighbor.getRadius())) {
				set.add(neighbor);
			}
		}
//		neighbors.stream().parallel().forEach(neighbor -> {
//			if (particle.getPosition().distance(neighbor.getPosition()) <= (particle.getRadius() + neighbor.getRadius())) {
//				set.add(neighbor);
//			}
//		});
		return set;
	}

	private static Particle findHighestParticle(List<Particle> particles, double x, double r) {
		double aux = 0;
		Particle p = null;
		for (Particle particle : particles) {
			if (Math.abs(particle.getPosition().getX() - x) < particle.getRadius() + r && particle.getPosition().getY() > aux) {
				aux = particle.getPosition().getY();
				p = particle;
			}
		}
		return p;
	}

	/**
	 * Calculate sum of forces
	 */
	private static void calculateForce(Particle particle, Set<Particle> neighbours, double kN, double kT) {
		Vector2D F = new Vector2D(0, particle.getMass() * G);
		F = neighbours.stream().map(p2 -> {

			// Calculate epsilon
			double eps = particle.getRadius() + p2.getRadius() - (particle.getPosition().distance(p2.getPosition()));

			// Calculate Fn
			double Fn = -kN * eps;

			// Calculate x component of contact unit vector e
			double Enx = (p2.getPosition().getX() - particle.getPosition().getX()) / (p2.getPosition().distance(particle.getPosition()));

			// Calculate y component of contact unit vector e
			double Eny = (p2.getPosition().getY() - particle.getPosition().getY()) / (p2.getPosition().distance(particle.getPosition()));

			// Calculate Ft
			double Ft = -kT * eps * (((particle.getVelocity().getX() - p2.getVelocity().getX()) * (-Eny))
					+ ((particle.getVelocity().getY() - p2.getVelocity().getY()) * (Enx)));

			double Fx = Fn * Enx + Ft * (-Eny);
			double Fy = Fn * Eny + Ft * Enx;

			return new Vector2D(Fx, Fy);
		}).reduce(F, Vector2D::add);

		// Particle knows its force at THIS frame
		particle.setForce(F);
	}

	private static void moveParticle(Particle particle, double dt) {
		IntegrationMethodWithNeighbours integrationMethod = particleIntegrationMethods.get(particle);
		integrationMethod.updatePosition(particle, dt);
	}

	/**
	 * For the ones that make contact, add a fake particle to the set of neighbours.
	 */
	private static void addFakeWallParticles(Particle particle, Set<Particle> neighbours) {
		int fakeId = -1;

		// Analyse left wall
		if (particle.getPosition().getX() - particle.getRadius() <= 0) {
			Particle leftWallParticle = new Particle(fakeId--, particle.getRadius(), particle.getMass());
			leftWallParticle.setPosition(new Vector2D(-particle.getRadius(), particle.getPosition().getY()));
			leftWallParticle.setVelocity(Vector2D.ZERO);
			neighbours.add(leftWallParticle);
		}
		// Analyse right wall
		else if (particle.getPosition().getX() + particle.getRadius() >= boxWidth) {
			Particle rightWallParticle = new Particle(fakeId--, particle.getRadius(), particle.getMass());
			rightWallParticle.setPosition(new Vector2D(particle.getRadius() + boxWidth, particle.getPosition().getY()));
			rightWallParticle.setVelocity(Vector2D.ZERO);
			neighbours.add(rightWallParticle);
		}

		double diameterStart = (boxWidth / 2 - boxDiameter / 2);
		boolean outsideGap = particle.getPosition().getX() < diameterStart || particle.getPosition().getX() > (diameterStart + boxDiameter);

		double bottomWall = boxHeight / 10;
		double upperWall = boxHeight * 1.1;

		// Analyse bottom wall
		if (particle.getPosition().getY() >= bottomWall
				&& (particle.getPosition().getY() - particle.getRadius() <= bottomWall)
				&& outsideGap) {
//				&& particle.getVelocity().getY() < 0) {
			Particle bottomWallParticle = new Particle(fakeId--, particle.getRadius(), particle.getMass());
			bottomWallParticle.setPosition(new Vector2D(particle.getPosition().getX(), bottomWall - particle.getRadius()));
			bottomWallParticle.setVelocity(Vector2D.ZERO);
			neighbours.add(bottomWallParticle);
		}
		// Analyse top wall
		else if ((particle.getPosition().getY() + particle.getRadius()) >= upperWall) {
			Particle topWallParticle = new Particle(fakeId--, particle.getRadius(), particle.getMass());
			topWallParticle.setPosition(new Vector2D(particle.getPosition().getX(), particle.getRadius() + upperWall));
			topWallParticle.setVelocity(Vector2D.ZERO);
			neighbours.add(topWallParticle);
		}


		// TODO: y las del borde del gap? puntual fija masa y radio 0
	}

	private static void printFirstFrame(BufferedWriter buff, List<Particle> particles) throws IOException {
		// Print dummy particles to simulation output file
		buff.write(String.valueOf(particles.size() + 2));
		buff.newLine();
		buff.write("0");
		buff.newLine();
		printGridDummyParticles(buff);

		// Print remaining particles
		particles.forEach(particle -> {
			try {
				buff.write(particleToString(particle));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	private static void printGridDummyParticles(BufferedWriter buff) throws IOException {
		// Particles for fixing Ovito grid
		Particle dummy1 = new Particle(-100, 0, 0);
		Particle dummy2 = new Particle(-101, 0, 0);
		dummy1.setPosition(new Vector2D(0, boxHeight / 10));
		dummy1.setVelocity(new Vector2D(0, 0));
		dummy2.setPosition(new Vector2D(boxWidth, boxHeight + (boxHeight / 10)));
		dummy2.setVelocity(new Vector2D(0, 0));
		buff.write(particleToString(dummy1));
		buff.write(particleToString(dummy2));
	}

	private static String particleToString(Particle p) {
		return p.getId() + " " +
				p.getRadius() + " " +
				p.getPosition().getX() + " " +
				p.getPosition().getY() + " " +
				p.getVelocity().getX() + " " +
				p.getVelocity().getY() + " \n"
				;
	}
}
