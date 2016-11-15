package game.entities.enemies;

import java.util.List;

import game.entities.AABB;
import game.entities.Entity;
import game.mechanics.BodyPart;
import game.mechanics.Side;
import graphics.sprites.Sprite;
import graphics.sprites.SpriteList;

public class Creep extends Enemy {

	public Creep() {
		name = "Creep";
		super.init();
		this.setSprite(new Sprite(SpriteList.CARL));

		setBlocksOtherEntities(false);

		bounds = new AABB(posX, posY, sprite.getWidth() / 32, sprite.getHeight() / 32); // No collision for decoration

		setCollisionFilter(e -> false);

		this.setSize(1f, 1f);
		speed = 2f;
		health = 5f;
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
	
}
