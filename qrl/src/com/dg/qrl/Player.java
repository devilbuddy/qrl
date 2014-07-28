package com.dg.qrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.dg.qrl.World.Actor;

public class Player extends Entity implements Actor {

	private final String tag = "Player";
	
	private final World world;
	private final MessageLog messageLog;
	
	private AtomicBoolean canAct = new AtomicBoolean(false);
	private List<Point> path;
	
	private List<Card> deck = new ArrayList<Card>();
	private List<Card> cards = new ArrayList<Card>();
	
	public static final int MAX_CARDS_IN_HAND = 5;
	
	public Player(World world, MessageLog messageLog) {
		super(true, "Hero");
		this.world = world;
		this.messageLog = messageLog;
	}

	@Override
	public void act() {
		Gdx.app.log(tag, "act");
		canAct.set(true);
		getStats().increaseMana(1);
		
		world.triggerUIRefresh();
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
			} 
			
		} 
	}
	
	public void addCard(Card card) {
		if(cards.size() < MAX_CARDS_IN_HAND) {
			cards.add(card);
		} else {
			addCardToDeck(card);
		}
	}

	public void addCardToDeck(Card card) {
		deck.add(card);
		Collections.shuffle(deck);
	}
	
	public boolean playCard(Card card) {
		int manaCost = card.getType().getManaCost();
		if(canAct() && manaCost <= getStats().mp) {
			Gdx.app.log(tag, "playCard " + card);
			cards.remove(card);
			getStats().decreaseMana(manaCost);
			if(deck.size() > 0) {
				cards.add(deck.remove(0));
			}
			card.getType().getCardEffect().apply(world);
			
			world.triggerUIRefresh();
			world.getScheduler().unlock(0.05f);
			canAct.set(false);
			
			
			messageLog.addMessage("Playing card - " + card.getType().toString());
			
			return true;
		} else {
			return false;
		}
		
	}
	
	public void attack(Monster monster) {
		
		
		
		world.attack(this, monster);
		world.getScheduler().unlock(0.05f);
		canAct.set(false);
	}
	
	public List<Card> getCards() {
		return cards;
	}

	public List<Card> getDeck() {
		return deck;
	}

	
}
