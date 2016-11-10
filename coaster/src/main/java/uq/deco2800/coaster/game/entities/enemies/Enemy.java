package uq.deco2800.coaster.game.entities.enemies;

import uq.deco2800.coaster.game.entities.Entity;
import uq.deco2800.coaster.game.world.World;

public abstract class Enemy extends Entity {
	
	protected void init() {
		World.getInstance();
	}
	
	@Override
	protected void tick(long ms) {
		
	}
}
