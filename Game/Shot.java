import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

/** =============================================================
 * Shot extends from figure.
 * Shot is every shot object in the game.
 * Main game has a list of Shots, and each one is a 
 * 		Shot object.
  ============================================================= */
public class Shot extends Figure {
	public static final int GOOD = -1;
	public static final int BAD = 1;
	
	private int _type;
	private int _index;
	private int _sideSpeed;

	/**
	 * Default constructor
	 * @param player The player
	 * @param speed Speed
	 * @param index For future purpose
	 * @param game The game
	 */
	public Shot (Player player, int speed, int index, Game game) {
		super(player.getX() + player.getW() / 2, player.getY() - 5, 2, 5, -speed);
		setSideSpeed(0);
		setType(GOOD);
		setDirection(speed, player, game);
	}

	/*
	 * Sets the direction of the shot with an uber cool formula.
	 * Without an item, there are 3 direction - North, North-east, North-west.
	 * With an item, there are all 8 wind-directions
	 */
	private void setDirection(int speed, Player player, Game game) {
		Point p = game.getMousePoint();
		double  x = p.x - (player.getX() + player.getW() / 2),
				y = p.y - (player.getY() + player.getH() / 2),
				m = (x == 0 ? y / 0.1 : y / x),
				degPiBy8 = Math.tan(Math.PI / 8), // angle 22.5
				deg3PiBy8 = Math.tan(3 * Math.PI / 8); // angle 67.5
		
		if (m > degPiBy8 && m < deg3PiBy8) { // right down and left up
			if (x < 0) // p is to the left
				speed = -speed;
			setSpeed(speed);
			setSideSpeed(speed);
		} else if (m < -degPiBy8 && m > -deg3PiBy8) { // right up and left down
			if (x < 0) // p is to the left
				speed = -speed;
			setSpeed(-speed);
			setSideSpeed(speed);
		} else if (m >= -degPiBy8 && m <= degPiBy8) { // sides
			if (x < 0) 
				speed = -speed;
			setSpeed(0);
			setSideSpeed(speed);
		} else { // up and down
			if (y < 0) // p is above
				speed = -speed;
			setSpeed(speed);
			setSideSpeed(0);
		}
		if (!Game.items[Game.ITEM_SHOOT_BACK]) // shoot only forward
			setSpeed(-Math.abs(speed));
	}

	/**
	 * updates the shot list. updates each shot in the list repeatedly.
	 * @param shot The shot list
	 * @param player The player
	 * @param game The game
	 */
	public static void update (ArrayList<Shot> shot, Player player, Game game) {	
		// shoots automatically
		if (Player._isSpaceClicked && Game.delay(Game.DELAY_SHOT, .35f * (Game.items[Game.ITEM_QUICK_SHOT_1] ? .8f : 1) // less delay
																	   * (Game.items[Game.ITEM_QUICK_SHOT_2] ? .8f : 1) 
																	   * (Game.items[Game.ITEM_QUICK_SHOT_3] ? .6f : 1))) 
			shot.add(new Shot(player, 5, player.getShotStyle(), game));
		for (int i = 0; i < shot.size(); i++) {
			shot.get(i).update();
			// manage when to remove
			if (shot.get(i).outOfBorders())
				shot.remove(i);
		}
	}
	
	/**
	 * Updates each shot
	 */
	public void update() {
		setY(getY() + getSpeed()); 
		setX(getX() + getSideSpeed());
	}
	
	/**
	 * Draws each shot.
	 */
	public void draw(Graphics bbg) {
		bbg.setColor(Color.black);
		if (getSpeed() == 0)
			bbg.fillRect(getX(), getY(), getH(), getW());
		else bbg.fillRect(getX(), getY(), getW(), getH());
	}

	/**
	 * Gets the type
	 * @return type
	 */
	public int getType() {
		return _type;
	}
	/**
	 * Sets the type
	 * @param type New type
	 */
	public void setType(int type) {
		this._type = type;
	}
	/**
	 * Gets the index
	 * @return index
	 */
	public int getIndex() {
		return _index;
	}
	/**
	 * Sets the index
	 * @param index New index
	 */
	public void setIndex(int index) {
		this._index = index;
	}
	/**
	 * Gets the speed. doubled if has item
	 * @return speed The speed
	 */
	public int getSpeed () {
		return super.getSpeed() * (Game.items[Game.ITEM_FAST_SHOT] ? 2 : 1);
	}
	/**
	 * Gets the Side Speed
	 * @return sidespeed
	 */
	public int getSideSpeed() {
		return _sideSpeed * (Game.items[Game.ITEM_FAST_SHOT] ? 2 : 1);
	}
	/**
	 * Sets the Side Speed
	 * @param sideSpeed New sideSpeed
	 */
	public void setSideSpeed(int sideSpeed) {
		this._sideSpeed = sideSpeed;
	}
}
