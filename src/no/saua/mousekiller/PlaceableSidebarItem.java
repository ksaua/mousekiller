package no.saua.mousekiller;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Entity;
import no.saua.engine.Font;
import no.saua.engine.GuiEntity;
import no.saua.engine.GuiText;
import no.saua.engine.Texture;

public class PlaceableSidebarItem  {
	public interface SidebarItemCreator {
		public Entity createItem(GL10 gl, MouseChangeListener mcl, Map map, int tilex, int tiley);
		public Texture getIconTexture();
	}
	
	private GuiEntity icon;
	private float origx;
	private float origy;
	
	private SidebarItemCreator creator;
	
	private int amount;
	private GuiText text;
	private boolean textNeedsUpdating;
	public PlaceableSidebarItem(float posx, float posy, SidebarItemCreator creator, int startamount, Font font, GL10 gl) {
		icon = new GuiEntity();
		icon.setPosition(posx, posy);
		icon.setTexture(creator.getIconTexture());
		
		this.creator = creator;
		this.amount = startamount;
		
		origx = posx;
		origy = posy;
		
		text = new GuiText(gl, font, String.valueOf(startamount), (int)(origx + 23), (int)(origy - font.getCharHeight() / 2));
		
		updateText(gl);
	}
	
	public void render(GL10 gl) {
		icon.render(gl);
		if (textNeedsUpdating) {
			updateText(gl);
			textNeedsUpdating = false;
		}
		text.render(gl);
	}
	
	private void updateText(GL10 gl) {
		text.setText(gl, String.valueOf(amount));
	}

	public boolean isDraggable() {
		return amount > 0;
	}

	public void setIconPosition(float x, float y) {
		icon.setPosition(x, y);
	}

	public void resetIconPosition() {
		icon.setPosition(origx, origy);
	}

	public float getOrigX() {
		return origx;
	}

	public float getOrigY() {
		return origy;
	}

	public int getIconRadius() {
		return 25;
	}

	public Entity createItem(GL10 gl, MouseChangeListener mcl, Map map, int tilex, int tiley) {
		amount -= 1;
		textNeedsUpdating = true;
		return creator.createItem(gl, mcl, map, tilex, tiley);
	}
}
