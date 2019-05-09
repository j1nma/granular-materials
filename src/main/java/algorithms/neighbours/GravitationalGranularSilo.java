package algorithms.neighbours;

import models.Criteria;
import models.Particle;
import models.TimeCriteria;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Consider a Lennard-Jones gas formed by particles whose dimensionless parameters are
 * rm (distance where V is minimum) = 1,
 * ε = 2,
 * m = 0.1,
 * and initial velocity v = 10.
 * <p>
 * The cutoff distance of the potential is r = 5.
 * <p>
 * The box containing the gas measures 200 units high x 400 wide with a partition that divides the box
 * into two halves of 200 x 200 and has a central hole of 10 units (qualitatively similar to Fig.1 of TP Nro .3).
 * <p>
 * Initially all the particles are on the left side of the box and as the system evolves they will spread
 * to the other half. The particles are confined in the box so the boundary condition is rigid walls.
 */
public class GravitationalGranularSilo {

	private static double distanceAtMinimum = 1.0; // rM
	private static double holeDepth = 2;
	private static double interactionRadius = 5;
	private static double boxHeight = 1.0;
	private static double boxWidth = 0.3;
	private static double centralHoleUnits = 10;
	private static double RM = 1.0;
	private static double e = 2.0;

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
			double kT,
			double gamma) throws IOException {

		boxHeight = length;
		boxWidth = width;

//		Particle p1 = particles.get(0);
//		Particle p2 = particles.get(1);
//		p1.setPosition(new Vector2D(100, 196));
//		p2.setPosition(new Vector2D(102, 52));
//		p1.setVelocity(new Vector2D(0, 0));
//		p2.setVelocity(new Vector2D(0, 0));
//		List<Particle> test2particles = new ArrayList<>();
//		test2particles.add(p1);
//		test2particles.add(p2);
//		particles = test2particles;

		// Print to buffer and set dummy particles for Ovito grid
		printFirstFrame(buffer, particles);

		Criteria timeCriteria = new TimeCriteria(0);

		// Print frame
		int currentFrame = 1;
		printDeltaT = 0.01;
		int printFrame = (int) Math.ceil(printDeltaT / dt); // Print every 100 frames

		while (!timeCriteria.isDone(particles, time)) {
			time += dt;

//			// Calculate neighbours
//			CellIndexMethod.run(particles,
//					Math.max(boxHeight, boxWidth),
//					(int) Math.floor(Math.max(boxHeight, boxWidth) / interactionRadius),
//					interactionRadius);
//
////			// calcular la sumatoria de fuerzas de cada particula con falsas para las paredes
//			particles.stream().parallel().forEach(p -> {
//				Set<Particle> neighboursCustom = new HashSet<>(p.getNeighbours());
////				addFakeWallParticles(p, neighboursCustom);
//				calculateForce(p, neighboursCustom);
//			});
//
//			// Only at first frame, initialize previous position of Verlet with Euler
//			if (time == dt) {
//				particles.stream().forEach(p -> {
//					if (time == dt) {
//						Vector2D currentForce = p.getForce();
//						double posX = p.getPosition().getX() - dt * p.getVelocity().getX();
//						double posY = p.getPosition().getY() - dt * p.getVelocity().getY();
//						posX += Math.pow(dt, 2) * currentForce.getX() / (2 * p.getMass());
//						posY += Math.pow(dt, 2) * currentForce.getY() / (2 * p.getMass());
//
//						particleIntegrationMethods.put(p,
//								new VerletWithNeighbours(new Vector2D(posX, posY)));
//					}
//				});
//			} else {
//				// Update position
//				particles.stream().parallel().forEach(p -> moveParticle(p, dt));
//			}

			// calculo nueva posicion e imprimo
//			particles.stream().parallel().forEach(p -> {
//				// get new X position
//				double Ax = p.getForce().getX() / p.getMass(); // acceleration in X axis
//				double X = p.getPosition().getX() + Ax * dt; // new X position
//				// get new Y position
//				double Ay = p.getForce().getY() / p.getMass(); // acceleration in Y axis
//				double Y = p.getPosition().getY() + Ay * dt; // new Y position
//
//				p.setPosition(new Vector2D(X, Y));
//			});

			if ((currentFrame % printFrame) == 0) {
				buffer.write(String.valueOf(particles.size() + 2 + ((int) boxHeight / 5)));
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

			// agrego las dummy
			printGridDummyParticles(buffer);

			System.out.println("Current frame: " + currentFrame);
			currentFrame++;
		}

	}


	/**
	 * Calcula la sumatoria de fuerzas sobre la particula
	 *
	 * @param particle
	 * @param neighbours
	 */
	private static void calculateForce(Particle particle, Set<Particle> neighbours) {
		Vector2D F = new Vector2D(0, 0);
		F = neighbours.stream().map(p2 -> {
			// sacar angulo entre particulas  atam2
			double angle = p2.getAngleWith(particle);

			if (particle.getDistanceBetween(p2) <= 0.5) {
				int a = 0;
			}

			// TODO CALCULATE AND PRINT WITH N WITHOUT ANGLES LIKE GERMAN TO SEE IF THEY COINCIDE


			// calculo modulo de la fuerza
			double fraction = RM / particle.getDistanceBetween(p2);
			double force = (12 * e / RM) * (Math.pow(fraction, 13) - Math.pow(fraction, 7));

			// descompongo force con angle para sacar f.x y f.y
			return new Vector2D(force * Math.cos(angle), force * Math.sin(angle));
		}).reduce(F, (F1, F2) -> F1.add(F2));

		// Particle knows its force at THIS frame
		particle.setForce(F);
	}


	/**
	 * Calcula el potencial entre dos particulas
	 *
	 * @param distanceAtMinimum
	 * @param distanceBetweenParticles
	 * @param holeDepth
	 * @return
	 */
	private static double calculatePotential(double distanceAtMinimum,
	                                         double distanceBetweenParticles,
	                                         double holeDepth) {
		double fraction = RM / distanceBetweenParticles;
		return holeDepth * (Math.pow(fraction, 12) - 2.0 * Math.pow(fraction, 6));
	}

	/**
	 * @param particle
	 * @return
	 */
	private static void moveParticle(Particle particle, double dt) {
//		neighbours = neighbours
//				.stream()
//				.filter(n -> !centralHoleInBetween(particle, n))
//				.collect(Collectors.toSet());

//		addFakeWallParticles(particle, neighbours);

		IntegrationMethodWithNeighbours integrationMethod = particleIntegrationMethods.get(particle);
		integrationMethod.updatePosition(particle, dt);
	}

	/**
	 * Dada dos particulas, si estan en cuadrantes distintas
	 *
	 * @param particle1
	 * @param particle2
	 * @return
	 */
	private static boolean centralHoleInBetween(Particle particle1, Particle particle2) {
		double centralHoleLowerLimit = (boxHeight / 2) - (centralHoleUnits / 2);
		double centralHoleHigherLimit = boxHeight - centralHoleLowerLimit;

		double x1 = particle1.getPosition().getX();
		double y1 = particle1.getPosition().getY();
		double x2 = particle2.getPosition().getX();
		double y2 = particle2.getPosition().getY();

		// Both particles at left or right, one above the other
		if (x1 == x2) return false;

		// Calculate line between particles' positions
		double m = (y2 - y1) / (x2 - x1);
		double b = y1 - m * x1;

		// Calculate central hole's height is in that line
		double xCentralHole = boxWidth / 2;
		double yCentralHoleBetweenParticles = m * xCentralHole + b;

		// Return true if central hole's y is between the gap
		// And particles are at different sides
		return yCentralHoleBetweenParticles < centralHoleHigherLimit
				&& yCentralHoleBetweenParticles > centralHoleLowerLimit
				&& ((x1 < boxWidth / 2 && x2 > boxWidth / 2)
				|| (x1 > boxWidth / 2 && x2 < boxWidth / 2));
	}

//	/**
//	 * Agrega en neighbours set las particulas falsas necesarias
//	 *
//	 * @param particle
//	 * @param neighbours
//	 */
//	private static void addFakeWallParticles(Particle particle, Set<Particle> neighbours) {
//		double centralHoleLowerLimit = (boxHeight / 2) - (centralHoleUnits / 2);
//		double centralHoleHigherLimit = (boxHeight / 2) + (centralHoleUnits / 2);
//
//		int fakeId = -1;
//
//		// Analyse left wall
//		double distanceToLeftWall = particle.getPosition().getX();
//		if (distanceToLeftWall <= interactionRadius) {
//			// Add fake wall particle to its left at the box's left wall
//			Particle leftWallParticle = new Particle(fakeId--, Double.POSITIVE_INFINITY);
//			leftWallParticle.setPosition(new Vector2D(0.0, particle.getPosition().getY()));
//			leftWallParticle.setVelocity(Vector2D.ZERO);
//			neighbours.add(leftWallParticle);
//		}
//
//		// Analyse right wall
//		double distanceToRightWall = boxWidth - particle.getPosition().getX();
//		if (distanceToRightWall <= interactionRadius) {
//			// Add fake wall particle to its left at the box's right wall
//			Particle rightWallParticle = new Particle(fakeId--, Double.POSITIVE_INFINITY);
//			rightWallParticle.setPosition(new Vector2D(boxWidth, particle.getPosition().getY()));
//			rightWallParticle.setVelocity(Vector2D.ZERO);
//			neighbours.add(rightWallParticle);
//		}
//
//		// Analyse up wall
//		double distanceToTopWall = boxHeight - particle.getPosition().getY();
//		if (distanceToTopWall <= interactionRadius) {
//			Particle topWallParticle = new Particle(fakeId--, Double.POSITIVE_INFINITY);
//			topWallParticle.setPosition(new Vector2D(particle.getPosition().getX(), boxHeight));
//			topWallParticle.setVelocity(Vector2D.ZERO);
//			neighbours.add(topWallParticle);
//		}
//
//		// Analyse down wall
//		double distanceToLowerWall = particle.getPosition().getY();
//		if (distanceToLowerWall <= interactionRadius) {
//			Particle lowerWallParticle = new Particle(fakeId--, Double.POSITIVE_INFINITY);
//			lowerWallParticle.setPosition(new Vector2D(particle.getPosition().getX(), 0.0));
//			lowerWallParticle.setVelocity(Vector2D.ZERO);
//			neighbours.add(lowerWallParticle);
//		}
//
//		// Particle between ys of central hole
//		if (particle.getPosition().getY() < centralHoleHigherLimit
//				&& particle.getPosition().getY() > centralHoleLowerLimit) {
//			Particle higherHoleWallParticle = new Particle(fakeId--, Double.POSITIVE_INFINITY);
//			higherHoleWallParticle.setPosition(new Vector2D(boxWidth / 2, centralHoleHigherLimit));
//			higherHoleWallParticle.setVelocity(Vector2D.ZERO);
//			double particleDistance = particle.getPosition().distance(higherHoleWallParticle.getPosition());
//			if (particleDistance <= interactionRadius) {
//				neighbours.add(higherHoleWallParticle);
//			}
//
//			Particle lowerHoleWallParticle = new Particle(fakeId-- / 2, Double.POSITIVE_INFINITY);
//			lowerHoleWallParticle.setPosition(new Vector2D(boxWidth / 2, centralHoleLowerLimit));
//			lowerHoleWallParticle.setVelocity(Vector2D.ZERO);
//			particleDistance = particle.getPosition().distance(lowerHoleWallParticle.getPosition());
//			if (particleDistance <= interactionRadius) {
//				neighbours.add(lowerHoleWallParticle);
//			}
//		} else {
//			// Particle close to central line
//			Particle middleWallParticle = new Particle(fakeId--, Double.POSITIVE_INFINITY);
//			middleWallParticle.setPosition(new Vector2D(boxWidth / 2, particle.getPosition().getY()));
//			middleWallParticle.setVelocity(Vector2D.ZERO);
//			double particleDistance = particle.getPosition().distance(middleWallParticle.getPosition());
//			if (particleDistance <= interactionRadius) {
//				neighbours.add(middleWallParticle);
//			}
//		}
//	}

	private static void printFirstFrame(BufferedWriter buff, List<Particle> particles) throws IOException {
		// Print dummy particles to simulation output file
//		buff.write(String.valueOf(particles.size() + 2 + ((int) boxHeight / 5)));
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
		dummy1.setPosition(new Vector2D(0, 0));
		dummy1.setVelocity(new Vector2D(0, 0));
		dummy2.setPosition(new Vector2D(boxWidth, boxHeight));
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
