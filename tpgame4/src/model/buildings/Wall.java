package model.buildings;

import model.Building;
import model.ResourceType;

/**
 * Wall - high health, no action.
 */
public class Wall extends Building {
    public Wall() {
        super("Mur", 300, 1);
        cost.put(ResourceType.STONE, 50);
        cost.put(ResourceType.WOOD, 20);
    }

    @Override
    public void performAction(controller.GameController controller) {
        // Wall does nothing
    }
}
