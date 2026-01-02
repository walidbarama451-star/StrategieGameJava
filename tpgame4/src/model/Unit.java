package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class representing a unit in the game.
 */
public abstract class Unit {
    protected String name;
    protected int maxHealth;
    protected int currentHealth;
    protected int attack;
    protected int defense;
    protected int range;
    protected int movement;
    protected Map<ResourceType, Integer> cost;
    protected Player owner;
    protected int x, y; // Position on map
    protected boolean isDefending;

    public Unit(String name, int health, int attack, int defense, int range, int movement) {
        this.name = name;
        this.maxHealth = health;
        this.currentHealth = health;
        this.attack = attack;
        this.defense = defense;
        this.range = range;
        this.movement = movement;
        this.cost = new HashMap<>();
        this.isDefending = false;
    }

    public void setDefending(boolean defending) {
        this.isDefending = defending;
    }

    public boolean isDefending() {
        return isDefending;
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

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getRange() {
        return range;
    }

    public int getMovement() {
        return movement;
    }

    public Map<ResourceType, Integer> getCost() {
        return new HashMap<>(cost);
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

    /**
     * Take damage from an attack.
     */
    public void takeDamage(int damage) {
        if (isDefending) {
            damage = Math.max(0, damage / 2); // 50% damage reduction
        }
        currentHealth -= damage;
        if (currentHealth < 0) {
            currentHealth = 0;
        }
    }

    /**
     * Check if unit is alive.
     */
    public boolean isAlive() {
        return currentHealth > 0;
    }

    /**
     * Attack another unit.
     */
    public int attack(Unit target) {
        int damage = calculateDamage(target);
        target.takeDamage(damage);
        return damage;
    }

    /**
     * Calculate damage dealt to target.
     */
    protected int calculateDamage(Unit target) {
        int baseDamage = attack - target.defense;
        // Minimum 1 damage
        return Math.max(1, baseDamage);
    }

    /**
     * Check if target is in range.
     */
    public boolean isInRange(Unit target) {
        int dx = Math.abs(target.x - this.x);
        int dy = Math.abs(target.y - this.y);
        return Math.max(dx, dy) <= range;
    }

    /**
     * Check if target is in movement range.
     */
    public boolean canReach(int targetX, int targetY) {
        int dx = Math.abs(targetX - this.x);
        int dy = Math.abs(targetY - this.y);
        return Math.max(dx, dy) <= movement;
    }
}
