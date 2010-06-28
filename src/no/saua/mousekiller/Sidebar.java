package no.saua.mousekiller;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.utils.Utils;

public class Sidebar {
	public interface SidebarListener {
		public void sidebarItemDragged(SidebarItem item, float x, float y);
	}
	
	private SidebarListener sidebarListener;
	private ArrayList<SidebarItem> items;
	
	public Sidebar() {
		items = new ArrayList<SidebarItem>();
	}
	
	public void addGuiEntity(SidebarItem si) {
		items.add(si);
	}
	
	public void removeGuiEntity(SidebarItem si) {
		items.remove(si);
	}
	
	public void setListener(SidebarListener sl) {
		sidebarListener = sl;
	}
	
	public void render(GL10 gl) {
		for (SidebarItem item: items) {
			item.render(gl);
		}
	}
	
	public void drag(float startx, float starty, float x, float y, boolean done) {
		for (SidebarItem item: items) {
			if (item.isDraggable()) {
				if (Utils.distanceSquared(startx, starty, item.getOrigX(), item.getOrigY()) < item.getCollisionRadius() * item.getCollisionRadius()) {
					if (!done) {
						item.setPosition(x, y);
					} else {
						sidebarListener.sidebarItemDragged(item, x, y);
						item.resetPosition();
					}
					return;
				}
			}
		}
	}
}
