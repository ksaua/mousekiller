package no.saua.engine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.util.Log;

public class Grid {
	
	private float[] vertices;
	private short[] indices;
	private float[] textureCoords;
	private int vertexbufferId;
	private int texturebufferId;
	private int indexbufferId;
	private int tiles;
	
	public Grid(int tiles) {
		this.tiles = tiles;
		int numVertices = tiles * 4;
		
		// Set up what's known about vertices
		vertices = new float[numVertices * 2]; // 2 points per vertex
		
		// Set up indices
		// Needs to be drawn as triangles, the first tile is drawn in this order: 0, 2, 3, 0, 1, 3. So it has 6 indices per square
		
		indices = new short[tiles * 6];
		for (int i = 0; i < tiles; i++) {
			indices[i * 6 + 0] = (short) (i * 4 + 0);
			indices[i * 6 + 1] = (short) (i * 4 + 2);
			indices[i * 6 + 2] = (short) (i * 4 + 3);
			indices[i * 6 + 3] = (short) (i * 4 + 0);
			indices[i * 6 + 4] = (short) (i * 4 + 1);
			indices[i * 6 + 5] = (short) (i * 4 + 3);
		}
		
		textureCoords = new float[numVertices * 2];
		// Set up texture coordinates
		for (int j = 0; j < tiles; j++) {
			int i = 2 * 4 * j; // Offset, 4 vertices per tile, 2 floats per vertex
			// Bottom left corner
			textureCoords[i + 0] = 0;  
			textureCoords[i + 1] = 1;
			
			// Bottom right corner
			textureCoords[i + 2] = 1;  
			textureCoords[i + 3] = 1;	
			
			// Top-left
			textureCoords[i + 4] = 0;  
			textureCoords[i + 5] = 0; 

			// Top right corner
			textureCoords[i + 6] = 1;  
			textureCoords[i + 7] = 0;
		}
	}
	
	public void setSquare(int tileId, float x1, float y1, float x2, float y2) {
		int i = tileId * 8;
		
		// Bottom left corner
		vertices[i + 0] = x1;
		vertices[i + 1] = y1;
		
		// Bottom right corner
		vertices[i + 2] = x2;
		vertices[i + 3] = y1;
		
		// Top left corner
		vertices[i + 4] = x1;
		vertices[i + 5] = y2;
		
		// Top right corner
		vertices[i + 6] = x2;
		vertices[i + 7] = y2;
	}
	
	public void generateBuffers(GL10 gl) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		// Set up Indexbuffer
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		ShortBuffer indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);

		// Set up Texturebuffer
		ByteBuffer tbb = ByteBuffer.allocateDirect(textureCoords.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		FloatBuffer textureBuffer = tbb.asFloatBuffer();
		textureBuffer.put(textureCoords);
		textureBuffer.position(0);
		
		
		// Initialize gl buffers
		if (gl instanceof GL11) {
			GL11 gl11 = (GL11)gl;
			int[] buffer = new int[1];

			// Allocate and fill the vertex buffer.
			gl11.glGenBuffers(1, buffer, 0);
			vertexbufferId = buffer[0];
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, vertexbufferId);
			final int vertexSize = vertexBuffer.capacity() * 4; 
			gl11.glBufferData(GL11.GL_ARRAY_BUFFER, vertexSize, 
					vertexBuffer, GL11.GL_STATIC_DRAW);

			// Allocate and fill the texture coordinate buffer.
			gl11.glGenBuffers(1, buffer, 0);
			texturebufferId = buffer[0];
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, texturebufferId);
			final int texCoordSize = textureBuffer.capacity() * 4;
			gl11.glBufferData(GL11.GL_ARRAY_BUFFER, texCoordSize, textureBuffer, GL11.GL_STATIC_DRAW);   

			// Allocate and fill the index buffer.
			gl11.glGenBuffers(1, buffer, 0);
			indexbufferId = buffer[0];
			gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, indexbufferId);
			// A short is 2 bytes.
			final int indexSize = indexBuffer.capacity() * 2;
			gl11.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, indexSize, indexBuffer, GL11.GL_STATIC_DRAW);

			// Unbind the element array buffer.
			gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);

			if (vertexbufferId == 0 || texturebufferId == 0 || indexbufferId == 0 || gl.glGetError() != 0) {
				Log.e("Grid", "VBO Not available: " + gl.glGetError() + ", " + gl.glGetString(gl.glGetError()));
			}
		}
	}
	
	
	 public void releaseHardwareBuffers(GL10 gl) {
		 GL11 gl11 = (GL11)gl;
		 int[] buffer = new int[1];
		 buffer[0] = vertexbufferId;
		 gl11.glDeleteBuffers(1, buffer, 0);

		 buffer[0] = texturebufferId;
		 gl11.glDeleteBuffers(1, buffer, 0);

		 buffer[0] = indexbufferId;
		 gl11.glDeleteBuffers(1, buffer, 0);
	 }
	 
	public void render(GL10 gl, Texture texture) {
		gl.glEnable(GL10.GL_TEXTURE_2D);
		texture.bind(gl);
		
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		GL11 gl11 = (GL11)gl;
        // draw using hardware buffers
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, vertexbufferId);
        gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
        
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, texturebufferId);
        gl11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
        
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, indexbufferId);
        gl11.glDrawElements(GL11.GL_TRIANGLES, indices.length, GL11.GL_UNSIGNED_SHORT, 0);
        
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
        
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        
//		if (vertexbufferId == 0 || texturebufferId == 0 || indexbufferId == 0 || gl11.glGetError() != 0) {
//			Log.e("Grid", "VBO Not available: " + gl11.glGetError() + ", " + gl11.glGetString(gl11.glGetError()));
//		}
	}
}
