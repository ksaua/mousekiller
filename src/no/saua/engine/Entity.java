package no.saua.engine;

import javax.microedition.khronos.opengles.GL10;

import no.saua.mousekiller.GameState;

public abstract class Entity {
	// Position
	protected float posx;
	protected float posy;
	
	protected float rotation;
	
	// Texture
	protected Texture texture;
	protected Animation animation;

	// Collision stuff
	private boolean collidable;
	private float collisionRadius;
	private boolean moveable;
	
	private boolean removing;
	

	public void setPosition(float x, float y) {
		posx = x;
		posy = y;
	}
	
	public float getX() {
		return posx;
	}
	
	public float getY() {
		return posy;
	}
	
	public void move(float x, float y) {
		posx += x;
		posy += y;
	}
	
	public boolean collidable() {
		return collidable;
	}
	
	public void setCollidable(boolean b) {
		this.collidable = b;
	}
	
	public void setCollisionRadius(float radius) {
		this.collisionRadius = radius;
	}
	
	public float getCollisionRadius() {
		return collisionRadius;
	}

	public void setAnimation(Animation anim) {
		this.animation = anim;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void remove() {
		removing = true;
	}
	
	public boolean isRemoving() {
		return removing;
	}
	
	public void setMoveable(boolean m) {
		moveable = m;
	}
	
	public boolean isMoveable() {
		return moveable;
	}
	
	public void update(float dt, GameState gs) {
		if (animation != null) animation.update(dt);
	}
	
	public void render(GL10 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(posx, posy, 0);
		gl.glRotatef(rotation, 0, 0, 1);
		if (animation != null) Engine.normalStrategy.render(gl, animation.getCurrentTexture());
		else if (texture != null) Engine.normalStrategy.render(gl, texture);
		gl.glPopMatrix();
	}
}
