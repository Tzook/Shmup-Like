import java.awt.Component;
import java.awt.event.*;

/**
 * Makes handling input a lot simpler 
 */ 
public class InputHandler implements KeyListener, MouseListener
{
	boolean [] keys = new boolean [256];
	
    
	/** 
	 * Assigns the newly created InputHandler to a Component 
	 * @param c Component to get input from 
	 */ 
	public InputHandler(Component c) 
	{ 
		c.addKeyListener(this); 
		c.addMouseListener(this);
	}
	
	/** 
	 * Checks whether a specific key is down 
	 * @param keyCode The key to check 
	 * @return Whether the key is pressed or not 
	 */ 
	public boolean isKeyDown(int keyCode) 
	{ 
		return isInRange(keyCode) ? keys[keyCode] : false;           
	}
	
	/** 
	 * Called when a key is pressed while the component is focused 
	 * @param e KeyEvent sent by the component 
	 */ 
	public void keyPressed(KeyEvent e) 
	{ 
		if (isInRange(e.getKeyCode()))
			keys[e.getKeyCode()] = true;
	} 

	/** 
	 * Called when a key is released while the component is focused 
	 * @param e KeyEvent sent by the component 
	 */ 
	public void keyReleased(KeyEvent e) 
	{ 
		if (isInRange(e.getKeyCode())) 
			keys[e.getKeyCode()] = false;
	} 

	/** 
	 * checks whether a specific key is in the keys range 
	 * @param key The key to check 
	 * @return Whether the key is in range or not 
	 */ 
	private boolean isInRange(int key)
	{
		return (key > 0 && key < 256 ? true : false);
	}
	
	/** 
	 * lets the program know if any item was purchased
	 * @param e MousEvent sent by the component
	 */
	public void mouseClicked(MouseEvent e) {
		if (Game.pause && !Game.items[Game.ITEM_STORE_PAUSE]) // don't let buy if paused
			return;
		if (e.getX() > 9 && e.getX() < 41) {
			if (e.getY() > 60 && e.getY() < 90   && Game.delay(Game.DELAY_BUY, .5f))   // items 0-14 are GUNS
				Game.buy = 1;
			if (e.getY() > 120 && e.getY() < 150 && Game.delay(Game.DELAY_BUY, .5f)) // items 15-29 are ARMORS
				Game.buy = 2;
			if (e.getY() > 180 && e.getY() < 230 && Game.delay(Game.DELAY_BUY, .5f)) // items 30-44 are ENGINES
				Game.buy = 3;
			if (e.getY() > 240 && e.getY() < 270 && Game.delay(Game.DELAY_BUY, .5f)) // items 75-89 are BOMBS
				Game.buy = 6;
			if (e.getY() > 300 && e.getY() < 330 && Game.delay(Game.DELAY_BUY, .5f)) // items 45-59 are ARTIFACTS
				Game.buy = 4;
			if (e.getY() > 360 && e.getY() < 390 && Game.delay(Game.DELAY_BUY, .5f)) // items 60-74 are PATTERNS
				Game.buy = 5;
		}
	}

	/** 
	 * Not used 
	 */ 
	public void keyTyped(KeyEvent e){}
	/** 
	 * Not used 
	 */ 
	public void mouseEntered(MouseEvent e) { }

	/** 
	 * Not used 
	 */ 
	public void mouseExited(MouseEvent e) {	}

	/** 
	 * Not used 
	 */ 
	public void mousePressed(MouseEvent e) { } 

	/** 
	 * Not used 
	 */ 
	public void mouseReleased(MouseEvent e) { }
} 
