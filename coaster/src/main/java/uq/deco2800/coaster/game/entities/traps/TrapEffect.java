package uq.deco2800.coaster.game.entities.traps;

public class TrapEffect {
	private int damage;
	private int speedModifier;
	
	public TrapEffect() {
		setDamage(0);
		setSpeedModifier(1);
	}

	public int getSpeedModifier() {
		return speedModifier;
	}

	public void setSpeedModifier(int speedModifier) {
		this.speedModifier = speedModifier;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}
	
}
