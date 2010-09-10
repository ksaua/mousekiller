package no.saua.mousekiller;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Engine;
import no.saua.engine.Entity;
import no.saua.engine.Font;
import no.saua.engine.State;
import no.saua.engine.Texture;
import no.saua.engine.collisionsystem.CollisionGroup;
import no.saua.engine.collisionsystem.CollisionHandler;
import no.saua.mousekiller.Sidebar.SidebarListener;
import android.content.res.AssetManager;
import android.util.Log;

public class GameState extends State implements SidebarListener {
	Map map;
	
	ArrayList<Entity> entities; 
	ArrayList<Bomb> bombs;
	
	CollisionGroup collisionGroup;
	
	Camera camera;
	
	Sidebar sidebar;
	SidebarItem sideBomb, sideStopSign, sideMale, sideFemale;
	
	Engine engine;
	FPSText fpstext;
	
	public synchronized void init(Engine e, GL10 gl, AssetManager assets) {
		engine = e;
		try {
			Font font = new Font(Texture.loadTexture(gl, assets.open("fonts/monospacenumbers.png")), "1234567890", 12, 15);
			fpstext = new FPSText(e, font);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		gl.glClearColor(12f / 256f, 1, 0, 1);
		
		try {
			Map.loadSprites(gl, assets);
			Mouse.loadSprites(gl, assets);		
			StopSign.loadSprites(gl, assets);
			Bomb.loadSprites(gl, assets);
			GenderChanger.loadSprites(gl, assets);
			map = Map.load(gl, "map", assets);
		} catch (IOException ioe) {
			Log.e("GameState", "Error loading: " + ioe.getMessage());
			System.exit(1);
		}
		
		entities = new ArrayList<Entity>();
		bombs = new ArrayList<Bomb>();
		
		collisionGroup = new CollisionGroup();
		collisionGroup.addHandler(new CollisionHandler() {
			public void collisionOccured(Entity a, Entity b) {
				if ((a instanceof Mouse) && (b instanceof Mouse)) {
					((Mouse)a).collided((Mouse)b);
					((Mouse)b).collided((Mouse)a);
				} else if ((a instanceof StopSign) && (b instanceof Mouse)) {
					((StopSign) a).collision((Mouse) b);
				}  else if ((a instanceof Mouse) && (b instanceof StopSign)) {
					((StopSign) b).collision((Mouse) a);
				} else if ((a instanceof GenderChanger) && (b instanceof Mouse)) {
					((GenderChanger) a).collision((Mouse) b);
				}  else if ((a instanceof Mouse) && (b instanceof GenderChanger)) {
					((GenderChanger) b).collision((Mouse) a);
				}
			}
		});
		
		camera = new Camera(map, e.getScreenWidth(), e.getScreenHeight());
		
		sideBomb = new SidebarItem(20, 20, Bomb.bomb[0]);
		sideStopSign = new SidebarItem(20, 60, StopSign.tex);
		sideMale = new SidebarItem(20, 100, GenderChanger.texMale);
		sideFemale = new SidebarItem(20, 140, GenderChanger.texFemale);
		sidebar = new Sidebar();		
		sidebar.setListener(this);
		sidebar.addGuiEntity(sideBomb);
		sidebar.addGuiEntity(sideStopSign);
		sidebar.addGuiEntity(sideMale);
		sidebar.addGuiEntity(sideFemale);
		
		addCollideableEntity(new Mouse(map));
		addCollideableEntity(new Mouse(map));
		addCollideableEntity(new Mouse(map));
		addCollideableEntity(new Mouse(map));
		addCollideableEntity(new Mouse(map));
	}

	public synchronized void render(Engine e, GL10 gl) {
		
		// Center map
		camera.transform(gl);
		map.render(gl);
		
		for (Entity en: entities) {
			en.render(gl);
		} 
		
		for (Bomb bomb: bombs) {
			bomb.renderGrid(gl);
		}
		fpstext.render(gl);
		gl.glLoadIdentity();
		sidebar.render(gl);
	}

	public synchronized void update(Engine e, float dt) {
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i).isRemoving()) {
				collisionGroup.removeEntity(entities.get(i));
				entities.remove(i);
			} else if (entities.get(i) != null) {
				entities.get(i).update(dt, this);
			}
		}
		
		for (int i = 0; i < bombs.size(); i++) {
			if (bombs.get(i).isRemoving())
				bombs.remove(i);
		}
//		if (e.getGL().glGetError() != 0) {
//			Log.e("Grid", "VBO Not available: " + e.getGL().glGetError() + ", " + e.getGL().glGetString(e.getGL().glGetError()));
//		}
		
		collisionGroup.check();
		
		fpstext.update(dt, e.gl);
	}

	public synchronized void onTouchPress(Engine e, float x, float y) {
//		Log.e("GameState", "PResses :" + x + ", " + y);
//		Log.e("Gamestate", "FPS: " + e.getFPS());
//		Log.e("GameState", "x: " + x + ", " + camera.gameX((int) x) + ", " + map.getTile(camera.gameX((int) x)));
//		Log.e("GameState", "y: " + y + ", " + camera.gameY((int) y) + ", " + map.getTile(camera.gameY((int) y)));
	}

	public synchronized void onTouchDrag(Engine e, float x1, float y1, float x2, float y2, boolean done) {
//		Log.e("GameState", "Dragged :" + x1 + ", " + y1 + " - " + x2 + ", " + y2 + ", " + done);
		// Inside sidebar
		if (x1 <= 40 && y1 <= 150) {
			sidebar.drag(x1, y1, x2, y2, done);
		} else {
			camera.drag(x1, y1, x2, y2, done);
		}
	}

	public void addCollideableEntity(Entity entity) {
		entities.add(entity);
		collisionGroup.addEntity(entity);
	}

	public void sidebarItemDragged(SidebarItem item, float x, float y) {
		x -= engine.getWidth() / 2f;
		y -= engine.getHeight() / 2f;
		
		int tilex = map.getTileX(camera.gameX((int) x));
		int tiley = map.getTileY(camera.gameY((int) y));
		
		if (map.isWalkable(tilex, tiley)) {
			if (item == sideStopSign) {
				addCollideableEntity(new StopSign(map, tilex, tiley));
			} else if ((item == sideMale) || (item == sideFemale)) {
				addCollideableEntity(new GenderChanger(map, tilex, tiley, item == sideMale));
			} else if (item == sideBomb) {
				Bomb b = new Bomb(engine.gl, map, tilex, tiley);
				entities.add(b);
				bombs.add(b);
			}
		}
	}
}
