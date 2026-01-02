package model.buildings;

import model.Building;
import model.ResourceType;

/**
 * Command center - produces resources and is required to win.
 */
public class CommandCenter extends Building {
    public CommandCenter() {
        super("Centre de Commandement", 200, 3);
        cost.put(ResourceType.GOLD, 200);
        cost.put(ResourceType.WOOD, 100);
        cost.put(ResourceType.STONE, 150);
    }

    @Override
    public void performAction(controller.GameController controller) {
        if (isCompleted && owner != null) {
            // Command center produces gold
            owner.getResources().addResource(ResourceType.GOLD, 10);
        }
    }
}
