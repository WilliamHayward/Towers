package uq.deco2800.coaster.game.tiles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import uq.deco2800.coaster.graphics.sprites.Sprite;

public class Tile {
	private static Logger logger = LoggerFactory.getLogger(Tile.class);
	private TileInfo type;
	private String variant;
	private boolean visited;
	private Sprite sprite;
	/* Light levels range between 0 and 100: 0 being no darkness, 100 being completely black */
	List<Integer> lightLevel = new ArrayList<>();
	
	public Tile() {
		//Default value
		type = TileInfo.get(Tiles.AIR);
		variant = "DEFAULT";
		sprite = new Sprite(type, variant);
	}

	public Tile(TileInfo ti) {
		setTileType(ti);
	}

	public Tile(Tiles t) {
		this(TileInfo.get(t));
	}

	/**
	 * Converts a given tile into a blank one.
	 */
	public void convertToBlank() {
		sprite = null;
	}

	/**
	 * Returns the tile type for a given tile.
	 *
	 * @return tile type
	 */
	public TileInfo getTileType() {
		return type;
	}

	/**
	 * Sets the tile type for a given tile.
	 *
	 * @param newType the type to be set
	 */
	public void setTileType(TileInfo newType) {
		List<String> variants = newType.getVariants();
		if (!variants.isEmpty()) {
			setTileType(newType, newType.getVariants().get(0));
		} else {
			throw new IllegalArgumentException(
					"Tile of type " + newType.getDisplayName() + " has no variants. Can't change to this tile type");
		}
	}

	/**
	 * Adds a new light source for the tile to be affected by.
	 *
	 * @param lightLevel an integer value between 0 and 100.
	 */
	public void setLightLevel(int lightLevel) { this.lightLevel.add(lightLevel); }

	/**
	 * Sets the flag for whether a tile has been visited by the player or not.
	 *
	 * @param visited a boolean value
	 */
	public void setVisited(boolean visited) { this.visited = visited; }

	/** Gets the flag for whether a tile has been visited by the player or not.
	 *
	 * @return a boolean value
	 */
	public boolean getVisited() { return this.visited; }

	/* Remove a light source */
	public void removeLightLevel(int lightLevel) {
		this.lightLevel.remove(Integer.valueOf(lightLevel));
	}

	/**
	 * Sets the type and variant of a given tile.
	 *
	 * @param newType the type for the tile
	 * @param variant the variant of the tile's type.
	 */
	public void setTileType(TileInfo newType, String variant) {
		type = newType;
		this.variant = variant;
		sprite = new Sprite(type, variant);
	}

	/**
	 * Gets the variant of a given tile.
	 *
	 * @return given tile's variant
	 */
	public String getVariant() {
		return variant;
	}

	/**
	 * Sets the variant of a given tile.
	 *
	 * @param variant variant to be assigned to tile
	 */
	public void setVariant(String variant) {
		this.variant = variant;
		sprite = new Sprite(type, variant);
	}

	/**
	 * Gets the sprite for a given tile.
	 *
	 * @return sprite for tile
	 */
	public Sprite getSprite() {
		if (sprite == null) {
			logger.debug("Tried to load null sprite: " + type.getDisplayName());
		}
		return sprite;
	}

}
