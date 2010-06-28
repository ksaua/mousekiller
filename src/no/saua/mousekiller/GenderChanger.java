package no.saua.mousekiller;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Entity;
import no.saua.engine.Texture;
import android.content.res.AssetManager;

public class GenderChanger extends Entity {
	public static Texture texMale;
	public static Texture texFemale;
	private boolean male;
	public GenderChanger(Map map, int tilex, int tiley, boolean male) {
		this.male = male;
		setPosition(map.getTileCenterX(tilex), map.getTileCenterY(tiley));
		setCollisionRadius(12);
		setCollidable(true);
		setTexture(male? texMale : texFemale);
	}
	
	public void collision(Mouse mouse) {
		mouse.setSex(male ? Mouse.Sex.male : Mouse.Sex.female);
		remove();
	}
	
	public static void loadSprites(GL10 gl, AssetManager assets) throws IOException {
		texMale = Texture.loadTexture(gl, assets.open("textures/male.png"));
		texFemale = Texture.loadTexture(gl, assets.open("textures/female.png"));
	}
}
