package no.saua.mousekiller;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Engine;
import no.saua.engine.Entity;
import no.saua.engine.Font;
import no.saua.engine.GuiEntity;
import no.saua.engine.GuiText;
import no.saua.engine.utils.Color4f;
import no.saua.engine.utils.Utils;
import no.saua.mousekiller.entities.Mouse;

public class Sidebar {
	public interface SidebarListener {
		public void sidebarItemDragged(PlaceableSidebarItem item, float x, float y);
	}
	
	private SidebarListener sidebarListener;
	private ArrayList<PlaceableSidebarItem> items;
	private Color4f bgColor;
	private Engine engine;
	private GuiText males, females;
	private GuiEntity maleIcon, femaleIcon;
	
	public Sidebar(GL10 gl, Engine e, Font font) {
		items = new ArrayList<PlaceableSidebarItem>();
		bgColor = new Color4f(0,0,0,0.45f);
		this.engine = e;
		males = new GuiText(gl, font, "0", (int)(20 + 23), (int)(e.getScreenHeight() - 20 - font.getCharHeight() / 2));
		females = new GuiText(gl, font, "0", (int)(20 + 23), (int)(e.getScreenHeight() - 50 - font.getCharHeight() / 2));
		
		maleIcon = new GuiEntity();
		maleIcon.setPosition(20, e.getScreenHeight() - 20);
		maleIcon.setTexture(Mouse.male);
		femaleIcon = new GuiEntity();
		femaleIcon.setPosition(20, e.getScreenHeight() - 50);
		femaleIcon.setTexture(Mouse.female);
	}
	
	public void addSidebarEntity(PlaceableSidebarItem si) {
		items.add(si);
	}
	
	public void removeGuiEntity(PlaceableSidebarItem si) {
		items.remove(si);
	}
	
	public void setListener(SidebarListener sl) {
		sidebarListener = sl;
	}
	
	public void render(GL10 gl) {
		Engine.normalStrategy.render(gl, bgColor, 36, engine.getScreenHeight() / 2f, 72, engine.getScreenHeight());
		for (PlaceableSidebarItem item: items) {
			item.render(gl);
		}
		males.render(gl);
		females.render(gl);
		maleIcon.render(gl);
		femaleIcon.render(gl);
	}
	
	public void setMiceAmount(GL10 gl, int males, int females) {
		this.males.setText(gl, String.valueOf(males));
		this.females.setText(gl, String.valueOf(females));
	}
	
	public void drag(float startx, float starty, float x, float y, boolean done) {
		for (PlaceableSidebarItem item: items) {
			if (item.isDraggable()) {
				if (Utils.distanceSquared(startx, starty, item.getOrigX(), item.getOrigY()) < item.getIconRadius() * item.getIconRadius()) {
					if (!done) {
						item.setIconPosition(x, y);
					} else {
						sidebarListener.sidebarItemDragged(item, x, y);
						item.resetIconPosition();
					}
					return;
				}
			}
		}
	}
}
