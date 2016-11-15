package turrets.game.entities.buildings.traps;

import turrets.graphics.sprites.Sprite;
import turrets.graphics.sprites.SpriteList;

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
