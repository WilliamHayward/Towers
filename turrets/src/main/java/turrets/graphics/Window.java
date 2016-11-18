package turrets.graphics;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turrets.core.Engine;
import turrets.core.Settings;
import turrets.core.input.InputManager;
import turrets.core.sound.SoundCache;
import turrets.core.sound.SoundLoad;
import turrets.game.tiles.TileInfo;
import turrets.game.world.World;
import turrets.graphics.screens.*;
import turrets.graphics.screens.controllers.EditorScreenController;
import turrets.graphics.sprites.SpriteCache;

import javax.sound.midi.InvalidMidiDataException;
import java.io.IOException;


/**
 * The main class of the game, which sets up the JavaFX canvas, stage and window
 * and creates and runs our engine.
 */
public class Window extends Application {
	private static final Logger logger = LoggerFactory.getLogger(Window.class);
	
	private boolean editor = true;

	private Settings settings = new Settings();
	private Engine engine = new Engine();
	private static int resWidth;
	private static int resHeight;
	private boolean loaded;

	public Window() {
		resWidth = settings.getResWidth();
		resHeight = settings.getResHeight();
		// Put tile types in registry and load them into Tiles class.
		TileInfo.clearRegistry();
		TileInfo.registerTiles();
	}
	
	
	public void init(Stage stage) {
		
	}

	public void start(Stage stage) {
		// Load assets
		try {
			SpriteCache.loadAllSprites();
		} catch (IOException e) {
			logger.error("Error loading sprites", e);
			return;
		}

		try {
			SoundLoad.loadSound();
		} catch (IOException | InvalidMidiDataException e) {
			logger.error("Error loading sounds", e);
		}

		// Init game
		final Viewport viewport = new Viewport(resWidth / 2, resHeight);

		// Init JavaFX scene
		Canvas gameScreenCanvas = new Canvas(resWidth, resHeight);
		Canvas debugCanvas = new Canvas(resWidth, resHeight);

		Group root = new Group();

		Scene scene = new Scene(root, resWidth, resHeight, Color.DARKGRAY);
		gameScreenCanvas.requestFocus();

		// Init input
		InputManager inputManager = new InputManager();
		scene.setOnKeyPressed(inputManager);
		scene.setOnKeyReleased(inputManager);

		// MouseEvents
		scene.setOnMouseMoved(inputManager);
		scene.setOnMouseDragged(inputManager);
		scene.setOnMousePressed(inputManager);
		scene.setOnMouseReleased(inputManager);

		// Start
		stage.setTitle("Turrets");
		stage.setScene(scene);

		// Add Fonts
		Font.loadFont(getClass().getClassLoader().getResource("font/Pixeled.ttf").toExternalForm(), 10);

		Renderer renderer = new Renderer(stage, viewport, engine);

		engine.setGraphicsOutput(renderer);

		Screen gameScreen = new GameScreen(viewport, gameScreenCanvas);
		Screen debugScreen = new DebugScreen(viewport, engine, debugCanvas);
		
		EditorScreenController editor = new EditorScreenController(scene);
		
		// Add all screens to renderer
		renderer.addScreen("Game", gameScreen);
		renderer.addScreen("Debug", debugScreen);
		initGame();
		
		//renderer.enableScreen("Start Screen");

		// Start
		stage.show();
		addSizeListeners(stage, viewport, renderer);

		SoundCache.play("title");
		loaded = true;
	}

	/*
	 * Return whether the game has been loaded
	 */
	public boolean getLoaded() {
		return loaded;
	}

	/**
	 * Initiate the game
	 */
	public void initGame() {
		logger.debug("Game initiation started");

		Renderer r = engine.getRenderer();
		r.hideAllScreens();
		r.getScreen("Game").setVisible(true);
		
		System.out.println(editor);
		engine.initEngine(editor);
		// Set the original 'defaults'
		// This is repeated code, but will not be 'repeated' later
		SoundCache.play("game");
		engine.start();
	}
	
	/**
	 * Switch between the current screen and a specified screen
	 *
	 * @param currentScreenID StringID of the current Screen
	 * @param newScreenID     StringID of the desiredScreen
	 */
	public void goToScreen(String currentScreenID, String newScreenID) {
		Renderer r = engine.getRenderer();
		r.disableScreen(currentScreenID);
		r.enableScreen(newScreenID);

	}

	/**
	 * Switches between screens depending on which one is currently active
	 *
	 * @param screenID      screenID of selected Screen
	 * @param otherScreenID screenID of another selected Screen
	 */
	public void toggleScreens(String screenID, String otherScreenID) {
		if (engine.getRenderer().isActiveScreen(screenID)) {
			goToScreen(screenID, otherScreenID);
		} else {
			goToScreen(otherScreenID, screenID);
		}
	}

	/**
	 * A method that returns the engine, which is a singleton
	 *
	 * @return the engine
	 */
	public Engine getEngine() {
		return engine;
	}


	private static void addSizeListeners(Stage stage, Viewport viewport, Renderer renderer) {

		World world = World.getInstance();
		stage.widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

				resWidth = newValue.intValue();
				viewport.initViewport(resWidth, resHeight);
				renderer.setWidth(resWidth);

				viewport.calculateBorders(world.getTiles().getWidth(), world.getTiles().getHeight());


			}
		});
		stage.heightProperty().addListener(new ChangeListener<Number>() {

			public void changed(ObservableValue<? extends Number> observable, Number
					oldValue, Number newValue) {

				resHeight = newValue.intValue();
				viewport.initViewport(resWidth, resHeight);
				renderer.setHeight(resHeight);

				viewport.calculateBorders(world.getTiles().getWidth(), world.getTiles().getHeight());

			}
		});
	}

	public static int getResWidth() {
		return resWidth;
	}

	public static int getResHeight() {
		return resHeight;
	}

	public void begin() {
		launch();
	}
	
	public void begin(String mode) {
		switch (mode) {
		case "editor":
			this.editor = true;
			System.out.println("EDITOR YO");
			break;
		}
		launch();
		System.out.println("EDITOR YO");
	}

	public static void exit() {
		Platform.exit();
	}

	public void stop() {
		SoundCache.getInstance();
		SoundCache.closeMidi();
		SoundCache.getInstance().stopAll();
	}

}
