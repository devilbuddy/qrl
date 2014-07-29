package com.dg.qrl;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;

public class Effects {

	private static class Effect {
		Entity target;
		int x;
		int y;
		float age;
		float maxAge;
		float gravity;
	}
	
	
	public enum EffectType {
		POWERUP(3f);
		
		private float maxAge;
		
		private EffectType(float maxAge) {
			this.maxAge = maxAge;
		}
		
	}
	
	
	private Pool<Effect> effectPool = new Pool<Effects.Effect>() {

		@Override
		protected Effect newObject() {
			return new Effect();
		}
		
	};
	
	private final Assets assets;
	
	public Effects(Assets assets) {
		this.assets = assets;
	}
	
	public void addEffect(EffectType type, Entity target) {
		Effect effect = effectPool.obtain();
		effect.age = 0;
		effect.maxAge = type.maxAge;
		
	}
	
	public void update(float delta) {
		
	}
	
	public void draw(SpriteBatch spriteBatch) {
		
	}
	
}
