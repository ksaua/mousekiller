package no.saua.engine.utils;

public class Utils {
	public static int randint(int to) {
		return (int) (Math.random() * (to + 1));
	}
	public static int randint(int from, int to) {
		return randint(to - from) + from;
	}
	
	public static float distanceSquared(float x1, float y1, float x2, float y2) {
		return (float) (Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
}
