package uq.deco2800.coaster.core;

import javafx.animation.AnimationTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.coaster.core.input.GameAction;
import uq.deco2800.coaster.core.input.InputManager;
import uq.deco2800.coaster.core.sound.SoundCache;
import uq.deco2800.coaster.game.entities.Player;
import uq.deco2800.coaster.game.entities.npcs.mounts.*;
import uq.deco2800.coaster.game.items.ItemDrop;
import uq.deco2800.coaster.game.items.ItemRegistry;
import uq.deco2800.coaster.game.mechanics.Difficulty;
import uq.deco2800.coaster.game.preservation.ExportableWorld;
import uq.deco2800.coaster.game.preservation.Preservation;
import uq.deco2800.coaster.game.tiles.TileInfo;
import uq.deco2800.coaster.game.tiles.Tiles;
import uq.deco2800.coaster.game.world.Chunk;
import uq.deco2800.coaster.game.world.MiniMap;
import uq.deco2800.coaster.game.world.Waveform;
import uq.deco2800.coaster.game.world.World;
import uq.deco2800.coaster.graphics.Renderer;
import uq.deco2800.coaster.graphics.notifications.Toaster;
import java.io.File;
import java.util.Random;


//The main game loop class. The function handle(long now) is called every tick.
public class Engine extends AnimationTimer {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Renderer renderer;
	private long lastTime;

	private boolean skillTreeOn = false;
	private boolean isTutorial = false;
	private boolean isPaused = false;
	private boolean inMenu = false;
	private boolean isMultiplayer = false;
	private boolean passiveInfo = false;

	private String currentMenuName;

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

		if (InputManager.justPressed(GameAction.PAUSE) && !inMenu) {
			togglePause();
		}

		// If a different menu screen is active - disable it
		if (InputManager.justPressed(GameAction.PAUSE) && inMenu && currentMenuName != null) {
			renderer.disableScreen(currentMenuName);
			inMenu = false;
		}

		if (InputManager.justPressed(GameAction.MUTE)) {
			if (SoundCache.getMute()) {
				SoundCache.unmute();
			} else {
				SoundCache.mute();
			}
		}
		if (InputManager.justPressed(GameAction.VOLUME_UP)) {
			SoundCache.setVolume(SoundCache.getVolume() + SoundCache.VOLUME_STEP);
			Toaster.toast("Volume: " + Integer.toString((int) (SoundCache.getVolume() * 100)) + "%");
		}

		if (InputManager.justPressed(GameAction.VOLUME_DOWN)) {
			SoundCache.setVolume(SoundCache.getVolume() - SoundCache.VOLUME_STEP);
			Toaster.toast("Volume: " + Integer.toString((int) (SoundCache.getVolume() * 100)) + "%");
		}

		String inventoryScreenId = "Inventory";
		if (InputManager.justPressed(GameAction.INVENTORY) && !isPaused
				&& (!inMenu || renderer.isActiveScreen(inventoryScreenId))) {
			inMenu = !inMenu;
			currentMenuName = inventoryScreenId;
			renderer.toggleScreen(inventoryScreenId);
		}

		// Store Button only works when standing next to store NPC
		String storeScreenId = "Store";
		String bankScreenId = "Bank";
		if (InputManager.justPressed(GameAction.TRADER_NPC) && !isPaused

				&& (!inMenu || renderer.isActiveScreen(storeScreenId) || renderer.isActiveScreen(bankScreenId))) {

			Player player = world.getFirstPlayer();
		}

		if (InputManager.justPressed(GameAction.DEBUG_CONSOLE)) {
			renderer.toggleScreen("Debug Console");
		}

		// Skill Tree key press
		if (InputManager.justPressed(GameAction.SKILL_TREE_UI) && !isPaused && !passiveInfo
				&& (!inMenu || renderer.isActiveScreen("Skill Tree"))) {
			inMenu = !inMenu;
			currentMenuName = "Skill Tree";
			renderer.toggleScreen("Skill Tree");
		}

		if (world.isGameOver() && !isGameOver) {
			// Show Game Over Screen and button
			inMenu = true;
			isGameOver = true;
			renderer.toggleScreen("Game Over");
		}
		if (renderer.getViewport() != null) {
			// Add NPCs
			if (InputManager.justPressed(GameAction.ADD_NPC)) {
			}

			if (InputManager.justPressed(GameAction.ADD_MOUNT)) {
				int tileX = (int) InputManager.getMouseTileX();
				int tileY = (int) InputManager.getMouseTileY();


				ItemDrop.drop(ItemRegistry.getItem("ex_ammo"), tileX + 7, tileY - 5);

				ItemDrop.drop(ItemRegistry.getItem("ex_ammo"), tileX + 7, tileY - 5);


				Mount testMount = null;
				Random rn = new Random();
				switch (rn.nextInt(7)) {
					case 0:
						testMount = new JumpingMount();
						break;
					case 1:
						testMount = new BatMount();
						break;
					case 2:
						testMount = new RhinoMount();
						break;
					case 3:
						testMount = new ElephantMount();
						break;
					case 4:
						testMount = new TurtleMount();
						break;
					case 5:
						testMount = new BirdMount();
						break;
					case 6:
						testMount = new DogMount();
						break;
					default:
						testMount = new Mount();
						break;
				}
				testMount.setPosition(tileX + 10, tileY);
				world.addEntity(testMount);

			}

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

		if (InputManager.getActionState(GameAction.PRINT_TILE)) {
			double tileX = InputManager.getMouseTileX();
			double tileY = InputManager.getMouseTileY();
			Toaster.toast("X co-ordinate " + tileX);
			Toaster.toast("Y co-ordinate " + tileY);
		}

		if (InputManager.getActionState(GameAction.ENABLE_CHECKPOINTS)) {
			((Player) World.getInstance().getFirstPlayer()).setCheckPointsEnabled(true);
			Toaster.ejectAllToast();
			Toaster.toast("Check points auto loading enabled");
		}

		//SoundCache.getInstance();
		//SoundCache.tick(ms);

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

		// Show/Hide Map
		if (InputManager.justPressed(GameAction.SHOW_MAP)) {
			MiniMap.toggleVisibility();
		}

		// Change button mapping on the fly
		if (InputManager.justPressed(GameAction.RE_MAP)) {
			InputManager.flagToSwap();
		}
		if (InputManager.justPressed(GameAction.QUERY_KEY)) {
			InputManager.queryKey();
		}
		// Toggle Hitboxes
		if (InputManager.justPressed(GameAction.SHOW_HITBOXES)) {
			world.toggleHitboxes();
			Toaster.toast("Visible hitboxes " + Boolean.toString(world.renderHitboxes()));
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
	 * Toggles the tutorial menu on/off - method to access with controller
	 */
	public void toggleTutorialMenu() {
		if (renderer.isActiveScreen("Weapon Screen")) {
			toggleWeaponMenu();
		}
		isTutorial = !isTutorial;
		renderer.toggleScreen("Tutorial Screen");
	}

	/**
	 * Toggles the weapon menu on/off - method to access with controller
	 */
	public void toggleWeaponMenu() {
		isTutorial = !isTutorial;
		renderer.toggleScreen("Weapon Screen");
	}

	/**
	 * Toggles the skills menu on/off - method to access with controller
	 */
	public void toggleSkillsMenu() {
		isTutorial = !isTutorial;
		renderer.toggleScreen("Skills Screen");
	}

	/**
	 * Toggles the commerce menu on/off - method to access with controller
	 */
	public void toggleCommerceMenu() {
		isTutorial = !isTutorial;
		renderer.toggleScreen("Commerce Screen");
	}

	/**
	 * Toggles the items menu on/off - method to access with controller
	 */
	public void toggleItemMenu() {
		isTutorial = !isTutorial;
		renderer.toggleScreen("Item Screen");
	}

	/**
	 * Save the game state - public method to access with controller
	 */
	public void save() {
		save("tmp/save.json");
	}

	/**
	 * Save the game state - public method to access with controller
	 */
	public void save(String file) {
		Toaster.toast("Saving...");
		Preservation.save(file);
		Toaster.toast("Saved.");
	}


	/**
	 * load game state - public method to access with controller.
	 */
	public void load() {
		load("tmp/save.json");
	}

	/**
	 * load game state
	 */
	public void load(String file) {
		World world = World.getInstance();
		Toaster.toast("Attempting to load...");
		ExportableWorld load = Preservation.load(file);
		if (load.entities != null) {
			world.loadEntities(load.entities);
		}
		if (load.playerEntities != null) {
			if (load.playerEntities.size() == 1) {
				world.loadPlayer(load.playerEntities.get(0));
			} else {
				logger.debug("Can't load non singular number of players at the moment.");
			}
		}
		Toaster.toast("Loaded.");
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
			world.setLightning(false);
			world.setRandomSeedEnabled(false);
			player.setPosition(50, 100);
			player.setBlocksOtherEntities(true);
			world.setTiles(world.getTutorialWorld());

		} else {
			world.setDecoGenEnabled(true);
			world.setNpcGenEnabled(true);
			world.setBuildingGenEnabled(true);
			world.setTotemGenEnabled(true);
			world.setTerrainDestruction(true);
			world.setLightning(true);
			world.setRandomSeedEnabled(true);
			world.setLightGenEnabled(true);

			// add terrain waveforms
			world.addTerrainWaveform(new Waveform(40, 80)); // mountainous waveform
			world.addTerrainWaveform(new Waveform(200, 10)); // bumpy waveform
			world.addTerrainWaveform(new Waveform(8, 120)); // hill waveform
			world.addTerrainWaveform(new Waveform(10, 30)); // flat waveform

			// add cave waveforms
			world.addCaveWaveform(new Waveform(16, 8));
			world.addCaveWaveform(new Waveform(8, 16));
			world.addCaveWaveform(new Waveform(4, 32));

			// get starting X position, chosen randomly from between -CHUNK_WIDTH and +CHUNK_WIDTH
			int startingX = Chunk.CHUNK_WIDTH / 2;
			int startingY = Chunk.CHUNK_HEIGHT / 3;

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
	 * Wrapper method for world.setDifficulty
	 *
	 * @param option One of the Difficulty enum values represent scale factor.
	 */
	public void setDifficulty(Difficulty option) {
		World.getInstance().setDifficulty(option);
	}

	/**
	 * Sets tutorial mode
	 */
	public void setTutorialMode(boolean tutorialMode) {
		World.getInstance().setTutorialMode(tutorialMode);
	}
}
