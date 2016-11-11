package uq.deco2800.coaster.game.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.mutable.MutableDouble;
import uq.deco2800.coaster.core.input.GameAction;
import uq.deco2800.coaster.core.input.GameInput;
import uq.deco2800.coaster.core.input.InputManager;
import uq.deco2800.coaster.core.sound.SoundCache;
import uq.deco2800.coaster.game.debug.Debug;
import uq.deco2800.coaster.game.entities.buildings.turrets.MachineGun;
import uq.deco2800.coaster.game.entities.buildings.turrets.Turret;
import uq.deco2800.coaster.game.mechanics.BodyPart;
import uq.deco2800.coaster.game.mechanics.Side;
import uq.deco2800.coaster.game.world.Room;
import uq.deco2800.coaster.game.world.World;
import uq.deco2800.coaster.graphics.Viewport;
import uq.deco2800.coaster.graphics.sprites.Sprite;
import uq.deco2800.coaster.graphics.sprites.SpriteList;
import uq.deco2800.coaster.graphics.sprites.SpriteRelation;

import java.util.*;

import static uq.deco2800.coaster.core.input.InputManager.justPressed;


public class Player extends BasicMovingEntity {

	protected static final float EPSILON = 0.00001f;


	protected static final long SPECIAL_ATTACK_THRESHOLD = 2000;
	// Need to hold the button for 2 seconds
	protected static final long SPECIAL_ATTACK_FIRE_DURATION = 1000;
	// The lazor fires for 1 second
	
	public int viewDistance = 1200; // view distance in pixels

	protected float wallSlidingFallModifier = 0.5f;
	protected float wallSlidingTerminalModifier = 0.2f;

	protected float dashSpeed = 25f;
	protected long dashDuration = 250L; // 250 ms

	protected float slideSpeed = 12.5f;
	protected long slideDuration = 500L; // 500 ms

	protected float wallJumpSpeedModifier = 2f;
	protected long wallJumpDuration = 100L; // 100 ms

	protected long knockBackDuration = 1000L;
	protected long knockBackEndDuration = 750L;
	protected long knockBackRenderTimer = 0L; // used to flip renderFlag
	protected long knockBackLastRenderTime = 0L;
	protected int knockBackRenderGap = 75;

	protected float knockBackSpeedX = 15f;
	protected float knockBackSpeedY = -10f;

	GameInput playerInput = new GameInput();
	PlayerStats stats = new PlayerStats();

	protected static final int TARGET_NPC_KILL_COUNT = 100; // Need to kill 100
	// mobs for the boss
	// spawn
	
	private int cooldown = 0;
	
	protected int turretTimer;
	protected long actionTimer = -1L; // timer used for sliding, dashing and air
	// dashing. 1000f = 1s
	protected long wallJumpTimer = -1L; // Timer used to make wall jump motions
	// feel fluid.
	protected long knockBackTimer = -1L;
	protected boolean airDashAvailable = true;
	protected boolean doubleJumpAvailable = true;
	protected boolean jumpAvailable;
	protected boolean invincible = false;
	protected int inputDir;

	// Set directions for mouse shooting and sprite animation
	protected double aimAngle;
	protected int accuracy = 5;// num of degrees deviation of bullet (will be
	// angle +/- random(accuracy)

	protected MutableDouble armRenderAngle = new MutableDouble();
	protected MutableDouble headRenderAngle = new MutableDouble();
	protected MutableDouble weaponRenderAngle = new MutableDouble();

	protected boolean specialAttackCharging;
	protected boolean specialAttackFiring;
	protected long specialAttackChargeTimer;
	protected long specialAttackFireTimer;

	protected int genericBulletDamage;
	protected int bulletSpeed;
	protected long experiencePoints;
	protected long firingRateTracker;


	// Mount CPS
	private boolean talking = false;


	protected static final float BASE_JUMP_SPEED = -20f;
	protected static final float BASE_MOVE_SPEED = 10f;
	
	static float scale = 1f;

	protected static final float BASE_WIDTH = 1f * scale;
	protected static final float BASE_HEIGHT = 1.41f * scale;
	protected static final float CROUCH_WIDTH = 0.96f * scale;
	protected static final float CROUCH_HEIGHT = 1f * scale;


	protected int healing;
	protected int healTickCount = 0;


	// Spell cooldown timers to be displayed on the screen.
	// These times are not implemented into spell logic at the moment
	private List<Double> cooldowns = new ArrayList<>();
	private boolean healingActivated = false;


	// weapons + armour
	protected ArrayList<String> activeWeapons = new ArrayList<>();
	
	private List<Integer> spellPhases = new ArrayList<>();
	private List<Integer> currentSpellPhase = new ArrayList<>();
	private List<ArrayList<Integer>> spellLoopIterations = new ArrayList<>();
	private List<ArrayList<Integer>> spellLoopTimings = new ArrayList<>();

	private List<Integer> currentSpellLoopIteration = new ArrayList<>();
	private List<Integer> currentSpellLoopTiming = new ArrayList<>();
	private List<Boolean> usingSpells = new ArrayList<>();

	private List<PlayerBuff> buffList = new ArrayList<PlayerBuff>();
	Iterator<PlayerBuff> iter = buffList.iterator();

	protected boolean updateHud = false;
	private Sprite drawSkill;
	private Sprite drawSkill2;
	private String spellKey;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected String name;

	private String spellKey2;
	private boolean skillswap = false;
	private List<Sprite> skillSprites = Arrays.asList(null, null, null, null);


	protected Map<BodyPart, SpriteRelation> commonSpriteSet = new HashMap<>();
	protected Map<BodyPart, SpriteRelation> sprintSpriteSet = new HashMap<>();

	protected List<AABB> commonHitboxes = new ArrayList<>();
	protected List<AABB> crouchingHitboxes = new ArrayList<>();
	protected List<AABB> slidingHitboxes = new ArrayList<>();

	private boolean checkPointReached = false;

	private boolean checkPointsEnabled = false;

	/**
	 * The Player class is the entity controlled by the user.
	 */
	public Player() {
		setCollisionFilter(e -> this.knockBackTimer < 0);
		turretTimer = 0;

		sprites.put(EntityState.STANDING, new Sprite(SpriteList.KNIGHT_STANDING));
		sprites.put(EntityState.JUMPING, new Sprite(SpriteList.KNIGHT_JUMPING));
		sprites.put(EntityState.MOVING, new Sprite(SpriteList.KNIGHT_WALKING));
		sprites.put(EntityState.CROUCHING, new Sprite(SpriteList.KNIGHT_CROUCH));

		this.enableManaBar();
		this.addMana(100);
		this.healing = 1;
		
		this.maxHealth = stats.getMaxHealth();
		this.currentHealth = maxHealth;

		for (int i = 0; i < 4; i++) {
			spellLoopIterations.add(i, new ArrayList<>());
			spellLoopTimings.add(i, new ArrayList<>());
			spellPhases.add(i, 0);
			usingSpells.add(false);
			currentSpellPhase.add(i, 0);
			currentSpellLoopTiming.add(i, 0);
			currentSpellLoopIteration.add(i, 0);
			cooldowns.add(i, 0d);
		}

		setSprite(sprites.get(EntityState.JUMPING));
		bounds = new AABB(posX, posY, BASE_WIDTH, BASE_HEIGHT); // Size is 1x2
		// for now

		// dud implementation of the obtaining weapons
		// Subject to change with inventory and weapon drop progression

		// adjust firing Rate - currently average of player stats and native gun
		this.moveSpeed = BASE_MOVE_SPEED;
		this.jumpSpeed = BASE_JUMP_SPEED;

		// Add this for standing, jumping and moving
		this.additionalSpritesCache.put(EntityState.STANDING, commonSpriteSet);
		this.additionalSpritesCache.put(EntityState.JUMPING, commonSpriteSet);
		this.additionalSpritesCache.put(EntityState.MOVING, commonSpriteSet);
		this.additionalSpritesCache.put(EntityState.SPRINTING, sprintSpriteSet);

		this.hitboxesCache.put(EntityState.STANDING, commonHitboxes);
		this.hitboxesCache.put(EntityState.JUMPING, commonHitboxes);
		this.hitboxesCache.put(EntityState.MOVING, commonHitboxes);
		this.hitboxesCache.put(EntityState.WALL_SLIDING, commonHitboxes);
		this.hitboxesCache.put(EntityState.DASHING, commonHitboxes);
		this.hitboxesCache.put(EntityState.AIR_DASHING, commonHitboxes);
		this.hitboxesCache.put(EntityState.SPRINTING, commonHitboxes);
		this.hitboxesCache.put(EntityState.DEAD, commonHitboxes);
		this.hitboxesCache.put(EntityState.KNOCK_BACK, commonHitboxes);

		this.hitboxesCache.put(EntityState.SLIDING, slidingHitboxes);

		this.hitboxesCache.put(EntityState.CROUCHING, crouchingHitboxes);
		this.hitboxesCache.put(EntityState.AIR_CROUCHING, crouchingHitboxes);

		setState(EntityState.JUMPING);

		this.strafeActive = true;

		this.firingRateTracker = stats.getFiringRate();
		this.bulletSpeed = 60;
		this.genericBulletDamage = stats.getDamage();

		System.out.println(World.getInstance().getSpawnX());
		System.out.println(World.getInstance().getSpawnY());
		this.setX(World.getInstance().getSpawnX());
		this.setY(World.getInstance().getSpawnY());
	}

	public void clear() {
		playerInput = new GameInput();
		stats = new PlayerStats();
		firstTick = true;
	}

	public void setHealing(int healing) {
		this.healing = healing;
	}

	/**
	 * Makes the Player unable to take damage
	 */
	public void setInvincible() {
		this.invincible = true;
	}

	/**
	 * Makes the Player susceptible to damage
	 */
	public void disableInvinciblity() {
		this.invincible = false;
	}

	/**
	 * Method to add current playerBuff's to player
	 */
	public void addPlayerBuff(PlayerBuff playerBuff) {
		buffList.add(playerBuff);
	}

	
	/**
	 * Getter method for obtaining the player's currently active weapons Used in
	 * tests
	 *
	 * @return arraylist of the player's currently active weapons
	 */
	public List<String> getActiveWeapons() {
		return activeWeapons;
	}


	/**
	 * Updates the players state through user button presses.
	 *
	 * @param ms millisecond tick the player attack is being handled on
	 */

	@Override
	protected void stateUpdate(long ms) {

		// Attacks
		boolean basicAttackPressed = InputManager.justReleased(GameAction.BASIC_ATTACK);
		boolean specialAttackPressed = InputManager.getActionState(GameAction.SPECIAL_ATTACK);
		playerAttack(ms, basicAttackPressed, specialAttackPressed);

		if (strafeActive) {
			if (InputManager.getDiffX(posX) > 0) {
				renderFacing = 1;
			} else {
				renderFacing = -1;
			}

			if (inputDir == 0) {
				facing = renderFacing;
			}
		}

		playerInput.updateGameInput();
		moveStateEntity(ms);
	}

	/**
	 * Applies passive healing effects
	 */
	private void updateHealing() {
		if (healTickCount == 50) {
			if (healingActivated) {
				addHealth(healing);
				healTickCount = 0;
			}
		} else {
			healTickCount++;
		}
	}

	/**
	 * Applies particle effects for dashing/air dashing
	 */
	private void dashParticleEffects() {
		if (currentState == EntityState.DASHING || currentState == EntityState.AIR_DASHING) {
			for (float y = getY(); y < getY() + getHeight(); y += 0.2) {
			}
		}
	}

	/**
	 * Code below for applying buffs to player based on ticks If a buff exist
	 * apply it once then countdown time remaining each tick when a buff is
	 * removevd remove the buff effect from the player
	 */
	private void updateBuffs(long ms) {
		if (!buffList.isEmpty()) {
			for (PlayerBuff buff : buffList) {
				if (!buff.getApplied()) {
					buff.setApplied();
					applyBuffEffect(buff);
				}
			}

			List<PlayerBuff> toRemove = new ArrayList<PlayerBuff>();
			for (PlayerBuff buff : buffList) {
				buff.countDown(ms);
				if (buff.getTime() < 10) {
					toRemove.add(buff);
				}
			}
			buffList.removeAll(toRemove);

			for (PlayerBuff buff : toRemove) {
				if (buff.getApplied()) {
					removeBuffEffect(buff);
				}
			}
		}

	}

	/**
	 * Tick handler for player
	 *
	 * @param ms millisecond tick the player attack is being handled on
	 */
	@Override
	protected void tick(long ms) {
		if (cooldown > 0) {
			cooldown--;
		}
		if (this.invincible) {
			setSprite(sprites.get(EntityState.INVINCIBLE));
		}
		addMana(1);
		stateUpdate(ms);
		// Skill Controlling
		updateHealing();
		if (stunned) {
			return;
		}
		// particleFX
		dashParticleEffects();

		this.experiencePoints = stats.getExperiencePoints();

		if (specialAttackFiring) {
			tickSpecialAttackFire(ms);
			return; // You can't do anything if you're firin' your lazor.
		}
		tickSpecialAttack(ms);
		updateTimers(ms);

		// This should be if'd
		tickDebug();

		updateBuffs(ms);
	}

	/***
	 * Method for applying buff to current player depending on the PlayerBuff
	 * class parsed into function
	 *
	 * @require newbuff.getStat() = String
	 */
	private void applyBuffEffect(PlayerBuff newBuff) {
		SoundCache.play("PowerUp");
		switch (newBuff.getStat()) {

			case "health":
		
				addHealth((int) newBuff.getModifier());
				break;
			case "mana":
				addMana((int) newBuff.getModifier());

				break;
			case "shield":
				setShielded(true);

				break;
			case "weapon":
				increaseBaseDamage(getBaseDamage());
				break;
			case "speed":

				moveSpeed *= newBuff.getModifier();
				if (moveSpeed > 30) {
					moveSpeed = 30;
				}
				break;
			default:
		}
	}

	/**
	 * Method for removing buff to current player depending on the PlayerBuff
	 * class parsed into function
	 *
	 * @require newbuff.getStat() = String
	 */
	private void removeBuffEffect(PlayerBuff newBuff) {
		if ("shield".equals(newBuff.getStat())) {
			boolean shieldBuffLeft = false;
			for (PlayerBuff buff : buffList) {
				if ("shield".equals(buff.getStat())) {
					shieldBuffLeft = true;
				}
			}
			if (!shieldBuffLeft) {
				setShielded(false);
			}
		} else if ("weapon".equals(newBuff.getStat())) {
			increaseBaseDamage(-getBaseDamage() / 2);
		} else if ("speed".equals(newBuff.getStat())) {
			moveSpeed /= newBuff.getModifier();
			if (moveSpeed < BASE_MOVE_SPEED) {
				moveSpeed = BASE_MOVE_SPEED;
			}
		}
	}
	
	/**
	 * Tick handler for special attack
	 *
	 * @param ms millisecond tick the player attack is being handled on
	 */
	private void tickSpecialAttack(long ms) {
		if (justPressed(GameAction.SPECIAL_ATTACK)) {
			specialAttackCharging = true;
		}
		if (InputManager.justReleased(GameAction.SPECIAL_ATTACK)) {
			specialAttackCharging = false;
			specialAttackChargeTimer = 0;
		}

		if (specialAttackCharging && InputManager.getActionState(GameAction.SPECIAL_ATTACK)) {
			specialAttackChargeTimer += ms;
			if (specialAttackChargeTimer > SPECIAL_ATTACK_THRESHOLD) {
				launchSpecialAttack();
			}
		}
	}

	/**
	 * Tick handler for Debug screen
	 */
	protected void tickDebug() {
		Debug debug = world.getDebug();
		if (debug != null) {
			String debugString = "FPS: " + World.getInstance().getFps() + "\n";

			debugString += "# of Entities: " + world.getAllEntities().size() + "\n";
			debugString += "# of Players: " + world.getPlayerEntities().size() + "\n";

			debugString += "# of Mobs: " + world.getNpcEntities().size() + "\n";
			debugString += "# of loaded Chunks: " + world.getTiles().getWidth() / Room.WIDTH + "\n";
			debugString += "HP: " + getCurrentHealth() + "\n";
			debugString += "XP: " + stats.getExperiencePoints() + "\n";
			debugString += "X: " + String.format("%.2f", posX) + ", Y: " + String.format("%.2f", posY) + "\n";
			debugString += "velX: " + String.format("%.2f", velX) + ", velY: " + String.format("%.2f", velY) + "\n";
			debugString += "Player State: " + currentState + "\n";

			debugString += "Move speed: " + moveSpeed + '\n';

			debugString += "Move speed: " + moveSpeed + "\n";

			debug.addToDebugString(debugString);
		}
	}

	/**
	 * Tick handler for special attack
	 */
	private void tickSpecialAttackFire(long ms) {
		specialAttackFireTimer += ms;

		if (specialAttackFireTimer > SPECIAL_ATTACK_FIRE_DURATION) {
			specialAttackFiring = false;
			specialAttackFireTimer = 0;
		}
	}
	
	/**
	 * Method to handle player's attack and to have a look at the attack type.
	 * <p>
	 * The type of player attack determined. When more types of attacks or
	 * different weapons is added to the game then this function could
	 * distinguish between the attacks. This method also decides if the attack
	 * was a critical hit or not.
	 *
	 * @param ms            millisecond tick the player attack is being handled on
	 * @param basicAttack   true if the basic attack is selected by the player
	 * @param specialAttack true if the special attack is selected by the player
	 */
	protected void playerAttack(long ms, boolean basicAttack, boolean specialAttack) {
		switch (currentState) {
			case DASHING:
			case AIR_DASHING:
			case STUNNED:
			case SLIDING:
			case CROUCHING:
			case AIR_CROUCHING:
			case KNOCK_BACK:
			case SPRINTING:
				return;
			default:
				break;
		}
		if (basicAttack) {
			if (cooldown > 0) {
				return;
			}
			System.out.println("Placed");
			Turret turret = new MachineGun();
			float turretPosX = (float) Math.floor(InputManager.getMouseTileX());
			float turretPosY = (float) Math.floor(InputManager.getMouseTileY());
			turret.setPosition(turretPosX, turretPosY);
			//turret.setPosition(this.getX(), this.getY());
			System.out.println("Me: " + this.getX() + "," + this.getY());
			System.out.println("It: " + turretPosX + ", " + turretPosY);
			World.getInstance().addEntity(turret);
			//cooldown = 20;
			//World.getInstance().getTiles().set( (int) InputManager.getMouseTileX(), (int) InputManager.getMouseTileY(), TileInfo.get(Tiles.BANK));
		} else {
			this.firingRateTracker -= ms;
		}
	}

	/**
	 * Launches player's special attack
	 */
	private void launchSpecialAttack() {
		specialAttackFiring = true;
		specialAttackCharging = false;
		specialAttackChargeTimer = 0;
	}

	/**
	 * Getter for the current fall rate modifier
	 *
	 * @return the current fall rate modifer
	 */
	public float getFallModifier() {
		return fallModifier;
	}

	/**
	 * Setter for the current fall rate modifier
	 *
	 * @param value the new fall rate modifier
	 */
	public void setFallModifier(float value) {
		fallModifier = value;
	}

	/**
	 * Wrapper for double jump conditions
	 * <p>
	 * Should reduce them stanks a lil
	 *
	 * @return true if the player is able to double jump, otherwise false
	 */
	protected boolean ableToDoubleJump() {
		return (doubleJumpAvailable /*&& getSkillUnlocked("Double jump")*/ || isUnderLiquid) && velY > 0 && ableToJump();
	}

	/**
	 * Transitions the player to a crouching state
	 */
	protected void transitionToCrouch() {
		if (changeBounds(CROUCH_WIDTH, CROUCH_HEIGHT)) {
			if (Math.abs(velX) > 0.2f) {
				velX *= 0.8f;
			} else {
				velX = 0;
			}
			velY = 0;
			strafeActive = false;
			setState(EntityState.CROUCHING);
		}
	}

	/**
	 * Transitions the player to an air-crouching state
	 */
	protected void transitionToAirCrouch() {
		if (changeBounds(1f, 1.4f)) {
			if (Math.abs(velX) > 0.2f) {
				velX *= 0.8f;
			} else {
				velX = 0;
			}
			strafeActive = false;
			setState(EntityState.AIR_CROUCHING);
		}
	}

	/**
	 * Transitions the player to an air dash in the specified directions
	 */
	protected void transitionToAirDash(boolean left, boolean right, boolean up, boolean down) {
		if (getCurrentMana() >= 50) {
			addMana(-50);
			enableGravity = false;
			strafeActive = false;

			setState(EntityState.AIR_DASHING);
			setVelocity(0, 0);
			if (left && right) {
				actionTimer = dashDuration;
				velX = facing * dashSpeed;
				airDashAvailable = false;
			} else if (left || right) {
				actionTimer = dashDuration;
				velX = playerInput.getInputDirection() * dashSpeed;
				airDashAvailable = false;
			}
			if (up) {
				actionTimer = dashDuration;
				velY = -dashSpeed;
				airDashAvailable = false;
			} else if (down) {
				actionTimer = dashDuration;
				velY = dashSpeed;
				airDashAvailable = false;
			}

			if (airDashAvailable) {
				actionTimer = dashDuration;
				velX = facing * dashSpeed;
				airDashAvailable = false;
			}
		}
	}

	/**
	 * Sets all modifiers to their basic value due to the player landing.
	 */
	protected void transitionOnLanding() {
		setFallModifier(1f);
		setTerminalVelModifier(1f);
		airDashAvailable = true;
		doubleJumpAvailable = true;
		strafeActive = true;
		velY = 0;
		transitionToOnGround();
	}

	/**
	 * Transitions from a non-standing or moving state to standing or moving.
	 */
	protected void transitionToOnGround() {
		EntityState prevState = currentState;
		strafeActive = true;
		if (playerInput.getInputDirection() == 0) {
			setState(EntityState.STANDING);
		} else {
			setState(EntityState.MOVING);
		}
		if (((getWidth() - BASE_WIDTH) > EPSILON) || ((getHeight() - BASE_HEIGHT) > EPSILON)) {
			if (!changeBounds(BASE_WIDTH, BASE_HEIGHT)) {
				currentState = prevState;
			}
		}
	}

	/**
	 * Transitions the player to a knock-back state
	 * <p>
	 */
	protected void transitionToKnockBack(int knockBackDir) {
		if (currentState == EntityState.KNOCK_BACK) {
			return;
		} else if (knockBackTimer > 0 && knockBackTimer < knockBackEndDuration) {
			knockBackTimer = knockBackEndDuration - 100;
			return;
		}
		setState(EntityState.KNOCK_BACK);
		setBlocksOtherEntities(false);
		knockBackTimer = knockBackDuration;
		knockBackRenderTimer = 0L;
		knockBackLastRenderTime = 0L;
		velX = knockBackDir * knockBackSpeedX;
		velY = knockBackSpeedY;
	}

	/**
	 * The ex-hueg state machine that governs how the player moves
	 *
	 * @param ms time since the last tick in ms
	 */
	protected void moveStateEntity(long ms) {
		if (currentState == EntityState.DEAD) {
			return;
		}
		inputDir = playerInput.getInputDirection();

		boolean left = playerInput.getLeftPressed();
		boolean right = playerInput.getRightPressed();
		boolean jump = playerInput.getJumpPressed();
		boolean up = playerInput.getUpPressed();
		boolean down = playerInput.getDownPressed();
		boolean dash = false;//playerInput.getDashPressed();
		boolean slide = false;//playerInput.getSlidePressed();

		jumpAvailable = ableToJump();

		switch (currentState) {
			case DEAD:
				return;
			case KNOCK_BACK:
				entityKnockBack();
				break;
			case STANDING:
				entityStand(left, right, down, jump, dash, slide);
				break;
			case SPRINTING: // intentional fall down
			case MOVING:
				entityMove(left, right, down, jump, dash, slide);
				break;
			case JUMPING:
				entityJump(left, right, up, down, jump, dash);
				break;
			case WALL_SLIDING:
				entityWallSlide(left, right, jump);
				break;
			case DASHING:
				entityDash(jump);
				break;
			case SLIDING:
				entitySlide(down);
				break;
			case AIR_DASHING:
				entityAirDash();
				break;
			case CROUCHING:
				entityCrouch(down, jump, dash, slide);
				break;
			case AIR_CROUCHING:
				entityAirCrouch(left, right, up, down, dash);
				break;
			default:
				break;
		}
	}

	/**
	 * Handles state transitions while crouching in the air
	 * <p>
	 * -> standing/moving: landing without holding down<br>
	 * -> crouching: landing while holding down <br>
	 * -> jumping: down is released while mid air <br>
	 * -> air dash: dash is pressed <br>
	 *
	 * @param left
	 * @param right
	 * @param up
	 * @param down
	 * @param dash
	 */
	protected void entityAirCrouch(boolean left, boolean right, boolean up, boolean down, boolean dash) {
		applyJumpingPhysics();

		if (onGround) {
			if (!down) {
				transitionOnLanding();
				return;
			} else {
				setFallModifier(1f);
				setTerminalVelModifier(1f);
				airDashAvailable = true;
				doubleJumpAvailable = true;
				transitionToCrouch();
				return;
			}
		}
		if (!down && changeBounds(1f, 2f)) {
			setState(EntityState.JUMPING);
			strafeActive = true;
		}
		if (dash /*&& getSkillUnlocked("Dash")*/ && changeBounds(1f, 2f)) {
			transitionToAirDash(left, right, up, down);
		}
	}

	/**
	 * Handles state transitions in knock back
	 * <p>
	 * -> standing/moving: knockback timer expired
	 */
	protected void entityKnockBack() {
		if (knockBackTimer < knockBackEndDuration) {
			transitionOnLanding();
			setBlocksOtherEntities(true);
			return;
		} else if (!onGround) {
			velX *= 0.95f;
		} else {
			setVelocity(0, 0);
		}
	}

	/**
	 * Handles state transitions when crouching
	 * <p>
	 * -> standing: down is released <br>
	 * -> jumping: jump is pressed and its possible to jump, or the player falls
	 * off the ground <br>
	 * -> sliding: dash or slide are pressed
	 *
	 * @param down  is the down button pressed
	 * @param jump  is the jump button pressed
	 * @param dash  is the dash button pressed
	 * @param slide is the slide button pressed
	 */
	protected void entityCrouch(boolean down, boolean jump, boolean dash, boolean slide) {
		if (Math.abs(velX) > 0.2f) {
			velX *= 0.8f;
		} else {
			velX = 0;
		}
		velY = 0;
		if (!down && changeBounds(BASE_WIDTH, BASE_HEIGHT) && onGround) {
			setState(EntityState.STANDING);
			strafeActive = true;
		}
		if (!onGround && changeBounds(BASE_WIDTH, BASE_HEIGHT)) {
			setState(EntityState.JUMPING);
			strafeActive = true;
		}

		if (jump && jumpAvailable) {
			SoundCache.play("jump");
			setState(EntityState.AIR_CROUCHING);
			velY = jumpSpeed;
			return;
		}
	}

	/**
	 * Handles transitions from the standing state
	 * <p>
	 * -> moving: user input to either side <br>
	 * -> jumping: either user inputs jump, or the player walks off a ledge <br>
	 * -> dashing: user inputs dash (and there is enough mana and the skill is
	 * unlocked) <br>
	 * -> sliding: user inputs dash (and there is enough mana and this doesn't
	 * cause collision problems)
	 */
	protected void entityStand(boolean left, boolean right, boolean down, boolean jump, boolean dash, boolean slide) {
		if (((getWidth() - BASE_WIDTH) > EPSILON) || ((getHeight() - BASE_HEIGHT) > EPSILON)) {
			changeBounds(BASE_WIDTH, BASE_HEIGHT);
		}
		setVelocity(0, 0);
		if (!onGround) {
			setState(EntityState.JUMPING);
			return;
		}
		if (left != right) {
			setState(EntityState.MOVING);
		} else if (jump && jumpAvailable) {
			SoundCache.play("jump");
			setState(EntityState.JUMPING);
			velY = jumpSpeed;
			return;
		}
		if (down) {
			transitionToCrouch();
			return;
		}
	}

	/**
	 * Handles transitions from the moving state
	 * <p>
	 * -> standing: no user input to either side <br>
	 * -> sprinting (sub-state of moving): user double taps the left/right
	 * button <br>
	 * -> jumping: either user inputs jump, or the player walks off a ledge <br>
	 * -> dashing: user inputs dash (and there is enough mana and the skill is
	 * unlocked) <br>
	 * -> sliding: user inputs dash (and there is enough mana and this doesn't
	 * cause collision problems)
	 */
	protected void entityMove(boolean left, boolean right, boolean down, boolean jump, boolean dash, boolean slide) {
		if (left == right) {
			setState(EntityState.STANDING);
			return;
		} else {
			velX = inputDir * moveSpeed;
		}
		
		if (jump && jumpAvailable) {
			SoundCache.play("jump");
			setState(EntityState.JUMPING);
			velY = jumpSpeed;
			return;
		} else if (!onGround) {
			setState(EntityState.JUMPING);
			return;
		}
		if (down) {
			transitionToCrouch();
			return;
		}
	}

	/**
	 * Handles transitions from the jumping state
	 * <p>
	 * -> standing: user lands with no horizontal input -> moving: user lands
	 * with horizontal input -> double jumping (sub-state of jumping): user
	 * inputs jump (and their velocity is downwards and the skill is unlocked)
	 * -> wall sliding: player is against a wall and holding the input of that
	 * direction -> air dashing: user inputs dash (and there is enough mana and
	 * the skill is unlocked)
	 */
	protected void entityJump(boolean left, boolean right, boolean up, boolean down, boolean jump, boolean dash) {
		applyJumpingPhysics();
		if (jump && ableToDoubleJump() && getCurrentMana() >= 20) {
			addMana(-20);
			SoundCache.play("jump");
			setState(EntityState.JUMPING);
			// particleFX
			for (float x = getX(); x < getX() + getWidth(); x += 0.2) {
			}
			velY = jumpSpeed;
			doubleJumpAvailable = false;
		}
		
		if (onGround) { // We hit the ground
			transitionOnLanding();
			return;
		}
		if (onCeiling) {
			velY = 0f;
		}
	}

	/**
	 * Handles transitions from the wall sliding state
	 * <p>
	 * -> standing: user lands with no horizontal input -> moving: user lands
	 * with horizontal input -> jumping: player is no longer against a wall and
	 * holding the input of that direction -> wall jumping (sub-state of
	 * jumping): player inputs jump
	 */
	protected void entityWallSlide(boolean left, boolean right, boolean jump) {
		if ((inputDir != onWall) || (velY < 0)) {
			setState(EntityState.JUMPING);
			setFallModifier(1f);
			setTerminalVelModifier(1f);
			strafeActive = true;
		}
		if (jump && jumpAvailable) {
			setFallModifier(1f);
			setTerminalVelModifier(1f);
			setState(EntityState.JUMPING);
			velY = jumpSpeed * fallModifier;
			velX = -wallJumpSpeedModifier * facing * moveSpeed;
			wallJumpTimer = wallJumpDuration;
			strafeActive = true;
		}
		if (onGround) { // We hit the ground
			transitionOnLanding();
		}
	}

	/**
	 * Handles transitions from the dashing state
	 * <p>
	 * -> standing: user runs into a wall, or the timer runs out with no
	 * horizontal input <br>
	 * -> moving: the timer runs out with horizontal input <br>
	 * -> jumping: user jumps or falls off a ledge <br>
	 * -> wall jumping (sub-state of jumping): player inputs jump
	 */
	protected void entityDash(boolean jump) {
		if (!onGround) {
			strafeActive = true;
			setState(EntityState.JUMPING);
		}
		if (onWall != 0) {
			strafeActive = true;
			setState(EntityState.STANDING);
		}
		if (jump && jumpAvailable) {
			strafeActive = true;
			setState(EntityState.JUMPING);
			velY = jumpSpeed;
		}
		if (actionTimer < 0) {
			transitionToOnGround();
		}
	}

	/**
	 * Handles transitions from the sliding state
	 * <p>
	 * -> standing: user runs into a wall, or the timer runs out with no
	 * horizontal input <br>
	 * -> moving: the timer runs out with horizontal input <br>
	 * -> jumping: user jumps or falls off a ledge <br>
	 * -> wall jumping (sub-state of jumping): player inputs jump
	 */
	protected void entitySlide(boolean down) {
		if (!onGround && jumpAvailable) {
			strafeActive = true;
			setState(EntityState.JUMPING);
			changeBounds(BASE_WIDTH, BASE_HEIGHT);
			// No need to check if transition is possible because of
			// jumpAvailable (in theory)
		}
		if (onWall != 0 && jumpAvailable) {
			strafeActive = true;
			setState(EntityState.STANDING);
			changeBounds(BASE_WIDTH, BASE_HEIGHT);
		} else if (onWall != 0) {
			velX *= -1;
			actionTimer = -1L;
		}
		if (actionTimer < 0 && jumpAvailable) {
			if (down) {
				transitionToCrouch();
			} else {
				changeBounds(BASE_WIDTH, BASE_HEIGHT);
				transitionToOnGround();
			}
		}
	}

	/**
	 * Handles transitions from the air dashing state
	 * <p>
	 * -> standing: user lands with no horizontal input <br>
	 * -> moving: the user lands with horizontal input <br>
	 * -> jumping: the timer runs out
	 */
	protected void entityAirDash() {
		if (onGround) { // We hit the ground
			enableGravity = true;
			strafeActive = true;
			transitionOnLanding();
		}

		if (actionTimer < 0) {
			enableGravity = true;
			strafeActive = true;
			setState(EntityState.JUMPING);
			velY = 0;
			if (inputDir == 0) {
				velX = 0;
			} else {
				velX = facing * moveSpeed;
			}
		}
	}

	/**
	 * Handles horizontal speed while jumping:
	 * <p>
	 * If the player has just wall jumped, they have no control. <br>
	 * If the player releases left/right then drag applies to slow them down
	 * <br>
	 * Otherwise its as you'd expect with left/right controlling velX <br>
	 */

	private void applyJumpingPhysics() {
		if (wallJumpTimer < 0) {
			if (velX <= moveSpeed && velX >= -moveSpeed) {
				velX = inputDir * moveSpeed;
			} else if (facing != inputDir && inputDir != 0) {
				velX = -velX;
			} else if (inputDir == 0) {
				velX *= 0.9f;
			}
		}
	}

	/**
	 * Updates timers that track player states
	 * <p>
	 * action timer: handles dash/slide <br>
	 * wall jump timer: handles wall jumps <br>
	 * knock back timer: handles knock back and associated invulnerability, also
	 * handles toggling the render flag
	 *
	 * @param ms
	 */
	protected void updateTimers(float ms) {
		if (actionTimer >= 0) {
			actionTimer -= ms;
		}

		if (wallJumpTimer >= 0) {
			wallJumpTimer -= ms;
		}

		if (knockBackTimer >= 0) {
			knockBackTimer -= ms;
			knockBackRenderTimer += ms;
			if (knockBackRenderTimer > knockBackLastRenderTime + knockBackRenderGap) {
				renderFlag = !renderFlag;
				knockBackLastRenderTime = knockBackRenderTimer;
			}
		} else {
			renderFlag = true;
		}
	}

	@Override
	public void render(GraphicsContext gc, Viewport viewport, long ms) {
		super.render(gc, viewport, ms);
		float tileSize = viewport.getTileSideLength();

		int leftBorder = viewport.getLeftBorder();
		int topBorder = viewport.getTopBorder();

		int left = (int) Math.floor(viewport.getLeft());
		int top = (int) Math.floor(viewport.getTop());

		float subTileShiftX = (viewport.getLeft() - left) * tileSize;
		float subTileShiftY = (viewport.getTop() - top) * tileSize;

		float x = (posX - left) * tileSize + leftBorder - subTileShiftX;
		float y = (posY - top) * tileSize + topBorder - subTileShiftY;

		float playerLabelX = x - 20;
		float playerLabelY = (y + bounds.getHeight() * tileSize) + 5;
		/*
			gc.setFill(new Color(0, 0, 0, 0.5));
			gc.fillRect(playerLabelX, playerLabelY, bounds.getWidth() * tileSize, 20);
			*/

		if (name != null && name.length() > 0) {
			gc.setFill(new Color(0, 0, 0, 0.5));
			gc.fillRect(playerLabelX, playerLabelY, 60, 20);
			gc.setFill(new Color(1, 1, 1, 0.75));
			gc.fillText(name, playerLabelX, playerLabelY + 15, 60);
		}

		//TODO Fill box with playerName, make width suit playerName length

	}
	
	/**
	 * @param value
	 * @param cooldownIndex
	 */
	public void setCooldown(double value, int cooldownIndex) {
		cooldowns.set(cooldownIndex, value);
	}


	/**
	 * @param cooldownIndex
	 * @return
	 */
	public double getCooldown(int cooldownIndex) {
		return cooldowns.get(cooldownIndex);
	}

	/**
	 * Get the base damage of the player.
	 *
	 * @return the players base damage.
	 */

	public int getBaseDamage() {
		return stats.getDamage();
	}

	
	/**
	 * This method adds player's experience points to the player's stats
	 *
	 * @param points experience points gained by the player
	 */
	public void addExperiencePoint(int points) {
		stats.addExperiencePoints(points);
		if (stats.getExperiencePoints() == 20 * stats.getPlayerLevel()) {
			stats.levelUp();
			this.genericBulletDamage = stats.getDamage();
		} else if (stats.getExperiencePoints() > 20 * stats.getPlayerLevel()) {
			stats.addExperiencePoints(-20 * stats.getPlayerLevel());
			stats.levelUp();
			this.genericBulletDamage = stats.getDamage();
		}
	}

	/**
	 * Increase Accuracy in the player stats by given int
	 *
	 * @param increase Degrees in which to improve accuracy int increase < 15
	 */
	public void increaseAccuracy(int increase) {
		stats.addFiringAccuracy(increase);
	}

	public void increaseBaseDamage(int increase) {
		stats.setBaseDamage(getBaseDamage() + increase);
	}

	public void setSpellPhases(int index, int numPhases) {
		spellPhases.set(index, numPhases);
	}

	public void setSpellLoopIterations(int index, int phaseIndex, int numLoops) {
		spellLoopIterations.get(index).add(phaseIndex, numLoops);
	}

	public void setSpellLoopTimings(int index, int phaseNum, int duration) {
		spellLoopTimings.get(index).add(phaseNum, duration);
	}

	public void setUsingSpell(int index, boolean status) {
		usingSpells.set(index, status);
	}

	/**
	 * A method to get the player's current experience points
	 *
	 * @return player's experience points
	 */
	public long getExperiencePoints() {
		return this.experiencePoints;
	}

	/**
	 * This method adds player's kill count to the player's stats
	 *
	 * @param count kill count gained by the player
	 */
	public void addKillCount(int count) {
		stats.addKillCount(count);
	}

	/**
	 * This method adds player's BossNPC kill count to the player's stats
	 *
	 * @param bossID the ID associated with the boss
	 */
	public void addBossKill(String bossID) {
		stats.addBossKill(bossID);
	}

	/**
	 * Wrapper method for the PlayerStats.addPickUpCount()
	 */
	public void addPickUpCount() {
		stats.addPickUpCount();
	}

	/**
	 * Wrapper method for PlayerStats.addCountCount()
	 */
	public void addCoinCount(int coinValue) {
		stats.addCoinCount(coinValue);
	}

	/**
	 * A method to get the player's current level
	 *
	 * @return player's level
	 */
	public int getPlayerLevel() {
		return stats.getPlayerLevel();
	}

	/**
	 * Entity collision event handler
	 * <p>
	 * We deal knockback only with the first entity for convenience
	 */
	@Override
	protected void onEntityCollide(List<Entity> entities, List<BodyPart> hitLocations) {
		if (entities.isEmpty()) {
			return;
		}
		for (Entity entity : entities) {
			if (false) { //TODO: PLayer knockback
				if (!this.invincible) {
					if (getCurrentState() != EntityState.DEAD) {
						int knockBackDir = (int) Math.signum(entity.getVelX());
						transitionToKnockBack(knockBackDir);
					}
					return;
				} else {
					entity.onDeath(this);
				}
			}
		}
	}

	/**
	 * Terrain collision event handler;
	 */
	@Override
	protected void onTerrainCollide(int tileX, int tileY, Side side) {
	}

	/**
	 * On death event handler;
	 *
	 * @param cause entity that caused the death
	 */
	@Override
	protected void onDeath(Entity cause) {
		velX = 0;

		if (isCheckPointReached() && checkPointsEnabled) {
			return;
		}
		setState(EntityState.DEAD);
	}

	/**
	 * Adjusts the BME's health by the input quantity, up to the maximum value.
	 *
	 * @param health the amount of health to add.
	 */
	@Override
	public void addHealth(int health) {
		if (health < 0 && !invincible) {
			SoundCache.play("damaged");
		}
		if (currentState == EntityState.KNOCK_BACK || knockBackTimer > 0) {
			return;
		}
		if (health >= 0 || !shielded) {
			currentHealth += health;
		}

		if (currentHealth > maxHealth) {
			currentHealth = maxHealth;
		}
	}

	/**
	 * returns the player's health
	 *
	 * @return current health
	 */

	public int getHealth() {
		return currentHealth;
	}

	/**
	 * Update skills placeholder
	 */
	protected boolean updateSkills() {
		return this.experiencePoints != 0 && this.experiencePoints % 100 == 0;
	}

	/**
	 * Getter method used in tests
	 *
	 * @return player's dash speed
	 */
	public float getDashSpeed() {
		return dashSpeed;
	}

	/**
	 * Getter method used in tests
	 *
	 * @return player's dash duration
	 */
	public float getDashDuration() {
		return dashDuration;
	}

	/**
	 * Getter method used in tests
	 *
	 * @return player's slide speed
	 */
	public float getSlideSpeed() {
		return slideSpeed;
	}

	/**
	 * Getter method used in tests
	 *
	 * @return player's slide duration
	 */
	public float getSlideDuration() {
		return slideDuration;
	}

	/**
	 * Getter method used in tests
	 *
	 * @return player's wall sliding fall modifier
	 */
	public float getWallSlidingFallModifier() {
		return wallSlidingFallModifier;
	}

	/**
	 * Getter method used in tests
	 *
	 * @return player's wall jump duration
	 */
	public float getWallJumpDuration() {
		return wallJumpDuration;
	}

	/**
	 * Returns the players PlayerStats class.
	 *
	 * @return the players stats.
	 */
	public PlayerStats getPlayerStatsClass() {
		return stats;
	}

	public void setUpdateHud(boolean bool) {
		updateHud = bool;
	}

	public boolean getUpdateHud() {
		return updateHud;
	}

	public void setDrawSKill(Sprite skill) {
		drawSkill = skill;
	}

	/**
	 * @return the DrawSkill sprite.
	 */
	public Sprite getDrawSkill() {
		return drawSkill;
	}


	/**
	 * Set the players spellkey.
	 *
	 * @param key the spellkey to set.
	 */
	public void setSpellKey(String key) {
		spellKey = key;
	}

	/**
	 * Returns the spellkey.
	 *
	 * @return the players spellkey.
	 */
	public String getSpellKey() {
		return spellKey;
	}


	public void setDrawSKill2(Sprite skill) {
		drawSkill2 = skill;
	}

	public Sprite getDrawSkill2() {
		return drawSkill2;
	}

	public String getSpellKey2() {
		return spellKey2;
	}

	public void setSpellKey2(String s) {
		spellKey2 = s;

	}

	public void setSkillSwap(boolean bool) {
		skillswap = bool;
	}

	public boolean getSkillSwap() {
		return skillswap;
	}

	public List<Sprite> getSkillSprites() {
		return skillSprites;
	}

	public void setSkillSprites(Sprite sprite, int index) {
		skillSprites.set(index, sprite);
	}


	/**
	 * Returns the string form of the player This is the sprite, bounds, posX,
	 * posY, experience points and bullet damage, in a line-separated format.
	 *
	 * @return string representation of the player
	 */
	public String toString() {
		String total = "";
		total += "sprite" + this.sprite.toString();
		total += "\nbounds" + this.bounds.toString();
		total += "\nX" + this.posX;
		total += "\nY" + this.posY;
		total += "\nXP: " + this.experiencePoints;
		total += "\nDamage: " + this.genericBulletDamage;
		return total;
	}

	/**
	 * This method determines whether the player has requested to switch the
	 * currently selected weapon and will change the currently equipped weapon.
	 * <p>
	 * The numbers 1 - 5 represent the first 5 weapons in the activeWeapons
	 * array. It is expected that the owned weapon array with be converted to
	 * activeWeapons after addition of inventory.
	 *
	 * @param weaponChange indicates which weapon change has been pressed
	 */
	protected void weaponChange(List<Boolean> weaponChange) {
		for (int i = 0; i < weaponChange.size(); i++) {
			if (weaponChange.get(i) && activeWeapons.size() > i - 1) {
			}
		}
	}

	public void setViewDistance(int viewDistance) {
		this.viewDistance = viewDistance;
	}

	public int getViewDistance() {
		return viewDistance;
	}

	/**
	 * Sets the players mode when talking
	 */
	public void setTalking(boolean input) {
		this.talking = input;
	}

	/**
	 * Sets the players mode when talking
	 *
	 * @return whether or not the player is talking
	 */
	public boolean getTalking() {
		return talking;
	}

	/**
	 * @return the checkPointReached
	 */
	public boolean isCheckPointReached() {
		return checkPointReached;
	}

	/**
	 * @param checkPointReached the checkPointReached to set
	 */
	public void setCheckPointReached(boolean checkPointReached) {
		this.checkPointReached = checkPointReached;
	}

	/**
	 * @return the checkPointsEnabled
	 */
	public boolean isCheckPointsEnabled() {
		return checkPointsEnabled;
	}

	/**
	 * @param checkPointsEnabled the checkPointsEnabled to set
	 */
	public void setCheckPointsEnabled(boolean checkPointsEnabled) {
		this.checkPointsEnabled = checkPointsEnabled;
	}

	/**
	 * @param set the value to set variable to
	 */
	public void setJumpAvailable(boolean set) {
		this.jumpAvailable = set ? true : false;
	}

	/**
	 * @param speed the speed value to give the player
	 */
	public void setJumpSpeed(float speed) {
		this.jumpSpeed = speed;
	}
}
