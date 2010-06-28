package no.saua.engine.collisionsystem;

import java.util.ArrayList;

import no.saua.engine.Entity;
import no.saua.engine.utils.Utils;

public class CollisionGroup {
	ArrayList<Entity> entities;
	ArrayList<CollisionHandler> handlers;
	
	public CollisionGroup() {
		entities = new ArrayList<Entity>();
		handlers = new ArrayList<CollisionHandler>();
	}
	
	public void addEntity(Entity e) {
		entities.add(e);
	}
	
	public void removeEntity(Entity e) {
		entities.remove(e);
	}
	
	public void addHandler(CollisionHandler ch) {
		handlers.add(ch);
	}
	
	public void removeHandler(CollisionHandler ch) {
		handlers.remove(ch);
	}
	
	public void check() {
		for (int i = 0; i < entities.size(); i++) {
			for (int j = i + 1; j < entities.size(); j++) {
				check(entities.get(i), entities.get(j));
			}
		}
	}
	
	private void check(Entity a, Entity b) {
		if (a.collidable() && b.collidable()) {
			if (Utils.distanceSquared(b.getX(), b.getY(), a.getX(), a.getY()) < Math.pow(b.getCollisionRadius() + a.getCollisionRadius(), 2)) {
				for (CollisionHandler ch: handlers) {
					ch.collisionOccured(a, b);
				}
			}
		}
	}
}
