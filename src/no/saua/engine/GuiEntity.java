package no.saua.engine;

import javax.microedition.khronos.opengles.GL10;

public class GuiEntity extends Entity {
	@Override
	public void render(GL10 gl) {
		Engine.uiStrategy.render(gl, texture, posx, posy, texture.getWidth(), texture.getHeight());
	}
}
