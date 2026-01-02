package model;

/**
 * Represents a single tile on the game map.
 */
public class Tile {
    private TileType type;
    private int x, y;
    private Unit unit; // Unit on this tile
    private Building building; // Building on this tile
    private Player owner; // Owner of this tile (for territory)
    
    public Tile(TileType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.unit = null;
        this.building = null;
        this.owner = null;
    }
    
    public TileType getType() {
        return type;
    }
    
    public void setType(TileType type) {
        this.type = type;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public Unit getUnit() {
        return unit;
    }
    
    public void setUnit(Unit unit) {
        this.unit = unit;
    }
    
    public Building getBuilding() {
        return building;
    }
    
    public void setBuilding(Building building) {
        this.building = building;
    }
    
    public Player getOwner() {
        return owner;
    }
    
    public void setOwner(Player owner) {
        this.owner = owner;
    }
    
    public boolean isWalkable() {
        return type.isWalkable() && unit == null && building == null;
    }
    
    public boolean isEmpty() {
        return unit == null && building == null;
    }
}




