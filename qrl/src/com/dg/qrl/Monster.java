package com.dg.qrl;

import java.util.Random;

import com.dg.qrl.World.Actor;

public class Monster extends Entity implements Actor {

	
	
	private interface State {
		void act(Monster monster, World world);
	}
			
	private static final Random RANDOM = new Random();

	private static final State moveRandomlyState  = new State() {
		@Override
		public void act(Monster monster, World world) {
			if(RANDOM.nextBoolean()) {
				Point position = new Point();
				position.set(monster.getPosition());
				switch(RANDOM.nextInt(5)) {
				case 0:
					position.setX(position.getX() + 1);
					break;
				case 1:
					position.setX(position.getX() - 1);
					break;
				case 2:
					position.setY(position.getY() + 1);
					break;
				case 3:
					position.setY(position.getY() - 1);
					break;
				}

				if(world.isPassable(position.getX(), position.getY())) {
					world.moveEntity(monster, position);
				}
			}
		}
	};
	
	private static final State idleState  = new State() {
		@Override
		public void act(Monster monster, World world) {
			
		}
	};
	
	private enum StateKey {
		IDLE(idleState),
		MOVE_RANDOMLY(moveRandomlyState);
		
		public final State state;
		private StateKey(State state) {
			this.state = state;
		}
	}
	

	
	private final World world;
	private boolean canAct = false;
	private State state;
	
	public Monster(World world) {
		super();
		this.world = world;
		setState(StateKey.IDLE);
	}
	
	@Override
	public void act() {
		canAct = true;
		state.act(this, world);
		canAct = false;
	}
	
	void setState(StateKey stateKey) {
		this.state = stateKey.state;
		if(canAct) {
			state.act(this, world);
		}
	}
}
