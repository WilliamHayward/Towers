package turrets.core.input;

//Cieran Kent
public class GameInput {
	private boolean leftPressed;
	private boolean rightPressed;
	private boolean jumpPressed;
	private boolean upPressed;
	private boolean downPressed;
	private boolean dashPressed;
	private boolean slidePressed;
	private int inputDir;

	public GameInput() {
		this.leftPressed = false;
		this.rightPressed = false;
		this.jumpPressed = false;
		this.upPressed = false;
		this.downPressed = false;
		this.dashPressed = false;
		this.slidePressed = false;
		this.inputDir = 0;

	}

	public void updateGameInput() {
		leftPressed = InputManager.getActionState(GameAction.MOVE_LEFT);
		rightPressed = InputManager.getActionState(GameAction.MOVE_RIGHT);
		jumpPressed = InputManager.getActionState(GameAction.JUMP);
		downPressed = InputManager.getActionState(GameAction.CROUCH);
		inputDir = inputDirection(leftPressed, rightPressed);

		//Preparing for Button remapping2
	}

	public boolean getLeftPressed() {
		return leftPressed;
	}

	public boolean getRightPressed() {
		return rightPressed;
	}

	public boolean getJumpPressed() {
		return jumpPressed;
	}

	public boolean getUpPressed() {
		return upPressed;
	}

	public boolean getDownPressed() {
		return downPressed;
	}

	public boolean getDashPressed() {
		return dashPressed;
	}

	public boolean getSlidePressed() {
		return slidePressed;
	}

	public int getInputDirection() {
		return inputDir;
	}

	/**
	 * Returns the direction of the user's input, using the same convention as onWall and facing.
	 *
	 * @return -1 if left && !right, 0 if left && right, 1 if !left && right
	 */
	private int inputDirection(boolean left, boolean right) {
		if (left == right) {
			return 0;
		} else if (left) {
			return -1;
		}
		//Logically right must be true
		return 1;
	}

}
