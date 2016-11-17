package turrets.core.input;

//A list of possible controls/game actions.
public enum GameAction {
	UNDEFINED,
	
	MOVE_LEFT,
	MOVE_RIGHT,
	JUMP,
	BASIC_ATTACK,
	CROUCH,

	//WilliamHayward
	SHOW_DEBUG,
	SHOW_HITBOXES,
	DELETE_TILE,
	ADD_TILE,
	SHOW_MAP,
	
	SLOT_ONE,
	SLOT_TWO,
	SLOT_THREE,
	SLOT_FOUR,

	// Debug Console
	DEBUG_CONSOLE,

	//Sound
	MUTE,
	VOLUME_UP,
	VOLUME_DOWN,
	
	//Freeroaming camera
	CAMERA_LEFT,
	CAMERA_RIGHT,
	CAMERA_UP,
	CAMERA_DOWN,
	CAMERA_FAST,
}
