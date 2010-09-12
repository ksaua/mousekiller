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
	
	public int getCharPositionX(Character c) {
		return charPositions.get(c).x;
	}
	public int getCharPositionY(Character c) {
		return charPositions.get(c).y;
	}

	public int getCharWidth() {
		return charWidth;
	}

	public int getCharHeight() {
		return charHeight;
	}

	public Texture getTexture() {
		return texture;
	}
}
