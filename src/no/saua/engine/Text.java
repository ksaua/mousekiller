package no.saua.engine;

import javax.microedition.khronos.opengles.GL10;

public class Text {
	private TextureGrid tg;
	private int x;
	private int y;
	
	public Text(TextureGrid tg, int posx, int posy) {
		this.tg = tg;
		this.x = posx;
		this.y = posy;
	}
	
	public void render(GL10 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(x, y, 0);
		tg.render(gl);	
		gl.glPopMatrix();
	}

	public void release(GL10 gl) {
		tg.releaseHardwareBuffers(gl);
	}
}
