package model.equipment;

import model.Hero;
import model.combat.Enemy;
import java.util.List;
import java.util.Objects;

/**
 * Provides context for equipment usage during combat.
 */
public class CombatContext {
    private final Hero hero;
    private final List<Enemy> enemies;
    private Enemy targetEnemy;

    /**
     * Creates a new combat context.
     * @param hero the hero
     * @param enemies the list of enemies
     */
    public CombatContext(Hero hero, List<Enemy> enemies) {
        this.hero = Objects.requireNonNull(hero);
        this.enemies = Objects.requireNonNull(enemies);
    }

    /**
     * Gets the hero.
     * @return the hero
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * Gets all enemies.
     * @return list of enemies
     */
    public List<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * Gets the target enemy.
     * @return the target enemy
     */
    public Enemy getTargetEnemy() {
        return targetEnemy;
    }

    /**
     * Sets the target enemy.
     * @param enemy the enemy to target
     */
    public void setTargetEnemy(Enemy enemy) {
        if (enemy != null && !enemies.contains(enemy)) {
            throw new IllegalArgumentException("Enemy not in combat");
        }
        this.targetEnemy = enemy;
    }
}
