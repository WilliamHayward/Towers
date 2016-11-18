package turrets.game.entities.players;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import turrets.core.input.GameAction;
import turrets.core.input.GameInput;
import turrets.core.input.InputManager;
import turrets.core.sound.SoundCache;
import turrets.game.debug.Debug;
import turrets.game.entities.AABB;
import turrets.game.entities.BasicMovingEntity;
import turrets.game.entities.Entity;
import turrets.game.entities.EntityState;
import turrets.game.mechanics.BodyPart;
import turrets.game.mechanics.Side;
import turrets.game.modes.BuildMode;
import turrets.game.modes.BuildingList;
import turrets.game.world.Coordinate;
import turrets.game.world.World;
import turrets.graphics.LayerList;
import turrets.graphics.Viewport;
import turrets.graphics.sprites.SpriteRelation;

import java.util.*;

public abstract class Player extends BasicMovingEntity {

	protected static final float EPSILON = 0.00001f;

	protected Map<GameAction, BuildingList> availableBuildings = new HashMap<>();
	protected BuildingList activeBuilding = BuildingList.MACHINE_GUN;
	
	protected long knockBackDuration = 1000L;
	protected long knockBackEndDuration = 750L;
	protected long knockBackRenderTimer = 0L; // used to flip renderFlag
	protected long knockBackLastRenderTime = 0L;
	protected int knockBackRenderGap = 75;

	protected float knockBackSpeedX = 15f;
	protected float knockBackSpeedY = -10f;

	GameInput playerInput = new GameInput();
	
	protected long knockBackTimer = -1L;
	protected boolean doubleJumpAvailable = true;
	protected boolean jumpAvailable;
	protected boolean invincible = false;
	protected int inputDir;

	protected static final float BASE_JUMP_SPEED = -20f;
	protected static final float BASE_MOVE_SPEED = 10f;
	
	static float scale = 1f;

	protected static final float BASE_WIDTH = 1f * scale;
	protected static final float BASE_HEIGHT = 1.41f * scale;
	protected static final float CROUCH_WIDTH = 0.96f * scale;
	protected static final float CROUCH_HEIGHT = 1f * scale;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BuildingList getActiveBuilding() {
		return activeBuilding;
	}

	public void setActiveBuilding(BuildingList activeBuilding) {
		this.activeBuilding = activeBuilding;
	}

	protected String name;

	protected Map<BodyPart, SpriteRelation> commonSpriteSet = new HashMap<>();
	protected Map<BodyPart, SpriteRelation> sprintSpriteSet = new HashMap<>();

	protected List<AABB> commonHitboxes = new ArrayList<>();
	protected List<AABB> crouchingHitboxes = new ArrayList<>();
	protected List<AABB> slidingHitboxes = new ArrayList<>();

	/**
	 * The Player class is the entity controlled by the user.
	 */
	public Player() {
		layer = LayerList.PLAYERS;
		
		setSprite(sprites.get(EntityState.JUMPING));
		bounds = new AABB(posX, posY, BASE_WIDTH, BASE_HEIGHT); // Size is 1x2
		// for now

		// dud implementation of the obtaining weapons
		// Subject to change with inventory and weapon drop progression

		// adjust firing Rate - currently average of player stats and native gun
		this.moveSpeed = BASE_MOVE_SPEED;
		this.jumpSpeed = BASE_JUMP_SPEED;
		
		// Pair buildings to keys
		availableBuildings.put(GameAction.SLOT_ONE, BuildingList.MACHINE_GUN);
		availableBuildings.put(GameAction.SLOT_TWO, BuildingList.CANNON);
		availableBuildings.put(GameAction.SLOT_THREE, BuildingList.ACID_TRAP);

		// Add this for standing, jumping and moving
		this.additionalSpritesCache.put(EntityState.STANDING, commonSpriteSet);
		this.additionalSpritesCache.put(EntityState.JUMPING, commonSpriteSet);
		this.additionalSpritesCache.put(EntityState.MOVING, commonSpriteSet);
		this.additionalSpritesCache.put(EntityState.SPRINTING, sprintSpriteSet);

		this.hitboxesCache.put(EntityState.STANDING, commonHitboxes);
		this.hitboxesCache.put(EntityState.JUMPING, commonHitboxes);
		this.hitboxesCache.put(EntityState.MOVING, commonHitboxes);
		this.hitboxesCache.put(EntityState.DEAD, commonHitboxes);
		this.hitboxesCache.put(EntityState.KNOCK_BACK, commonHitboxes);
		
		this.hitboxesCache.put(EntityState.CROUCHING, crouchingHitboxes);

		setState(EntityState.JUMPING);

		this.strafeActive = true;
		spawn();
	}
	
	public void spawn() {
		Coordinate spawn = World.getInstance().getRandomSpawn();
		this.setX(spawn.getX());
		this.setY(spawn.getY());
	}

	public void clear() {
		playerInput = new GameInput();
		firstTick = true;
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
		playerAttack(ms, basicAttackPressed);

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
	 * Tick handler for player
	 *
	 * @param ms millisecond tick the player attack is being handled on
	 */
	@Override
	protected void tick(long ms) {
		if (this.invincible) {
			setSprite(sprites.get(EntityState.INVINCIBLE));
		}
		stateUpdate(ms);
		if (stunned) {
			return;
		}
		
		for (GameAction action: availableBuildings.keySet()) {
			if (InputManager.justPressed(action)) {
				activeBuilding = availableBuildings.get(action);
			}
		}
		updateTimers(ms);

		// This should be if'd
		tickDebug();
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

			debugString += "# of Enemies: " + world.getEnemyEntities().size() + "\n";
			debugString += "Current Building: " + BuildMode.getInstance().getBuildingName(activeBuilding) + "\n";
			debugString += "HP: " + getCurrentHealth() + "\n";
			debugString += "X: " + String.format("%.2f", posX) + ", Y: " + String.format("%.2f", posY) + "\n";
			debugString += "velX: " + String.format("%.2f", velX) + ", velY: " + String.format("%.2f", velY) + "\n";
			debugString += "Player State: " + currentState + "\n";

			debugString += "Move speed: " + moveSpeed + '\n';

			debugString += "Move speed: " + moveSpeed + "\n";

			debug.addToDebugString(debugString);
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
	 */
	protected void playerAttack(long ms, boolean basicAttack) {
		switch (currentState) {
			case DASHING:
			case AIR_DASHING:
			case STUNNED:
			case CROUCHING:
			case KNOCK_BACK:
				return;
			default:
				break;
		}
		
		if (basicAttack) {
			float x = (float) Math.floor(InputManager.getMouseTileX());
			float y = (float) Math.floor(InputManager.getMouseTileY());
			BuildMode.getInstance().build(activeBuilding, x, y);
		}
	}

	/**
	 * Wrapper for double jump conditions
	 * <p>
	 * Should reduce them stanks a lil
	 *
	 * @return true if the player is able to double jump, otherwise false
	 */
	protected boolean ableToDoubleJump() {
		return doubleJumpAvailable && velY > 0;
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
	 * Sets all modifiers to their basic value due to the player landing.
	 */
	protected void transitionOnLanding() {
		setFallModifier(1f);
		setTerminalVelModifier(1f);
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
			case CROUCHING:
				entityCrouch(down, jump, dash, slide);
				break;
			default:
				break;
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
		if (jump && ableToDoubleJump()) {
			SoundCache.play("jump");
			setState(EntityState.JUMPING);
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
	 * Handles horizontal speed while jumping:
	 * <p>
	 * If the player has just wall jumped, they have no control. <br>
	 * If the player releases left/right then drag applies to slow them down
	 * <br>
	 * Otherwise its as you'd expect with left/right controlling velX <br>
	 */

	private void applyJumpingPhysics() {
		if (velX <= moveSpeed && velX >= -moveSpeed) {
			velX = inputDir * moveSpeed;
		} else if (facing != inputDir && inputDir != 0) {
			velX = -velX;
		} else if (inputDir == 0) {
			velX *= 0.9f;
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
					entity.kill(this);
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

		setState(EntityState.DEAD);
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
		return total;
	}
}
