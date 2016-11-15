package graphics;

import game.entities.Entity;

public class Camera {
	private Entity leader = null;
	private Viewport viewport;
	public Camera(Viewport viewport, float x, float y) {
		this.viewport = viewport;
		viewport.centerOnX(x);
		viewport.centerOnX(y);
	}
	
	public Camera(Viewport viewport, Entity leader) {
		this.viewport = viewport;
		this.leader = leader;
	}
	
	public void setFollow(Entity leader) {
		this.leader = leader;
	}
	
	public void removeFollow() {
		this.leader = null;
	}
	
	public boolean isFollowing() {
		return (this.leader != null);
	}
	
	public boolean isFollowing(Entity leader) {
		return (this.leader == leader);
	}
	

	public void tick(long ms) {
		if (leader != null) {
			viewport.centerOn(leader.getX() + leader.getWidth() / 2, leader.getY() + leader.getHeight() / 2);
		}
	}
}
