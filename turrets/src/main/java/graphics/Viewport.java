package graphics;

import game.tiles.TileInfo;

//This is the class that controls the "camera" of the game. You can move the camera, position it onto a certain tile,
//and much more.
public class Viewport {
	//These units are in tiles
	//The viewport render will "snap" to tiles if you try to pan in a map smaller than the screen res, but that's fine
	//(you shouldn't be able to pan in those maps anyway)
	private float top;
	private float left;
	//These are in pixels
	private int resWidth;
	private int resHeight;
	private int borderLeft;
	private int borderTop;

	private float defaultTileSideLength;
	private float tileSideLength;

	public Viewport(int resWidth, int resHeight) {
		initViewport(resWidth, resHeight);
	}

	public void initViewport(int resWidth, int resHeight) {
		this.resWidth = resWidth;
		this.resHeight = resHeight;
		defaultTileSideLength = TileInfo.BLOCK_HEIGHT;
		tileSideLength = defaultTileSideLength;
	}

	public void calculateBorders(int mapWidth, int mapHeight) {
		float minWidth = Math.min((float) mapWidth, getWidth());
		float minHeight = Math.min((float) mapHeight, getHeight());

		borderLeft = (int) ((resWidth - (minWidth * tileSideLength)) / 2);
		borderTop = (int) ((resHeight - (minHeight * tileSideLength)) / 2);
		if (borderLeft < 0) {
			borderLeft = 0;
		}
		if (borderTop < 0) {
			borderTop = 0;
		}
	}
	
	public void zoomIn() {
		tileSideLength *= 2;
	}
	public void zoomOut() {
		tileSideLength /= 2;
	}
	
	public void zoom(float scale) {
		tileSideLength = defaultTileSideLength * scale;
	}
	
	//Takes a tile coordinate input and returns the pixel coordinate.
	public int getPixelCoordX(float x) {
		float pixel = (x - left) * tileSideLength;
		return (int) pixel;
	}

	public int getPixelCoordY(float y) {
		float pixel = (y - top) * tileSideLength;
		return (int) pixel;
	}

	//Takes a pixel coordinate and converts it to tile coordinates.
	public float getTileCoordX(int x) {
		int adjustedX = (int) (x + left * tileSideLength); //- borderLeft;
		return (adjustedX / tileSideLength);
	}

	public float getTileCoordY(int y) {
		int adjustedY = (int) (y + top * tileSideLength); //- borderLeft;
		return (adjustedY / tileSideLength);
		
		//int adjustedY = y - borderTop;
		//return (adjustedY / tileSideLength) + top;
	}

	/**
	 * Moves the viewport the specified number of tiles.
	 */
	public void move(float dx, float dy) {
		top += dy;
		left += dx;
	}

	/*
	 * Returns the pixel length of a tile's side.
	 */
	public float getTileSideLength() {
		return tileSideLength;
	}

	/**
	 * Centers the viewport on the specified tile coordinate.
	 */
	public void centerOn(float x, float y) {
		centerOnX(x);
		centerOnY(y);
	}

	public void centerOnX(float x) {
		left = x - (getWidth() / 2.0f);
		/*if (left < 0) {
			left = 0;
		}*/
		/*if (left + VIEWPORT_WIDTH > Room.WIDTH) {
			left = Room.WIDTH - VIEWPORT_WIDTH;
		}*/
	}

	public void centerOnY(float y) {
		top = y - (getHeight() / 2.0f);

		/*if (top + VIEWPORT_HEIGHT > Room.HEIGHT) {
			top = Room.HEIGHT - VIEWPORT_HEIGHT - 1;
		}*/
	}

	public float getTop() {
		return top;
	}

	public float getLeft() {
		return left;
	}

	public float getRight() {
		return left +  getWidth();
	}

	public float getBottom() {
		return top + getHeight();
	}

	public float getWidth() {
		return resWidth / tileSideLength;
	}

	public float getHeight() {
		return resHeight / tileSideLength;
	}

	public int getLeftBorder() {
		return borderLeft;
	}

	public int getTopBorder() {
		return borderTop;
	}

	public int getResWidth() {
		return resWidth;
	}

	public int getResHeight() {
		return resHeight;
	}
}
