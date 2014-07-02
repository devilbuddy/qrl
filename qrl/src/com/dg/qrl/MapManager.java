package com.dg.qrl;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.dg.qrl.World.TileType;

public class MapManager {

	private final Assets assets;
	
	private TiledMap tiledMap;
	
	public MapManager(Assets assets) {
		this.assets = assets;
	}
	
	public void initTiledMap(World world) {
		tiledMap = new TiledMap();
		tiledMap.getTileSets().addTileSet(assets.tiledMapTileSet);
		
		int width = world.getWidth();
		int height = world.getHeight();
		
		TiledMapTileLayer tiledMapTileLayer = new TiledMapTileLayer(width, height, 8, 8);
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				TileType tileType = world.getTile(x, y);
				Cell cell = new Cell();
				cell.setTile(assets.getTile(tileType));
				tiledMapTileLayer.setCell(x, y, cell);
			}
		}
		tiledMap.getLayers().add(tiledMapTileLayer);
		
	}
	
	public TiledMap getMap() {
		return tiledMap;
	}
	
}
