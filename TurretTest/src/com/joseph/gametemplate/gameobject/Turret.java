package com.joseph.gametemplate.gameobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;

import com.joseph.gametemplate.engine.GameEngine;
import com.joseph.gametemplate.interfaces.IMouseReliant;
import com.joseph.gametemplate.math.DPoint;
import com.joseph.gametemplate.math.MathHelper;
import com.joseph.gametemplate.math.physics.Vector;
import com.joseph.gametemplate.reference.Reference;
import com.joseph.gametemplate.reference.ScreenReference;

public class Turret extends GameObject implements IMouseReliant {
	private Polygon shape = new Polygon(new int[] {-20, -20, 19, 19, 14, 14, 4, 4, -5, -5, -15, -15, -20, 19}, new int[] {-10, 19, 19, -10, -10, -20, -20, -10, -10, -20, -20, -10, -10, -10}, 14);
	private final Point[] shapePoints;
	private Point[] drawPoints;
	private GameObject target;
	private Vector movementVector;
	private Vector firingVector;
	private Vector visualFiringVector;
	private double targetDegrees;
	private double targetDegreesWithLead;
	private double previousDegWithLead;
	private double lead;
	private double degrees;
	private int fireCounter;
	private int burstLeft;
	
	public Turret() {
		this(1000, 1000);
	}
	
	public Turret(double x, double y) {
		super(x, y);
		this.shapePoints = new Point[shape.npoints];
		this.drawPoints = new Point[shapePoints.length];
		for (int i = 0; i < shapePoints.length; i++) {
			shapePoints[i] = new Point(shape.xpoints[i], shape.ypoints[i]);
			drawPoints[i] = new Point(shape.xpoints[i], shape.ypoints[i]);
		}
		this.fireCounter = 200;
		this.burstLeft = 3;
		this.visualFiringVector = new Vector(3000, 0);
		this.movementVector = new Vector(0, 0);
		this.degrees = 0;
	}
	
	@Override
	public void draw(Graphics2D g, ImageObserver observer) {
		g.setColor(Color.white);
		g.drawPolygon(makePoly(drawPoints));
		g.setColor(Color.red);
		g.fillRect((int) this.x, (int) this.y, 1, 1);
		if (Reference.DEBUG_MODE) {
			g.drawLine((int) x, (int) y, (int) (x + visualFiringVector.getI()), (int) (y + visualFiringVector.getJ()));
			g.setColor(Color.green);
			g.drawString("" + this.degrees, (int) x, (int) y);
			g.drawString("" + this.targetDegreesWithLead, (int) x, (int) y + ScreenReference.charHeight);
			
		}
	}
	
	@Override
	public void update(double deltaTime) {
		this.x += movementVector.getI();
		this.y += movementVector.getJ();
		this.target = GameEngine.getClosestTarget(getLocation());
		if (this.target != null && this.target instanceof TestTarget) {
			TestTarget tt = (TestTarget) this.target;
			this.targetDegreesWithLead = getAngleNew(tt);
			/*  = = = = = = = = = = = = = OLD
			this.targetDegrees = getAngle(target.getLocation());
			if (tt.getMovementVector().getMagnitude() != 0) {
				this.lead = this.getLead(tt);
				this.targetDegreesWithLead = this.targetDegrees + this.lead;
//				this.targetDegrees = getAngle((TestTarget) this.target);
			} else {
				this.targetDegreesWithLead = this.targetDegrees;
			}
			System.err.println(this.targetDegreesWithLead - this.previousDegWithLead);
			*/
			if (this.targetDegreesWithLead > 180 && this.targetDegreesWithLead <= 360) {
				this.targetDegreesWithLead -= 180;
			} else if (this.targetDegreesWithLead < -180) {
				this.targetDegreesWithLead += 180;
			}
			
			if (this.targetDegreesWithLead == 180 && this.degrees < 0) {
				this.targetDegreesWithLead = -180;
			}
		}
//		this.degrees = getAngle(GameEngine.getInstance().getMouseLocation());
		if (this.degrees != this.targetDegreesWithLead) {
			if (Math.abs(this.degrees - this.targetDegreesWithLead) < 2.5) {
				this.degrees = this.targetDegreesWithLead;
			} else {
				if (this.movePositive(degrees, targetDegreesWithLead)) {
//					this.degrees++;
					this.degrees += 1.5;
				} else {
//					this.degrees--;
					this.degrees -= 1.5;
				}
				
				if (this.degrees < -180) {
					this.degrees += 360;
				} else if (this.degrees > 180) {
					this.degrees -= 360;
				}
			}
		}
		boolean targetLocked = this.targetDegreesWithLead == this.degrees;
		
		this.rotatePoints();
		this.visualFiringVector = new Vector(3000, Math.toRadians(degrees));
		
		if (this.fireCounter > 0) {
			this.fireCounter--;
		} else if (this.fireCounter == 0) {
			if (targetLocked) {
				this.fire();
				if (this.burstLeft > 0) {
					this.burstLeft--;
					this.fireCounter = 15;
				} else if (this.burstLeft == 0) {
					this.burstLeft = 3;
					this.fireCounter = 120;
				}
			}
		}
		this.previousDegWithLead = this.targetDegreesWithLead;
	}
	
	@Override
	public boolean onMouseEvent(MouseEvent e) {
//		this.fire();
		return false;
	}
	
	public void setTarget(GameObject obj) {
		this.target = obj;
		this.targetDegreesWithLead = this.getAngle(obj.getLocation());
	}
	
	public void setMovementVector(Vector movementVector) {
		this.movementVector = movementVector;
	}
	
	private boolean movePositive(double a, double b) {
		b += 180;
		a += 180;
		return (b - a + 360) % 360 < 180;
	}
	
	private double getAngle(DPoint target) {
		if (target == null) {
			return 0.0;
		}
		
		return Math.toDegrees(Math.atan2(target.getY() - this.y, target.getX() - this.x));
	}
	
	private double getAngle2(TestTarget tt) {
		double w = Projectile.PROJECTILE_MAGNITUDE;
		DPoint p = tt.getLocation();
		DPoint o = getLocation();
		Vector v = tt.getMovementVector();
		Vector distance = new Vector(p.subtract(o));
		double a = v.dotProduct(v) - (w * w);
		double b = MathHelper.square(v.dotProduct(distance));
		double c = distance.dotProduct(distance);
		
		double d = b * b - 4 * a * c;
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
		System.out.println(d);
		if (d < 0) {
			System.err.println("OJYGAGSDOUYFSADOUYASDOUYG");
			return this.degrees;
		} else {
			double t0 = (-b - Math.sqrt(d)) / (2 * a);
			double t1 = (-b + Math.sqrt(d)) / (2 * a);
			
			double t = (t0 < 0) ? t1 : (t1 < 0) ? t0 : Math.min(t1, t0);
			DPoint pintercept = tt.getLocation().offest(v.multiply(t));
			return MathHelper.getAngle(getLocation(), pintercept);
		}
	}
	
	private double getAngleNew2(TestTarget tt) {
		Vector ba = new Vector(tt.getLocation().subtract(getLocation()));
		System.err.println(ba);
		double vxv = tt.getMovementVector().dotProduct(tt.getMovementVector());
		System.err.println(tt.getMovementVector());
		System.err.println(vxv);
		double b = ba.dotProduct(tt.getMovementVector());
		System.err.println(b);
		double a = Projectile.PROJECTILE_MAGNITUDE * Projectile.PROJECTILE_MAGNITUDE - vxv;
		System.err.println(a);
		double quadSol = b + Math.sqrt(b*b + a * ba.dotProduct(ba)) / a;
		System.err.println(quadSol);
		DPoint intercept = tt.getLocation().offest(tt.getMovementVector().multiply(quadSol));
		System.err.println(intercept);
		
		return MathHelper.getAngle(getLocation(), intercept);
	}
	
	private double getAngleNew(TestTarget tt) {
		Vector targetVel = tt.getMovementVector();
		DPoint dp = tt.getLocation().subtract(getLocation());
		Vector targetMinusThis = new Vector(dp);
		double a = MathHelper.square(Projectile.PROJECTILE_MAGNITUDE) - targetVel.dotProduct(targetVel);
		double b = -2 * targetVel.dotProduct(targetMinusThis);
		double c = -(targetMinusThis.dotProduct(targetMinusThis));		
		
//		double quadSol = solveQuad(a, b, c);
		double d = b * b - 4 * a * c;
		if (d < 0) {
			System.err.println("OJYGAGSDOUYFSADOUYASDOUYG");
			return this.degrees;
		} else {
			double t0 = (-b - Math.sqrt(d)) / (2 * a);
			double t1 = (-b + Math.sqrt(d)) / (2 * a);
			
			double t = (t0 < 0) ? t1 : (t1 < 0) ? t0 : Math.min(t1, t0);
			DPoint pintercept = tt.getLocation().offest(tt.getMovementVector().multiply(t));
			return MathHelper.getAngle(getLocation(), pintercept);
		}
//		Vector v = targetVel.multiply(quadSol);
//		DPoint p = tt.getLocation().offest(v);
//		return MathHelper.getAngle(getLocation(), p);
		
		
//		System.err.println(targetVel);
//		System.err.println(tt.getLocation());
//		System.err.println(this.getLocation());
//		System.err.println(dp);
//		System.err.println(targetMinusThis);
//		System.err.println(a);
//		System.err.println(b);
//		System.err.println(c);
//		System.err.println("quadSol:" + quadSol);
//		System.err.println(v);
//		System.err.println(p);
		
//		Vector intercept = new Vector(p.subtract(getLocation()));
//		Vector finalV = intercept.multiply(Projectile.PROJECTILE_MAGNITUDE / Math.sqrt(intercept.dotProduct(intercept)));
//		return finalV.getAngle();
		
	}
	
	private double solveQuad(double a, double b, double c) {
		return (b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
	}
	
	private double getLead(TestTarget tt) {
		Vector targetVel = tt.getMovementVector();
		Vector targetMinusThis = new Vector(tt.getLocation().subtract(getLocation()));
		double a = MathHelper.square(Projectile.PROJECTILE_MAGNITUDE) - targetVel.dotProduct(targetVel);
		double b = -2 * targetVel.dotProduct(targetMinusThis);
		double c = -(targetMinusThis.dotProduct(targetMinusThis));
		
		DPoint p = tt.getLocation().offest(targetVel.multiply(solveQuad(a, b, c)));
		
//		return MathHelper.getAngle(getLocation(), tt.getLocation()) - MathHelper.getAngle(getLocation(), p);
		return MathHelper.getAngle(getLocation(), p) - MathHelper.getAngle(getLocation(), tt.getLocation());
	}

	private void fire() {
		GameEngine.sapwnProjectile(new Projectile(Math.toRadians(degrees), getLocation(), this.target));
		
	}
	
	private void rotatePoints() {
		AffineTransform.getRotateInstance(Math.toRadians(degrees + 90), 00, 00).transform(shapePoints, 0, drawPoints, 0, 14);
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