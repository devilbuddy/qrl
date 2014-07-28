package com.dg.qrl;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;

public class MessageLog {

	private static final float MAX_AGE = 5;

	private final BitmapFont font;
	
	Pool<Message> messagePool = new Pool<MessageLog.Message>() {

		@Override
		protected Message newObject() {
			// TODO Auto-generated method stub
			return new Message(null);
		}
		
	};
	
	private List<Message> messages = new ArrayList<MessageLog.Message>();
	
	private static class Message {
		String text;
		float age;
		public Message(String text) {
			this.text = text;
			
		}
	}
	
	public MessageLog(Assets assets) {
		font = assets.font;
	}
	
	public void addMessage(String text) {
		Message message = messagePool.obtain();
		message.age = 0;
		message.text = text;
		messages.add(0, message);
	}
	
	public void update(float delta) {
		for(int i = messages.size() - 1; i >= 0; i--) {
			Message message = messages.get(i);
			message.age += delta;
			if(message.age >= MAX_AGE) {
				messages.remove(i);
				messagePool.free(message);
			}
		}
	}
	
	public void draw(SpriteBatch spriteBatch) {
		int y = screenHeight - 1;
		for(int i = 0; i < messages.size(); i++) {
			Message message = messages.get(i);
	
			tmpColor.set(Color.WHITE);
			float a = 1 - message.age/MAX_AGE;
			tmpColor.mul(1, 1, 1, a);
			font.setColor(tmpColor);
			font.draw(spriteBatch, message.text, 2, y);
			y-=font.getCapHeight();
		}
	}

	private int screenWidth;
	private int screenHeight;
	private Color tmpColor = new Color(Color.WHITE);
	
	public void resize(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}
	
}
