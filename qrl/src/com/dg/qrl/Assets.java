package com.dg.qrl;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.dg.qrl.Card.CardType;
import com.dg.qrl.Monster.Type;
import com.dg.qrl.World.TileType;

public class Assets {

	public static class MapTile implements TiledMapTile {

		private int id;
		private TextureRegion textureRegion;
		private BlendMode blendMode;
		
		public MapTile(int id, TextureRegion textureRegion) {
			this(id, textureRegion, false);
		}
		
		public MapTile(int id, TextureRegion textureRegion, boolean transparency) {
			this.id = id;
			this.textureRegion = textureRegion;
			this.blendMode = transparency ? BlendMode.ALPHA : BlendMode.NONE;
		}
		
		@Override
		public int getId() {
			return id;
		}

		@Override
		public void setId(int id) {
			this.id = id;
		}

		@Override
		public BlendMode getBlendMode() {
			return blendMode;
		}

		@Override
		public void setBlendMode(BlendMode blendMode) {
			this.blendMode = blendMode;
		}

		@Override
		public TextureRegion getTextureRegion() {
			return textureRegion;
		}

		@Override
		public MapProperties getProperties() {
			return null;
		}
		
	}
	
	
	public BitmapFont font;
	private Texture environmentTexture;
	
	public TiledMapTileSet tiledMapTileSet;
	public TextureRegion playerTextureRegion;
	public TextureRegion orcTextureRegion;
	public TextureRegion snakeTextureRegion;
	public TextureRegion whitePixel;
	
	public NinePatch cardBackgroundPatch;
	
	public TextureRegion cardTextureRegion;
	
	public final Color seenShadowColor = new Color(0.5f, 0.5f, 0.5f, 0.7f);
	public final Color notSeenShadowColor = new Color(0.5f, 0.5f, 0.5f, 1);
	
	private Map<TileType, MapTile> tiles;
	
	interface RenderProperties {
		TextureRegion getTextureRegion(Entity e);
	}
	
	private Map<Class<?>, RenderProperties> renderProperties = new HashMap<Class<?>, Assets.RenderProperties>();
	
	public Assets() {
		
	}
	
	public void load() {
		font = new BitmapFont(Gdx.files.internal("04b_03.fnt"));
		environmentTexture = new Texture(Gdx.files.internal("assets.png"));
	
		TextureRegion[][] tileTextureRegions = TextureRegion.split(environmentTexture, 8, 8);
		
		tiledMapTileSet = new TiledMapTileSet();
		tiles = new HashMap<World.TileType, Assets.MapTile>();;
		addTile(TileType.WALL, 1, tileTextureRegions[2][13]);
		addTile(TileType.FLOOR, 2, tileTextureRegions[0][0]);
		
		playerTextureRegion = tileTextureRegions[0][30];
		orcTextureRegion = tileTextureRegions[17][32];
		snakeTextureRegion = tileTextureRegions[18][32];
		
		cardTextureRegion = tileTextureRegions[22][18];
		
		whitePixel = new TextureRegion(environmentTexture, 8, 8, 1, 1);
	
		cardBackgroundPatch = new NinePatch(tileTextureRegions[2][15], 3, 3, 3, 3);
		
		renderProperties.put(Monster.class, new RenderProperties() {
			Map<Monster.Type, TextureRegion> regions = new HashMap<Monster.Type, TextureRegion>();
			{
				regions.put(Type.ORC, orcTextureRegion);
				regions.put(Type.SNAKE, snakeTextureRegion);
			}
			@Override
			public TextureRegion getTextureRegion(Entity entity) {
				//TODO: there must be a better way!!
				Monster monster = Monster.class.cast(entity);
				return regions.get(monster.getType());
			}
		});
		renderProperties.put(Card.class, new RenderProperties() {
			Map<CardType, TextureRegion> regions = new HashMap<Card.CardType, TextureRegion>();
			{
				regions.put(CardType.FIREBALL, cardTextureRegion);
				regions.put(CardType.HEAL, cardTextureRegion);
			}
			@Override
			public TextureRegion getTextureRegion(Entity entity) {
				Card card = Card.class.cast(entity);
				return regions.get(card.getType());
			}
		});
	}
	
	public TextureRegion getTextureRegion(Entity entity) {
		RenderProperties props = renderProperties.get(entity.getClass());
		return props.getTextureRegion(entity);
	}
	
	private void addTile(TileType tileType, int id, TextureRegion textureRegion) {
		MapTile mapTile = new MapTile(id, textureRegion);
		tiles.put(tileType, mapTile);
		tiledMapTileSet.putTile(id, mapTile);
	}

	public TiledMapTile getTile(TileType tileType) {
		return tiles.get(tileType);
	}
	
	public void dispose() {
		font.dispose();
		environmentTexture.dispose();
	}
}
