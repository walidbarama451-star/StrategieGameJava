package model.buildings;

import model.Building;
import model.ResourceType;

/**
 * Farm - produces food.
 */
public class Farm extends Building {
    public Farm() {
        super("Ferme", 100, 2);
        cost.put(ResourceType.GOLD, 80);
        cost.put(ResourceType.WOOD, 60);
    }

    @Override
    public void performAction(controller.GameController controller) {
        if (isCompleted && owner != null) {
            owner.getResources().addResource(ResourceType.FOOD, 20);
        }
    }
}
