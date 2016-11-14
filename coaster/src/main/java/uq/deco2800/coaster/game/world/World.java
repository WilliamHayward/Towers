package uq.deco2800.coaster.game.world;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.coaster.game.debug.Debug;
import uq.deco2800.coaster.game.entities.Entity;
import uq.deco2800.coaster.game.entities.buildings.traps.Trap;
import uq.deco2800.coaster.game.entities.buildings.turrets.Turret;
import uq.deco2800.coaster.game.entities.enemies.Emitter;
import uq.deco2800.coaster.game.entities.enemies.Enemy;
import uq.deco2800.coaster.game.entities.players.BluePlayer;
import uq.deco2800.coaster.game.entities.players.Player;
import uq.deco2800.coaster.game.tiles.TileInfo;
import uq.deco2800.coaster.graphics.Camera;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A World class containing array for the underlying terrain as well as the list
 * of entities in the level.
 */
public class World {

	private static final Logger logger = LoggerFactory.getLogger(World.class);

	private static World world = new World(); // Singleton instance
	private WorldTiles tiles; // World tiles

	private long framesPerSecond; // Frames per second of the running game
	static final int NUM_MOBS_PER_CHUNK = 50; // default number of mobs that can be generated in a chunk
	private boolean renderHitboxes = false; // whether or not hit boxes are shown.
	private List<Entity> allEntities = new ArrayList<>(); // list of all entities
	private List<Player> playerEntities = new ArrayList<>(); // list of player entities
	private List<Entity> enemyEntities = new ArrayList<>();
	private List<Entity> turretEntities = new ArrayList<>();
	private List<Entity> trapEntities = new ArrayList<>();
	private List<Entity> newEntities = new ArrayList<>(); // list of new entities to be added
	private List<Entity> deleteEntities = new ArrayList<>(); // list of deleted entities to be deleted
	private Debug debug = new Debug(); // debugger initialiser
		
	private int entityRenderDistance = Room.WIDTH; // default entity rendering distance

	private List<Coordinate> spawns = new ArrayList<>();
	
	//lighting
	private int globalLightLevel = 100;

	/**
	 * Returns the singleton instance of World
	 */
	public static World getInstance() {
		return world;
	}

	/**
	 * Initialise an empty world instance
	 */
	World() {
		logger.info("new empty world instance");
		tiles = new WorldTiles(Room.WIDTH, Room.HEIGHT, Room.WIDTH);
	}

	public void start(Camera camera) {
		Player player = new BluePlayer();
		camera.setFollow(player);
		this.addEntity(player);
	}
	
	/**
	 * Set the horizontal distance away from the Player that entities will still be processed
	 */
	public void setEntityRenderDistance(int entityRenderDistance) {
		this.entityRenderDistance = entityRenderDistance;
	}

	/**
	 * Return the horizontal distance away from the Player that entities will still be processed
	 */
	public int getEntityRenderDistance() {
		return entityRenderDistance;
	}

	/**
	 * Set the ambient light level of the world
	 */
	public void setGlobalLightLevel(int lightLevel) {
		this.globalLightLevel = lightLevel;
	}

	/**
	 * Return the ambient light level of the world
	 */
	public int getGlobalLightLevel() {
		return globalLightLevel;
	}
	
	/**
	 * Reset the world tiles to an empty state and remove all decoration
	 * entities.
	 */
	public void resetTiles() {
		logger.info("tiles reset");
		tiles = new WorldTiles(Room.WIDTH, Room.HEIGHT, Room.WIDTH);
	}

	/**
	 * Reset the world back to its empty state. Clears all entities and states.
	 */
	public void resetWorld() {
		// Firstly make sure to exit the room
		clearEntities();
		renderHitboxes = false;
		debug = new Debug();
		resetTiles();
	}

	/**
	 * Reset the world to a state for use in testing
	 */
	public void debugReset() {
		// Firstly make sure to exit the room
		clearEntities();
		renderHitboxes = false;
		debug = new Debug();
		resetTiles();
	}

	/**
	 * inverts boolean value renderHitboxes
	 */
	public void toggleHitboxes() {
		renderHitboxes = !renderHitboxes;
	}

	/**
	 * returns value renderHitboxes
	 *
	 * @return boolean value renderHitbox variable
	 */
	public boolean renderHitboxes() {
		return renderHitboxes;
	}

	/**
	 * Adds an entity to a private Entity array newEntities Note that this does
	 * not add entity to the game, but addEntities will
	 *
	 * @param entity to be added
	 */
	public void addEntity(Entity entity) {
		newEntities.add(entity);
	}

	/**
	 * Adds an entity to a private Entity array deleteEntities Note that this
	 * does not delete entity from the game, but deleteEntities will
	 *
	 * @param entity to be
	 */
	public void deleteEntity(Entity entity) {
		deleteEntities.add(entity);
	}

	/**
	 * Returns a controller for DebugScreen for this world object.
	 *
	 * @return Debug object
	 */
	public Debug getDebug() {
		return debug;
	}


	/**
	 * Returns a list of all Entities
	 *
	 * @return list of all Entities
	 */
	public List<Entity> getAllEntities() {
		return allEntities;
	}

	/**
	 * Returns a list of Trap Entities
	 *
	 * @return list of Trap Entities
	 */
	public List<Entity> getTrapEntities() {
		return trapEntities;
	}

	/**
	 * Returns a list of Turret Entities
	 *
	 * @return list of Turret Entities
	 */
	public List<Entity> getTurretEntities() {
		return turretEntities;
	}

	/**
	 * Returns a list of Player Entities
	 *
	 * @return list of Player Entities
	 */
	public List<Player> getPlayerEntities() {
		return playerEntities;
	}

	/**
	 * Returns the frames per second of the game
	 *
	 * @return frames per second represented as a long
	 */
	public long getFps() {
		return framesPerSecond;
	}

	/**
	 * Returns WorldTiles object of this world
	 *
	 * @return WorldTiles
	 */
	public WorldTiles getTiles() {
		return tiles;
	}

	/**
	 * This is called every game tick, and calls down into our list of entities
	 * and calls tick on them as well.
	 */
	public void gameLoop(long ms) {
		// converts to frames per second
		framesPerSecond = ms == 0 ? Long.MAX_VALUE : 1000 / ms;
		processEntities(ms);
		addEntities();
		removeEntities();
		
	}

	/**
	 * adds entities in list entities and playerEntities from that exists in
	 * newEntities
	 */
	private void addEntities() {
		for (Entity entity : newEntities) {
			allEntities.add(entity);
			if (entity instanceof Player) {
				playerEntities.add((Player) entity);
			} else if (entity instanceof Enemy) {
				enemyEntities.add((Enemy) entity);
			} else if (entity instanceof Turret) {
				turretEntities.add((Turret) entity);
			} else if (entity instanceof Trap) {
				trapEntities.add((Trap) entity);
			}
		}
		newEntities.clear();
	}

	/**
	 * removes entities in list entities and playerEntities from that exists in
	 * deleteEntities
	 */
	private void removeEntities() {
		for (Entity entity : deleteEntities) {
			allEntities.remove(entity);
			if (entity instanceof Player) {
				playerEntities.remove(entity);
			} else if (entity instanceof Enemy) {
				enemyEntities.remove(entity);
			} else if (entity instanceof Turret) {
				turretEntities.remove(entity);
			} else if (entity instanceof Trap) {
				trapEntities.remove(entity);
			}
		}
		deleteEntities.clear();
	}

	/**
	 * Controls flow of the game, such as: make entities move generates terrain
	 * if any entity is near an empty chunk generates npc near player if entity
	 * is an instance of player
	 */

	private void processEntities(long ms) {
		for (Entity entity : allEntities) {
			entity.entityLoop(ms);
		}
	}
	
	/**
	 * @return the first player that was added to the world. Currently, should
	 * be the only player.
	 */
	public Player getFirstPlayer() {
		if (playerEntities.isEmpty() && newEntities.isEmpty()) {
			return null;
		} else if (!newEntities.isEmpty() && newEntities.get(0) instanceof Player) {
			return (Player) newEntities.get(0);
		}
		return playerEntities.isEmpty() ? null : playerEntities.get(0);
	}

	/**
	 * This method will add an entity to this world and set it's position.
	 *
	 * @param entity the entity to be added
	 * @param x      the x coordinate to place it
	 * @param y      the y coordinate to place it
	 * @return the entity that was added.
	 */
	Entity addEntityToWorldPos(Entity entity, float x, float y) {
		entity.setWorld(this);
		entity.setPosition(x, y);
		this.addEntity(entity);
		return entity;
	}

	/**
	 * Clears every entity list in the current world
	 */
	private void clearEntities() {
		allEntities.clear();
		playerEntities.clear();
		newEntities.clear();
		deleteEntities.clear();
	}
	
	public void addSpawn(int spawnX, int spawnY) {
		Coordinate spawn = new Coordinate(spawnX, spawnY);
		spawns.add(spawn);
	}
	
	public Coordinate getRandomSpawn() {
		Random random = new Random();
		Coordinate spawn = spawns.get(random.nextInt(spawns.size()));
		return spawn;
	}
	
	public void loadRoom() {
		Room room = new Room();

		for (int x = 0; x < Room.WIDTH; x++) {
			for (int y = 0; y < Room.HEIGHT; y++) {
				TileInfo chunkTile = room.getBlocks().get(x, y).getTileType();
				tiles.get(x, y).setTileType(chunkTile);
			}
		}
		Emitter emitter = new Emitter(room.getWaypoints());
		emitter.setPosition(room.getWaypoints().get(0).getX(), room.getWaypoints().get(0).getY());
		this.addEntity(emitter);		
	}

	public List<Entity> getEnemyEntities() {
		return enemyEntities;
	}
}