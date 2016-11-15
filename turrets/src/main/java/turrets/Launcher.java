package turrets;

import turrets.graphics.Window;

public class Launcher {
	private static Window window;


	private Launcher() {
	}

	/**
	 * Call this method to launch into play
	 */
	public static void main(String[] args) {
		window = new Window();
		window.begin();
	}
}
