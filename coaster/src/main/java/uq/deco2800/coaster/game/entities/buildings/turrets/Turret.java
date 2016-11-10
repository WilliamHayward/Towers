package uq.deco2800.coaster.game.entities.buildings.turrets;

import javafx.scene.canvas.GraphicsContext;
import uq.deco2800.coaster.core.input.InputManager;
import uq.deco2800.coaster.game.entities.Entity;
import uq.deco2800.coaster.graphics.Viewport;
import uq.deco2800.coaster.graphics.sprites.AngledSpriteRelation;
import uq.deco2800.coaster.graphics.sprites.Sprite;

public abstract class Turret extends Entity {
	protected String name;
	private int cooldownTimer;
	protected int cooldownLength;
	
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
		barrel.setTarget(InputManager.getMouseTileX(), InputManager.getMouseTileY());
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
