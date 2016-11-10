package uq.deco2800.coaster.graphics.screens;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.coaster.game.entities.Entity;
import uq.deco2800.coaster.game.entities.Player;
import uq.deco2800.coaster.game.world.*;
import uq.deco2800.coaster.graphics.Viewport;
import uq.deco2800.coaster.graphics.sprites.Sprite;
import java.util.List;


public class GameScreen extends Screen {
	private Canvas canvas;
	private GraphicsContext gc;
	private Viewport viewport;
	Logger logger = LoggerFactory.getLogger(GameScreen.class);
	
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

		float subTileShiftX = (viewport.getLeft() - left) * tileSize;
		float subTileShiftY = (viewport.getTop() - top) * tileSize;
		if (firstTileSize == 0) {
			firstTileSize = tileSize;
		}

		//display sprites and light level
		for (int x = 0; x <= Chunk.CHUNK_WIDTH; x++) {
			for (int y = 0; y <= Chunk.CHUNK_HEIGHT; y++) {
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
			}
		}
	}

	private void renderEntities(List<Entity> entities, long ms) {
		float widthLeeway = viewport.getWidth() / 2; // Leeway to make sure that no sprites are cut off
		float heightLeeway = viewport.getHeight() / 2; // Leeway to make sure that no sprites are cut off
		float left = viewport.getLeft() - widthLeeway;
		float right = viewport.getRight() + widthLeeway;
		float top = viewport.getTop() - heightLeeway;
		float bottom = viewport.getBottom() + heightLeeway;
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
			player.render(gc, viewport, ms);
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
		}
		gc.fillRect(x, y, entitySize, entitySize);

		gc.setGlobalAlpha(alpha);

	}
}
