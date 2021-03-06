package com.joseph.gametemplate.player;

import com.joseph.gametemplate.gameobject.GameObject;

/**
 * An abstract player class. Used to contain common types and methods used by various player objects.
 * 
 * @author Joseph
 * @author David Santamaria
 *
 */
public abstract class AbstractPlayer extends GameObject {
	public AbstractPlayer(double x, double y) {
		super(x, y);
	}

	protected enum EnumDirection {
		UP, DOWN, LEFT, RIGHT;
	}
}