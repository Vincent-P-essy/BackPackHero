package model.combat;

import model.Hero;
import java.util.Objects;

/**
 * Represents a status effect (buff or debuff) in combat.
 */
public abstract class StatusEffect {
    private final String name;
    private final EffectType type;
    private int duration;
    private int stacks;

    /**
     * Effect types.
     */
    public enum EffectType {
        BUFF,    // Positive effect
        DEBUFF   // Negative effect
    }

    /**
     * Creates a new status effect.
     * @param name the effect name
     * @param type the effect type
     * @param duration duration in turns (-1 for permanent)
     * @param stacks number of stacks
     */
    protected StatusEffect(String name, EffectType type, int duration, int stacks) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.duration = duration;

        if (stacks < 1) {
            throw new IllegalArgumentException("Stacks must be at least 1");
        }
        this.stacks = stacks;
    }

    /**
     * Gets the effect name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the effect type.
     * @return the type
     */
    public EffectType getType() {
        return type;
    }

    /**
     * Gets the duration.
     * @return duration (-1 for permanent)
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Gets the number of stacks.
     * @return stacks
     */
    public int getStacks() {
        return stacks;
    }

    /**
     * Adds stacks to this effect.
     * @param amount number of stacks to add
     */
    public void addStacks(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        stacks += amount;
    }

    /**
     * Decreases duration by one turn.
     * @return true if effect expired
     */
    public boolean tick() {
        if (duration > 0) {
            duration--;
            return duration == 0;
        }
        return false;
    }

    /**
     * Applies the effect at the start of turn.
     * @param hero the hero
     */
    public abstract void applyStartOfTurn(Hero hero);

    /**
     * Applies the effect at the end of turn.
     * @param hero the hero
     */
    public abstract void applyEndOfTurn(Hero hero);

    /**
     * Applies the effect on an enemy.
     * @param enemy the enemy
     */
    public abstract void applyOnEnemy(Enemy enemy);

    /**
     * Applies the effect at the start of the enemy's turn.
     * Default implementation does nothing (can be overridden).
     * @param enemy the enemy
     */
    public void applyStartOfTurnEnemy(Enemy enemy) {
        // Default: no effect
    }

    /**
     * Applies the effect at the end of the enemy's turn.
     * Default implementation does nothing (can be overridden).
     * @param enemy the enemy
     */
    public void applyEndOfTurnEnemy(Enemy enemy) {
        // Default: no effect
    }

    @Override
    public String toString() {
        String stackStr = stacks > 1 ? " x" + stacks : "";
        String durationStr = duration > 0 ? " (" + duration + " turns)" : "";
        return name + stackStr + durationStr;
    }
}
