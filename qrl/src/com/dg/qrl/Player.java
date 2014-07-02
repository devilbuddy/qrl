package com.dg.qrl;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.dg.qrl.World.Actor;

public class Player extends Entity implements Actor {

	private final World world;
	
	private AtomicBoolean canAct = new AtomicBoolean(false);
	private List<Point> path;
	
	public Player(World world) {
		super();
		this.world = world;
	}

	@Override
	public void act() {
		canAct.set(true);
		world.getScheduler().lock();
		
		if(path != null && path.size() > 0) {
			Point next = path.remove(0);
			world.moveEntity(this, next);
			world.updateFieldOfView();
			world.getScheduler().unlock(0.1f);
		} 
	}

	public boolean canAct() {
		return canAct.get();
	}
	
	public void setPath(List<Point> path) {
		this.path = path;
		world.getScheduler().unlock();
	}
}
