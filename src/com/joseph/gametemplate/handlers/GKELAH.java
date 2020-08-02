package com.joseph.gametemplate.handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import com.joseph.gametemplate.engine.GameEngine;
import com.joseph.gametemplate.math.physics.Vector;
import com.joseph.gametemplate.reference.Reference;

/**
 * GKELAH, or GlobalKeyEventListenerrAndHandler, is a key event handler that listens for all
 * key events and does a specific action based on the state of the engine and the key pressed.
 * Used for KeyStroke logging for text typing or for special key that must perform a specific 
 * action the moment they are pressed as opposed to waiting for the next update cycle of the 
 * object that will be using that special key.
 * 
 * <p>For legacy input, use {@link InputHandler InputHandler}.
 * 
 * @author Joseph
 * @see InputHandler
 */
public class GKELAH implements KeyListener {
	public GKELAH() {
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
		
		if (e.getKeyCode() == KeyEvent.VK_F1) {
			Reference.DEBUG_MODE = !Reference.DEBUG_MODE;
			return;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_F2) {
			Reference.TEXT_DEBUG_DRAWING = !Reference.TEXT_DEBUG_DRAWING;
			return;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_N) {
			Random r = new Random();
//			GameEngine.populateNewTargets(r, r.nextInt(10) + 90);
		}
		
		switch (e.getKeyCode()) {
			case KeyEvent.VK_NUMPAD2:
				GameEngine.updateTargetVelocities(new Vector(1, Math.toRadians(90)));
				break;
			case KeyEvent.VK_NUMPAD4:
				GameEngine.updateTargetVelocities(new Vector(1, Math.toRadians(180)));
				break;
			case KeyEvent.VK_NUMPAD6:
				GameEngine.updateTargetVelocities(new Vector(1, Math.toRadians(0)));
				break;
			case KeyEvent.VK_NUMPAD8:
				GameEngine.updateTargetVelocities(new Vector(1, Math.toRadians(-90)));
				break;
			case KeyEvent.VK_NUMPAD5:
				GameEngine.updateTargetVelocities(new Vector(0, 0));
				break;
			default:
				break;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}