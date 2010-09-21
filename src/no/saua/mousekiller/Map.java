package no.saua.mousekiller;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

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
			boolean walkable, placeable, visible;
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
				t.visible = o.readBoolean();
				
				types[i] = t;
			}
		}
	}

	
	public static void loadSprites(GL10 gl, AssetManager assets) throws IOException {
//		tilemap = Texture.loadTexture(gl, assets.open("textures/map.png"));
	}
		
	private byte[][][] tiles;
	private int sizex;
	private int sizey;
	private int sizez;
	private Tileset tileset;
	private TextureGrid[] layers;
//	private TextureGrid texturegrid;
	
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
	
	public Map(GL10 gl, Tileset tileset, int sizex, int sizey, int sizez, byte[][][] tiles) throws IOException {
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
		this.sizex = sizex;
		this.sizey = sizey;
		this.sizez = sizez;
		
		// Needs to be flipped
//		for (int i = 0; i < tiles.length / 2; i++) {
//			byte[] t = tiles[i];
//			tiles[i] = tiles[tiles.length - i - 1];
//			tiles[tiles.length - i - 1] = t;
//		}

		// Set up layers
		layers = new TextureGrid[tiles.length]; 
		for (int z = 0; z < sizez;  z++) {
			
			// Figure out how many squares we need in this layer
			int squares = 0;
			for (int y = 0; y < sizey; y++) {
				for (int x = 0; x < sizex; x++) {
					if (tiles[z][y][x] != -1) squares++;
				}
			}
			
			// Set up the texturegrid
			layers[z] = new TextureGrid(tileset.tileimage, squares, TILESIZE, TILESIZE);
			for (int y = 0; y < sizey; y++) {
				for (int x = 0; x < sizex; x++) {
					if (tiles[z][y][x] != -1) {
						Tileset.Tiletype type = tileset.types[tiles[z][y][x]];
						layers[z].set(--squares, x, y, (int)type.tileimagex, (int)type.tileimagey, type.rotation);
					}
				}
			}
			layers[z].generateBuffers(gl);
		}
	}
	

	
	public void renderUnder(GL10 gl) {
		layers[0].render(gl);
	}
	public void renderOver(GL10 gl) {
		for (int z = 1; z < layers.length; z++) {
			layers[z].render(gl);
		}
	}
	
	public boolean isWalkable(int x, int y) {
		if (x < 0 || y < 0) return false;
		if (!(sizey > y && sizex > x)) return false;
		if (tiles[0][y][x] == -1) return false;
		
		return tileset.types[tiles[0][y][x]].walkable;
	}
	
	private ArrayList<Vector2i> roads;
	public Vector2i getRandomRoad() {
		if (roads == null) {
			roads = new ArrayList<Vector2i>();
			for (int y = 0; y < sizey; y++) {
				for (int x = 0; x < sizex; x++) {
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
		return sizey * TILESIZE;
	}
	
	public int getWidth() {
		return sizex * TILESIZE;
	}
	
	public static Map load(GL10 gl, String name, AssetManager assets) throws IOException {
		DataInputStream o = new DataInputStream(assets.open("maps/" + name));
		Tileset tileset = new Tileset(gl, o.readUTF(), assets);
		
		int sizex = o.readByte();
		int sizey = o.readByte();
		int sizez = o.readByte();
		
		Log.d("Map", sizex + ", " + sizey + ", " + sizez);
		
		byte[][][] tiles = new byte[sizez][sizey][sizex];
		for (int z = 0; z < sizez; z++) {
			for (int y = 0; y < sizey; y++) {
				for (int x = 0; x < sizex; x++) {
					tiles[z][y][x] = o.readByte();
					Log.d("Map", x + ", "+ y + ", " + z + ": " + tiles[z][y][x]);
				}	
			}
		}

		
		return new Map(gl, tileset, sizex, sizey, sizez, tiles);
	}



	public boolean isVisible(int x, int y) {
		return tiles[1][y][x] == -1 || tileset.types[tiles[1][y][x]].visible;
	}
}
