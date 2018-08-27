package com.joseph.gametemplate.gameobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import com.joseph.gametemplate.math.DPoint;
import com.joseph.gametemplate.math.physics.Vector;
import com.joseph.gametemplate.reference.ScreenReference;

public class Projectile extends GameObject implements Cloneable {
	public static final int PROJECTILE_MAGNITUDE = 5;
	private Vector v;
	
	public Projectile(double angle, DPoint loc) {
		this(angle, loc, new Vector(0, 0));
	}
	
	public Projectile(double angle, DPoint loc, Vector turretMovement) {
		super(loc.getX(), loc.getY());
		this.v = new Vector(PROJECTILE_MAGNITUDE, angle).add(turretMovement);
	}
	
	private Projectile(Vector v, double x, double y) {
		super(x, y);
		this.v = v;
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
	}
	
	@Override
	public Projectile clone() throws CloneNotSupportedException {
		return new Projectile(v, x, y);
	}
	
	public boolean inGameMap() {
		return this.x + 5 >= 0 && this.x - 5 <= ScreenReference.WIDTH && this.y + 5 >= 0 && this.y - 5 <= ScreenReference.HEIGHT;
	}
}