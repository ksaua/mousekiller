package no.saua.engine;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.renderstrategies.GenericRenderStrategy;
import no.saua.engine.renderstrategies.RenderDrawTexture;
import no.saua.engine.renderstrategies.RenderVBO;
import no.saua.engine.renderstrategies.UIRenderStrategy;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Engine extends GLSurfaceView implements GLSurfaceView.Renderer, OnTouchListener {

	// Render strategies
	public static UIRenderStrategy uiStrategy;
	public static GenericRenderStrategy normalStrategy;
	
	long lastdrawtime;
	float fps;
	
	int screenWidth;
	int screenHeight;

	ArrayList<State> states;
	State currentstate;
	
	private GL10 gl;
	
	boolean running = true;
	
	public Engine(Context context) {
		super(context);
		states = new ArrayList<State>();
		setRenderer(this);
		setOnTouchListener(this);
		requestFocus();
	}
	
	public void addState(State state) {
		if (currentstate == null) currentstate = state;
		states.add(state);
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}
	
	public int getScreenHeight() {
		return screenHeight;
	}
	
	private static int PRESS_THRESHOLD = 150;
	private float touch_startx;
	private float touch_starty;
	
	public boolean onTouch(View v, MotionEvent event) {
//		float x = event.getX() - screenWidth / 2f;;
//		float y = screenHeight / 2f - event.getY();

		float x = event.getX();
		float y = screenHeight - event.getY();
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touch_startx = x;
			touch_starty = y;
			return true;
		}
		
		boolean inside_threshold = (event.getEventTime() - event.getDownTime()) < PRESS_THRESHOLD;
		if (event.getAction() == MotionEvent.ACTION_MOVE && inside_threshold) return true;
		
		if (event.getAction() == MotionEvent.ACTION_UP && inside_threshold) { //TODO: Or haven't moved 
			currentstate.onTouchPress(this, x, y);
			return true;
		}
		
		if (!inside_threshold && (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP)) {
			currentstate.onTouchDrag(this, touch_startx, touch_starty, x, y, event.getAction() == MotionEvent.ACTION_UP);
			return true;
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			touch_startx = x;
			touch_starty = y;
		}
		
		Log.d("Engine", "OnTouch, something happened: " + event.getAction());
		return false;
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		this.gl = gl;
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_BLEND); 
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		

		
		// Set up render strategies
		normalStrategy = new RenderVBO();
		uiStrategy = new RenderDrawTexture();
		normalStrategy.init(gl);
		uiStrategy.init(gl);
		
		this.gl = null;
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		this.gl = gl;
		
		this.screenWidth = width;
		this.screenHeight = height;
		
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, 0, width, 0, height);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		for (State state: states) {
			state.init(this, gl, getResources().getAssets());
		}
		
		this.gl = null;
		Runtime.getRuntime().gc();
	}
	
	public float getFPS() {
		return fps;
	}
	
	@Override
	public void onPause() {
		running = false;
	}
	
	@Override
	public void onResume() {
		lastdrawtime = 0;
		running = true;
	}
	
	public GL10 getGL() {
		if (gl == null) Log.d("Engine", "Trying to access gl object outside of a GL-enabled context?");
		return gl;
	}



	public void onDrawFrame(GL10 gl) {
		long time = SystemClock.uptimeMillis();
		float dt = 0;
		if (lastdrawtime != 0) {
			dt = (time - lastdrawtime ) / 1000f;
			fps = 1 / dt;
		}
		lastdrawtime = time;
		
		if (running) {
			this.gl = gl;
			currentstate.update(this, dt);
						
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			gl.glLoadIdentity();
			gl.glTranslatef(screenWidth / 2f, screenHeight / 2f, 0);

			currentstate.render(this, gl);
			this.gl = null;
		}
	}
}
