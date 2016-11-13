package uq.deco2800.coaster.graphics;

/**
 * A list of all the layers in the game (for rendering entities, mainly)
 * The ordering is important - the first item on the list will be rendered below the second,
 * and the second will be rendered below the third, etc.
 * Background and tiles will always render behind, GUI will always render in front.
 * @author WilliamHayward
 */
public enum LayerList {
	DEFAULT,
	TRAPS,
	ENEMIES,
	TURRETS,
	PLAYERS
}
