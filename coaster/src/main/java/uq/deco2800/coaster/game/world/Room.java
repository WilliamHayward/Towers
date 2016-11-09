package uq.deco2800.coaster.game.world;

import uq.deco2800.coaster.game.tiles.TileInfo;
import uq.deco2800.coaster.game.tiles.Tiles;

public class Room {
	public static final int WIDTH = 200; // Default chunk width
	public static final int HEIGHT = 200; // Default chunk height
	private WorldTiles tiles;
	public Room() {
		tiles = new WorldTiles();
		for (int x = 0; x < tiles.getWidth(); x++) {
			for (int y = 0; y < tiles.getHeight(); y++) {
				if (y > tiles.getHeight() / 2) {
					tiles.set(x, y, TileInfo.get(Tiles.BRICK));
				} else {
					tiles.set(x, y, TileInfo.get(Tiles.AIR));
				}
			}
		}
	}
	
	public WorldTiles getTiles() {
		return tiles;
	}
}
