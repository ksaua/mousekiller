package no.saua.engine;

import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import no.saua.engine.TextureGrid.Tilerotation;
import no.saua.engine.utils.Vector2i;

public class Font {
	HashMap<Character, Vector2i> charPositions;
	Texture texture;
	String[] chars;
	int charWidth;
	int charHeight;
	
	public Font(Texture texture, String[] chars, int charWidth, int charHeight) {
		this.texture = texture;
		this.charWidth = charWidth;
		this.charHeight = charHeight;
		charPositions = new HashMap<Character, Vector2i>();
		for (int y = 0; y < chars.length; y++) {
			for (int x = 0; x < chars[y].length(); x++) {
				charPositions.put(chars[y].charAt(x), new Vector2i(x, y));
			}
		}
	}
	
	public Font(Texture texture, String string, int charWidth, int charHeight) {
		this(texture, new String[]{string}, charWidth, charHeight);
	}

	public Text makeText(GL10 gl, String text, int posx, int posy) {
		TextureGrid tg = new TextureGrid(texture, text.length(), 1, charWidth, charHeight);
		for (int i = 0; i < text.length(); i++) {
			Vector2i charpos = charPositions.get(text.charAt(i));
			tg.set(i, 0, charpos.x, charpos.y, Tilerotation.r0);
		}
		tg.generateBuffers(gl);
		return new Text(tg, posx, posy);
	}

	public int getCharWidth() {
		return charWidth;
	}

	public int getCharHeight() {
		return charHeight;
	}
}
