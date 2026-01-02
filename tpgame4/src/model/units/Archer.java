package model.units;

import model.ResourceType;
import model.Unit;

/**
 * Ranged unit with high attack but low defense.
 */
public class Archer extends Unit {
    public Archer() {
        super("Archer", 30, 20, 2, 3, 2);
        this.cost.put(ResourceType.GOLD, 60);
        this.cost.put(ResourceType.WOOD, 30);
        this.cost.put(ResourceType.FOOD, 15);
    }
}

