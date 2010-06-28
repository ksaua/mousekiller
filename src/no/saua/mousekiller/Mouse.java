package no.saua.mousekiller;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import no.saua.engine.Entity;
import no.saua.engine.Texture;
import no.saua.engine.utils.Utils;
import no.saua.engine.utils.Vector2i;
import android.content.res.AssetManager;

public class Mouse extends Entity {
	private static final float speed = 25;

	private static Texture male;
	private static Texture female;
	private static Texture child;
	public enum Sex {male, female};

	private enum State {idle, fucking, pregnant, growing}; 
	private Sex sex;
	private State state;

	private byte direction;
	
	// How long until it should change state
	private float stateTime;

	private Map map;
	
	public Mouse(Map map) {
		this(getRandomSex(), map);
	}
	
	public Mouse(Sex sex, Map map) {
		this.map = map;
		this.sex = sex;
		Vector2i p = map.getRandomRoad();
		setPosition(map.getTileCenterX(p.x), map.getTileCenterY(p.y));
		findNewDirection(map);
		setState(State.idle); // Also fixes sprite
		setCollisionRadius(5);
		setCollidable(true);
	}
	
	public Mouse(Sex sex, Map map, float posx, float posy) {
		this.map = map;
		this.sex = sex;
		setPosition(posx, posy);
		findNewDirection(map);
		setState(State.idle); // Also fixes sprite
		setCollisionRadius(5);
		setCollidable(true);
	}

	public void fixSprite() {
		if (state == State.growing)
			setTexture(child);
		else if (sex == Sex.male)
			setTexture(male);
		else if (sex == Sex.female)
			setTexture(female);
	}
	
	public void setState(State s) {
		state = s;
		if (State.idle == s) {
		} else if (State.fucking == s) {
			stateTime = 5;
		} else if (State.pregnant == s) {
			stateTime = 10;
		} else if (State.growing == s) {
			stateTime = 15;
		}
		fixSprite();
	}
	
	public void setDirection(byte b) {
		direction = b;
		rotation = Direction.rotation(direction);
	}
	
	public byte getDirection() {
		return direction;
	}
	
	private void findNewDirection(Map map) {
		int cpx = map.getTileX(posx);
		int cpy = map.getTileY(posy);
		
		byte availableDirections = 0;
		byte numavail = 0;
		for (byte d: Direction.ALL) {
			if (d != Direction.getReverseDirection(direction) && map.isWalkable(cpx + Direction.getX(d), cpy + Direction.getY(d))) {
				availableDirections |= d;
				numavail++;
			}
		}
		if (numavail == 0) {
			direction = Direction.getReverseDirection(direction);
		} else if (numavail == 1) {
			direction = availableDirections;
		} else {
			int chosen = Utils.randint(1, numavail);
			for (byte d: Direction.ALL) {
				if ((d & availableDirections) != 0) {
					if (chosen == 1) {
						direction = d;
						break;
					} else {
						chosen--;
					}
				}
			}
		}
		rotation = Direction.rotation(direction);
	}

	@Override
	public void update(float dt, GameState gs) {
		
		if (State.idle == state) {
			moveInDirection(dt, map);
		} else {
			stateTime -= dt;
			if (State.fucking == state) {
				if (stateTime < 0) {
					if (sex == Sex.male) {
						setState(State.idle);
					} else if (sex == Sex.female) {
						setState(State.pregnant);
					}
				}
			} else if (State.pregnant == state) {
				if (stateTime < 0) {
					setState(State.idle);
					Mouse m = new Mouse(getRandomSex(), map, posx, posy);
					m.setDirection(Direction.getReverseDirection(direction));
					m.setState(State.growing);
					gs.addCollideableEntity(m);
				}
				moveInDirection(dt, map);
			} else if (State.growing == state) {
				if (stateTime < 0) {
					setState(State.idle);
				}
				moveInDirection(dt, map);
			}
		}
	}
	
	public void moveInDirection(float dt, Map map) {
		float cpxt = map.getTileCenterX(map.getTileX(posx));
		float cpyt = map.getTileCenterY(map.getTileY(posy));
		
		// Is mouse left or down of center
		boolean x1 = posx < cpxt;
		boolean y1 = posy < cpyt; 
		move(Direction.getX(direction) * dt * speed, Direction.getY(direction) * dt * speed);

		// Is mouse left or down of center2
		boolean x2 = posx < cpxt;
		boolean y2 = posy < cpyt; 		
		
		if ((x1 != x2) || (y1 != y2)) {
			findNewDirection(map);
			setPosition(cpxt + Direction.getX(direction) * 0.2f, cpyt + Direction.getY(direction) * 0.2f);
		}
	}

	public void collided(Mouse other) {
		// Only make a child if I am female and other is male
		if (sex == Sex.female && other.sex == Sex.male) {
			// In addition both must be wandering, and not fucking anyone else (or pregnant)
			if (state == State.idle && other.state == State.idle) {
				setState(State.fucking);
				other.setState(State.fucking); 
			}
		}
	}

	public static Sex getRandomSex() {
		return Utils.randint(0, 1) == 0 ? Sex.male : Sex.female;
	}
	
	public void setSex(Sex s) {
		if (this.sex == Sex.female && s == Sex.male && state == State.pregnant) setState(State.idle);
		this.sex = s;
		fixSprite();
	}
	
	public static void loadSprites(GL10 gl, AssetManager assets) throws IOException {
		male = Texture.loadTexture(gl, assets.open("textures/mouseblue.png"));
		female = Texture.loadTexture(gl, assets.open("textures/mousered.png"));
		child = Texture.loadTexture(gl, assets.open("textures/mousechild.png"));
	}
}
