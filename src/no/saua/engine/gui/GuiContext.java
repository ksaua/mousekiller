package no.saua.engine.gui;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class GuiContext {
	private ArrayList<GuiEntity> entities;
	
	public GuiContext() {
		entities = new ArrayList<GuiEntity>();
	}
	
	public void addEntity(GuiEntity ge) {
		entities.add(ge);
	}
	
	public void press(float x, float y) {
		for (GuiEntity ge: entities) {
			ge.press(x, y);
		} 
	}
	
	public void render(GL10 gl) {
		for (GuiEntity ge: entities) {
			ge.render(gl);
		}
	}
}
