package model.dungeon;

import model.combat.Enemy;
import model.equipment.Equipment;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the content of a room.
 */
public class RoomContent {
    private final List<Enemy> enemies;
    private final List<Equipment> equipment;
    private int healingCost;
    private int healingAmount;

    /**
     * Creates empty room content.
     */
    public RoomContent() {
        this.enemies = new ArrayList<>();
        this.equipment = new ArrayList<>();
        this.healingCost = 0;
        this.healingAmount = 0;
    }

    /**
     * Adds an enemy to this room.
     * @param enemy the enemy
     */
    public void addEnemy(Enemy enemy) {
        Objects.requireNonNull(enemy);
        enemies.add(enemy);
    }

    /**
     * Gets all enemies in this room.
     * @return list of enemies
     */
    public List<Enemy> getEnemies() {
        return new ArrayList<>(enemies);
    }

    /**
     * Checks if there are enemies.
     * @return true if enemies present
     */
    public boolean hasEnemies() {
        return !enemies.isEmpty();
    }

    /**
     * Clears all enemies from this room.
     * Used after combat is won.
     */
    public void clearEnemies() {
        enemies.clear();
    }

    /**
     * Adds equipment to this room.
     * @param eq the equipment
     */
    public void addEquipment(Equipment eq) {
        Objects.requireNonNull(eq);
        equipment.add(eq);
    }

    /**
     * Gets all equipment in this room.
     * @return list of equipment
     */
    public List<Equipment> getEquipment() {
        return new ArrayList<>(equipment);
    }

    /**
     * Removes equipment from this room.
     * @param eq the equipment to remove
     * @return true if removed
     */
    public boolean removeEquipment(Equipment eq) {
        return equipment.remove(eq);
    }

    /**
     * Checks if there is equipment.
     * @return true if equipment present
     */
    public boolean hasEquipment() {
        return !equipment.isEmpty();
    }

    /**
     * Sets healing parameters for healer room.
     * @param cost the cost in gold
     * @param amount the amount healed
     */
    public void setHealing(int cost, int amount) {
        if (cost < 0 || amount < 0) {
            throw new IllegalArgumentException("Healing parameters cannot be negative");
        }
        this.healingCost = cost;
        this.healingAmount = amount;
    }

    /**
     * Gets the healing cost.
     * @return healing cost
     */
    public int getHealingCost() {
        return healingCost;
    }

    /**
     * Gets the healing amount.
     * @return healing amount
     */
    public int getHealingAmount() {
        return healingAmount;
    }
}
