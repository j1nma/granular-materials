package models;

import java.util.List;

public interface Criteria {

	boolean isDone(final List<Particle> particles, final double time);
}