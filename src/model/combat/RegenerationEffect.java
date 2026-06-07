package model.combat;

import model.Hero;

/**
 * Regeneration effect - heals at start of turn.
 */
public class RegenerationEffect extends StatusEffect {
    private static final int HEALING_PER_STACK = 2;

    /**
     * Creates a regeneration effect.
     * @param duration duration in turns
     * @param stacks number of stacks
     */
    public RegenerationEffect(int duration, int stacks) {
        super("Regeneration", EffectType.BUFF, duration, stacks);
    }

    @Override
    public void applyStartOfTurn(Hero hero) {
        int healing = HEALING_PER_STACK * getStacks();
        hero.heal(healing);
    }

    @Override
    public void applyEndOfTurn(Hero hero) {
        // Regeneration only applies at start of turn
    }

    @Override
    public void applyOnEnemy(Enemy enemy) {
        // Regeneration doesn't apply to enemies in this implementation
    }

    @Override
    public void applyStartOfTurnEnemy(Enemy enemy) {
        int healing = HEALING_PER_STACK * getStacks();
        enemy.heal(healing);
    }
}
