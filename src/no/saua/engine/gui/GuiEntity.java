package no.saua.engine.gui;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Engine;
import no.saua.engine.Entity;
import no.saua.engine.Texture;

public class GuiEntity extends Entity {
	private float width;
	private float height;
	private GuiPressListener gpl;
	
	public GuiEntity() {}
	public GuiEntity(float posx, float posy, Texture texture) {
		setPosition(posx, posy);
		setTexture(texture);
		setCollisionBox(texture.getWidth(), texture.getHeight());
	}

	@Override
	public void render(GL10 gl) {
		Engine.uiStrategy.render(gl, texture, posx, posy, texture.getWidth(), texture.getHeight());
	}
	
	public void setCollisionBox(float width, float height) {
		this.width = width;
		this.height = height;
	}
	
	public boolean contains(float x, float y) {
		return posx - width / 2f <= x && x <= posx + width / 2f && posy - height / 2f <= y && y <= posy + height / 2f; 
	}
	public void press(float x, float y) {
		if (contains(x, y) && gpl != null) {
			gpl.guiEntityPressed(this, x, y);
		}			
	}
	public void setGuiPressListener(GuiPressListener gpl) {
		this.gpl = gpl;
	}
}
