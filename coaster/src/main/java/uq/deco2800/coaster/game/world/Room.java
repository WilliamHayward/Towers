package uq.deco2800.coaster.game.world;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import uq.deco2800.coaster.game.tiles.TileInfo;
import uq.deco2800.coaster.game.tiles.Tiles;

/**
 * A Chunk class containing array for the underlying terrain and information for chunks
 */
public class Room {
	public static final int WIDTH = 100; // Room width
	public static final int HEIGHT = 100; // Room height

	private String name;
	private WorldTiles blocks; //array of blocks currently in chunk
	private Map<Integer, Coordinate> waypoints;

	/**
	 * Standard chunk constructor. Receives a starting X position and a seed and will randomly generate the rest in a
	 * deterministic manner.
	 *
	 * @param startingX starting x position of the chunk
	 * @param mapSeed   map seed of the world
	 * @throws FileNotFoundException 
	 */
	public Room() {
		waypoints = new HashMap<>();
		try {
			load("rooms/test.room");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void load(String fileName) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		blocks = new WorldTiles(WIDTH, HEIGHT, WIDTH);
		FileReader fileReader = new FileReader(classLoader.getResource("rooms/test.room").getFile());
		BufferedReader file = new BufferedReader(fileReader);
		String line;
		line = file.readLine();
		String[] data;
		data = line.split(",");
		int position = 0;
		name = data[position];
		position++;
		String type;
		String modifier;
		line = file.readLine();
		for (int y = 0; y < HEIGHT; y++) {
			data = line.split(",");
			for (int x = 0; x < WIDTH; x++) {
				if (data[x].contains("-")) {
					type = data[x].split("-")[0];
					modifier = data[x].split("-")[1];
					if (modifier.equals("P")) {
						World.getInstance().setSpawn(x, y);
						System.out.println("Set spawn: " + x + ", " + y);
					} else {
						int pos = Integer.parseInt(modifier);
						Coordinate coordinates = new Coordinate(x, y);
						waypoints.put(pos, coordinates);
					}
				} else {
					type = data[x];
				}
				switch (Integer.parseInt(type)) {
					case 0:
						blocks.get(x, y).setTileType(TileInfo.get(Tiles.AIR));	
						break;
					case 1:
						blocks.get(x, y).setTileType(TileInfo.get(Tiles.DIRT));
						break;
					default:
						System.out.println(type);
						break;
				}
			}
			line = file.readLine();
		}
		file.close();
	}
	
	/**
	 * Returns waypoints of room
	 */
	public Map<Integer, Coordinate> getWaypoints() {
		return waypoints;
	}
	
	/**
	 * Return name of the room
	 */
	public String getName() {
		return name;
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