package com.joseph.gametemplate.math;

import java.awt.geom.Point2D;

import com.joseph.gametemplate.math.physics.Vector;

public class DPoint extends Point2D {
	private double x;
	private double y;
	
	public DPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public DPoint add(DPoint p) {
		return new DPoint(x + p.x, y + p.y);
	}
	
	public DPoint subtract(DPoint p) {
		return new DPoint(x - p.x, y - p.y);
	}
	
	public DPoint offest(Vector v) {
		return new DPoint(x + v.getI(), y + v.getJ());
	}
	
	@Override
	public String toString() {
		return super.toString() + " x:" + this.x + " y:" + this.y;
	}

	@Override
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}
}