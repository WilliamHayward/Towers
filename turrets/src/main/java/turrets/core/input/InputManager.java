package turrets.core.input;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import turrets.core.sound.SoundCache;
import turrets.graphics.Viewport;

/**
 * A manager that turns the event-based key callbacks of JavaFX into a static
 * map that you can retrieve and get KeyStates from anytime.
 */
public class InputManager implements EventHandler<InputEvent> {
	private static Logger logger = LoggerFactory.getLogger(InputManager.class);
	private static Map<KeyCode, Boolean> prevKeyStates = new HashMap<>();
	private static Map<KeyCode, Boolean> keyStates = new HashMap<>();
	private static final KeyCode[] secretCodeOrder = { KeyCode.UP, KeyCode.UP, KeyCode.DOWN, KeyCode.DOWN, KeyCode.LEFT,
			KeyCode.RIGHT, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.B, KeyCode.A, KeyCode.ENTER };
	private int secretCodeProgress = 0;

	private static Viewport viewport;
	private static double mouseX;
	private static double mouseY;
	
	private KeyCode mouseLeft = KeyCode.KANJI;

	static {
		//Prefill the keystate maps with false's
		clearKeyStates();
	}

	private static boolean swapNextActions = false;
	private static KeyCode swapKey1;
	private static boolean swapKey1Detected = false;
	private static boolean redirectNextKey = false;
	private static GameAction actionToMap;
	private static boolean queryNextKey = false;

	private static void updateMousePosition(InputEvent e) {
		mouseX = ((MouseEvent) e).getX();
		mouseY = ((MouseEvent) e).getY();
	}

	public static void updateKeyStates() {
		for (KeyCode key : keyStates.keySet()) {
			prevKeyStates.put(key , keyStates.get(key));
		}
	}

	public static void clearKeyStates() {
		for (KeyCode key: ControlsKeyMap.getAllKeyCodes()) {
			prevKeyStates.put(key, false);
			keyStates.put(key, false);

		}
	}

	public static boolean getActionState(GameAction action) {
		//TODO: Implenent second set of states for mouse, check against both 
		KeyCode key = ControlsKeyMap.getKeyCode(action);
		return keyStates.get(key);
	}

	public static boolean justPressed(GameAction action) {
		 
		KeyCode key = ControlsKeyMap.getKeyCode(action);
		return keyStates.get(key) && !prevKeyStates.get(key);
	}

	public static boolean justReleased(GameAction action) { 
		KeyCode key = ControlsKeyMap.getKeyCode(action);
		return prevKeyStates.get(key) && !keyStates.get(key);
	}

	public static void setViewport(Viewport port) {
		viewport = port;
	}

	/**
	 * Returns the difference in pixels of the x and mouseX
	 *
	 * @param x value to compare mouseX to
	 * @return the difference in pixels of x and mouseX
	 */
	public static double getDiffX(double x) {
		try {
			return mouseX - (viewport.getPixelCoordX((float) x));
		} catch (NullPointerException e) {
			logger.error("Mouse diffX error", e);
			return 0;
		}
	}

	public static double getDiffY(double y) {
		try {
			return mouseY - (viewport.getPixelCoordY((float) y));
		} catch (NullPointerException e) {
			logger.error("Mouse diffY error", e);
			return 0;
		}
	}

	/**
	 * Returns the mousex value corresponding to the left edge
	 * of the tile the mouse is over
	 */
	public static int getCurrentTilePixelX() {
		return viewport.getPixelCoordX((int) InputManager.getMouseTileX());
	}

	/**
	 * Returns the mousey value corresponding to the top edge
	 * of the tile the mouse is over
	 */
	public static int getCurrentTilePixelY() {
		return viewport.getPixelCoordY((int) InputManager.getMouseTileY());
	}
	
	/**
	 * Returns mousex value in either Pixel coordinates
	 *
	 * @return mouse X position
	 */
	public static double getMousePixelX() {
		return mouseX;
	}

	/**
	 * Returns mousey value in Pixel coordinates
	 *
	 * @return mouse y position
	 */
	public static double getMousePixelY() {
		return mouseY;
	}

	/**
	 * Returns mousex value in Tile coordinates
	 *
	 * @return mouse x position in tile coord
	 */
	public static double getMouseTileX() {
		try {
			return viewport.getTileCoordX((int) mouseX);
		} catch (NullPointerException e) {
			logger.error("Mouse tileX error", e);
			return 0;
		}
	}

	/**
	 * Returns mousey value in Tile coordinates
	 *
	 * @return mouse y position in tile coord
	 */
	public static double getMouseTileY() {
		try {
			return viewport.getTileCoordY((int) mouseY);
		} catch (NullPointerException e) {
			logger.error("Mouse tileY error", e);
			return 0;
		}
	}

	public static void clearAllValues() {
		for (KeyCode key : ControlsKeyMap.getAllKeyCodes()) {
			prevKeyStates.put(key, false);
			keyStates.put(key, false);
		}

	}

	public void handle(InputEvent e) {
		EventType<?> type = e.getEventType();
		if (type == KeyEvent.KEY_PRESSED) {
			KeyCode keyCode = ((KeyEvent) e).getCode();
			GameAction action = ControlsKeyMap.getGameAction(keyCode);
			if (action == null) {
				return;
			}
			reMap(keyCode);
			keyStates.put(keyCode, true);
		} else if (type == KeyEvent.KEY_RELEASED) {
			KeyCode keyCode = ((KeyEvent) e).getCode();
			keyStates.put(keyCode, false);
			processSecretCode(keyCode);
		} else if (type == MouseEvent.MOUSE_MOVED) {
			updateMousePosition(e);
		} else if (type == MouseEvent.MOUSE_DRAGGED || type == MouseEvent.MOUSE_PRESSED) {
			updateMousePosition(e);
			keyStates.put(mouseLeft, true);
		} else if (type == MouseEvent.MOUSE_RELEASED) {
			keyStates.put(mouseLeft, false);
		}
	}

	private static void reMap(KeyCode keyCode) {
		//************** CoastBusters - CPS ****************/
		// redirects next two key presses to swap button mappings
		KeyCode swapKey2;
		if (swapNextActions) {
			if (!swapKey1Detected) {
				swapKey1Detected = true;
				swapKey1 = keyCode;
			} else {
				swapKey2 = keyCode;
				swapNextActions = false;
				swapKey1Detected = false;
				ControlsKeyMap.swapKeys(swapKey1, swapKey2);
			}
			return;
		}
		// redirects next key press to remap specified action
		if (redirectNextKey) {
			redirectNextKey = false;
			ControlsKeyMap.addUniqueMapping(actionToMap, keyCode);
			return;
		}
		// redirects next key press to display action
		if (queryNextKey) {
			queryNextKey = false;
			return;
		}
	}

	private void processSecretCode(KeyCode code) {
		if (code == secretCodeOrder[secretCodeProgress]) {
			secretCodeProgress++;
			if (secretCodeProgress == secretCodeOrder.length) {
				SoundCache.pauseMidi();
				SoundCache.play("secretCode");
				SoundCache.resumeMidiDelayed(19000);
				secretCodeProgress = 0;
			}
		} else {
			secretCodeProgress = 0;
		}
	}

	// The following methods are the responsiblity of CoasterBuster's CPS

	/**
	 * Flags the Input Manager to swap the key bindings of the next two keys
	 * pressed. All other bindings for either action will be deleted.
	 */
	public static void flagToSwap() {
		swapNextActions = true;
		logger.info("Swapping keys");
	}

	/**
	 * Flags the specified action to be remapped. After the next key press all
	 * key binds for the action will be replaced with the key press.
	 *
	 * @param action The actions for which the key will be changed
	 */

	public static void flagToReMap(GameAction action) {
		redirectNextKey = true;
		actionToMap = action;
		logger.info("Remapping next key");
	}

	/**
	 * This method can be used to cancel flagtoReMap if key mapping is active.
	 */
	public static void breakFromReMap() {
		if (redirectNextKey) {
			redirectNextKey = false;
			logger.info("Cancelling remap");
		}
	}

	/**
	 * Flags the Input Manager to notify the user of the action of the next key
	 * pressed via toast.
	 */
	public static void queryKey() {
		queryNextKey = true;

	}

	/**
	 * Returns true if key swapping is active
	 *
	 * @return Returns true if key swapping is active
	 */
	public static boolean getSwappingState() {
		return swapNextActions;
	}

	/**
	 * Returns true if the first key has been entered during the key swapping
	 * process
	 *
	 * @return Returns true if the first key has been entered during the key
	 *         swapping process
	 */
	public static boolean getSwapKey1State() {
		return swapKey1Detected;
	}

	/**
	 * Returns true if key remapping is active
	 *
	 * @return Returns true if key remapping is active
	 */
	public static boolean getMappingState() {
		return redirectNextKey;
	}

	/**
	 * Returns true if key query is active
	 *
	 * @return Returns true if key query is active
	 */
	public static boolean getQueryState() {
		return queryNextKey;
	}

}
