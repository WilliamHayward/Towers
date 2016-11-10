package uq.deco2800.coaster.game.entities.buildings.turrets;

import uq.deco2800.coaster.game.entities.Entity;

public abstract class Turret extends Entity {
	protected String name;
	private int cooldownTimer;
	protected int cooldownLength;
	
	public Turret() {
	}
	
	protected void init() {
		cooldownTimer = cooldownLength;
	}
	
	@Override
	public void updatePhysics(long ms) {
	}
	
	@Override
	protected void tick(long ms) {
		cooldownTimer--;
		if (cooldownTimer == 0) {
			System.out.println("Fire " + name);
			cooldownTimer = cooldownLength;
		}
	}
	
	protected void fire() {
		
	}
	
	public String getName() {
		return name;
	}
}
