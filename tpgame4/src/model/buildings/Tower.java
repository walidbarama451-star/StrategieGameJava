package model.buildings;

import model.Building;
import model.ResourceType;
import model.Unit;
import model.Tile;
import java.util.List;

/**
 * Tower - attacks nearby enemies.
 */
public class Tower extends Building {
    private int attackDamage;
    private int rage;

    public Tower() {
        super("Tour de Garde", 150, 2);
        cost.put(ResourceType.GOLD, 100);
        cost.put(ResourceType.STONE, 100);
        cost.put(ResourceType.WOOD, 50);
        this.attackDamage = 15;
        this.rage = 4; // Range in tiles
    }

    @Override
    public void performAction(controller.GameController controller) {
        if (!isCompleted || owner == null)
            return;

        model.Map map = controller.getMap();
        List<Tile> nearbyTiles = map.getTilesInRange(x, y, rage);

        for (Tile tile : nearbyTiles) {
            Unit unit = tile.getUnit();
            if (unit != null && unit.getOwner() != owner && unit.isAlive()) {
                // Attack enemy unit
                unit.takeDamage(attackDamage);
                controller.addEvent(getName() + " tire sur " + unit.getName() + " pour " + attackDamage + " dégâts.");
                if (!unit.isAlive()) {
                    tile.setUnit(null);
                    unit.getOwner().removeUnit(unit);
                    controller.addEvent(unit.getName() + " éliminé par une tour!");
                }
                return; // Attack only one target per turn
            }
        }
    }
}
