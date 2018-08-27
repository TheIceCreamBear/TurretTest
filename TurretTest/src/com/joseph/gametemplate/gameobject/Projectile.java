package com.joseph.gametemplate.gameobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;

import com.joseph.gametemplate.math.DPoint;
import com.joseph.gametemplate.math.MathHelper;
import com.joseph.gametemplate.math.physics.Vector;
import com.joseph.gametemplate.reference.ScreenReference;

public class Projectile extends GameObject implements Cloneable {
	public static final int PROJECTILE_MAGNITUDE = 5;
	private Vector v;
	private GameObject forDebugOnly;
	private boolean print = true;
	
	public Projectile(double angle, DPoint loc, GameObject target) {
		super(loc.getX(), loc.getY());
		this.v = new Vector(PROJECTILE_MAGNITUDE, angle);
		this.forDebugOnly = target;
	}
	
	private Projectile(Vector v, double x, double y, GameObject target) {
		super(x, y);
		this.v = v;
		this.forDebugOnly = target;
	}

	@Override
	public void draw(Graphics2D g, ImageObserver observer) {
		g.setColor(Color.cyan);
		g.fillRect((int) (x - 5), (int) (y - 5), 10, 10);
	}

	@Override
	public void update(double deltaTime) {
		x += v.getI();
		y += v.getJ();
//		if (print)
//			System.err.println(MathHelper.getDistance(getLocation(), forDebugOnly.getLocation()));
	}
	
	@Override
	public Projectile clone() throws CloneNotSupportedException {
		return new Projectile(v, x, y, forDebugOnly);
	}
	
	public void dontPrint() {
		this.print = false;
	}
	
	public boolean inGameMap() {
		return this.x + 5 >= 0 && this.x - 5 <= ScreenReference.WIDTH && this.y + 5 >= 0 && this.y - 5 <= ScreenReference.HEIGHT;
	}
}