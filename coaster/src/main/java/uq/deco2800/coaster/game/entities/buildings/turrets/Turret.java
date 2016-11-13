package uq.deco2800.coaster.game.entities.buildings.turrets;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import uq.deco2800.coaster.game.entities.Entity;
import uq.deco2800.coaster.game.world.World;
import uq.deco2800.coaster.graphics.LayerList;
import uq.deco2800.coaster.graphics.Viewport;
import uq.deco2800.coaster.graphics.sprites.AngledSpriteRelation;

public abstract class Turret extends Entity {
	protected String name;
	private float cooldownTimer;
	protected float cooldownLength;
	protected int range;
	protected int rangeAngle;
	protected int restingAngle;
	
	protected AngledSpriteRelation barrel;
	
	public Turret() {
	}
	
	protected void init() {
		layer = LayerList.TURRETS;
		cooldownTimer = cooldownLength;
		barrel.setAngle(restingAngle);
	}
	
	@Override
	public void updatePhysics(long ms) {
	}
	
	@Override
	protected void tick(long ms) {
		cooldownTimer -= (float) ms / 1000;
		if (cooldownTimer <= 0) {
			System.out.println("Fire " + name);
			cooldownTimer = cooldownLength;
		}
		List<Entity> allTargets = World.getInstance().getEnemyEntities();
		Entity target = this.getClosest(allTargets);
		double targetX = target.getX() + (target.getWidth() / 2);
		double targetY = target.getY() + (target.getHeight() / 2);
		double targetAngle = barrel.getAngle(targetX, targetY);
		
		boolean targetInRange = (targetAngle > (restingAngle - rangeAngle / 2) && targetAngle < (restingAngle + rangeAngle / 2));
		//targetInRange = targetInRange && this.distanceFrom(target) > range;
		if (target == null || !targetInRange) {
			//barrel.setAngle(restingAngle);
		} else {
			barrel.setAngle(targetAngle);
		}
	}
	
	@Override
	public void render(GraphicsContext gc, Viewport viewport, long ms) {
		float tileSize = viewport.getTileSideLength();
		float thisX = (this.getX() - viewport.getLeft()) * viewport.getTileSideLength();
		float thisY = (this.getY() - viewport.getTop()) * viewport.getTileSideLength();
		barrel.renderSprite(gc, tileSize, thisX, thisY);
		
		super.render(gc, viewport, ms);

	}
	
	protected void fire() {
		
	}
	
	public String getName() {
		return name;
	}
}
