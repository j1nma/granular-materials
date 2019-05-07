package algorithms.neighbours;

import models.Particle;

public interface IntegrationMethodWithNeighbours {

	void updatePosition(Particle particle, double dt);
}
