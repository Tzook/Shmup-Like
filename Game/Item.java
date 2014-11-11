import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

/** =============================================================
 * Item extends Figure class.
 * Item is each item in the game. 
 * Currently there are Coins and Hearts. Bombs in the future.
  ============================================================= */
public class Item extends Figure {
	private static Item oneItem;
	
	private long _time;
	
	/**
	 * Default constructor
	 * @param enemy The enemy that died
	 * @param s Whether it is hp or money
	 */
	public Item (Enemy enemy, String s) { // overload
		this (enemy, s, new Random());
		setTime(0);
	}
	
	/**
	 * Overload constructor
	 * @param enemy The enemy that died
	 * @param s Whether it is hp or money
	 * @param rand Random number generator
	 */
	public Item(Enemy enemy, String s, Random rand) {  // generates 2 on 2  size coin at enemy's location
		super(enemy.getX() + enemy.getW() / 2 - 7, enemy.getY() + enemy.getH() / 2 - 7, 15, 15, rand.nextInt(2) + 1);
		if (s.equals("hp")) { // if it is hp
			setHp(rand.nextInt(10) - 5 + enemy.getMaxHp() / 10);
			setPower(0);
		} else if (s.equals("money")){ // if it is money
			setPower(rand.nextInt(10) - 5 + enemy.getMaxHp() / 20);
			setHp(0);
		}
	}
	/**
	 * updates the items list. updates each item in it
	 * @param item The items list
	 * @param player The player
	 */
	public static void update (ArrayList<Item> item, Player player) {		
		for (int i = 0; i < item.size(); i++) {
			oneItem = item.get(i);
			oneItem.update();
			// manage when to remove
			if (oneItem.getTime() == 0 && (oneItem.collide(player) // is an money/heart and touches player
				|| (Game.items[Game.ITEM_GET_ALL_MON] && player.getY() < Game.WINDOW_WIDTH / 10))) { // or has item and player is up 
				player.setMoney(player.getMoney() + oneItem.getPower()); // if money
				if (Game.items[Game.ITEM_MONEY_HEALS] && oneItem.getPower() > 0) // increase hp if has item and picked money
					player.setHp(player.getHp() + 3);
				player.setHp(player.getHp() + oneItem.getHp()); // if heart
				oneItem.setTime(System.currentTimeMillis());
				oneItem.setSpeed(-1);
			}
			if (Game.items[Game.ITEM_MONEY_BLOCK] && oneItem.getY() > Game.WINDOW_HEIGHT - oneItem.getH() - 1)
				oneItem.setY(Game.WINDOW_HEIGHT - oneItem.getH() - 1);
			if ((oneItem.getTime() > 0 && System.currentTimeMillis() - oneItem.getTime() > 500) || oneItem.outOfBorders())
				item.remove(i);			
		}
	}
	/**
	 * Draws all the items on the map
	 */
	public void draw (Graphics bbg) {
		if (getTime() > 0) { // after player has collected the item
			bbg.setFont(new Font("Arial", Font.BOLD, 14));
			if (getHp() > 0) { // is a heart
				bbg.setColor(Color.red);
				bbg.drawString("+" + (int)getHp(), getX(), getY() - 10);
			} else { // is money
				bbg.setColor(Color.black);
				bbg.drawString("+" + getPower(), getX(), getY() - 10);
			}
		} else { // item drops down the screens
		if (getHp() > 0)  // is heart
			drawHeart(bbg);
		else  // is money
			drawMoney(bbg);
		}
	}
	
	/*
	 * draws if it is a coin
	 */
	private void drawMoney(Graphics bbg) {
		bbg.setColor(new Color(255,153,0)); // orange
		bbg.fillOval(getX(), getY(), getW(), getH());
		bbg.setFont(new Font("Arial", Font.ITALIC, 12));
		bbg.setColor(Color.white);
		bbg.drawString("B", getX() + getW() / 4  , getY() + getH() * 4 / 5);
	}
	/*
	 * draws a perfectly looking heart!
	 */
	private void drawHeart(Graphics bbg) { 
		bbg.setColor(Color.red);
		bbg.fillPolygon(new int [] {getX() + 2, getX() + getW() / 2, getX() + getW() - 2, getX() + getW() / 2},
						new int [] {getY() + getH() / 2, getY() + 2, getY() + getH() / 2, getY() + getH() - 2}, 4);
		bbg.fillOval(getX(), getY(), getW() / 2, getH() / 2);
		bbg.fillOval(getX() + getW() / 2, getY(), getW() / 2, getH() / 2);
	}

	/**
	 * Gets the power - doubled if there is an item
	 * @return power = amount of money / hp
	 */
	public int getPower() {
		return (int)(super.getPower() * (Game.items[Game.ITEM_MONEY_DBL] ? 2 : 1));
	}
	/**
	 * Gets the time after the item has been collected
	 * @return time
	 */
	public long getTime() {
		return _time;
	}
	/** 
	 * Sets the time after the item has been collected
	 * @param time The time
	 */
	public void setTime(long time) {
		this._time = time;
	}
}
