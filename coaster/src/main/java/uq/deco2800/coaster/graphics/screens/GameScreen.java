package uq.deco2800.coaster.graphics.screens;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.coaster.core.input.InputManager;
import uq.deco2800.coaster.game.entities.Entity;
import uq.deco2800.coaster.game.entities.Player;
import uq.deco2800.coaster.game.entities.npcs.BaseNPC;
import uq.deco2800.coaster.game.tiles.TileInfo;
import uq.deco2800.coaster.game.tiles.Tiles;
import uq.deco2800.coaster.game.world.*;
import uq.deco2800.coaster.graphics.Viewport;
import uq.deco2800.coaster.graphics.sprites.Sprite;
import uq.deco2800.coaster.graphics.sprites.SpriteList;

import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameScreen extends Screen {
	private Canvas canvas;
	private GraphicsContext gc;
	private Viewport viewport;
	private List<Tiles> lightSources = new ArrayList<>();

	private Map<Integer, Integer> shadows = new HashMap<>();

	private final Color LIGHTNING_OVERLAY = new Color(0, 0, 0, 0.5);
	private final Color LIGHTNING_COLOR = new Color(0.76, 0.92, 1, 1);
	Logger logger = LoggerFactory.getLogger(GameScreen.class);
	boolean drawF;
	Sprite fSprite;
	boolean drawQ;
	Sprite qSprite;
	boolean drawE;
	Sprite eSprite;
	boolean drawR;
	Sprite rSprite;

	int lastLeft = 0;
	int lastTop = 0;
	int lastRight = 0;
	int lastBottom = 0;
	float firstTileSize = 0;
	boolean destructionShadowUpdate = false;
	WritableImage shadowMap;


	public GameScreen(Viewport viewport, Canvas canvas) {
		super(canvas);
		this.viewport = viewport;
		this.setVisible(true);
		this.canvas = canvas;
		gc = canvas.getGraphicsContext2D();

		
		//create shadow colors
		for (int i = 0; i <= 100; i++) {
			shadows.put(i, new java.awt.Color(0, 0, 0, i / 100f).getRGB());
		}

	}

	@Override
	public void setWidth(int newWidth) {
		canvas.setWidth(newWidth);
	}

	@Override
	public void setHeight(int newHeight) {
		canvas.setHeight(newHeight);
	}


	public void render(long ms, boolean renderBackground) {
		World.getInstance();
		
		//Background
		renderBackground(ms);
		
		//Render tiles
		renderTerrain(World.getInstance().getTiles());
	
		//Render entities
		renderEntities(World.getInstance().getAllEntities(), ms);

		//Render minimap
		renderMap();
	}
	
	private void renderBackground(long ms) {
		gc.setFill(Color.GRAY);
		gc.fillRect(0, 0, viewport.getResWidth(), viewport.getResHeight());
	}
	
	private void renderTerrain(WorldTiles worldTiles) {

		float tileSize = viewport.getTileSideLength();
		int leftBorder = viewport.getLeftBorder();
		int topBorder = viewport.getTopBorder();

		int left = (int) Math.floor(viewport.getLeft());
		int top = (int) Math.floor(viewport.getTop());
		int right = (int) Math.ceil(viewport.getRight());
		int bottom = (int) Math.ceil(viewport.getBottom());

		float subTileShiftX = (viewport.getLeft() - left) * tileSize;
		float subTileShiftY = (viewport.getTop() - top) * tileSize;

		int extraFogSpace = 5;
		//fog calculations
		for (int x = left - extraFogSpace; x <= right + extraFogSpace; x++) {
			for (int y = top - extraFogSpace; y <= bottom + extraFogSpace; y++) {
				if (!worldTiles.test(x, y)) {
					// check for invalid lookups
					continue;
				}
				if (World.getInstance().getLightingState()) {
					calculateFog(x, y, worldTiles);
				}
			}
		}

		int padding = 1;
		if (firstTileSize == 0) {
			firstTileSize = tileSize;
		}
		if (World.getInstance().getLightingState()
				&& !(lastLeft == left && lastRight == right && lastBottom == bottom & lastTop == top)) {
			shadowMap = new WritableImage((int) firstTileSize * (right - left + 1 + 2 * padding),
					(int) firstTileSize * (bottom - top + 1 + 2 * padding));
			lastLeft = left;
			lastRight = right;
			lastBottom = bottom;
			lastTop = top;
			World.getInstance().setDestructionShadowUpdate(false);
		} else if (World.getInstance().getDestructionShadowUpdate()) {
			World.getInstance().setDestructionShadowUpdate(false);
		} else {
		}
		WritablePixelFormat.getIntArgbInstance();
		//display sprites and light level
		for (int x = left - padding; x <= right + padding; x++) {
			for (int y = top - padding; y <= bottom + padding; y++) {
				//We still want to iterate over "negative" tiles even if we don't render so we can center the map
				boolean invalidTile = false;
				if (!worldTiles.test(x, y)) {
					invalidTile = true;
				}

				if (!invalidTile) {
					Sprite sprite = worldTiles.get(x, y).getSprite();
					float xPos = (x - left) * tileSize + leftBorder;
					float yPos = (y - top) * tileSize + topBorder;
					gc.drawImage(sprite.getFrame(), xPos - subTileShiftX, yPos - subTileShiftY, tileSize, tileSize);
				}
				if (World.getInstance().getLightingState()) {

					int totalLightLevel;
					if (!invalidTile) {
						totalLightLevel = worldTiles.get(x, y).getFogAndLight();
						if (lightSources.contains(worldTiles.get(x, y).getTileType().getType())) {
							totalLightLevel = worldTiles.get(x, y).getLightLevel();
						}
					} else {
						totalLightLevel = World.getInstance().getGlobalLightLevel();

					}
					if (totalLightLevel == 0) {
						continue;
					}
				}
			}
		}
		if (World.getInstance().getLightingState()) {
			gc.drawImage(shadowMap, -subTileShiftX - tileSize, -subTileShiftY - tileSize,
					tileSize * (right - left + 1 + 2 * padding), tileSize * (bottom - top + 1 + 2 * padding));
		}
	}

	private void calculateFog(int x, int y, WorldTiles tiles) {
		for (int tileX = -1; tileX < 2; tileX++) {
			for (int tileY = -1; tileY < 2; tileY++) {
				boolean invalidTile = false;
				if (!tiles.test(x + tileX, y + tileY)) {
					// check for invalid lookups
					invalidTile = true;
				}

				if (invalidTile) {
					tiles.get(x, y).setBlockFog(13);
				} else if (tiles.get(x, y).getFogCheck() == 2) {
					continue;
				} else if (lightSources.contains(tiles.get(x + tileX, y + tileY).getTileType().getType())) {
					tiles.get(x, y).setBlockFog(0); //show block completely
					tiles.get(x, y).setFogCheck(2);
					break; //stop checking for smooth fog effect, since it will be completely visible anyway
				} else if (tiles.get(x + tileX, y + tileY).getFog() < tiles.get(x, y).getFog()) {
					//visibility scales based on "brightest" nearby block
					tiles.get(x, y).setBlockFog(tiles.get(x + tileX, y + tileY).getFog() + 13);
				}
			}
		}
		if (tiles.get(x, y).getFogCheck() == 0) {
			tiles.get(x, y).setFogCheck(1);
		}
	}

	private void renderEntities(List<Entity> entities, long ms) {
		float left = viewport.getLeft();
		float right = viewport.getRight();
		float top = viewport.getTop();
		float bottom = viewport.getBottom();
		Player player = null;
		for (Entity entity : entities) {
			if (entity instanceof Player) {
				player = (Player) entity;
			} else {
				if (entity.getX() + entity.getWidth() > left && entity.getX() < right
						&& entity.getY() + entity.getHeight() > top && entity.getY() < bottom) {
					entity.render(gc, viewport, ms);
				}
			}
		}
		if (player != null) { //Render player in front of all entities
			renderHud(player);
			player.render(gc, viewport, ms);
			
			if (player.getSkillSwap()) {
				player.setUpdateHud(true);
				player.setSpellKey(player.getSpellKey2());
				player.setDrawSKill(player.getDrawSkill2());
				;
				player.setSpellKey2("");
				player.setSkillSwap(false);
				player.setDrawSKill2(null);

			}
		}
	}

	private void renderMap() {
		if (!MiniMap.getVisibility()) {
			return;
		}

		WorldTiles tiles = World.getInstance().getTiles();

		float tileSize = 1;
		int leftBorder = viewport.getLeftBorder();
		int topBorder = viewport.getTopBorder();

		int top = (int) Math.floor(viewport.getTop() - (Chunk.CHUNK_HEIGHT / 4));
		int left = (int) Math.floor(viewport.getLeft()) - (Chunk.CHUNK_WIDTH / 2);
		int right = (int) Math.floor(viewport.getRight()) + (Chunk.CHUNK_WIDTH / 2);
		int bottom = (int) Math.floor(viewport.getBottom() + (Chunk.CHUNK_HEIGHT / 4));

		double alpha = gc.getGlobalAlpha();
		gc.setGlobalAlpha(0.5);
		for (int x = left; x <= right; x++) {
			for (int y = top; y <= bottom; y++) {
				// We still want to iterate over "negative" tiles even if we don't render so we can center the map
				if (!tiles.test(x, y) || !tiles.getVisited(x, y)) {
					gc.setFill(Color.BLACK);
					gc.fillRect(leftBorder + x - left + MiniMap.MAP_PADDING, topBorder + y - top + MiniMap.MAP_PADDING,
							tileSize, tileSize);
					continue;
				}

				Sprite sprite = tiles.get(x, y).getSprite();
				gc.drawImage(sprite.getFrame(), 0, 0, tileSize, tileSize, leftBorder + x - left + MiniMap.MAP_PADDING,
						topBorder + y - top + MiniMap.MAP_PADDING, tileSize, tileSize);

			}
		}

		List<Entity> mapEntities = MiniMap.getMapEntities(World.getInstance().getAllEntities());

		for (Entity entity : mapEntities) {
			renderMapEntity(entity);
		}
		gc.setGlobalAlpha(alpha);


	}

	private void renderMapEntity(Entity entity) {
		if (entity == null) {
			return;
		}

		float entitySize = 5; //square pixel size
		float tileSize = viewport.getTileSideLength();
		int leftBorder = viewport.getLeftBorder();
		int topBorder = viewport.getTopBorder();

		int mapTop = (int) Math.floor(viewport.getTop() - (Chunk.CHUNK_HEIGHT / 4));
		int mapLeft = (int) Math.floor(viewport.getLeft()) - (Chunk.CHUNK_WIDTH / 2);
		int mapRight = (int) Math.ceil(viewport.getRight()) + (Chunk.CHUNK_WIDTH / 2);
		int mapBottom = (int) Math.floor(viewport.getBottom() + (Chunk.CHUNK_HEIGHT / 4));
		int mapMidX = (mapRight - mapLeft) / 2;
		int mapMidY = (mapBottom - mapTop) / 2;

		int left = (int) Math.floor(viewport.getLeft());
		int right = (int) Math.floor(viewport.getRight());
		int top = (int) Math.floor(viewport.getTop());
		int bottom = (int) Math.floor(viewport.getBottom());
		int midX = (right - left) / 2;
		int midY = (bottom - top) / 2;

		float subTileShiftX = (viewport.getLeft() - left) * tileSize;
		WorldTiles tiles = World.getInstance().getTiles();

		if (entity.getX() < mapLeft || entity.getX() > mapRight || entity.getY() < mapTop || entity.getY() > mapBottom
				|| (tiles.test((int) entity.getX(), (int) entity.getY()))

				&& !tiles.getVisited((int) entity.getX(), (int) entity.getY())) {

			return;
		}

		float x = (entity.getX() - midX - left) * tileSize + leftBorder - subTileShiftX;
		float y = (entity.getY() - midY - top) * tileSize + topBorder - subTileShiftX;

		x /= tileSize;
		y /= tileSize;
		x += mapMidX + MiniMap.MAP_PADDING;
		y += mapMidY + MiniMap.MAP_PADDING;

		double alpha = gc.getGlobalAlpha();
		gc.setGlobalAlpha(0.5);

		if (entity instanceof Player) {
			gc.setFill(Color.BLUE);
		} else if (entity instanceof BaseNPC) {
			gc.setFill(Color.RED);
		}
		gc.fillRect(x, y, entitySize, entitySize);

		gc.setGlobalAlpha(alpha);

	}

	/**
	 * Method used to render the experience bar, health bar and mana bar over
	 * the top over the player hud. All three of these bars will be updated when
	 * their value changes; e.g. the health bar will get smaller when health has
	 * been lost.
	 * <p>
	 * Currently the bars don't stay over top of the player HUD when the screen
	 * is stretched but this is being worked on.
	 *
	 * @param entity The player entity is parsed into the method to allow us to
	 *               get their health, mana and experience so we can update the
	 *               bars when necessary.
	 */
	private void renderPlayerBars(Entity entity, double hudx, double hudy) {
		//Values to calculate how full each bar is
		float maxExperience = ((Player) entity).getPlayerLevel() * 20;
		long currentExperience = ((Player) entity).getExperiencePoints();
		float experiencePercent = currentExperience / maxExperience;
		float healthPercent = ((Player) entity).getCurrentHealth() / (float) ((Player) entity).getMaxHealth();
		float manaPercent = ((Player) entity).getCurrentMana() / (float) ((Player) entity).getMaxMana();
		// Experience bar
		gc.setFill(new Color(0.3, 0.3, 0.3, 1));
		gc.fillRect(hudx + 51.2, hudy + 130.32, 87.04, 15);

		// Purple bit
		gc.setFill(new Color(0.5, 0, 0.9, 1));
		gc.fillRect(hudx + 51.2, hudy + 130.32, 87.04 * experiencePercent, 15);

		// Text to show experience values
		gc.setFont(new Font(12));
		int intMaxExperience = ((Player) entity).getPlayerLevel() * 20;
		gc.setFill(new Color(1, 1, 1, 1));
		gc.fillText(currentExperience + "/" + intMaxExperience, hudx + 80.64, hudy + 142.64);
		gc.fillText("Level: " + ((Player) entity).getPlayerLevel(), hudx + 78.07999, hudy + 126.8);

		//  Health bar
		gc.setFill(new Color(0.3, 0.3, 0.3, 1));
		gc.fillRect(hudx + 144.64, hudy + 51.28, 321.28, 17);

		// Green bit
		gc.setFill(new Color(0, 1, 0, 1));
		gc.fillRect(hudx + 144.64, hudy + 51.28, 321.28 * healthPercent, 17);

		// Text to show health values
		gc.setFont(new Font(14));
		gc.setFill(new Color(0, 0, 0, 1));
		gc.fillText(((Player) entity).getCurrentHealth() + "/" + ((Player) entity).getMaxHealth(), hudx + 268.8,
				hudy + 65.28);

		//  Mana bar Empty bit
		gc.setFill(new Color(0.3, 0.3, 0.3, 1));
		gc.fillRect(hudx + 144.64, hudy + 71.28, 321.28, 17);

		// Mana bar blue bit
		gc.setFill(new Color(0, 0, 1, 1));
		gc.fillRect(hudx + 144.64, hudy + 71.28, 321.28 * manaPercent, 17);
		//Text
		gc.setFill(new Color(0, 0, 0, 1));
		gc.fillText(((Player) entity).getCurrentMana() + "/" + ((Player) entity).getMaxMana(), hudx + 268.8,
				hudy + 84.96);
	}
	/**
	 * Method used to render the player HUD at the bottom of the screen. The HUD
	 * is just an image drawn onto the screen which has spots for the currently
	 * unlocked spells to be drawn over the top. These spells also have their
	 * cooldown timer over the top of them.
	 * <p>
	 * The cooldown timer is currently not implemented into the spell logic
	 * because the cooldowns have not been decided yet.
	 *
	 * @param entity The player entity is parsed in so that we can get the
	 *               currently unlocked skills as well when see when they player
	 *               casts them.
	 */
	private void renderHud(Entity entity) {
		// Draw the HUD
		double hudx = viewport.getResWidth() * 0.35;
		double hudy = viewport.getResHeight() * 0.79;
		gc.drawImage(new Sprite(SpriteList.HUD).getFrame(), hudx, hudy, 831 / 1.4, 215 / 1.4);
		// Call the bars the be rendered on top of the HUD
		renderPlayerBars(entity, hudx, hudy);
		// Variables for spell sprites and cooldowns
		gc.setFont(new Font(18));
		gc.setFill(new Color(1, 0, 0, 1));
		new Sprite(SpriteList.DEATH_BLOSSOM);
		new Sprite(SpriteList.HIGH_NOON);
		new Sprite(SpriteList.SPLIT_SHOT);
		new Sprite(SpriteList.TIME_LOCK);
		double qCooldown = ((Player) entity).getCooldown(0);
		double eCooldown = ((Player) entity).getCooldown(1);
		double rCooldown = ((Player) entity).getCooldown(2);
		double wCooldown = ((Player) entity).getCooldown(3);
		DecimalFormat df = new DecimalFormat("#.##");
		// Draws each spell over the specificed spot on the HUD and places the cooldown over the top
		// Q
		if (((Player) entity).getUpdateHud() && ((Player) entity).getSpellKey().equals("0")) {
			qSprite = ((Player) entity).getDrawSkill();
			drawQ = true;
			((Player) entity).setUpdateHud(false);
			((Player) entity).setSpellKey("");
			((Player) entity).setDrawSKill(null);
		}
		if (drawQ && qSprite != null) {
			gc.drawImage(qSprite.getFrame(), hudx + 169, hudy + 99, 128 / 3.1, 128 / 3.1);
		}
		if (qCooldown > 0) {
			gc.fillText("" + df.format(qCooldown), hudx + 171.52, hudy + 127.44);
		}
		// E
		if (((Player) entity).getUpdateHud() && ((Player) entity).getSpellKey().equals("1")) {
			eSprite = ((Player) entity).getDrawSkill();
			drawE = true;
			((Player) entity).setUpdateHud(false);
			((Player) entity).setSpellKey("");
			((Player) entity).setDrawSKill(null);
		}
		if (drawE && eSprite != null) {
			gc.drawImage(eSprite.getFrame(), hudx + 245, hudy + 99, 128 / 3.1, 128 / 3.1);
		}
		if (eCooldown > 0) {
			gc.fillText("" + df.format(eCooldown), hudx + 249.6, hudy + 127.44);
		}
		//R
		if (((Player) entity).getUpdateHud() && ((Player) entity).getSpellKey().equals("2")) {
			rSprite = ((Player) entity).getDrawSkill();
			drawR = true;
			((Player) entity).setUpdateHud(false);
			((Player) entity).setSpellKey("");
			((Player) entity).setDrawSKill(null);
		}
		if (drawR && rSprite != null) {
			gc.drawImage(rSprite.getFrame(), hudx + 323, hudy + 98, 128 / 3.1, 128 / 3.1);
		}
		if (rCooldown > 0) {
			gc.fillText("" + df.format(rCooldown), hudx + 327.68, hudy + 127.44);
		}

		if (((Player) entity).getUpdateHud() && ((Player) entity).getSpellKey().equalsIgnoreCase("3")) {
			fSprite = ((Player) entity).getDrawSkill();
			drawF = true;
			((Player) entity).setUpdateHud(false);
			((Player) entity).setSpellKey("");
			((Player) entity).setDrawSKill(null);
		}
		if (drawF && fSprite != null) {
			gc.drawImage(fSprite.getFrame(), hudx + 402, hudy + 98.5, 128 / 3.1, 128 / 3.1);
		}
		if (wCooldown > 0) {
			gc.fillText("" + df.format(wCooldown), hudx + 405, hudy + 127.44);
		}

		for (int i = 0; i < 4; i++) {
			if (i == 0) {
				((Player) entity).setSkillSprites(qSprite, i);
			}
			if (i == 1) {
				((Player) entity).setSkillSprites(eSprite, i);
			}
			if (i == 2) {
				((Player) entity).setSkillSprites(rSprite, i);
			}
			if (i == 3) {
				((Player) entity).setSkillSprites(fSprite, i);
			}
		}
	}
}
