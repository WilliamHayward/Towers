package uq.deco2800.coaster.graphics.screens;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import uq.deco2800.coaster.game.modes.BuildMode;
import uq.deco2800.coaster.game.modes.BuildingList;
import uq.deco2800.coaster.game.world.World;
import uq.deco2800.coaster.graphics.Viewport;

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

		BuildingList currentBuilding = World.getInstance().getFirstPlayer().getActiveBuilding();
		gc.setFill(Color.BLACK);
		gc.fillText(BuildMode.getInstance().getBuildingName(currentBuilding), right - 5, bottom - 5);
		
		gc.setTextAlign(TextAlignment.RIGHT);
	}

}
