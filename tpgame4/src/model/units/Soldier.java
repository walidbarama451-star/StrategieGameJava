package model.units;

import model.ResourceType;
import model.Unit;

/**
 * Basic melee unit.
 */
public class Soldier extends Unit {
    public Soldier() {
        super("Soldat", 50, 15, 5, 1, 2);
        this.cost.put(ResourceType.GOLD, 50);
        this.cost.put(ResourceType.FOOD, 20);
    }
}

