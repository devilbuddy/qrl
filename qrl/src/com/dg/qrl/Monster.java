package com.dg.qrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.dg.qrl.QrlGame.GameController;
import com.dg.qrl.World.Actor;

public class Monster extends Entity implements Actor {

	public enum Type {
		ORC,
		SNAKE
	}
	
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
			} else {
				Player player = world.getPlayer();
				if(world.existsLineOfSight(monster.getPosition(), player.getPosition(), monster.getProperties().alertRadius)) {
					monster.setState(StateKey.MOVE_AND_ATTACK);	
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
		
		private Point selectRandomAdjacentTile(World world, Point position) {
			
			List<Point> possible = new ArrayList<Entity.Point>();
			for(int dx = -1; dx <=1; dx++) {
				for(int dy = -1; dy <= 1; dy++) {
					if(dx != 0 && dy != 0) {
						if(world.isPassable(position.getX() + dx, position.getY() + dy)) {
							possible.add(new Point(position.getX() + dx, position.getY() + dy));
						}
					}
				}
			}
			if(possible.size() > 0) {
				Collections.shuffle(possible);
				return possible.get(0);
			}
			return null;
		}
		
		@Override
		public void act(Monster monster, World world) {
			Player player = world.getPlayer();
			if(monster.getPosition().isAdjacentTo(player.getPosition())) {
				monster.gameController.attack(monster, player);
			} else {
				Point target = selectRandomAdjacentTile(world, player.getPosition());
				if(target != null) {
					List<Point> path = world.findPath(monster.getPosition(), target);
					if(path != null) {
						
						Point next = path.get(0);
						if(world.isPassable(next)) {
							world.moveEntity(monster, next);	
							return;
						}
					}
				}
				Gdx.app.log(tag, "no path");
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
	
	private final Type type;
	private final World world;
	private final GameController gameController;
	private boolean canAct = false;
	private State state;
	private MonsterProperties properties = new MonsterProperties();
	
	public Monster(Type type, World world, GameController gameController) {
		super(true, type.toString());
		this.type = type;
		this.world = world;
		this.gameController = gameController;
		setState(StateKey.IDLE);
	}
	
	public Type getType() {
		return type;
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
	
	@Override
	public void onRemoved() {
		world.getScheduler().remove(this);
	}
}
