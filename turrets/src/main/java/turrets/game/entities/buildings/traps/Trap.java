package turrets.game.entities.buildings.traps;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import turrets.game.entities.AABB;
import turrets.game.entities.Entity;
import turrets.game.entities.buildings.Building;
import turrets.game.mechanics.BodyPart;
import turrets.game.mechanics.Side;
import turrets.graphics.LayerList;
import turrets.graphics.Viewport;

public abstract class Trap extends Building {
	TrapEffect effects;
	
	protected void init() {
		layer = LayerList.TRAPS;
		
		bounds = new AABB(posX, posY + 1f, 1f, 2f);

		this.setSize(1f, 2f);
	}
	@Override
	protected void tick(long ms) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onEntityCollide(List<Entity> entities, List<BodyPart> hitLocations) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onTerrainCollide(int tileX, int tileY, Side side) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void entityLoop(long ms) {
		tick(ms);
	}

	@Override
	protected void onDeath(Entity cause) {
		// TODO Auto-generated method stub
		
	}
	
	public TrapEffect getEffects() {
		return effects;
	}
	
	@Override
	public void updatePhysics(long ms) {
	}
	
}
