package turrets.game.entities;

import java.util.List;
import turrets.game.mechanics.BodyPart;
import turrets.game.mechanics.Side;
import turrets.graphics.sprites.Sprite;
import turrets.graphics.sprites.SpriteList;

/**
 * Created by adam on 10/23/16.
 */
public class GenericExplosion extends Entity{
	private float x;
	private float y;
	private int radius;
	private float currentRadius;

	/**
	 * Generates an explosion at a given position
	 * @param x
	 * @param y
	 * @param radius
	 */
	public GenericExplosion(float x, float y, int radius){
		this.x = x;
		this.y = y;
		bounds = new AABB(x, y, 1, 1);
		setPosition(x,y);
		this.radius = radius;
		this.currentRadius = 1;

		this.setSprite(new Sprite(SpriteList.PLACEHOLDER));
	}

	/**
	 * Logic for each tick of the game. Expands the explosion
	 * @param ms millisecond tick the entity is being handled on
	 */
	public void tick(long ms){
		if(currentRadius < radius){
			this.bounds.setAABB(0,0,currentRadius*2,currentRadius*2);
			setPosition(x-currentRadius,y-currentRadius);
			currentRadius++;
		} else {
			this.delete();
		}
	};
	public void onDeath(Entity cause){}
	public void onEntityCollide(List<Entity> entities, List<BodyPart> hitLocations){};
	public void onTerrainCollide(int tileX, int tileY, Side side){};
}
