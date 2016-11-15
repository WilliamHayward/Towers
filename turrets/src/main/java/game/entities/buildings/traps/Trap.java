package game.entities.buildings.traps;

import java.util.List;

import game.entities.AABB;
import game.entities.Entity;
import game.entities.buildings.Building;
import game.mechanics.BodyPart;
import game.mechanics.Side;
import graphics.LayerList;

public abstract class Trap extends Building {
	TrapEffect effects;
	
	protected void init() {
		layer = LayerList.TRAPS;

		bounds = new AABB(posX, posY, sprite.getWidth() / 32, sprite.getHeight() / 32); // No collision for decoration

		setCollisionFilter(e -> false);

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
