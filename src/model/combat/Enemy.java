package model.combat;

import model.Hero;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Represents an enemy in combat.
 */
public abstract class Enemy {
    private final String name;
    private final int maxHealthPoints;
    private int currentHealthPoints;
    private int protection;
    private final int experienceReward;
    private final Random random;
    private EnemyAction nextAction;
    private final List<StatusEffect> statusEffects;

    /**
     * Possible enemy actions.
     */
    public enum EnemyAction {
        ATTACK, DEFEND
    }

    /**
     * Creates a new enemy.
     * @param name the enemy name
     * @param healthPoints the enemy HP
     * @param experienceReward experience given when defeated
     */
    protected Enemy(String name, int healthPoints, int experienceReward) {
        this.name = Objects.requireNonNull(name);

        if (healthPoints <= 0) {
            throw new IllegalArgumentException("Health points must be positive");
        }
        if (experienceReward < 0) {
            throw new IllegalArgumentException("Experience reward cannot be negative");
        }

        this.maxHealthPoints = healthPoints;
        this.currentHealthPoints = healthPoints;
        this.experienceReward = experienceReward;
        this.protection = 0;
        this.random = new Random();
        this.nextAction = null;
        this.statusEffects = new ArrayList<>();
    }

    /**
     * Gets the enemy name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets current health points.
     * @return current HP
     */
    public int getCurrentHealthPoints() {
        return currentHealthPoints;
    }

    /**
     * Gets maximum health points.
     * @return maximum HP
     */
    public int getMaxHealthPoints() {
        return maxHealthPoints;
    }

    /**
     * Gets current protection.
     * @return protection
     */
    public int getProtection() {
        return protection;
    }

    /**
     * Gets experience reward.
     * @return experience points
     */
    public int getExperienceReward() {
        return experienceReward;
    }

    /**
     * Gets the next planned action.
     * @return the next action
     */
    public EnemyAction getNextAction() {
        return nextAction;
    }

    /**
     * Takes damage, reduced by protection.
     * @param damage the amount of damage
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
     * Adds protection.
     * @param amount the amount of protection
     */
    public void addProtection(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Protection cannot be negative");
        }

        protection += amount;
    }

    /**
     * Resets protection to zero.
     */
    public void resetProtection() {
        protection = 0;
    }

    /**
     * Checks if the enemy is alive.
     * @return true if alive
     */
    public boolean isAlive() {
        return currentHealthPoints > 0;
    }

    /**
     * Plans the next action for the enemy.
     */
    public void planNextAction() {
        nextAction = chooseAction();
    }

    /**
     * Chooses an action (to be implemented by subclasses).
     * @return the chosen action
     */
    protected abstract EnemyAction chooseAction();

    /**
     * Executes the planned action.
     * @param hero the target hero
     */
    public void executeAction(Hero hero) {
        Objects.requireNonNull(hero);

        if (nextAction == null) {
            planNextAction();
        }

        switch (nextAction) {
            case ATTACK:
                performAttack(hero);
                break;
            case DEFEND:
                performDefend();
                break;
        }

        hero.resetProtection();
        nextAction = null;
    }

    /**
     * Gets the attack damage for this enemy.
     * @return attack damage
     */
    protected abstract int getAttackDamage();

    /**
     * Gets the defense value for this enemy.
     * @return defense value
     */
    protected abstract int getDefenseValue();

    /**
     * Performs an attack on the hero.
     * @param hero the hero
     */
    private void performAttack(Hero hero) {
        hero.takeDamage(getAttackDamage());
    }

    /**
     * Performs a defensive action.
     */
    private void performDefend() {
        addProtection(getDefenseValue());
    }

    /**
     * Gets the random number generator.
     * @return the random generator
     */
    protected Random getRandom() {
        return random;
    }

    /**
     * Adds a status effect to this enemy.
     * If the effect already exists, stacks are added.
     * @param effect the status effect to add
     */
    public void addStatusEffect(StatusEffect effect) {
        Objects.requireNonNull(effect);

        // Check if effect already exists
        for (StatusEffect existing : statusEffects) {
            if (existing.getName().equals(effect.getName())) {
                existing.addStacks(effect.getStacks());
                return;
            }
        }

        statusEffects.add(effect);
    }

    /**
     * Gets all active status effects.
     * @return list of status effects
     */
    public List<StatusEffect> getStatusEffects() {
        return new ArrayList<>(statusEffects);
    }

    /**
     * Heals the enemy.
     * @param amount the amount to heal
     */
    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Heal amount cannot be negative");
        }

        currentHealthPoints = Math.min(maxHealthPoints, currentHealthPoints + amount);
    }

    /**
     * Applies status effects at the start of the enemy's turn.
     * Used by combat system.
     */
    public void applyStartOfTurnEffects() {
        List<StatusEffect> toRemove = new ArrayList<>();

        for (StatusEffect effect : statusEffects) {
            effect.applyStartOfTurnEnemy(this);

            if (effect.tick()) {
                toRemove.add(effect);
            }
        }

        statusEffects.removeAll(toRemove);
    }

    /**
     * Applies status effects at the end of the enemy's turn.
     * Used by combat system.
     */
    public void applyEndOfTurnEffects() {
        for (StatusEffect effect : statusEffects) {
            effect.applyEndOfTurnEnemy(this);
        }
    }
}
