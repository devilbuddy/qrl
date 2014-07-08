package com.dg.qrl;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class CardDeckView extends InputAdapter {

	public static class CardView {
		
		private int x;
		private int y;
		private int width;
		private int height;
		
		NinePatchDrawable background;
		TextureRegion symbol;
		Rectangle touchBounds = new Rectangle();
		
		public CardView(int width, int height, NinePatch backgroundPatch, TextureRegion symbol) {
			this.width = width;
			this.height = height; 
			this.background = new NinePatchDrawable(backgroundPatch);
			this.symbol = symbol;
			touchBounds.setWidth(width).setHeight(height);
		}


		public void setPosition(int x, int y) {
			this.x = x;
			this.y = y;
			touchBounds.setX(x).setY(y);
		}
		
		public void translate(int dx, int dy) {
			this.x += dx;
			this.y += dy;
			touchBounds.setX(x).setY(y);
		}
		
		public void draw(SpriteBatch spriteBatch) {
			background.draw(spriteBatch, x, y, width, height);
			spriteBatch.draw(symbol, x + 1, y + height - symbol.getRegionHeight() - 1);
		}
	}

	private static final String tag = "CardDeckView";
	
	private final Camera camera;
	
	private List<CardView> cardViews = new ArrayList<CardDeckView.CardView>();
	private CardView selectedCardView = null;
	
	private int lastX;
	private int lastY;
	private int startX;
	private int startY;
	
	private int playCardY;
	
	public CardDeckView(Camera camera, Assets assets) {
		this.camera = camera;
	
		int x = 50;
		int y = 30;
		for(int i = 0; i < 3; i++) {
			CardView cardView = new CardView(30, 45, assets.cardBackgroundPatch, assets.orcTextureRegion);
			cardView.setPosition(x, y);
			cardViews.add(cardView);
			
			x += 31;
		}
	}

	public void update(float delta) {
		
	}
	
	public void draw(SpriteBatch spriteBatch) {
		for(int i = 0; i < cardViews.size(); i++) {
			CardView cardView = cardViews.get(i);
			cardView.draw(spriteBatch);
		}
	}
	
	private final Vector3 tmp = new Vector3();
	private Vector3 unproject(float x, float y, Camera camera) {
		tmp.set(x, y, 0);
		camera.unproject(tmp);
		return tmp;
	}

	
	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		Gdx.app.log(tag, "screenX " + screenX + " screenY:" + screenY);
		
		Vector3 v = unproject(screenX, screenY, camera);
		
		for(int i = 0; i < cardViews.size(); i++) {
			CardView cardView = cardViews.get(i);

			Gdx.app.log(tag, "touchBounds: " + cardView.touchBounds.toString());
			if(cardView.touchBounds.contains(v.x, v.y)) {
				selectedCardView = cardView;
				lastX = (int)v.x;
				lastY = (int)v.y;
				
				startX = cardView.x;
				startY = cardView.y;
				
				Gdx.app.log(tag, "touchDown " + cardView);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		if(selectedCardView != null) {
			
			Gdx.app.log(tag, "selectedCardView.y " + selectedCardView.y );
			Gdx.app.log(tag, "playCardY " + playCardY );
			
			if(selectedCardView.y >= playCardY) {
				Gdx.app.log(tag, "play");
			} else {
				selectedCardView.setPosition(startX, startY);
			}
			
			selectedCardView = null;	
		}
		
		
		
		return false;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		if(selectedCardView != null) {
			Vector3 v = unproject(screenX, screenY, camera);
			int dx = (int)v.x - lastX;
			int dy = (int)v.y - lastY;
			selectedCardView.translate(dx, dy);
			lastX = (int)v.x;
			lastY = (int)v.y;
		}
		
		return false;
	}

	public void setPlayCardY(int y) {
		playCardY = y;
	}
}
