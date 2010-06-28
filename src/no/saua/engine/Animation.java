package no.saua.engine;

public class Animation {
	private final float td;
	private float current_time;
	private final Texture[] textures;
	public Animation(Texture[] textures, float timedifference) {
		this.td = timedifference;
		this.textures = textures;
		this.current_time = 0;
	}
	
	public void update(float dt) {
		current_time += dt;
	}
	
	public Texture getCurrentTexture() {
		return textures[(int)(current_time / td) % textures.length];
	}
}
