package turrets.game.entities.buildings.turrets;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableDouble;

import turrets.game.entities.Entity;
import turrets.game.mechanics.BodyPart;
import turrets.game.mechanics.Side;
import turrets.graphics.sprites.AngledSpriteRelation;
import turrets.graphics.sprites.Sprite;
import turrets.graphics.sprites.SpriteList;

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
