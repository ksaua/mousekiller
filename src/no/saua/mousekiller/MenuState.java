package no.saua.mousekiller;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Engine;
import no.saua.engine.State;
import no.saua.engine.Texture;
import no.saua.engine.gui.GuiContext;
import no.saua.engine.gui.GuiEntity;
import no.saua.engine.gui.GuiPressListener;
import android.content.res.AssetManager;

public class MenuState extends State {

	GuiContext context;
	private boolean start;
	private boolean exit;
	
	@Override
	public void init(Engine engine, GL10 gl, AssetManager assets) {
		try {
			context = new GuiContext();
			GuiEntity start = new GuiEntity(
					engine.getWidth() / 2f,
					engine.getHeight() / 2f + 50,
					Texture.loadTexture(gl, assets.open("textures/start.png")));
			
			start.setGuiPressListener(new GuiPressListener() {
				public void guiEntityPressed(GuiEntity source, float x, float y) {
					start();
				}
			});
			
			GuiEntity quit = new GuiEntity(
					engine.getWidth() / 2f,
					engine.getHeight() / 2f - 50,
					Texture.loadTexture(gl, assets.open("textures/quit.png")));
			
			quit.setGuiPressListener(new GuiPressListener() {
				public void guiEntityPressed(GuiEntity source, float x, float y) {
					quit();
				}
			});
			
			context.addEntity(start);
			context.addEntity(quit);

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}



	private void start() {
		start = true;
	}
	
	private void quit() {
		
	}
	
	@Override
	public void onTouchDrag(Engine e, float x1, float y1, float x2, float y2, boolean done) {
		if (done) context.press(x1, y1);
	}

	@Override
	public void onTouchPress(Engine e, float x, float y) {
		context.press(x, y);
	}

	@Override
	public void render(Engine engine, GL10 gl) {
		context.render(gl);
	}

	@Override
	public void update(Engine engine, float dt) {
		if (start) {
			GameState gs = (GameState) Engine.engine.getState("game");
			gs.loadMap(engine.getGL(), engine.getResources().getAssets(), "map");
			Engine.engine.setState("game");
			start = false;
		}
		
		else if (exit) {
			
		}
	}

}
