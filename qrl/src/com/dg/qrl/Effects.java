package com.dg.qrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
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
	
	private Pool<Emitter> emitterPool = new Pool<Effects.Emitter>() {
		@Override
		protected Emitter newObject() {
			return new Emitter();
		}
	};
	
	private static class Particle implements Poolable {
		private Vector2 tmp = new Vector2();
		public Vector2 position = new Vector2();
		public Vector2 velocity = new Vector2();
		public Vector2 acceleration = new Vector2();
		public Color color = new Color();
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
	
	private class Emitter implements Poolable {

		private Random random = new Random();
		private Vector2 position = new Vector2();
		public float emitDelay;
		public int minEmitCount;
		public int maxEmitCount;
		public boolean repeat;
		public float accumulator;
		public Effect effect;
		boolean done = false;
		public void update(float delta) {
			if(!done) {
				accumulator += delta;
				if(accumulator >= emitDelay) {
					int particlesToEmit = minEmitCount + random.nextInt(maxEmitCount);
					for(int i = 0; i < particlesToEmit; i++) {
						Particle particle = particlePool.obtain();
						particle.maxAge = effect.maxAge/10;
						particle.position.set(position);
						particle.velocity.set(random.nextBoolean()?-1:1 * random.nextFloat(), random.nextBoolean()?-1:1 * random.nextFloat());
						effect.particles.add(particle);
					}
					if(repeat) {
						accumulator = 0;
					} else {
						done = true;
					}
				} 	
			}
			
		}
		
		@Override
		public void reset() {
			accumulator = 0;
			done = false;
		}
		
	}
	
	public class Effect implements Poolable {
		Entity target;
		List<Emitter> emitters = new ArrayList<Effects.Emitter>();
		List<Particle> particles = new ArrayList<Effects.Particle>();
		public float age;
		public float maxAge;
		public Color baseColor;
		public Anchor anchor;
		public void update(float delta) {
			age += delta;
			
			for(int i = 0; i < emitters.size(); i++) {
				emitters.get(i).update(delta);
			}
			
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
		
			for(int i = 0; i < emitters.size(); i++) {
				emitterPool.free(emitters.get(i));
			}
			emitters.clear();
		}
		
		public void draw(SpriteBatch spriteBatch, int posX, int posY) {
			for(int j = 0; j < particles.size(); j++) {
				Particle particle = particles.get(j);
				spriteBatch.setColor(baseColor);
				spriteBatch.draw(assets.whitePixel, posX + particle.position.x, posY + particle.position.y);
			}
		}
	}
	
	static class EmitterProperties {
		public float emitDelay;
		public int minEmitCount;
		public int maxEmitCount;
		public boolean repeat;
	}
	

	static EmitterProperties BURST = new EmitterProperties();
	static {
		BURST.emitDelay = 0;
		BURST.minEmitCount = 3;
		BURST.maxEmitCount = 7;
		BURST.repeat = false;
	}
	public enum EffectType {
		
		POWERUP(3f, Color.RED, new EmitterProperties[] {BURST});
		
		private float maxAge;
		private Color baseColor;
		private EmitterProperties[] emitterProperties;
		private EffectType(float maxAge, Color baseColor, EmitterProperties[] emitterProperties) {
			this.maxAge = maxAge;
			this.baseColor = baseColor;
			this.emitterProperties = emitterProperties;
		}
	}
		
	public enum Anchor {
		CENTER
	}
	
	private final Assets assets;
	private List<Effect> effects = new ArrayList<Effects.Effect>();
	
	public Effects(Assets assets) {
		this.assets = assets;
	}
	
	public void addEffect(EffectType type, Entity target, Anchor anchor) {
		Effect effect = effectPool.obtain();
		effect.target = target;
		effect.maxAge = type.maxAge;
		effect.baseColor = type.baseColor;
		effect.anchor = anchor;
		for(int i = 0; i < type.emitterProperties.length; i++) {
			EmitterProperties emitterProperties = type.emitterProperties[i];
			Emitter emitter = emitterPool.obtain();
			emitter.emitDelay = emitterProperties.emitDelay;
			emitter.minEmitCount = emitterProperties.minEmitCount;
			emitter.maxEmitCount = emitterProperties.maxEmitCount;
			emitter.repeat = emitterProperties.repeat;
			emitter.effect = effect;
			effect.emitters.add(emitter);
		}
		
		effects.add(effect);
	}
	
	public void getEffectsForEntity(Entity entity, List<Effect> effectsOut) {
		effectsOut.clear();
		for(int i = 0; i < effects.size(); i++) {
			if(effects.get(i).target == entity) {
				effectsOut.add(effects.get(i));
			}
		}
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
	
}
