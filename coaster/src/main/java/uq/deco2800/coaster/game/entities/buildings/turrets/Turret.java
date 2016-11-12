package uq.deco2800.coaster.game.entities.buildings.turrets;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import uq.deco2800.coaster.game.entities.Entity;
import uq.deco2800.coaster.game.world.World;
import uq.deco2800.coaster.graphics.Viewport;
import uq.deco2800.coaster.graphics.sprites.AngledSpriteRelation;

public abstract class Turret extends Entity {
	protected String name;
	private int cooldownTimer;
	protected int cooldownLength;
	protected int range;
	
	protected AngledSpriteRelation barrel;
	
	public Turret() {
	}
	
	protected void init() {
		cooldownTimer = cooldownLength;
	}
	
	@Override
	public void updatePhysics(long ms) {
	}
	
	@Override
	protected void tick(long ms) {
		cooldownTimer--;
		if (cooldownTimer == 0) {
			System.out.println("Fire " + name);
			cooldownTimer = cooldownLength;
		}
		List<Entity> allTargets = World.getInstance().getEnemiesEntities();
		Entity target = this.getClosest(World.getInstance().getEnemiesEntities());
		if (target == null || this.distanceFrom(target) > range) {
			barrel.setAngle(90);
		} else {
			barrel.setTarget(target.getX(), target.getY());
		}
	}
	
	@Override
	public void render(GraphicsContext gc, Viewport viewport, long ms) {
		super.render(gc, viewport, ms);

		float tileSize = viewport.getTileSideLength();
		float thisX = (this.getX() - viewport.getLeft()) * viewport.getTileSideLength();
		float thisY = (this.getY() - viewport.getTop()) * viewport.getTileSideLength();
		barrel.renderSprite(gc, tileSize, thisX, thisY);
	}
	
	protected void fire() {
		
	}
	
	public String getName() {
		return name;
	}
}
