package model.combat;

import model.Hero;

/**
 * Weakness effect - decreases damage dealt.
 */
public class WeaknessEffect extends StatusEffect {
    private static final int DAMAGE_REDUCTION_PER_STACK = 2;

    /**
     * Creates a weakness effect.
     * @param duration duration in turns
     * @param stacks number of stacks
     */
    public WeaknessEffect(int duration, int stacks) {
        super("Weakness", EffectType.DEBUFF, duration, stacks);
    }

    @Override
    public void applyStartOfTurn(Hero hero) {
        // Weakness is passive, no start of turn effect
    }

    @Override
    public void applyEndOfTurn(Hero hero) {
        // Weakness is passive, no end of turn effect
    }

    @Override
    public void applyOnEnemy(Enemy enemy) {
        // Weakness doesn't directly apply to enemies
    }

    /**
     * Gets the damage reduction from this effect.
     * @return damage reduction
     */
    public int getDamageReduction() {
        return DAMAGE_REDUCTION_PER_STACK * getStacks();
    }
}
