package model;

import model.buildings.CommandCenter;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private ResourceManager resources;
    private List<Unit> units;
    private List<Building> buildings;
    private boolean isHuman;

    public Player(String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
        this.resources = new ResourceManager(500, 300, 200, 400);
        this.units = new ArrayList<>();
        this.buildings = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ResourceManager getResources() {
        return resources;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public void addUnit(Unit unit) {
        units.add(unit);
    }

    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    public void addBuilding(Building building) {
        buildings.add(building);
    }

    public void removeBuilding(Building building) {
        buildings.remove(building);
    }

    public boolean hasCommandCenter() {
        return buildings.stream().anyMatch(b -> b instanceof CommandCenter);
    }

    public CommandCenter getCommandCenter() {
        return buildings.stream()
                .filter(b -> b instanceof CommandCenter)
                .map(b -> (CommandCenter) b)
                .findFirst()
                .orElse(null);
    }

    public boolean isDefeated() {
        return !hasCommandCenter() && units.isEmpty();
    }
}
