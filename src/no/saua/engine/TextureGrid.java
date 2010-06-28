package no.saua.engine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.util.Log;

/**
 * Generates a Vertex-buffer-object which is filled with a grid of textures.
 * @author Knut Saua Mathiesen
 *
 */
public class TextureGrid {
	
	public enum Tilerotation {r0, r90, r180, r270};
	
	private Texture texture;
	private int columns, rows;
	private int tilewidth, tileheight;
	
	private float[] vertices;
	private short[] indices;
	private float[] textureCoords;
	
	private int vertexbufferId;
	private int indexbufferId;
	private int texturebufferId;
	
	private float tileTexRatioX;
	private float tileTexRatioY;
	private int textureRows;
	private int textureColumns;
	
	public TextureGrid(Texture texture, int columns, int rows, int tilewidth, int tileheight) {
		this.texture = texture;
		this.columns = columns;
		this.rows = rows;
		this.tilewidth = tilewidth;
		this.tileheight = tileheight;
		
		tileTexRatioX = tilewidth / (float)texture.getWidth();
		tileTexRatioY = tileheight / (float)texture.getHeight();
		textureColumns = (int) (1 / tileTexRatioX);
		textureRows = (int) (1 / tileTexRatioY); 
		
		/* Since texture coordinates is linked to a vertex, we must create a single square with all its independent vertices for each tile.
		 * The vertices is set up in this fashion:
		 * 10      11 14       15
		 *  |--------|--------|
		 *  |        |        |
		 *  |        |        |
		 * 8|       9|12      |13
		 *  |--------|--------|
		 * 2|       3|6       |7
		 *  |        |        |
		 *  |        |        |
		 *  |--------|--------|
		 * 0        1 4        5
		 */
		
		int numTiles = columns * rows;
		// Set up vertices 
		int numVertices = 4 * numTiles;
		vertices = new float[numVertices * 2]; // 2 points per vertice
		
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < columns; x++) {
				int i = (y * columns + x) * 8; // Offset
				// Bottom left corner
				vertices[i + 0] = tilewidth * x;
				vertices[i + 1] = tileheight * y;
				
				// Bottom right corner
				vertices[i + 2] = tilewidth * (x + 1);
				vertices[i + 3] = tileheight * y;
				
				// Top left corner
				vertices[i + 4] = tilewidth * x;
				vertices[i + 5] = tileheight * (y + 1);
				
				// Top right corner
				vertices[i + 6] = tilewidth * (x + 1);
				vertices[i + 7] = tileheight * (y + 1);
			}
		}
		
		// Set up indices
		// Needs to be drawn as triangles, the first tile is drawn in this order: 0, 2, 3, 0, 1, 3. So it has 6 indices per square
		
		indices = new short[numTiles * 6];
		for (int i = 0; i < numTiles; i++) {
			indices[i * 6 + 0] = (short) (i * 4 + 0);
			indices[i * 6 + 1] = (short) (i * 4 + 2);
			indices[i * 6 + 2] = (short) (i * 4 + 3);
			indices[i * 6 + 3] = (short) (i * 4 + 0);
			indices[i * 6 + 4] = (short) (i * 4 + 1);
			indices[i * 6 + 5] = (short) (i * 4 + 3);
		}
		
		// Set up what's known about texturesCoords
		textureCoords = new float[numVertices * 2];
	} 
	
	public void set(int column, int row, int textureX, int textureY, Tilerotation rotation) {
		int j = row * columns + column; // Tiles it should jump over
		int i = 2 * 4 * j; // Offset, 4 vertices per tile, 2 floats per vertex
		
		if (rotation == Tilerotation.r0) {
			// Bottom left corner
			textureCoords[i + 0] = tileTexRatioX * textureX;  
			textureCoords[i + 1] = tileTexRatioY * (textureY + 1);
			
			// Bottom right corner
			textureCoords[i + 2] = tileTexRatioX * (textureX + 1);  
			textureCoords[i + 3] = tileTexRatioY * (textureY + 1);

			// Top right corner
			textureCoords[i + 6] = tileTexRatioX * (textureX + 1);  
			textureCoords[i + 7] = tileTexRatioY * textureY;
	
			// Top-left
			textureCoords[i + 4] = tileTexRatioX * textureX;  
			textureCoords[i + 5] = tileTexRatioY * textureY; 
		} else if (rotation == Tilerotation.r90) {
			// Bottom left corner
			textureCoords[i + 0] = tileTexRatioX * textureX;  
			textureCoords[i + 1] = tileTexRatioY * textureY; 
			
			// Bottom right corner
			textureCoords[i + 2] = tileTexRatioX * textureX; 
			textureCoords[i + 3] = tileTexRatioY * (textureY + 1);

			// Top right corner
			textureCoords[i + 6] = tileTexRatioX * (textureX + 1);  
			textureCoords[i + 7] = tileTexRatioY * (textureY + 1);
	
			// Top-left
			textureCoords[i + 4] = tileTexRatioX * (textureX + 1);  
			textureCoords[i + 5] = tileTexRatioY * textureY;
		} else if (rotation == Tilerotation.r180) {
			// Bottom left corner
			textureCoords[i + 0] = tileTexRatioX * (textureX + 1);  
			textureCoords[i + 1] = tileTexRatioY * textureY;
			
			// Bottom right corner
			textureCoords[i + 2] = tileTexRatioX * textureX;
			textureCoords[i + 3] = tileTexRatioY * textureY; 

			// Top right corner
			textureCoords[i + 6] = tileTexRatioX * textureX;  
			textureCoords[i + 7] = tileTexRatioY * (textureY + 1);
				
			// Top-left
			textureCoords[i + 4] = tileTexRatioX * (textureX + 1);  
			textureCoords[i + 5] = tileTexRatioY * (textureY + 1);	
		} else if (rotation == Tilerotation.r270) {
			// Bottom left corner
			textureCoords[i + 0] = tileTexRatioX * (textureX + 1); 
			textureCoords[i + 1] = tileTexRatioY * (textureY + 1);
			
			// Bottom right corner
			textureCoords[i + 2] = tileTexRatioX * (textureX + 1);  
			textureCoords[i + 3] = tileTexRatioY * textureY;

			// Top right corner
			textureCoords[i + 6] = tileTexRatioX * textureX;
			textureCoords[i + 7] = tileTexRatioY * textureY; 
				
			// Top-left
			textureCoords[i + 4] = tileTexRatioX * textureX;  
			textureCoords[i + 5] = tileTexRatioY * (textureY + 1);
		} 
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

			if (vertexbufferId == 0 || texturebufferId == 0 || indexbufferId == 0 || gl11.glGetError() != 0) {
				Log.e("TextureGrid", "Not available: " + gl11.glGetError());
			}
		}
	}
	
	public void render(GL10 gl) {
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
//			Log.e("RenderVBO", "Not available: " + gl11.glGetError());
//		}
	}
}
