package no.saua.engine.gui;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Font;
import no.saua.engine.TextureGrid;
import no.saua.engine.TextureGrid.Tilerotation;

public class GuiText extends GuiEntity {
	private TextureGrid tg;
	private Font font;
	private String text;
	
	public GuiText(GL10 gl, Font font, String text, int posx, int posy) {
		this.font = font; 
		setPosition(posx, posy);
		
		setText(gl, text);
	}
	
	public void setText(GL10 gl, String text) {
		if (text.equals(this.text)) return;
		
		if (tg != null) {
			tg.releaseHardwareBuffers(gl);
		}
		
		this.text = text;
		
		tg = new TextureGrid(font.getTexture(), text.length(), font.getCharWidth(), font.getCharHeight());
		for (int i = 0; i < text.length(); i++) {
			int x = font.getCharPositionX(text.charAt(i));
			int y = font.getCharPositionY(text.charAt(i));
			tg.set(i, i, 0, x, y, Tilerotation.r0);
		}
		tg.generateBuffers(gl);	
	}
	
	public void render(GL10 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(posx, posy, 0);
		tg.render(gl);	
		gl.glPopMatrix();
	}
}
