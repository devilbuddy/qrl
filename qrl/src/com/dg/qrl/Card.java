package com.dg.qrl;

public class Card extends Entity {

	public enum CardType {
		FIREBALL,
		HEAL
	}
	
	private final CardType cardType;
	
	public Card(CardType cardType) {
		super(false);
		this.cardType = cardType;
	}
	
	public CardType getType() {
		return cardType;
	}
}
