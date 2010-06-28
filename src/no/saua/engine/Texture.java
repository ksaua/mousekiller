package no.saua.engine;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

public class Texture {
	private int id;
	private int width;
	private int height;
	private float wratio;
	private float hratio;
	
	private Texture(int id, int imgwidth, int imgheight, int glwidth, int glheight) {
		this.id = id;
		this.wratio = imgwidth / (float)glwidth;
		this.hratio = imgheight / (float)glheight;
		this.width = imgwidth;
		this.height = imgheight;
	}

	public void bind(GL10 gl) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
	} 
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public float getWRatio() {
		return wratio;
	}

	public float getHRatio() {
		return hratio;
	}

	public static Texture loadTexture(GL10 gl, InputStream is) {
		Bitmap bitmap = null;

		try {
			//BitmapFactory is an Android graphics utility for images
			bitmap = BitmapFactory.decodeStream(is);
		} finally {
			//Always clear and close
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		int[] textures = new int[1];
		
		gl.glGenTextures(1, textures, 0);
	
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
				
		//Create Nearest Filtered Texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
		
		//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
		int[] cropping = new int[4];
        cropping[0] = 0;
        cropping[1] = bitmap.getHeight();
        cropping[2] = bitmap.getWidth();
        cropping[3] = -bitmap.getHeight();
        
        ((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, 
                GL11Ext.GL_TEXTURE_CROP_RECT_OES, cropping, 0);
		
		//Clean up
		bitmap.recycle();

		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.e("SpriteMethodTest", "Texture Load GLError: " + error);
		}
		return new Texture(textures[0], width, height, nextPow2(width), nextPow2(height));
	}
	
	private static int nextPow2(int n) {
		int i = 1;
		while (Math.pow(2, i) < n) {
			i++;
		}
		return (int) Math.pow(2, i);
	}
}
