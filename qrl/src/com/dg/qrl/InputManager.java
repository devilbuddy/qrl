package com.dg.qrl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dg.qrl.Entity.Point;

public class InputManager extends InputMultiplexer implements GestureListener {

	private final World world;
	private final Camera camera;
	private final GestureDetector gestureDetector;
	
	private final Rectangle touchableArea = new Rectangle();
	
	private final Vector3 tmp = new Vector3();
	
	public InputManager(World world, Camera camera) {
		this.world = world;
		this.camera = camera;
		gestureDetector = new GestureDetector(this);
		addProcessor(gestureDetector);
	}
	
	public void update() {
		Entity player = world.getPlayer();
		Point position = player.getPosition();
		int y = position.getY();
		int x = position.getX();
		int nextX = x;
		int nextY = y;
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			nextY++;
		} else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			nextY--;
		} else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			nextX--;
		} else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			nextX++;
		} 
		boolean moved = false;
		if(nextX != x && nextX >= 0 && nextX < world.getWidth()) {
			moved = true;
		}
		if(nextY != y && nextY >= 0 && nextY < world.getHeight()) {
			
			moved = true;
		}
		if(moved) {
			if(world.isPassable(nextX, nextY)) {
				position.setX(nextX);
				position.setY(nextY);
				world.updateFieldOfView();	
			}
			
		}
		
	}

	private Vector3 unproject(float x, float y) {
		tmp.set(x, y, 0);
		camera.unproject(tmp);
		return tmp;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		if(touchableArea.contains(x, y)) {
			Vector3 v = unproject(x, y);
			int tileX = (int) (v.x / 8);
			int tileY = (int) (v.y / 8);
			
			world.onTileTapped(tileX, tileY);
		}
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void setTouchableArea(Rectangle rect) {
		touchableArea.set(rect);
	}
	
}
