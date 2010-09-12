package no.saua.mousekiller.entities;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Entity;
import no.saua.engine.Texture;
import no.saua.mousekiller.Direction;
import no.saua.mousekiller.Map;
import no.saua.mousekiller.PlaceableSidebarItem.SidebarItemCreator;
import android.content.res.AssetManager;

public class StopSign extends Entity {
	public static class StopSignCreator implements SidebarItemCreator {
		public Entity createItem(GL10 gl, Map map, int tilex, int tiley) {
			return new StopSign(map, tilex, tiley);
		}

		public Texture getIconTexture() { return tex; }
	}
	public static Texture tex;
	private int collisionsLeft;
	public StopSign(Map map, int tilex, int tiley) {
		setPosition(map.getTileCenterX(tilex), map.getTileCenterY(tiley));
		setCollisionRadius(12);
		setCollidable(true);
		setMoveable(false);
		setTexture(tex);
		collisionsLeft = 3;
	}
	
	public void collision(Mouse mouse) {
		mouse.setDirection(Direction.getReverseDirection(mouse.getDirection()));
		float mx = posx + Direction.getX(mouse.getDirection()) * (mouse.getCollisionRadius() + getCollisionRadius());
		float my = posy + Direction.getY(mouse.getDirection()) * (mouse.getCollisionRadius() + getCollisionRadius());
		mouse.setPosition(mx, my);
		
		collisionsLeft -= 1;
		if (collisionsLeft < 1) {
			remove();
		}
	}
	
	public static void loadSprites(GL10 gl, AssetManager assets) throws IOException {
		tex = Texture.loadTexture(gl, assets.open("textures/stop.png"));
	}
}
