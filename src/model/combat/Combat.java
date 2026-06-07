package model.combat;

import model.Hero;
import model.Cell.PlacedEquipment;
import model.equipment.CombatContext;
import model.equipment.Equipment;
import model.equipment.ManaStone;
import model.equipment.HealthStone;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a combat encounter.
 */
public class Combat {
    private final Hero hero;
    private final List<Enemy> enemies;
    private final CombatContext context;
    private boolean heroTurn;
    private boolean combatEnded;
    private int initialMana;

    /**
     * Creates a new combat.
     * @param hero the hero
     * @param enemies the list of enemies
     */
    public Combat(Hero hero, List<Enemy> enemies) {
        this.hero = Objects.requireNonNull(hero);
        this.enemies = new ArrayList<>(Objects.requireNonNull(enemies));

        if (enemies.isEmpty()) {
            throw new IllegalArgumentException("At least one enemy required");
        }

        this.context = new CombatContext(hero, this.enemies);
        this.heroTurn = true;
        this.combatEnded = false;
        this.initialMana = 0;

        initializeCombat();
    }

    /**
     * Initializes combat (mana stones, enemy actions).
     */
    private void initializeCombat() {
        // Count mana stones and add mana
        initialMana = 0;
        for (PlacedEquipment pe : hero.getBackpack().getEquipment()) {
            if (pe.equipment() instanceof ManaStone) {
                ManaStone stone = (ManaStone) pe.equipment();
                initialMana += stone.getManaProvided();
            }
        }
        hero.addMana(initialMana);

        // Reset hero energy
        hero.resetEnergy();

        // Reset health stones
        resetHealthStones();

        // Plan enemy actions
        for (Enemy enemy : enemies) {
            enemy.planNextAction();
        }
    }

    /**
     * Gets the hero.
     * @return the hero
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * Gets the enemies.
     * @return list of enemies
     */
    public List<Enemy> getEnemies() {
        return new ArrayList<>(enemies);
    }

    /**
     * Gets the combat context.
     * @return the context
     */
    public CombatContext getContext() {
        return context;
    }

    /**
     * Checks if it's the hero's turn.
     * @return true if hero's turn
     */
    public boolean isHeroTurn() {
        return heroTurn;
    }

    /**
     * Checks if combat has ended.
     * @return true if ended
     */
    public boolean isCombatEnded() {
        return combatEnded;
    }

    /**
     * Checks if the hero won.
     * @return true if hero won
     */
    public boolean isHeroVictorious() {
        return combatEnded && hero.isAlive() && enemies.stream().noneMatch(Enemy::isAlive);
    }

    /**
     * Uses an equipment from the backpack.
     * @param placedEq the equipment to use
     * @param targetEnemy the target enemy (if applicable)
     * @return true if usage successful
     */
    public boolean useEquipment(PlacedEquipment placedEq, Enemy targetEnemy) {
        if (!heroTurn || combatEnded) {
            return false;
        }

        Objects.requireNonNull(placedEq);

        Equipment eq = placedEq.equipment();

        // Check costs
        if (eq.getEnergyCost() > 0 && !hero.useEnergy(eq.getEnergyCost())) {
            return false;
        }

        if (eq.getManaCost() > 0 && !hero.useMana(eq.getManaCost())) {
            // Refund energy if mana check failed
            hero.addEnergy(eq.getEnergyCost());
            return false;
        }

        if (eq.getGoldCost() > 0 && !hero.useGold(eq.getGoldCost())) {
            // Refund energy and mana if gold check failed
            hero.addEnergy(eq.getEnergyCost());
            hero.addMana(eq.getManaCost());
            return false;
        }

        // Set target if applicable
        context.setTargetEnemy(targetEnemy);

        // Use equipment
        eq.use(context);

        // Remove dead enemies and check combat end
        cleanupDeadEnemies();

        return true;
    }

    /**
     * Removes defeated enemies and ends combat if none remain.
     */
    public void cleanupDeadEnemies() {
        enemies.removeIf(e -> !e.isAlive());
        if (enemies.isEmpty()) {
            endCombat();
        }
    }

    /**
     * Ends the hero's turn.
     */
    public void endHeroTurn() {
        if (!heroTurn || combatEnded) {
            return;
        }

        // Apply end of turn status effects
        hero.applyEndOfTurnEffects();

        heroTurn = false;

        // Execute enemy actions
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.executeAction(hero);
            }
        }

        // Check if hero died
        if (!hero.isAlive()) {
            combatEnded = true;
            return;
        }

        // Start new hero turn
        startHeroTurn();
    }

    /**
     * Starts a new hero turn.
     */
    private void startHeroTurn() {
        heroTurn = true;

        // Apply start of turn status effects
        hero.applyStartOfTurnEffects();

        // Reset hero energy
        hero.resetEnergy();

        // Reset enemy protection
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.resetProtection();
            }
        }

        // Reset health stones
        resetHealthStones();

        // Plan enemy actions
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.planNextAction();
            }
        }
    }

    /**
     * Resets all health stones in the backpack.
     */
    private void resetHealthStones() {
        for (PlacedEquipment pe : hero.getBackpack().getEquipment()) {
            if (pe.equipment() instanceof HealthStone) {
                ((HealthStone) pe.equipment()).resetTurn();
            }
        }
    }

    /**
     * Ends the combat (victory).
     */
    private void endCombat() {
        combatEnded = true;

        // Award experience
        int totalExp = 0;
        for (Enemy enemy : getEnemies()) {
            totalExp += enemy.getExperienceReward();
        }
        hero.addExperience(totalExp);
    }
}
