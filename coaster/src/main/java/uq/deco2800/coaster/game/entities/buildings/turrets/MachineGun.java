package uq.deco2800.coaster.game.entities.buildings.turrets;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableDouble;

import uq.deco2800.coaster.game.entities.Entity;
import uq.deco2800.coaster.game.mechanics.BodyPart;
import uq.deco2800.coaster.game.mechanics.Side;
import uq.deco2800.coaster.graphics.sprites.AngledSpriteRelation;
import uq.deco2800.coaster.graphics.sprites.Sprite;
import uq.deco2800.coaster.graphics.sprites.SpriteList;

public class MachineGun extends Turret {
	public MachineGun() {
		Sprite sprite = new Sprite(SpriteList.TURRET_BASE);
		setSprite(sprite);
		MutableDouble angle = new MutableDouble();
		angle.setValue(0);
		
		barrel = new AngledSpriteRelation(new Sprite(SpriteList.BARREL_MACHINE_GUN), (Entity) this, angle, 
				0.5f, 0f, 1f, 0.25f, 0f, 0.125f);
		setBlocksOtherEntities(false);
		cooldownLength = 2f;
		super.init();
		this.setSize(1f, 0.5f);
		name = "Machine Gun";
		range = 5;
		rangeAngle = 90;
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
