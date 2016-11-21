package turrets.game.entities.buildings.turrets;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import turrets.core.input.InputManager;
import turrets.game.entities.Entity;
import turrets.game.entities.buildings.Building;
import turrets.game.entities.enemies.Enemy;
import turrets.game.world.World;
import turrets.game.world.WorldTiles;
import turrets.graphics.LayerList;
import turrets.graphics.Viewport;
import turrets.graphics.sprites.AngledSpriteRelation;

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
		if (!built) {
			this.setX((float) Math.floor(InputManager.getMouseTileX()));
			this.setY((float) Math.floor(InputManager.getMouseTileY()));
			return;
		}
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
		float thisX = (this.getX() - viewport.getLeft()) * viewport.getTileSideLength();
		float thisY = (this.getY() - viewport.getTop()) * viewport.getTileSideLength();
		
		float tileSize = viewport.getTileSideLength();
		
		barrel.renderSprite(gc, tileSize, thisX, thisY);
		
		super.render(gc, viewport, ms);
		if (built) {
			gc.setFill(Color.ALICEBLUE);
			gc.fillRect(thisX, thisY, cooldownTimer / cooldownLength * this.getWidth() * viewport.getTileSideLength(), 10);
		}
		Enemy target = aim();
		if (target == null) {
			return;
		}
		double targetX = target.getX() + (target.getWidth() / 2);
		double targetY = target.getY() + (target.getHeight() / 2);
		
		Line line = new Line();
		line.setStartX(thisX);
		line.setStartY(thisY);
		line.setEndX(targetX);
		line.setEndY(targetY);
	}


	@Override
	public boolean validBuildingPosition() {
		WorldTiles tiles = World.getInstance().getTiles();
		int left = (int) Math.floor(this.getX());
		int right;
		if (this.getWidth() > 1) { 
			right = (int) Math.floor(this.getX() + this.getWidth());
		} else {
			right = left;
		}
		int top = (int) Math.floor(this.getY());
		// Check left
		if (tiles.get(left, top).getTileType().isObstacle()) {
			return false;
		}
		// Check right
		if (tiles.get(right, top).getTileType().isObstacle()) {
			System.out.println("Right");
			return false;
		}
		
		// Check top
		for (int x = left; x <= right; x++) {
			if (tiles.get(x, top).getTileType().isObstacle()) {
				return false;
			}
			if (!tiles.get(x, top - 1).getTileType().isObstacle()) {
				return false;
			}
		}
		return true;
	}
}
