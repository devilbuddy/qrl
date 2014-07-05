package com.dg.qrl;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class CardView {
	
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
		touchBounds.setX(x).setY(y + height);
	}
	
	public void draw(SpriteBatch spriteBatch) {
		background.draw(spriteBatch, x, y, width, height);
		spriteBatch.draw(symbol, x + 1, y + height - symbol.getRegionHeight() - 1);
	}
}
