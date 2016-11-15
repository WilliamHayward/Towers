package turrets.game.entities.players;

import turrets.game.entities.EntityState;
import turrets.graphics.sprites.Sprite;
import turrets.graphics.sprites.SpriteList;

public class BluePlayer extends Player {

	public BluePlayer() {
		sprites.put(EntityState.STANDING, new Sprite(SpriteList.PLAYER_BLUE_STANDING));
		sprites.put(EntityState.JUMPING, new Sprite(SpriteList.PLAYER_BLUE_JUMPING));
		sprites.put(EntityState.MOVING, new Sprite(SpriteList.PLAYER_BLUE_WALKING));
		sprites.put(EntityState.CROUCHING, new Sprite(SpriteList.PLAYER_BLUE_CROUCH));
	}

}
