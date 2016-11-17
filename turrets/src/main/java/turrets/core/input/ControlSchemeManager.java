package turrets.core.input;

import java.util.Map;

import javafx.scene.input.KeyCode;

/**
 * This class is used to define and manage different control schemes.
 */
public class ControlSchemeManager {

	private ControlSchemeManager() {

	}

	/**
	 * Adds the key mappings for the specified control scheme to the specified
	 * KeyCode/GameAction map.
	 *
	 * @param controlScheme The control scheme to use
	 * @param keymap The KeyCode/GameAction map to store the key mappings in
	 */
	public static void loadControlScheme(ControlScheme controlScheme, Map<GameAction, KeyCode> keymap) {
		// KobiMcKerihan (kobimac)
		keymap.put(GameAction.BASIC_ATTACK, KeyCode.KANJI);
		keymap.put(GameAction.CROUCH, KeyCode.S);
		
		// WilliamHayward
		keymap.put(GameAction.SHOW_DEBUG, KeyCode.J);
		keymap.put(GameAction.SHOW_HITBOXES, KeyCode.H);
		keymap.put(GameAction.DELETE_TILE, KeyCode.BACK_SPACE);
		keymap.put(GameAction.ADD_TILE, KeyCode.ENTER);

		keymap.put(GameAction.DEBUG_CONSOLE, KeyCode.BACK_SLASH);

		// Hayley, switching weapons (subject to change with inventory
		// updates)
		keymap.put(GameAction.SLOT_ONE, KeyCode.DIGIT1);
		keymap.put(GameAction.SLOT_TWO, KeyCode.DIGIT2);
		keymap.put(GameAction.SLOT_THREE, KeyCode.DIGIT3);
		keymap.put(GameAction.SLOT_FOUR, KeyCode.DIGIT4);
		
		keymap.put(GameAction.SHOW_MAP, KeyCode.L);

		// Daniel, Sound Controls
		keymap.put(GameAction.MUTE, KeyCode.M);
		keymap.put(GameAction.VOLUME_DOWN, KeyCode.COMMA);
		keymap.put(GameAction.VOLUME_UP, KeyCode.PERIOD);

		// KobiMcKerihan (kobimac)
		keymap.put(GameAction.MOVE_LEFT, KeyCode.A);
		keymap.put(GameAction.MOVE_RIGHT, KeyCode.D);
		keymap.put(GameAction.JUMP, KeyCode.SPACE);
		
		// Camera controls
		keymap.put(GameAction.CAMERA_UP, KeyCode.W);
		keymap.put(GameAction.CAMERA_DOWN, KeyCode.S);
		keymap.put(GameAction.CAMERA_LEFT, KeyCode.A);
		keymap.put(GameAction.CAMERA_RIGHT, KeyCode.D);
		keymap.put(GameAction.CAMERA_FAST, KeyCode.SHIFT);

	}
}
