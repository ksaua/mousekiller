package no.saua.mousekiller;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;


public class Camera {
	
	Map map;
	int screenwidth;
	int screenheight;
	
	int offsetx;
	int offsety;
	int dragStartx;
	int dragStarty;
	
	public Camera(Map map, int screenwidth, int screenheight) {
		this.map = map;
		offsetx = -map.getWidth() / 2;
		offsety = -map.getHeight() / 2;
		dragStartx = offsetx;
		dragStarty = offsety;
	}
	
	/**
	 * Converts 
	 */
	public int gameX(int screenx) {
		return screenx - offsetx;
	}
	
	public int gameY(int screeny) {
		return screeny - offsety;
	}
	
	/**
	 * Centers the camera and offsets it accordingly
	 */
	public void transform(GL10 gl) {
		gl.glTranslatef(offsetx, offsety, 0);
	}

	public void drag(float x1, float y1, float x2, float y2, boolean done) {
		offsetx = (int) (dragStartx + x2 - x1);
		offsety = (int) (dragStarty + y2 - y1);
		if (done) {
			dragStartx = offsetx;
			dragStarty = offsety;
		}
	}
}
