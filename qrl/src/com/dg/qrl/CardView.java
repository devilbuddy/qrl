package com.dg.qrl;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class CardView {
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	NinePatchDrawable background;
	
	public CardView(int x, int y, int width, int height, NinePatch backgroundPatch) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height; 
		this.background = new NinePatchDrawable(backgroundPatch);
	}


	public void draw(SpriteBatch spriteBatch) {
		background.draw(spriteBatch, x, y, width, height);
	}
}
