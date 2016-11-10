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

public class MachineGun extends Turret {
	public MachineGun() {
		Sprite sprite = new Sprite(SpriteList.BANK);
		setSprite(sprite);
		MutableDouble angle = new MutableDouble();
		angle.setValue(0);
		barrel = new AngledSpriteRelation(new Sprite(SpriteList.PARTICLE1), (Entity) this, angle, 1f, 0f, 5f, 2f, 0f, 1f);
		setBlocksOtherEntities(false);

		bounds = new AABB(posX, posY, sprite.getWidth() / 32, sprite.getHeight() / 32); // No collision for decoration

		setCollisionFilter(e -> false);
		cooldownLength = 150;
		super.init();
		this.setSize(2f, 2f);
		name = "Machine Gun";
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
	protected void tick(long ms) {
		super.tick(ms);
	}

}
