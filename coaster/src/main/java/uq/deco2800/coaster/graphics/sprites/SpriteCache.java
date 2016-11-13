package uq.deco2800.coaster.graphics.sprites;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import uq.deco2800.coaster.game.tiles.TileInfo;

//Our sprite cache! Stores the actual images for the sprites and registers them against an ID.
//If you need to add a new sprite to the game, you can do it here.
public class SpriteCache {

	private SpriteCache() {
	}

	private static Map<SpriteList, SpriteInfo> spritesCache = new HashMap<>();
	private static Map<TileInfo, Map<String, SpriteInfo>> tileSpritesCache = new HashMap<>();
	private static boolean hasLoaded = false;

	/**
	 * To be called once, on game startup. Loads all assets from disk into our
	 * FrameCollection cache.
	 *
	 * @throws IOException
	 *             if a sprite cannot be found, or the input dimensions are
	 *             wrong
	 */
	public static void loadAllSprites() throws IOException {
		// CARL:
		loadStandardSprite(SpriteList.CARL, "sprites/carl.png", 32, 32, 1, 1);
		
		// Player Sprites
		loadStandardSprite(SpriteList.KNIGHT_STANDING, "sprites/player-blue-standing.png", 110, 156, 1, 0);
		
		loadStandardSprite(SpriteList.KNIGHT_WALKING, "sprites/player-blue-walking.png", 110, 156,
				2, 200); //TODO: CLipping issues between the two frames
		
		loadStandardSprite(SpriteList.KNIGHT_JUMPING, "sprites/player-blue-jumping.png", 110, 156, 1, 1);
		loadStandardSprite(SpriteList.KNIGHT_CROUCH, "sprites/player-blue-crouching.png", 113, 117, 1, 1); //TODO: The helmet changes size. Investigate!
		loadStandardSprite(SpriteList.KNIGHT_KNOCK_BACK, "sprites/player-blue-hit.png", 110, 156, 1, 1);

		loadStandardSprite(SpriteList.PARTICLE1, "sprites/particle1.png", 50, 50, 1, 1);

		loadStandardSprite(SpriteList.BARREL_MACHINE_GUN, "sprites/barrel-machine-gun.png", 47, 10, 1, 1);
		loadStandardSprite(SpriteList.BARREL_CANNON, "sprites/barrel-cannon.png", 41, 16, 1, 1);
		loadStandardSprite(SpriteList.TURRET_BASE, "sprites/turret-base.png", 41, 20, 1, 1);
		if (TileInfo.hasLoaded()) {
			for (TileInfo tileInfo : TileInfo.getTileRegistry().values()) {
				loadTileSprite(tileInfo);
			}
		} else {
			throw new IllegalStateException("Tried to get tile sprites before they were loaded");
		}

		loadStandardSprite(SpriteList.PLACEHOLDER, "sprites/PLACEHOLDER.png", 15, 8, 1, 1);
		hasLoaded = true;
	}

	public static boolean hasLoaded() {
		return hasLoaded;
	}

	/**
	 * Loads a frame collection (texture/spritesheet) from disk and adds it to
	 * the master frame collection. Textures are a 2D grid of animated frames --
	 * if no animation is required, then supply the value 1 to the numFrames
	 * parameter.
	 * <p>
	 * Frames are loaded row-by-row within the spritesheet, e.g.: 1234 567 (for
	 * a total of 7 frames within a 4x2 frame spritesheet)
	 */
	private static SpriteInfo loadSpriteInfo(String path, int frameWidth, int frameHeight, int numFrames,
			int frameDuration) throws SpriteLoadException, IOException {

		// Load the image as a BufferedImage so we can use .getSubImage() after
		// to split it
		BufferedImage masterImage = SwingFXUtils.fromFXImage(new Image(path), null);

		// Check that the number of frames matches up with our params
		if (masterImage.getWidth() % frameWidth != 0) {
			throw new SpriteLoadException(
					"Image " + path + " could not be loaded; image width was not divisible by the frame width.");
		}
		if (masterImage.getHeight() % frameHeight != 0) {
			throw new SpriteLoadException(
					"Image " + path + " could not be loaded; image height was not divisible by the frame height.");
		}
		// Check if we asked for more frames than the texture has
		int totalFrameCount = (masterImage.getWidth() / frameWidth) * (masterImage.getHeight() / frameHeight);
		if (numFrames > totalFrameCount) {
			throw new SpriteLoadException("Image " + path
					+ " could not be loaded; requested frame count exceeded frames in the sprite sheet.");
		}
		int numFramesX = masterImage.getWidth() / frameWidth;
		int numFramesY = masterImage.getHeight() / frameHeight;

		Image[] frames = new Image[numFrames];

		// Split into frames
		boolean done = false;
		for (int row = 0; row < numFramesY; row++) {
			for (int column = 0; column < numFramesX; column++) {
				if (row * numFramesX + column >= numFrames) {
					done = true;
					break;
				}
				// Convert to JavaFX image
				BufferedImage frame = masterImage.getSubimage(column * frameWidth, row * frameHeight, frameWidth,
						frameHeight);
				WritableImage fxImage = SwingFXUtils.toFXImage(frame, null);
				frames[row * numFramesX + column] = fxImage;
			}

			if (done) {
				break;
			}
		}

		FrameCollection frameCollection = new FrameCollection(frames);
		return new SpriteInfo(frameWidth, frameHeight, numFrames, frameDuration, frameCollection);
	}

	private static void loadStandardSprite(SpriteList spriteID, String path, int frameWidth, int frameHeight,
			int numFrames, int frameDuration) throws SpriteLoadException, IOException {
		if (spritesCache.containsKey(spriteID)) {
			throw new SpriteLoadException("Attempted to load new standard sprite, but ID " + spriteID
					+ " already exists in the frame cache.");
		}
		spritesCache.put(spriteID, loadSpriteInfo(path, frameWidth, frameHeight, numFrames, frameDuration));
	}

	private static void loadTileSprite(TileInfo tileType) throws IOException {
		for (Map.Entry<String, String> variantSprite : tileType.getSpriteFilenames().entrySet()) {
			String variant = variantSprite.getKey();
			loadTileSprite(tileType, variant, variantSprite.getValue(), tileType.getBlockWidth(),
					tileType.getBlockHeight(), tileType.getNumSpriteFrames(), tileType.getSpriteFrameDuration());
		}
	}

	private static void loadTileSprite(TileInfo tileType, String variant, String path, int frameWidth,
			int frameHeight, int numFrames, int frameDuration) throws SpriteLoadException, IOException {
		Map<String, SpriteInfo> spriteVariants = tileSpritesCache.get(tileType);
		if (spriteVariants != null && spriteVariants.containsKey(variant)) {
			throw new SpriteLoadException("Attempted to load new tile sprite, but tile type "
					+ tileType.getDisplayName() + ": " + variant + " already exists in the frame cache.");
		}
		SpriteInfo si = loadSpriteInfo(path, frameWidth, frameHeight, numFrames, frameDuration);
		if (spriteVariants == null) {
			spriteVariants = new HashMap<>();
			tileSpritesCache.put(tileType, spriteVariants);
		}
		spriteVariants.put(variant, si);
	}

	/**
	 * Retrieves a SpriteInfo from the cache.
	 */
	public static SpriteInfo getSpriteInfo(SpriteList spriteId) throws SpriteLoadException {
		if (!spritesCache.containsKey(spriteId)) {
			throw new SpriteLoadException(
					"Attempted to retrieve standard sprite ID '" + spriteId + "', which does not exist.");
		}
		return spritesCache.get(spriteId);
	}

	/**
	 * Retrieves a SpriteInfo from the cache.
	 */
	public static SpriteInfo getSpriteInfo(TileInfo tileType, String variant) throws SpriteLoadException {
		Map<String, SpriteInfo> spriteVariants = tileSpritesCache.get(tileType);
		if (spriteVariants == null || !spriteVariants.containsKey(variant)) {
			throw new SpriteLoadException("Attempted to retrieve tile sprite '" + tileType.getDisplayName() + ": "
					+ variant + "', which does not exist.");
		}
		return spriteVariants.get(variant);
	}

	/**
	 * looks up default sprite into number, based on the SpriteInfo provided
	 *
	 * @param si
	 *            sprite info to find
	 * @return index/reference of the sprite info
	 */
	public static SpriteList lookupSprite(SpriteInfo si) {
		for (SpriteList k : spritesCache.keySet()) {
			if (si.equals(spritesCache.get(k))) {
				return k;
			}
		}
		return SpriteList.NULL;
	}

	/**
	 * Retrieves a set of all spriteIDs in the cache.
	 */
	public static Set<SpriteList> defaultSpriteKeys() {
		return spritesCache.keySet();
	}
}