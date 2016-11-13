package uq.deco2800.coaster.game.entities.buildings.turrets;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableDouble;

import uq.deco2800.coaster.game.entities.AABB;
import uq.deco2800.coaster.game.entities.Entity;
import uq.deco2800.coaster.game.mechanics.BodyPart;
import uq.deco2800.coaster.game.mechanics.Side;
import uq.deco2800.coaster.graphics.sprites.AngledSpriteRelation;
import uq.deco2800.coaster.graphics.sprites.Sprite;
import uq.deco2800.coaster.graphics.sprites.SpriteList;

public class Cannon extends Turret {
	public Cannon() {
		Sprite sprite = new Sprite(SpriteList.TURRET_BASE);
		setSprite(sprite);
		MutableDouble angle = new MutableDouble();
		angle.setValue(0);

		barrel = new AngledSpriteRelation(new Sprite(SpriteList.BARREL_CANNON), (Entity) this, angle,
				1.5f, -0.5f, 2f, 1.25f, -0.3f, 0.625f);
		setBlocksOtherEntities(false);
		
		bounds = new AABB(posX, posY, sprite.getWidth() / 32, sprite.getHeight() / 32); // No collision for decoration

		setCollisionFilter(e -> false);
		cooldownLength = 5f;
		super.init();
		this.setSize(2.5f, 1.25f);
		name = "Cannon";
		range = 5;
		rangeAngle = 120;
		restingAngle = 90;
		barrel.setAngle(restingAngle);
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
