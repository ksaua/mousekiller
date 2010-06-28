package no.saua.engine;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.opengles.GL10;

import no.saua.mousekiller.Camera;

import android.content.res.AssetManager;

public abstract class State {
	private CopyOnWriteArrayList<Entity> entities;
	private Camera camera;
	
	public State() {
		entities = new CopyOnWriteArrayList<Entity>();
	}
	
	public CopyOnWriteArrayList<Entity> getEntities() {
		return entities;
	}
	
	public void addCollideableEntity(Entity e) {
		entities.add(e);
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public Camera getCamera() {
		return camera;
	}

	public abstract void init(Engine engine, GL10 gl, AssetManager assets);
	public abstract void update(Engine engine, float dt);
	public abstract void render(Engine engine, GL10 gl);
	
	public abstract void onTouchPress(Engine e, float x, float y);
	public abstract void onTouchDrag(Engine e, float x1, float y1, float x2, float y2, boolean done);
}
