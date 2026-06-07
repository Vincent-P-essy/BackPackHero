package model;

import model.combat.StatusEffect;
import model.combat.StrengthEffect;
import model.combat.WeaknessEffect;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the hero in the game.
 * The hero has health points, energy, mana, experience, and a backpack.
 */
public class Hero {
    private int maxHealthPoints;
    private int currentHealthPoints;
    private int energy;
    private int maxEnergy;
    private int mana;
    private int gold;
    private int experience;
    private int level;
    private int protection;
    private final Backpack backpack;
    private List<StatusEffect> statusEffects;

    /**
     * Creates a new hero with default values.
     * Initial HP: 40, Level: 1, Energy: 3
     */
    public Hero() {
        this.maxHealthPoints = 40;
        this.currentHealthPoints = 40;
        this.maxEnergy = 3;
        this.energy = 3;
        this.mana = 0;
        this.gold = 0;
        this.experience = 0;
        this.level = 1;
        this.protection = 0;
        this.backpack = new Backpack(3, 5); // Phase 1: 3 rows x 5 columns = 15 cells
        this.statusEffects = new ArrayList<>();
    }

    /**
     * Gets current health points.
     * @return current health points
     */
    public int getCurrentHealthPoints() {
        return currentHealthPoints;
    }

    /**
     * Gets maximum health points.
     * @return maximum health points
     */
    public int getMaxHealthPoints() {
        return maxHealthPoints;
    }

    /**
     * Gets current energy.
     * @return current energy
     */
    public int getEnergy() {
        return energy;
    }

    /**
     * Gets maximum energy.
     * @return maximum energy
     */
    public int getMaxEnergy() {
        return maxEnergy;
    }

    /**
     * Gets current mana.
     * @return current mana
     */
    public int getMana() {
        return mana;
    }

    /**
     * Gets current gold.
     * @return current gold
     */
    public int getGold() {
        return gold;
    }

    /**
     * Gets current experience.
     * @return current experience
     */
    public int getExperience() {
        return experience;
    }

    /**
     * Gets current level.
     * @return current level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets current protection.
     * @return current protection
     */
    public int getProtection() {
        return protection;
    }

    /**
     * Gets the hero's backpack.
     * @return the backpack
     */
    public Backpack getBackpack() {
        return backpack;
    }

    /**
     * Takes damage, reduced by protection.
     * @param damage the amount of damage to take
     */
    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }

        int actualDamage = Math.max(0, damage - protection);
        currentHealthPoints = Math.max(0, currentHealthPoints - actualDamage);
        protection = Math.max(0, protection - damage);
    }

    /**
     * Heals the hero.
     * @param amount the amount to heal
     */
    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Heal amount cannot be negative");
        }

        currentHealthPoints = Math.min(maxHealthPoints, currentHealthPoints + amount);
    }

    /**
     * Adds protection to the hero.
     * @param amount the amount of protection to add
     */
    public void addProtection(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Protection amount cannot be negative");
        }

        protection += amount;
    }

    /**
     * Resets protection to zero (called at the end of enemy turn).
     */
    public void resetProtection() {
        protection = 0;
    }

    /**
     * Adds energy to the hero.
     * @param amount the amount of energy to add
     */
    public void addEnergy(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Energy amount cannot be negative");
        }

        energy += amount;
    }

    /**
     * Uses energy.
     * @param amount the amount of energy to use
     * @return true if there was enough energy, false otherwise
     */
    public boolean useEnergy(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Energy amount cannot be negative");
        }

        if (energy >= amount) {
            energy -= amount;
            return true;
        }
        return false;
    }

    /**
     * Resets energy to maximum (called at start of turn).
     */
    public void resetEnergy() {
        energy = maxEnergy;
    }

    /**
     * Adds mana to the hero.
     * @param amount the amount of mana to add
     */
    public void addMana(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Mana amount cannot be negative");
        }

        mana += amount;
    }

    /**
     * Uses mana.
     * @param amount the amount of mana to use
     * @return true if there was enough mana, false otherwise
     */
    public boolean useMana(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Mana amount cannot be negative");
        }

        if (mana >= amount) {
            mana -= amount;
            return true;
        }
        return false;
    }

    /**
     * Adds gold to the hero.
     * @param amount the amount of gold to add
     */
    public void addGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Gold amount cannot be negative");
        }

        gold += amount;
    }

    /**
     * Uses gold.
     * @param amount the amount of gold to use
     * @return true if there was enough gold, false otherwise
     */
    public boolean useGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Gold amount cannot be negative");
        }

        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    /**
     * Adds experience to the hero.
     * @param amount the amount of experience to add
     */
    public void addExperience(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Experience amount cannot be negative");
        }

        experience += amount;
        checkLevelUp();
    }

    /**
     * Checks if the hero should level up, cascading if enough XP for multiple levels.
     */
    private void checkLevelUp() {
        int requiredExp = level * 10;
        while (experience >= requiredExp) {
            experience -= requiredExp;
            level++;
            applyLevelUpBonuses();
            requiredExp = level * 10;
        }
    }

    /**
     * Applies stat bonuses on level up: +5 max HP, +1 max energy every 3 levels.
     */
    private void applyLevelUpBonuses() {
        maxHealthPoints += 5;
        currentHealthPoints = Math.min(currentHealthPoints + 5, maxHealthPoints);
        if (level % 3 == 0) {
            maxEnergy++;
        }
    }

    /**
     * Gets the experience required for next level.
     * @return experience required
     */
    public int getExperienceForNextLevel() {
        return level * 10;
    }

    /**
     * Checks if the hero is alive.
     * @return true if alive, false otherwise
     */
    public boolean isAlive() {
        return currentHealthPoints > 0;
    }

    /**
     * Gets all status effects.
     * @return list of status effects
     */
    public List<StatusEffect> getStatusEffects() {
        return new ArrayList<>(statusEffects);
    }

    /**
     * Adds a status effect.
     * @param effect the effect to add
     */
    public void addStatusEffect(StatusEffect effect) {
        Objects.requireNonNull(effect);

        // Check if same effect already exists
        for (StatusEffect existing : statusEffects) {
            if (existing.getName().equals(effect.getName())) {
                // Add stacks to existing effect
                existing.addStacks(effect.getStacks());
                return;
            }
        }

        // Add new effect
        statusEffects.add(effect);
    }

    /**
     * Removes a status effect.
     * @param effect the effect to remove
     */
    public void removeStatusEffect(StatusEffect effect) {
        statusEffects.remove(effect);
    }

    /**
     * Applies all status effects at start of turn.
     */
    public void applyStartOfTurnEffects() {
        List<StatusEffect> toRemove = new ArrayList<>();

        for (StatusEffect effect : statusEffects) {
            effect.applyStartOfTurn(this);

            if (effect.tick()) {
                toRemove.add(effect);
            }
        }

        statusEffects.removeAll(toRemove);
    }

    /**
     * Applies all status effects at end of turn.
     */
    public void applyEndOfTurnEffects() {
        for (StatusEffect effect : statusEffects) {
            effect.applyEndOfTurn(this);
        }
    }

    /**
     * Gets total damage bonus from effects.
     * @return damage bonus
     */
    public int getDamageBonus() {
        int bonus = 0;

        for (StatusEffect effect : statusEffects) {
            if (effect instanceof StrengthEffect) {
                bonus += ((StrengthEffect) effect).getDamageBonus();
            }
        }

        return bonus;
    }

    /**
     * Gets total damage reduction from effects.
     * @return damage reduction
     */
    public int getDamageReduction() {
        int reduction = 0;

        for (StatusEffect effect : statusEffects) {
            if (effect instanceof WeaknessEffect) {
                reduction += ((WeaknessEffect) effect).getDamageReduction();
            }
        }

        return reduction;
    }

    /**
     * Clears all status effects.
     */
    public void clearStatusEffects() {
        statusEffects.clear();
    }
}
