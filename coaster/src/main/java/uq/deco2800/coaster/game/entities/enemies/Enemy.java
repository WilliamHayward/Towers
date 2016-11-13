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
		float seconds = ms / (float) 1000;
		float scaledSpeed = speed * seconds / this.getCollisionScale(ms);
		scaledSpeed *= 2;
		Coordinate destination = waypoints.get(destinationWaypoint);
		
		float xDiff = destination.getX() - this.getX();
		float yDiff = destination.getY() - this.getY();
		float horizontalSpeed = 0;
		float verticalSpeed = 0;
		
		if (Math.abs(xDiff) >= Math.abs(scaledSpeed)) {
			horizontalSpeed = speed * Math.signum(xDiff);
		} else {
			this.setPosition(destination.getX(), this.getY());
		}
		
		if (Math.abs(yDiff) >= Math.abs(scaledSpeed)) {
			verticalSpeed = speed * Math.signum(yDiff);
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

	@Override
	public void updatePhysics(long ms) {
		float seconds = ms / (float) 1000;
		int collisionScale = getCollisionScale(ms);

		// distance travelled in one step
		float diffX = velX * seconds / collisionScale;
		float diffY = velY * seconds / collisionScale;
		// process things one step at a time
		for (int step = 0; step < collisionScale; step++) {
			// increment position
			bounds.setX(bounds.left() + diffX);
			bounds.setY(bounds.top() + diffY);
		}
		// End step

		posX = bounds.left();
		posY = bounds.top();

		if (velX != 0) {
			renderFacing = (int) Math.signum(velX);
		}
	}
	
	public void setWaypoints(Map<Integer, Coordinate> waypoints) {
		this.waypoints = waypoints;
	}
}
