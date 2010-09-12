package no.saua.mousekiller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Engine;
import no.saua.engine.Entity;
import no.saua.engine.Font;
import no.saua.engine.State;
import no.saua.engine.Texture;
import no.saua.engine.collisionsystem.CollisionGroup;
import no.saua.engine.collisionsystem.CollisionHandler;
import no.saua.mousekiller.Sidebar.SidebarListener;
import no.saua.mousekiller.entities.Bomb;
import no.saua.mousekiller.entities.GenderChanger;
import no.saua.mousekiller.entities.Mouse;
import no.saua.mousekiller.entities.StopSign;
import no.saua.mousekiller.entities.Bomb.BombCreator;
import no.saua.mousekiller.entities.GenderChanger.MaleChangerCreator;
import no.saua.mousekiller.entities.GenderChanger.FemaleChangerCreator;
import no.saua.mousekiller.entities.StopSign.StopSignCreator;
import android.content.res.AssetManager;
import android.util.Log;

public class GameState extends State implements SidebarListener, MouseChangeListener {
	private Map map;
	
	private CopyOnWriteArrayList<Entity> entities;
	
	private CollisionGroup collisionGroup;
	
	private Camera camera;
	
	private Sidebar sidebar;
	private PlaceableSidebarItem sideBomb, sideStopSign, sideMale, sideFemale;
	
	private Engine engine;
	private FPSText fpstext;
	
	private Font font;
	
	private int males;
	private int females;
	
	public synchronized void init(Engine e, GL10 gl, AssetManager assets) {
		engine = e;
		entities = new CopyOnWriteArrayList<Entity>();
		try {
			font = new Font(Texture.loadTexture(gl, assets.open("fonts/monospacenumbers.png")), "1234567890", 12, 15);
			fpstext = new FPSText(e, gl, font);
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
		
//		entities = new ArrayList<Entity>();
//		bombs = new ArrayList<Bomb>();
		
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
		sideBomb = new PlaceableSidebarItem(20, 20, new BombCreator(), 3, font, gl);
		sideStopSign = new PlaceableSidebarItem(20, 60, new StopSignCreator(), 2, font, gl);
		sideMale = new PlaceableSidebarItem(20, 100, new MaleChangerCreator(), 2, font, gl);
		sideFemale = new PlaceableSidebarItem(20, 140, new FemaleChangerCreator(), 2, font, gl);
		sidebar = new Sidebar(gl,e, font);		
		sidebar.setListener(this);
		sidebar.addSidebarEntity(sideBomb);
		sidebar.addSidebarEntity(sideStopSign);
		sidebar.addSidebarEntity(sideMale);
		sidebar.addSidebarEntity(sideFemale);
		
		addCollideableEntity(new Mouse(this, map));
		addCollideableEntity(new Mouse(this, map));
		addCollideableEntity(new Mouse(this, map));
		addCollideableEntity(new Mouse(this, map));
		addCollideableEntity(new Mouse(this, map));
		
		for (Entity en: entities) {
			if (en instanceof Mouse) {
				if (((Mouse) en).getSex() == Mouse.Sex.male) males++;
				else females++;
			}
		}
		sidebar.setMiceAmount(gl, males, females);
	}

	public synchronized void render(Engine e, GL10 gl) {
		
		// Center map
		camera.transform(gl);
		map.render(gl);
		
		for (Entity en: entities) {
			en.render(gl);
		} 
		
//		for (Bomb bomb: bombs) {
//			bomb.renderGrid(gl);
//		}
		gl.glLoadIdentity();
		sidebar.render(gl);
		fpstext.render(gl);
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
				
//		for (int i = 0; i < bombs.size(); i++) {
//			if (bombs.get(i).isRemoving())
//				bombs.remove(i);
//		}
//		if (e.getGL().glGetError() != 0) {
//			Log.e("Grid", "VBO Not available: " + e.getGL().glGetError() + ", " + e.getGL().glGetString(e.getGL().glGetError()));
//		}
		
		collisionGroup.check();
		
		fpstext.update(dt, e.getGL());
		sidebar.setMiceAmount(e.getGL(), males, females);
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
	
	public CopyOnWriteArrayList<Entity> getEntities() {
		return entities;
	}

	public void sidebarItemDragged(PlaceableSidebarItem item, float x, float y) {
		x -= engine.getWidth() / 2f;
		y -= engine.getHeight() / 2f;
		
		int tilex = map.getTileX(camera.gameX((int) x));
		int tiley = map.getTileY(camera.gameY((int) y));
		
		if (map.isWalkable(tilex, tiley)) {
			Entity e = item.createItem(engine.getGL(), this, map, tilex, tiley);
			
			if (e.collidable()) collisionGroup.addEntity(e);
			entities.add(e);
		}
	}

	public Map getMap() {
		return map;
	}

	public Engine getEngine() {
		return engine;
	}

	public void modifyMiceAmounts(int dmale, int dfemale) {
		males += dmale;
		females += dfemale;
		sidebar.setMiceAmount(engine.getGL(), males, females);
	}
}
