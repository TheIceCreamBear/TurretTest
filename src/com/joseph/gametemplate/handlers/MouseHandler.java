package com.joseph.gametemplate.handlers;

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import com.joseph.gametemplate.interfaces.IMouseReliant;
import com.joseph.gametemplate.interfaces.IWaypointListener;
import com.joseph.gametemplate.math.DPoint;

/**
 * Like {@link GKELAH} but for mouse events. Use to distribute the mouse events
 * to all of the objects that are registered to rely on mouse events
 * 
 * @author Joseph
 *
 */
public class MouseHandler implements MouseListener {
	private ArrayList<IMouseReliant> reliants;
	private ArrayList<IWaypointListener> waypoints;
	private boolean extraButtons;
	
	public MouseHandler() {
		this.reliants = new ArrayList<IMouseReliant>();
		this.waypoints = new ArrayList<IWaypointListener>();
		this.extraButtons = Toolkit.getDefaultToolkit().areExtraMouseButtonsEnabled();
	}
	
	public void registerMouseReliant(IMouseReliant imr) {
		synchronized (reliants) {
			this.reliants.add(imr);
		}
	}
	
	public boolean removeMouseReliant(IMouseReliant imr) {
		synchronized (reliants) {
			return this.reliants.remove(imr);
		}
	}
	
	public void registerWaypointListener(IWaypointListener iwl) {
		synchronized (waypoints) {
			this.waypoints.add(iwl);
		}
	}
	
	public boolean removeWaypointListener(IWaypointListener iwl) {
		synchronized (waypoints) {
			return this.waypoints.remove(iwl);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			synchronized (reliants) {
				for (IMouseReliant imr : reliants) {
					if (imr.onMouseEvent(e)) {
						break;
					}
				}
			}
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			synchronized (waypoints) {
				for (IWaypointListener listener : waypoints) {
					listener.updateWaypoint(new DPoint(e.getX(), e.getY()));
				}
			}
		} else {
			if (extraButtons) {
				if (e.getButton() == 4) {
					
				} else if (e.getButton() == 5) {
					
				} 
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		// NO-OP
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		// NO-OP
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// NO-OP
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		// NO-OP
	}
}