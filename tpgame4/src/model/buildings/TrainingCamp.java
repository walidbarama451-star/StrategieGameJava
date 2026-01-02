package model.buildings;

import model.Building;
import model.ResourceType;

/**
 * Training camp - allows training of units.
 */
public class TrainingCamp extends Building {
    public TrainingCamp() {
        super("Camp d'Entraînement", 150, 2);
        cost.put(ResourceType.GOLD, 150);
        cost.put(ResourceType.WOOD, 80);
        cost.put(ResourceType.STONE, 50);
    }

    @Override
    public void performAction(controller.GameController controller) {
        // Training camp doesn't produce resources, but enables unit training
    }
}
