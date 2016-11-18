package turrets.graphics.screens;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import turrets.game.entities.Editor;
import turrets.game.entities.Entity;
import turrets.game.modes.GameModes;
import turrets.game.tiles.Tile;
import turrets.game.world.*;
import turrets.graphics.LayerList;
import turrets.graphics.Viewport;
import turrets.graphics.sprites.Sprite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
		//Background
		renderBackground(ms);
		
		//Render tiles
		renderTerrain(World.getInstance().getTiles());
	
		//Render entities
		renderEntities(World.getInstance().getAllEntities(), ms);
		
		//Render HUD
		renderHUD(World.getInstance().getGameMode());
	}
	
	private void renderHUD(GameModes mode) {
		int bottom = viewport.getResHeight();
		int right = viewport.getResWidth();
		
		switch (mode) {
			case EDITOR:
				gc.setFill(Color.WHITE);
				gc.fillRect(0, bottom - 100, right, 100);
				gc.setFill(Color.BLACK);
				gc.fillRect(0, bottom - 102, right, 2);
				Button test = new Button("Test");
				//test.setGraphic(value);
				Editor editor = World.getInstance().getEditor();
				if (editor == null) {
					return;
				}
				Tile currentTile = editor.getCurrentTile();
				Sprite sprite = currentTile.getSprite();
				gc.drawImage(sprite.getFrame(), right - sprite.getWidth() - 10, bottom - sprite.getHeight() - 50);
				break;
			default:
				break;
		}
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
		for (int x = 0; x <= Room.WIDTH; x++) {
			for (int y = 0; y <= Room.HEIGHT; y++) {
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
		
		Map<LayerList, List<Entity>> layers = new HashMap<>();
		for (LayerList layer: LayerList.values()) {
			layers.put(layer, new ArrayList<>());
		}
		
		for (Entity entity : entities) {
			if (entity.getX() + entity.getWidth() > left && entity.getX() < right
					&& entity.getY() + entity.getHeight() > top && entity.getY() < bottom) {
				layers.get(entity.getLayer()).add(entity);
			}
		}
		
		for (LayerList layer: LayerList.values()) {
			for (Entity entity: layers.get(layer)) {
				entity.render(gc, viewport, ms);
			}
		}
	}
}
