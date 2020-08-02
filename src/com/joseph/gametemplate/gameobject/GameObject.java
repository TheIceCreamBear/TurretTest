package com.joseph.gametemplate.gameobject;

import com.joseph.gametemplate.engine.GameEngine;
import com.joseph.gametemplate.interfaces.IDrawable;
import com.joseph.gametemplate.interfaces.IUpdateable;
import com.joseph.gametemplate.math.DPoint;

/**
 * Abstract object used mainly for storage of objects in {@link GameEngine} and to 
 * combine {@link IUpdateable} and {@link IDrawable} objects together.
 * 
 * @author Joseph
 * @see IUpdateable
 * @see IDrawable
 *
 */
public abstract class GameObject implements IDrawable, IUpdateable {
	protected double x;
	protected double y;
	
	public GameObject(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public DPoint getLocation() {
		return new DPoint(x, y);
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
}
