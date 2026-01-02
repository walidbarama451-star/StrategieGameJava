package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    private Tile[][] tiles;
    private int width;
    private int height;
    private Random random;
    
    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[height][width];
        this.random = new Random();
        generateMap();
    }
    
    private void generateMap() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                TileType type;
                double rand = random.nextDouble();
                
                if (rand < 0.6) {
                    type = TileType.GRASS;
                } else if (rand < 0.75) {
                    type = TileType.FOREST;
                } else if (rand < 0.9) {
                    type = TileType.WATER;
                } else {
                    type = TileType.MOUNTAIN;
                }
                
                tiles[y][x] = new Tile(type, x, y);
            }
        }
        
        tiles[1][1].setType(TileType.GRASS);
        tiles[height - 2][width - 2].setType(TileType.GRASS);
    }
    
    public Tile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }
        return tiles[y][x];
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public List<Tile> getTilesInRange(int x, int y, int range) {
        List<Tile> result = new ArrayList<>();
        for (int dy = -range; dy <= range; dy++) {
            for (int dx = -range; dx <= range; dx++) {
                if (dx * dx + dy * dy <= range * range) {
                    Tile tile = getTile(x + dx, y + dy);
                    if (tile != null) {
                        result.add(tile);
                    }
                }
            }
        }
        return result;
    }
    
    public List<Tile> findPath(int startX, int startY, int endX, int endY) {
        List<Tile> path = new ArrayList<>();
        int dx = Integer.compare(endX, startX);
        int dy = Integer.compare(endY, startY);
        
        int x = startX, y = startY;
        while (x != endX || y != endY) {
            if (x != endX) {
                Tile tile = getTile(x + dx, y);
                if (tile != null && tile.isWalkable()) {
                    x += dx;
                    path.add(getTile(x, y));
                    continue;
                }
            }
            if (y != endY) {
                Tile tile = getTile(x, y + dy);
                if (tile != null && tile.isWalkable()) {
                    y += dy;
                    path.add(getTile(x, y));
                    continue;
                }
            }
            break;
        }
        return path;
    }
}




