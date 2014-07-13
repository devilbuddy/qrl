package com.dg.qrl;

public class Card extends Entity {

	public enum CardType {
		FIREBALL(2),
		HEAL(3);
		
		private final int manaCost;
		
		private CardType(int manaCost) {
			this.manaCost = manaCost;
		}
		
		public int getManaCost() {
			return manaCost;
		}
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
