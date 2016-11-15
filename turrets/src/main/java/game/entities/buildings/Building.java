package game.entities.buildings;

import game.entities.AABB;
import game.entities.Entity;

public abstract class Building extends Entity {
	protected String name;

	public Building() {
		bounds = new AABB(posX, posY, 1, 1); 
	}
	public String getName() {
		return name;
	}
}
