package no.saua.mousekiller;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Engine;
import no.saua.engine.utils.Color4f;
import no.saua.engine.utils.Utils;

public class Sidebar {
	public interface SidebarListener {
		public void sidebarItemDragged(PlaceableSidebarItem item, float x, float y);
	}
	
	private SidebarListener sidebarListener;
	private ArrayList<PlaceableSidebarItem> items;
	private Color4f bgColor;
	private Engine engine;
	
	public Sidebar(GL10 gl, Engine e) {
		items = new ArrayList<PlaceableSidebarItem>();
		bgColor = new Color4f(0,0,0,0.45f);
		this.engine = e;
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
