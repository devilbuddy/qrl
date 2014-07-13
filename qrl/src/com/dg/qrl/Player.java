package com.dg.qrl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.badlogic.gdx.Gdx;
import com.dg.qrl.World.Actor;

public class Player extends Entity implements Actor {

	private final String tag = "Player";
	
	private final World world;
	
	private AtomicBoolean canAct = new AtomicBoolean(false);
	private List<Point> path;
	
	private List<Card> cards = new ArrayList<Card>();
	
	
	public Player(World world) {
		super(true);
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
			if(world.isPassable(next)) {
				List<Card> cards = world.getEntities(next.getX(), next.getY(), Card.class);
				for(Card card : cards) {
					world.removeEntity(card);
					addCard(card);
					world.triggerUIRefresh();
				}
				world.moveEntity(this, next);
				world.updateFieldOfView();
				world.getScheduler().unlock(0.05f);
				canAct.set(false);	
			} else {
				path = null;
			}
			
		} 
	}
	
	public void addCard(Card card) {
		cards.add(card);
	}
	
	public void playCard(Card card) {
		cards.remove(card);
		world.getScheduler().unlock(0.05f);
		
	}
	
	public List<Card> getCards() {
		return cards;
	}
	
}
