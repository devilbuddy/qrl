package com.dg.qrl;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.badlogic.gdx.Gdx;
import com.dg.qrl.World.Actor;

public class Player extends Entity implements Actor {

	private final String tag = "Player";
	
	private final World world;
	
	private AtomicBoolean canAct = new AtomicBoolean(false);
	private List<Point> path;
	
	public Player(World world) {
		super();
		this.world = world;
	}

	@Override
	public void act() {
		Gdx.app.log(tag, "act");
		canAct.set(true);
		world.getScheduler().lock();
		
		stepPathIfPossible();
	}

	public boolean canAct() {
		return canAct.get();
	}
	
	public void setPath(List<Point> path) {
		this.path = path;
		stepPathIfPossible();
	}
	
	private void stepPathIfPossible() {
		if(canAct() && path != null && path.size() > 0) {
			Point next = path.remove(0);
			world.moveEntity(this, next);
			world.updateFieldOfView();
			world.getScheduler().unlock(0.05f);
			canAct.set(false);
		} 
	}
	
}
