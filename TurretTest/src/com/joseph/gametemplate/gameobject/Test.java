package com.joseph.gametemplate.gameobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

public class Test extends GameObject {
	private int radius;
	private int progress;
	private boolean animate;
	
	public Test() {
		super(300, 300);
		this.radius = 5;
		this.animate = true;
	}
	
	@Override
	public void draw(Graphics2D g, ImageObserver observer) {
		g.setColor(Color.RED);
		g.drawArc(300 - radius, 300 - radius, radius * 2, radius * 2, 0, 360);
//		g.fillArc(300 - radius, 300 - radius, radius * 2, radius * 2, 0, 180);
		g.fillRect(300, 300, 1, 1);
	}
	
	@Override
	public void update(double deltaTime) {
		if (this.animate && this.progress < 120) {
			this.progress++;
			this.radius++;
		}
		
		if (this.progress == 120) {
			this.animate = false;
		}
	}	
}