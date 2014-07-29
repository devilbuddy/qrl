package com.dg.qrl;

public class Card extends Entity {

	public interface CardEffect {
		void apply(World world);
	}
	
	private static final CardEffect noEffect = new CardEffect() {
		
		@Override
		public void apply(World world) {
			Player player = world.getPlayer();
			player.getStats().increaseHealth(2);
		}
	};
	
	public enum CardType {
		FIREBALL(2, noEffect),
		HEAL(3, noEffect);
		
		private final int manaCost;
		private final CardEffect cardEffect;
		private CardType(int manaCost, CardEffect cardEffect) {
			this.manaCost = manaCost;
			this.cardEffect = cardEffect;
		}
		
		public int getManaCost() {
			return manaCost;
		}
		
		public CardEffect getCardEffect() {
			return cardEffect;
		}
	}
	
	private final CardType cardType;
	private boolean faceDown = true;
	public Card(CardType cardType) {
		super(false, cardType.toString());
		this.cardType = cardType;
	}
	
	public CardType getType() {
		return cardType;
	}

	public boolean isFacedown() {
		return faceDown;
	}
	
	public void flip() {
		faceDown = false;
	}
}
