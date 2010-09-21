package no.saua.mousekiller;

import no.saua.engine.Texture;
import no.saua.engine.gui.GuiEntity;

public class SidebarItem extends GuiEntity {
	
	private float origx, origy;
	private boolean dragable;
	

	public SidebarItem(float posx, float posy, Texture texture) {
		setPosition(posx, posy);
		setTexture(texture);
		setCollisionRadius(25);
		
		dragable = true;
		origx = posx;
		origy = posy;
	}
	
	public void resetPosition() {
		setPosition(origx, origy);
	}
	
	public void setDragable(boolean b) {
		dragable = b;
	}

	public boolean isDraggable() {
		return dragable;
	}

	public float getOrigX() {
		return origx;
	}

	public float getOrigY() {
		return origy;
	}
}
