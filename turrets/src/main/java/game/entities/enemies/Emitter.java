package game.entities.enemies;

import java.util.List;
import java.util.Map;

import game.entities.AABB;
import game.entities.Entity;
import game.mechanics.BodyPart;
import game.mechanics.Side;
import game.world.Coordinate;
import game.world.World;
import graphics.sprites.Sprite;
import graphics.sprites.SpriteList;

public class Emitter extends Entity {
	private int children = 0;
	private Map<Integer, Coordinate> waypoints;
	
	public Emitter(Map<Integer, Coordinate> waypoints) {
		super();

		this.setSprite(new Sprite(SpriteList.PLACEHOLDER));
		setBlocksOtherEntities(false);

		bounds = new AABB(posX, posY, sprite.getWidth() / 32, sprite.getHeight() / 32); // No collision for decoration

		setCollisionFilter(e -> false);
		this.setSize(1f, 1f);
		this.waypoints = waypoints;
	}
	@Override
	protected void tick(long ms) {
		// TODO Auto-generated method stub
		if (children < 1) {
			Enemy child = new Creep();
			child.setWaypoints(waypoints);
			child.setPosition(this.getX(), this.getY());
			World.getInstance().addEntity(child);
			children++;
		}
	}
	@Override
	protected void onEntityCollide(List<Entity> entities, List<BodyPart> hitLocations) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onTerrainCollide(int tileX, int tileY, Side side) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onDeath(Entity cause) {
		// TODO Auto-generated method stub
		
	}
}
