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
	public static void loadControlScheme(ControlScheme controlScheme, Map<KeyCode, GameAction> keymap) {
		// KobiMcKerihan (kobimac)
		keymap.put(KeyCode.Z, GameAction.BASIC_ATTACK);
		keymap.put(KeyCode.X, GameAction.SPECIAL_ATTACK);
		keymap.put(KeyCode.S, GameAction.CROUCH);

		// WilliamHayward
		keymap.put(KeyCode.J, GameAction.SHOW_DEBUG);
		keymap.put(KeyCode.H, GameAction.SHOW_HITBOXES);
		keymap.put(KeyCode.BACK_SPACE, GameAction.DELETE_TILE);
		keymap.put(KeyCode.ENTER, GameAction.ADD_TILE);

		keymap.put(KeyCode.BACK_SLASH, GameAction.DEBUG_CONSOLE);

		// Hayley, switching weapons (subject to change with inventory
		// updates)
		keymap.put(KeyCode.DIGIT1, GameAction.SLOT_ONE);
		keymap.put(KeyCode.DIGIT2, GameAction.SLOT_TWO);
		keymap.put(KeyCode.DIGIT3, GameAction.SLOT_THREE);
		keymap.put(KeyCode.DIGIT4, GameAction.SLOT_FOUR);
		
		keymap.put(KeyCode.L, GameAction.SHOW_MAP);

		// Daniel, Sound Controls
		keymap.put(KeyCode.M, GameAction.MUTE);
		keymap.put(KeyCode.COMMA, GameAction.VOLUME_DOWN);
		keymap.put(KeyCode.PERIOD, GameAction.VOLUME_UP);

		// KobiMcKerihan (kobimac)
		keymap.put(KeyCode.A, GameAction.MOVE_LEFT);
		keymap.put(KeyCode.D, GameAction.MOVE_RIGHT);
		keymap.put(KeyCode.SPACE, GameAction.JUMP);

	}
}
