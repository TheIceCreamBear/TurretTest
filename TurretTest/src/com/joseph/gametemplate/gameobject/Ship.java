package com.joseph.gametemplate.gameobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

import com.joseph.gametemplate.interfaces.IWaypointListener;
import com.joseph.gametemplate.math.DPoint;
import com.joseph.gametemplate.math.physics.Vector;
import com.joseph.gametemplate.reference.Reference;

public class Ship extends GameObject implements IWaypointListener {
	private Polygon shape = new Polygon(new int[] {0, 299, -300, 0}, new int[] {-400, 399, 399, -400}, 4);
	private final Point[] shapePoints;
	private Point[] drawPoints;
	private Vector movementVector;
	private DPoint waypoint;
	private double degrees;
	
	public Ship(double x, double y) {
		super(x, y);
		this.shapePoints = new Point[shape.npoints];
		this.drawPoints = new Point[shapePoints.length];
		for (int i = 0; i < shapePoints.length; i++) {
			shapePoints[i] = new Point(shape.xpoints[i], shape.ypoints[i]);
			drawPoints[i] = new Point(shape.xpoints[i], shape.ypoints[i]);
		}
		this.movementVector = new Vector(0, 0);
		this.waypoint = new DPoint(x, y);
	}

	@Override
	public void draw(Graphics2D g, ImageObserver observer) {
		g.setColor(Color.white);
		g.drawPolygon(makePoly(drawPoints));
		g.setColor(Color.red);
		g.fillRect((int) this.x, (int) this.y, 1, 1);
		if (Reference.DEBUG_MODE) {
			g.drawLine((int) x, (int) y, (int) (x + movementVector.getI()), (int) (y + movementVector.getJ()));
			g.setColor(Color.green);
			g.drawString("" + this.degrees, (int) x, (int) y);
			
		}
	}
	
	@Override
	public void update(double deltaTime) {
		
		this.rotatePoints();
	}
	
	@Override
	public void updateWaypoint(DPoint waypoint) {
		this.waypoint = waypoint;
	}
	
	private void rotatePoints() {
		AffineTransform.getRotateInstance(Math.toRadians(degrees), 00, 00).transform(shapePoints, 0, drawPoints, 0, 4);
	}
	
	private Polygon makePoly(Point[] points) {
		Polygon poly = new Polygon();
		
		for (int i = 0; i < points.length; i++) {
			Point p = points[i];
			poly.addPoint(p.x, p.y);
		}
		
		poly.translate((int) this.x, (int) this.y);
		
		return poly;
	}
}