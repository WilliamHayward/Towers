package game.entities.buildings.traps;

import graphics.sprites.Sprite;
import graphics.sprites.SpriteList;

public class AcidTrap extends Trap {
	public AcidTrap() {
		this.setSprite(new Sprite(SpriteList.PLACEHOLDER));
		super.init();
		name = "Acid Trap";
		effects = new TrapEffect();
		effects.setDamage(2);
		effects.setSpeedModifier(0.5f);
	}
}
