package com.joseph.gametemplate.gameobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

import com.joseph.gametemplate.interfaces.IWaypointListener;
import com.joseph.gametemplate.math.DPoint;
import com.joseph.gametemplate.math.MathHelper;
import com.joseph.gametemplate.math.physics.Vector;

public class TestTarget extends GameObject implements IWaypointListener {
	private Vector movementVector;
	private DPoint waypoint;
	
	public TestTarget() {
		this (1500, 600);
	}
	
	public TestTarget(double x, double y) {
		super(x, y);
		this.waypoint = new DPoint(x, y);
		this.movementVector = new Vector(0, 0);
	}

	@Override
	public void draw(Graphics2D g, ImageObserver observer) {
		g.setColor(Color.green);
		g.fillRect((int) this.x - 2, (int) this.y - 2, 5, 5);
		g.setColor(Color.red);
		movementVector = movementVector.multiply(100);
		g.drawLine((int) x, (int) y, (int) (x + movementVector.getI()), (int) (y + movementVector.getJ()));
	}
	
	@Override
	public void update(double deltaTime) {
		if ((int) this.x == (int) waypoint.getX() && (int) this.y == (int) waypoint.getY()) {
			this.movementVector = new Vector(0, 0);
		} else {
			this.movementVector = new Vector(1, MathHelper.getAngleRad(x, y, waypoint));
		}
		this.x += movementVector.getI();
		this.y += movementVector.getJ();
	}

	@Override
	public void updateWaypoint(DPoint waypoint) {
		this.waypoint = waypoint;
	}
	
	public boolean coliding(Projectile p) {
		Rectangle2D r = new Rectangle2D.Double(x - 2, y - 2, 5, 5);
		return r.contains(p.getX(), p.getY());
	}
}