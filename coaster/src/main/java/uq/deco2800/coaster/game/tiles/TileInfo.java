package uq.deco2800.coaster.game.tiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uq.deco2800.coaster.game.entities.Entity;

/**
 * This class stores the information about a type of tile. For the most basic tile, this includes the sprite, name,
 * whether or not the tile is an obstacle,
 *
 * This class also contains the registry of all the tiles in the game
 */
public abstract class TileInfo {
	public static final int BLOCK_WIDTH = 32;
	public static final int BLOCK_HEIGHT = 32;
	//region TileInfo registry
	//A map from Tiles (type of tile) to TileInfo. This is where you will add new tiles and their data to the game.
	private static Map<Tiles, TileInfo> tileRegistry = new HashMap<>();
	// Variable which keeps track of whether the tiles have been loaded yet.
	private static boolean hasLoaded = false;

	/**
	 * @return a copy of the tile registry.
	 */
	public static Map<Tiles, TileInfo> getTileRegistry() {
		return Collections.unmodifiableMap(tileRegistry);
	}

	/**
	 * @return whether or not the tiles have been loaded yet.
	 */
	public static boolean hasLoaded() {
		return hasLoaded;
	}

	/**
	 * This method is responsible for loading the information about all the tiles including: - The type, such as
	 * Tiles.DIRT. - The display name (what is shown to the user). - The sprite for the tile in it's default variant. -
	 * Whether or not the sprite is a natural surface (used for biome generation). - Any other properties that are
	 * defined in the general metadata region of this file.
	 */
	public static void registerTiles() {

		registerTile((new TileSolid(Tiles.DIRT)).setDisplayName("Dirt").setDefaultFilename("brick.png")
				
				.setNaturalSurface(true).setDestructible(true).setDamagedTile(Tiles.DIRT)
				.setBackgroundTile(Tiles.AIR));
		registerTile((new TileGas(Tiles.AIR)).setDisplayName("Air").setDefaultFilename("air.png").setDestructible(false));
		hasLoaded = true;
	}

	/**
	 * Puts a tile in the registry.
	 *
	 * @param tile tile to be put in the registry.
	 */
	private static void registerTile(TileInfo tile) {
		tileRegistry.put(tile.getType(), tile);
	}

	/**
	 * Removes all the tiles from the registry.
	 */
	public static void clearRegistry() {
		tileRegistry = new HashMap<>();
		hasLoaded = false;
	}

	public static TileInfo get(Tiles type) {
		return tileRegistry.get(type);
	}
	//endregion

	//region TileInfo sprite metadata
	// This is a mapping from a tile's variant to a sprite filename.
	private final Map<String, String> spriteFilenames = new HashMap<>();

	/**
	 * @return the sprite filenames mapping
	 */
	public Map<String, String> getSpriteFilenames() {
		return spriteFilenames;
	}

	/**
	 * Will set the filename for the default variant.
	 *
	 * @param filename the filename to set
	 * @return this instance of the TileInfo
	 */
	public TileInfo setDefaultFilename(String filename) {
		return setFilename("DEFAULT", filename);
	}

	/**
	 * Will set the filename for a given variant
	 *
	 * @param variant  the variant of the tile
	 * @param filename the filename for the sprite
	 * @return this instance of the TileInfo
	 */
	TileInfo setFilename(String variant, String filename) {
		this.spriteFilenames.put(variant, "sprites/tiles/" + filename);
		return this;
	}

	/**
	 * @return the width of the sprite for the tile
	 */
	public int getBlockWidth() {
		return BLOCK_WIDTH;
	}

	/**
	 * @return the height of the sprite for the tile
	 */
	public int getBlockHeight() {
		return BLOCK_HEIGHT;
	}

	/**
	 * @return the number of frames in the sprite
	 */
	public int getNumSpriteFrames() {
		return 1;
	}

	/**
	 * @return the duration in ms for each frame of the sprite
	 */
	public int getSpriteFrameDuration() {
		return 1;
	}
	//endregion

	//region TileInfo general metadata
	// Type of the tile, list seen in the Tiles enum
	private final Tiles type;
	// The name shown to users if they see this tile
	private String displayName = null;
	// Whether or not this tile is a natural surface. In use for biome generation
	private boolean naturalSurface = false;
	private boolean destructible = true;
	private Tiles backgroundTile = null;
	private Tiles damagedTile = null;


	TileInfo(Tiles type) {
		this.type = type;
	}

	/**
	 * @return this tile's type
	 */
	public Tiles getType() {
		return type;
	}

	/**
	 * @return this tile's display name
	 */
	public String getDisplayName() {
		return displayName == null ? type.name() : displayName;
	}

	/**
	 * Sets the display name of the tile
	 *
	 * @param displayName the name to set
	 * @return this instance of the TileInfo
	 */
	public TileInfo setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	/**
	 * @return whether or not this tile is a natural surface.
	 */
	public boolean isNaturalSurface() {
		return naturalSurface;
	}

	/**
	 * @return whether or not this tile is a destructible tile.
	 */
	public boolean isDestructible() {
		return destructible;
	}

	/**
	 * @return the background tile for a given tile type.
	 */
	public TileInfo getBackgroundTile() {
		return TileInfo.get(this.backgroundTile);
	}

	/**
	 * @return the damaged tile for a given tile type.
	 */
	public TileInfo getDamagedTile() {
		return TileInfo.get(this.damagedTile);
	}

	/**
	 * Will set the background tile for a given type.
	 *
	 * @param backgroundTile the tile to be set as the background tile.
	 * @return this instance of TileInfo.
	 */
	public TileInfo setBackgroundTile(Tiles backgroundTile) {
		this.backgroundTile = backgroundTile;
		return this;
	}

	/**
	 * Will set the damaged tile for a given type.
	 *
	 * @param damagedTile the tile to be set as the damaged tile.
	 * @return this instance of TileInfo.
	 */
	public TileInfo setDamagedTile(Tiles damagedTile) {
		this.damagedTile = damagedTile;
		return this;
	}


	/**
	 * Sets this tile to be a natural surface
	 *
	 * @param naturalSurface whether or not this tile is a natural surface
	 * @return this instance of TileInfo
	 */
	public TileInfo setNaturalSurface(boolean naturalSurface) {
		this.naturalSurface = naturalSurface;
		return this;
	}


	/**
	 * Sets this tile to be destructible
	 *
	 * @param destructible whether or not this tile is destructible
	 * @return this instance of TileInfo
	 */
	public TileInfo setDestructible(boolean destructible) {
		this.destructible = destructible;
		return this;
	}

	/**
	 * This is an abstract method that needs to be implemented in subclasses that will
	 *
	 * @return whether or not this block is an obstacle for the player in the World.
	 */
	public abstract boolean isObstacle();

	/**
	 * This is an abstract method that needs to be implemented in subclasses that will
	 *
	 * @return whether or not this block is a liquid for the player in the World.
	 */
	public abstract boolean isLiquid();


	/**
	 * This method is called when an entity is on top of a tile
	 *
	 * @param entity The entity which is on the tile
	 * @param tile   The instance of the tile
	 * @param ms     The time since the last game loop tick in ms
	 */
	public void onEntityOnTopOfBlock(Entity entity, Tile tile, long ms) {
		// By default, nothing should happen when an entity is on top of a block.
	}

	/**
	 * @return all the variants which have been given sprites
	 */
	public List<String> getVariants() {
		return new ArrayList<>(this.getSpriteFilenames().keySet());
	}

	//We use the type of the tile to uniquely identify it.
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || !(o instanceof TileInfo)) {
			return false;
		}

		TileInfo tileInfo = (TileInfo) o;

		return type.equals(tileInfo.type);
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}
	//endregion
}
