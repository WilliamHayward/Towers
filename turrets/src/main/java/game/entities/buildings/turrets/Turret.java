package game.entities.buildings.turrets;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import game.entities.Entity;
import game.entities.buildings.Building;
import game.entities.enemies.Enemy;
import game.world.World;
import graphics.LayerList;
import graphics.Viewport;
import graphics.sprites.AngledSpriteRelation;

public abstract class Turret extends Building {
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
		Enemy target = aim();
		
		cooldownTimer -= (float) ms / 1000;
		if (cooldownTimer <= 0 && target != null) {
			fire(target);
		}
		
	}
	
	protected void fire(Enemy target) {
		System.out.println(name + " fire at " + target.getName());
		cooldownTimer = cooldownLength;
		
	}
	
	protected Enemy aim() {
		List<Entity> allTargets = World.getInstance().getEnemyEntities();
		Entity target = this.getClosest(allTargets);
		if (target == null) {
			return null;
		}
		double targetX = target.getX() + (target.getWidth() / 2);
		double targetY = target.getY() + (target.getHeight() / 2);
		double targetAngle = barrel.getAngle(targetX, targetY);
		
		boolean targetInRange = (targetAngle > (restingAngle - rangeAngle / 2) && targetAngle < (restingAngle + rangeAngle / 2));
		if (!targetInRange) {
			return null;
		}
		barrel.setAngle(targetAngle);
		return (Enemy) target;
	}
	
	@Override
	public void render(GraphicsContext gc, Viewport viewport, long ms) {
		float tileSize = viewport.getTileSideLength();
		float thisX = (this.getX() - viewport.getLeft()) * viewport.getTileSideLength();
		float thisY = (this.getY() - viewport.getTop()) * viewport.getTileSideLength();
		barrel.renderSprite(gc, tileSize, thisX, thisY);
		
		super.render(gc, viewport, ms);
		gc.setFill(Color.ALICEBLUE);
		gc.fillRect(thisX, thisY, cooldownTimer / cooldownLength * this.getWidth() * viewport.getTileSideLength(), 10);

	}
}
