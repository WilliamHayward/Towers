package game.entities.enemies;

import java.util.List;
import java.util.Map;

import game.entities.Entity;
import game.entities.buildings.traps.Trap;
import game.entities.buildings.traps.TrapEffect;
import game.world.Coordinate;
import game.world.World;
import graphics.LayerList;
import graphics.sprites.Sprite;
import graphics.sprites.SpriteList;

public abstract class Enemy extends Entity {
	protected Map<Integer, Coordinate> waypoints;
	protected int destinationWaypoint = 0;
	protected int direction = 1;
	protected float speed;
	protected float health;
	protected String name = "Enemy";
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected void init() {
		layer = LayerList.ENEMIES;
		
	}
	
	public TrapEffect getTraps() {
		TrapEffect effect = new TrapEffect();
		List<Entity> traps = World.getInstance().getTrapEntities();
		Sprite sprite = new Sprite(SpriteList.CARL);
		for (Entity trap: traps) {
			if (this.getBounds().collides(trap.getBounds())) {
				Trap trapActual = (Trap) trap;
				effect.add(trapActual.getEffects());
				//sprite = new Sprite(SpriteList.PLACEHOLDER);
			}
		}
		this.setSprite(sprite);
		return effect;
	}
	
	@Override
	protected void tick(long ms) {
		float seconds = ms / (float) 1000;
		
		Coordinate destination = waypoints.get(destinationWaypoint);
		
		float xDiff = destination.getX() - this.getX();
		float yDiff = destination.getY() - this.getY();
		float horizontalSpeed = 0;
		float verticalSpeed = 0;
		
		TrapEffect trap = getTraps();
		
		// Damage
		float damage = trap.getDPS() * seconds / this.getCollisionScale(ms);
		health -= damage;

		// Movement
		float modifiedSpeed = speed * trap.getSpeedModifier();
		float scaledSpeed = modifiedSpeed * seconds / this.getCollisionScale(ms);
		scaledSpeed *= 2;
		
		if (Math.abs(xDiff) >= Math.abs(scaledSpeed)) {
			horizontalSpeed = modifiedSpeed * Math.signum(xDiff);
		} else {
			this.setPosition(destination.getX(), this.getY());
		}
		
		if (Math.abs(yDiff) >= Math.abs(scaledSpeed)) {
			verticalSpeed = modifiedSpeed * Math.signum(yDiff);
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
		if (health < 0) {
			die();
		}
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
	
	private void die() {
		this.delete();
	}
	
	public void setWaypoints(Map<Integer, Coordinate> waypoints) {
		this.waypoints = waypoints;
	}
	
	protected void onDeath(Entity cause) {
		this.delete();
	}
}
