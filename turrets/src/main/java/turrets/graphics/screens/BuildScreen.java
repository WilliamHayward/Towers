package turrets.graphics.screens;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import turrets.game.entities.buildings.Building;
import turrets.game.modes.BuildMode;
import turrets.game.modes.BuildingList;
import turrets.game.world.World;
import turrets.graphics.Viewport;

public class BuildScreen extends Screen {
	private Canvas canvas;
	private GraphicsContext gc;
	private Viewport viewport;

	public BuildScreen(Node root) {
		super(root);
		// TODO Auto-generated constructor stub
	}

	public BuildScreen(Viewport viewport, Canvas canvas) {
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


	@Override
	public void render(long ms, boolean renderBackGround) {
		gc.setTextAlign(TextAlignment.RIGHT);
		int right = viewport.getResWidth();
		int bottom = viewport.getResHeight();

		Building currentBuilding = World.getInstance().getFirstPlayer().getActiveBuilding();
		gc.setFill(Color.BLACK);
		gc.fillText(currentBuilding.getName(), right - 5, bottom - 5);
		
		gc.setTextAlign(TextAlignment.RIGHT);
	}

}
