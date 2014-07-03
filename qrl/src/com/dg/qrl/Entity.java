package com.dg.qrl;

public class Entity {

	public static class Point {
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
		
		public void set(Point other) {
			this.x = other.getX();
			this.y = other.getY();
		}
		
		public String toString() {
			return "[" + x + "," + y + "]";
		}
	}
	
	public static class Stats {
		private int hp;
		
	}
	
	private Point position = new Point();
	private Stats stats = new Stats();
		
	public Entity() {
		
	}
	
	public Point getPosition() {
		return position;
	}
	
	public Stats getStats() {
		return stats;
	}
	
}
