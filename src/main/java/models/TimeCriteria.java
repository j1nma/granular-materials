package models;

import java.util.List;

public class TimeCriteria implements Criteria {

	private final double limitTime;

	public TimeCriteria(double limit) {
		this.limitTime = limit;
	}

	@Override
	public boolean isDone(List<Particle> particles, double time) {
		return time > limitTime;
	}
}