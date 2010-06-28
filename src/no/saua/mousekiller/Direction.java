package no.saua.mousekiller;

public class Direction {
	public static final byte UP 	= 1;
	public static final byte DOWN 	= 2;
	public static final byte LEFT 	= 4;
	public static final byte RIGHT 	= 8;
	
	public static final byte ALL[] = new byte[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};

	
	public static byte getReverseDirection(byte d) {
		switch (d) {
			case Direction.UP: return Direction.DOWN;
			case Direction.DOWN: return Direction.UP;
			case Direction.LEFT: return Direction.RIGHT;
			case Direction.RIGHT: return Direction.LEFT;
		}
		return 0;
	}
	
	public static int getX(byte d) {
		return d == Direction.LEFT ? -1 : d == Direction.RIGHT ? 1 : 0;
	}
	public static int getY(byte d) {
		return d == Direction.DOWN ? -1 : d == Direction.UP ? 1 : 0;
	}

	public static float rotation(byte direction) {
		switch(direction) {
			case Direction.UP: return 0;
			case Direction.DOWN: return 180;
			case Direction.LEFT: return 90;
			case Direction.RIGHT: return 270;
		}
		return 0;
	}
	public static String str(byte d) {
		switch(d) {
			case Direction.UP: return "Up";
			case Direction.DOWN: return "Down";
			case Direction.LEFT: return "Left";
			case Direction.RIGHT: return "Right";
		}
		return "None";
	}
}
