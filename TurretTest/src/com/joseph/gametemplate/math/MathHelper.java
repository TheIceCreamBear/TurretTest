package com.joseph.gametemplate.math;

public class MathHelper {
	public static double square(double a) {
		return a * a;
	}
	
	public static double getDistanceSqrd(DPoint dp1, DPoint dp2) {
		return getDistanceSqrd(dp1.getX(), dp1.getY(), dp2.getX(), dp2.getY());
	}
	
	public static double getDistanceSqrd(double x1, double y1, double x2, double y2) {
		return square(x2 - x1) + square(y2 - y1);
	}
	
	public static double getDistance(DPoint dp1, DPoint dp2) {
		return Math.sqrt(getDistanceSqrd(dp1, dp2));
	}
	
	public static double getDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(getDistanceSqrd(x1, y1, x2, y2));
	}
	
	public static double getAngleRad(DPoint original, DPoint target) {
		return getAngleRad(original.getX(), original.getY(), target);
	}

	public static double getAngleRad(double x, double y, DPoint target) {
		if (target == null) {
			return 0.0;
		}
		
		return Math.atan2(target.getY() - y, target.getX() - x);
	}
	
	public static double getAngle(DPoint original, DPoint target) {
		return getAngle(original.getX(), original.getY(), target);
	}
	
	public static double getAngle(double x, double y, DPoint target) {
		if (target == null) {
			return 0.0;
		}
		
		return Math.toDegrees(Math.atan2(target.getY() - y, target.getX() - x));
	}	
}