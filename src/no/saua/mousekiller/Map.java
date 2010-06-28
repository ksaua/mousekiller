package no.saua.mousekiller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Texture;
import no.saua.engine.TextureGrid;
import no.saua.engine.TextureGrid.Tilerotation;
import no.saua.engine.utils.Utils;
import no.saua.engine.utils.Vector2i;
import android.content.res.AssetManager;
import android.util.Log;

public class Map {
	public static final int TILESIZE = 32;
		
	private static class Tileset { 
		/* byte num_tiletypes
		 * 
		 * For each tiletype
		 *   byte tile_image_x
		 *   byte tile_image_y
		 *   byte rotation
		 *   boolean walkable
		 *   boolean placeable
		 */
		class Tiletype {
			byte tileimagex, tileimagey;
			TextureGrid.Tilerotation rotation;
			boolean walkable, placeable;
		}
		public Texture tileimage;
		public Tiletype[] types;
		public Tileset(GL10 gl, String name, AssetManager assets) throws IOException {
			DataInputStream o = new DataInputStream(assets.open(name));
			
			tileimage = Texture.loadTexture(gl, assets.open("textures/" + o.readUTF())); 
			
			byte numTileTypes = o.readByte();
			types = new Tiletype[numTileTypes];
			for (int i = 0; i < numTileTypes; i++) {
				Tiletype t = new Tiletype();
				t.tileimagex = o.readByte();
				t.tileimagey = o.readByte();
				
				byte rotation = o.readByte();
				if (rotation == 0) t.rotation = Tilerotation.r0;
				else if (rotation == 1) t.rotation = Tilerotation.r90;
				else if (rotation == 2) t.rotation = Tilerotation.r180;
				else if (rotation == 3) t.rotation = Tilerotation.r270;
				
				
				t.walkable = o.readBoolean();
				t.placeable = o.readBoolean();
				
				types[i] = t;
			}
		}
	}

	
	public static void loadSprites(GL10 gl, AssetManager assets) throws IOException {
//		tilemap = Texture.loadTexture(gl, assets.open("textures/map.png"));
	}
		
	private byte[][] tiles;
	private Tileset tileset;
	private TextureGrid texturegrid;
	
	/*
	 * String map name
	 * String tilemapimage
	 * 
	 * byte num_tiletypes
	 * 
	 * For each tiletype
	 *   byte tilemapimagex
	 *   byte tilemapimagey
	 *   byte rotation
	 *   boolean walkable
	 *   boolean placeable
	 *   
	 * 
	 */ 
	
	public Map(GL10 gl, Tileset tileset, byte[][] tiles) throws IOException {
//		tileset = new Tileset(assets.open("tileset1"), gl, assets);
//		
//		tiles = new byte[][] {
//				{  0,  3,  3,  3,  3,  3,  3,  0},
//				{  4, 15, 15, 15, 15, 15, 15,  2},
//				{  4, 15,  9, 15,  5,  8, 15,  2},
//				{  0, 13,  7, 15,  6,  7, 15,  2},
//				{  4, 15, 15, 15, 15, 15, 15,  2},
//				{  4, 15,  9, 15, 10, 12, 15,  2},
//				{  4, 15, 14, 15, 15, 15, 15,  2},
//				{  4, 15,  6, 13, 13, 12, 15,  2},
//				{  4, 15, 15, 15, 15, 15, 15,  2},
//				{  0,  1,  1,  1,  1,  1,  1,  0},
//		};
		this.tileset = tileset;
		this.tiles = tiles;
		
		// Needs to be flipped
//		for (int i = 0; i < tiles.length / 2; i++) {
//			byte[] t = tiles[i];
//			tiles[i] = tiles[tiles.length - i - 1];
//			tiles[tiles.length - i - 1] = t;
//		}

		// Set up Texturegrid
		texturegrid = new TextureGrid(tileset.tileimage, tiles[0].length, tiles.length, TILESIZE, TILESIZE);
		
		for (int y = 0; y < tiles.length; y++) {
			for (int x = 0; x < tiles[0].length; x++) {
				Tileset.Tiletype type = tileset.types[tiles[y][x]];
				texturegrid.set(x, y, (int)type.tileimagex, (int)type.tileimagey, type.rotation);
			}
		}
		
		texturegrid.generateBuffers(gl);
	}
	

	
	public void render(GL10 gl) {
		texturegrid.render(gl);
	}
	
	public boolean isWalkable(int x, int y) {
		if (x < 0 || y < 0) return false;
		if (!(tiles.length > y && tiles[0].length > x)) return false;
		
		return tileset.types[tiles[y][x]].walkable;
	}
	
	private ArrayList<Vector2i> roads;
	public Vector2i getRandomRoad() {
		if (roads == null) {
			roads = new ArrayList<Vector2i>();
			for (int y = 0; y < tiles.length; y++) {
				for (int x = 0; x < tiles[0].length; x++) {
					if (isWalkable(x, y)) {
						roads.add(new Vector2i(x, y));
					}
				}	
			}
		}
		return roads.get(Utils.randint(0, roads.size() - 1 ));
	}
	
	public float getTileCenterX(int tilex) {
		return (tilex + 0.5f) * TILESIZE; 
	}
	
	public float getTileCenterY(int tiley) {
		return (tiley + 0.5f) * TILESIZE; 
	}
	
	public int getTileX(float a) {
		return (int)(a) / TILESIZE;
	}
	
	public int getTileY(float a) {
		return (int)(a) / TILESIZE;
	}

	public int getHeight() {
		return tiles.length * TILESIZE;
	}
	
	public int getWidth() {
		return tiles[0].length * TILESIZE;
	}
	
	public static Map load(GL10 gl, String name, AssetManager assets) throws IOException {
		DataInputStream o = new DataInputStream(assets.open("maps/" + name));
		Tileset tileset = new Tileset(gl, o.readUTF(), assets);
		
		int sizex = o.readByte();
		int sizey = o.readByte();
		
		Log.e("Map", sizex + ", " + sizey);
		
		byte[][] tiles = new byte[sizey][sizex];
		for (int y = 0; y < sizey; y++) {
			for (int x = 0; x < sizex; x++) {
				tiles[y][x] = o.readByte();
				Log.e("Map", x + ", "+ y + ": " + tiles[y][x]);
			}	
		}
		
		return new Map(gl, tileset, tiles);
	}
}
