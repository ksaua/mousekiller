package no.saua.mousekiller;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

import no.saua.engine.Engine;
import no.saua.engine.utils.Color4f;
import no.saua.engine.utils.Utils;

public class Sidebar {
	public interface SidebarListener {
		public void sidebarItemDragged(SidebarItem item, float x, float y);
	}
	
	private SidebarListener sidebarListener;
	private ArrayList<SidebarItem> items;
	private Color4f bgColor;
	private Engine engine;
	
	public Sidebar(GL10 gl, Engine e) {
		items = new ArrayList<SidebarItem>();
		bgColor = new Color4f(0,0,0,0.35f);
		this.engine = e;
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
		Engine.normalStrategy.render(gl, bgColor, 50, engine.getScreenHeight() / 2f, 100, engine.getScreenHeight());
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
