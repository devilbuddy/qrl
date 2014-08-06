package com.dg.qrl;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Effects {

	private Pool<Effect> effectPool = new Pool<Effects.Effect>() {
		@Override
		protected Effect newObject() {
			return new Effect();
		}
	};
	
	private Pool<Particle> particlePool = new Pool<Effects.Particle>() {
		@Override
		protected Particle newObject() {
			return new Particle();
		}
	};
	
	private static class Particle implements Poolable {
		private Vector2 tmp = new Vector2();
		public Vector2 position = new Vector2();
		public Vector2 velocity = new Vector2();
		public Vector2 acceleration = new Vector2();
		public float age;
		public float maxAge;
		
		public void update(float delta) {
			age += delta;
			tmp.set(acceleration).scl(delta); 
			velocity.add(tmp);
			tmp.set(velocity).scl(delta);
			position.add(velocity);
		}

		@Override
		public void reset() {
			age = 0;
		}
	}
	
	private class Effect implements Poolable {
		Entity target;
		List<Particle> particles = new ArrayList<Effects.Particle>();
		public float age;
		public float maxAge;
		
		public void update(float delta) {
			age += delta;
			for(int i = particles.size() - 1; i >= 0; i--) {
				Particle particle = particles.get(i);
				particle.update(delta);
				if(particle.age >= particle.maxAge) {
					particles.remove(i);
					particlePool.free(particle);
				}
			}
		}

		@Override
		public void reset() {
			age = 0;
			target = null;
			for(int i = 0; i < particles.size(); i++) {
				particlePool.free(particles.get(i));
			}
			particles.clear();
			
		}
	}
	
	
	public enum EffectType {
		POWERUP(3f);
		
		private float maxAge;
		
		private EffectType(float maxAge) {
			this.maxAge = maxAge;
		}
	}
		
	private final Assets assets;
	private List<Effect> effects = new ArrayList<Effects.Effect>();
	
	public Effects(Assets assets) {
		this.assets = assets;
	}
	
	public void addEffect(EffectType type, Entity target) {
		Effect effect = effectPool.obtain();
		effect.target = target;
		
		effects.add(effect);
	}
	
	public void update(float delta) {
		for(int i = effects.size() - 1; i >= 0; i--) {
			Effect effect = effects.get(i);
			effect.update(delta);
			if(effect.age >= effect.maxAge) {
				effects.remove(i);
				effectPool.free(effect);
			}
		}
	}
	
	public void draw(SpriteBatch spriteBatch) {
		for(int i = 0; i < effects.size(); i++) {
			Effect effect = effects.get(i);
			for(int j = 0; j < effect.particles.size(); j++) {
				Particle particle = effect.particles.get(j);
				
				spriteBatch.draw(assets.whitePixel, particle.position.x, particle.position.y);
			}
		}
	}
	
}
