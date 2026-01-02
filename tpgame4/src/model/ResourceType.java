package model;

/**
 * Enum representing different types of resources in the game.
 */
public enum ResourceType {
    GOLD("Or"),
    WOOD("Bois"),
    STONE("Pierre"),
    FOOD("Nourriture");

    private final String frenchName;

    ResourceType(String frenchName) {
        this.frenchName = frenchName;
    }

    public String getFrenchName() {
        return frenchName;
    }
}
