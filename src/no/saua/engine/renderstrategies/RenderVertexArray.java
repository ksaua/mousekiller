package no.saua.engine.renderstrategies;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import no.saua.engine.Texture;
import no.saua.engine.utils.Color4f;

public class RenderVertexArray implements GenericRenderStrategy {

	private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;
	private FloatBuffer textureBuffer;

	public void init(GL10 gl) {
		final float[] vertices = new float[] {
				-0.5f, 0.5f, 0.0f,  // 0, Top Left 
				-0.5f,-0.5f, 0.0f,  // 1, Bottom Left
				0.5f,-0.5f, 0.0f,  // 2, Bottom Right
				0.5f, 0.5f, 0.0f,  // 3, Top Right
		};

		final short[] indices = { 0, 1, 2, 0, 2, 3 };

		final float[] texureIndices = new float[] {
				0, 0,
				0, 1,
				1, 1,
				1, 0};

		// Set up Vertexbuffer
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		// Set up Indexbuffer
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);

		// Set up Texturebuffer
		ByteBuffer tbb = ByteBuffer.allocateDirect(texureIndices.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		textureBuffer = tbb.asFloatBuffer();
		textureBuffer.put(texureIndices);
		textureBuffer.position(0);
	}

	public void render(GL10 gl, Texture texture, float posx, float posy, float width, float height) {
		gl.glEnable(GL10.GL_TEXTURE_2D);
		texture.bind(gl);
		
		gl.glTranslatef(posx, posy, 0);
		gl.glScalef(width, height, 0);
		
		// Enabled the vertices buffer for writing and to be used during
		// rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		// Specifies the location and data format of an array of vertex
		// coordinates to use when rendering.
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0,
                                 vertexBuffer);
		
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

		gl.glDrawElements(GL10.GL_TRIANGLES, indexBuffer.capacity(), GL10.GL_UNSIGNED_SHORT, indexBuffer);

		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
	public void render(GL10 gl, Texture texture, int width, int height) {
		render(gl, texture, 0, 0, texture.getWidth(), texture.getHeight());	
	}

	public void render(GL10 gl, Texture texture) {
		render(gl, texture, 0, 0);
	}

	public void render(GL10 gl, Color4f color, float posx, float posy, float width,
			float height) {
		Log.e("RenderDrawTexture", "Render color not implemented");	
	}
}
