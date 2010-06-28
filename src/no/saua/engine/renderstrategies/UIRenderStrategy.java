package no.saua.engine.renderstrategies;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Texture;

public interface UIRenderStrategy {
	public void init(GL10 gl);
	public void render(GL10 gl, Texture texture, float posx, float posy, float width, float height);
}
