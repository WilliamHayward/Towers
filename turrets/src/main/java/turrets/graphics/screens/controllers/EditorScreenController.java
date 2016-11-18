package turrets.graphics.screens.controllers;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class EditorScreenController {

	public EditorScreenController(Scene scene) {
		Pane editorHUD = new Pane();
		Button test = new Button();
		test.setText("HELLO");
		test.setLayoutX(5);
		test.setLayoutY(5);
		editorHUD.getChildren().add(test);
		
	}

}
