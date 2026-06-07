package model.combat;

import model.Hero;

/**
 * Poison effect - deals damage at start of turn.
 */
public class PoisonEffect extends StatusEffect {
    private static final int DAMAGE_PER_STACK = 2;

    /**
     * Creates a poison effect.
     * @param duration duration in turns
     * @param stacks number of stacks
     */
    public PoisonEffect(int duration, int stacks) {
        super("Poison", EffectType.DEBUFF, duration, stacks);
    }

    @Override
    public void applyStartOfTurn(Hero hero) {
        int damage = DAMAGE_PER_STACK * getStacks();
        hero.takeDamage(damage);
    }

    @Override
    public void applyEndOfTurn(Hero hero) {
        // Poison only applies at start of turn
    }

    @Override
    public void applyOnEnemy(Enemy enemy) {
        int damage = DAMAGE_PER_STACK * getStacks();
        enemy.takeDamage(damage);
    }
}
