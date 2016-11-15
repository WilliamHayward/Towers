package turrets.game.entities.buildings;

import turrets.game.entities.AABB;
import turrets.game.entities.Entity;

public abstract class Building extends Entity {
	protected String name;

	public Building() {
		bounds = new AABB(posX, posY, 1, 1); 
	}
	public String getName() {
		return name;
	}
}
