package com.dg.qrl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import rlforj.los.IFovAlgorithm;
import rlforj.los.ILosBoard;
import rlforj.los.PrecisePermissive;

import com.badlogic.gdx.Gdx;
import com.dg.qrl.Entity.Point;
import com.nuclearunicorn.libroguelike.utils.pathfinder.astar.Mover;
import com.nuclearunicorn.libroguelike.utils.pathfinder.astar.PathFinder;
import com.nuclearunicorn.libroguelike.utils.pathfinder.astar.TileBasedMap;
import com.nuclearunicorn.libroguelike.utils.pathfinder.astar.implementation.AStarPathFinder;

public class World {

	public enum TileType {
		VOID(false),
		WALL(false),
		FLOOR(true);
		
		private final boolean passable;
		
		private TileType(boolean passable) {
			this.passable = passable;
		}
		
		public boolean isPassable() {
			return passable;
		}
	}
	
	public static class Tile {
		public final TileType tileType;
		public boolean seen = false;
		public boolean inFov = false;
		public List<Entity> entities = new ArrayList<Entity>();
		
		public Tile(TileType tileType) {
			this.tileType = tileType;
		}
	}
	
	private class LosBoardAdapter implements ILosBoard {

		@Override
		public boolean contains(int x, int y) {
			return x >= 0 && x < width && y >= 0 && y < height;
		}

		@Override
		public boolean isObstacle(int x, int y) {
			return !isPassable(x, y);
		}

		@Override
		public void visit(int x, int y) {
			mapData[y][x].seen = true;
			mapData[y][x].inFov = true;
		}
		
	}
	
	private class TileBasedMapAdapter implements TileBasedMap {

		@Override
		public int getWidthInTiles() {
			return width;
		}

		@Override
		public int getHeightInTiles() {
			return height;
		}

		@Override
		public void pathFinderVisited(int x, int y) {
		}

		@Override
		public boolean blocked(Mover mover, int x, int y) {
			return !isPassable(x, y);
		}

		@Override
		public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
			return 1;
		}

		@Override
		public int getScaleFactor() {
			return 1;
		}
		
	}
	
	private class MoverAdapter implements Mover {	
	}
	
	public interface Actor {
		void act();
	}
	
	public static class Scheduler implements Callable<Void> {
		
		private AtomicInteger lockCount = new AtomicInteger(0);
		private Queue<Actor> queue = new LinkedList<Actor>();
		
		private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		
		public Scheduler() {
		}
		
		public void lock() {
			lockCount.incrementAndGet();
		}
		
		public void unlock() {
			int c = lockCount.decrementAndGet();
			if(c < 0) {
				throw new RuntimeException("Can't unlock already unlocked scheduler");
			}
		}
		
		public void unlock(float delay) {
			executor.schedule(this, (long)(delay * 1000), TimeUnit.MILLISECONDS);
		}
		
		public void add(Actor actor) {
			queue.add(actor);
		}
		
		public void remove(Actor actor) {
			queue.remove(actor);
		}
		
		private static final int MAX_ITERATIONS = 5;
		
		public void update() {
			int iterations = 0;
			while(lockCount.get() == 0 && iterations < MAX_ITERATIONS) {
				Actor next = queue.poll();
				if(next != null) {
					next.act();
					queue.add(next);
				}
				iterations++;
			}
		}

		@Override
		public Void call() throws Exception {
			unlock();
			return null;
		}
	}
	
	
	private int width;
	private int height;
	
	private Tile[][] mapData;

	private Player player;
	
	private IFovAlgorithm fovAlgorithm;
	private ILosBoard losBoard;
	
	private TileBasedMap tileBasedMap;
	private PathFinder pathFinder;
	private Mover mover;
	
	private Scheduler scheduler = new Scheduler();
	
	private List<Entity> entities = new ArrayList<Entity>();
	
	public World(int width, int height) {
		this.width = width;
		this.height = height;
		
		fovAlgorithm = new PrecisePermissive();
		losBoard = new LosBoardAdapter();
		tileBasedMap = new TileBasedMapAdapter();
		pathFinder = new AStarPathFinder(tileBasedMap, 10, true);
		mover = new MoverAdapter();
		
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean contains(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}
	
	public TileType getTileType(int x, int y) {
		return mapData[y][x].tileType;
	}
	
	public boolean isPassable(int x, int y) {
		return contains(x, y) && mapData[y][x].tileType.isPassable();
	}
	
	public boolean isInFieldfOfView(Point position) {
		return isInFieldfOfView(position.getX(), position.getY());
	}
	
	public boolean isInFieldfOfView(int x, int y) {
		return contains(x, y) && mapData[y][x].inFov;
	}
	
	public boolean isSeen(int x, int y) {
		return contains(x, y) && mapData[y][x].seen;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	private Tile getTile(Point position) {
		return getTile(position.getX(), position.getY());
	}
	
	private Tile getTile(int x, int y) {
		return mapData[y][x];
	}
	
	public void addEntity(Entity entity, int x, int y) {
		entities.add(entity);
		entity.getPosition().setX(x).setY(y);
		getTile(x, y).entities.add(entity);
	}
	
	public void addEntity(Entity entity, Point position) {
		addEntity(entity, position.getX(), position.getY());
	}
	
	public void removeEntity(Entity entity) {
		entities.remove(entity);
		getTile(entity.getPosition()).entities.remove(entity);
	}
	
	public void moveEntity(Entity entity, Point newPosition) {
		getTile(entity.getPosition()).entities.remove(entity);
		entity.getPosition().set(newPosition);
		getTile(newPosition).entities.add(entity);
	}
	
	public void update() {
		scheduler.update();
	}
	
	public void generate(long seed) {
		Random r = new Random(seed);
		mapData = new Tile[width][height];
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				TileType tileType = r.nextFloat() > 0.1f ? TileType.FLOOR : TileType.WALL;
				Tile tile = new Tile(tileType);
				mapData[y][x] = tile;
				if(tileType == TileType.FLOOR) {
					if(r.nextFloat() < 0.05f) {
						Monster monster = new Monster(this);
						addEntity(monster, x, y);
						scheduler.add(monster);
					}	
				}	
			}
		}
		

		player = new Player(this);
		addEntity(player, 0, 0);
		scheduler.add(player);
		
		updateFieldOfView();
	}

	public void updateFieldOfView() {

		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				mapData[y][x].inFov = false;
			}
		}
		Point position = player.getPosition();
		fovAlgorithm.visitFieldOfView(losBoard, position.getX(), position.getY(), 3);
	}

	public void onTileTapped(int x, int y) {
		if(contains(x, y) && isSeen(x, y) && isPassable(x, y)) {
			
			Point start = player.getPosition();
			if(player.canAct()) {
				List<Point> path = pathFinder.findPath(mover, start.getX(), start.getY(), x, y);
				if(path != null) {
					Gdx.app.log("", "" + path.toString());
					player.setPath(path);	
				}
			}
		}
	}

	public List<Entity> getEntities() {
		return entities;
	}
	
}
