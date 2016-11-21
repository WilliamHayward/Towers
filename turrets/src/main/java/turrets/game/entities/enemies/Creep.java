package turrets.game.entities.enemies;

import java.util.List;

import turrets.game.entities.AABB;
import turrets.game.entities.Entity;
import turrets.game.mechanics.BodyPart;
import turrets.game.mechanics.Side;
import turrets.graphics.sprites.Sprite;
import turrets.graphics.sprites.SpriteList;

public class Creep extends Enemy {

	public Creep() {
		name = "Creep";
		maxHealth = 5f;
		super.init();
		this.setSprite(new Sprite(SpriteList.CARL));

		setBlocksOtherEntities(false);

		bounds = new AABB(posX, posY, sprite.getWidth() / 32, sprite.getHeight() / 32); // No collision for decoration

		this.setSize(1f, 1f);
		speed = 2f;
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

	@Override
	protected void stateUpdate(long ms) {
		// TODO Auto-generated method stub
		
	}
	
}
