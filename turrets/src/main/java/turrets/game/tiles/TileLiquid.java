package turrets.game.tiles;

public class TileLiquid extends TileInfo {
	public TileLiquid(Tiles type) {
		super(type);
	}

	@Override
	public boolean isObstacle() {
		return false;
	}

	@Override
	public boolean isLiquid() {
		return true;
	}
}
