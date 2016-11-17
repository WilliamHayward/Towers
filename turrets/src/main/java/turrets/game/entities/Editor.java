package turrets.game.entities;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import turrets.core.input.GameAction;
import turrets.core.input.InputManager;
import turrets.game.mechanics.BodyPart;
import turrets.game.mechanics.Side;
import turrets.game.tiles.Tile;
import turrets.game.tiles.TileInfo;
import turrets.game.tiles.Tiles;
import turrets.game.world.Room;
import turrets.game.world.World;
import turrets.graphics.Viewport;
import turrets.graphics.sprites.Sprite;
import turrets.graphics.sprites.SpriteList;

public class Editor extends Entity {
	
	Tile currentTile = new Tile(Tiles.DIRT);
	
	boolean validPosition = false;

	public Editor() {
		bounds = new AABB(posX, posY, 1, 1);
		this.setSize(1, 1);
		this.toggleGravity();
		this.setSprite(new Sprite(SpriteList.PLACEHOLDER));
	}

	@Override
	protected void tick(long ms) {
		int speed = InputManager.getActionState(GameAction.CAMERA_FAST) ? 30 : 10;
		int moveLeft = InputManager.getActionState(GameAction.CAMERA_LEFT) ? 1 : 0;
		int moveRight = InputManager.getActionState(GameAction.CAMERA_RIGHT) ? 1 : 0;
		int moveUp = InputManager.getActionState(GameAction.CAMERA_UP) ? 1 : 0;
		int moveDown = InputManager.getActionState(GameAction.CAMERA_DOWN) ? 1 : 0;
		int moveHorizontal = moveRight - moveLeft;
		int moveVertical = moveDown - moveUp;
		this.setVelocity(moveHorizontal * speed, moveVertical * speed);
		double xTile = InputManager.getMouseTileX();
		double yTile = InputManager.getMouseTileY();
		if (xTile < 0 || xTile > Room.WIDTH || yTile < 0 || yTile > Room.HEIGHT) {
			validPosition = false;
		} else {
			validPosition = true;
		}
		
		if (validPosition && InputManager.getActionState(GameAction.BASIC_ATTACK)) {
			World.getInstance().getTiles().set((int) xTile, (int) yTile, currentTile.getTileType());
		}

	}

	@Override
	protected void onEntityCollide(List<Entity> entities, List<BodyPart> hitLocations) {
	}

	@Override
	protected void onTerrainCollide(int tileX, int tileY, Side side) {
	}

	@Override
	protected void onDeath(Entity cause) {
	}
	
	@Override
	public void render(GraphicsContext gc, Viewport viewport, long ms) {
		Image cursor;

		double mouseX;
		double mouseY;
		if (validPosition) {
			cursor = currentTile.getSprite().getFrame();
			mouseX = InputManager.getCurrentTilePixelX();
			mouseY = InputManager.getCurrentTilePixelY();
		} else {
			cursor = new Sprite(SpriteList.PLACEHOLDER).getFrame();
			mouseX = InputManager.getMousePixelX();
			mouseY = InputManager.getMousePixelY();
		}
		
		gc.drawImage(cursor, mouseX, mouseY);
	}

}
