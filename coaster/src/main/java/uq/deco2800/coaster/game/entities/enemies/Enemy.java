package uq.deco2800.coaster.game.entities.enemies;

import java.util.Map;

import uq.deco2800.coaster.game.entities.Entity;
import uq.deco2800.coaster.game.world.Coordinate;

public abstract class Enemy extends Entity {
	protected Map<Integer, Coordinate> waypoints;
	protected int destinationWaypoint = 0;
	protected int direction = 1;
	protected float speed;
	protected void init() {
		
	}
	
	@Override
	protected void tick(long ms) {
		Coordinate destination = waypoints.get(destinationWaypoint);
		float horizontalDifference = destination.getX() - this.getX();
		float verticalDifference = destination.getY() - this.getY();
		float horizontalSpeed = 0;
		float verticalSpeed = 0;
		if (horizontalDifference != 0) {
			horizontalSpeed = speed * Math.signum(horizontalDifference);
		} else {
			this.setPosition(destination.getX(), this.getY());
		}
		if (verticalDifference != 0) {
			verticalSpeed = speed * Math.signum(verticalDifference);
		} else {
			this.setPosition(this.getX(), destination.getY());
		}
		if (horizontalSpeed == 0 && verticalSpeed == 0) {
			destinationWaypoint += direction;
			if (waypoints.get(destinationWaypoint) == null) {
				direction *= -1;
				destinationWaypoint += direction;
			}
		}
		this.setVelocity(horizontalSpeed, verticalSpeed);
	}
	
	public void setWaypoints(Map<Integer, Coordinate> waypoints) {
		this.waypoints = waypoints;
	}
}
