package uq.deco2800.coaster.core;

import javafx.animation.AnimationTimer;
import uq.deco2800.coaster.core.input.GameAction;
import uq.deco2800.coaster.core.input.InputManager;
import uq.deco2800.coaster.core.sound.SoundCache;
import uq.deco2800.coaster.game.entities.Player;
import uq.deco2800.coaster.game.entities.buildings.turrets.MachineGun;
import uq.deco2800.coaster.game.tiles.TileInfo;
import uq.deco2800.coaster.game.tiles.Tiles;
import uq.deco2800.coaster.game.world.Room;
import uq.deco2800.coaster.game.world.World;
import uq.deco2800.coaster.graphics.Renderer;
import java.io.File;


//The main game loop class. The function handle(long now) is called every tick.
public class Engine extends AnimationTimer {
	private Renderer renderer;
	private long lastTime;

	private boolean skillTreeOn = false;
	private boolean isPaused = false;
	private boolean inMenu = false;
	private boolean passiveInfo = false;
	private boolean isGameOver;

	public void setGraphicsOutput(Renderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * @return Engine Renderer
	 */
	public Renderer getRenderer() {
		return renderer;
	}

	public boolean getPassiveInfo() {
		return this.passiveInfo;
	}

	/**
	 * Toggles the status of passiveInfoPanel. If it is active skillTree cannot
	 * be toggled with T.
	 */
	public void togglePassiveStatus() {
		passiveInfo = !passiveInfo;
	}

	public void toggleInMenu() {
		inMenu = !inMenu;
	}

	public boolean getInMenu() {
		return inMenu;
	}

	public boolean isPaused() {
		return isPaused;
	}


	/**
	 * Respond to key presses, check game over, and render
	 */
	public void handle(long now) {
		// Don't do anything if the game is currently over, all the user can do
		// is press the button.
		{
			if (isGameOver) {
				return;
			}
		}
		World world = World.getInstance();
		// TODO cleanup methods
		long thisTime = System.currentTimeMillis();
		long ms = thisTime - lastTime;

		if (InputManager.justPressed(GameAction.MUTE)) {
			if (SoundCache.getMute()) {
				SoundCache.unmute();
			} else {
				SoundCache.mute();
			}
		}
		if (InputManager.justPressed(GameAction.VOLUME_UP)) {
			SoundCache.setVolume(SoundCache.getVolume() + SoundCache.VOLUME_STEP);
		}

		if (InputManager.justPressed(GameAction.VOLUME_DOWN)) {
			SoundCache.setVolume(SoundCache.getVolume() - SoundCache.VOLUME_STEP);
		}


		if (InputManager.justPressed(GameAction.DEBUG_CONSOLE)) {
			//renderer.toggleScreen("Debug Console");
			MachineGun gun = new MachineGun();
			
			gun.setPosition(0, 0);
			world.addEntity(gun);
		}

		if (world.isGameOver() && !isGameOver) {
			// Show Game Over Screen and button
			inMenu = true;
			isGameOver = true;
			renderer.toggleScreen("Game Over");
		}
		if (renderer.getViewport() != null) {
			// Add tiles
			if (InputManager.getActionState(GameAction.ADD_TILE)) {
				int tileX = (int) InputManager.getMouseTileX();
				int tileY = (int) InputManager.getMouseTileY();
				world.getTiles().set(tileX, tileY, TileInfo.get(Tiles.DIRT));
			}

			// Remove tiles
			if (InputManager.getActionState(GameAction.DELETE_TILE)) {
				int tileX = (int) InputManager.getMouseTileX();
				int tileY = (int) InputManager.getMouseTileY();
				world.getTiles().set(tileX, tileY, TileInfo.get(Tiles.AIR));
			}
		}

		checkInputs();
		// Prevents game from running will paused on in a menu
		renderer.render(ms, !(isPaused || inMenu));
		if (!isPaused && !inMenu) {
			world.gameLoop(ms);
		}

		InputManager.updateKeyStates();
		lastTime = thisTime;
		checkUi();
	}
	
	/**
	 * Sees if the state of skillTreeOn has changed and if so updates the
	 * screen.
	 */
	private void checkUi() {
		World world = World.getInstance();
		if (world.getSkillTreeScreen() != skillTreeOn) {
			renderer.swapScreens(0, 1);
		}
		skillTreeOn = world.getSkillTreeScreen();
	}

	/**
	 * Checks if any of the utility inputs are pressed and implements their
	 * actions; Save, load or quit.
	 */
	private void checkInputs() {
		World world = World.getInstance();
		// Show Debug Screen
		if (InputManager.justPressed(GameAction.SHOW_DEBUG)) {
			renderer.toggleScreen("Debug");
			world.getDebug().clearAllDebugStrings(); // Clear slate
		}

		// Toggle Hitboxes
		if (InputManager.justPressed(GameAction.SHOW_HITBOXES)) {
			world.toggleHitboxes();
		}
	}

	/**
	 * Toggles the Pause Menu on/off - public method to access with controller
	 */
	public void togglePause() {
		isPaused = !isPaused;
		renderer.toggleScreen("Pause Menu");
	}
	
	/**
	 * Toggles the weapon menu on/off - method to access with controller
	 */
	public void toggleWeaponMenu() {
		renderer.toggleScreen("Weapon Screen");
	}

	/**
	 * Toggles the skills menu on/off - method to access with controller
	 */
	public void toggleSkillsMenu() {
		renderer.toggleScreen("Skills Screen");
	}

	/**
	 * Toggles the commerce menu on/off - method to access with controller
	 */
	public void toggleCommerceMenu() {
		renderer.toggleScreen("Commerce Screen");
	}

	/**
	 * Toggles the items menu on/off - method to access with controller
	 */
	public void toggleItemMenu() {
		renderer.toggleScreen("Item Screen");
	}
	
	/**
	 * Initialize engine variables
	 *
	 * @throws IllegalStateException
	 */
	public void initEngine() {
		initGeneric();
		initDefaults();
		initWorld(World.getInstance());
	}

	private void initGeneric() {
		if (renderer == null) {
			throw new IllegalStateException("Renderer uninitialised.");
		}
		InputManager.setViewport(renderer.getViewport());
		lastTime = System.currentTimeMillis();
	}


	public void initDefaults() {
		isGameOver = false;
		skillTreeOn = false;
		isPaused = false;
		inMenu = false;
	}

	public void initWorld(World world) {
		Player player = new Player();
		
		player.setBlocksOtherEntities(true);

		world.resetWorld();
		world.addEntity(player);

		if (world.getTutorialMode()) {
			world.setDecoGenEnabled(false);
			world.setNpcGenEnabled(false);
			world.setBuildingGenEnabled(false);
			world.setTotemGenEnabled(false);
			world.setTerrainDestruction(false);
			player.setPosition(50, 100);
			player.setBlocksOtherEntities(true);

		} else {
			world.setDecoGenEnabled(true);
			world.setNpcGenEnabled(true);
			world.setBuildingGenEnabled(true);
			world.setTotemGenEnabled(true);
			world.setTerrainDestruction(true);
			world.setLightGenEnabled(true);

			// get starting X position, chosen randomly from between -WIDTH and +WIDTH
			int startingX = Room.WIDTH / 2;
			int startingY = Room.HEIGHT / 2 - 1;

			// set the player to the given starting positions, making sure he spawns slightly above ground.
			player.setPosition(startingX, startingY - player.getHeight());
		}

		setWorld(world);
	}

	/**
	 * Change Engine world
	 */
	public void setWorld(World newWorld) {
		renderer.getViewport().calculateBorders(newWorld.getTiles().getWidth(), newWorld.getTiles().getHeight());
	}

	public boolean saveExists() {
		File save = new File("tmp/save.json");
		return (save.exists() && !save.isDirectory());
	}

	/**
	 * Sets tutorial mode
	 */
	public void setTutorialMode(boolean tutorialMode) {
		World.getInstance().setTutorialMode(tutorialMode);
	}
}
