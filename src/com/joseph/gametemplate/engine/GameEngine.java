package com.joseph.gametemplate.engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import com.joseph.gametemplate.gameobject.GameObject;
import com.joseph.gametemplate.gameobject.Projectile;
import com.joseph.gametemplate.gameobject.RenderLockObject;
import com.joseph.gametemplate.gameobject.Ship;
import com.joseph.gametemplate.gameobject.TestTarget;
import com.joseph.gametemplate.gameobject.Turret;
import com.joseph.gametemplate.gui.IGuiElement;
import com.joseph.gametemplate.handlers.GKELAH;
import com.joseph.gametemplate.handlers.MouseHandler;
import com.joseph.gametemplate.interfaces.IDrawable;
import com.joseph.gametemplate.interfaces.IUpdateable;
import com.joseph.gametemplate.math.DPoint;
import com.joseph.gametemplate.math.MathHelper;
import com.joseph.gametemplate.math.physics.Vector;
import com.joseph.gametemplate.reference.Reference;
import com.joseph.gametemplate.reference.ScreenReference;
import com.joseph.gametemplate.threads.RenderThread;
import com.joseph.gametemplate.threads.ShutdownThread;

/**
 * Class responsible for doing all the heavy lifting in the game. Hold the engine 
 * Algorithm and references to all objects used by the game.
 * @author David Santamaria - Original Author
 * @author Joseph Terribile - Current Maintainer
 */
public class GameEngine {

	/**
	 * boolean that expressed the state of the engine, whether it is
	 * <code> running </code> or not
	 */
	private static boolean running = true;
	
	/**
	 * The instance of the GameEngine
	 */
	private static GameEngine instance;
	
	/**
	 * Displayed at the top of the screen. Expresses the fps, and time and other
	 * such things
	 */
	private static String stats = "";

	/**
	 * Used to display the screen
	 */
	private JFrame frame;

	/**
	 * First graphics instance
	 */
	private Graphics2D g;
	
	/**
	 * BufferedImage graphics instance
	 */
	private Graphics2D g2;
	
	/**
	 * Image that is displayed on the screen
	 */
	private BufferedImage i;
	
	/**
	 * Refrence to the FontRenderContext for the game, saved 
	 * in game engine for easy access.
	 */
	private FontRenderContext frc;

	// Threads
	private RenderLockObject rlo;
	private RenderThread rtInstance;
	private ShutdownThread sdtInstance;
	
	/**
	 * Instance of {@link GKELAH GKELAH} stored to keep a reference to it.
	 */
	private GKELAH keyHandlerInstance;
	
	/**
	 * Instance of the mouse handler object
	 */
	private static MouseHandler mouseHandlerInstace;

	/**
	 * ArrayList of GameObjects - to be looped over to update and draw
	 */
	private static ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	
	/**
	 * Only updatable objects, looped through to update
	 */
	private static ArrayList<IUpdateable> updateable = new ArrayList<IUpdateable>();
	
	/**
	 * Drawable only objects
	 */
	private static ArrayList<IDrawable> drawable = new ArrayList<IDrawable>();
	private static ArrayList<IGuiElement> guiElements = new ArrayList<IGuiElement>();
	private static ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	private static ArrayList<TestTarget> targets = new ArrayList<TestTarget>();
	private static ArrayList<Turret> team1Turrets = new ArrayList<Turret>();
	private static ArrayList<Turret> team2Turrets = new ArrayList<Turret>();
	
	private static Queue<Projectile> waitingAddProjectiles = new ArrayBlockingQueue<Projectile>(50, true);
	private static Queue<Projectile> waitingRemoveProjectiles = new ArrayBlockingQueue<Projectile>(50, true);
	private static Queue<TestTarget> waitingAddTargets = new ArrayBlockingQueue<TestTarget>(500, true);
	private static Queue<Turret> waitingRemoveTurrets1 = new ArrayBlockingQueue<Turret>(50, true);
	private static Queue<Turret> waitingRemoveTurrets2 = new ArrayBlockingQueue<Turret>(50, true);
	
	private int updateCounter;
	private int drawCounter;

	/**
	 * 
	 * @return the instance of the GameEngine
	 */
	public static GameEngine getInstance() {
		return instance;
	}

	/**
	 * 
	 * @return state of {@link GameEngine#running GameEngine.running}
	 */
	public static boolean isRunning() {
		return running;
	}

	public static void main(String[] args) {
		if (Reference.DEBUG_MODE) {
			System.out.println(Runtime.getRuntime().maxMemory());
			System.err.println("x: " + ScreenReference.WIDTH + "y: " + ScreenReference.HEIGHT);
		}
		instance = new GameEngine();
		instance.run();
	}

	/**
	 * Starts the GameEngine
	 */
	public static void startGameEngine() {
		instance = new GameEngine();
		instance.run();
	}

	/**
	 * Initializes and instantiates
	 */
	private GameEngine() {
		initialize();
	}

	/**
	 * Initializes all the stuff
	 */
	private void initialize() {
		instance = this;
		
		if ((System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("windows")) && !System.getProperty("user.home").contains("AppData")) {
			System.setProperty("user.home", System.getProperty("user.home") + "/AppData/Roaming");
		}
		
		ScreenReference.doScreenCalc();
		
		Reference.Fonts.init();
		
		this.sdtInstance = new ShutdownThread();
		Runtime.getRuntime().addShutdownHook(sdtInstance);

		this.frame = new JFrame("Game Template");
		this.frame.setBounds(0, 0, ScreenReference.WIDTH, ScreenReference.HEIGHT);
		this.frame.setResizable(false);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setUndecorated(true);
		this.frame.setVisible(true);

		this.rlo = new RenderLockObject();
		this.rtInstance = new RenderThread("RenderThread", this.rlo, this);
		this.rtInstance.start();
		
		this.keyHandlerInstance = new GKELAH();
		this.frame.addKeyListener(keyHandlerInstance);
		
		mouseHandlerInstace = new MouseHandler();
		this.frame.addMouseListener(mouseHandlerInstace);

		this.i = new BufferedImage(ScreenReference.WIDTH, ScreenReference.HEIGHT, BufferedImage.TYPE_INT_RGB);
		this.g2 = this.i.createGraphics();
		this.g = (Graphics2D) frame.getGraphics();
		
		// Turn on AnitAliasing
		this.g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		this.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.g2.transform(AffineTransform.getScaleInstance(.5, .5));
//		this.g2.transform(AffineTransform.getScaleInstance(0.125, 0.125));
		
		this.frc = this.g2.getFontRenderContext();
		
		Turret t = new Turret();
		gameObjects.add(t);
		mouseHandlerInstace.registerMouseReliant(t);
		
//		t = new Turret(100, 100);
//		gameObjects.add(t);
//		this.mouseHandlerInstace.registerMouseReliant(t);
//		
//		t = new Turret(3500, 2000);
//		gameObjects.add(t);
//		this.mouseHandlerInstace.registerMouseReliant(t);
		
		TestTarget tt = new TestTarget();
		gameObjects.add(tt);
		targets.add(tt);
		mouseHandlerInstace.registerWaypointListener(tt);
		
		tt = new TestTarget(300, 2000);
		gameObjects.add(tt);
		targets.add(tt);
		mouseHandlerInstace.registerWaypointListener(tt);
		
		tt = new TestTarget(1500, 1000);
		gameObjects.add(tt);
		targets.add(tt);
		mouseHandlerInstace.registerWaypointListener(tt);
		
		Random r = new Random();
		int num = r.nextInt(20) + 80;
		for (int i = 0; i < num; i++) {
			tt = new TestTarget(r.nextInt(ScreenReference.WIDTH), r.nextInt(ScreenReference.HEIGHT));
			gameObjects.add(tt);
			targets.add(tt);
			mouseHandlerInstace.registerWaypointListener(tt);
		}
		
		Ship s = new Ship(500, 500, new Color(27, 156, 255), 0);
		Turret[] turrets = s.getTurrets();
		for (int i = 0; i < turrets.length; i++) {
			team1Turrets.add(turrets[i]);
		}
		s.registerTurrets(mouseHandlerInstace);
		gameObjects.add(s);
		mouseHandlerInstace.registerWaypointListener(s);
		
		s = new Ship(500, 1500, new Color(27, 156, 255), -6);
		turrets = s.getTurrets();
		for (int i = 0; i < turrets.length; i++) {
			team1Turrets.add(turrets[i]);
		}
		gameObjects.add(s);
		
		s = new Ship(2500, 500, new Color(240, 70, 3), 6);
		turrets = s.getTurrets();
		for (int i = 0; i < turrets.length; i++) {
			team2Turrets.add(turrets[i]);
		}
		gameObjects.add(s);
		s.registerTurrets(mouseHandlerInstace);
		
		s = new Ship(2500, 1500, new Color(240, 70, 3), -6);
		turrets = s.getTurrets();
		for (int i = 0; i < turrets.length; i++) {
			team2Turrets.add(turrets[i]);
		}
		gameObjects.add(s);
		
		this.releaseFocous();
		
		System.gc();
	}

	/**
	 * Loops through all the updatables and updates them
	 * 
	 * @param deltaTime
	 *            - Time between each frame (used to evaluate things within
	 *            update methods of each object)
	 */
	private void update(double deltaTime) {
		long start = System.nanoTime();
		for (TestTarget tt : waitingAddTargets) {
			synchronized (gameObjects) {
				gameObjects.add(tt);
			}
			targets.add(tt);
			mouseHandlerInstace.registerWaypointListener(tt);
			waitingAddTargets.remove();
		}
		
		for (Projectile p : waitingAddProjectiles) {
			try {
				Projectile toAdd = p.clone();
				synchronized (projectiles) {
					projectiles.add(toAdd);
				}
				waitingAddProjectiles.remove();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		
		for (Projectile p : projectiles) {
			p.update(deltaTime);
			if (!p.inGameMap()) {
				waitingRemoveProjectiles.add(p);
			} else {
				for (int i = 0; i < team1Turrets.size(); i++) {
					if (team1Turrets.get(i).coliding(p) && !waitingRemoveTurrets1.contains(team1Turrets.get(i))) {
						waitingRemoveTurrets1.add(team1Turrets.get(i));
					}
				}
				for (int i = 0; i < team2Turrets.size(); i++) {
					if (team2Turrets.get(i).coliding(p) && !waitingRemoveTurrets2.contains(team2Turrets.get(i))) {
						waitingRemoveTurrets2.add(team2Turrets.get(i));
					}
				}
				for (int i = 0; i < targets.size(); ) {
					if (targets.get(i).coliding(p)) {
						synchronized (gameObjects) {
							gameObjects.remove(targets.remove(i));
						}
					} else {
						i++;
					}
				}
			}
		}
		
		for (Turret turret : waitingRemoveTurrets1) {
			synchronized (team1Turrets) {
				team1Turrets.remove(turret);
			}
		}
		
		for (Turret turret : waitingRemoveTurrets2) {
			synchronized (team2Turrets) {
				team2Turrets.remove(turret);
			}
		}
		
		for (Projectile p : waitingRemoveProjectiles) {
			synchronized (projectiles) {
				projectiles.remove(p);
			}
			waitingRemoveProjectiles.remove();
		}
		
		for (GameObject gameObject : gameObjects) {
			gameObject.update(deltaTime);
		}

		for (IUpdateable upject : updateable) {
			upject.update(deltaTime);
		}
		
		for (IGuiElement gui : guiElements) {
			gui.updateUpdateableElements(deltaTime);
		}
		long end = System.nanoTime();
		System.err.println("Update: " + (end - start));
	}

	/**
	 * Loops through all the Drawables and draws them
	 * 
	 * @param g
	 *            Graphics instance to draw upon
	 * @param observer
	 *            observer to put graphics instance upon
	 */
	private void render(Graphics2D g, ImageObserver observer) {
		long start = System.nanoTime();
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, ScreenReference.WIDTH * 8, ScreenReference.HEIGHT * 8);
		
		synchronized (gameObjects) {
			for (GameObject gameObject : gameObjects) {
				gameObject.draw(g2, observer);
			}
		}
		
		synchronized (projectiles) {
			for (GameObject projectile : projectiles) {
				projectile.draw(g2, observer);
			}
		}

		for (IDrawable iDrawable : drawable) {
			iDrawable.draw(g, observer);
		}

		if (Reference.DEBUG_MODE) {
			g2.setColor(Color.GREEN);
			g2.setFont(ScreenReference.getTheFont());
			g2.drawString(stats, 25, 60);
			
			Point p = getMouseLocation();
			if (p != null) {
				String s = p.toString();
				Rectangle2D r;
				r = ScreenReference.getTheFont().getStringBounds(s, frc);
				int yOff = (int) r.getHeight();
				// System.err.println(s + "," + p.x + "," + (p.y+ yOff));
				g2.drawString(s, p.x, p.y + yOff);
			}
		}

		g.drawImage(this.i, 0, 0, this.frame);
		long stop = System.nanoTime();
		drawCounter++;
//		System.out.println("draw " + drawCounter + " took " + (stop - start) + "nano seconds with " 
//				+ (16666666.666 - (start - stop)) + "nano seconds to spare");
	}

	/**
	 * Short hand for {@link GameEngine#render(Graphics, ImageObserver)}, 
	 * used by {@link com.joseph.gametemplate.threads.RenderThread RenderThread}
	 * to render the game onto the frame.
	 */
	public void render() {
		render(g, frame);
	}

	/**
	 * Runs the GameEngine
	 */
	private void run() {
		long time = System.nanoTime();
		final double tick = 60.0;
		double ms = 1000000000 / tick;
		double deltaTime = 0;
		int ticks = 0;
		int fps = 0;
		long timer = System.currentTimeMillis();
		long frameLimit = 80;
		long currentTime;
		int seconds = 0;
		int minutes = 0;
		int hours = 0;

		while (running) {
			currentTime = System.nanoTime();
			deltaTime += (currentTime - time) / ms;
			time = currentTime;

			if (deltaTime >= 1) {
				ticks++;
				long start = System.nanoTime();
				update(deltaTime);
				long stop = System.nanoTime();
				updateCounter++;
//				System.out.println("update " + updateCounter + " took " + (stop - start) + " nano seconds with " 
//						+ (16666666.666 - (start - stop)) + " nano seconds to spare");
				deltaTime--;
//				System.out.println(deltaTime);
			}

			synchronized (rlo) {
				rlo.setWasNotified(true);
				rlo.notify();
			}
			fps++;

			while (deltaTime < frameLimit) {
				currentTime = System.nanoTime();
				deltaTime += (currentTime - time) / ms;
				time = currentTime;
			}

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				seconds++;
				if (seconds > 60) {
					seconds %= 60;
					minutes++;

					if (minutes > 60) {
						minutes %= 60;
						hours++;
					}
				}

				// GT stands for GameTime.
				stats = "Ticks: " + ticks + ", FPS: " + fps + ", GT: " + ((hours < 10) ? "0" + hours : hours) + ":"
						+ ((minutes < 10) ? "0" + minutes : minutes) + ":" + ((seconds < 10) ? "0" + seconds : seconds);
				if (Reference.DEBUG_MODE) {
					System.out.println(stats);
				}
				ticks = 0;
				fps = 0;
				if (Reference.DEBUG_MODE) {
					System.out.println(Runtime.getRuntime().freeMemory());
				}
				System.gc();
				if (Reference.DEBUG_MODE) {
					System.out.println(Runtime.getRuntime().freeMemory());
				}
			}
		}
	}
	
	/**
	 * gets the location of the mouse in the frame
	 * 
	 * @return - the location of the mouse relative to the frame
	 */
	public Point getMouseLocation() {
		return this.frame.getContentPane().getMousePosition();
	}
	
	/**
	 * Refocuses the frame to make sure that key events are captured
	 */
	public void releaseFocous() {
		this.frame.requestFocus();
	}
	
	public FontRenderContext getFrc() {
		return this.frc;
	}
	
	public static void updateTargetVelocities(Vector v) {
		for (TestTarget tt : targets) {
			tt.setMovementVector(v);
		}
	}
	
	public static void populateNewTargets(Random r, int number) {
		for (int i = 0; i < number; i++) {
			TestTarget tt = new TestTarget(r.nextInt(ScreenReference.WIDTH), r.nextInt(ScreenReference.HEIGHT));
//			gameObjects.add(tt);
//			targets.add(tt);
//			mouseHandlerInstace.registerWaypointListener(tt);
			waitingAddTargets.add(tt);
		}
	}
	
	public static void sapwnProjectile(Projectile p) {
//		gameObjects.add(p);
		waitingAddProjectiles.add(p);
	}
	
	public static GameObject getClosestTarget(DPoint origin) {
		if (targets.size() == 0) {
			return null;
		}
		GameObject closest = targets.get(0);
		double closestDistance = MathHelper.getDistance(origin, closest.getLocation());
		for (int i = 1; i < targets.size(); i++) {
			GameObject current = targets.get(i);
			double currentDistance = MathHelper.getDistance(origin, current.getLocation());
			if (currentDistance < closestDistance) {
				closestDistance = currentDistance;
				closest = current;
			}
		}
		return closest;
	}
	
	public static Turret getClosestEnemyTurret(Turret tur) {
		if (team1Turrets.contains(tur)) {
			if (team2Turrets.size() == 0) {
				return tur;
			}
			Turret closest = team2Turrets.get(0);
			double closestDistance = MathHelper.getDistance(tur.getLocation(), closest.getLocation());
			for (int i = 1; i < team2Turrets.size(); i++) {
				Turret current = team2Turrets.get(i);
				if (current.isDead()) {
					continue;
				}
				double currentDistance = MathHelper.getDistance(tur.getLocation(), current.getLocation());
				if (currentDistance < closestDistance) {
					closestDistance = currentDistance;
					closest = current;
				}
			}
			return closest;
		} else {
			if (team1Turrets.size() == 0) {
				return tur;
			}
			Turret closest = team1Turrets.get(0);
			double closestDistance = MathHelper.getDistance(tur.getLocation(), closest.getLocation());
			for (int i = 1; i < team1Turrets.size(); i++) {
				Turret current = team1Turrets.get(i);
				if (current.isDead()) {
					continue;
				}
				double currentDistance = MathHelper.getDistance(tur.getLocation(), current.getLocation());
				if (currentDistance < closestDistance) {
					closestDistance = currentDistance;
					closest = current;
				}
			}
			return closest;
		}
	}
}
/*
 * -XX:+UnlockCommercialFeatures -XX:+FlightRecorder
 * -XX:FlightRecorderOptions=stackdepth=2048
 * -XX:StartFlightRecording=duration=60m,filename=GameTemplate.jfr
 */