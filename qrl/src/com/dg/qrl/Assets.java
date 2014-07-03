package com.dg.qrl;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
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
	public TextureRegion monsterTextureRegion;
	
	public TextureRegion whitePixel;
	
	public final Color seenShadowColor = new Color(0.5f, 0.5f, 0.5f, 0.7f);
	public final Color notSeenShadowColor = new Color(0.5f, 0.5f, 0.5f, 1);
	
	private Map<TileType, MapTile> tiles;
	
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
		monsterTextureRegion = tileTextureRegions[17][32];
		
		whitePixel = new TextureRegion(environmentTexture, 8, 8, 1, 1);
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
