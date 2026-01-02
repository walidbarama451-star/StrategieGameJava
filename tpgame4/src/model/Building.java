package model;

import java.util.HashMap;
import java.util.Map;

public abstract class Building {
    protected String name;
    protected int maxHealth;
    protected int currentHealth;
    protected Map<ResourceType, Integer> cost;
    protected int buildTime;
    protected Player owner;
    protected int x, y; // Position on map
    protected boolean isCompleted;
    protected int constructionProgress;

    public Building(String name, int health, int buildTime) {
        this.name = name;
        this.maxHealth = health;
        this.currentHealth = health;
        this.buildTime = buildTime;
        this.cost = new HashMap<>();
        this.isCompleted = false;
        this.constructionProgress = 0;
    }

    public String getName() {
        return name;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public Map<ResourceType, Integer> getCost() {
        return new HashMap<>(cost);
    }

    public int getBuildTime() {
        return buildTime;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean build() {
        constructionProgress++;
        if (constructionProgress >= buildTime) {
            isCompleted = true;
            return true;
        }
        return false;
    }

    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth < 0) {
            currentHealth = 0;
        }
    }

    public boolean isDestroyed() {
        return currentHealth <= 0;
    }

    public abstract void performAction(controller.GameController controller);
}
