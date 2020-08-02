package com.joseph.gametemplate.gameobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

import com.joseph.gametemplate.handlers.MouseHandler;
import com.joseph.gametemplate.interfaces.IWaypointListener;
import com.joseph.gametemplate.math.DPoint;
import com.joseph.gametemplate.math.MathHelper;
import com.joseph.gametemplate.math.physics.Vector;
import com.joseph.gametemplate.reference.Reference;

public class Ship extends GameObject implements IWaypointListener {
	public static final double SHIP_SLOW_SPEED = 1.5;
	public static final double SHIP_FAST_SPEED = 2;
	public static final double SHIP_MAX_SPEED = 25;
//	private Polygon shape = new Polygon(new int[] {0, 299, -300, 0}, new int[] {-400, 399, 399, -400}, 4);
	private Polygon shape = new Polygon(new int[] {0, 299, 49, 49, 99, 99, -100, -100, -50, -50, -300, 0}, new int[] {-400, 399, 399, 449, 449, 399, 399, 449, 449, 399, 399, -400}, 12);
	private final Point[] shapePoints;
	private Point[] drawPoints;
	private Vector accelerationVector;
	private Vector movementVector;
	private DPoint waypoint;
	private Turret portTurret;
	private Turret starbordTurret;
	private Turret portTurret1;
	private Turret starbordTurret1;
	private Turret portTurret2;
	private Turret starbordTurret2;
	private CrazyTurret ct;
	private Color c;
	private double facingDegrees;
	private double waypointDegrees;
	
	public Ship(double x, double y, Color c, int id) {
		super(x, y);
		this.shapePoints = new Point[shape.npoints];
		this.drawPoints = new Point[shapePoints.length];
		for (int i = 0; i < shapePoints.length; i++) {
			shapePoints[i] = new Point(shape.xpoints[i], shape.ypoints[i]);
			drawPoints[i] = new Point(shape.xpoints[i], shape.ypoints[i]);
		}
		this.movementVector = new Vector(0, 0);
		this.accelerationVector = new Vector(0, 0);
		this.waypoint = new DPoint(x, y);
		this.portTurret = new Turret(x - 100, y + 100, c, id);
		this.starbordTurret = new Turret(x + 100, y + 100, c, id+ 1);
		this.portTurret1 = new Turret(x - 150, y + 200, c, id+ 2);
		this.starbordTurret1 = new Turret(x + 150, y + 200, c, id+ 3);
		this.portTurret2 = new Turret(x - 200, y + 300, c, id+ 4);
		this.starbordTurret2 = new Turret(x + 200, y + 300, c, id+ 5);
		this.ct = new CrazyTurret(x, y);
		this.c = c;
	}

	@Override
	public void draw(Graphics2D g, ImageObserver observer) {
		g.setColor(c);
		Polygon p = makePoly(drawPoints);
		g.drawPolygon(p);
		if (Reference.DEBUG_MODE) {
			g.setColor(Color.red);
			g.fillRect((int) this.x, (int) this.y, 1, 1);
			g.setColor(Color.GREEN);
			Vector visVec = this.movementVector.multiply(100);
			g.drawLine((int) x, (int) y, (int) (x + visVec.getI()), (int) (y + visVec.getJ()));
			g.setColor(Color.MAGENTA);
			g.draw(p.getBounds2D());
		}
		if (Reference.TEXT_DEBUG_DRAWING) {
			g.setColor(Color.green);
			g.drawString("" + this.facingDegrees, (int) x, (int) y);
		}
			
		this.portTurret.draw(g, observer);
		this.starbordTurret.draw(g, observer);
		this.portTurret1.draw(g, observer);
		this.starbordTurret1.draw(g, observer);
		this.portTurret2.draw(g, observer);
		this.starbordTurret2.draw(g, observer);
		this.ct.draw(g, observer);
	}
	
	@Override
	public void update(double deltaTime) {
		double distance = MathHelper.getDistance(getLocation(), waypoint);
		if (distance > this.movementVector.getMagnitude()) {
			this.waypointDegrees = MathHelper.getAngle(getLocation(), waypoint);
			if (this.waypointDegrees == 180 && this.facingDegrees < 0) {
				this.waypointDegrees = -180;
			}
			
			if (this.facingDegrees != this.waypointDegrees) {
				if (Math.abs(this.facingDegrees - this.waypointDegrees) < 0.75) {
					this.facingDegrees = this.waypointDegrees;
				} else {
					if (this.rotatePositive(facingDegrees, waypointDegrees)) {
						this.facingDegrees++;
//						this.facingDegrees += 0.5;
					} else {
						this.facingDegrees--;
//						this.facingDegrees -= 0.5;
					}
					
					if (this.facingDegrees < -180) {
						this.facingDegrees += 360;
					} else if (this.facingDegrees > 180) {
						this.facingDegrees -= 360;
					}
				}
			}
			
			if (Math.abs(this.facingDegrees - this.waypointDegrees) < 2.5) {
				// This should actually be an acceleration vector that adds to the movement vector
				this.accelerationVector = new Vector(SHIP_FAST_SPEED, Math.toRadians(facingDegrees));
			} else if (Math.abs(this.facingDegrees - this.waypointDegrees) < 30) {
				this.accelerationVector = new Vector(SHIP_SLOW_SPEED, Math.toRadians(facingDegrees));
			} else {
				this.accelerationVector = new Vector(0, 0);
			}
			
		} else {
			if (this.movementVector.getMagnitude() != 0) {
				this.accelerationVector = new Vector(0, 0).subtract(movementVector.multiply(.5));
			}
			if (MathHelper.getDistance(getLocation(), waypoint) < 5) {
				this.accelerationVector = new Vector(0, 0).subtract(movementVector);
				this.waypoint = getLocation();
			}
		}
		
		if (this.accelerationVector.getMagnitude() > 0 /*&& this.movementVector.getMagnitude() < SHIP_MAX_SPEED*/) {
			this.movementVector = this.movementVector.add(accelerationVector);
		}
		
		this.x += movementVector.getI();
		this.y += movementVector.getJ();
		
		this.rotatePoints();
		
		this.portTurret.setMovementVector(movementVector);
		this.starbordTurret.setMovementVector(movementVector);
		this.portTurret1.setMovementVector(movementVector);
		this.starbordTurret1.setMovementVector(movementVector);
		this.portTurret2.setMovementVector(movementVector);
		this.starbordTurret2.setMovementVector(movementVector);
		this.ct.setMovementVector(movementVector);
		
		this.portTurret.update(deltaTime);
		this.starbordTurret.update(deltaTime);
		this.portTurret1.update(deltaTime);
		this.starbordTurret1.update(deltaTime);
		this.portTurret2.update(deltaTime);
		this.starbordTurret2.update(deltaTime);
		this.ct.update(deltaTime);
	}
	
	private void checkValidVector(Vector v) {
		if (v.getI() == Double.NaN) {
			throw new RuntimeException("ITS NAN");
		}
	}
	
	@Override
	public void updateWaypoint(DPoint waypoint) {
		this.waypoint = waypoint;
		this.waypoint = new DPoint(this.waypoint.getX() * 8, this.waypoint.getY() * 8);
		this.waypointDegrees = MathHelper.getAngle(getLocation(), waypoint);
	}
	
	public Turret[] getTurrets() {
		return new Turret[] {portTurret, starbordTurret, portTurret1, starbordTurret1, portTurret2, starbordTurret2};
	}
	
	public void registerTurrets(MouseHandler handler) {
		handler.registerMouseReliant(portTurret);
		handler.registerMouseReliant(portTurret1);
		handler.registerMouseReliant(portTurret2);
		handler.registerMouseReliant(starbordTurret);
		handler.registerMouseReliant(starbordTurret1);
		handler.registerMouseReliant(starbordTurret2);
	}
	
	private boolean rotatePositive(double a, double b) {
		b += 180;
		a += 180;
		return (b - a + 360) % 360 < 180;
	}
	
	private void rotatePoints() {
		AffineTransform.getRotateInstance(Math.toRadians(facingDegrees + 90), 00, 00).transform(shapePoints, 0, drawPoints, 0, shape.npoints);
		AffineTransform turretRotate = AffineTransform.getRotateInstance(Math.toRadians(facingDegrees + 90), this.x, this.y);
		DPoint port = portTurret.getLocation();
		DPoint star = starbordTurret.getLocation();
		turretRotate.transform(port, port);
		turretRotate.transform(star, star);
		this.portTurret.setDrawPos(port);
		this.starbordTurret.setDrawPos(star);
		
		DPoint port1 = portTurret1.getLocation();
		DPoint star1 = starbordTurret1.getLocation();
		turretRotate.transform(port1, port1);
		turretRotate.transform(star1, star1);
		this.portTurret1.setDrawPos(port1);
		this.starbordTurret1.setDrawPos(star1);
		
		DPoint port2 = portTurret2.getLocation();
		DPoint star2 = starbordTurret2.getLocation();
		turretRotate.transform(port2, port2);
		turretRotate.transform(star2, star2);
		this.portTurret2.setDrawPos(port2);
		this.starbordTurret2.setDrawPos(star2);
		
		DPoint ctp = ct.getLocation();
		turretRotate.transform(ctp, ctp);
		this.ct.setDrawPos(ctp);
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