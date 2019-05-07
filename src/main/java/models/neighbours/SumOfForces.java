package models.neighbours;

import models.Particle;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Set;

public interface SumOfForces {

	Vector2D sumOfForces(final Particle particle, final Set<Particle> neighbours);
}
