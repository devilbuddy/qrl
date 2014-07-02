package com.dg.qrl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.dg.qrl.Entity.Point;

public class QrlGame implements ApplicationListener {
	
	private static final String tag = "QrlGame";
	
	private static final int MAX_UPDATE_ITERATIONS = 5;
	private static final float FIXED_TIMESTEP = 1f / 60f;
	private float accumulator = 0;

	// main viewport width (for UI)
	private int width = 160;
	private int height;
	
	private OrthographicCamera mainCamera;
	private OrthographicCamera mapCamera;
	private SpriteBatch spriteBatch;
	private TiledMapRenderer tiledMapRenderer;
	private Assets assets;
	private World world;
	private MapManager mapManager;
	private InputManager inputManager;
	
	private Vector3 cameraTarget = new Vector3();
	//private Rectangle mapViewport = new Rectangle();
	private Rectangle mapScreenArea = new Rectangle();
	
	@Override
	public void create() {		
		mainCamera = new OrthographicCamera();
		mapCamera = new OrthographicCamera();
		spriteBatch = new SpriteBatch();
		assets = new Assets();
		assets.load();
		
		world = new World(30, 30);
		world.generate(1);
		
		mapManager = new MapManager(assets);
		mapManager.initTiledMap(world);		
		
		tiledMapRenderer = new OrthogonalTiledMapRenderer(mapManager.getMap());
		
		inputManager = new InputManager(world, mapCamera);
		Gdx.input.setInputProcessor(inputManager);
	
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}

	@Override
	public void render() {	
		// step
		accumulator += Gdx.graphics.getRawDeltaTime();
		int iterations = 0;
		while (accumulator > FIXED_TIMESTEP && iterations < MAX_UPDATE_ITERATIONS) {
			step(FIXED_TIMESTEP);
			accumulator -= FIXED_TIMESTEP;
			iterations++;
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		mapCamera.update();
		tiledMapRenderer.setView(mapCamera);
		tiledMapRenderer.render();
		
		spriteBatch.setProjectionMatrix(mapCamera.combined);
		spriteBatch.begin();
		
		// render FOV
		for(int y = 0; y < world.getHeight(); y++) {
			for(int x = 0; x < world.getHeight(); x++) {
				boolean seen = world.isSeen(x, y);
				boolean inFov = world.isInFieldfOfView(x, y);
				
				if(!inFov) {
					if(seen) {
						spriteBatch.setColor(0.5f, 0.5f, 0.5f, 0.7f);
						spriteBatch.draw(assets.whitePixel, x * 8, y * 8, 8, 8);
					} else {
						spriteBatch.setColor(0.5f, 0.5f, 0.5f, 1);
						spriteBatch.draw(assets.whitePixel, x * 8, y * 8, 8, 8);
					}
					
				} 				
			}
		}
		
		Entity player = world.getPlayer();
		spriteBatch.setColor(Color.WHITE);
		spriteBatch.draw(assets.playerTextureRegion, player.getPosition().getX() * 8, player.getPosition().getY() * 8);
		spriteBatch.end();
		
		spriteBatch.setProjectionMatrix(mainCamera.combined);
		spriteBatch.begin();
		spriteBatch.setColor(Color.WHITE);
		float mapHeight = mapCamera.viewportWidth * 2;
		spriteBatch.draw(assets.whitePixel, 0, 0, mainCamera.viewportWidth, mainCamera.viewportHeight - mapHeight);
		
		
		assets.font.draw(spriteBatch, "X:" + player.getPosition().getX() +  " Y:" + player.getPosition().getY(), 1, 32);
		assets.font.draw(spriteBatch, "Camera:" + mapCamera.position, 1, 24);
		assets.font.draw(spriteBatch, "mapScreenArea:" + mapScreenArea, 1, 16);
		spriteBatch.end();
		
	}

	private void updateCamera() {
		Entity cameraFocus = world.getPlayer();
		Point positon = cameraFocus.getPosition();
		float cameraTargetX = positon.getX() * 8 + 4;
		float cameraTargetY = positon.getY() * 8 - 16;
		
		float camMinX = mapCamera.viewportWidth / 2;
		float camMaxX = world.getWidth() * 8 - camMinX;
		
		float camYOffset = mapCamera.viewportHeight - mapCamera.viewportWidth;;
		
		float camMinY = mapCamera.viewportHeight / 2 - camYOffset; 
		float camMaxY = world.getHeight() * 8 - camMinY - camYOffset;
		
		if (cameraTargetX < camMinX)
			cameraTargetX = camMinX;
		if (cameraTargetX > camMaxX)
			cameraTargetX = camMaxX;

		if (cameraTargetY < camMinY)
			cameraTargetY = camMinY;
		if (cameraTargetY > camMaxY)
			cameraTargetY = camMaxY;
		
		cameraTarget.set(cameraTargetX, cameraTargetY, 0);
		mapCamera.position.slerp(cameraTarget, 0.3f);
		mapCamera.update();
	}

	private void step(float delta) {
		inputManager.update();	
		updateCamera();
		world.update();
	}
	
	@Override
	public void resize(int w, int h) {
		float ratio = (float)h/(float)w;
		height = (int)(width * ratio);
		mainCamera.setToOrtho(false, width, height);
		mapCamera.setToOrtho(false, width/2, height/2);
		
		mainCamera.position.set(width/2, height/2, 0);
		mainCamera.update();
		
		
		mapCamera.position.set(mapCamera.viewportWidth/2, mapCamera.viewportHeight/2, 0);
		mapCamera.update();
		Gdx.app.log(tag, "resize w:" + w + "x" + h + " - mainCamera " + mainCamera.viewportWidth + "x" + mainCamera.viewportHeight + " mapCamera " + mapCamera.viewportWidth + "x" + mapCamera.viewportHeight);
	
	
		mapScreenArea.setWidth(w).setHeight(w);
		mapScreenArea.setX(0);
		mapScreenArea.setY(0);
		
		Gdx.app.log(tag, "mapScreenArea:" + mapScreenArea);
		inputManager.setTouchableArea(mapScreenArea);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}