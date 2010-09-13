package no.saua.mousekiller.entities;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Entity;
import no.saua.engine.Texture;
import no.saua.mousekiller.Map;
import no.saua.mousekiller.MouseChangeListener;
import no.saua.mousekiller.PlaceableSidebarItem.SidebarItemCreator;
import android.content.res.AssetManager;

public class GenderChanger extends Entity {
	public static class MaleChangerCreator implements SidebarItemCreator {
		public Entity createItem(GL10 gl, MouseChangeListener mcl, Map map, int tilex, int tiley) {
			return new GenderChanger(mcl, map, tilex, tiley, true);
		}
		public Texture getIconTexture() { return texMale; }
	}
	
	public static class FemaleChangerCreator implements SidebarItemCreator {
		public Entity createItem(GL10 gl, MouseChangeListener mcl, Map map, int tilex, int tiley) {
			return new GenderChanger(mcl, map, tilex, tiley, false);
		}
		public Texture getIconTexture() { return texFemale; }
	}
	
	public static Texture texMale;
	public static Texture texFemale;
	private boolean male;
	private MouseChangeListener mcl;
	public GenderChanger(MouseChangeListener mcl, Map map, int tilex, int tiley, boolean male) {
		this.male = male;
		this.mcl = mcl;
		setPosition(map.getTileCenterX(tilex), map.getTileCenterY(tiley));
		setCollisionRadius(12);
		setCollidable(true);
		setMoveable(false);
		setTexture(male? texMale : texFemale);
	}
	
	public void collision(Mouse mouse) {
		if (mouse.getSex() == Mouse.Sex.male != male) {
			if (mouse.getState() != Mouse.State.growing) { // Don't change if it's a child.
				if (mouse.getSex() == Mouse.Sex.male)
					mcl.modifyMiceAmounts(-1, 1);
				else
					mcl.modifyMiceAmounts(1, -1);
			}
			mouse.setSex(male ? Mouse.Sex.male : Mouse.Sex.female);
		}
		
		remove();
	}
	
	public static void loadSprites(GL10 gl, AssetManager assets) throws IOException {
		texMale = Texture.loadTexture(gl, assets.open("textures/male.png"));
		texFemale = Texture.loadTexture(gl, assets.open("textures/female.png"));
	}
}
