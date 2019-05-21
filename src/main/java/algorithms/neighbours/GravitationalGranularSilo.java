package algorithms.neighbours;

import models.Criteria;
import models.Particle;
import models.ParticleGenerator;
import models.TimeCriteria;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class GravitationalGranularSilo {

	private static double boxHeight = 1.5;
	private static double boxWidth = 0.3;
	private static double boxDiameter = 0.15;

	private static final double G = -10;

	private static final double MAX_INTERACTION_RADIUS = 0.03 / 2;

	private static double currentMaxPressure = 0.0;

	// Initial State
	private static double time = 0.0;

	// Each particle's integration method
	private static final Map<Particle, IntegrationMethodWithNeighbours> particleIntegrationMethods = new HashMap<>();

	public static void run(
			List<Particle> particles,
			BufferedWriter buffer,
			BufferedWriter energyBuffer,
			BufferedWriter flowFileBuffer,
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
//		p1.setPosition(new Vector2D(0.05 - 0.02, 0.2));
//		p1.setRadius(0.014);
//		p2.setRadius(0.014);
//		p2.setPosition(new Vector2D(0.05 - 0.02, 0.5));
//		p1.setVelocity(new Vector2D(0, 0));
//		p2.setVelocity(new Vector2D(0, 0));
//		List<Particle> test2particles = new ArrayList<>();
//		test2particles.add(p1);
//		test2particles.add(p2);
//		particles = test2particles;

		// Print to buffer
		printFirstFrame(buffer, energyBuffer, particles);

		Criteria timeCriteria = new TimeCriteria(limitTime);

		// Print frame
		int currentFrame = 1;
		int printFrame = (int) Math.ceil(printDeltaT / dt);

		while (!timeCriteria.isDone(particles, time)) {
			time += dt;

			// Calculate neighbours
			CellIndexMethod.run(particles,
					(boxHeight * 1.1),
					(int) Math.floor((boxHeight * 1.1) / (2 * MAX_INTERACTION_RADIUS))
			);

			// Calculate sum of forces, including fake wall particles
			particles.stream().parallel().forEach(p -> {
				Set<Particle> neighboursCustom = new HashSet<>(p.getNeighbours());
				neighboursCustom = filterNeighbors(p, neighboursCustom);
				addFakeWallParticles(p, neighboursCustom);
				calculateForce(p, neighboursCustom, kN, kT);
			});

			// Save current max pressure for color calculation TODO: paralelizar
			currentMaxPressure = Collections.max(particles, Comparator.comparing(Particle::calculatePressure)).calculatePressure();

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

			// Relocate particles that go outside box a distance of L/10 and clear neighbours
			final List<Particle> finalParticles = particles;
			particles.stream().parallel().forEach(p -> {
				if (p.getPosition().getY() <= 0) {

					// Write time for flow
					try {
						flowFileBuffer.write(String.valueOf(time));
						flowFileBuffer.newLine();
					} catch (IOException e) {
						e.printStackTrace();
					}

					relocateParticle(p, finalParticles);
				}
				p.clearNeighbours();
			});

			if ((currentFrame % printFrame) == 0) {
				buffer.write(String.valueOf(particles.size()));
				buffer.newLine();
				buffer.write(String.valueOf(currentFrame));
				buffer.newLine();

				AtomicReference<Double> totalKinetic = new AtomicReference<>(0.0);

				AtomicReference<Integer> densityCounter = new AtomicReference<>(0);

				particles.stream().parallel().forEach(p -> {
					try {
						buffer.write(particleToString(p));
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					totalKinetic.accumulateAndGet(p.getKineticEnergy(), (x, y) -> x + y);

//					if (p.getPosition().getY() >= (boxHeight / 10)
//							&& p.getPosition().getY() <= ((boxHeight / 10) + 0.35)
//							&& p.getPosition().getX() >= (boxWidth / 2 - boxDiameter / 2)
//							&& p.getPosition().getX() <= (boxWidth / 2 + boxDiameter / 2)) {
//						densityCounter.accumulateAndGet(1, (x, y) -> x + y);
//					}

//					if (p.getPosition().getY() >= (boxHeight / 10)
//							&& p.getPosition().getY() <= ((boxHeight / 10) + 0.35)) {
//						densityCounter.accumulateAndGet(1, (x, y) -> x + y);
//					}
				});

//				System.out.println("Density:" + densityCounter.get() / (boxWidth * 0.35));

				energyBuffer.write(String.valueOf(time) + " " + String.valueOf(totalKinetic.get()));
				energyBuffer.newLine();
			}

			System.out.println("Current progress: " + 100 * (time / limitTime));
			currentFrame++;
		}

		System.out.println("Max pressure: " + currentMaxPressure);
	}

	@SuppressWarnings({"StatementWithEmptyBody", "SuspiciousNameCombination"})
	private static void relocateParticle(Particle particle, List<Particle> particles) {

		int maxTries = 1000;
		int tries = 0;

		// Save previous radius since call to setNewRandomPosition changes it
		double previousRadius = particle.getRadius();

		while ((tries++ < maxTries) && !ParticleGenerator.setNewRandomPosition(particles,
				particle,
				new Vector2D(0.0, boxWidth),
				new Vector2D(boxHeight * 0.5, boxHeight * 1.1),
				particle.getRadius())) {
		}

		particle.setRadius(previousRadius);

		if (tries == maxTries) {
			Random r = new Random();
			double x = r.nextDouble() * (boxWidth - 2 * particle.getRadius()) + particle.getRadius();
			particle.setPosition(new Vector2D(x, boxHeight * 1.1));
			particle.setVelocity(Vector2D.ZERO);
		}

		particleIntegrationMethods.put(particle,
				new VerletWithNeighbours(particle.getPosition()));

	}

	private static Set<Particle> filterNeighbors(Particle particle, Set<Particle> neighbors) {
		HashSet<Particle> set = new HashSet<>();
		neighbors.forEach(neighbor -> {
			if (particle.getPosition().distance(neighbor.getPosition()) <= (particle.getRadius() + neighbor.getRadius())) {
				set.add(neighbor);
			}
		});
		return set;
	}

	/**
	 * Calculate sum of forces
	 */
	private static void calculateForce(Particle particle, Set<Particle> neighbours, double kN, double kT) {
		// Particle normal force reset and accumulator
		particle.resetNormalForce();
		AtomicReference<Double> atomicNormalForce = new AtomicReference<>(0.0);

		// Particle force calculation
		Vector2D F = new Vector2D(0, particle.getMass() * G);
		F = neighbours.stream().map(p2 -> {

			// Calculate distance between centers
			double distance = particle.getPosition().distance(p2.getPosition());

			// Calculate epsilon
			double eps = particle.getRadius() + p2.getRadius() - distance;

			if (eps > 0.0) {
				// Calculate Fn
				double Fn = -kN * eps;

				// Calculate x component of contact unit vector e
				double Enx = (p2.getPosition().getX() - particle.getPosition().getX()) / distance;

				// Calculate y component of contact unit vector e
				double Eny = (p2.getPosition().getY() - particle.getPosition().getY()) / distance;

				// Calculate Ft
				Vector2D relativeVelocity = particle.getVelocity().subtract(p2.getVelocity());
				Vector2D tangentVector = new Vector2D(-Eny, Enx);
				double Ft = -kT * eps * (relativeVelocity.dotProduct(tangentVector));

				double Fx = Fn * Enx + Ft * (-Eny);
				double Fy = Fn * Eny + Ft * Enx;

				atomicNormalForce.accumulateAndGet(Fn, (x, y) -> x + y);

				return new Vector2D(Fx, Fy);
			} else {
				return new Vector2D(0.0, 0.0);
			}
		}).reduce(F, Vector2D::add);

		// Particle knows its force at THIS frame
		particle.setForce(F);

		// Set particle's normal force for pressure calculation later on
		particle.setNormalForce(atomicNormalForce.get());
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
				&& particle.getPosition().getY() - particle.getRadius() <= bottomWall) {
//				&& particle.getVelocity().getY() < 0) {
			if (outsideGap) {
				Particle bottomWallParticle = new Particle(fakeId--, particle.getRadius(), particle.getMass());
				bottomWallParticle.setPosition(new Vector2D(particle.getPosition().getX(), bottomWall - particle.getRadius()));
				bottomWallParticle.setVelocity(Vector2D.ZERO);
				neighbours.add(bottomWallParticle);
			} else {
				if (boxDiameter > 0.0) {
					if (particle.getPosition().getX() - particle.getRadius() <= diameterStart
							&& particle.getPosition().distance(new Vector2D(diameterStart, bottomWall)) < particle.getRadius()) {
						Particle leftDiameterStartParticle = new Particle(fakeId--, 0.0, 0.0);
						leftDiameterStartParticle.setPosition(new Vector2D(diameterStart, bottomWall));
						leftDiameterStartParticle.setVelocity(Vector2D.ZERO);
						neighbours.add(leftDiameterStartParticle);
					} else if (particle.getPosition().getX() + particle.getRadius() >= diameterStart + boxDiameter
							&& particle.getPosition().distance(new Vector2D(diameterStart + boxDiameter, bottomWall)) < particle.getRadius()) {
						Particle rightDiameterStartParticle = new Particle(fakeId--, 0.0, 0.0);
						rightDiameterStartParticle.setPosition(new Vector2D(diameterStart + boxDiameter, bottomWall));
						rightDiameterStartParticle.setVelocity(Vector2D.ZERO);
						neighbours.add(rightDiameterStartParticle);
					}
				} else {
					if (particle.getPosition().getY() - particle.getRadius() <= bottomWall) {
						Particle closedDiameterParticle = new Particle(fakeId--, particle.getRadius(), particle.getMass());
						closedDiameterParticle.setPosition(new Vector2D(particle.getPosition().getX(), bottomWall - particle.getRadius()));
						closedDiameterParticle.setVelocity(Vector2D.ZERO);
						neighbours.add(closedDiameterParticle);
					}
				}
			}
		}
		// Analyse top wall
		else if (particle.getPosition().getY() + particle.getRadius() >= upperWall) {
			Particle topWallParticle = new Particle(fakeId--, particle.getRadius(), particle.getMass());
			topWallParticle.setPosition(new Vector2D(particle.getPosition().getX(), particle.getRadius() + upperWall));
			topWallParticle.setVelocity(Vector2D.ZERO);
			neighbours.add(topWallParticle);
		}
	}

	private static void printFirstFrame(BufferedWriter buff, BufferedWriter energyBuffer, List<Particle> particles) throws IOException {
		buff.write(String.valueOf(particles.size()));
		buff.newLine();
		buff.write("0");
		buff.newLine();

		// Print remaining particles
		particles.forEach(particle -> {
			try {
				buff.write(particleToString(particle));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		// Write N to energy file
		energyBuffer.write(String.valueOf(particles.size()));
		energyBuffer.newLine();
	}

	private static String particleToString(Particle p) {
		return p.getId() + " " +
				p.getRadius() + " " +
				p.getPosition().getX() + " " +
				p.getPosition().getY() + " " +
				p.getVelocity().getX() + " " +
				p.getVelocity().getY() + " " +
				p.calculatePressure() + " \n";
	}
}
