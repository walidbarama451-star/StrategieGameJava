package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages resources for a player.
 * Uses a Map to store resource quantities.
 */
public class ResourceManager {
    private Map<ResourceType, Integer> resources;
    
    public ResourceManager() {
        resources = new HashMap<>();
        // Initialize all resources to 0
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
    }
    
    /**
     * Initialize resources with starting amounts.
     */
    public ResourceManager(int gold, int wood, int stone, int food) {
        this();
        resources.put(ResourceType.GOLD, gold);
        resources.put(ResourceType.WOOD, wood);
        resources.put(ResourceType.STONE, stone);
        resources.put(ResourceType.FOOD, food);
    }
    
    public int getResource(ResourceType type) {
        return resources.getOrDefault(type, 0);
    }
    
    public void addResource(ResourceType type, int amount) {
        resources.put(type, getResource(type) + amount);
    }
    
    public boolean removeResource(ResourceType type, int amount) {
        int current = getResource(type);
        if (current >= amount) {
            resources.put(type, current - amount);
            return true;
        }
        return false;
    }
    
    /**
     * Check if player has enough resources for a cost.
     */
    public boolean canAfford(Map<ResourceType, Integer> cost) {
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            if (getResource(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Pay a cost if possible.
     */
    public boolean payCost(Map<ResourceType, Integer> cost) {
        if (!canAfford(cost)) {
            return false;
        }
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            removeResource(entry.getKey(), entry.getValue());
        }
        return true;
    }
    
    public Map<ResourceType, Integer> getAllResources() {
        return new HashMap<>(resources);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ResourceType type : ResourceType.values()) {
            sb.append(type.getFrenchName()).append(": ").append(getResource(type)).append("  ");
        }
        return sb.toString();
    }
}




