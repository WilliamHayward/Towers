package turrets.core.input;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javafx.scene.input.KeyCode;

/**
 * A class used to hold the active 'keyboard key' to 'game action' mappings. To
 * define control schemes refer to the ControlSchemeManager class.
 */
public class ControlsKeyMap {

	private static Logger logger = LoggerFactory.getLogger(ControlsKeyMap.class);
	//the map of the keys to actions used by the game
	private static Map<GameAction, KeyCode> keymap = new HashMap<>();
	//the  controller used to handle button remapping for the controls menu

	private ControlsKeyMap() {

	}

	static {
		ControlSchemeManager.loadControlScheme(ControlScheme.DEFAULT, keymap);
	}

	/**
	 * Registers a KeyCode into the control mapping and maps it to an action.
	 *
	 * @param code The KeyCode to register
	 * @param action The action that is mapped to the given KeyCode
	 */
	public static void registerKey(KeyCode code, GameAction action) {
		keymap.put(action, code);
	}

	/**
	 * Returns the action corresponding to the given KeyCode
	 *
	 * @param code the Keycode to return the action for
	 * @return Returns the action corresponding to the given KeyCode.
	 */
	public static GameAction getGameAction(KeyCode code) {
		for (GameAction k : keymap.keySet()) {
			if (keymap.get(k) == code) {
				return k;
			}
		}
		return null;
	}

	/**
	 * Returns a single key code associated with the specified game action. If
	 * the active key map contains duplicate entries for a game action, the key
	 * returned may vary between calls.
	 *
	 * @param action The game action for which the key is desired to be known
	 * @return Returns one key associated with the specified game action. If no
	 *         key is found returns the UNDEFINED key.
	 */
	public static KeyCode getKeyCode(GameAction action) {
		return keymap.get(action);
	}
	
	/**
	 * Return a list of all the key codes mapped to GameActions
	 */
	public static List<KeyCode> getAllKeyCodes() {
		List <KeyCode> keyCodes = new ArrayList<>();
		for (KeyCode key: keymap.values()) {
			keyCodes.add(key);
		}
			
		return keyCodes;
	}


	/**
	 * Returns a string of the specified game action for the player to see.
	 *
	 * @param action The game action for which the string is desired
	 * @return Returns string of the specified game action. If no action is
	 *         found returns "MISSING";.
	 */
	public static String getStyledGameAction(GameAction action) {
		String styledAction;
		switch (action) {
			case MOVE_LEFT:
				styledAction = "move left.";
				break;
			case MOVE_RIGHT:
				styledAction = "move right.";
				break;
			case JUMP:
				styledAction = "jump.";
				break;
			case BASIC_ATTACK:
				styledAction = "attack with your newly activated weapon.\n Holding down the left mouse button also works.";
				break;
			case SHOW_MAP:
				styledAction = "show the minimap.\nDisable it with the same key.";
				break;
			default:
				styledAction = "UNDEFINED!";
		}
		return styledAction;
	}

	/**
	 * Deletes all button mappings for the specified action
	 *
	 * @param action The action to delete all button mappings for
	 */
	public static void deleteAllKeyCodesFor(GameAction action) {
		keymap.values().removeIf(action::equals);
	}

	/**
	 * Creates a button mapping for the action specified to the key specified.
	 * Deletes all other key mappings for the action. If the key specified is
	 * already associated with an action, that action will also be mapped to the
	 * original key of the action specified.
	 *
	 * @param actionOne The action to map
	 * @param keyTwo The key to map the button to
	 */
	public static void addUniqueMapping(GameAction actionOne, KeyCode keyTwo) {
		KeyCode aKeyOne = getKeyCode(actionOne);
		if (aKeyOne.equals(KeyCode.UNDEFINED)) {
			registerKey(KeyCode.UNDEFINED, actionOne);
		}
		swapKeys(keyTwo, aKeyOne);
	}

	/**
	 * Swaps the button mapping for the actions associated with each key
	 * specified. Deletes all other button mappings for both actions.
	 *
	 * @param key1 The key associated with the first action
	 * @param key2 The key associated with the second action
	 */
	public static void swapKeys(KeyCode key1, KeyCode key2) {
		if (key1.equals(KeyCode.ESCAPE) || key2.equals(KeyCode.ESCAPE)) {

		} else if (key1.equals(key2)) {

		} else if (keymap.containsKey(key1) && keymap.containsKey(key2)) {

			GameAction action1 = getGameAction(key1);
			GameAction action2 = getGameAction(key2);

			deleteAllKeyCodesFor(action1);
			deleteAllKeyCodesFor(action2);

			registerKey(key1, action2);
			registerKey(key2, action1);


		} else if (!keymap.containsKey(key1) && keymap.containsKey(key2)) {

			GameAction action2 = getGameAction(key2);

			deleteAllKeyCodesFor(action2);
			registerKey(key1, action2);


		} else if (keymap.containsKey(key1) && !keymap.containsKey(key2)) {

			GameAction action1 = getGameAction(key1);

			deleteAllKeyCodesFor(action1);
			registerKey(key2, action1);

		} else {


		}
	}

	/**
	 * Changes the current control mapping used by the game to that specified
	 *
	 * @param controlScheme The control scheme to switch to
	 */
	public static void importControlScheme(ControlScheme controlScheme) {
		clearAllMappings();
		ControlSchemeManager.loadControlScheme(controlScheme, keymap);
		logger.info("Loaded control scheme " + controlScheme.toString());
	}

	/**
	 * Saves the current button mappings to the file specified.
	 *
	 * @param file The file to save the current mapping to
	 */
	public static void saveMapping(File file) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String jsonText;
		try {
			jsonText = ow.writeValueAsString(keymap);
			FileWriter fw = new FileWriter(file);
			fw.write(jsonText);
			fw.close();
			logger.info("Saved control mapping");
		} catch (IOException exception) {
			logger.error("Save mapping failed", exception);
		}
	}

	/**
	 * Loads a button mapping from the specified file.
	 *
	 * @param file The file to load the button mapping from
	 */
	public static void loadMapping(File file) {
		try {
			keymap = (new ObjectMapper()).readValue(file, new TypeReference<Map<KeyCode, GameAction>>() {
			});
			logger.info("Loaded control mapping");
		} catch (IOException exception) {
			logger.error("Load mapping failed", exception);
			return;
		}
	}

	/**
	 * Clears all button mappings
	 */
	public static void clearAllMappings() {
		keymap.clear();
	}

	/**
	 * Checks if the button mapping contains the key and action pair
	 *
	 * @param code the key of the action
	 * @param action the action to check
	 * @return returns true iff the action and key are currently mapped together
	 */
	public static boolean mappingContains(KeyCode code, GameAction action) {
		return getGameAction(code) == action;
	}
}
