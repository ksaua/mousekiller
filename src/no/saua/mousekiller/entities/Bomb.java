package no.saua.mousekiller.entities;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Animation;
import no.saua.engine.Engine;
import no.saua.engine.Entity;
import no.saua.engine.Grid;
import no.saua.engine.Texture;
import no.saua.mousekiller.GameState;
import no.saua.mousekiller.Map;
import no.saua.mousekiller.PlaceableSidebarItem.SidebarItemCreator;
import android.content.res.AssetManager;

public class Bomb extends Entity {
	
	public static class BombCreator implements SidebarItemCreator {
		public  Entity createItem(GL10 gl, Map map, int tilex, int tiley) {
			return new Bomb(gl, map, tilex, tiley);
		}

		public Texture getIconTexture() { return bomb[0]; }
	}
	
	private static final int max_tiles = 5;
	public static Texture[] bomb;
	private static Texture[] bang;
	private Grid grid;

	private boolean initialized;
	private float fusetime;
	private float bangtime;
	private boolean exploded;
	
	private Animation bangAnimation;
	
	private int tilex, tiley;
	private int walkableLeft, walkableRight, walkableUp, walkableDown;
	
	public Bomb(GL10 gl, Map map, int tilex, int tiley) {
		this.tilex = tilex;
		this.tiley = tiley;

		setPosition(map.getTileCenterX(tilex), map.getTileCenterY(tiley));
		setCollisionRadius(12);
		setCollidable(true);
		setMoveable(false);
		fusetime = 4;
		bangtime = 0.5f;
		setAnimation(new Animation(bomb, fusetime / bomb.length ));
		bangAnimation = new Animation(bang, bangtime / bang.length);
		
		setupGrid(gl, map);
	}
	
	private void setupGrid(GL10 gl, Map map) {
		//TODO: Use only 2 squares, not ... a lot
		walkableLeft = Math.min(max_tiles, walkableTiles(map, tilex, tiley, -1, 0));
		walkableRight = Math.min(max_tiles, walkableTiles(map, tilex, tiley, 1, 0));
		walkableUp = Math.min(max_tiles, walkableTiles(map, tilex, tiley, 0, 1));
		walkableDown = Math.min(max_tiles, walkableTiles(map, tilex, tiley, 0, -1));
		
		grid = new Grid(walkableLeft + walkableRight + walkableUp + walkableDown + 1); // 1 = this grid
		setGridSquare(map, 0, tilex, tiley);

		
		for (int i = 1; i <= walkableLeft; i++)
			setGridSquare(map, i, tilex - i, tiley);
		
		for (int i = 1; i <= walkableRight; i++)
			setGridSquare(map, i + walkableLeft, tilex + i, tiley);
		
		for (int i = 1; i <= walkableUp; i++)
			setGridSquare(map, i + walkableLeft + walkableRight, tilex, tiley + i);
		
		for (int i = 1; i <= walkableDown; i++)
			setGridSquare(map, i + walkableLeft + walkableRight + walkableUp, tilex, tiley - i);
	}
	
	private void setGridSquare(Map map, int squareId, int tilex, int tiley) {
		grid.setSquare(squareId,
				map.getTileCenterX(tilex) - Map.TILESIZE / 2f,
				map.getTileCenterY(tiley) - Map.TILESIZE / 2f,
				map.getTileCenterX(tilex) + Map.TILESIZE / 2f,
				map.getTileCenterY(tiley) + Map.TILESIZE / 2f);
	}
	
	private int walkableTiles(Map map, int cx, int cy, int dx, int dy) {
		int i = 1;
		while (map.isWalkable(cx + dx * i, cy + dy * i)) { i++; }
		return i - 1;
	}
	
	public void render(GL10 gl) {
		if (exploded) grid.render(gl, bangAnimation.getCurrentTexture());
		else super.render(gl);
	}
	
	@Override
	public void update(float dt, GameState gs) {
		super.update(dt, gs);
		if (!initialized) {
			grid.generateBuffers(gs.engine.getGL());
			initialized = true;
		}

		if (!exploded) {
			fusetime -= dt;
			if (fusetime < 0) {
				explode(gs);
			}
			
		} else {
			bangtime -= dt;
			if (bangtime < 0) {
				grid.releaseHardwareBuffers(gs.engine.getGL());
				this.remove();
			} else {
				bangAnimation.update(dt);
			}
		}
	}

	
	private void explode(GameState gs) {
		exploded = true;
		setAnimation(null);
		
		// Remove entities inside tiles
		for (Entity entity: gs.entities) {
			if (entity != this) {
				int etx = gs.map.getTileX(entity.getX());
				int ety = gs.map.getTileY(entity.getY());
				if (tiley == ety && (tilex - walkableLeft <= etx && etx <= tilex + walkableRight)) 
					entity.remove();
				else if (tilex == etx && (tiley - walkableDown <= ety && ety <= tiley + walkableUp))
					entity.remove();
			}
		}
	}

	public static void loadSprites(GL10 gl, AssetManager assets) throws IOException {
		bomb = new Texture[8];
		for (int i = 0; i < bomb.length; i++) {
			bomb[i] = Texture.loadTexture(gl, assets.open("textures/bomb" + (i + 1) + ".png"));;
		}
		
		bang = new Texture[14];
		for (int i = 0; i < bang.length; i++) {
			bang[i] = Texture.loadTexture(gl, assets.open("textures/bang" + (i + 1) + ".png"));;
		}
	}

	public Entity createItem(GL10 gl, Map map, int tilex, int tiley) {
		return null;
	}
}