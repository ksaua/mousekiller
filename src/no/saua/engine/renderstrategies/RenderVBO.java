package no.saua.engine.renderstrategies;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.util.Log;

import no.saua.engine.Texture;

public class RenderVBO implements GenericRenderStrategy {

	private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;
	private FloatBuffer textureBuffer;
	
	private int vbId;
	private int ibId;
	private int tbId;

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

		// Initialize gl buffers
		if (gl instanceof GL11) {
			GL11 gl11 = (GL11)gl;
			int[] buffer = new int[1];

			// Allocate and fill the vertex buffer.
			gl11.glGenBuffers(1, buffer, 0);
			vbId = buffer[0];
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, vbId);
			final int vertexSize = vertexBuffer.capacity() * 4; 
			gl11.glBufferData(GL11.GL_ARRAY_BUFFER, vertexSize, 
					vertexBuffer, GL11.GL_STATIC_DRAW);

			// Allocate and fill the texture coordinate buffer.
			gl11.glGenBuffers(1, buffer, 0);
			tbId = buffer[0];
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, tbId);
			final int texCoordSize = textureBuffer.capacity() * 4;
			gl11.glBufferData(GL11.GL_ARRAY_BUFFER, texCoordSize, textureBuffer, GL11.GL_STATIC_DRAW);   

			// Allocate and fill the index buffer.
			gl11.glGenBuffers(1, buffer, 0);
			ibId = buffer[0];
			gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, ibId);
			// A char is 2 bytes.
			final int indexSize = indexBuffer.capacity() * 2;
			gl11.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, indexSize, indexBuffer, GL11.GL_STATIC_DRAW);

			// Unbind the element array buffer.
			gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);

//			mUseHardwareBuffers = true;

			if (vbId == 0 || tbId == 0 || ibId == 0 || gl11.glGetError() != 0) {
				Log.e("RenderVBO", "Not available");
			}
//			assert vbId != 0;
//			assert tbId != 0;
//			assert ibId != 0;
//			assert gl11.glGetError() == 0;


		}
	}



	public void render(GL10 gl, Texture texture, float posx, float posy, float width, float height) {
		gl.glEnable(GL10.GL_TEXTURE_2D);
		texture.bind(gl);
		
		gl.glTranslatef(posx, posy, 0);
		gl.glScalef(width, height, 1f);
		

		
		GL11 gl11 = (GL11)gl;
        // draw using hardware buffers
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, vbId);
        gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
        
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, tbId);
        gl11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);

        
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, ibId);
        
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        
        gl11.glDrawElements(GL11.GL_TRIANGLES, indexBuffer.capacity(), GL11.GL_UNSIGNED_SHORT, 0);
        
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
        

	}



	public void render(GL10 gl, Texture texture, int width, int height) {
		render(gl, texture, 0, 0, texture.getWidth(), texture.getHeight());	
	}

	public void render(GL10 gl, Texture texture) {
		render(gl, texture, 0, 0);
	}
}
