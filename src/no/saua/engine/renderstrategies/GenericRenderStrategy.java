package no.saua.engine.renderstrategies;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Texture;

public interface GenericRenderStrategy extends UIRenderStrategy {
	public void render(GL10 gl, Texture texture);
	public void render(GL10 gl, Texture texture, int width, int height);	
}
