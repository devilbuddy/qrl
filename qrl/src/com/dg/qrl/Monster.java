package com.dg.qrl;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.dg.qrl.World.Actor;

public class Monster extends Entity implements Actor {

	private interface State {
		void act(Monster monster, World world);
	}
			
	private static final Random RANDOM = new Random();
	
	private static final Direction getRandomDirection(Random random) {
		return Direction.MOVE_DIRECTIONS[random.nextInt(Direction.MOVE_DIRECTIONS.length)];
	}
	
	private static final State moveRandomlyState  = new State() {
		@Override
		public void act(Monster monster, World world) {
			if(RANDOM.nextBoolean()) {
				Point position = new Point();
				position.set(monster.getPosition());
				Direction moveDirection = getRandomDirection(RANDOM);
				position.translate(moveDirection);
				if(world.isPassable(position.getX(), position.getY())) {
					world.moveEntity(monster, position);
				}
			}
		}
	};
	
	private static final State idleState  = new State() {
		@Override
		public void act(Monster monster, World world) {
			Player player = world.getPlayer();
			if(world.existsLineOfSight(monster.getPosition(), player.getPosition(), monster.getProperties().alertRadius)) {
				monster.setState(StateKey.MOVE_AND_ATTACK);	
			}
		}
	};
	
	private static final State moveAndAttackState = new State() {
		
		@Override
		public void act(Monster monster, World world) {
			Player player = world.getPlayer();
			List<Point> path = world.findPath(monster.getPosition(), player.getPosition());
			if(path != null) {
				Point next = path.get(0);
				if(world.isPassable(next)) {
					world.moveEntity(monster, next);	
				}
			} else {
				monster.setState(StateKey.MOVE_RANDOMLY);
			}
			
		}
	};
	
	
	private enum StateKey {
		IDLE(idleState),
		MOVE_RANDOMLY(moveRandomlyState),
		MOVE_AND_ATTACK(moveAndAttackState);
		
		public final State state;
		private StateKey(State state) {
			this.state = state;
		}
	}
	

	private class MonsterProperties {
		public int alertRadius = 2;
	}
	
	private static final String tag = "Monster";
	
	private final World world;
	private boolean canAct = false;
	private State state;
	private MonsterProperties properties = new MonsterProperties();
	
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
		Gdx.app.log(tag, "setState: " + stateKey);
		this.state = stateKey.state;
		if(canAct) {
			state.act(this, world);
		}
	}
	
	MonsterProperties getProperties() {
		return properties;
	}
	
	boolean canSee(Point target) {
		return world.existsLineOfSight(getPosition(), target);
	}
}
