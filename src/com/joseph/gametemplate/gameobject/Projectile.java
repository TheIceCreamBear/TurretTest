package com.joseph.gametemplate.gameobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

import com.joseph.gametemplate.math.DPoint;
import com.joseph.gametemplate.math.physics.Vector;
import com.joseph.gametemplate.reference.ScreenReference;

public class Projectile extends GameObject implements Cloneable {
	public static final int PROJECTILE_MAGNITUDE = 50;
	private Vector v;
	private Turret firedFrom;
	
	public Projectile(double angle, DPoint loc, Turret firedFrom) {
		this(angle, loc, new Vector(0, 0), firedFrom);
	}
	
	public Projectile(double angle, DPoint loc, Vector turretMovement, Turret firedFrom) {
		super(loc.getX(), loc.getY());
		this.v = new Vector(PROJECTILE_MAGNITUDE, angle).add(turretMovement);
		this.firedFrom = firedFrom;
	}
	
	private Projectile(Vector v, double x, double y, Turret firedFrom) {
		super(x, y);
		this.v = v;
		this.firedFrom = firedFrom;
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
		return new Projectile(v, x, y, firedFrom);
	}
	
	public boolean inGameMap() {
		return this.x + 5 >= 0 && this.x - 5 <= ScreenReference.WIDTH * 2 * 8 && this.y + 5 >= 0 && this.y - 5 <= ScreenReference.HEIGHT * 2 * 8;
	}
	
	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(x, y, 10, 10);
	}

	public Turret getFiredFrom() {
		return this.firedFrom;
	}
}