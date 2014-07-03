package com.dg.qrl;

import java.util.Random;

import com.dg.qrl.World.Actor;

public class Monster extends Entity implements Actor {

	private final World world;
	
	private final class Brain {
		private Random random = new Random();
		public void act(Monster monster, World world) {
			if(random.nextBoolean()) {
				Point position = new Point();
				position.set(monster.getPosition());
				
				if(random.nextBoolean()) {
					position.setX(position.getX() + 1);	
				} else {
					position.setX(position.getX() - 1);
				}
				if(world.isPassable(position.getX(), position.getY())) {
					world.moveEntity(monster, position);
				}
			}
		}
	}
	
	private Brain brain = new Brain();
	
	public Monster(World world) {
		super();
		this.world = world;
	}
	
	@Override
	public void act() {
		brain.act(this, world);
	}

}
