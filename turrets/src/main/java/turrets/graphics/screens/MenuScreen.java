package turrets.graphics.screens;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import turrets.graphics.Viewport;

public class MenuScreen extends FXMLScreen {
	private Canvas canvas;
	private GraphicsContext gc;
	private Viewport viewport;

	public MenuScreen(String fxml) {
		super(fxml);
		this.viewport = viewport;
		this.setVisible(true);
		this.canvas = canvas;
		//gc = canvas.getGraphicsContext2D();
	}

	@Override
	public void setWidth(int newWidth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHeight(int newHeight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(long ms, boolean renderBackGround) {
		gc.setFill(Color.AZURE);
		gc.fillRect(0, 0, viewport.getResWidth(), viewport.getResHeight());
		
	}

}
