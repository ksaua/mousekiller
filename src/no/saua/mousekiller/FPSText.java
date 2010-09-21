package no.saua.mousekiller;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import no.saua.engine.Engine;
import no.saua.engine.Font;
import no.saua.engine.gui.GuiText;

public class FPSText {
	private final static int updateFrequency = 5;
	private float timeToUpdate = 0;
	private Engine engine;
	private Font font;
	private GuiText text;
	
	public FPSText(Engine e, GL10 gl, Font font) {
		engine = e;
		this.font = font;
		text = new GuiText(gl, font, "0", e.getScreenWidth() - font.getCharWidth() * 2, e.getScreenHeight() - font.getCharHeight() * 1);
	}
	
	public void update(float dt, GL10 gl) {
		timeToUpdate -= dt;
		if (timeToUpdate < 0) {
			timeToUpdate = updateFrequency;
			updateText(gl);
		}
	}
	
	public void render(GL10 gl) {
		if (text != null) text.render(gl);
		else Log.e("FPSText", "Text is null");
	}
	
	private void updateText(GL10 gl) {
		Log.d("Fpstext", "FPS: " + String.valueOf((int)engine.getFPS()));
		text.setText(gl, String.valueOf((int)engine.getFPS()));
	}
}
