package model;

/**
 * Enum representing different tile types on the map.
 */
public enum TileType {
    GRASS("Herbe", true, 1),
    WATER("Eau", false, 0),
    MOUNTAIN("Montagne", false, 2),
    FOREST("Forêt", true, 1);
    
    private final String name;
    private final boolean walkable;
    private final int defenseBonus;
    
    TileType(String name, boolean walkable, int defenseBonus) {
        this.name = name;
        this.walkable = walkable;
        this.defenseBonus = defenseBonus;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isWalkable() {
        return walkable;
    }
    
    public int getDefenseBonus() {
        return defenseBonus;
    }
}




