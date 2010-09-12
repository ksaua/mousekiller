package no.saua.engine;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;

public abstract class State {
	public abstract void init(Engine engine, GL10 gl, AssetManager assets);
	public abstract void update(Engine engine, float dt);
	public abstract void render(Engine engine, GL10 gl);
	
	public abstract void onTouchPress(Engine e, float x, float y);
	public abstract void onTouchDrag(Engine e, float x1, float y1, float x2, float y2, boolean done);
}
