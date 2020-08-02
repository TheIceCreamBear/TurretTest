package com.joseph.gametemplate.math.physics;

import com.joseph.gametemplate.math.DPoint;
import com.joseph.gametemplate.math.MathHelper;

public class Vector {
	private double magnitude;
	private double angle;
	private double i;
	private double j;
	
	public Vector(double magnitude, double angle) {
		this.magnitude =  magnitude;
		this.angle = angle;
		this.i = this.magnitude * Math.cos(this.angle);
		this.j = this.magnitude * Math.sin(this.angle);
		checkAndMakeValid();
	}
	
	public Vector(DPoint p) {
		this.i = p.getX();
		this.j = p.getY();
		this.magnitude = Math.sqrt(MathHelper.square(this.i) + MathHelper.square(this.j));
		this.angle = Math.atan(this.j / this.i);
		checkAndMakeValid();
	}
	
	private Vector(Vector v1, Vector v2, boolean add) {
		if (add) {
			this.i = v1.i + v2.i;
			this.j = v1.j + v2.j;
			this.magnitude = Math.sqrt(Math.pow(this.i, 2) + Math.pow(this.j, 2));
			this.angle = Math.atan(this.j / this.i);
		} else {
			this.i = v1.i - v2.i;
			this.j = v1.j - v2.j;
			this.magnitude = Math.sqrt(Math.pow(this.i, 2) + Math.pow(this.j, 2));
			this.angle = Math.atan(this.j / this.i);
		}
		
		checkAndMakeValid();
	}
	
	private void checkAndMakeValid() {
		if (this.i == Double.NaN) {
			this.i = 0;
		}
		if (this.j == Double.NaN) {
			this.j = 0;
		}
		if (this.magnitude == Double.NaN) {
			this.magnitude = 0;
		}
		if (this.angle == Double.NaN) {
			this.angle = 0;
		}
	}
	
	public double getI() {
		return this.i;
	}
	
	public double getJ() {
		return this.j;
	}
	
	public double getAngle() {
		return this.angle;
	}
	
	public double getMagnitude() {
		return this.magnitude;
	}
	
	public double dotProduct(Vector other) {
		return i * other.i + j * other.j;
	}
	
	/**
	 * 
	 * @param other
	 * @return - a new vector 
	 */
	public Vector add(Vector other) {
		return new Vector(this, other, true);
	}
	
	public Vector subtract(Vector other) {
		return new Vector(this, other, false);
	}
	
	public Vector multiply(double scalar) {
		return new Vector(magnitude * scalar, angle);
	}
	
	public boolean movingTwoards(DPoint origin, DPoint movingObj) {
		return MathHelper.getDistance(origin, movingObj) < MathHelper.getDistance(origin, movingObj.offest(this));
	}
	
	@Override
	public String toString() {
		checkAndMakeValid();
		return super.toString() + " i:" + this.i + " j:" + this.j + " mag:" + this.magnitude + " ang:" + this.angle;
	}
}