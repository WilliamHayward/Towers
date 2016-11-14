package uq.deco2800.coaster.game.entities.buildings.traps;

import uq.deco2800.coaster.graphics.sprites.Sprite;
import uq.deco2800.coaster.graphics.sprites.SpriteList;

public class AcidTrap extends Trap {
	public AcidTrap() {
		this.setSprite(new Sprite(SpriteList.PLACEHOLDER));
		super.init();
		effects = new TrapEffect();
		effects.setDamage(2);
		effects.setSpeedModifier(0.5f);
	}
}
