package model.buildings;

import model.Building;
import model.ResourceType;

/**
 * Sawmill - produces wood.
 */
public class Sawmill extends Building {
    public Sawmill() {
        super("Scierie", 100, 2);
        cost.put(ResourceType.GOLD, 90);
        cost.put(ResourceType.WOOD, 40);
        cost.put(ResourceType.STONE, 30);
    }

    @Override
    public void performAction(controller.GameController controller) {
        if (isCompleted && owner != null) {
            owner.getResources().addResource(ResourceType.WOOD, 15);
        }
    }
}
