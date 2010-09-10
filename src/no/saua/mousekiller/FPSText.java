package no.saua.mousekiller;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import no.saua.engine.Engine;
import no.saua.engine.Font;
import no.saua.engine.Text;

public class FPSText {
	private final static int updateFrequency = 5;
	private float timeToUpdate = 0;
	private Engine engine;
	private Font font;
	private Text text;
	
	public FPSText(Engine e, Font font) {
		engine = e;
		this.font = font;
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
		Log.e("Fpstext", "FPS: " + String.valueOf((int)engine.getFPS()));
		if (text != null) text.release(gl);
		text = font.makeText(gl, String.valueOf((int)engine.getFPS()), 0, 0);
	}
}
