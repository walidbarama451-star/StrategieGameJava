package model.buildings;

import model.Building;
import model.ResourceType;

/**
 * Quarry - produces stone.
 */
public class Quarry extends Building {
    public Quarry() {
        super("Carrière", 100, 2);
        cost.put(ResourceType.GOLD, 80);
        cost.put(ResourceType.WOOD, 60);
    }

    @Override
    public void performAction(controller.GameController controller) {
        if (isCompleted && owner != null) {
            owner.getResources().addResource(ResourceType.STONE, 15);
        }
    }
}
