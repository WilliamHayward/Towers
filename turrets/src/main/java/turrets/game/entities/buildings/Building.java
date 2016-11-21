package turrets.game.entities.buildings;

import javafx.scene.canvas.GraphicsContext;
import turrets.game.entities.AABB;
import turrets.game.entities.Entity;
import turrets.game.world.World;
import turrets.graphics.Viewport;

public abstract class Building extends Entity {
	protected String name;
	protected boolean built = false;

	public Building() {
		bounds = new AABB(posX, posY, 1, 1); 
	}
	
	public String getName() {
		return name;
	}

	public void build() {
		World.getInstance().addEntity(this);
		built = true;
	}
	
	public void renderUnbuilt(GraphicsContext gc, Viewport viewport, long ms) {
		double alpha;
		if (validBuildingPosition()) {
			alpha = 1;
		} else {
			alpha = 0.5;
		}
		gc.setGlobalAlpha(alpha);
		this.render(gc, viewport, ms);
		gc.setGlobalAlpha(1.0); // TODO: This isn't always working
	}
	
	public abstract boolean validBuildingPosition();
}
