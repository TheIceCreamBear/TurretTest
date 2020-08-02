package com.joseph.gametemplate.gameobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

import com.joseph.gametemplate.engine.GameEngine;
import com.joseph.gametemplate.interfaces.IMouseReliant;
import com.joseph.gametemplate.math.DPoint;
import com.joseph.gametemplate.math.MathHelper;
import com.joseph.gametemplate.math.physics.Vector;
import com.joseph.gametemplate.reference.Reference;
import com.joseph.gametemplate.reference.ScreenReference;

public class CrazyTurret extends GameObject implements IMouseReliant {
	private Polygon shape = new Polygon(new int[] {-20, -20, 19, 19, 14, 14, 4, 4, -5, -5, -15, -15, -20, 19}, new int[] {-10, 19, 19, -10, -10, -20, -20, -10, -10, -20, -20, -10, -10, -10}, 14);
	private final Point[] shapePoints;
	private Point[] drawPoints;
	private GameObject target;
	private Vector movementVector;
	private Vector visualFiringVector;
	private DPoint drawPos;
	private double targetDegrees;
	private double degrees;
	private int fireCounter;
	private int burstLeft;
	
	public CrazyTurret() {
		this(1000, 1000);
	}
	
	public CrazyTurret(double x, double y) {
		super(x, y);
		this.shapePoints = new Point[shape.npoints];
		this.drawPoints = new Point[shapePoints.length];
		for (int i = 0; i < shapePoints.length; i++) {
			shapePoints[i] = new Point(shape.xpoints[i], shape.ypoints[i]);
			drawPoints[i] = new Point(shape.xpoints[i], shape.ypoints[i]);
		}
		this.fireCounter = 20;
		this.burstLeft = 3;
		this.visualFiringVector = new Vector(3000, 0);
		this.movementVector = new Vector(0, 0);
		this.degrees = 0;
		this.drawPos = this.getLocation();
	}
	
	@Override
	public void draw(Graphics2D g, ImageObserver observer) {
		double x = drawPos.getX();
		double y = drawPos.getY();
		g.setColor(Color.white);
		Polygon p = makePoly(drawPoints, x, y);
		g.drawPolygon(p);
		g.setColor(Color.red);
		if (Reference.DEBUG_MODE) {
			g.fillRect((int) x, (int) y, 1, 1);
			g.drawLine((int) x, (int) y, (int) (x + visualFiringVector.getI()), (int) (y + visualFiringVector.getJ()));
			g.setColor(Color.MAGENTA);
			g.draw(p.getBounds2D());
		}
		if (Reference.TEXT_DEBUG_DRAWING) {
			g.setColor(Color.green);
			g.drawString("" + this.degrees, (int) x, (int) y);
			g.drawString("" + this.targetDegrees, (int) x, (int) y + ScreenReference.charHeight);
		}
	}
	
	@Override
	public void update(double deltaTime) {
		this.x += movementVector.getI();
		this.y += movementVector.getJ();
//		this.target = GameEngine.getClosestTarget(drawPos);
//		if (this.target != null && this.target instanceof TestTarget) {
//			TestTarget tt = (TestTarget) this.target;
//			this.targetDegrees = getAngleWithLead(tt);
//			if (this.targetDegrees > 180 && this.targetDegrees <= 360) {
//				this.targetDegrees -= 180;
//			} else if (this.targetDegrees < -180) {
//				this.targetDegrees += 180;
//			}
//			
//			if (this.targetDegrees == 180 && this.degrees < 0) {
//				this.targetDegrees = -180;
//			}
//		}
		
//		this.degrees = getAngle(GameEngine.getInstance().getMouseLocation());
//		if (this.degrees != this.targetDegrees) {
//			if (Math.abs(this.degrees - this.targetDegrees) < 2.5) {
//				this.degrees = this.targetDegrees;
//			} else {
//				if (this.rotatePositive(degrees, targetDegrees)) {
////					this.degrees++;
//					this.degrees += 1.5;
//				} else {
////					this.degrees--;
//					this.degrees -= 1.5;
//				}
//				
//				if (this.degrees < -180) {
//					this.degrees += 360;
//				} else if (this.degrees > 180) {
//					this.degrees -= 360;
//				}
//			}
//		}
		this.degrees += Math.sqrt(2);
		
		this.rotatePoints();
		this.visualFiringVector = new Vector(3000, Math.toRadians(degrees));
		
//		this.fire();
	}
	
	@Override
	public boolean onMouseEvent(MouseEvent e) {
//		this.fire();
		return false;
	}
	
	public void setTarget(TestTarget obj) {
		this.target = obj;
		this.targetDegrees = this.getAngleWithLead(obj);
	}
	
	public void setMovementVector(Vector movementVector) {
		this.movementVector = movementVector;
	}
	
	public void setDrawPos(DPoint drawPos) {
		this.drawPos = drawPos;
	}
	
	private boolean rotatePositive(double a, double b) {
		b += 180;
		a += 180;
		return (b - a + 360) % 360 < 180;
	}
	
	private double getAngleWithLead(TestTarget tt) {
		Vector targetVel = tt.getMovementVector();
		DPoint dp = tt.getLocation().subtract(drawPos);
		Vector targetMinusThis = new Vector(dp);
		double a = MathHelper.square(Projectile.PROJECTILE_MAGNITUDE) - targetVel.dotProduct(targetVel);
		double b = -2 * targetVel.dotProduct(targetMinusThis);
		double c = -(targetMinusThis.dotProduct(targetMinusThis));		
		
		double d = b * b - 4 * a * c;
		
		if (d < 0) {
			return this.degrees;
		} else {
			double t0 = (-b - Math.sqrt(d)) / (2 * a);
			double t1 = (-b + Math.sqrt(d)) / (2 * a);
			
			double t = (t0 < 0) ? t1 : (t1 < 0) ? t0 : Math.min(t1, t0);
			DPoint pintercept = tt.getLocation().offest(tt.getMovementVector().multiply(t));
			return MathHelper.getAngle(drawPos, pintercept);
		}
	}	

	private void fire() {
		GameEngine.sapwnProjectile(new Projectile(Math.toRadians(degrees), drawPos, null));
	}
	
	private void rotatePoints() {
		AffineTransform.getRotateInstance(Math.toRadians(degrees + 90), 00, 00).transform(shapePoints, 0, drawPoints, 0, 14);
	}
	
	private Polygon makePoly(Point[] points, double x, double y) {
		Polygon poly = new Polygon();
		
		for (int i = 0; i < points.length; i++) {
			Point p = points[i];
			poly.addPoint(p.x, p.y);
		}
		
		poly.translate((int) x, (int) y);
		
		return poly;
	}
}