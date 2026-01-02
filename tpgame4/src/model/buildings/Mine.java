package model.buildings;

import model.Building;
import model.ResourceType;

/**
 * Mine - produces gold.
 */
public class Mine extends Building {
    public Mine() {
        super("Mine", 100, 2);
        cost.put(ResourceType.GOLD, 100);
        cost.put(ResourceType.WOOD, 50);
        cost.put(ResourceType.STONE, 100);
    }

    @Override
    public void performAction(controller.GameController controller) {
        if (isCompleted && owner != null) {
            owner.getResources().addResource(ResourceType.GOLD, 15);
        }
    }
}
