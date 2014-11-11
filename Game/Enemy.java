import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/** =============================================================
 * Enemy extends from figure.
 * Enemy is every enemy object in the game.
 * Main game has a list of Enemies, and each one is an 
 * 		Enemy object.
  ============================================================= */
public class Enemy extends Figure {
	private static Enemy oneEnemy;
	private int _maxHp;
	private int _sideSpeed;
	
	/**
	 * Default constructor
	 * @param x X
	 * @param y Y
	 * @param w Width
	 * @param h Height
	 * @param speed Speed
	 * @param color Color
	 */
	public Enemy(int x, int y, int w, int h, int speed, Color color) {
		super(x, y, w, h, speed);
		setColor(color);
		setMaxHp(Game.level * 200);
		setHp(getMaxHp());
		setPower(Game.level);
		setSideSpeed(0);
	}
	
	/**
	 * Updates the enemies list. cycles through it and updates each enemy.
	 * @param enemy The enemies list
	 * @param player The player
	 * @param item The items list. needed in order to put new items once an enemy dies
	 */
	public static void update (ArrayList<Enemy> enemy, Player player, ArrayList<Item> item) {		
		for (int i = 0; i < enemy.size(); i++) {
			oneEnemy = enemy.get(i);
			oneEnemy.update(player);
			// checks if the player was hurt from this enemy
			player.checkIfHurt(oneEnemy);
			// if shot hit the enemy
			for (int j = 0; j < player.getShot().size(); j++) {
				if (oneEnemy.collide(player.getShot().get(j))) {
					oneEnemy.setHp(oneEnemy.getHp() - player.getPower());
					if (!Game.items[Game.ITEM_LASER_SHOTS]) // need to make enemies hurt only once!
						player.getShot().remove(j);
				}
			}
			// manage when to remove
			if (oneEnemy.getHp() <= 0) {
				item.add(new Item(oneEnemy,(System.currentTimeMillis() % 10 == 1 ? "hp" : "money")));
				enemy.remove(i);				
			}
			if (oneEnemy.outOfBorders())
				enemy.remove(i);
		}
	}

	/**
	 * Updates the enemy, according to its wave
	 * @param player The player
	 */
	public void update (Player player) {
		// moves up/down at 60 * speed per second 
		setY(getY() + getSpeed());
		// moves right/left at 60 * sideSpeed per second
		setX(getX() + getSideSpeed());

		if (getColor().equals(Color.blue)) { // deals with waveN
			if (getX() > Game.WINDOW_WIDTH / 2) {
				if (getY() > Game.WINDOW_HEIGHT / 10*9 ) {
					setSideSpeed(-2);
					setSpeed(getSpeed() * -1);
				}
			} else {
				if (getY() < Game.WINDOW_HEIGHT / 10) {
					setSideSpeed(0);
					setSpeed(getSpeed() * -1);
				}
			}
		} // waveN
	} // update
	
	/**
	 * Draws each enemy
	 */
	public void draw(Graphics bbg) {
		bbg.setColor(getColor());
		bbg.fillRect(getX(), getY(), getW(), getH());
	}

	/**
	 * Gets the max HP
	 * @return maxhp
	 */
	public int getMaxHp() {
		return _maxHp;
	}
	/**
	 * Sets the max HP
	 * @param maxHp New maxHP
	 */
	public void setMaxHp(int maxHp) {
		this._maxHp = maxHp;
	}
	/**
	 * Gets the Side Speed
	 * @return sidespeed
	 */
	public int getSideSpeed() {
		return _sideSpeed;
	}
	/**
	 * Sets the Side Speed
	 * @param sideSpeed New sideSpeed
	 */
	public void setSideSpeed(int sideSpeed) {
		this._sideSpeed = sideSpeed;
	}
}
