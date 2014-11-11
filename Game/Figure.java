import java.awt.Color;
import java.awt.Graphics;

/** =============================================================
 * Figure is the base structure for anything in the game.
 * Player, Enemy, Shot and Item classes extend it.
  ============================================================= */
public class Figure {
	protected static final int DEFAULT_SPEED = 5;
	protected static final int DEFAULT_WIDTH= 20;
	protected static final int DEFAULT_HEIGHT = 20;
	protected static final int MAX_BAR = 120;
	protected static float MAX_HP = 120;
	
	private int _x;
	private int _y;
	private int _w;
	private int _h;	
	private int _speed;
	private int _power;
	private float _hp;
	private Color _color;

	/**
	 *  constructor for overriding
	 */
	public Figure () { }
	
	/**
	 *  default constructor
	 * @param x X
	 * @param y Y
	 * @param w	Width
	 * @param h	Height
	 * @param speed	Speed
	 */
	public Figure (int x, int y, int w, int h, int speed) {
		setX(x);
		setY(y);
		setW(w);
		setH(h);
		setSpeed(speed); 
	}
	/**
	 * default update - lowers the figure down the screen with its speed
	 */
	public void update () {
		// descending
		setY(getY() + getSpeed());
	}
	
	/**
	 * draws the figure - to be overwritten. 
	 * @param bbg The drawing object
	 */
	public void draw (Graphics bbg) { }

	/**
	 * gets a Figure object, and checks if the two are in the same area - "collide"
	 * @param enemy The other figure to check
	 * @return If the two figures touch each other
	 */
	public boolean collide (Figure enemy) {
		return (getX() + getW() >= enemy.getX() && getX() <= enemy.getX() + enemy.getW()
			&& getY() + getH() >= enemy.getY() && getY() <= enemy.getY() + enemy.getH()) ? true : false;
	}

	/**
	 *  tells if the figure is out of the map
	 * @return true if figure is out of the map
	 */
	public boolean outOfBorders() {
		return getX() + getW() < 0 || getX() > Game.WINDOW_WIDTH // out on sides
				/*|| getY() + getH() < Game.WINDOW_START_Y + 5 */|| getY() > Game.WINDOW_HEIGHT // out on top/bottom 
			? true : false;
	}
	
	// get and set functions
	/**
	 * returns the x
	 * @return x
	 */
	public int getX() {
		return _x;
	}
	/**
	 * sets the x
	 * @param x
	 */
	public void setX(int x) {
		this._x = x;
	}
	/**
	 * returns the y
	 * @return y
	 */
	public int getY() {
		return _y;
	}
	/**
	 * sets the y
	 * @param y
	 */
	public void setY(int y) {
		this._y = y;
	}
	/**
	 * returns the speed
	 * @return speed
	 */
	public int getSpeed() {
		return _speed;
	}
	/**
	 * sets the speed
	 * @param speed
	 */
	public void setSpeed(int speed) {
		this._speed = speed;
	}
	/**
	 * returns the width
	 * @return width
	 */
	public int getW() {
		return _w;
	}
	/**
	 * sets the width
	 * @param width
	 */
	public void setW(int w) {
		this._w = w;
	}
	/**
	 * returns the height
	 * @return height
	 */
	public int getH() {
		return _h;
	}
	/**
	 * sets the height
	 * @param height
	 */
	public void setH(int h) {
		this._h = h;
	}	
	/**
	 * returns the hp
	 * @return hp
	 */
	public float getHp() {
		return _hp;
	}
	/**
	 * sets the hp. cannot be set above max hp
	 * @param hp
	 */
	public void setHp(float hp) {
		this._hp= hp;
		if (_hp > MAX_HP)
			this._hp = MAX_HP;
	}
	/**
	 * returns the power
	 * @return power
	 */
	public int getPower() {
		return _power;
	}
	/**
	 * sets the power
	 * @param power
	 */
	public void setPower(int power) {
		this._power = power;
	}
	/**
	 * returns the color
	 * @return color
	 */
	public Color getColor() {
		return _color;
	}
	/**
	 * sets the color
	 * @param color
	 */
	public void setColor(Color color) {
		this._color = color;
	}
}
