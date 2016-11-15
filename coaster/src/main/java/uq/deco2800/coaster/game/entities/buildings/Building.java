package uq.deco2800.coaster.game.entities.buildings;

import uq.deco2800.coaster.game.entities.AABB;
import uq.deco2800.coaster.game.entities.Entity;

public abstract class Building extends Entity {
	protected String name;

	public Building() {
		bounds = new AABB(posX, posY, 1, 1); 
	}
	public String getName() {
		return name;
	}
}
