package turrets.game.entities.buildings.turrets;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableDouble;

import turrets.game.entities.AABB;
import turrets.game.entities.Entity;
import turrets.game.mechanics.BodyPart;
import turrets.game.mechanics.Side;
import turrets.graphics.sprites.AngledSpriteRelation;
import turrets.graphics.sprites.Sprite;
import turrets.graphics.sprites.SpriteList;

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
