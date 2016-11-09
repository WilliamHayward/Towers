package uq.deco2800.coaster.game.world;

import uq.deco2800.coaster.game.tiles.TileInfo;
import uq.deco2800.coaster.game.tiles.Tiles;

/**
 * A Chunk class containing array for the underlying terrain and information for chunks
 */
public class Chunk {
	public static final int CHUNK_WIDTH = 240; // Default chunk width
	public static final int CHUNK_HEIGHT = 180; // Default chunk height

	private WorldTiles blocks; //array of blocks currently in chunk

	/**
	 * Standard chunk constructor. Receives a starting X position and a seed and will randomly generate the rest in a
	 * deterministic manner.
	 *
	 * @param startingX starting x position of the chunk
	 * @param mapSeed   map seed of the world
	 */
	public Chunk(int startingX, int mapSeed) {
		blocks = new WorldTiles(CHUNK_WIDTH, CHUNK_HEIGHT, CHUNK_WIDTH);

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
	 * Returns the chunks tile set of blocks
	 *
	 * @return chunk's block tileset
	 */
	public WorldTiles getBlocks() {
		return blocks;
	}
}