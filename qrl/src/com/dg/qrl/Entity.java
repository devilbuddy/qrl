package com.dg.qrl;

public class Entity {

	public static class Point {
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
		
		public String toString() {
			return "[" + x + "," + y + "]";
		}
	}
	
	public static class Stats {
		public int hp = 5;
		public int maxHp = 10;
		public int mp = 5;
		public int maxMp = 10;
	}
	
	private Point position = new Point();
	private Stats stats = new Stats();
	private boolean solid;	
	
	public Entity(boolean solid) {
		this.solid = solid;
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
}
