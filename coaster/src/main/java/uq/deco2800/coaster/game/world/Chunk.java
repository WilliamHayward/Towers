package uq.deco2800.coaster.game.world;

import uq.deco2800.coaster.game.tiles.TileInfo;
import uq.deco2800.coaster.game.tiles.Tiles;

/**
 * A Chunk class containing array for the underlying terrain and information for chunks
 */
public class Chunk {
	public static final int CHUNK_WIDTH = 240; // Default chunk width
	public static final int CHUNK_HEIGHT = 180; // Default chunk height

	// Middle chunk position (exact centre chunk in map, half way between 0 and INT MAX).
	public static final int MIDDLE_CHUNK_POS = CHUNK_WIDTH * Math.round((float) (Integer.MAX_VALUE / 2) / CHUNK_WIDTH);

		public static final int GROUND_LEVEL = 40; // Default ground level (counted from the top of map)
	public static final int GROUND_LEVEL_LIMIT = 100; // Default ground level limit (counted from the top of map)
	
	private int id; //unique chunk id
	private int xPos; //x position of chunk
	private WorldTiles blocks; //array of blocks currently in chunk

	/**
	 * Chunk constructor mainly used for testing or specific special chunks.
	 *
	 * @param tileTemplate tile set to insert into chunk
	 * @param biomeType    biome type of chunk
	 * @param xPos         x position of chunk
	 * @param topBlocks    the top block height at each x coordinate in the chunk
	 */
	public Chunk(WorldTiles tileTemplate, int xPos, int[] topBlocks) {
		if (tileTemplate.getWidth() != CHUNK_WIDTH || tileTemplate.getHeight() != CHUNK_HEIGHT) {
			throw new IllegalArgumentException("Invalid chunk dimensions");
		}

		this.blocks = tileTemplate;
		this.xPos = xPos;
		this.id = xPos / CHUNK_WIDTH;
	}

	/**
	 * Standard chunk constructor. Receives a starting X position and a seed and will randomly generate the rest in a
	 * deterministic manner.
	 *
	 * @param startingX starting x position of the chunk
	 * @param mapSeed   map seed of the world
	 */
	public Chunk(int startingX, int mapSeed) {
		blocks = new WorldTiles(CHUNK_WIDTH, CHUNK_HEIGHT, CHUNK_WIDTH);

		this.xPos = CHUNK_WIDTH * ((MIDDLE_CHUNK_POS + startingX) / CHUNK_WIDTH);
		this.id = this.xPos / CHUNK_WIDTH;

		generateFlat(0, CHUNK_WIDTH, CHUNK_HEIGHT / 2, TileInfo.get(Tiles.DIRT), true);
	}

	/**
	 * Generate flat landscape in given range
	 */
	private void generateFlat(int startX, int endX, int y, TileInfo tile, boolean filled) {
		for (int x = startX; x < endX; x++) {
			blocks.get(x, y).setTileType(tile);
			if (filled) {
				fillVerticalSpace(x, y, CHUNK_HEIGHT, tile);
			}
		}
	}

	/**
	 * Make a pillar of given material in given range
	 */
	private void fillVerticalSpace(int x, int startY, int endY, TileInfo newTile) {
		for (int y = startY; y < endY; y++) {
			blocks.set(x, y, newTile);
		}
	}
	
	/**
	 * Set block at position to given tile
	 */
	public void set(int x, int y, TileInfo tile) {
		blocks.get(x, y).setTileType(tile);
	}

	/**
	 * Returns the chunk's x position on map
	 *
	 * @return chunk's x position
	 */
	public int getX() {
		return xPos;
	}

	/**
	 * Returns the chunk's id
	 *
	 * @return chunk id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the chunks tile set of blocks
	 *
	 * @return chunk's block tileset
	 */
	public WorldTiles getBlocks() {
		return blocks;
	}
}