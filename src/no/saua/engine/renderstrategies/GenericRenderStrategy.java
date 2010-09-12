package no.saua.engine.renderstrategies;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Texture;
import no.saua.engine.utils.Color4f;

public interface GenericRenderStrategy extends UIRenderStrategy {
	public void render(GL10 gl, Texture texture);
	public void render(GL10 gl, Texture texture, int width, int height);
	public void render(GL10 gl, Color4f color, float posx, float posy, float width, float height);
}
