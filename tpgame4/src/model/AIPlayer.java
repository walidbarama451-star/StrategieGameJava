package model;

import controller.GameController;
import model.buildings.*;
import model.units.Archer;
import model.units.Cavalry;
import model.units.Soldier;

import java.util.List;
import java.util.Random;

public class AIPlayer extends Player {
    private Random random;

    public AIPlayer(String name) {
        super(name, false);
        this.random = new Random();
    }

    public void playTurn(GameController controller) {
        for (Building building : getBuildings()) {
            if (!building.isCompleted()) {
                building.build();
            }
        }

        for (Building building : getBuildings()) {
            if (building.isCompleted()) {
                building.performAction(controller);
            }
        }

        buildBuildings(controller);

        trainUnits(controller);
        moveAndAttack(controller);
    }

    private void buildBuildings(GameController controller) {
        Map gameMap = controller.getMap();

        if (!hasCommandCenter()) {
            tryBuildBuilding(new CommandCenter(), gameMap);
        }

        if (hasCommandCenter() && getResources().getResource(ResourceType.GOLD) < 200) {
            if (random.nextBoolean()) {
                tryBuildBuilding(new Mine(), gameMap);
            }
        }

        if (hasCommandCenter() && getResources().getResource(ResourceType.FOOD) < 100) {
            if (random.nextBoolean()) {
                tryBuildBuilding(new Farm(), gameMap);
            }
        }

        if (hasCommandCenter() && getResources().getResource(ResourceType.WOOD) < 100) {
            if (random.nextBoolean()) {
                tryBuildBuilding(new Sawmill(), gameMap);
            }
        }

        boolean hasTrainingCamp = getBuildings().stream()
                .anyMatch(b -> b instanceof TrainingCamp);
        if (!hasTrainingCamp && hasCommandCenter()) {
            tryBuildBuilding(new TrainingCamp(), gameMap);
        }
    }

    private void tryBuildBuilding(Building building, Map gameMap) {
        if (!getResources().canAfford(building.getCost())) {
            return;
        }

        int startX = 1, startY = 1;
        CommandCenter cc = getCommandCenter();
        if (cc != null) {
            startX = cc.getX();
            startY = cc.getY();
        }

        for (int range = 1; range <= 3; range++) {
            List<Tile> nearbyTiles = gameMap.getTilesInRange(startX, startY, range);
            for (Tile tile : nearbyTiles) {
                if (tile != null && tile.getType() == TileType.GRASS && tile.isEmpty()) {
                    if (getResources().payCost(building.getCost())) {
                        building.setOwner(this);
                        building.setPosition(tile.getX(), tile.getY());
                        tile.setBuilding(building);
                        addBuilding(building);
                        return;
                    }
                }
            }
        }
    }

    private void trainUnits(GameController controller) {
        boolean hasTrainingCamp = getBuildings().stream()
                .anyMatch(b -> b instanceof TrainingCamp && b.isCompleted());

        if (!hasTrainingCamp) {
            return;
        }

        if (getUnits().size() < 5) {
            Unit unit = chooseUnitToTrain();
            if (unit != null && getResources().canAfford(unit.getCost())) {
                if (getResources().payCost(unit.getCost())) {
                    Building trainingCamp = getBuildings().stream()
                            .filter(b -> b instanceof TrainingCamp)
                            .findFirst()
                            .orElse(null);

                    if (trainingCamp != null) {
                        Tile spawnTile = findSpawnTile(controller.getMap(),
                                trainingCamp.getX(), trainingCamp.getY());
                        if (spawnTile != null) {
                            unit.setOwner(this);
                            unit.setPosition(spawnTile.getX(), spawnTile.getY());
                            spawnTile.setUnit(unit);
                            addUnit(unit);
                        }
                    }
                }
            }
        }
    }

    private Unit chooseUnitToTrain() {
        int choice = random.nextInt(3);
        switch (choice) {
            case 0:
                return new Soldier();
            case 1:
                return new Archer();
            case 2:
                return new Cavalry();
            default:
                return new Soldier();
        }
    }

    private Tile findSpawnTile(Map gameMap, int x, int y) {
        for (int range = 1; range <= 2; range++) {
            List<Tile> tiles = gameMap.getTilesInRange(x, y, range);
            for (Tile tile : tiles) {
                if (tile != null && tile.isWalkable() && tile.isEmpty()) {
                    return tile;
                }
            }
        }
        return null;
    }

    private void moveAndAttack(GameController controller) {
        Player humanPlayer = controller.getHumanPlayer();
        Map gameMap = controller.getMap();

        for (Unit unit : getUnits()) {
            if (!unit.isAlive()) {
                continue;
            }

            Unit targetUnit = findNearestEnemyUnit(unit, humanPlayer, gameMap);
            Building targetBuilding = findNearestEnemyBuilding(unit, humanPlayer, gameMap);

            if (targetUnit != null && unit.isInRange(targetUnit)) {
                unit.attack(targetUnit);
                if (!targetUnit.isAlive()) {
                    Tile tile = gameMap.getTile(targetUnit.getX(), targetUnit.getY());
                    if (tile != null) {
                        tile.setUnit(null);
                    }
                    humanPlayer.removeUnit(targetUnit);
                }
                continue;
            }

            if (targetBuilding != null) {
                int dx = Math.abs(targetBuilding.getX() - unit.getX());
                int dy = Math.abs(targetBuilding.getY() - unit.getY());
                if (Math.max(dx, dy) <= unit.getRange()) {
                    int damage = unit.getAttack();
                    targetBuilding.takeDamage(damage);
                    controller.addEvent(unit.getName() + " (IA) attaque " + targetBuilding.getName() + " pour " + damage
                            + " dégâts.");
                    if (targetBuilding.isDestroyed()) {
                        Tile tile = gameMap.getTile(targetBuilding.getX(), targetBuilding.getY());
                        if (tile != null) {
                            tile.setBuilding(null);
                            tile.setOwner(null);
                        }
                        humanPlayer.removeBuilding(targetBuilding);
                        controller.addEvent(targetBuilding.getName() + " détruit!");
                    }
                    continue;
                }
            }

            if (targetUnit != null) {
                moveTowards(unit, targetUnit.getX(), targetUnit.getY(), gameMap);
            } else if (targetBuilding != null) {
                moveTowards(unit, targetBuilding.getX(), targetBuilding.getY(), gameMap);
            }
        }

        getUnits().removeIf(u -> !u.isAlive());
    }

    private Unit findNearestEnemyUnit(Unit unit, Player enemy, Map gameMap) {
        Unit nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (Unit enemyUnit : enemy.getUnits()) {
            if (!enemyUnit.isAlive())
                continue;

            int dx = Math.abs(enemyUnit.getX() - unit.getX());
            int dy = Math.abs(enemyUnit.getY() - unit.getY());
            int distance = Math.max(dx, dy);

            if (distance < minDistance) {
                minDistance = distance;
                nearest = enemyUnit;
            }
        }

        return nearest;
    }

    private Building findNearestEnemyBuilding(Unit unit, Player enemy, Map gameMap) {
        Building nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (Building building : enemy.getBuildings()) {
            if (building.isDestroyed())
                continue;

            int dx = Math.abs(building.getX() - unit.getX());
            int dy = Math.abs(building.getY() - unit.getY());
            int distance = Math.max(dx, dy);

            if (distance < minDistance) {
                minDistance = distance;
                nearest = building;
            }
        }

        return nearest;
    }

    private void moveTowards(Unit unit, int targetX, int targetY, Map gameMap) {
        int dx = Integer.compare(targetX, unit.getX());
        int dy = Integer.compare(targetY, unit.getY());

        int newX = unit.getX();
        int newY = unit.getY();

        if (dx != 0 && Math.abs(targetX - unit.getX()) > Math.abs(targetY - unit.getY())) {
            newX += dx;
        } else if (dy != 0) {
            newY += dy;
        }

        Tile targetTile = gameMap.getTile(newX, newY);
        if (targetTile != null && targetTile.isWalkable() && unit.canReach(newX, newY)) {
            Tile currentTile = gameMap.getTile(unit.getX(), unit.getY());
            if (currentTile != null) {
                currentTile.setUnit(null);
            }
            unit.setPosition(newX, newY);
            targetTile.setUnit(unit);
        }
    }
}
