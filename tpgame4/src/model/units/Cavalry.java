package model.units;

import model.ResourceType;
import model.Unit;

/**
 * Fast unit with high movement and attack.
 */
public class Cavalry extends Unit {
    public Cavalry() {
        super("Cavalier", 60, 18, 6, 1, 4);
        this.cost.put(ResourceType.GOLD, 100);
        this.cost.put(ResourceType.FOOD, 40);
    }
}

