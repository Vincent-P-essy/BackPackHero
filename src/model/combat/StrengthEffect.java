package model.combat;

import model.Hero;

/**
 * Strength effect - increases damage dealt.
 * This is a buff that should be checked when dealing damage.
 */
public class StrengthEffect extends StatusEffect {
    private static final int DAMAGE_BONUS_PER_STACK = 3;

    /**
     * Creates a strength effect.
     * @param duration duration in turns
     * @param stacks number of stacks
     */
    public StrengthEffect(int duration, int stacks) {
        super("Strength", EffectType.BUFF, duration, stacks);
    }

    @Override
    public void applyStartOfTurn(Hero hero) {
        // Strength is passive, no start of turn effect
    }

    @Override
    public void applyEndOfTurn(Hero hero) {
        // Strength is passive, no end of turn effect
    }

    @Override
    public void applyOnEnemy(Enemy enemy) {
        // Strength doesn't directly apply to enemies
    }

    /**
     * Gets the damage bonus from this effect.
     * @return damage bonus
     */
    public int getDamageBonus() {
        return DAMAGE_BONUS_PER_STACK * getStacks();
    }
}
