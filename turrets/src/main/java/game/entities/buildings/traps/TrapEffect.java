package game.entities.buildings.traps;

public class TrapEffect {
	private float damage;
	private float speedModifier;
	
	public TrapEffect() {
		setDamage(0);
		setSpeedModifier(1);
	}

	public float getSpeedModifier() {
		return speedModifier;
	}

	public void setSpeedModifier(float speedModifier) {
		this.speedModifier = speedModifier;
	}

	public float getDPS() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}
	
	public void add(TrapEffect other) {
		damage += other.getDPS();
		speedModifier *= other.getSpeedModifier(); 
	}
	
}
