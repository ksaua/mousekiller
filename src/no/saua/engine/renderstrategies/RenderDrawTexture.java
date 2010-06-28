package no.saua.engine.renderstrategies;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11Ext;

import no.saua.engine.Texture;

public class RenderDrawTexture implements UIRenderStrategy {
	public void init(GL10 gl) {}
	
	public void render(GL10 gl, Texture texture, float posx, float posy, float width, float height) {
		gl.glEnable(GL10.GL_TEXTURE_2D);
		texture.bind(gl);
		((GL11Ext) gl).glDrawTexfOES(posx - width / 2f, posy - height / 2f, 0, width, height);
	}
}
