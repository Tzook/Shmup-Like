import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

/** =============================================================
 * Main player of the game.
 * extends Figure, for it is an object in the game.
  ============================================================= */
public class Player extends Figure {
	public static boolean _isSpaceClicked;
	public static boolean _isShiftClicked;
	
	private static final int RIGHT = 0;
	private static final int LEFT = 1;
	private static final int UP = 2;
	private static final int DOWN= 3;
	
	private static float MAX_DEF = 50;
	private static int itemName;
	private static long itemTime;
	
	private String _spaceship;
	private float _regenHp; // in percents
	private float _regenDef; // used only when has def in percents
	private float _def;
	private float _acc [];
	private int _shotStyle;
	private int _money;
	
	
	private ArrayList<Shot> _shot;
	
	/**
	 * main constructor
	 * @param x X
	 * @param y Y
	 * @param w Width
	 * @param h Height
	 * @param name Name of the spaceship - used in the future! 
	 */
	public Player (int x, int y, int w, int h, String name) {
		super(x, y, w, h, DEFAULT_SPEED); // default constructor of Figure
		setHp(MAX_HP);
		setDef(MAX_DEF);
		setRegenHp(3f);
		setRegenDef(10f); 
		setShotStyle(0);
		setShot(new ArrayList<Shot>());
		setPower(100);
		setMoney(0);
		setSpaceship(name);
		_isSpaceClicked = false;
		_isShiftClicked = false;
		_acc = new float [4];
		itemName = 0;
		itemTime = 0;
	}

	/**
	 * Updates the player according to anything related to it
	 * Included: shots, input keys, health, regeneration, items purchased.
	 * @param input Input object - keyboard
	 * @param game The game
	 */
	public void update(InputHandler input, Game game) {
		setColor(Color.black);
		manageKeys(input);
		manageBuyItems();
		Shot.update(getShot(), this, game);
		keepInBorders();
		regen();
		isGameOver();
	}

	/*
	 * Manage the store - decide if there is enough money and if can buy
	 */
	private void manageBuyItems() {
		switch (Game.buy) {
			case 4: case 5: if (!Game.items[Game.ITEM_VET_SHOPPER]) break; 
			case 1:	case 2: case 3: if (getMoney() < (Game.items[Game.ITEM_HALF_PRICE] ? 50 : 100)) 
										break;
									setMoney(getMoney() - (Game.items[Game.ITEM_HALF_PRICE] ? 25 : 50));
			case 6: 				if (getMoney() < (Game.items[Game.ITEM_HALF_PRICE] ? 25 : 50))
										break;
									setMoney(getMoney() - (Game.items[Game.ITEM_HALF_PRICE] ? 25 : 50));
									itemName = buyItem((int)(System.currentTimeMillis() % 100), (new Random()).nextInt(15));
									System.out.println(itemName);
									itemTime = System.currentTimeMillis();
		}
		Game.buy = 0;
	}

	/*
	 * Purchase one item in random from the items lists.
	 * Retry if the item has already been bought.
	 * Each item has 15% chance of being received, however the purchased class has 40% instead.
	 */
	private int buyItem(int perc, int rand) {
		switch (Game.buy) { // bombs - 100% to get
			case 6: Game.items[75 + rand] = true;
					return 75 + rand;
		}
		if (perc < 75) {  // 15% for each item
			if (Game.items[perc] == true)
				return buyItem((new Random()).nextInt(100), rand); // re-roll if already has item
			else {
				Game.items[perc] = true;
				return perc;
			}
		} else {			// extra 25% for selected item
			if (Game.items[15 * (Game.buy - 1) + rand] == true) // re-roll if already has item
				return buyItem((new Random()).nextInt(100), (new Random()).nextInt(15));
			else {
				Game.items[15 * (Game.buy - 1) + rand] = true;
				return 15 * (Game.buy - 1) + rand;
			}
		}
	}

	/**
	 * Draws the player and its related toys on the screen
	 */
	public void draw(Graphics bbg) {		
		// top area - "HP"
		bbg.setFont(new Font("Arial", Font.BOLD, 9));
		bbg.drawString("- HP", 135, Game.items[Game.ITEM_DEF] ? 12 : 14);
		// top area - hp box + hp
		bbg.setColor(Color.red);
		bbg.fillRect(10, 4, (int)(getHp() * MAX_BAR / (MAX_HP + (Game.items[Game.ITEM_MORE_HP_1] ? 25 : 0) 
				 											  + (Game.items[Game.ITEM_MORE_HP_2] ? 25 : 0) 
				 											  + (Game.items[Game.ITEM_MORE_HP_3] ? 50 : 0)))
				 											  , Game.items[Game.ITEM_DEF] ? 8 : 12);
		bbg.setColor(Color.black);
		bbg.drawRect(10, 4, MAX_BAR, Game.items[Game.ITEM_DEF] ? 8 : 12); 
		// amount of hp
		bbg.drawString("" + (int)getHp(), 60, Game.items[Game.ITEM_DEF] ? 12 : 14);
		// draws an item if recently purchased
		drawItemName(bbg);
		// only draw def if there is a def
		drawDef(bbg);
		
		// player
		bbg.setColor(getColor());
		drawSpaceship(bbg);
	}

	/*
	 * Whenever purchasing an item, write its name on the screen.
	 * Used for "cool" purposes.
	 */
	private void drawItemName(Graphics bbg) {
		if (System.currentTimeMillis() - itemTime > 750)
			return;
		bbg.setFont(new Font("Arial", Font.BOLD, 14));
		switch (itemName) {
			case Game.ITEM_LASER_SHOTS:  bbg.drawString("Laser Bullets!", 	(Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 0
			case Game.ITEM_MORE_POWER_1: bbg.drawString("Stronger Guns!", 	(Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 1
			case Game.ITEM_MORE_POWER_2: bbg.drawString("Stronger Bullets!",(Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 2
			case Game.ITEM_MORE_POWER_3: bbg.drawString("Sharp Shooter!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 3
				
			case Game.ITEM_DEF: bbg.drawString("Solar Shield!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 15
			case Game.ITEM_DOUBLE_REGEN: bbg.drawString("Solar Capacity!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 16
			case Game.ITEM_SPIKES: bbg.drawString("Spikes!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 17
			case Game.ITEM_MONEY_HEALS: bbg.drawString("Useful Thinking!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 18
			case Game.ITEM_MORE_HP_1: bbg.drawString("Stronger Armor!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 19
			case Game.ITEM_MORE_HP_2: bbg.drawString("Better Armor!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 20
			case Game.ITEM_MORE_HP_3: bbg.drawString("Perfect Coating!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 21
				
			case Game.ITEM_FAST_SHOT: bbg.drawString("Energy Bullets!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 30
			case Game.ITEM_QUICK_SHOT_1: bbg.drawString("Stronger Hands!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 31
			case Game.ITEM_QUICK_SHOT_2: bbg.drawString("Better Hands!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 32
			case Game.ITEM_QUICK_SHOT_3: bbg.drawString("World Clicker!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 33
			case Game.ITEM_FAST_SHIP_1: bbg.drawString("Stronger Engine!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 34
			case Game.ITEM_FAST_SHIP_2: bbg.drawString("Better Engine!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 35
			case Game.ITEM_FAST_SHIP_3: bbg.drawString("Perfect Maneuvers!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 36
			case Game.ITEM_SLOW_MO:		bbg.drawString("Slow Mo!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 37
				

			case Game.ITEM_VET_SHOPPER: bbg.drawString("Veteran Shopper!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 45
			case Game.ITEM_MONEY_BLOCK: bbg.drawString("Pick It All!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 46
			case Game.ITEM_MONEY_DBL: bbg.drawString("Double Money!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 47
			case Game.ITEM_HALF_PRICE: bbg.drawString("Sales Day!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 48
			case Game.ITEM_GET_ALL_MON: bbg.drawString("Borderline!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 49
			case Game.ITEM_STORE_PAUSE: bbg.drawString("Time Control!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 50
				
			case Game.ITEM_SHOOT_BACK: bbg.drawString("8!", (Game.WINDOW_WIDTH + Game.WINDOW_START_X) / 2 - 50, Game.WINDOW_HEIGHT / 2 - (int)(System.currentTimeMillis() - itemTime) / 25);
				break; // 60
		}
		bbg.setFont(new Font("Arial", Font.BOLD, 9));
	}

	/*
	 * Draws the spaceship in a very-super-ultra-duper cool way.
	 */
	private void drawSpaceship(Graphics bbg) {
		if ((getSpaceship()).equals("box")) { // normal box
			bbg.drawRect(getX(), getY(), getW(), getH());
		} else if (getSpaceship().equals("")) { // draws a normal spaceship!
			if (getColor().equals(Color.red)) // draw red outline if hurt
				bbg.drawPolygon(new int [] {getX()+getW()/2, getX()+getW()/10*3, getX()+getW()/10*3, // points : 1-3
						getX()		   , getX()			  , getX()+getW()/4, getX()+getW()-getW()/4, // 4-7
						getX()+getW()	 , getX()+getW()  , getX()+getW()-getW()/10*3, getX()+getW()-getW()/10*3}, // 8-11
								new int [] {getY()		   , getY()+getH()/4, getY()+getH()/5*3, // 1-3
						getY()+getH()/2, getY()+getH()/5*3, getY()+getH()  , getY()+getH(), // 4-7
						getY()+getH()/5*3, getY()+getH()/2, getY()+getH()/5*3, getY()+getH()/4}, 11); // 8-11
			bbg.setColor(new Color(139, 137, 137));
			bbg.fillPolygon(new int [] {getX()+getW()/2, getX()+getW()/10*3, getX()+getW()/10*3, // points : 1-3
					getX()		   , getX()			  , getX()+getW()/4, getX()+getW()-getW()/4, // 4-7
					getX()+getW()	 , getX()+getW()  , getX()+getW()-getW()/10*3, getX()+getW()-getW()/10*3}, // 8-11
							new int [] {getY()		   , getY()+getH()/4, getY()+getH()/5*3, // 1-3
					getY()+getH()/2, getY()+getH()/5*3, getY()+getH()  , getY()+getH(), // 4-7
					getY()+getH()/5*3, getY()+getH()/2, getY()+getH()/5*3, getY()+getH()/4}, 11); // 8-11
		} 
	}

	/**
	 * Reduce the HP if touched the enemy 
	 * @param enemy The enemy to check if touched
	 */
	public void checkIfHurt(Figure enemy) {
		if (collide(enemy)) {
			setColor(Color.red);
			if (getDef() > 0)
				setDef(getDef() - enemy.getPower());
			else 
				setHp(super.getHp() - enemy.getPower()); // need pure hp without items
			if (Game.items[Game.ITEM_SPIKES])
				enemy.setHp(enemy.getHp() - 1);
			
		}
	}
	/*
	 * Game is over when hp is below 0.
	 */
	private void isGameOver() { // shield below 0
		if (getHp() <= 0)
			System.out.println("dead");
	}
	/*
	 * DEF item - 
	 * Draws only if have the item. gives extra DEF before HP is being reduced.
	 */
	private void drawDef(Graphics bbg) {
		if (!Game.items[Game.ITEM_DEF])
			return;
		// the person
		bbg.setColor(Color.gray);
		bbg.drawRect(10, 12, MAX_BAR, 8);
		bbg.fillRect(10, 12, (int)(getDef() * MAX_BAR / MAX_DEF) , 8); // make it the length of the shield
		// top area - "DEF"
		bbg.setColor(Color.black);
		bbg.drawString("- DEF", 135, 21);
		// amount of def
		bbg.drawString("" + (int)getDef(), 60, 20);
	}
	/*
	 * Constantly regenerate HP (and DEF if has the item).
	 */
	private void regen() { // increase shield and def
		if (Game.delay(Game.DELAY_REGEN, .05f)) { // 20 times a second
			if (super.getHp() < MAX_HP)
				setHp(super.getHp() + ((MAX_HP / 100) * getRegenHp()) / 20); // % of MAX_SHIELD
			// manage def if it is available
			if (!Game.items[Game.ITEM_DEF])
				setDef(0);
			else if (getDef() < MAX_DEF)
				setDef(getDef() + ((MAX_DEF / 100) * getRegenDef()) / 20); // % of MAX_DEF
		}
	}
	/*
	 * Moves player according to keys clicked + accelerating.
	 */
	private void manageKeys(InputHandler input) { 
		if (input.isKeyDown(KeyEvent.VK_RIGHT) || input.isKeyDown(KeyEvent.VK_D)) { // move right + acc
			setAcc(1, LEFT); // reset left acc
			if (getAcc(RIGHT) < getSpeed()) {
				setX(getX() + (int)getAcc(RIGHT) - 1);
				setAcc(getAcc(RIGHT) * 14 / 11, RIGHT);
			} else {
				setX(getX() + getSpeed());
				setAcc(getSpeed(), RIGHT);
			}
		} else {
			setX(getX() + (int)getAcc(RIGHT) - 1);
			setAcc(getAcc(RIGHT) / 15 * 14, RIGHT);
		}
		if (input.isKeyDown(KeyEvent.VK_LEFT) || input.isKeyDown(KeyEvent.VK_A)) { // move left + acc
			setAcc(1, RIGHT); // reset right acc
			if (getAcc(LEFT) < getSpeed()) {
				setX(getX() - (int)getAcc(LEFT) + 1);
				setAcc(getAcc(LEFT) * 14 / 11, LEFT);
			} else {
				setX(getX() - getSpeed());
				setAcc(getSpeed(), LEFT);
			}
		} else {
			setX(getX() - (int)getAcc(LEFT) + 1);
			setAcc(getAcc(LEFT) / 15 * 14, LEFT);
		}
		if (input.isKeyDown(KeyEvent.VK_UP) || input.isKeyDown(KeyEvent.VK_W)) { // move up + acc
			setAcc(1, DOWN); // reset down acc
			if (getAcc(UP) < getSpeed()) {
				setY(getY() - (int)getAcc(UP) + 1);
				setAcc(getAcc(UP) * 14 / 11, UP);
			} else {
				setY(getY() - getSpeed());
				setAcc(getSpeed(), UP);
			}
		} else {
			setY(getY() - (int)getAcc(UP) + 1);
			setAcc(getAcc(UP) / 15 * 14, UP);
		}
		if (input.isKeyDown(KeyEvent.VK_DOWN) || input.isKeyDown(KeyEvent.VK_S)) { // move down + acc
			setAcc(1, UP); // reset up acc
			if (getAcc(DOWN) < getSpeed()) {
				setY(getY() + (int)getAcc(DOWN) - 1);
				setAcc(getAcc(DOWN) * 14 / 11, DOWN);
			} else {
				setY(getY() +getSpeed());
				setAcc(getSpeed(), DOWN);
			}
		} else {
			setY(getY() + (int)getAcc(DOWN) - 1);
			setAcc(getAcc(DOWN) / 15 * 14, DOWN);
		}
		if (input.isKeyDown(KeyEvent.VK_SPACE) && Game.delay(Game.DELAY_SPACE, 0.2f))
			_isSpaceClicked = !_isSpaceClicked;
		if (input.isKeyDown(KeyEvent.VK_SHIFT))
			_isShiftClicked = true;
		else _isShiftClicked = false;
	}
	/*
	 * Makes sure player is in the map.
	 */
	private void keepInBorders() { 
		if ( getX() < Game.WINDOW_START_X + 5)
			setX(Game.WINDOW_START_X + 5);
		if ( getX() > Game.WINDOW_WIDTH - getW() - 5)
			setX(Game.WINDOW_WIDTH - getW() - 5);
		if ( getY() < Game.WINDOW_START_Y + 5 )
			setY(Game.WINDOW_START_Y + 5);
		if ( getY() > Game.WINDOW_HEIGHT- getH() - 5)
			setY(Game.WINDOW_HEIGHT - getH() - 5);
	}
	
	/**
	 * Gets the amount of Regeneration of HP. quicker if has an item.
	 * @return regenHP
	 */
	public float getRegenHp() {
		return _regenHp * (_isSpaceClicked ? 1 : 2) * (Game.items[Game.ITEM_DOUBLE_REGEN] ? 2 : 1);
	}
	/**
	 * sets the regenHP
	 * @param regenHp The new regenHP
	 */
	public void setRegenHp(float regenHp) {
		this._regenHp = regenHp;
	}
	/**
	 * Gets the amount of Regeneration of DEF. quicker if has an item.
	 * @return regenDef
	 */
	public float getRegenDef() {
		return _regenDef * (_isSpaceClicked ? 1 : 2) * (Game.items[Game.ITEM_DOUBLE_REGEN] ? 2 : 1);
	}
	/**
	 * Sets the regenDEF
	 * @param regenDef The new regenDEF
	 */
	public void setRegenDef(float regenDef) {
		this._regenDef = regenDef;
	}
	/**
	 * Gets the amount of HP + HP from items.
	 * @return the HP
	 */
	public float getHp() {
		return super.getHp() + (Game.items[Game.ITEM_MORE_HP_1] ? 25 : 0) // more hp
							 + (Game.items[Game.ITEM_MORE_HP_2] ? 25 : 0) 
							 + (Game.items[Game.ITEM_MORE_HP_3] ? 50 : 0);
	}
	/**
	 * Gets the amount of power + power from items.
	 * @return the power
	 */
	public int getPower() {
		return (int)(super.getPower() * (Game.items[Game.ITEM_MORE_POWER_1] ? 1.5 : 1) // more hp
									  * (Game.items[Game.ITEM_MORE_POWER_2] ? 1.5 : 1) 
									  * (Game.items[Game.ITEM_MORE_POWER_3] ? 2 : 1));
	}
	/**
	 * Gets the amount of speed + speed from items.
	 * @return the speed
	 */
	public int getSpeed() {
		return (int)(super.getSpeed() * (Game.items[Game.ITEM_FAST_SHIP_1] ? 1.3f : 1) 
									  * (Game.items[Game.ITEM_FAST_SHIP_2] ? 1.3f : 1) 
									  * (Game.items[Game.ITEM_FAST_SHIP_3] ? 1.5f : 1)
									  * (Game.items[Game.ITEM_SLOW_MO] && _isShiftClicked ? .1f : 1));
	}
	/**
	 * Gets the amount of def.
	 * @return the def
	 */
	public float getDef() {
		return _def;
	}
	/**
	 * sets the new def. if below 0, reset to 0
	 * @param def New def. 
	 */
	public void setDef(float def) {		
		this._def = def;
		if (_def < 0)
			_def = 0;
	}
	/**
	 * Gets the style of the shot
	 * @return the shotstyle
	 */
	public int getShotStyle() {
		return _shotStyle;
	}
	/**
	 * Sets the style of the shot
	 * @param shotStyle New shotstyle
	 */
	public void setShotStyle(int shotStyle) {
		this._shotStyle = shotStyle;
	}
	/**
	 * Returns the shot list
	 * @return shot
	 */
	public ArrayList<Shot> getShot() {
		return _shot;
	}
	/**
	 * Sets the shot list
	 * @param shot New shot
	 */
	public void setShot(ArrayList<Shot> shot) {
		this._shot = shot;
	}
	/**
	 * Gets the amount of money
	 * @return money
	 */
	public int getMoney() {
		return _money;
	}
	/**
	 * Sets the amount of money
	 * @param money New amount
	 */
	public void setMoney(int money) {
		this._money = money;
	}
	/**
	 * Gets the acceleration to the specific side. 
	 * @param side up/down/left/right
	 * @return its acceleration
	 */
	public float getAcc(int side) {
		return _acc[side];
	}
	/**
	 * Sets the array in location side to the acceleration. cannot be below 1 
	 * @param acc The new acceleration
	 * @param side up/down/left/right
	 */
	public void setAcc(float acc, int side) {
		this._acc[side]= acc;
		if (_acc[side] < 1)
			_acc[side] = 1;
	}
	/**
	 * Gets the spaceship name
	 * @return spaceshiip
	 */
	public String getSpaceship() {
		return _spaceship;
	}
	/**
	 * Sets the spaceship name
	 * @param spaceship New name
	 */
	public void setSpaceship(String spaceship) {
		this._spaceship = spaceship;
	}
}
