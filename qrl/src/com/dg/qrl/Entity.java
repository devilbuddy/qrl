package com.dg.qrl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

public class Entity {

	public static class Point {
		
		private static final String tag = "Point";
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		private int x;
		private int y;
		
		public Point() {
		}
		
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public Point setX(int x) {
			this.x = x;
			return this;
		}
		
		public Point setY(int y) {
			this.y = y;
			return this;
		}
		
		public void translate(Direction direction) {
			this.x += direction.dx;
			this.y += direction.dy;
		}
		
		public void set(Point other) {
			this.x = other.getX();
			this.y = other.getY();
		}
		
		public boolean isAdjacentTo(Point other) {
			return isAdjacentTo(other.x, other.y);
		}
		
		public boolean isAdjacentTo(int x, int y) {
			Gdx.app.log(tag, "isAdjacentTo " + this + " " + "[" + x + "," + y +"]");
			int dx = Math.abs(this.x - x);
			int dy = Math.abs(this.y - y);
			boolean adjacent = dx <= 1 && dy <=1;
			Gdx.app.log(tag, "adjacent:" + adjacent);
			return adjacent;
		}
		
		public String toString() {
			return "[" + x + "," + y + "]";
		}
	}
	
	public static class Stats {
		public int hp = 5;
		public int maxHp = 10;
		public int mp = 5;
		public int maxMp = 10;
		public void increaseMana(int inc) {
			mp+=inc;
			if(mp > maxMp) {
				mp = maxMp;
			}
		}
		public void decreaseMana(int dec) {
			mp-=dec;
			if(mp < 0) {
				mp = 0;
			}
		}
		public void increaseHealth(int inc) {
			hp+=inc;
			if(hp > maxHp) {
				hp = maxHp;
			}
		}
		public void decreaseHealth(int dec) {
			hp-=dec;
			if(hp<0) {
				hp = 0;
			}
		}
	}
	
	private Point position = new Point();
	private Stats stats = new Stats();
	private boolean solid;	
	private String name;
	public Entity(boolean solid, String name) {
		this.solid = solid;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Point getPosition() {
		return position;
	}
	
	public Stats getStats() {
		return stats;
	}
	
	public boolean isSolid() {
		return solid;
	}

	public void onRemoved() {
		// TODO Auto-generated method stub
		
	}

	
}
