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
	private int width = 5;
	private int height = 5;
	
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
		g.fillRect((int) this.x - (width / 2), (int) this.y - (height / 2), width, height);
		g.setColor(Color.red);
		Vector tempMovementVector = movementVector.multiply(100);
		g.drawLine((int) x, (int) y, (int) (x + tempMovementVector.getI()), (int) (y + tempMovementVector.getJ()));
	}
	
	@Override
	public void update(double deltaTime) {
		if ((int) Math.round(this.x) == (int) waypoint.getX() && (int) Math.round(this.y) == (int) waypoint.getY()) {
//			this.movementVector = new Vector(0, 0);
		} else {
//			this.movementVector = new Vector(1, MathHelper.getAngleRad(x, y, waypoint));
		}
		this.x += movementVector.getI();
		this.y += movementVector.getJ();
	}

	
	public Vector getMovementVector() {
		return this.movementVector;
	}
	
	@Override
	public void updateWaypoint(DPoint waypoint) {
//		this.waypoint = waypoint;
	}
	
	public boolean coliding(Projectile p) {
		Rectangle2D thisBou = new Rectangle2D.Double(this.x - (width / 2), this.y - (height / 2), width, height);
		Rectangle2D pBou = p.getBounds();
		boolean colliding = thisBou.getX() < (pBou.getX() + pBou.getWidth()) && (thisBou.getX() + thisBou.getWidth()) > pBou.getX() 
				&& thisBou.getY() < (pBou.getY() + pBou.getHeight()) && (thisBou.getY() + thisBou.getHeight()) > pBou.getY();
//		System.err.println(thisBou);
//		System.err.println(pBou);
//		System.err.println(colliding);
		return colliding;
	}
	
	public void setMovementVector(Vector movementVector) {
		this.movementVector = movementVector;
	}
}