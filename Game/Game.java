import java.awt.*; 
import java.awt.event.KeyEvent; 
import java.awt.image.BufferedImage; 
import java.util.ArrayList;

import javax.swing.*;

/** =============================================================
 * @author Tzook Shaked
 * Shmup Like is a game made for learning purposes only!
 * The game was requested by my brother, Michael.
 * 
 * How it works: You are a spaceship. DON'T DIE!
 * 				 Shoot: by pressing space once. again to stop.
 * 				 Move: ASDW or up/down/left/right.
 * 				 PAUSE: escape.
 * 				 Kill enemies, pick up money, and buy items!
 * 					that is all! so far.. 
  ============================================================= */
public class Game extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static final int FPS = 60;
	private static final int DELAY_AMOUNTS = 10;
	private static final int MAX_ITEMS = 90;
	
	// items list - need to complete, supposed to be 0-89 items (90 in total)
	// GUNS 0-14
	public static final int ITEM_LASER_SHOTS = 0; // shots are piercing through everything // need to modify
	public static final int ITEM_MORE_POWER_1= 1; // 50% more power
	public static final int ITEM_MORE_POWER_2= 2; // 50% more power
	public static final int ITEM_MORE_POWER_3= 3; // 100% more power
	
	// ARMOR 15-29
	public static final int ITEM_DEF 		 = 15; // adds DEF to the player
	public static final int ITEM_DOUBLE_REGEN= 16; // doubles the regeneration amount 
	public static final int ITEM_SPIKES		 = 17; // damages enemies by touch
	public static final int ITEM_MONEY_HEALS = 18; // money heals 3 hp
	public static final int ITEM_MORE_HP_1	 = 19; // 25 more hp
	public static final int ITEM_MORE_HP_2   = 20; // 25 more hp
	public static final int ITEM_MORE_HP_3   = 21; // 50 more hp
	
	// ENGINE 30-44
	public static final int ITEM_FAST_SHOT 	 = 30; // shot gets faster from point A to B
	public static final int ITEM_QUICK_SHOT_1= 31; // delay between shots reduced by 20%
	public static final int ITEM_QUICK_SHOT_2= 32; // delay between shots reduced by 20%
	public static final int ITEM_QUICK_SHOT_3= 33; // delay between shots reduced by 40%
	public static final int ITEM_FAST_SHIP_1 = 34; // ship is faster by 30%
	public static final int ITEM_FAST_SHIP_2 = 35; // ship is faster by 30%
	public static final int ITEM_FAST_SHIP_3 = 36; // ship is faster by 50%
	public static final int ITEM_SLOW_MO 	 = 37; // ship is faster by 50%
	
	// ARTIFACTS 45-59
	public static final int ITEM_VET_SHOPPER = 45; // more shopping options
	public static final int ITEM_MONEY_BLOCK = 46; // keeps the items from falling down the screen
	public static final int ITEM_MONEY_DBL 	 = 47; // doubles the amount of money found
	public static final int ITEM_HALF_PRICE  = 48; // lowers store prices by half!
	public static final int ITEM_GET_ALL_MON = 49; // if player is in the top 20% of the screen, all money/hearts/bombs are collected
	public static final int ITEM_STORE_PAUSE = 50; // enables you to shop at the store during pause
	
	// PATTERNS  60-74
	public static final int ITEM_SHOOT_BACK  = 60; // enables shooting in all 8 directions

	// BOMBS 75-89
	
	// delays list
	public static final int DELAY_TIME 		  = 0;
	public static final int DELAY_PAUSE 	  = 1;
	public static final int DELAY_GEN_ENEMIES = 2;
	public static final int DELAY_WAVES 	  = 3;
	public static final int DELAY_REGEN 	  = 4;
	public static final int DELAY_SPACE 	  = 5;
	public static final int DELAY_SHOT 		  = 6;
	public static final int DELAY_ENEMY_FALL  = 7;
	public static final int DELAY_BUY		  = 8;
	public static final int DELAY_SHOT_COL_EN = 9;
	// screen details
	public static final int WINDOW_WIDTH = 650;
	public static final int WINDOW_HEIGHT = 600;
	public static final int WINDOW_START_X= 50;
	public static final int WINDOW_START_Y= 25;
	
	public static long [] delay;
	public static long time;
	public static long timeOfOnePause;
	public static long timeOfTotalPause;
	public static long randomTimer;
	public static boolean [] items;
	public static boolean pause;
	public static int level;
	public static int count;
	public static int buy;
	public static ArrayList<Integer> waveFallTime;

	private boolean isRunning;
	ArrayList<Enemy> enemy; // all the enemies in the map
	ArrayList<Item> money; // all the money / hearts in the map
	Player player;
	BufferedImage backBuffer; 
	Insets insets; 
	InputHandler input; 
	
	/** 
	 * runs the program. exits once finished 
	 * @param args Not used
	 */ 
	public static void main(String[] args) 
	{ 
		Game game = new Game();
		game.run(); 
		System.exit(0); 
	} 
	
	/** 
	 * Run deals with the main program.
	 * It first initializes everything, and then it runs the game untill isRunning becomes false - when it is game over.
	 */ 
	public void run() 
	{ 
		initializeWindow();
		initializeGame();
		
		// this while runs FPS (60) times per second. each tick it updates everything and draws the entire screen again
		while(isRunning) { 
			long time = System.currentTimeMillis(); 
            
			update();
			draw(); 
			
			//  delay for each frame  -   time it took for one frame 
			if ((time = (1000 / FPS) - (System.currentTimeMillis() - time)) > 0) { 
				try { 
					Thread.sleep(time); 
				} 
				catch(Exception e){} 
			} 
		}
       setVisible(false); 
	} 
	/** 
	 * This method will set up everything need for the game to run 
	 */ 
	void initializeWindow() 
	{ 	
		isRunning = true;
		setTitle("The most awesome game you will ever play"); 
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT); 
		setResizable(false); 
		setDefaultCloseOperation(EXIT_ON_CLOSE); 
	    setUndecorated(true);
		setVisible(true);
		insets = getInsets(); 
		setSize(insets.left + WINDOW_WIDTH + insets.right, 
				insets.top + WINDOW_HEIGHT + insets.bottom);  
		backBuffer = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
		input = new InputHandler(this);
	} 
	/** 
	 * This method will initialize game-related things
	 */ 
	void initializeGame() 
	{ 	
    	enemy = new ArrayList<Enemy>();
    	money = new ArrayList<Item>();
		randomTimer = 0;
		time = 0;
		timeOfOnePause = 0;
		timeOfTotalPause = 0;
		buy = 0;
		level = 1;
		pause = false;
		delay = new long [DELAY_AMOUNTS];
		items = new boolean [MAX_ITEMS];
    	player = new Player (WINDOW_WIDTH / 2, WINDOW_HEIGHT, 30, 37, "");//textfield.getText().toLowerCase());
	} 
	

	/** 
	 * updates the game according to anything that changes.
	 */ 
	void update() 
	{ 
		// manage pausing
		if (input.isKeyDown(KeyEvent.VK_ESCAPE) && delay(DELAY_PAUSE, 0.2f)) {
			if (!pause)
				timeOfOnePause = System.currentTimeMillis();
			else 
				timeOfTotalPause += (System.currentTimeMillis() - timeOfOnePause);
			pause = !pause;
		}
		if (pause)
			return;
		
		// update time
		if (delay(DELAY_TIME, 1))
			time++;
		
		// update player and enemy
		player.update(input, this);
		Enemy.update(enemy, player, money);
		Item.update(money, player);
		
		generateEnemies();
	}
	
	/* 
	 * Creates a random enemies wave on the screen.
	 */
	private void generateEnemies() {
		if (delay(DELAY_GEN_ENEMIES, randomTimer / 2 + 12)) { // between 12 to 17
			randomTimer = System.currentTimeMillis() % 10 / 3; 
			count = -1;
		}
		wave((int)randomTimer);
	}

	private void wave(int wave) {
		switch (wave) {
			case 0: waveRow();
					break;
			case 1: waveN();
					break;
			case 2: waveY();
					break;
			case 3: waveUp();
					break;
			case 4: waveFall(); // need to work on!!
					break;
		}
	}

	/*
	 * WORK IN PROGRESS
	 */
	private void waveFall() {
		waveFallTime = new ArrayList<Integer>();
		while (++count < 10) { // 10 times
			enemy.add(new Enemy((WINDOW_WIDTH - WINDOW_START_X) / 10 * count + 15, WINDOW_START_Y, 30, 30, 1, new Color(255, 0, 127)));
			waveFallTime.add(count + 1);
		}
	}

	/*
	 * enemies coming from the bottom, right to left, one at a time
	 */
	private void waveUp() {
		if (delay(DELAY_WAVES, 1f) && ++count < 10) // 10 times
			enemy.add(new Enemy((WINDOW_WIDTH - WINDOW_START_X) / 10 * (10 - count) + 15, WINDOW_HEIGHT, 30, 30, -1, Color.orange)); 
	}
	/*
	 * enemies coming from top, in a Y shape
	 */
	private void waveY() {
		if (delay(DELAY_WAVES, 1f) && ++count < 5) { // 5 times
			if (count == 0)
				enemy.add(new Enemy((WINDOW_WIDTH + WINDOW_START_X) / 2, WINDOW_START_Y, 30, 30, 1, Color.yellow)); 
			else {
				enemy.add(new Enemy((WINDOW_WIDTH + WINDOW_START_X) / 2 + (WINDOW_WIDTH - WINDOW_START_X) / 10 * count, WINDOW_START_Y, 30, 30, 1, Color.yellow));
				enemy.add(new Enemy((WINDOW_WIDTH + WINDOW_START_X) / 2 - (WINDOW_WIDTH - WINDOW_START_X) / 10 * count, WINDOW_START_Y, 30, 30, 1, Color.yellow));
			}
		}
	}
	/*
	 * enemies coming from top right corner, moving around the map in an N shape
	 */
	private void waveN() { 
		if (delay(DELAY_WAVES, .5f) && ++count < 10) // 10 times
			enemy.add(new Enemy(WINDOW_WIDTH / 10 * 9, WINDOW_START_Y, 30, 30, 2, Color.blue));
	}
	
	/*
	 * enemies descending from top to bottom, starting from left to right
	 */
	private void waveRow() { 
		if (delay(DELAY_WAVES, 1f) && ++count < 10) // 10 times
			enemy.add(new Enemy((WINDOW_WIDTH - WINDOW_START_X) / 10 * count + 15, WINDOW_START_Y, 30, 30, 2, Color.pink)); 
	}

	/** 
	 * This method will draw everything 
	 */ 
	void draw() 
	{               
		Graphics g = getGraphics();
		Graphics bbg = backBuffer.getGraphics();
		
		drawBackground(bbg); // background
		for (Enemy en : enemy) // enemies
			en.draw(bbg);
		for (Item mon : money) // money
			mon.draw(bbg);
		for (Shot shot : player.getShot()) // shots
			shot.draw(bbg);
		drawLeftArea(bbg, player); // left area
		drawTopArea(bbg, player); // top area
		player.draw(bbg); // player
		drawPause(bbg); // pause
		
		g.drawImage(backBuffer, insets.left, insets.top, this);
	}
	/*
	 * draws the pause whenever pausing the game
	 */
	private void drawPause(Graphics bbg) {
		if (pause) {
			bbg.setFont(new Font("Arial", Font.BOLD, 32));
			bbg.setColor(Color.black);
			bbg.drawString("PAUSED", (WINDOW_WIDTH + WINDOW_START_X) / 2 - 86, WINDOW_HEIGHT / 2);
		}
	}
	/*
	 * draw the entire top area
	 */
	private void drawTopArea(Graphics bbg, Player player) {
		// top area color
		bbg.setColor(new Color(253, 227, 240)); // bright pale rose color
		bbg.fillRect(0, 0, WINDOW_WIDTH, WINDOW_START_Y);
		// top area - line
		bbg.setColor(Color.BLACK);
		bbg.drawLine(0, WINDOW_START_Y, WINDOW_WIDTH, WINDOW_START_Y);
		// top area - time
		bbg.setFont(new Font("Arial", Font.BOLD, 14));
		bbg.drawString("Time: " + time, 165, 17);
		// top area - money
		bbg.setColor(new Color(0, 100, 0));
		bbg.drawString("$ ", 240, 17);
		bbg.setColor(Color.black);
		bbg.drawString(": " + player.getMoney(), 250, 17);
	}
	/* 
	 * draw the entire left area
	 */
	private void drawLeftArea(Graphics bbg, Player player) {
		bbg.setFont(new Font("Arial", Font.BOLD, 14));
		// left area - color
		bbg.setColor(new Color(253, 227, 240)); // bright pale rose color
		bbg.fillRect(0, 0, WINDOW_START_X, WINDOW_HEIGHT);
		// left area - line
		bbg.setColor(Color.BLACK);
		bbg.drawLine(WINDOW_START_X, WINDOW_START_Y, WINDOW_START_X, WINDOW_HEIGHT);
		if (pause && !items[ITEM_STORE_PAUSE]) // don't show the store if paused
			return;
		for (int i = 1; i <= 6; i++) { // boxes and prices of each item
			if (i == 5 && !items[ITEM_VET_SHOPPER])
				break;
			bbg.drawRect(10, 60 * i, 30 , 30);
			bbg.setColor(player.getMoney() >= (Game.items[Game.ITEM_HALF_PRICE] ? (i == 4 ? 25 : 50) : (i == 4 ? 50 : 100)) ? Color.cyan : Color.gray);
			bbg.fillRect(10, 60 * i, 30, 30); 
			bbg.setColor(Color.BLACK);
			bbg.drawString((i == 4 ? "50$" : "100$"), (i == 4 ? 19 : 11), 45 + 60 * i);
			if (Game.items[Game.ITEM_HALF_PRICE]) {
				bbg.drawLine((i == 4 ? 19 : 11), 40 + 60 * i, 39, 40 + 60 * i);
				bbg.setColor(Color.red);
				bbg.setFont(new Font("Arial", Font.PLAIN, 12));
				bbg.drawString((i == 4 ? "25$" : "50$"), 21, 55 + 60 * i);
				bbg.setColor(Color.black);
				bbg.setFont(new Font("Arial", Font.BOLD, 14));
			}
		}
		// draws the "guns" - a sword
		bbg.drawPolygon(new int [] {16, 35, 37, 30, 13}, // the sword base
						new int [] {87, 75, 66, 66, 84}, 5);
		bbg.drawLine(17, 75, 26, 84); // the ring
		bbg.drawLine(21, 80, 31, 71); // the line across sword
		
		// draws the armor - a shield
		bbg.drawPolygon(new int [] {16 , 35 , 34 , 25 , 16 , 15 , 35}, // the shield
						new int [] {140, 123, 140, 148, 140, 123, 123}, 7);
		bbg.drawLine(15, 123, 34, 140); // the line from top left to bottom right
		
		// draw the engine
		bbg.drawRoundRect(15, 182, 8, 20, 5, 5); // left box
		bbg.drawRoundRect(27, 182, 8, 20, 5, 5); // right box
		bbg.drawLine(23, 192, 27, 192); // connecting line
		bbg.drawPolygon(new int [] {17 , 15 , 23 , 21}, // left boost
						new int [] {202, 205, 205, 202}, 4);
		bbg.drawPolygon(new int [] {29 , 27 , 35 , 33}, // right boost
						new int [] {202, 205, 205, 202}, 4);
		// draws the bombs
		bbg.drawOval(13, 248, 20, 20); // the bomb
		bbg.drawPolygon(new int [] {30 , 33 , 36 , 38}, // the thread
						new int [] {252, 246, 252, 245}, 4);
		if (items[ITEM_VET_SHOPPER]) {
			// draw artifact here

			// draws the patterns
			bbg.drawOval(13, 363, 24, 24); // the circle
			bbg.drawLine(15, 375, 35, 375); // the line from left to right
			bbg.drawLine(25, 365, 25, 385); // the line from up to down
			for (int i = 0; i < 5; i++) { // the little lines
				bbg.drawLine(24, 365 + 5 * i, 26, 365 + 5 * i);
				bbg.drawLine(15 + 5 * i, 374, 15 + 5 * i, 376);
			}
		}
	}
	
	/*
	 * draws the background - duh
	 */
	private void drawBackground(Graphics bbg) {
		bbg.setColor(Color.cyan); 
		bbg.fillRect(0, WINDOW_START_Y, WINDOW_WIDTH, WINDOW_HEIGHT);	
	}

	/**
	 *  returns the current location of the mouse
	 *  @return The current location of the mouse
	 */
	public Point getMousePoint() {
		Point mouseP = MouseInfo.getPointerInfo().getLocation();
		javax.swing.SwingUtilities.convertPointFromScreen(mouseP, this);
		return mouseP;
	}
	
	/**
	 * Delays something for a given amount of seconds.
	 * Once the delay is over, reset the delay count.
	 * @param i The index of the specific thing to delay
	 * @param sec The amount of seconds to delay
	 * @return true if delay is over, otherwise false
	 */
	public static boolean delay(int i, float sec) {
		if (System.currentTimeMillis() - timeOfTotalPause - delay[i] > sec * 1000) {
			delay[i] = System.currentTimeMillis() - timeOfTotalPause;
			return true;
		}
		return false;
	}
}
