package uq.deco2800.coaster.game.entities;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uq.deco2800.coaster.game.world.World;

public class PlayerStats {
	private static final Logger logger = LoggerFactory.getLogger(PlayerStats.class);
	// subclass once it's done
	// Attack
	private int baseDamage;
	private int firingRate;
	private int firingAccuracy;
	// misc.
	private int baseMana;
	private int baseHealth;
	private int experiencePoints;
	private int playerLevel;
	private int killCount;
	private int bossKillCount;
	private HashSet<String> bossTypesKilled;
	private int pickupCount;
	public PlayerStats() {
		this.baseDamage = 20;
		this.baseMana = 100;
		this.baseHealth = 1000;
		this.firingRate = 300; // Duration between shots in ms
		this.firingAccuracy = 15;// num of degrees deviation of bullet (will be
		// angle +/- random(accuracy)
		this.experiencePoints = 0;
		this.killCount = 0;
		this.bossKillCount = 0;
		this.bossTypesKilled = new HashSet<>();
		this.pickupCount = 0;
		this.playerLevel = 1;
	}
	
	/**
	 * This method resets the maximum mana points the player has
	 *
	 * @param additionalMana the amount of additional mana points
	 */
	public void addMaxMana(int additionalMana) {
		this.baseMana += additionalMana;
	}

	/**
	 * Get player's mana points
	 *
	 * @return player's mana points
	 */
	public int getMana() {
		return this.baseMana;
	}

	/**
	 * This method resets the maximum health points the player has
	 *
	 * @param additionalHealth the amount of additional health points
	 */
	public void addMaxHealth(int additionalHealth) {
		this.baseHealth += additionalHealth;
	}

	/**
	 * Get player's maximum health points
	 *
	 * @return player's maximum health points
	 */
	public int getMaxHealth() {
		return this.baseHealth;
	}

	/**
	 * Set player's base damage
	 *
	 * @param newDamage player's damage points
	 */
	public void setBaseDamage(int newDamage) {
		this.baseDamage = newDamage;
	}

	/**
	 * Method to calculate player's damage points including any damage modifiers
	 * given by passive skills the player activated
	 *
	 * @return the amount of damage the player can deal
	 */
	public int calculateDamage() {
		int damageModifier = 0;
		return damageModifier + baseDamage;
	}

	/**
	 * Get player's base damage
	 *
	 * @return player's base damage
	 */
	public int getDamage() {
		return this.baseDamage;
	}

	/**
	 * Set player's base armour
	 *
	 * @param newArmour armour points the player has
	 */
	public void setBaseArmour(int newArmour) {
	}
	
	/**
	 * Get the player's firing rate
	 *
	 * @return player's firing rate
	 */
	public long getFiringRate() {
		return this.firingRate;
	}

	public void addFiringAccuracy(int increaseAccuracy) {
		this.firingAccuracy = this.firingAccuracy - increaseAccuracy;
	}

	public int getFiringAccuracy() {
		return this.firingAccuracy;
	}

	/**
	 * This method adds player's experience points to the player's stats
	 *
	 * @param additionalExperiencePoints experience points gained by the player
	 */
	public void addExperiencePoints(int additionalExperiencePoints) {
		this.experiencePoints += additionalExperiencePoints;
	}

	/**
	 * A method to get the player's current experience points
	 *
	 * @return player's experience points
	 */
	public int getExperiencePoints() {
		return this.experiencePoints;
	}

	/**
	 * Set the player's chance of dealing critical damage when an attack is
	 * performed
	 *
	 * @param criticalHitChance The chance of the player dealing a critical hit
	 */
	public void setBaseCritChance(int criticalHitChance) {
	}

	/**
	 * Set the player's base critical damage points
	 *
	 * @param criticalHitDamage The player's damage points when a critical hit
	 *            is dealt
	 */
	public void setBaseCritDamage(int criticalHitDamage) {
	}

	/**
	 * Set the player's poison damage
	 *
	 * @param poison The amount of points which the poison deals
	 */
	public void setBasePoison(int poison) {
	}

	/**
	 * @return the number of pickups the Player has picked up
	 */
	public int getPickupCount() {
		return pickupCount;
	}

	/**
	 * Increments the pickups count and attemps to unlock associated
	 * Achievements.
	 */
	public void addPickUpCount() {
		pickupCount++;
	}

	/**
	 * Adds to the Player's running total of accumulated coins
	 *
	 * @param coinValue the value of the Coin
	 */
	public void addCoinCount(int coinValue) {
	}

	/**
	 * Add the number of mobs the player has killed
	 *
	 * @param kills The number of mobs the player has killed
	 */
	public void addKillCount(int kills) {
		this.killCount += kills;
	}

	/**
	 * Get the number of mobs the player has killed
	 *
	 * @return the number of mobs killed by the player
	 */
	public int getKillCount() {
		return killCount;
	}

	/**
	 * Increment the number of BossNPC the player has killed. And check for any
	 * unlocks
	 *
	 * @param bossID The ID for the boss
	 */
	public void addBossKill(String bossID) {
		bossKillCount++;
		bossTypesKilled.add(bossID);
		unlockBossKillAchievement();
	}

	/**
	 * Get the number of BossNPC the player has killed
	 *
	 * @return the number of BossNPC killed by the player
	 */
	public int getBossKillCount() {
		return bossKillCount;
	}

	/**
	 * @return the Set containing each boss type killed
	 */
	public Set<String> getBossesKilled() {
		return bossTypesKilled;
	}

	private void unlockBossKillAchievement() {
		if (bossTypesKilled.size() == 8) {
			World.getInstance().getPlayerEntities().stream().filter(p -> p.stats == this).findFirst()
					.get();
		}
	}

	/**
	 * This method is called when the player has gained enough experience points
	 * to level up
	 */
	public void levelUp() {
		logger.info("level up");
		playerLevel += 1;
		experiencePoints = 0;
		levelBenefits(playerLevel);
	}

	/**
	 * This method outputs benefits the player recieves once they reach a
	 * certain level
	 *
	 * @param level
	 */
	public void levelBenefits(int level) {
	}

	/**
	 * Get player's current level
	 *
	 * @return player's current level
	 */
	public int getPlayerLevel() {
		return this.playerLevel;
	}
}
